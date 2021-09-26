package se.kry.service.queue;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BlockingQueueImpl implements QueueHandler {
    private final int CAPACITY = 100;
    private final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(CAPACITY);

    @Override
    public void putNewService(String url) throws InterruptedException{
        blockingQueue.put(url);
    }

    @Override
    public List<String> drainQService() {
        List<String> services = new ArrayList<>();
        blockingQueue.drainTo(services, CAPACITY);
        return services;
    }
}
