package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.*;

public class PriceAggregator {

    private static final int TIME_OUT = 2995;
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
        long start = System.currentTimeMillis();
        List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(shopId -> CompletableFuture
                                .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                                .completeOnTimeout(null, TIME_OUT, MILLISECONDS)
//                                .handle((res, ex) -> Double.NaN)
                )
                .collect(Collectors.toList());

        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        future.join();

        return futures.stream()
                .filter(f -> f.getNow(null) != null)
                .map(CompletableFuture::join)
                .min(Double::compare)
                .orElse(Double.NaN);
    }
}
