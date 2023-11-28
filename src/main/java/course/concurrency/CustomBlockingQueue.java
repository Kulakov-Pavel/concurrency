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

    private int in;
    private int out;

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void enqueue(T value) {
        while (size >= capacity) {
            notifyAll();
            await();
        }
        ++in;
        var node = new Node<T>(value);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        ++size;
        notifyAll();
    }

    public synchronized T dequeue() {
        while (size == 0) {
            notifyAll();
            await();
        }
        ++out;
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
        notifyAll();
        return node.getValue();
    }

    public synchronized List<T> getElementsAsList() {
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

    public synchronized int size() {
        return size;
    }

    private void await() {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized String toString() {
        String elements = getElementsAsList().stream()
                .map(T::toString)
                .collect(Collectors.joining(", "));
        return String.format("""
                size: %d
                elements: %s
                """, size, elements);
    }

    public synchronized String analyse() {
        return String.format("""
                size: %d
                in: %d
                out: %d
                """, size, in, out);
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

}
