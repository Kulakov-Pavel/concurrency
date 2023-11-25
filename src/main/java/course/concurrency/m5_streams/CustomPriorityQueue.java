package course.concurrency.m5_streams;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CustomPriorityQueue {
    public static void main(String[] args) {

        PriorityQueue<Item> queue = new PriorityQueue<>(Comparator.comparing(Item::getValue));
        PriorityQueue<Item> timestampQueue = new PriorityQueue<>(Comparator.comparing(Item::getTimestamp));
        PriorityQueue<Item> valueQueue = new PriorityQueue<>(Comparator.comparing(Item::getValue));

        queue.offer(new Item("a", LocalDate.now()));
        queue.offer(new Item("b", LocalDate.now().minusDays(1)));
        queue.offer(new Item("c", LocalDate.now().minusDays(2)));
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        timestampQueue.addAll(queue);
        queue = timestampQueue;
        queue.offer(new Item("d", LocalDate.now().minusDays(3)));
        queue.offer(new Item("e", LocalDate.now().minusDays(4)));
        queue.offer(new Item("f", LocalDate.now().minusDays(5)));
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }

    @Getter
    private static class Item implements Comparable<Item>{
        String value;
        LocalDate timestamp;

        public Item(String value, LocalDate timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        @Override
        public int compareTo(Item item) {
            return timestamp.compareTo(item.getTimestamp());
        }

        @Override
        public String toString() {
            return "Item{" +
                    "value='" + value + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
