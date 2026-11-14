package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.PlatformDependent;

abstract class SpdyHeaderBlockEncoder {

    SpdyHeaderBlockEncoder() {}

    static SpdyHeaderBlockEncoder newInstance(int i, int j, int k, int l) {
        return (SpdyHeaderBlockEncoder) (PlatformDependent.javaVersion() >= 7 ? new SpdyHeaderBlockZlibEncoder(i, j) : new SpdyHeaderBlockJZlibEncoder(i, j, k, l));
    }

    abstract ByteBuf encode(ChannelHandlerContext channelhandlercontext, SpdyHeadersFrame spdyheadersframe) throws Exception;

    abstract void end();
}
