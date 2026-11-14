package io.netty.handler.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN> extends ChannelDuplexHandler {

    private final MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<Object>() {
        @Override
        public boolean acceptOutboundMessage(Object object) throws Exception {
            return MessageToMessageCodec.this.acceptOutboundMessage(object);
        }

        @Override
        protected void encode(ChannelHandlerContext channelhandlercontext, Object object, List<Object> list) throws Exception {
            MessageToMessageCodec.this.encode(channelhandlercontext, object, list);
        }
    };
    private final MessageToMessageDecoder<Object> decoder = new MessageToMessageDecoder<Object>() {
        @Override
        public boolean acceptInboundMessage(Object object) throws Exception {
            return MessageToMessageCodec.this.acceptInboundMessage(object);
        }

        @Override
        protected void decode(ChannelHandlerContext channelhandlercontext, Object object, List<Object> list) throws Exception {
            MessageToMessageCodec.this.decode(channelhandlercontext, object, list);
        }
    };
    private final TypeParameterMatcher inboundMsgMatcher;
    private final TypeParameterMatcher outboundMsgMatcher;

    protected MessageToMessageCodec() {
        this.inboundMsgMatcher = TypeParameterMatcher.find(this, MessageToMessageCodec.class, "INBOUND_IN");
        this.outboundMsgMatcher = TypeParameterMatcher.find(this, MessageToMessageCodec.class, "OUTBOUND_IN");
    }

    protected MessageToMessageCodec(Class<? extends INBOUND_IN> oclass, Class<? extends OUTBOUND_IN> oclass1) {
        this.inboundMsgMatcher = TypeParameterMatcher.get(oclass);
        this.outboundMsgMatcher = TypeParameterMatcher.get(oclass1);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        this.decoder.channelRead(channelhandlercontext, object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.encoder.write(channelhandlercontext, object, channelpromise);
    }

    public boolean acceptInboundMessage(Object object) throws Exception {
        return this.inboundMsgMatcher.match(object);
    }

    public boolean acceptOutboundMessage(Object object) throws Exception {
        return this.outboundMsgMatcher.match(object);
    }

    protected abstract void encode(ChannelHandlerContext channelhandlercontext, OUTBOUND_IN outbound_in, List<Object> list) throws Exception;

    protected abstract void decode(ChannelHandlerContext channelhandlercontext, INBOUND_IN inbound_in, List<Object> list) throws Exception;
}
