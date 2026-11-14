package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashedWheelTimer implements Timer {

    static final InternalLogger logger = InternalLoggerFactory.getInstance(HashedWheelTimer.class);
    private static final ResourceLeakDetector<HashedWheelTimer> leakDetector = new ResourceLeakDetector<>(HashedWheelTimer.class, 1, (long) (Runtime.getRuntime().availableProcessors() * 4));
    private final ResourceLeak leak;
    private final HashedWheelTimer.Worker worker;
    final Thread workerThread;
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    final AtomicInteger workerState;
    final long tickDuration;
    final Set<HashedWheelTimer.HashedWheelTimeout>[] wheel;
    final int mask;
    final ReadWriteLock lock;
    volatile int wheelCursor;

    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long i, TimeUnit timeunit) {
        this(Executors.defaultThreadFactory(), i, timeunit);
    }

    public HashedWheelTimer(long i, TimeUnit timeunit, int j) {
        this(Executors.defaultThreadFactory(), i, timeunit, j);
    }

    public HashedWheelTimer(ThreadFactory threadfactory) {
        this(threadfactory, 100L, TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(ThreadFactory threadfactory, long i, TimeUnit timeunit) {
        this(threadfactory, i, timeunit, 512);
    }

    public HashedWheelTimer(ThreadFactory threadfactory, long i, TimeUnit timeunit, int j) {
        this.worker = new HashedWheelTimer.Worker();
        this.workerState = new AtomicInteger();
        this.lock = new ReentrantReadWriteLock();
        if (threadfactory == null) {
            throw new NullPointerException("threadFactory");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else if (i <= 0L) {
            throw new IllegalArgumentException("tickDuration must be greater than 0: " + i);
        } else if (j <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + j);
        } else {
            this.wheel = createWheel(j);
            this.mask = this.wheel.length - 1;
            this.tickDuration = timeunit.toNanos(i);
            if (this.tickDuration >= Long.MAX_VALUE / (long) this.wheel.length) {
                throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", i, Long.MAX_VALUE / (long) this.wheel.length));
            } else {
                this.workerThread = threadfactory.newThread(this.worker);
                this.leak = HashedWheelTimer.leakDetector.open(this);
            }
        }
    }

    private static Set<HashedWheelTimer.HashedWheelTimeout>[] createWheel(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + i);
        } else if (i > 1073741824) {
            throw new IllegalArgumentException("ticksPerWheel may not be greater than 2^30: " + i);
        } else {
            i = normalizeTicksPerWheel(i);
            Set[] aset = new Set[i];

            for (int j = 0; j < aset.length; ++j) {
                aset[j] = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
            }

            return aset;
        }
    }

    private static int normalizeTicksPerWheel(int i) {
        int j;

        for (j = 1; j < i; j <<= 1) {
            ;
        }

        return j;
    }

    public void start() {
        switch (this.workerState.get()) {
            case 0:
                if (this.workerState.compareAndSet(0, 1)) {
                    this.workerThread.start();
                }
            case 1:
                return;
            case 2:
                throw new IllegalStateException("cannot be started once stopped");
            default:
                throw new Error("Invalid WorkerState");
        }
    }

    @Override
    public Set<Timeout> stop() {
        if (Thread.currentThread() == this.workerThread) {
            throw new IllegalStateException(HashedWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
        } else if (!this.workerState.compareAndSet(1, 2)) {
            this.workerState.set(2);
            if (this.leak != null) {
                this.leak.close();
            }

            return Collections.emptySet();
        } else {
            boolean flag = false;

            while (this.workerThread.isAlive()) {
                this.workerThread.interrupt();

                try {
                    this.workerThread.join(100L);
                } catch (InterruptedException interruptedexception) {
                    flag = true;
                }
            }

            if (flag) {
                Thread.currentThread().interrupt();
            }

            if (this.leak != null) {
                this.leak.close();
            }

            HashSet hashset = new HashSet();
            Set[] aset = this.wheel;
            int i = aset.length;

            for (int j = 0; j < i; ++j) {
                Set set = aset[j];

                hashset.addAll(set);
                set.clear();
            }

            return Collections.unmodifiableSet(hashset);
        }
    }

    @Override
    public Timeout newTimeout(TimerTask timertask, long i, TimeUnit timeunit) {
        long j = System.nanoTime();

        if (timertask == null) {
            throw new NullPointerException("task");
        } else if (timeunit == null) {
            throw new NullPointerException("unit");
        } else {
            this.start();
            long k = timeunit.toNanos(i);
            HashedWheelTimer.HashedWheelTimeout hashedwheeltimer_hashedwheeltimeout = new HashedWheelTimer.HashedWheelTimeout(timertask, j + k);

            this.scheduleTimeout(hashedwheeltimer_hashedwheeltimeout, k);
            return hashedwheeltimer_hashedwheeltimeout;
        }
    }

    void scheduleTimeout(HashedWheelTimer.HashedWheelTimeout hashedwheeltimer_hashedwheeltimeout, long i) {
        long j = (i + this.tickDuration - 1L) / this.tickDuration;

        if (j < 0L) {
            j = i / this.tickDuration;
        }

        if (j == 0L) {
            j = 1L;
        }

        if ((j & (long) this.mask) == 0L) {
            --j;
        }

        long k = j / (long) this.wheel.length;

        this.lock.readLock().lock();

        try {
            if (this.workerState.get() == 2) {
                throw new IllegalStateException("Cannot enqueue after shutdown");
            }

            int l = (int) ((long) this.wheelCursor + j & (long) this.mask);

            hashedwheeltimer_hashedwheeltimeout.stopIndex = l;
            hashedwheeltimer_hashedwheeltimeout.remainingRounds = k;
            this.wheel[l].add(hashedwheeltimer_hashedwheeltimeout);
        } finally {
            this.lock.readLock().unlock();
        }

    }

    private final class HashedWheelTimeout implements Timeout {

        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        private final TimerTask task;
        final long deadline;
        volatile int stopIndex;
        volatile long remainingRounds;
        private final AtomicInteger state = new AtomicInteger(0);

        HashedWheelTimeout(TimerTask timertask, long i) {
            this.task = timertask;
            this.deadline = i;
        }

        @Override
        public Timer timer() {
            return HashedWheelTimer.this;
        }

        @Override
        public TimerTask task() {
            return this.task;
        }

        @Override
        public boolean cancel() {
            if (!this.state.compareAndSet(0, 1)) {
                return false;
            } else {
                HashedWheelTimer.this.wheel[this.stopIndex].remove(this);
                return true;
            }
        }

        @Override
        public boolean isCancelled() {
            return this.state.get() == 1;
        }

        @Override
        public boolean isExpired() {
            return this.state.get() != 0;
        }

        public void expire() {
            if (this.state.compareAndSet(0, 2)) {
                try {
                    this.task.run(this);
                } catch (Throwable throwable) {
                    if (HashedWheelTimer.logger.isWarnEnabled()) {
                        HashedWheelTimer.logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', throwable);
                    }
                }

            }
        }

        public String toString() {
            long i = System.nanoTime();
            long j = this.deadline - i;
            StringBuilder stringbuilder = new StringBuilder(192);

            stringbuilder.append(this.getClass().getSimpleName());
            stringbuilder.append('(');
            stringbuilder.append("deadline: ");
            if (j > 0L) {
                stringbuilder.append(j);
                stringbuilder.append(" ms later, ");
            } else if (j < 0L) {
                stringbuilder.append(-j);
                stringbuilder.append(" ms ago, ");
            } else {
                stringbuilder.append("now, ");
            }

            if (this.isCancelled()) {
                stringbuilder.append(", cancelled");
            }

            return stringbuilder.append(')').toString();
        }
    }

    private final class Worker implements Runnable {

        private long startTime;
        private long tick;

        Worker() {}

        public void run() {
            ArrayList arraylist = new ArrayList();

            this.startTime = System.nanoTime();
            this.tick = 1L;

            while (HashedWheelTimer.this.workerState.get() == 1) {
                long i = this.waitForNextTick();

                if (i > 0L) {
                    this.fetchExpiredTimeouts(arraylist, i);
                    this.notifyExpiredTimeouts(arraylist);
                }
            }

        }

        private void fetchExpiredTimeouts(List<HashedWheelTimer.HashedWheelTimeout> list, long i) {
            HashedWheelTimer.this.lock.writeLock().lock();

            try {
                int j = HashedWheelTimer.this.wheelCursor = HashedWheelTimer.this.wheelCursor + 1 & HashedWheelTimer.this.mask;

                this.fetchExpiredTimeouts(list, HashedWheelTimer.this.wheel[j].iterator(), i);
            } finally {
                HashedWheelTimer.this.lock.writeLock().unlock();
            }

        }

        private void fetchExpiredTimeouts(List<HashedWheelTimer.HashedWheelTimeout> list, Iterator<HashedWheelTimer.HashedWheelTimeout> iterator, long i) {
            ArrayList arraylist = null;

            while (iterator.hasNext()) {
                HashedWheelTimer.HashedWheelTimeout hashedwheeltimer_hashedwheeltimeout = (HashedWheelTimer.HashedWheelTimeout) iterator.next();

                if (hashedwheeltimer_hashedwheeltimeout.remainingRounds <= 0L) {
                    iterator.remove();
                    if (hashedwheeltimer_hashedwheeltimeout.deadline <= i) {
                        list.add(hashedwheeltimer_hashedwheeltimeout);
                    } else {
                        if (arraylist == null) {
                            arraylist = new ArrayList();
                        }

                        arraylist.add(hashedwheeltimer_hashedwheeltimeout);
                    }
                } else {
                    --hashedwheeltimer_hashedwheeltimeout.remainingRounds;
                }
            }

            if (arraylist != null) {
                Iterator iterator1 = arraylist.iterator();

                while (iterator1.hasNext()) {
                    HashedWheelTimer.HashedWheelTimeout hashedwheeltimer_hashedwheeltimeout1 = (HashedWheelTimer.HashedWheelTimeout) iterator1.next();

                    HashedWheelTimer.this.scheduleTimeout(hashedwheeltimer_hashedwheeltimeout1, hashedwheeltimer_hashedwheeltimeout1.deadline - i);
                }
            }

        }

        private void notifyExpiredTimeouts(List<HashedWheelTimer.HashedWheelTimeout> list) {
            for (int i = list.size() - 1; i >= 0; --i) {
                ((HashedWheelTimer.HashedWheelTimeout) list.get(i)).expire();
            }

            list.clear();
        }

        private long waitForNextTick() {
            long i = this.startTime + HashedWheelTimer.this.tickDuration * this.tick;

            while (true) {
                long j = System.nanoTime();
                long k = (i - j + 999999L) / 1000000L;

                if (k <= 0L) {
                    ++this.tick;
                    if (j == Long.MIN_VALUE) {
                        return -9223372036854775807L;
                    }

                    return j;
                }

                if (PlatformDependent.isWindows()) {
                    k = k / 10L * 10L;
                }

                try {
                    Thread.sleep(k);
                } catch (InterruptedException interruptedexception) {
                    if (HashedWheelTimer.this.workerState.get() == 2) {
                        return Long.MIN_VALUE;
                    }
                }
            }
        }
    }
}
