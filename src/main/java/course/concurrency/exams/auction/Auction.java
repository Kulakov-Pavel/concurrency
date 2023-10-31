package course.concurrency.exams.auction;

public interface Auction {
    Bid initialBid = new Bid(null, null, 0L);

    boolean propose(Bid bid);

    Bid getLatestBid();
}
