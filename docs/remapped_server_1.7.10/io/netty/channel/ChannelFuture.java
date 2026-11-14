package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFuture extends Future<Void> {

    Channel channel();

    @Override
    ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelFuture sync() throws InterruptedException;

    @Override
    ChannelFuture syncUninterruptibly();

    @Override
    ChannelFuture await() throws InterruptedException;

    @Override
    ChannelFuture awaitUninterruptibly();
}
