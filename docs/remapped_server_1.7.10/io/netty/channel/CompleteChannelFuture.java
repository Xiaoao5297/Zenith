package io.netty.channel;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

abstract class CompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {

    private final Channel channel;

    protected CompleteChannelFuture(Channel channel, EventExecutor eventexecutor) {
        super(eventexecutor);
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            this.channel = channel;
        }
    }

    @Override
    protected EventExecutor executor() {
        EventExecutor eventexecutor = super.executor();

        return (EventExecutor) (eventexecutor == null ? this.channel().eventLoop() : eventexecutor);
    }

    @Override
    public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.addListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.addListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.removeListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.removeListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelFuture syncUninterruptibly() {
        return this;
    }

    @Override
    public ChannelFuture sync() throws InterruptedException {
        return this;
    }

    @Override
    public ChannelFuture await() throws InterruptedException {
        return this;
    }

    @Override
    public ChannelFuture awaitUninterruptibly() {
        return this;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public Void getNow() {
        return null;
    }
}
