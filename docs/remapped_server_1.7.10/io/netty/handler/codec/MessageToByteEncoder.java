package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class MessageToByteEncoder<I> extends ChannelOutboundHandlerAdapter {

    private final TypeParameterMatcher matcher;
    private final boolean preferDirect;

    protected MessageToByteEncoder() {
        this(true);
    }

    protected MessageToByteEncoder(Class<? extends I> oclass) {
        this(oclass, true);
    }

    protected MessageToByteEncoder(boolean flag) {
        this.matcher = TypeParameterMatcher.find(this, MessageToByteEncoder.class, "I");
        this.preferDirect = flag;
    }

    protected MessageToByteEncoder(Class<? extends I> oclass, boolean flag) {
        this.matcher = TypeParameterMatcher.get(oclass);
        this.preferDirect = flag;
    }

    public boolean acceptOutboundMessage(Object object) throws Exception {
        return this.matcher.match(object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        ByteBuf bytebuf = null;

        try {
            if (this.acceptOutboundMessage(object)) {
                Object object1 = object;

                if (this.preferDirect) {
                    bytebuf = channelhandlercontext.alloc().ioBuffer();
                } else {
                    bytebuf = channelhandlercontext.alloc().heapBuffer();
                }

                try {
                    this.encode(channelhandlercontext, object1, bytebuf);
                } finally {
                    ReferenceCountUtil.release(object);
                }

                if (bytebuf.isReadable()) {
                    channelhandlercontext.write(bytebuf, channelpromise);
                } else {
                    bytebuf.release();
                    channelhandlercontext.write(Unpooled.EMPTY_BUFFER, channelpromise);
                }

                bytebuf = null;
            } else {
                channelhandlercontext.write(object, channelpromise);
            }
        } catch (EncoderException encoderexception) {
            throw encoderexception;
        } catch (Throwable throwable) {
            throw new EncoderException(throwable);
        } finally {
            if (bytebuf != null) {
                bytebuf.release();
            }

        }

    }

    protected abstract void encode(ChannelHandlerContext channelhandlercontext, I i0, ByteBuf bytebuf) throws Exception;
}
