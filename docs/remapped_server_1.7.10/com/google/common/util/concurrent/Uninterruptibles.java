package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public final class Uninterruptibles {

    public static void awaitUninterruptibly(CountDownLatch countdownlatch) {
        boolean flag = false;

        try {
            while (true) {
                try {
                    countdownlatch.await();
                    return;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static boolean awaitUninterruptibly(CountDownLatch countdownlatch, long i, TimeUnit timeunit) {
        boolean flag = false;

        try {
            long j = timeunit.toNanos(i);
            long k = System.nanoTime() + j;

            while (true) {
                try {
                    boolean flag1 = countdownlatch.await(j, TimeUnit.NANOSECONDS);

                    return flag1;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                    j = k - System.nanoTime();
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static void joinUninterruptibly(Thread thread) {
        boolean flag = false;

        try {
            while (true) {
                try {
                    thread.join();
                    return;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean flag = false;

        try {
            while (true) {
                try {
                    Object object = future.get();

                    return object;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static <V> V getUninterruptibly(Future<V> future, long i, TimeUnit timeunit) throws ExecutionException, TimeoutException {
        boolean flag = false;

        try {
            long j = timeunit.toNanos(i);
            long k = System.nanoTime() + j;

            while (true) {
                try {
                    Object object = future.get(j, TimeUnit.NANOSECONDS);

                    return object;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                    j = k - System.nanoTime();
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static void joinUninterruptibly(Thread thread, long i, TimeUnit timeunit) {
        Preconditions.checkNotNull(thread);
        boolean flag = false;

        try {
            long j = timeunit.toNanos(i);
            long k = System.nanoTime() + j;

            while (true) {
                try {
                    TimeUnit.NANOSECONDS.timedJoin(thread, j);
                    return;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                    j = k - System.nanoTime();
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static <E> E takeUninterruptibly(BlockingQueue<E> blockingqueue) {
        boolean flag = false;

        try {
            while (true) {
                try {
                    Object object = blockingqueue.take();

                    return object;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static <E> void putUninterruptibly(BlockingQueue<E> blockingqueue, E e0) {
        boolean flag = false;

        try {
            while (true) {
                try {
                    blockingqueue.put(e0);
                    return;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static void sleepUninterruptibly(long i, TimeUnit timeunit) {
        boolean flag = false;

        try {
            long j = timeunit.toNanos(i);
            long k = System.nanoTime() + j;

            while (true) {
                try {
                    TimeUnit.NANOSECONDS.sleep(j);
                    return;
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                    j = k - System.nanoTime();
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }
    }

    private Uninterruptibles() {}
}
