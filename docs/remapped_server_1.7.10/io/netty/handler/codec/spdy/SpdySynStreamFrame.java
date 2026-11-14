package io.netty.handler.codec.spdy;

public interface SpdySynStreamFrame extends SpdyHeadersFrame {

    int getAssociatedToStreamId();

    SpdySynStreamFrame setAssociatedToStreamId(int i);

    byte getPriority();

    SpdySynStreamFrame setPriority(byte b0);

    boolean isUnidirectional();

    SpdySynStreamFrame setUnidirectional(boolean flag);

    @Override
    SpdySynStreamFrame setStreamId(int i);

    @Override
    SpdySynStreamFrame setLast(boolean flag);

    @Override
    SpdySynStreamFrame setInvalid();
}
