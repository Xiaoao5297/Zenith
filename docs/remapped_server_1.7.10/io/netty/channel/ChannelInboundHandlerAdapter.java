package io.netty.channel;

public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler {

    public ChannelInboundHandlerAdapter() {}

    @Override
    public void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        channelhandlercontext.fireChannelRead(object);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        channelhandlercontext.fireUserEventTriggered(object);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelhandlercontext) throws Exception {
        channelhandlercontext.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        channelhandlercontext.fireExceptionCaught(throwable);
    }
}
