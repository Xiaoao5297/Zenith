package io.netty.handler.codec.http.websocketx;

public enum WebSocketVersion {

    UNKNOWN, V00, V07, V08, V13;

    private WebSocketVersion() {}

    public String toHttpHeaderValue() {
        if (this == WebSocketVersion.V00) {
            return "0";
        } else if (this == WebSocketVersion.V07) {
            return "7";
        } else if (this == WebSocketVersion.V08) {
            return "8";
        } else if (this == WebSocketVersion.V13) {
            return "13";
        } else {
            throw new IllegalStateException("Unknown web socket version: " + this);
        }
    }
}
