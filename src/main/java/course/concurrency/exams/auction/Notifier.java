package course.concurrency.exams.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {

    private final ExecutorService executor = Executors.newFixedThreadPool(64);

    public synchronized void sendOutdatedMessage(Bid bid) {
        executor.submit(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
