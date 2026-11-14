package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;

public class WebSocketClientHandshaker07 extends WebSocketClientHandshaker {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;

    public WebSocketClientHandshaker07(URI uri, WebSocketVersion websocketversion, String s, boolean flag, HttpHeaders httpheaders, int i) {
        super(uri, websocketversion, s, httpheaders, i);
        this.allowExtensions = flag;
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        URI uri = this.uri();
        String s = uri.getPath();

        if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
            s = uri.getPath() + '?' + uri.getQuery();
        }

        if (s == null || s.isEmpty()) {
            s = "/";
        }

        byte[] abyte = WebSocketUtil.randomBytes(16);
        String s1 = WebSocketUtil.base64(abyte);
        String s2 = s1 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] abyte1 = WebSocketUtil.sha1(s2.getBytes(CharsetUtil.US_ASCII));

        this.expectedChallengeResponseString = WebSocketUtil.base64(abyte1);
        if (WebSocketClientHandshaker07.logger.isDebugEnabled()) {
            WebSocketClientHandshaker07.logger.debug(String.format("WS Version 07 Client Handshake key: %s. Expected response: %s.", s1, this.expectedChallengeResponseString));
        }

        DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, s);
        HttpHeaders httpheaders = defaultfullhttprequest.headers();

        httpheaders.add("Upgrade", (Object) "WebSocket".toLowerCase()).add("Connection", (Object) "Upgrade").add("Sec-WebSocket-Key", (Object) s1).add("Host", (Object) uri.getHost());
        int i = uri.getPort();
        String s3 = "http://" + uri.getHost();

        if (i != 80 && i != 443) {
            s3 = s3 + ':' + i;
        }

        httpheaders.add("Sec-WebSocket-Origin", (Object) s3);
        String s4 = this.expectedSubprotocol();

        if (s4 != null && !s4.isEmpty()) {
            httpheaders.add("Sec-WebSocket-Protocol", (Object) s4);
        }

        httpheaders.add("Sec-WebSocket-Version", (Object) "7");
        if (this.customHeaders != null) {
            httpheaders.add(this.customHeaders);
        }

        return defaultfullhttprequest;
    }

    @Override
    protected void verify(FullHttpResponse fullhttpresponse) {
        HttpResponseStatus httpresponsestatus = HttpResponseStatus.SWITCHING_PROTOCOLS;
        HttpHeaders httpheaders = fullhttpresponse.headers();

        if (!fullhttpresponse.getStatus().equals(httpresponsestatus)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + fullhttpresponse.getStatus());
        } else {
            String s = httpheaders.get("Upgrade");

            if (!"WebSocket".equalsIgnoreCase(s)) {
                throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + s);
            } else {
                String s1 = httpheaders.get("Connection");

                if (!"Upgrade".equalsIgnoreCase(s1)) {
                    throw new WebSocketHandshakeException("Invalid handshake response connection: " + s1);
                } else {
                    String s2 = httpheaders.get("Sec-WebSocket-Accept");

                    if (s2 == null || !s2.equals(this.expectedChallengeResponseString)) {
                        throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", s2, this.expectedChallengeResponseString));
                    }
                }
            }
        }
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket07FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket07FrameEncoder(true);
    }
}
