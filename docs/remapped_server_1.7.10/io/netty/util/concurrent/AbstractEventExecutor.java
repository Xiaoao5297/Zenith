package io.netty.util.concurrent;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {

    public AbstractEventExecutor() {}

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public boolean inEventLoop() {
        return this.inEventLoop(Thread.currentThread());
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return new AbstractEventExecutor.EventExecutorIterator();
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

    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise<>(this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise<>(this);
    }

    @Override
    public <V> Future<V> newSucceededFuture(V v0) {
        return new SucceededFuture<>(this, v0);
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable throwable) {
        return new FailedFuture<>(this, throwable);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return (Future) super.submit(runnable);
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t0) {
        return (Future) super.submit(runnable, t0);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return (Future) super.submit(callable);
    }

    protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t0) {
        return new PromiseTask<>(this, runnable, t0);
    }

    protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PromiseTask<>(this, callable);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long i, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long i, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long i, long j, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long i, long j, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    private final class EventExecutorIterator implements Iterator<EventExecutor> {

        private boolean nextCalled;

        private EventExecutorIterator() {}

        public boolean hasNext() {
            return !this.nextCalled;
        }

        public EventExecutor next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                this.nextCalled = true;
                return AbstractEventExecutor.this;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("read-only");
        }
    }
}
