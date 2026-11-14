package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isSuccess();

    boolean isCancellable();

    Throwable cause();

    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> genericfuturelistener);

    Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... agenericfuturelistener);

    Future<V> sync() throws InterruptedException;

    Future<V> syncUninterruptibly();

    Future<V> await() throws InterruptedException;

    Future<V> awaitUninterruptibly();

    boolean await(long i, TimeUnit timeunit) throws InterruptedException;

    boolean await(long i) throws InterruptedException;

    boolean awaitUninterruptibly(long i, TimeUnit timeunit);

    boolean awaitUninterruptibly(long i);

    V getNow();

    boolean cancel(boolean flag);
}
