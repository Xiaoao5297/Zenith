package io.netty.channel;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;

final class VoidChannelPromise extends AbstractFuture<Void> implements ChannelPromise {

    private final Channel channel;
    private final boolean fireException;

    public VoidChannelPromise(Channel channel, boolean flag) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            this.channel = channel;
            this.fireException = flag;
        }
    }

    @Override
    public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        fail();
        return this;
    }

    @Override
    public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        fail();
        return this;
    }

    @Override
    public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        return this;
    }

    @Override
    public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        return this;
    }

    @Override
    public VoidChannelPromise await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else {
            return this;
        }
    }

    @Override
    public boolean await(long i, TimeUnit timeunit) {
        fail();
        return false;
    }

    @Override
    public boolean await(long i) {
        fail();
        return false;
    }

    @Override
    public VoidChannelPromise awaitUninterruptibly() {
        fail();
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long i, TimeUnit timeunit) {
        fail();
        return false;
    }

    @Override
    public boolean awaitUninterruptibly(long i) {
        fail();
        return false;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean setUncancellable() {
        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public VoidChannelPromise sync() {
        fail();
        return this;
    }

    @Override
    public VoidChannelPromise syncUninterruptibly() {
        fail();
        return this;
    }

    @Override
    public VoidChannelPromise setFailure(Throwable throwable) {
        this.fireException(throwable);
        return this;
    }

    @Override
    public VoidChannelPromise setSuccess() {
        return this;
    }

    @Override
    public boolean tryFailure(Throwable throwable) {
        this.fireException(throwable);
        return false;
    }

    @Override
    public boolean cancel(boolean flag) {
        return false;
    }

    @Override
    public boolean trySuccess() {
        return false;
    }

    private static void fail() {
        throw new IllegalStateException("void future");
    }

    @Override
    public VoidChannelPromise setSuccess(Void ovoid) {
        return this;
    }

    public boolean trySuccess(Void ovoid) {
        return false;
    }

    @Override
    public Void getNow() {
        return null;
    }

    private void fireException(Throwable throwable) {
        if (this.fireException && this.channel.isRegistered()) {
            this.channel.pipeline().fireExceptionCaught(throwable);
        }

    }
}
