package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public abstract class CompleteFuture<V> extends AbstractFuture<V> {

    private final EventExecutor executor;

    protected CompleteFuture(EventExecutor eventexecutor) {
        this.executor = eventexecutor;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    @Override
    public Future<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        if (genericfuturelistener == null) {
            throw new NullPointerException("listener");
        } else {
            DefaultPromise.notifyListener(this.executor(), this, genericfuturelistener);
            return this;
        }
    }

    @Override
    public Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        if (agenericfuturelistener == null) {
            throw new NullPointerException("listeners");
        } else {
            GenericFutureListener[] agenericfuturelistener1 = agenericfuturelistener;
            int i = agenericfuturelistener.length;

            for (int j = 0; j < i; ++j) {
                GenericFutureListener genericfuturelistener = agenericfuturelistener1[j];

                if (genericfuturelistener == null) {
                    break;
                }

                DefaultPromise.notifyListener(this.executor(), this, genericfuturelistener);
            }

            return this;
        }
    }

    @Override
    public Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener) {
        return this;
    }

    @Override
    public Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener) {
        return this;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            return this;
        }
    }

    @Override
    public boolean await(long i, TimeUnit timeunit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            return true;
        }
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        return this;
    }

    @Override
    public boolean await(long i) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            return true;
        }
    }

    @Override
    public Future<V> awaitUninterruptibly() {
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long i, TimeUnit timeunit) {
        return true;
    }

    @Override
    public boolean awaitUninterruptibly(long i) {
        return true;
    }

    public boolean isDone() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean cancel(boolean flag) {
        return false;
    }
}
