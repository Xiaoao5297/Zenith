package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength;

    public FixedLengthFrameDecoder(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + i);
        } else {
            this.frameLength = i;
        }
    }

    @Override
    protected final void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        Object object = this.decode(channelhandlercontext, bytebuf);

        if (object != null) {
            list.add(object);
        }

    }

    protected Object decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws Exception {
        return bytebuf.readableBytes() < this.frameLength ? null : bytebuf.readBytes(this.frameLength);
    }
}
