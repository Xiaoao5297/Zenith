package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class TextWebSocketFrame extends WebSocketFrame {

    public TextWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public TextWebSocketFrame(String s) {
        super(fromText(s));
    }

    public TextWebSocketFrame(ByteBuf bytebuf) {
        super(bytebuf);
    }

    public TextWebSocketFrame(boolean flag, int i, String s) {
        super(flag, i, fromText(s));
    }

    private static ByteBuf fromText(String s) {
        return s != null && !s.isEmpty() ? Unpooled.copiedBuffer((CharSequence) s, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
    }

    public TextWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    public String text() {
        return this.content().toString(CharsetUtil.UTF_8);
    }

    @Override
    public TextWebSocketFrame copy() {
        return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public TextWebSocketFrame duplicate() {
        return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public TextWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public TextWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
