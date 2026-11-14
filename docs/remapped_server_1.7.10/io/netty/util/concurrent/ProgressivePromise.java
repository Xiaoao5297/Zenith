package io.netty.util.concurrent;

public interface ProgressivePromise<V> extends Promise<V>, ProgressiveFuture<V> {

    ProgressivePromise<V> setProgress(long i, long j);

    boolean tryProgress(long i, long j);

    @Override
    ProgressivePromise<V> setSuccess(V v0);

    @Override
    ProgressivePromise<V> setFailure(Throwable throwable);

    @Override
    ProgressivePromise<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    ProgressivePromise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    ProgressivePromise<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    ProgressivePromise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    ProgressivePromise<V> await() throws InterruptedException;

    @Override
    ProgressivePromise<V> awaitUninterruptibly();

    @Override
    ProgressivePromise<V> sync() throws InterruptedException;

    @Override
    ProgressivePromise<V> syncUninterruptibly();
}
