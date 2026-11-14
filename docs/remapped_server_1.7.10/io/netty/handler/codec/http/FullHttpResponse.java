package io.netty.handler.codec.http;

public interface FullHttpResponse extends HttpResponse, FullHttpMessage {

    @Override
    FullHttpResponse copy();

    @Override
    FullHttpResponse retain(int i);

    @Override
    FullHttpResponse retain();

    @Override
    FullHttpResponse setProtocolVersion(HttpVersion httpversion);

    @Override
    FullHttpResponse setStatus(HttpResponseStatus httpresponsestatus);
}
