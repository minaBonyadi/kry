package se.kry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import se.kry.enumeration.Status;
import se.kry.exception.PollServiceException;
import se.kry.model.ServiceIdentity;
import se.kry.model.User;
import se.kry.repository.ServiceRepository;
import se.kry.repository.UserRepository;
import se.kry.service.queue.BlockingQueueImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PollService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BlockingQueueImpl handler;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public void pollHandler() {
        List<User> users = userRepository.findAll();
        executorService.submit(() -> {
            int counter;
            for (User user : users) {
                counter = user.getServiceIdentities().size();
                if (counter > 0) {
                    String url = user.getServiceIdentities().stream().iterator().next().getUrl();
                    counter--;
                    try {
                        handler.putNewService(url);
                    } catch (InterruptedException e) {
                        log.warn(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
    }

    @Scheduled(cron = "${call.all.rest.services.cron}")
    public void callService() {
        List<String> onQServices = handler.drainQService();
        CountDownLatch count = new CountDownLatch(onQServices.size());
        try {
        ExecutorService executor = Executors.newFixedThreadPool(onQServices.size());
           checkServicesHealth(executor, onQServices, count);
           count.await();
       } catch (InterruptedException e) {
           log.warn(e.getMessage());
           Thread.currentThread().interrupt();
       } catch (IllegalArgumentException e) {
            log.warn(String.format("Queue is null!! %s", e.getMessage()));
        }
    }

    private void checkServicesHealth(ExecutorService executor, List<String> urls, CountDownLatch count){
        for (int i = 0; i < urls.size(); i++) {
            executor.submit(() -> {
                try {
                   for (String url: urls) {
                       Flux<Map> servicesFlx = WebClient.create().get()
                               .uri(url)
                               .retrieve()
                               .bodyToFlux(Map.class)
                               .doOnComplete(() ->
                                       updateService(url, Status.OK))
                               .doOnError(error -> {
                                   updateService(url, Status.FAIL);
                                   throw new PollServiceException(Status.FAIL);
                               });
                       servicesFlx.subscribe();
                   }
                }finally {
                    count.countDown();
                }
            });
        }
    }

    private void updateService(String url, Status status) {
        ServiceIdentity saveService = serviceRepository.findServiceIdentitiesByUrl(url);
        saveService.setUrl(url);
        saveService.setStatus(status);
        serviceRepository.save(saveService);
    }

    @Scheduled(cron = "${call.shutdown.signal.cron}")
    public void callShutdownSignal() {
        executorService.shutdown();
    }
}
