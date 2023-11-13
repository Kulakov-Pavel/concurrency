package course.concurrency;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class Counter {
    private static final Random RANDOM = new Random();
    private static final int LIMIT = 1_000_000;

    public static void main(String[] args) {
        var pool = ForkJoinPool.commonPool();
        var list = Stream.generate(() -> RANDOM.nextInt(0, 1000))
                .limit(LIMIT)
                .map(String::valueOf)
                .toList();
        int searchValue = 50;
        int threshold = list.size() / (Runtime.getRuntime().availableProcessors());

        CounterTask task = new CounterTask(list, 0, LIMIT, searchValue, threshold);
        Integer result = pool.invoke(task);
        System.out.printf("В данном списке число %s встречается %d раз", searchValue, result);
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
                return count();
            } else {
                int median = (right - left) / 2;
                var a = new CounterTask(list, left, median, searchValue, threshold);
                var b = new CounterTask(list, median, right, searchValue, threshold);
                invokeAll(a, b);
                return a.join() + b.join();
            }
        }

        private int count() {
            int counter = 0;
            for (int i = left; i < right; i++) {
                if (Integer.valueOf(list.get(i)).equals(searchValue))
                    ++counter;
            }
            return counter;
        }
    }

}
