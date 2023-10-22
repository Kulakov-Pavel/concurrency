package course.concurrency.m3_shared;

import java.util.concurrent.TimeUnit;

import static course.concurrency.m3_shared.PingPong.Value.*;

public class PingPong {
    private final static Class<PingPong> lock = PingPong.class;
    private static volatile Value value;

    static {
        value = PANG;
    }

    public static void ping() {
        for (; ; ) {
            synchronized (lock) {
                while (value != PANG) {
                    waitThread();
                }
                sleep(1);
                System.out.println(PING.value);
                value = PING;
                lock.notifyAll();
            }
        }
    }

    public static void pong() {
        for (; ; ) {
            synchronized (lock) {
                while (value != PING) {
                    waitThread();
                }
                sleep(1);
                System.out.println(PONG.value);
                value = PONG;
                lock.notifyAll();
            }
        }
    }

    public static void pang() {
        for (; ; ) {
            synchronized (lock ) {
                while (value != PONG) {
                    waitThread();
                }
                sleep(1);
                System.out.println(PANG.value);
                value = PANG;
                lock.notifyAll();
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
        Thread t3 = new Thread(PingPong::pang);
        t2.start();
        t3.start();
        t1.start();
    }

    enum Value {
        PING("ping"),
        PONG("pong"),
        PANG("pang");

        public final String value;

        Value(String value) {
            this.value = value;
        }
    }

}
