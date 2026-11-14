package io.netty.channel;

public abstract class ChannelHandlerAdapter implements ChannelHandler {

    boolean added;

    public ChannelHandlerAdapter() {}

    public boolean isSharable() {
        return this.getClass().isAnnotationPresent(ChannelHandler.Sharable.class);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {}

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {}

    /** @deprecated */
    @Deprecated
    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        channelhandlercontext.fireExceptionCaught(throwable);
    }
}
