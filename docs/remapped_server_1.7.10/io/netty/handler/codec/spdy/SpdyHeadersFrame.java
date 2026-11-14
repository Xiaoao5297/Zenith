package io.netty.handler.codec.spdy;

public interface SpdyHeadersFrame extends SpdyStreamFrame {

    boolean isInvalid();

    SpdyHeadersFrame setInvalid();

    boolean isTruncated();

    SpdyHeadersFrame setTruncated();

    SpdyHeaders headers();

    @Override
    SpdyHeadersFrame setStreamId(int i);

    @Override
    SpdyHeadersFrame setLast(boolean flag);
}
