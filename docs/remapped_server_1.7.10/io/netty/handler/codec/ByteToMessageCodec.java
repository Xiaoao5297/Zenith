package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class ByteToMessageCodec<I> extends ChannelDuplexHandler {

    private final TypeParameterMatcher outboundMsgMatcher;
    private final MessageToByteEncoder<I> encoder;
    private final ByteToMessageDecoder decoder;

    protected ByteToMessageCodec() {
        this(true);
    }

    protected ByteToMessageCodec(Class<? extends I> oclass) {
        this(oclass, true);
    }

    protected ByteToMessageCodec(boolean flag) {
        this.decoder = new ByteToMessageDecoder() {
            @Override
            public void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
                ByteToMessageCodec.this.decode(channelhandlercontext, bytebuf, list);
            }

            @Override
            protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
                ByteToMessageCodec.this.decodeLast(channelhandlercontext, bytebuf, list);
            }
        };
        this.outboundMsgMatcher = TypeParameterMatcher.find(this, ByteToMessageCodec.class, "I");
        this.encoder = new ByteToMessageCodec.Encoder(flag);
    }

    protected ByteToMessageCodec(Class<? extends I> oclass, boolean flag) {
        this.decoder = new ByteToMessageDecoder() {
            @Override
            public void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
                ByteToMessageCodec.this.decode(channelhandlercontext, bytebuf, list);
            }

            @Override
            protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
                ByteToMessageCodec.this.decodeLast(channelhandlercontext, bytebuf, list);
            }
        };
        this.checkForSharableAnnotation();
        this.outboundMsgMatcher = TypeParameterMatcher.get(oclass);
        this.encoder = new ByteToMessageCodec.Encoder(flag);
    }

    private void checkForSharableAnnotation() {
        if (this.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
            throw new IllegalStateException("@Sharable annotation is not allowed");
        }
    }

    public boolean acceptOutboundMessage(Object object) throws Exception {
        return this.outboundMsgMatcher.match(object);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.decoder.channelRead(channelhandlercontext, object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.encoder.write(channelhandlercontext, object, channelpromise);
    }

    protected abstract void encode(ChannelHandlerContext channelhandlercontext, I i0, ByteBuf bytebuf) throws Exception;

    protected abstract void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception;

    protected void decodeLast(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        this.decode(channelhandlercontext, bytebuf, list);
    }

    private final class Encoder extends MessageToByteEncoder<I> {

        Encoder(boolean flag) {
            super(flag);
        }

        @Override
        public boolean acceptOutboundMessage(Object object) throws Exception {
            return ByteToMessageCodec.this.acceptOutboundMessage(object);
        }

        @Override
        protected void encode(ChannelHandlerContext channelhandlercontext, I i0, ByteBuf bytebuf) throws Exception {
            ByteToMessageCodec.this.encode(channelhandlercontext, i0, bytebuf);
        }
    }
}
