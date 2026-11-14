package io.netty.handler.codec.http;

public interface HttpResponse extends HttpMessage {

    HttpResponseStatus getStatus();

    HttpResponse setStatus(HttpResponseStatus httpresponsestatus);

    @Override
    HttpResponse setProtocolVersion(HttpVersion httpversion);
}
