package io.netty.channel;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelPromise, ChannelFlushPromiseNotifier.FlushCheckpoint {

    private final Channel channel;
    private long checkpoint;

    public DefaultChannelPromise(Channel channel) {
        this.channel = channel;
    }

    public DefaultChannelPromise(Channel channel, EventExecutor eventexecutor) {
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
    public ChannelPromise setSuccess() {
        return this.setSuccess((Void) null);
    }

    @Override
    public ChannelPromise setSuccess(Void ovoid) {
        super.setSuccess(ovoid);
        return this;
    }

    @Override
    public boolean trySuccess() {
        return this.trySuccess((Object) null);
    }

    @Override
    public ChannelPromise setFailure(Throwable throwable) {
        super.setFailure(throwable);
        return this;
    }

    @Override
    public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.addListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.addListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.removeListener(genericfuturelistener);
        return this;
    }

    @Override
    public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.removeListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public ChannelPromise sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelPromise syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ChannelPromise await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ChannelPromise awaitUninterruptibly() {
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
    public ChannelPromise promise() {
        return this;
    }

    @Override
    protected void checkDeadLock() {
        if (this.channel().isRegistered()) {
            super.checkDeadLock();
        }

    }
}
