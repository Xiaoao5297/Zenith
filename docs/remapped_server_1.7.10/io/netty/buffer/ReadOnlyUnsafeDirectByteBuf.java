package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class ReadOnlyUnsafeDirectByteBuf extends ReadOnlyByteBufferBuf {

    private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private final long memoryAddress;

    public ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator bytebufallocator, ByteBuffer bytebuffer) {
        super(bytebufallocator, bytebuffer);
        this.memoryAddress = PlatformDependent.directBufferAddress(bytebuffer);
    }

    @Override
    protected byte _getByte(int i) {
        return PlatformDependent.getByte(this.addr(i));
    }

    @Override
    protected short _getShort(int i) {
        short short0 = PlatformDependent.getShort(this.addr(i));

        return ReadOnlyUnsafeDirectByteBuf.NATIVE_ORDER ? short0 : Short.reverseBytes(short0);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        long j = this.addr(i);

        return (PlatformDependent.getByte(j) & 255) << 16 | (PlatformDependent.getByte(j + 1L) & 255) << 8 | PlatformDependent.getByte(j + 2L) & 255;
    }

    @Override
    protected int _getInt(int i) {
        int j = PlatformDependent.getInt(this.addr(i));

        return ReadOnlyUnsafeDirectByteBuf.NATIVE_ORDER ? j : Integer.reverseBytes(j);
    }

    @Override
    protected long _getLong(int i) {
        long j = PlatformDependent.getLong(this.addr(i));

        return ReadOnlyUnsafeDirectByteBuf.NATIVE_ORDER ? j : Long.reverseBytes(j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        if (bytebuf == null) {
            throw new NullPointerException("dst");
        } else if (j >= 0 && j <= bytebuf.capacity() - k) {
            if (bytebuf.hasMemoryAddress()) {
                PlatformDependent.copyMemory(this.addr(i), bytebuf.memoryAddress() + (long) j, (long) k);
            } else if (bytebuf.hasArray()) {
                PlatformDependent.copyMemory(this.addr(i), bytebuf.array(), bytebuf.arrayOffset() + j, (long) k);
            } else {
                bytebuf.setBytes(j, (ByteBuf) this, i, k);
            }

            return this;
        } else {
            throw new IndexOutOfBoundsException("dstIndex: " + j);
        }
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        if (abyte == null) {
            throw new NullPointerException("dst");
        } else if (j >= 0 && j <= abyte.length - k) {
            if (k != 0) {
                PlatformDependent.copyMemory(this.addr(i), abyte, j, (long) k);
            }

            return this;
        } else {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", j, k, abyte.length));
        }
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.checkIndex(i);
        if (bytebuffer == null) {
            throw new NullPointerException("dst");
        } else {
            int j = Math.min(this.capacity() - i, bytebuffer.remaining());
            ByteBuffer bytebuffer1 = this.internalNioBuffer();

            bytebuffer1.clear().position(i).limit(i + j);
            bytebuffer.put(bytebuffer1);
            return this;
        }
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        UnpooledUnsafeDirectByteBuf unpooledunsafedirectbytebuf = (UnpooledUnsafeDirectByteBuf) this.alloc().directBuffer(j, this.maxCapacity());

        if (j != 0) {
            PlatformDependent.copyMemory(this.addr(i), unpooledunsafedirectbytebuf.addr(0), (long) j);
            unpooledunsafedirectbytebuf.setIndex(0, j);
        }

        return unpooledunsafedirectbytebuf;
    }

    private long addr(int i) {
        return this.memoryAddress + (long) i;
    }
}
