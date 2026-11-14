package io.netty.channel;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ThreadFactory;

public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);
    private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2));

    protected MultithreadEventLoopGroup(int i, ThreadFactory threadfactory, Object... aobject) {
        super(i == 0 ? MultithreadEventLoopGroup.DEFAULT_EVENT_LOOP_THREADS : i, threadfactory, aobject);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(this.getClass(), 10);
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.next().register(channel);
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelpromise) {
        return this.next().register(channel, channelpromise);
    }

    static {
        if (MultithreadEventLoopGroup.logger.isDebugEnabled()) {
            MultithreadEventLoopGroup.logger.debug("-Dio.netty.eventLoopThreads: {}", (Object) MultithreadEventLoopGroup.DEFAULT_EVENT_LOOP_THREADS);
        }

    }
}
