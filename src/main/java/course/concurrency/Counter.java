package course.concurrency;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class Counter {
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
        int threshold = (int) (list.size() / (Runtime.getRuntime().availableProcessors() * 0.25));

        start = System.currentTimeMillis();
        int counter = CounterTask.count(list, 0, LIMIT, searchValue);
        end = System.currentTimeMillis();
        System.out.printf("""
                        Последовательная обработка.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.
                        """,
                searchValue, counter, end - start);


        CounterTask task = new CounterTask(list, 0, LIMIT, searchValue, threshold);
        start = System.currentTimeMillis();
        Integer result = pool.invoke(task);
        end = System.currentTimeMillis();
        System.out.printf("""
                        Параллельная обработка.
                        В данном списке число %s встречается %d раз
                        Время выполнения: %dмс.
                        """,
                searchValue, result, end - start);
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
                int median = (right - left) / 2;
                var a = new CounterTask(list, left, median, searchValue, threshold);
                var b = new CounterTask(list, median, right, searchValue, threshold);
                invokeAll(a, b);
                return a.join() + b.join();
            }
        }

        public static int count(List<String> list, int left, int right, int searchValue) {
            int counter = 0;
            for (int i = left; i < right; i++) {
                if (Integer.valueOf(list.get(i)).equals(searchValue))
                    ++counter;
            }
            return counter;
        }
    }

}
