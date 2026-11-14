package io.netty.util.concurrent;

public interface ProgressiveFuture<V> extends Future<V> {

    @Override
    ProgressiveFuture<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    ProgressiveFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    ProgressiveFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    ProgressiveFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    ProgressiveFuture<V> sync() throws InterruptedException;

    @Override
    ProgressiveFuture<V> syncUninterruptibly();

    @Override
    ProgressiveFuture<V> await() throws InterruptedException;

    @Override
    ProgressiveFuture<V> awaitUninterruptibly();
}
