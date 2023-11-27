package course.concurrency;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CustomBlockingQueueTests {

    private CustomBlockingQueue<Integer> queue;
    private final Random random = new Random();
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenQueueIsFullAndOnlyWriters_thenSizeEqualsCapacity(int value) {
        int capacity = 7;
        queue = new CustomBlockingQueue<>(capacity);
        CountDownLatch latch = new CountDownLatch(value);

        for (int i = 0; i < value; i++) {
            pool.submit(() -> queue.enqueue(0));
            latch.countDown();
        }
        try {
            while(latch.getCount() != 0) {
                SECONDS.sleep(1);
            }
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertAll(
                () -> assertThat(((ThreadPoolExecutor) pool).getQueue().size()).isEqualTo(0),
                () -> assertThat(((ThreadPoolExecutor) pool).getActiveCount()).isEqualTo(0),
                () -> assertThat(capacity).isEqualTo(queue.getElementsAsList().size())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenQueueIsEmptyAndOnlyReaders_thenSizeZero(int value) {
        int capacity = 7;
        queue = new CustomBlockingQueue<>(capacity);
        CountDownLatch latch = new CountDownLatch(value);

        for (int i = 0; i < value; i++) {
            pool.submit(() -> queue.dequeue());
            latch.countDown();
        }
        try {
            while(latch.getCount() != 0) {
                SECONDS.sleep(1);
            }
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertAll(
                () -> assertThat(((ThreadPoolExecutor) pool).getQueue().size()).isEqualTo(0),
                () -> assertThat(((ThreadPoolExecutor) pool).getActiveCount()).isEqualTo(0),
                () -> assertThat(queue.getElementsAsList().size()).isEqualTo(0)
        );
    }


    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenQueueStopped_thenSizeNotMoreCapacity(int value) {
        int capacity = 7;
        queue = new CustomBlockingQueue<>(capacity);
        CountDownLatch latch = new CountDownLatch(value);

        for (int i = 0; i < value; i++) {
            if (i % 3 != 0) {
                pool.execute(() -> queue.enqueue(0));
            } else {
                pool.execute(() -> queue.dequeue());
            }
            latch.countDown();
        }
        try {
            while(latch.getCount() != 0) {
                SECONDS.sleep(1);
            }
            SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertAll(
                () -> assertThat(((ThreadPoolExecutor) pool).getQueue().size()).isEqualTo(0),
                () -> assertThat(((ThreadPoolExecutor) pool).getActiveCount()).isEqualTo(0),
                () -> assertThat(queue.getElementsAsList().size()).isLessThan(capacity + 1),
                () -> assertThat(queue.getElementsAsList().size()).isGreaterThan(-1)
        );

    }

}
