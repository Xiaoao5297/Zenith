package io.netty.util.concurrent;

public interface Promise<V> extends Future<V> {

    Promise<V> setSuccess(V v0);

    boolean trySuccess(V v0);

    Promise<V> setFailure(Throwable throwable);

    boolean tryFailure(Throwable throwable);

    boolean setUncancellable();

    @Override
    Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    @Override
    Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    @Override
    Promise<V> await() throws InterruptedException;

    @Override
    Promise<V> awaitUninterruptibly();

    @Override
    Promise<V> sync() throws InterruptedException;

    @Override
    Promise<V> syncUninterruptibly();
}
