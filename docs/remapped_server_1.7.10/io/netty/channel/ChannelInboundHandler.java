package io.netty.channel;

public interface ChannelInboundHandler extends ChannelHandler {

    void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception;

    /** @deprecated */
    @Deprecated
    void channelUnregistered(ChannelHandlerContext channelhandlercontext) throws Exception;

    void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception;

    void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception;

    void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception;

    void channelReadComplete(ChannelHandlerContext channelhandlercontext) throws Exception;

    void userEventTriggered(ChannelHandlerContext channelhandlercontext, Object object) throws Exception;

    void channelWritabilityChanged(ChannelHandlerContext channelhandlercontext) throws Exception;

    @Override
    void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception;
}
