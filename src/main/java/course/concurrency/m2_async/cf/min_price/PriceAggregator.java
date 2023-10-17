package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

public class PriceAggregator {

    private static final int TIME_OUT = 2950;
    private PriceRetriever priceRetriever = new PriceRetriever();
    private ExecutorService executor = Executors.newFixedThreadPool(Integer.MAX_VALUE);

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(shopId -> CompletableFuture
//                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                        .completeOnTimeout(Double.NaN, TIME_OUT, MILLISECONDS)
                        .exceptionally(ex -> Double.NaN)
                ).collect(toList());
        return futures.stream().
                map(CompletableFuture::join)
                .min(Double::compare)
                .orElse(Double.NaN);
    }
}
