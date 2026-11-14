package io.netty.handler.codec.http;

public class HttpRequestDecoder extends HttpObjectDecoder {

    public HttpRequestDecoder() {}

    public HttpRequestDecoder(int i, int j, int k) {
        super(i, j, k, true);
    }

    @Override
    protected HttpMessage createMessage(String[] astring) throws Exception {
        return new DefaultHttpRequest(HttpVersion.valueOf(astring[2]), HttpMethod.valueOf(astring[0]), astring[1]);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request");
    }

    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}
