package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;

public final class SpdyHttpCodec extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder> {

    public SpdyHttpCodec(int i, int j) {
        super(new SpdyHttpDecoder(i, j), new SpdyHttpEncoder(i));
    }
}
