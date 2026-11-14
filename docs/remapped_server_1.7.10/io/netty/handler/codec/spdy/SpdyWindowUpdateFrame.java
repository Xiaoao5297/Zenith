package io.netty.handler.codec.spdy;

public interface SpdyWindowUpdateFrame extends SpdyFrame {

    int getStreamId();

    SpdyWindowUpdateFrame setStreamId(int i);

    int getDeltaWindowSize();

    SpdyWindowUpdateFrame setDeltaWindowSize(int i);
}
