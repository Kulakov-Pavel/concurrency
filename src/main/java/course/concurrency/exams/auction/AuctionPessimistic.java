package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = Bid.initialBid;

    public boolean propose(Bid bid) {
        if (bid.getPrice() < latestBid.getPrice()) {
            return  false;
        }
        synchronized (this) {
            if(bid.getPrice() > latestBid.getPrice()) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
            }
        }
        return true;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
