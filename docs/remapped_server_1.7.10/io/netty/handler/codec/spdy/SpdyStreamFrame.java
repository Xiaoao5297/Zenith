package io.netty.handler.codec.spdy;

public interface SpdyStreamFrame extends SpdyFrame {

    int getStreamId();

    SpdyStreamFrame setStreamId(int i);

    boolean isLast();

    SpdyStreamFrame setLast(boolean flag);
}
