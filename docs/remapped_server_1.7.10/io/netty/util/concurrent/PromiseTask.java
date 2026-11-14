package io.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {

    protected final Callable<V> task;

    static <T> Callable<T> toCallable(Runnable runnable, T t0) {
        return new PromiseTask.RunnableAdapter<>(runnable, t0);
    }

    PromiseTask(EventExecutor eventexecutor, Runnable runnable, V v0) {
        this(eventexecutor, toCallable(runnable, v0));
    }

    PromiseTask(EventExecutor eventexecutor, Callable<V> callable) {
        super(eventexecutor);
        this.task = callable;
    }

    public final int hashCode() {
        return System.identityHashCode(this);
    }

    public final boolean equals(Object object) {
        return this == object;
    }

    public void run() {
        try {
            if (this.setUncancellableInternal()) {
                Object object = this.task.call();

                this.setSuccessInternal(object);
            }
        } catch (Throwable throwable) {
            this.setFailureInternal(throwable);
        }

    }

    @Override
    public final Promise<V> setFailure(Throwable throwable) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setFailureInternal(Throwable throwable) {
        super.setFailure(throwable);
        return this;
    }

    @Override
    public final boolean tryFailure(Throwable throwable) {
        return false;
    }

    protected final boolean tryFailureInternal(Throwable throwable) {
        return super.tryFailure(throwable);
    }

    @Override
    public final Promise<V> setSuccess(V v0) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setSuccessInternal(V v0) {
        super.setSuccess(v0);
        return this;
    }

    @Override
    public final boolean trySuccess(V v0) {
        return false;
    }

    protected final boolean trySuccessInternal(V v0) {
        return super.trySuccess(v0);
    }

    @Override
    public final boolean setUncancellable() {
        throw new IllegalStateException();
    }

    protected final boolean setUncancellableInternal() {
        return super.setUncancellable();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder stringbuilder = super.toStringBuilder();

        stringbuilder.setCharAt(stringbuilder.length() - 1, ',');
        stringbuilder.append(" task: ");
        stringbuilder.append(this.task);
        stringbuilder.append(')');
        return stringbuilder;
    }

    private static final class RunnableAdapter<T> implements Callable<T> {

        final Runnable task;
        final T result;

        RunnableAdapter(Runnable runnable, T t0) {
            this.task = runnable;
            this.result = t0;
        }

        public T call() {
            this.task.run();
            return this.result;
        }

        public String toString() {
            return "Callable(task: " + this.task + ", result: " + this.result + ')';
        }
    }
}
