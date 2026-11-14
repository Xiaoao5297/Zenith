package io.netty.handler.codec.http;

public class HttpResponseDecoder extends HttpObjectDecoder {

    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");

    public HttpResponseDecoder() {}

    public HttpResponseDecoder(int i, int j, int k) {
        super(i, j, k, true);
    }

    @Override
    protected HttpMessage createMessage(String[] astring) {
        return new DefaultHttpResponse(HttpVersion.valueOf(astring[0]), new HttpResponseStatus(Integer.valueOf(astring[1]), astring[2]));
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultHttpResponse(HttpVersion.HTTP_1_0, HttpResponseDecoder.UNKNOWN_STATUS);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
}
