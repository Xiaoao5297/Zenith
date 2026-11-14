package io.netty.channel;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelProgressivePromise extends DefaultProgressivePromise<Void> implements ChannelProgressivePromise, ChannelFlushPromiseNotifier.FlushCheckpoint {

    private final Channel channel;
    private long checkpoint;

    public DefaultChannelProgressivePromise(Channel channel) {
        this.channel = channel;
    }

    public DefaultChannelProgressivePromise(Channel channel, EventExecutor eventexecutor) {
        super(eventexecutor);
        this.channel = channel;
    }

    @Override
    protected EventExecutor executor() {
        EventExecutor eventexecutor = super.executor();

        return (EventExecutor) (eventexecutor == null ? this.channel().eventLoop() : eventexecutor);
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ChannelProgressivePromise setSuccess() {
        return this.setSuccess((Void) null);
    }

    @Override
    public ChannelProgressivePromise setSuccess(Void ovoid) {
        super.setSuccess(ovoid);
        return this;
    }

    @Override
    public boolean trySuccess() {
        return this.trySuccess((Object) null);
    }

    @Override
    public ChannelProgressivePromise setFailure(Throwable throwable) {
        super.setFailure(throwable);
        return this;
    }

    @Override
    public ChannelProgressivePromise setProgress(long i, long j) {
        super.setProgress(i, j);
        return this;
    }

    @Override
    public ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.addListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.addListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.removeListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.removeListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelProgressivePromise sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelProgressivePromise syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ChannelProgressivePromise await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ChannelProgressivePromise awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public long flushCheckpoint() {
        return this.checkpoint;
    }

    @Override
    public void flushCheckpoint(long i) {
        this.checkpoint = i;
    }

    @Override
    public ChannelProgressivePromise promise() {
        return this;
    }

    @Override
    protected void checkDeadLock() {
        if (this.channel().isRegistered()) {
            super.checkDeadLock();
        }

    }
}
