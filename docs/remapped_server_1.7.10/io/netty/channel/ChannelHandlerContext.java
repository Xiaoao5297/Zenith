package io.netty.channel;

import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;

public interface ChannelHandlerContext extends AttributeMap, ChannelPropertyAccess, ChannelInboundInvoker, ChannelOutboundInvoker {

    Channel channel();

    EventExecutor executor();

    String name();

    ChannelHandler handler();

    boolean isRemoved();

    @Override
    ChannelHandlerContext fireChannelRegistered();

    /** @deprecated */
    @Deprecated
    @Override
    ChannelHandlerContext fireChannelUnregistered();

    @Override
    ChannelHandlerContext fireChannelActive();

    @Override
    ChannelHandlerContext fireChannelInactive();

    @Override
    ChannelHandlerContext fireExceptionCaught(Throwable throwable);

    @Override
    ChannelHandlerContext fireUserEventTriggered(Object object);

    @Override
    ChannelHandlerContext fireChannelRead(Object object);

    @Override
    ChannelHandlerContext fireChannelReadComplete();

    @Override
    ChannelHandlerContext fireChannelWritabilityChanged();

    @Override
    ChannelHandlerContext flush();
}
