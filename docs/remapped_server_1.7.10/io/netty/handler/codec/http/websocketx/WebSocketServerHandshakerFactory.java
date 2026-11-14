package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class WebSocketServerHandshakerFactory {

    private final String webSocketURL;
    private final String subprotocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadLength;

    public WebSocketServerHandshakerFactory(String s, String s1, boolean flag) {
        this(s, s1, flag, 65536);
    }

    public WebSocketServerHandshakerFactory(String s, String s1, boolean flag, int i) {
        this.webSocketURL = s;
        this.subprotocols = s1;
        this.allowExtensions = flag;
        this.maxFramePayloadLength = i;
    }

    public WebSocketServerHandshaker newHandshaker(HttpRequest httprequest) {
        String s = httprequest.headers().get("Sec-WebSocket-Version");

        return (WebSocketServerHandshaker) (s != null ? (s.equals(WebSocketVersion.V13.toHttpHeaderValue()) ? new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength) : (s.equals(WebSocketVersion.V08.toHttpHeaderValue()) ? new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength) : (s.equals(WebSocketVersion.V07.toHttpHeaderValue()) ? new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength) : null))) : new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.maxFramePayloadLength));
    }

    public static void sendUnsupportedWebSocketVersionResponse(Channel channel) {
        DefaultHttpResponse defaulthttpresponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED);

        defaulthttpresponse.headers().set("Sec-WebSocket-Version", (Object) WebSocketVersion.V13.toHttpHeaderValue());
        channel.write(defaulthttpresponse);
    }
}
