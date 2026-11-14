package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface SpdyDataFrame extends ByteBufHolder, SpdyStreamFrame {

    @Override
    SpdyDataFrame setStreamId(int i);

    @Override
    SpdyDataFrame setLast(boolean flag);

    @Override
    ByteBuf content();

    @Override
    SpdyDataFrame copy();

    @Override
    SpdyDataFrame duplicate();

    @Override
    SpdyDataFrame retain();

    @Override
    SpdyDataFrame retain(int i);
}
