package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class HttpRequestEncoder extends HttpObjectEncoder<HttpRequest> {

    private static final char SLASH = '/';
    private static final byte[] CRLF = new byte[]{13, 10};

    public HttpRequestEncoder() {}

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return super.acceptOutboundMessage(object) && !(object instanceof HttpResponse);
    }

    protected void encodeInitialLine(ByteBuf bytebuf, HttpRequest httprequest) throws Exception {
        encodeAscii(httprequest.getMethod().toString(), bytebuf);
        bytebuf.writeByte(32);
        String s = httprequest.getUri();

        if (s.length() == 0) {
            s = s + '/';
        } else {
            int i = s.indexOf("://");

            if (i != -1 && s.charAt(0) != '/') {
                int j = i + 3;

                if (s.lastIndexOf(47) <= j) {
                    s = s + '/';
                }
            }
        }

        bytebuf.writeBytes(s.getBytes(CharsetUtil.UTF_8));
        bytebuf.writeByte(32);
        encodeAscii(httprequest.getProtocolVersion().toString(), bytebuf);
        bytebuf.writeBytes(HttpRequestEncoder.CRLF);
    }
}
