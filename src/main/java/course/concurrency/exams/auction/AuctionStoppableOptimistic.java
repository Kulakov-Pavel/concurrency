package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(Bid.initialBid, true);

    public boolean propose(Bid bid) {
        if(!latestBid.isMarked()) {
            return false;
        }
        var currentBid = latestBid.getReference();
        if (bid.getPrice() > currentBid.getPrice()) {
            if(latestBid.compareAndSet(currentBid, bid, true, true)) {
                notifier.sendOutdatedMessage(currentBid);
                return true;
            } else {
                propose(bid);
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        boolean stopped;
        do {
            stopped = latestBid.attemptMark(latestBid.getReference(), false);
        } while (!stopped);
        return latestBid.getReference();
    }
}
