package course.concurrency.m5_streams;

import java.util.concurrent.BlockingQueue;
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
        return new CustomThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    private static class CustomThreadPoolExecutor extends ThreadPoolExecutor {
        public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                        BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        public void execute(Runnable command) {
            if(getActiveCount() == getMaximumPoolSize()) {
                return;
            }
            super.execute(command);
        }
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
