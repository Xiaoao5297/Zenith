package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter {

    private final TypeParameterMatcher matcher;
    private final boolean autoRelease;

    protected SimpleChannelInboundHandler() {
        this(true);
    }

    protected SimpleChannelInboundHandler(boolean flag) {
        this.matcher = TypeParameterMatcher.find(this, SimpleChannelInboundHandler.class, "I");
        this.autoRelease = flag;
    }

    protected SimpleChannelInboundHandler(Class<? extends I> oclass) {
        this(oclass, true);
    }

    protected SimpleChannelInboundHandler(Class<? extends I> oclass, boolean flag) {
        this.matcher = TypeParameterMatcher.get(oclass);
        this.autoRelease = flag;
    }

    public boolean acceptInboundMessage(Object object) throws Exception {
        return this.matcher.match(object);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        boolean flag = true;

        try {
            if (this.acceptInboundMessage(object)) {
                this.channelRead0(channelhandlercontext, object);
            } else {
                flag = false;
                channelhandlercontext.fireChannelRead(object);
            }
        } finally {
            if (this.autoRelease && flag) {
                ReferenceCountUtil.release(object);
            }

        }

    }

    protected abstract void channelRead0(ChannelHandlerContext channelhandlercontext, I i0) throws Exception;
}
