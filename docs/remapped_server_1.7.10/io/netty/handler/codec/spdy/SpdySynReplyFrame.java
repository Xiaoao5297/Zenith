package io.netty.handler.codec.spdy;

public interface SpdySynReplyFrame extends SpdyHeadersFrame {

    @Override
    SpdySynReplyFrame setStreamId(int i);

    @Override
    SpdySynReplyFrame setLast(boolean flag);

    @Override
    SpdySynReplyFrame setInvalid();
}
