package course.concurrency;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

public class ForkJoinCounter {
    private static final Random RANDOM = new Random();
    private static final int LIMIT = 10_000_000;

    public static void main(String[] args) {
        long start, end;
        var pool = ForkJoinPool.commonPool();
        var list = Stream.generate(() -> RANDOM.nextInt(0, 1000))
                .limit(LIMIT)
                .map(String::valueOf)
                .toList();
        int searchValue = 50;
        int threshold = (int) (list.size() / (Runtime.getRuntime().availableProcessors() * 0.3));

        start = System.currentTimeMillis();
        int counter = count(list, 0, LIMIT, searchValue);
        end = System.currentTimeMillis();
        System.out.printf("""
                        Последовательная обработка.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.\n
                        """,
                searchValue, counter, end - start);

        start = System.currentTimeMillis();
        long count = list.stream()
                .parallel()
                .filter(val -> Integer.valueOf(val).equals(searchValue))
                .count();
        end = System.currentTimeMillis();
        System.out.printf("""
                        Параллельная обработка через Stream Api.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.\n
                        """,
                searchValue, count, end - start);

        CounterTask task = new CounterTask(list, 0, LIMIT, searchValue, threshold);
        start = System.currentTimeMillis();
        Integer result = pool.invoke(task);
        end = System.currentTimeMillis();
        System.out.printf("""
                        Параллельная обработка через RecursiveTask.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.\n
                        """,
                searchValue, result, end - start);

        var res = new LongAdder();
        CustomCountedCompleter countedCompleter = new CustomCountedCompleter(null, list, 0, LIMIT, searchValue, threshold, res);
        start = System.currentTimeMillis();
        pool.invoke(countedCompleter);
        end = System.currentTimeMillis();
        System.out.printf("""
                        Параллельная обработка через CountedCompleter.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.\n
                        """,
                searchValue, res.sum(), end - start);

    }

    public static int count(List<String> list, int left, int right, int searchValue) {
        int counter = 0;
        for (int i = left; i < right; i++) {
            if (Integer.valueOf(list.get(i)).equals(searchValue))
                ++counter;
        }
        return counter;
    }

    public static class CounterTask extends RecursiveTask<Integer> {
        private final int threshold;
        private final int searchValue;
        private final List<String> list;
        private final int left;
        private final int right;

        public CounterTask(List<String> list, int left, int right, int searchValue, int threshold) {
            this.list = list;
            this.left = left;
            this.right = right;
            this.searchValue = searchValue;
            this.threshold = threshold;
        }

        @Override
        protected Integer compute() {
            if (right - left <= threshold) {
                return count(list, left, right, searchValue);
            } else {
                int median = right - (right - left) / 2;
                var a = new CounterTask(list, left, median, searchValue, threshold);
                var b = new CounterTask(list, median, right, searchValue, threshold);
                invokeAll(a, b);
                return a.join() + b.join();
            }
        }
    }

    private static class CustomCountedCompleter extends CountedCompleter<Void> {

        private final List<String> list;
        private final LongAdder result;
        private final int left;
        private final int right;
        private final int searchValue;
        private final int threshold;

        public CustomCountedCompleter(CountedCompleter parent, List<String> list, int left, int right, int searchValue,
                                      int threshold, LongAdder result) {
            super(parent);
            this.list = list;
            this.left = left;
            this.right = right;
            this.searchValue = searchValue;
            this.threshold = threshold;
            this.result = result;
        }

        @Override
        public void compute() {
            if (right - left <= threshold) {
                result.add(count(list, left, right, searchValue));
            } else {
                int median = right - (right - left) / 2;
                addToPendingCount(2);
                new CustomCountedCompleter(this, list, left, median, searchValue, threshold, result).fork();
                new CustomCountedCompleter(this, list, median, right, searchValue, threshold, result).fork();
            }
            tryComplete();
        }
    }

}
