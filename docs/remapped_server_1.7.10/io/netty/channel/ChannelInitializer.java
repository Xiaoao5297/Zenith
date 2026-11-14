package io.netty.channel;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);

    public ChannelInitializer() {}

    protected abstract void initChannel(C c0) throws Exception;

    @Override
    public final void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        boolean flag = false;
        boolean flag1 = false;

        try {
            this.initChannel(channelhandlercontext.channel());
            channelhandlercontext.pipeline().remove((ChannelHandler) this);
            flag = true;
            channelhandlercontext.fireChannelRegistered();
            flag1 = true;
        } catch (Throwable throwable) {
            ChannelInitializer.logger.warn("Failed to initialize a channel. Closing: " + channelhandlercontext.channel(), throwable);
        } finally {
            if (!flag) {
                channelhandlercontext.pipeline().remove((ChannelHandler) this);
            }

            if (!flag1) {
                channelhandlercontext.close();
            }

        }

    }
}
