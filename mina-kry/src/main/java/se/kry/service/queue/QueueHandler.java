package se.kry.service.queue;

import java.util.List;

public interface QueueHandler {
    void putNewService(String newOrderRequest) throws InterruptedException;

    List<String> drainQService();
}
