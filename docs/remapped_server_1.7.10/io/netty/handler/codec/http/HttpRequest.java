package io.netty.handler.codec.http;

public interface HttpRequest extends HttpMessage {

    HttpMethod getMethod();

    HttpRequest setMethod(HttpMethod httpmethod);

    String getUri();

    HttpRequest setUri(String s);

    @Override
    HttpRequest setProtocolVersion(HttpVersion httpversion);
}
