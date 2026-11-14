package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ContinuationWebSocketFrame extends WebSocketFrame {

    private String aggregatedText;

    public ContinuationWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public ContinuationWebSocketFrame(ByteBuf bytebuf) {
        super(bytebuf);
    }

    public ContinuationWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    public ContinuationWebSocketFrame(boolean flag, int i, ByteBuf bytebuf, String s) {
        super(flag, i, bytebuf);
        this.aggregatedText = s;
    }

    public ContinuationWebSocketFrame(boolean flag, int i, String s) {
        this(flag, i, fromText(s), (String) null);
    }

    public String text() {
        return this.content().toString(CharsetUtil.UTF_8);
    }

    private static ByteBuf fromText(String s) {
        return s != null && !s.isEmpty() ? Unpooled.copiedBuffer((CharSequence) s, CharsetUtil.UTF_8) : Unpooled.EMPTY_BUFFER;
    }

    public String aggregatedText() {
        return this.aggregatedText;
    }

    @Override
    public ContinuationWebSocketFrame copy() {
        return new ContinuationWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy(), this.aggregatedText());
    }

    @Override
    public ContinuationWebSocketFrame duplicate() {
        return new ContinuationWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate(), this.aggregatedText());
    }

    @Override
    public ContinuationWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public ContinuationWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
