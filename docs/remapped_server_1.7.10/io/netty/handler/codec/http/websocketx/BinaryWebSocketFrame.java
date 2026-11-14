package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BinaryWebSocketFrame extends WebSocketFrame {

    public BinaryWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public BinaryWebSocketFrame(ByteBuf bytebuf) {
        super(bytebuf);
    }

    public BinaryWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    @Override
    public BinaryWebSocketFrame copy() {
        return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public BinaryWebSocketFrame duplicate() {
        return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public BinaryWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public BinaryWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
