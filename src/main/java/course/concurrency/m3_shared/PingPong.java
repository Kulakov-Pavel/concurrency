package course.concurrency.m3_shared;

import java.util.concurrent.TimeUnit;

public class PingPong {

    private final static Class<PingPong> lock = PingPong.class;
    private static volatile boolean started;

    public static void ping() {
        synchronized (lock) {
            started = true;
            System.out.println("Ping has started the game...\n");
            for (; ; ) {
                sleep(1);
                System.out.println("ping");
                lock.notify();
                waitThread();
            }
        }
    }

    public static void pong() {
        synchronized (lock) {
            if (started) {
                for (; true; ) {
                    sleep(1);
                    System.out.println("pong");
                    lock.notify();
                    waitThread();
                }
            } else {
                System.out.println("Pong passes the ball to Ping");
                waitThread();
                pong();
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
        t1.start();
        t2.start();
    }
}
