package io.netty.handler.codec.http;

import io.netty.channel.CombinedChannelDuplexHandler;

public final class HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> {

    public HttpServerCodec() {
        this(4096, 8192, 8192);
    }

    public HttpServerCodec(int i, int j, int k) {
        super(new HttpRequestDecoder(i, j, k), new HttpResponseEncoder());
    }
}
