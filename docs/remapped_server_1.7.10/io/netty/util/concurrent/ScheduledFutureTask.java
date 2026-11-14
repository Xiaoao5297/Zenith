package io.netty.util.concurrent;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class ScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V> {

    private static final AtomicLong nextTaskId = new AtomicLong();
    private static final long START_TIME = System.nanoTime();
    private final long id;
    private final Queue<ScheduledFutureTask<?>> delayedTaskQueue;
    private long deadlineNanos;
    private final long periodNanos;

    static long nanoTime() {
        return System.nanoTime() - ScheduledFutureTask.START_TIME;
    }

    static long deadlineNanos(long i) {
        return nanoTime() + i;
    }

    ScheduledFutureTask(EventExecutor eventexecutor, Queue<ScheduledFutureTask<?>> queue, Runnable runnable, V v0, long i) {
        this(eventexecutor, queue, toCallable(runnable, v0), i);
    }

    ScheduledFutureTask(EventExecutor eventexecutor, Queue<ScheduledFutureTask<?>> queue, Callable<V> callable, long i, long j) {
        super(eventexecutor, callable);
        this.id = ScheduledFutureTask.nextTaskId.getAndIncrement();
        if (j == 0L) {
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        } else {
            this.delayedTaskQueue = queue;
            this.deadlineNanos = i;
            this.periodNanos = j;
        }
    }

    ScheduledFutureTask(EventExecutor eventexecutor, Queue<ScheduledFutureTask<?>> queue, Callable<V> callable, long i) {
        super(eventexecutor, callable);
        this.id = ScheduledFutureTask.nextTaskId.getAndIncrement();
        this.delayedTaskQueue = queue;
        this.deadlineNanos = i;
        this.periodNanos = 0L;
    }

    @Override
    protected EventExecutor executor() {
        return super.executor();
    }

    public long deadlineNanos() {
        return this.deadlineNanos;
    }

    public long delayNanos() {
        return Math.max(0L, this.deadlineNanos() - nanoTime());
    }

    public long delayNanos(long i) {
        return Math.max(0L, this.deadlineNanos() - (i - ScheduledFutureTask.START_TIME));
    }

    public long getDelay(TimeUnit timeunit) {
        return timeunit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
    }

    public int compareTo(Delayed delayed) {
        if (this == delayed) {
            return 0;
        } else {
            ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) delayed;
            long i = this.deadlineNanos() - scheduledfuturetask.deadlineNanos();

            if (i < 0L) {
                return -1;
            } else if (i > 0L) {
                return 1;
            } else if (this.id < scheduledfuturetask.id) {
                return -1;
            } else if (this.id == scheduledfuturetask.id) {
                throw new Error();
            } else {
                return 1;
            }
        }
    }

    @Override
    public void run() {
        assert this.executor().inEventLoop();

        try {
            if (this.periodNanos == 0L) {
                if (this.setUncancellableInternal()) {
                    Object object = this.task.call();

                    this.setSuccessInternal(object);
                }
            } else if (!this.isCancelled()) {
                this.task.call();
                if (!this.executor().isShutdown()) {
                    long i = this.periodNanos;

                    if (i > 0L) {
                        this.deadlineNanos += i;
                    } else {
                        this.deadlineNanos = nanoTime() - i;
                    }

                    if (!this.isCancelled()) {
                        this.delayedTaskQueue.add(this);
                    }
                }
            }
        } catch (Throwable throwable) {
            this.setFailureInternal(throwable);
        }

    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder stringbuilder = super.toStringBuilder();

        stringbuilder.setCharAt(stringbuilder.length() - 1, ',');
        stringbuilder.append(" id: ");
        stringbuilder.append(this.id);
        stringbuilder.append(", deadline: ");
        stringbuilder.append(this.deadlineNanos);
        stringbuilder.append(", period: ");
        stringbuilder.append(this.periodNanos);
        stringbuilder.append(')');
        return stringbuilder;
    }
}
