package io.netty.buffer;

import java.nio.ByteBuffer;

public abstract class AbstractDerivedByteBuf extends AbstractByteBuf {

    protected AbstractDerivedByteBuf(int i) {
        super(i);
    }

    @Override
    public final int refCnt() {
        return this.unwrap().refCnt();
    }

    @Override
    public final ByteBuf retain() {
        this.unwrap().retain();
        return this;
    }

    @Override
    public final ByteBuf retain(int i) {
        this.unwrap().retain(i);
        return this;
    }

    @Override
    public final boolean release() {
        return this.unwrap().release();
    }

    @Override
    public final boolean release(int i) {
        return this.unwrap().release(i);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return this.unwrap().nioBuffer(i, j);
    }
}
