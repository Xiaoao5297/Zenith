package io.netty.handler.codec.bytes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf> {

    public ByteArrayDecoder() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        byte[] abyte = new byte[bytebuf.readableBytes()];

        bytebuf.getBytes(0, abyte);
        list.add(abyte);
    }
}
