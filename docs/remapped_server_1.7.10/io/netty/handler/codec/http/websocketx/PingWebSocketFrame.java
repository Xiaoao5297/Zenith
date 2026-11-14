package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PingWebSocketFrame extends WebSocketFrame {

    public PingWebSocketFrame() {
        super(true, 0, Unpooled.buffer(0));
    }

    public PingWebSocketFrame(ByteBuf bytebuf) {
        super(bytebuf);
    }

    public PingWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    @Override
    public PingWebSocketFrame copy() {
        return new PingWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public PingWebSocketFrame duplicate() {
        return new PingWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public PingWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public PingWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
