package io.netty.handler.codec.bytes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder<byte[]> {

    public ByteArrayEncoder() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, byte[] abyte, List<Object> list) throws Exception {
        list.add(Unpooled.wrappedBuffer(abyte));
    }
}
