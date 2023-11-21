package course.concurrency.m5_streams;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CustomPriorityQueue {
    public static void main(String[] args) {

        Comparator<Item> itemComparator = Comparator.comparing(Item::getTimestamp)
                .thenComparing(Item::getValue);

        PriorityQueue<Item> queue = new PriorityQueue<>(5, itemComparator);

        queue.offer(new Item("ccc", LocalDate.now()));
        queue.offer(new Item("ccc", LocalDate.now().minusDays(1)));
        queue.offer(new Item("ccc", LocalDate.now().minusDays(2)));
        queue.offer(new Item("a", LocalDate.now()));
        queue.offer(new Item("ccc", LocalDate.now().minusDays(3)));

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }

    }

    @Getter
    private static class Item {
        String value;
        LocalDate timestamp;

        public Item(String value, LocalDate timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("""
                    value: %s
                    timestamp: %s
                    """, value, timestamp);
        }
    }
}
