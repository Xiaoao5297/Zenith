package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture extends ChannelFuture, ProgressiveFuture<Void> {

    @Override
    ChannelProgressiveFuture addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelProgressiveFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelProgressiveFuture removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelProgressiveFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelProgressiveFuture sync() throws InterruptedException;

    @Override
    ChannelProgressiveFuture syncUninterruptibly();

    @Override
    ChannelProgressiveFuture await() throws InterruptedException;

    @Override
    ChannelProgressiveFuture awaitUninterruptibly();
}
