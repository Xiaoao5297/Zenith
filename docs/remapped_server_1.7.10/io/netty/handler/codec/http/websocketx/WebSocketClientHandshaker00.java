package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker {

    private ByteBuf expectedChallengeResponseBytes;

    public WebSocketClientHandshaker00(URI uri, WebSocketVersion websocketversion, String s, HttpHeaders httpheaders, int i) {
        super(uri, websocketversion, s, httpheaders, i);
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        int i = WebSocketUtil.randomNumber(1, 12);
        int j = WebSocketUtil.randomNumber(1, 12);
        int k = Integer.MAX_VALUE / i;
        int l = Integer.MAX_VALUE / j;
        int i1 = WebSocketUtil.randomNumber(0, k);
        int j1 = WebSocketUtil.randomNumber(0, l);
        int k1 = i1 * i;
        int l1 = j1 * j;
        String s = Integer.toString(k1);
        String s1 = Integer.toString(l1);

        s = insertRandomCharacters(s);
        s1 = insertRandomCharacters(s1);
        s = insertSpaces(s, i);
        s1 = insertSpaces(s1, j);
        byte[] abyte = WebSocketUtil.randomBytes(8);
        ByteBuffer bytebuffer = ByteBuffer.allocate(4);

        bytebuffer.putInt(i1);
        byte[] abyte1 = bytebuffer.array();

        bytebuffer = ByteBuffer.allocate(4);
        bytebuffer.putInt(j1);
        byte[] abyte2 = bytebuffer.array();
        byte[] abyte3 = new byte[16];

        System.arraycopy(abyte1, 0, abyte3, 0, 4);
        System.arraycopy(abyte2, 0, abyte3, 4, 4);
        System.arraycopy(abyte, 0, abyte3, 8, 8);
        this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer(WebSocketUtil.md5(abyte3));
        URI uri = this.uri();
        String s2 = uri.getPath();

        if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
            s2 = uri.getPath() + '?' + uri.getQuery();
        }

        if (s2 == null || s2.isEmpty()) {
            s2 = "/";
        }

        DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, s2);
        HttpHeaders httpheaders = defaultfullhttprequest.headers();

        httpheaders.add("Upgrade", (Object) "WebSocket").add("Connection", (Object) "Upgrade").add("Host", (Object) uri.getHost());
        int i2 = uri.getPort();
        String s3 = "http://" + uri.getHost();

        if (i2 != 80 && i2 != 443) {
            s3 = s3 + ':' + i2;
        }

        httpheaders.add("Origin", (Object) s3).add("Sec-WebSocket-Key1", (Object) s).add("Sec-WebSocket-Key2", (Object) s1);
        String s4 = this.expectedSubprotocol();

        if (s4 != null && !s4.isEmpty()) {
            httpheaders.add("Sec-WebSocket-Protocol", (Object) s4);
        }

        if (this.customHeaders != null) {
            httpheaders.add(this.customHeaders);
        }

        httpheaders.set("Content-Length", (Object) abyte.length);
        defaultfullhttprequest.content().writeBytes(abyte);
        return defaultfullhttprequest;
    }

    @Override
    protected void verify(FullHttpResponse fullhttpresponse) {
        HttpResponseStatus httpresponsestatus = new HttpResponseStatus(101, "WebSocket Protocol Handshake");

        if (!fullhttpresponse.getStatus().equals(httpresponsestatus)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + fullhttpresponse.getStatus());
        } else {
            HttpHeaders httpheaders = fullhttpresponse.headers();
            String s = httpheaders.get("Upgrade");

            if (!"WebSocket".equalsIgnoreCase(s)) {
                throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + s);
            } else {
                String s1 = httpheaders.get("Connection");

                if (!"Upgrade".equalsIgnoreCase(s1)) {
                    throw new WebSocketHandshakeException("Invalid handshake response connection: " + s1);
                } else {
                    ByteBuf bytebuf = fullhttpresponse.content();

                    if (!bytebuf.equals(this.expectedChallengeResponseBytes)) {
                        throw new WebSocketHandshakeException("Invalid challenge");
                    }
                }
            }
        }
    }

    private static String insertRandomCharacters(String s) {
        int i = WebSocketUtil.randomNumber(1, 12);
        char[] achar = new char[i];
        int j = 0;

        int k;

        while (j < i) {
            k = (int) (Math.random() * 126.0D + 33.0D);
            if (33 < k && k < 47 || 58 < k && k < 126) {
                achar[j] = (char) k;
                ++j;
            }
        }

        for (k = 0; k < i; ++k) {
            int l = WebSocketUtil.randomNumber(0, s.length());
            String s1 = s.substring(0, l);
            String s2 = s.substring(l);

            s = s1 + achar[k] + s2;
        }

        return s;
    }

    private static String insertSpaces(String s, int i) {
        for (int j = 0; j < i; ++j) {
            int k = WebSocketUtil.randomNumber(1, s.length() - 1);
            String s1 = s.substring(0, k);
            String s2 = s.substring(k);

            s = s1 + ' ' + s2;
        }

        return s;
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket00FrameEncoder();
    }
}
