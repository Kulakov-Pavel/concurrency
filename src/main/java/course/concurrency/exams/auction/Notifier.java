package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;

public class Notifier {

    public synchronized void sendOutdatedMessage(Bid bid) {
        imitateSending();
    }

    private void imitateSending() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
        });
    }

    public void shutdown() {}
}
