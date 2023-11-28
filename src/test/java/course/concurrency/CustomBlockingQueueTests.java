package course.concurrency;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CustomBlockingQueueTests {

    private CustomBlockingQueue<Integer> queue;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @ParameterizedTest
    @ValueSource(ints = {10, 1000, 10_000, 1_000_000})
    void whenWritersEqualsReaders_thenRestEqualsZero(int value) throws ExecutionException, InterruptedException {
        int capacity = 7;
        queue = new CustomBlockingQueue<>(capacity);

        List<Future<?>> futures = Stream.iterate(0, n -> ++n).limit(value)
                .map(i -> i % 2 == 0 ? pool.submit(() -> queue.enqueue(i)) : pool.submit(() -> queue.dequeue()))
                .toList();
        for (Future<?> f : futures) {
            f.get();
        }

        assertAll(
                () -> assertThat(queue.size()).isEqualTo(0),
                () -> assertThat(queue.analyse()).contains(String.format("in: %d", value / 2)),
                () -> assertThat(queue.analyse()).contains(String.format("out: %d", value / 2))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    void whenQueueWorks_thenItWorksInFIFO(int value) {
        queue = new CustomBlockingQueue<>(value);
        List<Integer> benchmark = Stream.iterate(0, n -> ++n).limit(value).toList();
        List<Integer> result = new ArrayList<>();

        IntStream.range(0, value).forEach(queue::enqueue);
        for (int i = 0; i < value; i++) {
            result.add(queue.dequeue());
        }

        assertAll(
                () -> assertThat(queue.size()).isEqualTo(0),
                () -> assertThat(result.size()).isEqualTo(value),
                () -> assertThat(result).isEqualTo(benchmark)
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

        assertAll(
                () -> assertThat(queue.size()).isEqualTo(0),
                () -> assertThat(queue.analyse()).contains(String.format("in: %d", value / 2)),
                () -> assertThat(queue.analyse()).contains(String.format("out: %d", value / 2))
        );

    }

}
