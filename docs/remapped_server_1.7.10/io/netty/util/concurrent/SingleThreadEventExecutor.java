package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class SingleThreadEventExecutor extends AbstractEventExecutor {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;
    private static final Runnable WAKEUP_TASK = new Runnable() {
        public void run() {}
    };
    private final EventExecutorGroup parent;
    private final Queue<Runnable> taskQueue;
    final Queue<ScheduledFutureTask<?>> delayedTaskQueue = new PriorityQueue();
    private final Thread thread;
    private final Object stateLock = new Object();
    private final Semaphore threadLock = new Semaphore(0);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet();
    private final boolean addTaskWakesUp;
    private long lastExecutionTime;
    private volatile int state = 1;
    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;
    private final Promise<?> terminationFuture;
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);

    protected SingleThreadEventExecutor(EventExecutorGroup eventexecutorgroup, ThreadFactory threadfactory, boolean flag) {
        this.terminationFuture = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);
        if (threadfactory == null) {
            throw new NullPointerException("threadFactory");
        } else {
            this.parent = eventexecutorgroup;
            this.addTaskWakesUp = flag;
            this.thread = threadfactory.newThread(new Runnable() {
                public void run() {
                    boolean flag1 = false;

                    SingleThreadEventExecutor.this.updateLastExecutionTime();
                    boolean flag2 = false;

                    label2893:
                    {
                        try {
                            flag2 = true;
                            SingleThreadEventExecutor.this.run();
                            flag1 = true;
                            flag2 = false;
                            break label2893;
                        } catch (Throwable throwable) {
                            SingleThreadEventExecutor.logger.warn("Unexpected exception from an event executor: ", throwable);
                            flag2 = false;
                        } finally {
                            if (flag2) {
                                if (SingleThreadEventExecutor.this.state < 3) {
                                    SingleThreadEventExecutor.this.state = 3;
                                }

                                if (flag1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                                    SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called " + "before run() implementation terminates.");
                                }

                                while (true) {
                                    boolean flag3 = false;

                                    try {
                                        flag3 = true;
                                        if (SingleThreadEventExecutor.this.confirmShutdown()) {
                                            flag3 = false;
                                            break;
                                        }
                                    } finally {
                                        if (flag3) {
                                            boolean flag4 = false;

                                            try {
                                                flag4 = true;
                                                SingleThreadEventExecutor.this.cleanup();
                                                flag4 = false;
                                            } finally {
                                                if (flag4) {
                                                    synchronized (SingleThreadEventExecutor.this.stateLock) {
                                                        SingleThreadEventExecutor.this.state = 5;
                                                    }

                                                    SingleThreadEventExecutor.this.threadLock.release();
                                                    if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                                        SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                                    }

                                                    SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                                }
                                            }

                                            synchronized (SingleThreadEventExecutor.this.stateLock) {
                                                SingleThreadEventExecutor.this.state = 5;
                                            }

                                            SingleThreadEventExecutor.this.threadLock.release();
                                            if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                                SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                            }

                                            SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                        }
                                    }
                                }

                                boolean flag5 = false;

                                try {
                                    flag5 = true;
                                    SingleThreadEventExecutor.this.cleanup();
                                    flag5 = false;
                                } finally {
                                    if (flag5) {
                                        synchronized (SingleThreadEventExecutor.this.stateLock) {
                                            SingleThreadEventExecutor.this.state = 5;
                                        }

                                        SingleThreadEventExecutor.this.threadLock.release();
                                        if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                            SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                        }

                                        SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                    }
                                }

                                synchronized (SingleThreadEventExecutor.this.stateLock) {
                                    SingleThreadEventExecutor.this.state = 5;
                                }

                                SingleThreadEventExecutor.this.threadLock.release();
                                if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                    SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                }

                                SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                            }
                        }

                        if (SingleThreadEventExecutor.this.state < 3) {
                            SingleThreadEventExecutor.this.state = 3;
                        }

                        if (flag1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                            SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called " + "before run() implementation terminates.");
                        }

                        while (true) {
                            boolean flag6 = false;

                            try {
                                flag6 = true;
                                if (SingleThreadEventExecutor.this.confirmShutdown()) {
                                    flag6 = false;
                                    break;
                                }
                            } finally {
                                if (flag6) {
                                    boolean flag7 = false;

                                    try {
                                        flag7 = true;
                                        SingleThreadEventExecutor.this.cleanup();
                                        flag7 = false;
                                    } finally {
                                        if (flag7) {
                                            synchronized (SingleThreadEventExecutor.this.stateLock) {
                                                SingleThreadEventExecutor.this.state = 5;
                                            }

                                            SingleThreadEventExecutor.this.threadLock.release();
                                            if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                                SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                            }

                                            SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                        }
                                    }

                                    synchronized (SingleThreadEventExecutor.this.stateLock) {
                                        SingleThreadEventExecutor.this.state = 5;
                                    }

                                    SingleThreadEventExecutor.this.threadLock.release();
                                    if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                        SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                    }

                                    SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                }
                            }
                        }

                        boolean flag8 = false;

                        try {
                            flag8 = true;
                            SingleThreadEventExecutor.this.cleanup();
                            flag8 = false;
                        } finally {
                            if (flag8) {
                                synchronized (SingleThreadEventExecutor.this.stateLock) {
                                    SingleThreadEventExecutor.this.state = 5;
                                }

                                SingleThreadEventExecutor.this.threadLock.release();
                                if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                    SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                }

                                SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                            }
                        }

                        synchronized (SingleThreadEventExecutor.this.stateLock) {
                            SingleThreadEventExecutor.this.state = 5;
                        }

                        SingleThreadEventExecutor.this.threadLock.release();
                        if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                            SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                        }

                        SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                        return;
                    }

                    if (SingleThreadEventExecutor.this.state < 3) {
                        SingleThreadEventExecutor.this.state = 3;
                    }

                    if (flag1 && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L) {
                        SingleThreadEventExecutor.logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called " + "before run() implementation terminates.");
                    }

                    while (true) {
                        boolean flag9 = false;

                        try {
                            flag9 = true;
                            if (SingleThreadEventExecutor.this.confirmShutdown()) {
                                flag9 = false;
                                break;
                            }
                        } finally {
                            if (flag9) {
                                boolean flag10 = false;

                                try {
                                    flag10 = true;
                                    SingleThreadEventExecutor.this.cleanup();
                                    flag10 = false;
                                } finally {
                                    if (flag10) {
                                        synchronized (SingleThreadEventExecutor.this.stateLock) {
                                            SingleThreadEventExecutor.this.state = 5;
                                        }

                                        SingleThreadEventExecutor.this.threadLock.release();
                                        if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                            SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                        }

                                        SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                                    }
                                }

                                synchronized (SingleThreadEventExecutor.this.stateLock) {
                                    SingleThreadEventExecutor.this.state = 5;
                                }

                                SingleThreadEventExecutor.this.threadLock.release();
                                if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                    SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                                }

                                SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                            }
                        }
                    }

                    boolean flag11 = false;

                    try {
                        flag11 = true;
                        SingleThreadEventExecutor.this.cleanup();
                        flag11 = false;
                    } finally {
                        if (flag11) {
                            synchronized (SingleThreadEventExecutor.this.stateLock) {
                                SingleThreadEventExecutor.this.state = 5;
                            }

                            SingleThreadEventExecutor.this.threadLock.release();
                            if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                                SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                            }

                            SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                        }
                    }

                    synchronized (SingleThreadEventExecutor.this.stateLock) {
                        SingleThreadEventExecutor.this.state = 5;
                    }

                    SingleThreadEventExecutor.this.threadLock.release();
                    if (!SingleThreadEventExecutor.this.taskQueue.isEmpty()) {
                        SingleThreadEventExecutor.logger.warn("An event executor terminated with non-empty task queue (" + SingleThreadEventExecutor.this.taskQueue.size() + ')');
                    }

                    SingleThreadEventExecutor.this.terminationFuture.setSuccess((Object) null);
                }
            });
            this.taskQueue = this.newTaskQueue();
        }
    }

    protected Queue<Runnable> newTaskQueue() {
        return new LinkedBlockingQueue();
    }

    @Override
    public EventExecutorGroup parent() {
        return this.parent;
    }

    protected void interruptThread() {
        this.thread.interrupt();
    }

    protected Runnable pollTask() {
        assert this.inEventLoop();

        Runnable runnable;

        do {
            runnable = (Runnable) this.taskQueue.poll();
        } while (runnable == SingleThreadEventExecutor.WAKEUP_TASK);

        return runnable;
    }

    protected Runnable takeTask() {
        assert this.inEventLoop();

        if (!(this.taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        } else {
            BlockingQueue blockingqueue = (BlockingQueue) this.taskQueue;

            Runnable runnable;

            do {
                ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) this.delayedTaskQueue.peek();

                if (scheduledfuturetask == null) {
                    Runnable runnable1 = null;

                    try {
                        runnable1 = (Runnable) blockingqueue.take();
                        if (runnable1 == SingleThreadEventExecutor.WAKEUP_TASK) {
                            runnable1 = null;
                        }
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }

                    return runnable1;
                }

                long i = scheduledfuturetask.delayNanos();

                runnable = null;
                if (i > 0L) {
                    try {
                        runnable = (Runnable) blockingqueue.poll(i, TimeUnit.NANOSECONDS);
                    } catch (InterruptedException interruptedexception1) {
                        return null;
                    }
                }

                if (runnable == null) {
                    this.fetchFromDelayedQueue();
                    runnable = (Runnable) blockingqueue.poll();
                }
            } while (runnable == null);

            return runnable;
        }
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

    protected Runnable peekTask() {
        assert this.inEventLoop();

        return (Runnable) this.taskQueue.peek();
    }

    protected boolean hasTasks() {
        assert this.inEventLoop();

        return !this.taskQueue.isEmpty();
    }

    public final int pendingTasks() {
        return this.taskQueue.size();
    }

    protected void addTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("task");
        } else {
            if (this.isShutdown()) {
                reject();
            }

            this.taskQueue.add(runnable);
        }
    }

    protected boolean removeTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("task");
        } else {
            return this.taskQueue.remove(runnable);
        }
    }

    protected boolean runAllTasks() {
        this.fetchFromDelayedQueue();
        Runnable runnable = this.pollTask();

        if (runnable == null) {
            return false;
        } else {
            do {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    SingleThreadEventExecutor.logger.warn("A task raised an exception.", throwable);
                }

                runnable = this.pollTask();
            } while (runnable != null);

            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
            return true;
        }
    }

    protected boolean runAllTasks(long i) {
        this.fetchFromDelayedQueue();
        Runnable runnable = this.pollTask();

        if (runnable == null) {
            return false;
        } else {
            long j = ScheduledFutureTask.nanoTime() + i;
            long k = 0L;

            long l;

            while (true) {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    SingleThreadEventExecutor.logger.warn("A task raised an exception.", throwable);
                }

                ++k;
                if ((k & 63L) == 0L) {
                    l = ScheduledFutureTask.nanoTime();
                    if (l >= j) {
                        break;
                    }
                }

                runnable = this.pollTask();
                if (runnable == null) {
                    l = ScheduledFutureTask.nanoTime();
                    break;
                }
            }

            this.lastExecutionTime = l;
            return true;
        }
    }

    protected long delayNanos(long i) {
        ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) this.delayedTaskQueue.peek();

        return scheduledfuturetask == null ? SingleThreadEventExecutor.SCHEDULE_PURGE_INTERVAL : scheduledfuturetask.delayNanos(i);
    }

    protected void updateLastExecutionTime() {
        this.lastExecutionTime = ScheduledFutureTask.nanoTime();
    }

    protected abstract void run();

    protected void cleanup() {}

    protected void wakeup(boolean flag) {
        if (!flag || this.state == 3) {
            this.taskQueue.add(SingleThreadEventExecutor.WAKEUP_TASK);
        }

    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public void addShutdownHook(final Runnable runnable) {
        if (this.inEventLoop()) {
            this.shutdownHooks.add(runnable);
        } else {
            this.execute(new Runnable() {
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.add(runnable);
                }
            });
        }

    }

    public void removeShutdownHook(final Runnable runnable) {
        if (this.inEventLoop()) {
            this.shutdownHooks.remove(runnable);
        } else {
            this.execute(new Runnable() {
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.remove(runnable);
                }
            });
        }

    }

    private boolean runShutdownHooks() {
        boolean flag = false;

        while (!this.shutdownHooks.isEmpty()) {
            ArrayList arraylist = new ArrayList(this.shutdownHooks);

            this.shutdownHooks.clear();
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                Runnable runnable = (Runnable) iterator.next();

                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    SingleThreadEventExecutor.logger.warn("Shutdown hook raised an exception.", throwable);
                } finally {
                    flag = true;
                }
            }
        }

        if (flag) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }

        return flag;
    }

    @Override
    public Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit) {
        if (i < 0L) {
            throw new IllegalArgumentException("quietPeriod: " + i + " (expected >= 0)");
        } else if (j < i) {
            throw new IllegalArgumentException("timeout: " + j + " (expected >= quietPeriod (" + i + "))");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (this.isShuttingDown()) {
            return this.terminationFuture();
        } else {
            boolean flag = this.inEventLoop();
            boolean flag1 = true;
            Object object = this.stateLock;

            synchronized (this.stateLock) {
                if (this.isShuttingDown()) {
                    return this.terminationFuture();
                }

                this.gracefulShutdownQuietPeriod = timeunit.toNanos(i);
                this.gracefulShutdownTimeout = timeunit.toNanos(j);
                if (flag) {
                    assert this.state == 2;

                    this.state = 3;
                } else {
                    switch (this.state) {
                        case 1:
                            this.state = 3;
                            this.thread.start();
                            break;
                        case 2:
                            this.state = 3;
                            break;
                        default:
                            flag1 = false;
                    }
                }
            }

            if (flag1) {
                this.wakeup(flag);
            }

            return this.terminationFuture();
        }
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void shutdown() {
        if (!this.isShutdown()) {
            boolean flag = this.inEventLoop();
            boolean flag1 = true;
            Object object = this.stateLock;

            synchronized (this.stateLock) {
                if (this.isShutdown()) {
                    return;
                }

                if (flag) {
                    assert this.state == 2 || this.state == 3;

                    this.state = 4;
                } else {
                    switch (this.state) {
                        case 1:
                            this.state = 4;
                            this.thread.start();
                            break;
                        case 2:
                        case 3:
                            this.state = 4;
                            break;
                        default:
                            flag1 = false;
                    }
                }
            }

            if (flag1) {
                this.wakeup(flag);
            }

        }
    }

    @Override
    public boolean isShuttingDown() {
        return this.state >= 3;
    }

    public boolean isShutdown() {
        return this.state >= 4;
    }

    public boolean isTerminated() {
        return this.state == 5;
    }

    protected boolean confirmShutdown() {
        if (!this.isShuttingDown()) {
            return false;
        } else if (!this.inEventLoop()) {
            throw new IllegalStateException("must be invoked from an event loop");
        } else {
            this.cancelDelayedTasks();
            if (this.gracefulShutdownStartTime == 0L) {
                this.gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
            }

            if (!this.runAllTasks() && !this.runShutdownHooks()) {
                long i = ScheduledFutureTask.nanoTime();

                if (!this.isShutdown() && i - this.gracefulShutdownStartTime <= this.gracefulShutdownTimeout) {
                    if (i - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
                        this.wakeup(true);

                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException interruptedexception) {
                            ;
                        }

                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else if (this.isShutdown()) {
                return true;
            } else {
                this.wakeup(true);
                return false;
            }
        }
    }

    private void cancelDelayedTasks() {
        if (!this.delayedTaskQueue.isEmpty()) {
            ScheduledFutureTask[] ascheduledfuturetask = (ScheduledFutureTask[]) this.delayedTaskQueue.toArray(new ScheduledFutureTask[this.delayedTaskQueue.size()]);
            ScheduledFutureTask[] ascheduledfuturetask1 = ascheduledfuturetask;
            int i = ascheduledfuturetask.length;

            for (int j = 0; j < i; ++j) {
                ScheduledFutureTask scheduledfuturetask = ascheduledfuturetask1[j];

                scheduledfuturetask.cancel(false);
            }

            this.delayedTaskQueue.clear();
        }
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (this.inEventLoop()) {
            throw new IllegalStateException("cannot await termination of the current thread");
        } else {
            if (this.threadLock.tryAcquire(i, timeunit)) {
                this.threadLock.release();
            }

            return this.isTerminated();
        }
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("task");
        } else {
            boolean flag = this.inEventLoop();

            if (flag) {
                this.addTask(runnable);
            } else {
                this.startThread();
                this.addTask(runnable);
                if (this.isShutdown() && this.removeTask(runnable)) {
                    reject();
                }
            }

            if (!this.addTaskWakesUp) {
                this.wakeup(flag);
            }

        }
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
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
                        SingleThreadEventExecutor.this.delayedTaskQueue.add(scheduledfuturetask);
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
                this.delayedTaskQueue.add(new ScheduledFutureTask<>(this, this.delayedTaskQueue, Executors.callable(new SingleThreadEventExecutor.PurgeTask(), (Object) null), ScheduledFutureTask.deadlineNanos(SingleThreadEventExecutor.SCHEDULE_PURGE_INTERVAL), -SingleThreadEventExecutor.SCHEDULE_PURGE_INTERVAL));
                this.thread.start();
            }

        }
    }

    private final class PurgeTask implements Runnable {

        private PurgeTask() {}

        public void run() {
            Iterator iterator = SingleThreadEventExecutor.this.delayedTaskQueue.iterator();

            while (iterator.hasNext()) {
                ScheduledFutureTask scheduledfuturetask = (ScheduledFutureTask) iterator.next();

                if (scheduledfuturetask.isCancelled()) {
                    iterator.remove();
                }
            }

        }
    }
}
