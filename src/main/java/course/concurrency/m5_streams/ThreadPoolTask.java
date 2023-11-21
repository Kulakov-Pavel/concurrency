package course.concurrency.m5_streams;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LIFOBlockingDeque<>());
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(7, 7, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
    }

    private static class LIFOBlockingDeque<T> extends LinkedBlockingDeque<T> {
        @Override
        public boolean offer(T t) {
            return super.offerFirst(t);
        }

        @Override
        public T remove() {
            return super.removeFirst();
        }
    }


}
