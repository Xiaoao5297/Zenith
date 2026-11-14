package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract class WrappingExecutorService implements ExecutorService {

    private final ExecutorService delegate;

    protected WrappingExecutorService(ExecutorService executorservice) {
        this.delegate = (ExecutorService) Preconditions.checkNotNull(executorservice);
    }

    protected abstract <T> Callable<T> wrapTask(Callable<T> callable);

    protected Runnable wrapTask(Runnable runnable) {
        final Callable callable = this.wrapTask(Executors.callable(runnable, (Object) null));

        return new Runnable() {
            public void run() {
                try {
                    callable.call();
                } catch (Exception exception) {
                    Throwables.propagate(exception);
                }

            }
        };
    }

    private final <T> ImmutableList<Callable<T>> wrapTasks(Collection<? extends Callable<T>> collection) {
        ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Callable callable = (Callable) iterator.next();

            immutablelist_builder.add((Object) this.wrapTask(callable));
        }

        return immutablelist_builder.build();
    }

    public final void execute(Runnable runnable) {
        this.delegate.execute(this.wrapTask(runnable));
    }

    public final <T> Future<T> submit(Callable<T> callable) {
        return this.delegate.submit(this.wrapTask((Callable) Preconditions.checkNotNull(callable)));
    }

    public final Future<?> submit(Runnable runnable) {
        return this.delegate.submit(this.wrapTask(runnable));
    }

    public final <T> Future<T> submit(Runnable runnable, T t0) {
        return this.delegate.submit(this.wrapTask(runnable), t0);
    }

    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapTasks(collection));
    }

    public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate.invokeAll(this.wrapTasks(collection), i, timeunit);
    }

    public final <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(this.wrapTasks(collection));
    }

    public final <T> T invokeAny(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(this.wrapTasks(collection), i, timeunit);
    }

    public final void shutdown() {
        this.delegate.shutdown();
    }

    public final List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    public final boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    public final boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    public final boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate.awaitTermination(i, timeunit);
    }
}
