package io.netty.channel;

import java.net.SocketAddress;

public class ChannelOutboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelOutboundHandler {

    public ChannelOutboundHandlerAdapter() {}

    @Override
    public void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.bind(socketaddress, channelpromise);
    }

    @Override
    public void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.connect(socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.disconnect(channelpromise);
    }

    @Override
    public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.close(channelpromise);
    }

    @Override
    public void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.deregister(channelpromise);
    }

    @Override
    public void read(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.read();
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.write(object, channelpromise);
    }

    @Override
    public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.flush();
    }
}
