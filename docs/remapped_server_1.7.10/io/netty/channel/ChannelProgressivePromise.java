package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;

public interface ChannelProgressivePromise extends ProgressivePromise<Void>, ChannelProgressiveFuture, ChannelPromise {

    @Override
    ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener);

    @Override
    ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener);

    @Override
    ChannelProgressivePromise sync() throws InterruptedException;

    @Override
    ChannelProgressivePromise syncUninterruptibly();

    @Override
    ChannelProgressivePromise await() throws InterruptedException;

    @Override
    ChannelProgressivePromise awaitUninterruptibly();

    @Override
    ChannelProgressivePromise setSuccess(Void ovoid);

    @Override
    ChannelProgressivePromise setSuccess();

    @Override
    ChannelProgressivePromise setFailure(Throwable throwable);

    @Override
    ChannelProgressivePromise setProgress(long i, long j);
}
