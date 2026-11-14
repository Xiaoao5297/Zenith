package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Encoder extends MessageToMessageEncoder<ByteBuf> {

    private final boolean breakLines;
    private final Base64Dialect dialect;

    public Base64Encoder() {
        this(true);
    }

    public Base64Encoder(boolean flag) {
        this(flag, Base64Dialect.STANDARD);
    }

    public Base64Encoder(boolean flag, Base64Dialect base64dialect) {
        if (base64dialect == null) {
            throw new NullPointerException("dialect");
        } else {
            this.breakLines = flag;
            this.dialect = base64dialect;
        }
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        list.add(Base64.encode(bytebuf, bytebuf.readerIndex(), bytebuf.readableBytes(), this.breakLines, this.dialect));
    }
}
