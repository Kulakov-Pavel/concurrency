package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(Bid.initialBid);

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.get().getPrice()) {
            do {
                notifier.sendOutdatedMessage(latestBid.get());
            } while (latestBid.get().getPrice() < bid.getPrice() &&
                    !latestBid.compareAndSet(latestBid.get(), bid));
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
