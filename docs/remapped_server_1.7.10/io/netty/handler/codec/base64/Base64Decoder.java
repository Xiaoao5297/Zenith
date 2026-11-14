package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Decoder extends MessageToMessageDecoder<ByteBuf> {

    private final Base64Dialect dialect;

    public Base64Decoder() {
        this(Base64Dialect.STANDARD);
    }

    public Base64Decoder(Base64Dialect base64dialect) {
        if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            this.dialect = base64dialect;
        }
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        list.add(Base64.decode(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes(), this.dialect));
    }
}
