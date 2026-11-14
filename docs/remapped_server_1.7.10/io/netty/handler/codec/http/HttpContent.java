package io.netty.handler.codec.http;

import io.netty.buffer.ByteBufHolder;

public interface HttpContent extends HttpObject, ByteBufHolder {

    @Override
    HttpContent copy();

    @Override
    HttpContent duplicate();

    @Override
    HttpContent retain();

    @Override
    HttpContent retain(int i);
}
