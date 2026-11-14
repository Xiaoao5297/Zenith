package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

final class SucceededChannelFuture extends CompleteChannelFuture {

    public SucceededChannelFuture(Channel channel, EventExecutor eventexecutor) {
        super(channel, eventexecutor);
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
