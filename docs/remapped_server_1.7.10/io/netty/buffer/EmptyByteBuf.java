package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public final class EmptyByteBuf extends ByteBuf {

    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocateDirect(0);
    private static final long EMPTY_BYTE_BUFFER_ADDRESS;
    private final ByteBufAllocator alloc;
    private final ByteOrder order;
    private final String str;
    private EmptyByteBuf swapped;

    public EmptyByteBuf(ByteBufAllocator bytebufallocator) {
        this(bytebufallocator, ByteOrder.BIG_ENDIAN);
    }

    private EmptyByteBuf(ByteBufAllocator bytebufallocator, ByteOrder byteorder) {
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else {
            this.alloc = bytebufallocator;
            this.order = byteorder;
            this.str = this.getClass().getSimpleName() + (byteorder == ByteOrder.BIG_ENDIAN ? "BE" : "LE");
        }
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public ByteBuf capacity(int i) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return this.order;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int maxCapacity() {
        return 0;
    }

    @Override
    public ByteBuf order(ByteOrder byteorder) {
        if (byteorder == null) {
            throw new NullPointerException("endianness");
        } else if (byteorder == this.order()) {
            return this;
        } else {
            EmptyByteBuf emptybytebuf = this.swapped;

            if (emptybytebuf != null) {
                return emptybytebuf;
            } else {
                this.swapped = emptybytebuf = new EmptyByteBuf(this.alloc(), byteorder);
                return emptybytebuf;
            }
        }
    }

    @Override
    public int readerIndex() {
        return 0;
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return this.checkIndex(i);
    }

    @Override
    public int writerIndex() {
        return 0;
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return this.checkIndex(i);
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        this.checkIndex(i);
        this.checkIndex(j);
        return this;
    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public int maxWritableBytes() {
        return 0;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public ByteBuf clear() {
        return this;
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + i + " (expected: >= 0)");
        } else if (i != 0) {
            throw new IndexOutOfBoundsException();
        } else {
            return this;
        }
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        if (i < 0) {
            throw new IllegalArgumentException("minWritableBytes: " + i + " (expected: >= 0)");
        } else {
            return i == 0 ? 0 : 1;
        }
    }

    @Override
    public boolean getBoolean(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public byte getByte(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short getUnsignedByte(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short getShort(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getUnsignedShort(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getMedium(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getUnsignedMedium(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getInt(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long getUnsignedInt(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long getLong(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public char getChar(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public float getFloat(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public double getDouble(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.checkIndex(i, bytebuf.writableBytes());
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.checkIndex(i, k);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.checkIndex(i, abyte.length);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.checkIndex(i, k);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.checkIndex(i, bytebuffer.remaining());
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) {
        this.checkIndex(i, j);
        return 0;
    }

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setChar(int i, int j) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setFloat(int i, float f) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setDouble(int i, double d0) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.checkIndex(i, k);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.checkIndex(i, abyte.length);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.checkIndex(i, k);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.checkIndex(i, bytebuffer.remaining());
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) {
        this.checkIndex(i, j);
        return 0;
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) {
        this.checkIndex(i, j);
        return 0;
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public boolean readBoolean() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public byte readByte() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short readUnsignedByte() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public short readShort() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readUnsignedShort() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readMedium() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readUnsignedMedium() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readInt() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long readUnsignedInt() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long readLong() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public char readChar() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public float readFloat() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public double readDouble() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return this.checkLength(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return this.checkLength(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.checkLength(bytebuf.writableBytes());
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.checkLength(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.checkLength(j);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        return this.checkLength(abyte.length);
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.checkLength(j);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.checkLength(bytebuffer.remaining());
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) {
        return this.checkLength(i);
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) {
        this.checkLength(i);
        return 0;
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return this.checkLength(i);
    }

    @Override
    public ByteBuf writeBoolean(boolean flag) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeByte(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeShort(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeMedium(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeInt(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeLong(long i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeChar(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeFloat(float f) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.checkLength(i);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.checkLength(j);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        return this.checkLength(abyte.length);
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.checkLength(j);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.checkLength(bytebuffer.remaining());
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) {
        this.checkLength(i);
        return 0;
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) {
        this.checkLength(i);
        return 0;
    }

    @Override
    public ByteBuf writeZero(int i) {
        return this.checkLength(i);
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        this.checkIndex(i);
        this.checkIndex(j);
        return -1;
    }

    @Override
    public int bytesBefore(byte b0) {
        return -1;
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        this.checkLength(i);
        return -1;
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        this.checkIndex(i, j);
        return -1;
    }

    @Override
    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return -1;
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        this.checkIndex(i, j);
        return -1;
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return -1;
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        this.checkIndex(i, j);
        return -1;
    }

    @Override
    public ByteBuf copy() {
        return this;
    }

    @Override
    public ByteBuf copy(int i, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public ByteBuf slice() {
        return this;
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return this.checkIndex(i, j);
    }

    @Override
    public ByteBuf duplicate() {
        return this;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer() {
        return EmptyByteBuf.EMPTY_BYTE_BUFFER;
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.checkIndex(i, j);
        return this.nioBuffer();
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[]{EmptyByteBuf.EMPTY_BYTE_BUFFER};
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        this.checkIndex(i, j);
        return this.nioBuffers();
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return EmptyByteBuf.EMPTY_BYTE_BUFFER;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return EmptyArrays.EMPTY_BYTES;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return EmptyByteBuf.EMPTY_BYTE_BUFFER_ADDRESS != 0L;
    }

    @Override
    public long memoryAddress() {
        if (this.hasMemoryAddress()) {
            return EmptyByteBuf.EMPTY_BYTE_BUFFER_ADDRESS;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString(Charset charset) {
        return "";
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        this.checkIndex(i, j);
        return this.toString(charset);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ByteBuf && !((ByteBuf) object).isReadable();
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        return bytebuf.isReadable() ? -1 : 0;
    }

    @Override
    public String toString() {
        return this.str;
    }

    @Override
    public boolean isReadable(int i) {
        return false;
    }

    @Override
    public boolean isWritable(int i) {
        return false;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public ByteBuf retain() {
        return this;
    }

    @Override
    public ByteBuf retain(int i) {
        return this;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int i) {
        return false;
    }

    private ByteBuf checkIndex(int i) {
        if (i != 0) {
            throw new IndexOutOfBoundsException();
        } else {
            return this;
        }
    }

    private ByteBuf checkIndex(int i, int j) {
        if (j < 0) {
            throw new IllegalArgumentException("length: " + j);
        } else if (i == 0 && j == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private ByteBuf checkLength(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("length: " + i + " (expected: >= 0)");
        } else if (i != 0) {
            throw new IndexOutOfBoundsException();
        } else {
            return this;
        }
    }

    static {
        long i = 0L;

        try {
            if (PlatformDependent.hasUnsafe()) {
                i = PlatformDependent.directBufferAddress(EmptyByteBuf.EMPTY_BYTE_BUFFER);
            }
        } catch (Throwable throwable) {
            ;
        }

        EMPTY_BYTE_BUFFER_ADDRESS = i;
    }
}
