package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;

public final class WebSocketClientHandshakerFactory {

    private WebSocketClientHandshakerFactory() {}

    public static WebSocketClientHandshaker newHandshaker(URI uri, WebSocketVersion websocketversion, String s, boolean flag, HttpHeaders httpheaders) {
        return newHandshaker(uri, websocketversion, s, flag, httpheaders, 65536);
    }

    public static WebSocketClientHandshaker newHandshaker(URI uri, WebSocketVersion websocketversion, String s, boolean flag, HttpHeaders httpheaders, int i) {
        if (websocketversion == WebSocketVersion.V13) {
            return new WebSocketClientHandshaker13(uri, WebSocketVersion.V13, s, flag, httpheaders, i);
        } else if (websocketversion == WebSocketVersion.V08) {
            return new WebSocketClientHandshaker08(uri, WebSocketVersion.V08, s, flag, httpheaders, i);
        } else if (websocketversion == WebSocketVersion.V07) {
            return new WebSocketClientHandshaker07(uri, WebSocketVersion.V07, s, flag, httpheaders, i);
        } else if (websocketversion == WebSocketVersion.V00) {
            return new WebSocketClientHandshaker00(uri, WebSocketVersion.V00, s, httpheaders, i);
        } else {
            throw new WebSocketHandshakeException("Protocol version " + websocketversion.toString() + " not supported.");
        }
    }
}
