package course.concurrency.m3_shared;

import java.util.concurrent.TimeUnit;

import static course.concurrency.m3_shared.PingPong.Value.*;

public class PingPong {
    private final static Class<PingPong> lock = PingPong.class;
    private static volatile Value value;

    public static void ping() {
        for (; ; ) {
            if (value == PANG || null == value) {
                synchronized (lock) {
                    sleep(1);
                    System.out.println(PING.value);
                    value = PING;
                }
            }
        }
    }

    public static void pong() {

        for (; ; ) {
            if (value == PING) {
                synchronized (lock) {
                    sleep(1);
                    System.out.println(PONG.value);
                    value = PONG;
                }
            }
        }
    }

    public static void pang() {

        for (; ; ) {
            if (value == PONG) {
                synchronized (lock) {
                    sleep(1);
                    System.out.println(PANG.value);
                    value = PANG;
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
        Thread t3 = new Thread(PingPong::pang);
        t3.start();
        t2.start();
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
