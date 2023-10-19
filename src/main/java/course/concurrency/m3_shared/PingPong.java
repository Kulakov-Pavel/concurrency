package course.concurrency.m3_shared;

import java.util.concurrent.TimeUnit;

import static course.concurrency.m3_shared.PingPong.Value.*;

public class PingPong {
    private final static Class<PingPong> lock = PingPong.class;
    private static volatile Value value;

    public static void ping() {
        synchronized (lock) {
            for (; ; ) {
                if (value == PONG || null == value) {
                    sleep(1);
                    System.out.println(PING.value);
                    value = PING;
                } else {
                    lock.notify();
                    waitThread();
                }
            }
        }
    }

    public static void pong() {
        synchronized (lock) {
            for (; ; ) {
                if (value == PING) {
                    sleep(1);
                    System.out.println(PONG.value);
                    value = PONG;
                } else {
                    lock.notify();
                    waitThread();
                }
            }
        }
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitThread() {
        try {
            lock.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(PingPong::ping);
        Thread t2 = new Thread(PingPong::pong);
        t2.start();
        t1.start();
    }

    enum Value {
        PING("ping"),
        PONG("pong");

        public final String value;

        Value(String value) {
            this.value = value;
        }
    }

}
