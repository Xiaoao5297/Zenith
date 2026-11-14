package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public abstract class WebSocketFrame extends DefaultByteBufHolder {

    private final boolean finalFragment;
    private final int rsv;

    protected WebSocketFrame(ByteBuf bytebuf) {
        this(true, 0, bytebuf);
    }

    protected WebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(bytebuf);
        this.finalFragment = flag;
        this.rsv = i;
    }

    public boolean isFinalFragment() {
        return this.finalFragment;
    }

    public int rsv() {
        return this.rsv;
    }

    @Override
    public abstract WebSocketFrame copy();

    @Override
    public abstract WebSocketFrame duplicate();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.content().toString() + ')';
    }

    @Override
    public WebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public WebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
