package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;

abstract class SpdyHeaderBlockDecoder {

    SpdyHeaderBlockDecoder() {}

    static SpdyHeaderBlockDecoder newInstance(int i, int j) {
        return new SpdyHeaderBlockZlibDecoder(i, j);
    }

    abstract void decode(ByteBuf bytebuf, SpdyHeadersFrame spdyheadersframe) throws Exception;

    abstract void reset();

    abstract void end();
}
