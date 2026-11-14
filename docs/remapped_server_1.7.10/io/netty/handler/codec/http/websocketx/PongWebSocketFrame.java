package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PongWebSocketFrame extends WebSocketFrame {

    public PongWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public PongWebSocketFrame(ByteBuf bytebuf) {
        super(bytebuf);
    }

    public PongWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    @Override
    public PongWebSocketFrame copy() {
        return new PongWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public PongWebSocketFrame duplicate() {
        return new PongWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public PongWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public PongWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
