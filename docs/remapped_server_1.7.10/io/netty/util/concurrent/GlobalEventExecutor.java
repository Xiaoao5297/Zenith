package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class GlobalEventExecutor extends AbstractEventExecutor {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
    public static final GlobalEventExecutor INSTANCE = new GlobalEventExecutor();
    final Queue<Runnable> taskQueue = new LinkedBlockingQueue();
    final Queue<ScheduledFutureTask<?>> delayedTaskQueue = new PriorityQueue();
    final ScheduledFutureTask<Void> purgeTask;
    private final ThreadFactory threadFactory;
    private final GlobalEventExecutor.TaskRunner taskRunner;
    private final Object stateLock;
    volatile Thread thread;
    private volatile int state;
    private final Future<?> terminationFuture;

    private GlobalEventExecutor() {
        this.purgeTask = new ScheduledFutureTask<>(this, this.delayedTaskQueue, Executors.callable(new GlobalEventExecutor.PurgeTask(), (Object) null), ScheduledFutureTask.deadlineNanos(GlobalEventExecutor.SCHEDULE_PURGE_INTERVAL), -GlobalEventExecutor.SCHEDULE_PURGE_INTERVAL);
        this.threadFactory = new DefaultThreadFactory(this.getClass());
        this.taskRunner = new GlobalEventExecutor.TaskRunner();
        this.stateLock = new Object();
        this.state = 1;
        this.terminationFuture = new FailedFuture<>(this, new UnsupportedOperationException());
        this.delayedTaskQueue.add(this.purgeTask);
    }

    @Override
    public EventExecutorGroup parent() {
        return null;
    }

    Runnable takeTask() {
        BlockingQueue blockingqueue = (BlockingQueue) this.taskQueue;

        Runnable runnable;

        do {
            ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) this.delayedTaskQueue.peek();

            if (scheduledfuturetask == null) {
                Runnable runnable1 = null;

                try {
                    runnable1 = (Runnable) blockingqueue.take();
                } catch (InterruptedException interruptedexception) {
                    ;
                }

                return runnable1;
            }

            long i = scheduledfuturetask.delayNanos();

            if (i > 0L) {
                try {
                    runnable = (Runnable) blockingqueue.poll(i, TimeUnit.NANOSECONDS);
                } catch (InterruptedException interruptedexception1) {
                    return null;
                }
            } else {
                runnable = (Runnable) blockingqueue.poll();
            }

            if (runnable == null) {
                this.fetchFromDelayedQueue();
                runnable = (Runnable) blockingqueue.poll();
            }
        } while (runnable == null);

        return runnable;
    }

    private void fetchFromDelayedQueue() {
        long i = 0L;

        while (true) {
            ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) this.delayedTaskQueue.peek();

            if (scheduledfuturetask == null) {
                break;
            }

            if (i == 0L) {
                i = ScheduledFutureTask.nanoTime();
            }

            if (scheduledfuturetask.deadlineNanos() > i) {
                break;
            }

            this.delayedTaskQueue.remove();
            this.taskQueue.add(scheduledfuturetask);
        }

    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    private void addTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("task");
        } else {
            this.taskQueue.add(runnable);
        }
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    @Override
    public Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit) {
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) {
        return false;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("task");
        } else {
            this.addTask(runnable);
            if (!this.inEventLoop()) {
                this.startThread();
            }

        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit) {
        if (runnable == null) {
            throw new NullPointerException("command");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (i < 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", i));
        } else {
            return this.schedule(new ScheduledFutureTask<>(this, this.delayedTaskQueue, runnable, (Object) null, ScheduledFutureTask.deadlineNanos(timeunit.toNanos(i))));
        }
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit) {
        if (callable == null) {
            throw new NullPointerException("callable");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (i < 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", i));
        } else {
            return this.schedule(new ScheduledFutureTask<>(this, this.delayedTaskQueue, callable, ScheduledFutureTask.deadlineNanos(timeunit.toNanos(i))));
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit) {
        if (runnable == null) {
            throw new NullPointerException("command");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (i < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", i));
        } else if (j <= 0L) {
            throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", j));
        } else {
            return this.schedule(new ScheduledFutureTask<>(this, this.delayedTaskQueue, Executors.callable(runnable, (Object) null), ScheduledFutureTask.deadlineNanos(timeunit.toNanos(i)), timeunit.toNanos(j)));
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit) {
        if (runnable == null) {
            throw new NullPointerException("command");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (i < 0L) {
            throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", i));
        } else if (j <= 0L) {
            throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", j));
        } else {
            return this.schedule(new ScheduledFutureTask<>(this, this.delayedTaskQueue, Executors.callable(runnable, (Object) null), ScheduledFutureTask.deadlineNanos(timeunit.toNanos(i)), -timeunit.toNanos(j)));
        }
    }

    private <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> scheduledfuturetask) {
        if (scheduledfuturetask == null) {
            throw new NullPointerException("task");
        } else {
            if (this.inEventLoop()) {
                this.delayedTaskQueue.add(scheduledfuturetask);
            } else {
                this.execute(new Runnable() {
                    public void run() {
                        GlobalEventExecutor.this.delayedTaskQueue.add(scheduledfuturetask);
                    }
                });
            }

            return scheduledfuturetask;
        }
    }

    private void startThread() {
        Object object = this.stateLock;

        synchronized (this.stateLock) {
            if (this.state == 1) {
                this.state = 2;
                this.thread = this.threadFactory.newThread(this.taskRunner);
                this.thread.start();
            }

        }
    }

    private final class PurgeTask implements Runnable {

        private PurgeTask() {}

        public void run() {
            Iterator iterator = GlobalEventExecutor.this.delayedTaskQueue.iterator();

            while (iterator.hasNext()) {
                ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) iterator.next();

                if (scheduledfuturetask.isCancelled()) {
                    iterator.remove();
                }
            }

        }
    }

    final class TaskRunner implements Runnable {

        TaskRunner() {}

        public void run() {
            while (true) {
                Runnable runnable = GlobalEventExecutor.this.takeTask();

                if (runnable != null) {
                    try {
                        runnable.run();
                    } catch (Throwable throwable) {
                        GlobalEventExecutor.logger.warn("Unexpected exception from the global event executor: ", throwable);
                    }

                    if (runnable != GlobalEventExecutor.this.purgeTask) {
                        continue;
                    }
                }

                if (GlobalEventExecutor.this.taskQueue.isEmpty() && GlobalEventExecutor.this.delayedTaskQueue.size() == 1) {
                    synchronized (GlobalEventExecutor.this.stateLock) {
                        if (GlobalEventExecutor.this.taskQueue.isEmpty() && GlobalEventExecutor.this.delayedTaskQueue.size() == 1) {
                            GlobalEventExecutor.this.state = 1;
                            return;
                        }
                    }
                }
            }
        }
    }
}
