package io.netty.util.concurrent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractEventExecutorGroup implements EventExecutorGroup {

    public AbstractEventExecutorGroup() {}

    @Override
    public Future<?> submit(Runnable runnable) {
        return this.next().submit(runnable);
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t0) {
        return this.next().submit(runnable, t0);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return this.next().submit(callable);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit) {
        return this.next().schedule(runnable, i, timeunit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit) {
        return this.next().schedule(callable, i, timeunit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit) {
        return this.next().scheduleAtFixedRate(runnable, i, j, timeunit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit) {
        return this.next().scheduleWithFixedDelay(runnable, i, j, timeunit);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public abstract void shutdown();

    /** @deprecated */
    @Deprecated
    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return Collections.emptyList();
    }

    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        return this.next().invokeAll(collection);
    }

    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException {
        return this.next().invokeAll(collection, i, timeunit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        return this.next().invokeAny(collection);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.next().invokeAny(collection, i, timeunit);
    }

    public void execute(Runnable runnable) {
        this.next().execute(runnable);
    }
}
