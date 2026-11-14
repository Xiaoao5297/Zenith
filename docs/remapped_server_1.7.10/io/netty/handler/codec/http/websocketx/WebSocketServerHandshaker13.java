package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandshaker13 extends WebSocketServerHandshaker {

    public static final String WEBSOCKET_13_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private final boolean allowExtensions;

    public WebSocketServerHandshaker13(String s, String s1, boolean flag, int i) {
        super(WebSocketVersion.V13, s, s1, i);
        this.allowExtensions = flag;
    }

    @Override
    protected FullHttpResponse newHandshakeResponse(FullHttpRequest fullhttprequest, HttpHeaders httpheaders) {
        DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS);

        if (httpheaders != null) {
            defaultfullhttpresponse.headers().add(httpheaders);
        }

        String s = fullhttprequest.headers().get("Sec-WebSocket-Key");

        if (s == null) {
            throw new WebSocketHandshakeException("not a WebSocket request: missing key");
        } else {
            String s1 = s + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            byte[] abyte = WebSocketUtil.sha1(s1.getBytes(CharsetUtil.US_ASCII));
            String s2 = WebSocketUtil.base64(abyte);

            if (WebSocketServerHandshaker13.logger.isDebugEnabled()) {
                WebSocketServerHandshaker13.logger.debug(String.format("WS Version 13 Server Handshake key: %s. Response: %s.", s, s2));
            }

            defaultfullhttpresponse.headers().add("Upgrade", (Object) "WebSocket".toLowerCase());
            defaultfullhttpresponse.headers().add("Connection", (Object) "Upgrade");
            defaultfullhttpresponse.headers().add("Sec-WebSocket-Accept", (Object) s2);
            String s3 = fullhttprequest.headers().get("Sec-WebSocket-Protocol");

            if (s3 != null) {
                String s4 = this.selectSubprotocol(s3);

                if (s4 == null) {
                    throw new WebSocketHandshakeException("Requested subprotocol(s) not supported: " + s3);
                }

                defaultfullhttpresponse.headers().add("Sec-WebSocket-Protocol", (Object) s4);
            }

            return defaultfullhttpresponse;
        }
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket13FrameDecoder(true, this.allowExtensions, this.maxFramePayloadLength());
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket13FrameEncoder(false);
    }
}
