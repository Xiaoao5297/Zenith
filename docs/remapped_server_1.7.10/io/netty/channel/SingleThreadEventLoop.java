package io.netty.channel;

import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    protected SingleThreadEventLoop(EventLoopGroup eventloopgroup, ThreadFactory threadfactory, boolean flag) {
        super(eventloopgroup, threadfactory, flag);
    }

    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup) super.parent();
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register(channel, channel.newPromise());
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelpromise) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (channelpromise == null) {
            throw new NullPointerException("promise");
        } else {
            channel.unsafe().register(this, channelpromise);
            return channelpromise;
        }
    }
}
