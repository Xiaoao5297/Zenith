package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

public abstract class AbstractByteBufAllocator implements ByteBufAllocator {

    private static final int DEFAULT_INITIAL_CAPACITY = 256;
    private static final int DEFAULT_MAX_COMPONENTS = 16;
    private final boolean directByDefault;
    private final ByteBuf emptyBuf;

    protected AbstractByteBufAllocator() {
        this(false);
    }

    protected AbstractByteBufAllocator(boolean flag) {
        this.directByDefault = flag && PlatformDependent.hasUnsafe();
        this.emptyBuf = new EmptyByteBuf(this);
    }

    @Override
    public ByteBuf buffer() {
        return this.directByDefault ? this.directBuffer() : this.heapBuffer();
    }

    @Override
    public ByteBuf buffer(int i) {
        return this.directByDefault ? this.directBuffer(i) : this.heapBuffer(i);
    }

    @Override
    public ByteBuf buffer(int i, int j) {
        return this.directByDefault ? this.directBuffer(i, j) : this.heapBuffer(i, j);
    }

    @Override
    public ByteBuf ioBuffer() {
        return PlatformDependent.hasUnsafe() ? this.directBuffer(0) : this.heapBuffer(0);
    }

    @Override
    public ByteBuf ioBuffer(int i) {
        return PlatformDependent.hasUnsafe() ? this.directBuffer(i) : this.heapBuffer(i);
    }

    @Override
    public ByteBuf ioBuffer(int i, int j) {
        return PlatformDependent.hasUnsafe() ? this.directBuffer(i, j) : this.heapBuffer(i, j);
    }

    @Override
    public ByteBuf heapBuffer() {
        return this.heapBuffer(256, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int i) {
        return this.heapBuffer(i, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int i, int j) {
        if (i == 0 && j == 0) {
            return this.emptyBuf;
        } else {
            validate(i, j);
            return this.newHeapBuffer(i, j);
        }
    }

    @Override
    public ByteBuf directBuffer() {
        return this.directBuffer(256, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int i) {
        return this.directBuffer(i, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int i, int j) {
        if (i == 0 && j == 0) {
            return this.emptyBuf;
        } else {
            validate(i, j);
            return this.newDirectBuffer(i, j);
        }
    }

    @Override
    public CompositeByteBuf compositeBuffer() {
        return this.directByDefault ? this.compositeDirectBuffer() : this.compositeHeapBuffer();
    }

    @Override
    public CompositeByteBuf compositeBuffer(int i) {
        return this.directByDefault ? this.compositeDirectBuffer(i) : this.compositeHeapBuffer(i);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        return this.compositeHeapBuffer(16);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int i) {
        return new CompositeByteBuf(this, false, i);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer() {
        return this.compositeDirectBuffer(16);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int i) {
        return new CompositeByteBuf(this, true, i);
    }

    private static void validate(int i, int j) {
        if (i < 0) {
            throw new IllegalArgumentException("initialCapacity: " + i + " (expectd: 0+)");
        } else if (i > j) {
            throw new IllegalArgumentException(String.format("initialCapacity: %d (expected: not greater than maxCapacity(%d)", i, j));
        }
    }

    protected abstract ByteBuf newHeapBuffer(int i, int j);

    protected abstract ByteBuf newDirectBuffer(int i, int j);
}
