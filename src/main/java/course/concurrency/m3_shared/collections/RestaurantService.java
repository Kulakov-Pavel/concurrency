package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, LongAdder> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.compute(restaurantName, (k, v) -> {
            if(v == null) {
                v = new LongAdder();
            }
            v.increment();
            return v;
        });

    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
                .map(e -> String.format("%s - %s", e.getKey(), e.getValue().sum()))
                .collect(Collectors.toUnmodifiableSet());
    }
}
