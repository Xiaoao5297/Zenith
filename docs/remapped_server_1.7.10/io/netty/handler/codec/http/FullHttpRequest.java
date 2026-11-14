package io.netty.handler.codec.http;

public interface FullHttpRequest extends HttpRequest, FullHttpMessage {

    @Override
    FullHttpRequest copy();

    @Override
    FullHttpRequest retain(int i);

    @Override
    FullHttpRequest retain();

    @Override
    FullHttpRequest setProtocolVersion(HttpVersion httpversion);

    @Override
    FullHttpRequest setMethod(HttpMethod httpmethod);

    @Override
    FullHttpRequest setUri(String s);
}
