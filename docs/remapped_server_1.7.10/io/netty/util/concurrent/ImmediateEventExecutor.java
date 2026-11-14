package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor extends AbstractEventExecutor {

    public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
    private final Future<?> terminationFuture;

    private ImmediateEventExecutor() {
        this.terminationFuture = new FailedFuture<>(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());
    }

    @Override
    public EventExecutorGroup parent() {
        return null;
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
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
    public void shutdown() {}

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
            throw new NullPointerException("command");
        } else {
            runnable.run();
        }
    }

    @Override
    public <V> Promise<V> newPromise() {
        return new ImmediateEventExecutor.ImmediatePromise<>(this);
    }

    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new ImmediateEventExecutor.ImmediateProgressivePromise<>(this);
    }

    static class ImmediateProgressivePromise<V> extends DefaultProgressivePromise<V> {

        ImmediateProgressivePromise(EventExecutor eventexecutor) {
            super(eventexecutor);
        }

        @Override
        protected void checkDeadLock() {}
    }

    static class ImmediatePromise<V> extends DefaultPromise<V> {

        ImmediatePromise(EventExecutor eventexecutor) {
            super(eventexecutor);
        }

        @Override
        protected void checkDeadLock() {}
    }
}
