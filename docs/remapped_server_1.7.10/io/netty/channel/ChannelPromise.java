package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public interface ChannelPromise extends ChannelFuture, Promise<Void> {

    @Override
    Channel channel();

    ChannelPromise setSuccess(Void ovoid);

    ChannelPromise setSuccess();

    boolean trySuccess();

    @Override
    ChannelPromise setFailure(Throwable throwable);

    @Override
    ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelPromise sync() throws InterruptedException;

    @Override
    ChannelPromise syncUninterruptibly();

    @Override
    ChannelPromise await() throws InterruptedException;

    @Override
    ChannelPromise awaitUninterruptibly();
}
