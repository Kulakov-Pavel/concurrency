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
            Bid currentBid;
            do{
                currentBid = latestBid.get();
            } while (currentBid.getPrice() < bid.getPrice() &&
                    !latestBid.compareAndSet(currentBid, bid));
            if(latestBid.get() == bid) {
                notifier.sendOutdatedMessage(currentBid);
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
