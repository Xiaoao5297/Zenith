package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;

public class CloseWebSocketFrame extends WebSocketFrame {

    public CloseWebSocketFrame() {
        super(Unpooled.buffer(0));
    }

    public CloseWebSocketFrame(int i, String s) {
        this(true, 0, i, s);
    }

    public CloseWebSocketFrame(boolean flag, int i) {
        this(flag, i, (ByteBuf) null);
    }

    public CloseWebSocketFrame(boolean flag, int i, int j, String s) {
        super(flag, i, newBinaryData(j, s));
    }

    private static ByteBuf newBinaryData(int i, String s) {
        byte[] abyte = EmptyArrays.EMPTY_BYTES;

        if (s != null) {
            abyte = s.getBytes(CharsetUtil.UTF_8);
        }

        ByteBuf bytebuf = Unpooled.buffer(2 + abyte.length);

        bytebuf.writeShort(i);
        if (abyte.length > 0) {
            bytebuf.writeBytes(abyte);
        }

        bytebuf.readerIndex(0);
        return bytebuf;
    }

    public CloseWebSocketFrame(boolean flag, int i, ByteBuf bytebuf) {
        super(flag, i, bytebuf);
    }

    public int statusCode() {
        ByteBuf bytebuf = this.content();

        if (bytebuf != null && bytebuf.capacity() != 0) {
            bytebuf.readerIndex(0);
            short short0 = bytebuf.readShort();

            bytebuf.readerIndex(0);
            return short0;
        } else {
            return -1;
        }
    }

    public String reasonText() {
        ByteBuf bytebuf = this.content();

        if (bytebuf != null && bytebuf.capacity() > 2) {
            bytebuf.readerIndex(2);
            String s = bytebuf.toString(CharsetUtil.UTF_8);

            bytebuf.readerIndex(0);
            return s;
        } else {
            return "";
        }
    }

    @Override
    public CloseWebSocketFrame copy() {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }

    @Override
    public CloseWebSocketFrame duplicate() {
        return new CloseWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }

    @Override
    public CloseWebSocketFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public CloseWebSocketFrame retain(int i) {
        super.retain(i);
        return this;
    }
}
