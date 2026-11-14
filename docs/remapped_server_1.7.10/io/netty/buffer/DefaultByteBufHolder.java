package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;

public class DefaultByteBufHolder implements ByteBufHolder {

    private final ByteBuf data;

    public DefaultByteBufHolder(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException("data");
        } else {
            this.data = bytebuf;
        }
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
    public ByteBufHolder copy() {
        return new DefaultByteBufHolder(this.data.copy());
    }

    @Override
    public ByteBufHolder duplicate() {
        return new DefaultByteBufHolder(this.data.duplicate());
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
        this.data.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int i) {
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
        return this.getClass().getSimpleName() + '(' + this.content().toString() + ')';
    }
}
