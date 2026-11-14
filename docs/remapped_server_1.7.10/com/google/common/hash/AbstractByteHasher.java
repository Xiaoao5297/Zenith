package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractByteHasher extends AbstractHasher {

    private final ByteBuffer scratch;

    AbstractByteHasher() {
        this.scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    }

    protected abstract void update(byte b0);

    protected void update(byte[] abyte) {
        this.update(abyte, 0, abyte.length);
    }

    protected void update(byte[] abyte, int i, int j) {
        for (int k = i; k < i + j; ++k) {
            this.update(abyte[k]);
        }

    }

    @Override
    public Hasher putByte(byte b0) {
        this.update(b0);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] abyte) {
        Preconditions.checkNotNull(abyte);
        this.update(abyte);
        return this;
    }

    @Override
    public Hasher putBytes(byte[] abyte, int i, int j) {
        Preconditions.checkPositionIndexes(i, i + j, abyte.length);
        this.update(abyte, i, j);
        return this;
    }

    private Hasher update(int i) {
        try {
            this.update(this.scratch.array(), 0, i);
        } finally {
            this.scratch.clear();
        }

        return this;
    }

    @Override
    public Hasher putShort(short short0) {
        this.scratch.putShort(short0);
        return this.update((int) 2);
    }

    @Override
    public Hasher putInt(int i) {
        this.scratch.putInt(i);
        return this.update((int) 4);
    }

    @Override
    public Hasher putLong(long i) {
        this.scratch.putLong(i);
        return this.update((int) 8);
    }

    @Override
    public Hasher putChar(char c0) {
        this.scratch.putChar(c0);
        return this.update((int) 2);
    }

    @Override
    public <T> Hasher putObject(T t0, Funnel<? super T> funnel) {
        funnel.funnel(t0, this);
        return this;
    }
}
