package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;

public final class SpdyFrameCodec extends CombinedChannelDuplexHandler<SpdyFrameDecoder, SpdyFrameEncoder> {

    public SpdyFrameCodec(int i) {
        this(i, 8192, 16384, 6, 15, 8);
    }

    public SpdyFrameCodec(int i, int j, int k, int l, int i1, int j1) {
        super(new SpdyFrameDecoder(i, j, k), new SpdyFrameEncoder(i, l, i1, j1));
    }
}
