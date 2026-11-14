package io.netty.channel;

import java.net.SocketAddress;

public interface ChannelOutboundHandler extends ChannelHandler {

    void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception;

    void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception;

    void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception;

    void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception;

    /** @deprecated */
    @Deprecated
    void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception;

    void read(ChannelHandlerContext channelhandlercontext) throws Exception;

    void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception;

    void flush(ChannelHandlerContext channelhandlercontext) throws Exception;
}
