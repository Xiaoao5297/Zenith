package io.netty.util.concurrent;

public class DefaultProgressivePromise<V> extends DefaultPromise<V> implements ProgressivePromise<V> {

    public DefaultProgressivePromise(EventExecutor eventexecutor) {
        super(eventexecutor);
    }

    protected DefaultProgressivePromise() {}

    @Override
    public ProgressivePromise<V> setProgress(long i, long j) {
        if (j < 0L) {
            j = -1L;
            if (i < 0L) {
                throw new IllegalArgumentException("progress: " + i + " (expected: >= 0)");
            }
        } else if (i < 0L || i > j) {
            throw new IllegalArgumentException("progress: " + i + " (expected: 0 <= progress <= total (" + j + "))");
        }

        if (this.isDone()) {
            throw new IllegalStateException("complete already");
        } else {
            this.notifyProgressiveListeners(i, j);
            return this;
        }
    }

    @Override
    public boolean tryProgress(long i, long j) {
        if (j < 0L) {
            j = -1L;
            if (i < 0L || this.isDone()) {
                return false;
            }
        } else if (i < 0L || i > j || this.isDone()) {
            return false;
        }

        this.notifyProgressiveListeners(i, j);
        return true;
    }

    @Override
    public ProgressivePromise<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        super.addListener(genericfuturelistener);
        return this;
    }

    @Override
    public ProgressivePromise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        super.addListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ProgressivePromise<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        super.removeListener(genericfuturelistener);
        return this;
    }

    @Override
    public ProgressivePromise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        super.removeListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ProgressivePromise<V> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ProgressivePromise<V> syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ProgressivePromise<V> await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ProgressivePromise<V> awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public ProgressivePromise<V> setSuccess(V v0) {
        super.setSuccess(v0);
        return this;
    }

    @Override
    public ProgressivePromise<V> setFailure(Throwable throwable) {
        super.setFailure(throwable);
        return this;
    }
}
