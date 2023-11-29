package course.concurrency;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CustomBlockingQueueTests {

    private CustomBlockingQueue<Integer> queue;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenWritersEqualsReaders_thenRestEqualsZero(int value) throws InterruptedException {
        int capacity = 16;
        queue = new CustomBlockingQueue<>(capacity);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < value; i++) {
            if(i % 2 == 0) {
                pool.submit(() -> {
                    queue.enqueue(0);
                    await(latch);
                });
            } else {
                pool.submit(() -> {
                    queue.dequeue();
                    await(latch);
                });
            }
        }
        latch.countDown();
        pool.shutdown();
        boolean done = pool.awaitTermination(60, SECONDS);

        assertThat(done).isTrue();
        assertThat(queue.size()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    void whenQueueWorks_thenItWorksInFIFO(int value) {
        queue = new CustomBlockingQueue<>(value);
        List<Integer> expected = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < value; i++) {
            expected.add(i);
            queue.enqueue(i);
        }
        for (int i = 0; i < value; i++) {
            result.add(queue.dequeue());
        }

        assertAll(
                () -> assertThat(queue.size()).isEqualTo(0),
                () -> assertThat(result.size()).isEqualTo(value),
                () -> assertThat(result).isEqualTo(expected)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenQueueWorksInSingleThread_thenSuccess(int value) {
        int capacity = 16;
        queue = new CustomBlockingQueue<>(capacity);

        for (int i = 0; i < value; i++) {
            if (i % 2 == 0) {
                queue.enqueue(i);
            } else {
                queue.dequeue();
            }
        }

        assertThat(queue.size()).isEqualTo(0);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
