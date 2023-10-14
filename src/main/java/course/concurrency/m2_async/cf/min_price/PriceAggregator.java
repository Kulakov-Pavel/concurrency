package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class PriceAggregator {

    private static final int TIME_OUT = 2950;
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }
    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);
    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        // place for your code
        var executor = Executors.newFixedThreadPool(shopIds.size());
        List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(shopId -> CompletableFuture
                                .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                                .completeOnTimeout(null, TIME_OUT, MILLISECONDS)
                                .handle((res, ex) -> res == null ? Double.NaN: res)
                )
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .min(Double::compare)
                .orElse(Double.NaN);
    }
}
