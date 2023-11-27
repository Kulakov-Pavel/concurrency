package course.concurrency;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomBlockingQueue<T> {

    private volatile int size;
    private final int capacity;
    private Node<T> head;
    private Node<T> tail;
    private final Object lock = new Object();

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(T value) {
        if (size >= capacity) {
            return;
        }
        synchronized (lock) {
            var node = new Node<T>(value);
            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
            ++size;
        }
    }

    public T dequeue() {
        if (size == 0) {
            return null;
        }
        synchronized (lock) {
            var node = head;
            if (size == 1) {
                head = tail = null;
            } else {
                if (size == 2) {
                    head = tail;
                } else {
                    head = head.next;
                }
            }
            --size;
            return node.getValue();
        }
    }

    public List<T> getElementsAsList() {
        synchronized (lock) {
            List<T> result = new ArrayList<>();
            if (head != null) {
                result.add(head.getValue());
                Node<T> next = head;
                while (next.hasNext()) {
                    result.add(next.next.getValue());
                    next = next.getNext();
                }
            }
            return result;
        }
    }

    @Getter
    @Setter
    private static class Node<T> {
        private final T value;
        private Node<T> next;

        Node(T value) {
            this.value = value;
        }

        public boolean hasNext() {
            return next != null;
        }

    }

    @Override
    public String toString() {
        String elements = getElementsAsList().stream()
                .map(T::toString)
                .collect(Collectors.joining(", "));
        return String.format("""
                size: %d
                elements: %s
                """, size, elements);
    }
}
