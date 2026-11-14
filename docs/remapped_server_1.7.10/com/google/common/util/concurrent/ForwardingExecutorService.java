package com.google.common.util.concurrent;

import com.google.common.collect.ForwardingObject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ForwardingExecutorService extends ForwardingObject implements ExecutorService {

    protected ForwardingExecutorService() {}

    @Override
    protected abstract ExecutorService delegate();

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().awaitTermination(i, timeunit);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        return this.delegate().invokeAll(collection);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException {
        return this.delegate().invokeAll(collection, i, timeunit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return this.delegate().invokeAny(collection);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().invokeAny(collection, i, timeunit);
    }

    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }

    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }

    public void shutdown() {
        this.delegate().shutdown();
    }

    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }

    public void execute(Runnable runnable) {
        this.delegate().execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return this.delegate().submit(callable);
    }

    public Future<?> submit(Runnable runnable) {
        return this.delegate().submit(runnable);
    }

    public <T> Future<T> submit(Runnable runnable, T t0) {
        return this.delegate().submit(runnable, t0);
    }
}
