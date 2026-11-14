package io.netty.channel;

import java.net.SocketAddress;

public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler> extends ChannelDuplexHandler {

    private I inboundHandler;
    private O outboundHandler;

    protected CombinedChannelDuplexHandler() {}

    public CombinedChannelDuplexHandler(I i0, O o0) {
        this.init(i0, o0);
    }

    protected final void init(I i0, O o0) {
        this.validate(i0, o0);
        this.inboundHandler = i0;
        this.outboundHandler = o0;
    }

    private void validate(I i0, O o0) {
        if (this.inboundHandler != null) {
            throw new IllegalStateException("init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with non-default constructor.");
        } else if (i0 == null) {
            throw new NullPointerException("inboundHandler");
        } else if (o0 == null) {
            throw new NullPointerException("outboundHandler");
        } else if (i0 instanceof ChannelOutboundHandler) {
            throw new IllegalArgumentException("inboundHandler must not implement " + ChannelOutboundHandler.class.getSimpleName() + " to get combined.");
        } else if (o0 instanceof ChannelInboundHandler) {
            throw new IllegalArgumentException("outboundHandler must not implement " + ChannelInboundHandler.class.getSimpleName() + " to get combined.");
        }
    }

    protected final I inboundHandler() {
        return this.inboundHandler;
    }

    protected final O outboundHandler() {
        return this.outboundHandler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.inboundHandler == null) {
            throw new IllegalStateException("init() must be invoked before being added to a " + ChannelPipeline.class.getSimpleName() + " if " + CombinedChannelDuplexHandler.class.getSimpleName() + " was constructed with the default constructor.");
        } else {
            try {
                this.inboundHandler.handlerAdded(channelhandlercontext);
            } finally {
                this.outboundHandler.handlerAdded(channelhandlercontext);
            }

        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        try {
            this.inboundHandler.handlerRemoved(channelhandlercontext);
        } finally {
            this.outboundHandler.handlerRemoved(channelhandlercontext);
        }

    }

    @Override
    public void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelRegistered(channelhandlercontext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelUnregistered(channelhandlercontext);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelActive(channelhandlercontext);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelInactive(channelhandlercontext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        this.inboundHandler.exceptionCaught(channelhandlercontext, throwable);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.inboundHandler.userEventTriggered(channelhandlercontext, object);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.inboundHandler.channelRead(channelhandlercontext, object);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelReadComplete(channelhandlercontext);
    }

    @Override
    public void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.bind(channelhandlercontext, socketaddress, channelpromise);
    }

    @Override
    public void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.connect(channelhandlercontext, socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.disconnect(channelhandlercontext, channelpromise);
    }

    @Override
    public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.close(channelhandlercontext, channelpromise);
    }

    @Override
    public void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.deregister(channelhandlercontext, channelpromise);
    }

    @Override
    public void read(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.outboundHandler.read(channelhandlercontext);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.outboundHandler.write(channelhandlercontext, object, channelpromise);
    }

    @Override
    public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.outboundHandler.flush(channelhandlercontext);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.inboundHandler.channelWritabilityChanged(channelhandlercontext);
    }
}
