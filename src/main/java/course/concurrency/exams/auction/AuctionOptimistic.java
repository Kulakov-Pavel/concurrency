package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(initialBid);

    public boolean propose(Bid bid) {
        var actualBid = latestBid.get();
        if (bid.getPrice() > actualBid.getPrice()) {
            if(latestBid.compareAndSet(actualBid, bid)) {
                notifier.sendOutdatedMessage(actualBid);
                return true;
            } else {
                propose(bid);
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
