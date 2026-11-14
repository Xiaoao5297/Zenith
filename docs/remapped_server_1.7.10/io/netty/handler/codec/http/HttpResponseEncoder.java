package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;

public class HttpResponseEncoder extends HttpObjectEncoder<HttpResponse> {

    private static final byte[] CRLF = new byte[]{13, 10};

    public HttpResponseEncoder() {}

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return super.acceptOutboundMessage(object) && !(object instanceof HttpRequest);
    }

    protected void encodeInitialLine(ByteBuf bytebuf, HttpResponse httpresponse) throws Exception {
        encodeAscii(httpresponse.getProtocolVersion().toString(), bytebuf);
        bytebuf.writeByte(32);
        encodeAscii(String.valueOf(httpresponse.getStatus().code()), bytebuf);
        bytebuf.writeByte(32);
        encodeAscii(String.valueOf(httpresponse.getStatus().reasonPhrase()), bytebuf);
        bytebuf.writeBytes(HttpResponseEncoder.CRLF);
    }
}
