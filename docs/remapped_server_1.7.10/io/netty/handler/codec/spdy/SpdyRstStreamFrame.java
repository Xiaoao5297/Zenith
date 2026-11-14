package io.netty.handler.codec.spdy;

public interface SpdyRstStreamFrame extends SpdyStreamFrame {

    SpdyStreamStatus getStatus();

    SpdyRstStreamFrame setStatus(SpdyStreamStatus spdystreamstatus);

    @Override
    SpdyRstStreamFrame setStreamId(int i);

    @Override
    SpdyRstStreamFrame setLast(boolean flag);
}
