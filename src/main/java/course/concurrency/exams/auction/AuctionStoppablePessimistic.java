package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = initialBid;
    private volatile boolean running = true;

    public synchronized boolean propose(Bid bid) {
        if(!running) {
            return false;
        }
        if (bid.getPrice() > latestBid.getPrice()) {
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
            return true;
        }
        return false;
    }

    public synchronized Bid getLatestBid() {
        return latestBid;
    }

    public synchronized Bid stopAuction() {
        running = false;
        return latestBid;
    }
}
