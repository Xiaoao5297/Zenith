package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyDataFrame extends DefaultSpdyStreamFrame implements SpdyDataFrame {

    private final ByteBuf data;

    public DefaultSpdyDataFrame(int i) {
        this(i, Unpooled.buffer(0));
    }

    public DefaultSpdyDataFrame(int i, ByteBuf bytebuf) {
        super(i);
        if (bytebuf == null) {
            throw new NullPointerException("data");
        } else {
            this.data = validate(bytebuf);
        }
    }

    private static ByteBuf validate(ByteBuf bytebuf) {
        if (bytebuf.readableBytes() > 16777215) {
            throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
        } else {
            return bytebuf;
        }
    }

    @Override
    public SpdyDataFrame setStreamId(int i) {
        super.setStreamId(i);
        return this;
    }

    @Override
    public SpdyDataFrame setLast(boolean flag) {
        super.setLast(flag);
        return this;
    }

    @Override
    public ByteBuf content() {
        if (this.data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(this.data.refCnt());
        } else {
            return this.data;
        }
    }

    @Override
    public SpdyDataFrame copy() {
        DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(this.getStreamId(), this.content().copy());

        defaultspdydataframe.setLast(this.isLast());
        return defaultspdydataframe;
    }

    @Override
    public SpdyDataFrame duplicate() {
        DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(this.getStreamId(), this.content().duplicate());

        defaultspdydataframe.setLast(this.isLast());
        return defaultspdydataframe;
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public SpdyDataFrame retain() {
        this.data.retain();
        return this;
    }

    @Override
    public SpdyDataFrame retain(int i) {
        this.data.retain(i);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int i) {
        return this.data.release(i);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("(last: ");
        stringbuilder.append(this.isLast());
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Stream-ID = ");
        stringbuilder.append(this.getStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Size = ");
        if (this.refCnt() == 0) {
            stringbuilder.append("(freed)");
        } else {
            stringbuilder.append(this.content().readableBytes());
        }

        return stringbuilder.toString();
    }
}
