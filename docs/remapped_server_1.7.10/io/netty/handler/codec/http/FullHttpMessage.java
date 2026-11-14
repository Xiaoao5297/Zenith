package io.netty.handler.codec.http;

public interface FullHttpMessage extends HttpMessage, LastHttpContent {

    @Override
    FullHttpMessage copy();

    @Override
    FullHttpMessage retain(int i);

    @Override
    FullHttpMessage retain();
}
