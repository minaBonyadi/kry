package se.kry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
import wiremock.org.eclipse.jetty.util.BlockingArrayQueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class PollService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BlockingQueue<ServiceIdentity> blockingQueue = new BlockingArrayQueue<>();


    public void pollHandler() {
        List<User> users = userRepository.findAll();
        users.forEach(user-> {
            if (user.getServiceIdentities().iterator().hasNext()) {
                ServiceIdentity oneService = user.getServiceIdentities().iterator().next();
                blockingQueue.add(oneService);
            }
        });
    }

    @Scheduled(cron = "${call.all.rest.services.cron}")
    public void callService() {
        try {
            ServiceIdentity service = blockingQueue.take();

            Flux<Map> services = WebClient.create().get()
                    .uri(service.getUrl())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .doOnComplete(() -> {
                        service.setStatus(Status.OK);
                        updateService(service);
                    })
                    .doOnError(error -> {
                        service.setStatus(Status.FAIL);
                        updateService(service);
                        throw new PollServiceException(Status.FAIL);
                    });
            services.subscribe();
        }catch (InterruptedException ex){
            log.warn(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void updateService(ServiceIdentity service) {
        ServiceIdentity saveService = serviceRepository.findById(service.getId()).orElseThrow();
        saveService.setUrl(service.getUrl());
        saveService.setStatus(service.getStatus());
        serviceRepository.save(saveService);
    }
}
