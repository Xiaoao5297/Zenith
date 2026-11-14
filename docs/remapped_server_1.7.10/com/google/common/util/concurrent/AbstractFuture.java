package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import javax.annotation.Nullable;

public abstract class AbstractFuture<V> implements ListenableFuture<V> {

    private final AbstractFuture.Sync<V> sync = new AbstractFuture.Sync<>();
    private final ExecutionList executionList = new ExecutionList();

    protected AbstractFuture() {}

    public V get(long i, TimeUnit timeunit) throws InterruptedException, TimeoutException, ExecutionException {
        return this.sync.get(timeunit.toNanos(i));
    }

    public V get() throws InterruptedException, ExecutionException {
        return this.sync.get();
    }

    public boolean isDone() {
        return this.sync.isDone();
    }

    public boolean isCancelled() {
        return this.sync.isCancelled();
    }

    public boolean cancel(boolean flag) {
        if (!this.sync.cancel(flag)) {
            return false;
        } else {
            this.executionList.execute();
            if (flag) {
                this.interruptTask();
            }

            return true;
        }
    }

    protected void interruptTask() {}

    protected final boolean wasInterrupted() {
        return this.sync.wasInterrupted();
    }

    @Override
    public void addListener(Runnable runnable, Executor executor) {
        this.executionList.add(runnable, executor);
    }

    protected boolean set(@Nullable V v0) {
        boolean flag = this.sync.set(v0);

        if (flag) {
            this.executionList.execute();
        }

        return flag;
    }

    protected boolean setException(Throwable throwable) {
        boolean flag = this.sync.setException((Throwable) Preconditions.checkNotNull(throwable));

        if (flag) {
            this.executionList.execute();
        }

        return flag;
    }

    static final CancellationException cancellationExceptionWithCause(@Nullable String s, @Nullable Throwable throwable) {
        CancellationException cancellationexception = new CancellationException(s);

        cancellationexception.initCause(throwable);
        return cancellationexception;
    }

    static final class Sync<V> extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 0L;
        static final int RUNNING = 0;
        static final int COMPLETING = 1;
        static final int COMPLETED = 2;
        static final int CANCELLED = 4;
        static final int INTERRUPTED = 8;
        private V value;
        private Throwable exception;

        Sync() {}

        protected int tryAcquireShared(int i) {
            return this.isDone() ? 1 : -1;
        }

        protected boolean tryReleaseShared(int i) {
            this.setState(i);
            return true;
        }

        V get(long i) throws TimeoutException, CancellationException, ExecutionException, InterruptedException {
            if (!this.tryAcquireSharedNanos(-1, i)) {
                throw new TimeoutException("Timeout waiting for task.");
            } else {
                return this.getValue();
            }
        }

        V get() throws CancellationException, ExecutionException, InterruptedException {
            this.acquireSharedInterruptibly(-1);
            return this.getValue();
        }

        private V getValue() throws CancellationException, ExecutionException {
            int i = this.getState();

            switch (i) {
                case 2:
                    if (this.exception != null) {
                        throw new ExecutionException(this.exception);
                    }

                    return this.value;
                case 4:
                case 8:
                    throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.exception);
                default:
                    throw new IllegalStateException("Error, synchronizer in invalid state: " + i);
            }
        }

        boolean isDone() {
            return (this.getState() & 14) != 0;
        }

        boolean isCancelled() {
            return (this.getState() & 12) != 0;
        }

        boolean wasInterrupted() {
            return this.getState() == 8;
        }

        boolean set(@Nullable V v0) {
            return this.complete(v0, (Throwable) null, 2);
        }

        boolean setException(Throwable throwable) {
            return this.complete((Object) null, throwable, 2);
        }

        boolean cancel(boolean flag) {
            return this.complete((Object) null, (Throwable) null, flag ? 8 : 4);
        }

        private boolean complete(@Nullable V v0, @Nullable Throwable throwable, int i) {
            boolean flag = this.compareAndSetState(0, 1);

            if (flag) {
                this.value = v0;
                this.exception = (Throwable) ((i & 12) != 0 ? new CancellationException("Future.cancel() was called.") : throwable);
                this.releaseShared(i);
            } else if (this.getState() == 1) {
                this.acquireShared(-1);
            }

            return flag;
        }
    }
}
