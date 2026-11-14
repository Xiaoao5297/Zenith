package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;

@Beta
public final class Monitor {

    private final boolean fair;
    private final ReentrantLock lock;
    @GuardedBy("lock")
    private Monitor.Guard activeGuards;

    public Monitor() {
        this(false);
    }

    public Monitor(boolean flag) {
        this.activeGuards = null;
        this.fair = flag;
        this.lock = new ReentrantLock(flag);
    }

    public void enter() {
        this.lock.lock();
    }

    public void enterInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    public boolean enter(long i, TimeUnit timeunit) {
        long j = timeunit.toNanos(i);
        ReentrantLock reentrantlock = this.lock;

        if (!this.fair && reentrantlock.tryLock()) {
            return true;
        } else {
            long k = System.nanoTime() + j;
            boolean flag = Thread.interrupted();

            try {
                while (true) {
                    try {
                        boolean flag1 = reentrantlock.tryLock(j, TimeUnit.NANOSECONDS);

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
    }

    public boolean enterInterruptibly(long i, TimeUnit timeunit) throws InterruptedException {
        return this.lock.tryLock(i, timeunit);
    }

    public boolean tryEnter() {
        return this.lock.tryLock();
    }

    public void enterWhen(Monitor.Guard monitor_guard) throws InterruptedException {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;
            boolean flag = reentrantlock.isHeldByCurrentThread();

            reentrantlock.lockInterruptibly();
            boolean flag1 = false;

            try {
                if (!monitor_guard.isSatisfied()) {
                    this.await(monitor_guard, flag);
                }

                flag1 = true;
            } finally {
                if (!flag1) {
                    this.leave();
                }

            }

        }
    }

    public void enterWhenUninterruptibly(Monitor.Guard monitor_guard) {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;
            boolean flag = reentrantlock.isHeldByCurrentThread();

            reentrantlock.lock();
            boolean flag1 = false;

            try {
                if (!monitor_guard.isSatisfied()) {
                    this.awaitUninterruptibly(monitor_guard, flag);
                }

                flag1 = true;
            } finally {
                if (!flag1) {
                    this.leave();
                }

            }

        }
    }

    public boolean enterWhen(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) throws InterruptedException {
        long j = timeunit.toNanos(i);

        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;
            boolean flag = reentrantlock.isHeldByCurrentThread();

            if (this.fair || !reentrantlock.tryLock()) {
                long k = System.nanoTime() + j;

                if (!reentrantlock.tryLock(i, timeunit)) {
                    return false;
                }

                j = k - System.nanoTime();
            }

            boolean flag1 = false;
            boolean flag2 = true;

            boolean flag3;

            try {
                flag1 = monitor_guard.isSatisfied() || this.awaitNanos(monitor_guard, j, flag);
                flag2 = false;
                flag3 = flag1;
            } finally {
                if (!flag1) {
                    try {
                        if (flag2 && !flag) {
                            this.signalNextWaiter();
                        }
                    } finally {
                        reentrantlock.unlock();
                    }
                }

            }

            return flag3;
        }
    }

    public boolean enterWhenUninterruptibly(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) {
        long j = timeunit.toNanos(i);

        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;
            long k = System.nanoTime() + j;
            boolean flag = reentrantlock.isHeldByCurrentThread();
            boolean flag1 = Thread.interrupted();

            try {
                boolean flag2;
                InterruptedException interruptedexception;

                if (this.fair || !reentrantlock.tryLock()) {
                    flag2 = false;

                    do {
                        try {
                            flag2 = reentrantlock.tryLock(j, TimeUnit.NANOSECONDS);
                            if (!flag2) {
                                boolean flag3 = false;

                                return flag3;
                            }
                        } catch (InterruptedException interruptedexception1) {
                            interruptedexception = interruptedexception1;
                            flag1 = true;
                        }

                        j = k - System.nanoTime();
                    } while (!flag2);
                }

                flag2 = false;

                try {
                    while (true) {
                        try {
                            interruptedexception = flag2 = monitor_guard.isSatisfied() || this.awaitNanos(monitor_guard, j, flag);
                            return (boolean) interruptedexception;
                        } catch (InterruptedException interruptedexception2) {
                            flag1 = true;
                            flag = false;
                            j = k - System.nanoTime();
                        }
                    }
                } finally {
                    if (!flag2) {
                        reentrantlock.unlock();
                    }

                }
            } finally {
                if (flag1) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }

    public boolean enterIf(Monitor.Guard monitor_guard) {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;

            reentrantlock.lock();
            boolean flag = false;

            boolean flag1;

            try {
                flag1 = flag = monitor_guard.isSatisfied();
            } finally {
                if (!flag) {
                    reentrantlock.unlock();
                }

            }

            return flag1;
        }
    }

    public boolean enterIfInterruptibly(Monitor.Guard monitor_guard) throws InterruptedException {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;

            reentrantlock.lockInterruptibly();
            boolean flag = false;

            boolean flag1;

            try {
                flag1 = flag = monitor_guard.isSatisfied();
            } finally {
                if (!flag) {
                    reentrantlock.unlock();
                }

            }

            return flag1;
        }
    }

    public boolean enterIf(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else if (!this.enter(i, timeunit)) {
            return false;
        } else {
            boolean flag = false;

            boolean flag1;

            try {
                flag1 = flag = monitor_guard.isSatisfied();
            } finally {
                if (!flag) {
                    this.lock.unlock();
                }

            }

            return flag1;
        }
    }

    public boolean enterIfInterruptibly(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) throws InterruptedException {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;

            if (!reentrantlock.tryLock(i, timeunit)) {
                return false;
            } else {
                boolean flag = false;

                boolean flag1;

                try {
                    flag1 = flag = monitor_guard.isSatisfied();
                } finally {
                    if (!flag) {
                        reentrantlock.unlock();
                    }

                }

                return flag1;
            }
        }
    }

    public boolean tryEnterIf(Monitor.Guard monitor_guard) {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            ReentrantLock reentrantlock = this.lock;

            if (!reentrantlock.tryLock()) {
                return false;
            } else {
                boolean flag = false;

                boolean flag1;

                try {
                    flag1 = flag = monitor_guard.isSatisfied();
                } finally {
                    if (!flag) {
                        reentrantlock.unlock();
                    }

                }

                return flag1;
            }
        }
    }

    public void waitFor(Monitor.Guard monitor_guard) throws InterruptedException {
        if (!(monitor_guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        } else {
            if (!monitor_guard.isSatisfied()) {
                this.await(monitor_guard, true);
            }

        }
    }

    public void waitForUninterruptibly(Monitor.Guard monitor_guard) {
        if (!(monitor_guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        } else {
            if (!monitor_guard.isSatisfied()) {
                this.awaitUninterruptibly(monitor_guard, true);
            }

        }
    }

    public boolean waitFor(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) throws InterruptedException {
        long j = timeunit.toNanos(i);

        if (!(monitor_guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        } else {
            return monitor_guard.isSatisfied() || this.awaitNanos(monitor_guard, j, true);
        }
    }

    public boolean waitForUninterruptibly(Monitor.Guard monitor_guard, long i, TimeUnit timeunit) {
        long j = timeunit.toNanos(i);

        if (!(monitor_guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        } else if (monitor_guard.isSatisfied()) {
            return true;
        } else {
            boolean flag = true;
            long k = System.nanoTime() + j;
            boolean flag1 = Thread.interrupted();

            try {
                while (true) {
                    try {
                        boolean flag2 = this.awaitNanos(monitor_guard, j, flag);

                        return flag2;
                    } catch (InterruptedException interruptedexception) {
                        flag1 = true;
                        if (!monitor_guard.isSatisfied()) {
                            flag = false;
                            j = k - System.nanoTime();
                        } else {
                            boolean flag3 = true;

                            return flag3;
                        }
                    }
                }
            } finally {
                if (flag1) {
                    Thread.currentThread().interrupt();
                }

            }
        }
    }

    public void leave() {
        ReentrantLock reentrantlock = this.lock;

        try {
            if (reentrantlock.getHoldCount() == 1) {
                this.signalNextWaiter();
            }
        } finally {
            reentrantlock.unlock();
        }

    }

    public boolean isFair() {
        return this.fair;
    }

    public boolean isOccupied() {
        return this.lock.isLocked();
    }

    public boolean isOccupiedByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    public int getOccupiedDepth() {
        return this.lock.getHoldCount();
    }

    public int getQueueLength() {
        return this.lock.getQueueLength();
    }

    public boolean hasQueuedThreads() {
        return this.lock.hasQueuedThreads();
    }

    public boolean hasQueuedThread(Thread thread) {
        return this.lock.hasQueuedThread(thread);
    }

    public boolean hasWaiters(Monitor.Guard monitor_guard) {
        return this.getWaitQueueLength(monitor_guard) > 0;
    }

    public int getWaitQueueLength(Monitor.Guard monitor_guard) {
        if (monitor_guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else {
            this.lock.lock();

            int i;

            try {
                i = monitor_guard.waiterCount;
            } finally {
                this.lock.unlock();
            }

            return i;
        }
    }

    @GuardedBy("lock")
    private void signalNextWaiter() {
        for (Monitor.Guard monitor_guard = this.activeGuards; monitor_guard != null; monitor_guard = monitor_guard.next) {
            if (this.isSatisfied(monitor_guard)) {
                monitor_guard.condition.signal();
                break;
            }
        }

    }

    @GuardedBy("lock")
    private boolean isSatisfied(Monitor.Guard monitor_guard) {
        try {
            return monitor_guard.isSatisfied();
        } catch (Throwable throwable) {
            this.signalAllWaiters();
            throw Throwables.propagate(throwable);
        }
    }

    @GuardedBy("lock")
    private void signalAllWaiters() {
        for (Monitor.Guard monitor_guard = this.activeGuards; monitor_guard != null; monitor_guard = monitor_guard.next) {
            monitor_guard.condition.signalAll();
        }

    }

    @GuardedBy("lock")
    private void beginWaitingFor(Monitor.Guard monitor_guard) {
        int i = monitor_guard.waiterCount++;

        if (i == 0) {
            monitor_guard.next = this.activeGuards;
            this.activeGuards = monitor_guard;
        }

    }

    @GuardedBy("lock")
    private void endWaitingFor(Monitor.Guard monitor_guard) {
        int i = --monitor_guard.waiterCount;

        if (i == 0) {
            Monitor.Guard monitor_guard1 = this.activeGuards;

            Monitor.Guard monitor_guard2;

            for (monitor_guard2 = null; monitor_guard1 != monitor_guard; monitor_guard1 = monitor_guard1.next) {
                monitor_guard2 = monitor_guard1;
            }

            if (monitor_guard2 == null) {
                this.activeGuards = monitor_guard1.next;
            } else {
                monitor_guard2.next = monitor_guard1.next;
            }

            monitor_guard1.next = null;
        }

    }

    @GuardedBy("lock")
    private void await(Monitor.Guard monitor_guard, boolean flag) throws InterruptedException {
        if (flag) {
            this.signalNextWaiter();
        }

        this.beginWaitingFor(monitor_guard);

        try {
            do {
                monitor_guard.condition.await();
            } while (!monitor_guard.isSatisfied());
        } finally {
            this.endWaitingFor(monitor_guard);
        }

    }

    @GuardedBy("lock")
    private void awaitUninterruptibly(Monitor.Guard monitor_guard, boolean flag) {
        if (flag) {
            this.signalNextWaiter();
        }

        this.beginWaitingFor(monitor_guard);

        try {
            do {
                monitor_guard.condition.awaitUninterruptibly();
            } while (!monitor_guard.isSatisfied());
        } finally {
            this.endWaitingFor(monitor_guard);
        }

    }

    @GuardedBy("lock")
    private boolean awaitNanos(Monitor.Guard monitor_guard, long i, boolean flag) throws InterruptedException {
        if (flag) {
            this.signalNextWaiter();
        }

        this.beginWaitingFor(monitor_guard);

        try {
            boolean flag1;

            do {
                if (i < 0L) {
                    flag1 = false;
                    return flag1;
                }

                i = monitor_guard.condition.awaitNanos(i);
            } while (!monitor_guard.isSatisfied());

            flag1 = true;
            return flag1;
        } finally {
            this.endWaitingFor(monitor_guard);
        }
    }

    @Beta
    public abstract static class Guard {

        final Monitor monitor;
        final Condition condition;
        @GuardedBy("monitor.lock")
        int waiterCount = 0;
        @GuardedBy("monitor.lock")
        Monitor.Guard next;

        protected Guard(Monitor monitor) {
            this.monitor = (Monitor) Preconditions.checkNotNull(monitor, "monitor");
            this.condition = monitor.lock.newCondition();
        }

        public abstract boolean isSatisfied();
    }
}
