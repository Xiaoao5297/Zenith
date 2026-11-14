package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator {

    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());

    public UnpooledByteBufAllocator(boolean flag) {
        super(flag);
    }

    @Override
    protected ByteBuf newHeapBuffer(int i, int j) {
        return new UnpooledHeapByteBuf(this, i, j);
    }

    @Override
    protected ByteBuf newDirectBuffer(int i, int j) {
        return (ByteBuf) (PlatformDependent.hasUnsafe() ? new UnpooledUnsafeDirectByteBuf(this, i, j) : new UnpooledDirectByteBuf(this, i, j));
    }

    @Override
    public boolean isDirectBufferPooled() {
        return false;
    }
}
