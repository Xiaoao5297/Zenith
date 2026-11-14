package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public final class Queues {

    private Queues() {}

    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int i) {
        return new ArrayBlockingQueue(i);
    }

    public static <E> ArrayDeque<E> newArrayDeque() {
        return new ArrayDeque();
    }

    public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new ArrayDeque(Collections2.cast(iterable));
        } else {
            ArrayDeque arraydeque = new ArrayDeque();

            Iterables.addAll(arraydeque, iterable);
            return arraydeque;
        }
    }

    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue();
    }

    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new ConcurrentLinkedQueue(Collections2.cast(iterable));
        } else {
            ConcurrentLinkedQueue concurrentlinkedqueue = new ConcurrentLinkedQueue();

            Iterables.addAll(concurrentlinkedqueue, iterable);
            return concurrentlinkedqueue;
        }
    }

    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
        return new LinkedBlockingDeque();
    }

    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int i) {
        return new LinkedBlockingDeque(i);
    }

    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new LinkedBlockingDeque(Collections2.cast(iterable));
        } else {
            LinkedBlockingDeque linkedblockingdeque = new LinkedBlockingDeque();

            Iterables.addAll(linkedblockingdeque, iterable);
            return linkedblockingdeque;
        }
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue();
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int i) {
        return new LinkedBlockingQueue(i);
    }

    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new LinkedBlockingQueue(Collections2.cast(iterable));
        } else {
            LinkedBlockingQueue linkedblockingqueue = new LinkedBlockingQueue();

            Iterables.addAll(linkedblockingqueue, iterable);
            return linkedblockingqueue;
        }
    }

    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue();
    }

    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new PriorityBlockingQueue(Collections2.cast(iterable));
        } else {
            PriorityBlockingQueue priorityblockingqueue = new PriorityBlockingQueue();

            Iterables.addAll(priorityblockingqueue, iterable);
            return priorityblockingqueue;
        }
    }

    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue();
    }

    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> iterable) {
        if (iterable instanceof Collection) {
            return new PriorityQueue(Collections2.cast(iterable));
        } else {
            PriorityQueue priorityqueue = new PriorityQueue();

            Iterables.addAll(priorityqueue, iterable);
            return priorityqueue;
        }
    }

    public static <E> SynchronousQueue<E> newSynchronousQueue() {
        return new SynchronousQueue();
    }

    @Beta
    public static <E> int drain(BlockingQueue<E> blockingqueue, Collection<? super E> collection, int i, long j, TimeUnit timeunit) throws InterruptedException {
        Preconditions.checkNotNull(collection);
        long k = System.nanoTime() + timeunit.toNanos(j);
        int l = 0;

        while (l < i) {
            l += blockingqueue.drainTo(collection, i - l);
            if (l < i) {
                Object object = blockingqueue.poll(k - System.nanoTime(), TimeUnit.NANOSECONDS);

                if (object == null) {
                    break;
                }

                collection.add(object);
                ++l;
            }
        }

        return l;
    }

    @Beta
    public static <E> int drainUninterruptibly(BlockingQueue<E> blockingqueue, Collection<? super E> collection, int i, long j, TimeUnit timeunit) {
        Preconditions.checkNotNull(collection);
        long k = System.nanoTime() + timeunit.toNanos(j);
        int l = 0;
        boolean flag = false;

        try {
            while (l < i) {
                l += blockingqueue.drainTo(collection, i - l);
                if (l < i) {
                    Object object;

                    while (true) {
                        try {
                            object = blockingqueue.poll(k - System.nanoTime(), TimeUnit.NANOSECONDS);
                            break;
                        } catch (InterruptedException interruptedexception) {
                            flag = true;
                        }
                    }

                    if (object == null) {
                        break;
                    }

                    collection.add(object);
                    ++l;
                }
            }
        } finally {
            if (flag) {
                Thread.currentThread().interrupt();
            }

        }

        return l;
    }

    @Beta
    public static <E> Queue<E> synchronizedQueue(Queue<E> queue) {
        return Synchronized.queue(queue, (Object) null);
    }

    @Beta
    public static <E> Deque<E> synchronizedDeque(Deque<E> deque) {
        return Synchronized.deque(deque, (Object) null);
    }
}
