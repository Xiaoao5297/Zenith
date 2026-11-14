package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

public class RtspResponseEncoder extends RtspObjectEncoder<HttpResponse> {

    private static final byte[] CRLF = new byte[]{13, 10};

    public RtspResponseEncoder() {}

    @Override
    public boolean acceptOutboundMessage(Object object) throws Exception {
        return object instanceof FullHttpResponse;
    }

    protected void encodeInitialLine(ByteBuf bytebuf, HttpResponse httpresponse) throws Exception {
        encodeAscii(httpresponse.getProtocolVersion().toString(), bytebuf);
        bytebuf.writeByte(32);
        bytebuf.writeBytes(String.valueOf(httpresponse.getStatus().code()).getBytes(CharsetUtil.US_ASCII));
        bytebuf.writeByte(32);
        encodeAscii(String.valueOf(httpresponse.getStatus().reasonPhrase()), bytebuf);
        bytebuf.writeBytes(RtspResponseEncoder.CRLF);
    }
}
