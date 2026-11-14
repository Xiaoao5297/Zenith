package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class RtspRequestEncoder extends RtspObjectEncoder<HttpRequest> {

    private static final byte[] CRLF = new byte[]{13, 10};

    public RtspRequestEncoder() {}

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return object instanceof FullHttpRequest;
    }

    protected void encodeInitialLine(ByteBuf bytebuf, HttpRequest httprequest) throws Exception {
        encodeAscii(httprequest.getMethod().toString(), bytebuf);
        bytebuf.writeByte(32);
        bytebuf.writeBytes(httprequest.getUri().getBytes(CharsetUtil.UTF_8));
        bytebuf.writeByte(32);
        encodeAscii(httprequest.getProtocolVersion().toString(), bytebuf);
        bytebuf.writeBytes(RtspRequestEncoder.CRLF);
    }
}
