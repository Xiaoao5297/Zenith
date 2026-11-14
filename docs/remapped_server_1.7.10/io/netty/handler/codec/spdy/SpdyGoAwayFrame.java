package io.netty.handler.codec.spdy;

public interface SpdyGoAwayFrame extends SpdyFrame {

    int getLastGoodStreamId();

    SpdyGoAwayFrame setLastGoodStreamId(int i);

    SpdySessionStatus getStatus();

    SpdyGoAwayFrame setStatus(SpdySessionStatus spdysessionstatus);
}
