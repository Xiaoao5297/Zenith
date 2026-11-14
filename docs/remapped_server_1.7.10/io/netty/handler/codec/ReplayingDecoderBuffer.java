package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.Signal;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class ReplayingDecoderBuffer extends ByteBuf {

    private static final Signal REPLAY = ReplayingDecoder.REPLAY;
    private ByteBuf buffer;
    private boolean terminated;
    private SwappedByteBuf swapped;
    static final ReplayingDecoderBuffer EMPTY_BUFFER = new ReplayingDecoderBuffer(Unpooled.EMPTY_BUFFER);

    ReplayingDecoderBuffer() {}

    ReplayingDecoderBuffer(ByteBuf bytebuf) {
        this.setCumulation(bytebuf);
    }

    void setCumulation(ByteBuf bytebuf) {
        this.buffer = bytebuf;
    }

    void terminate() {
        this.terminated = true;
    }

    @Override
    public int capacity() {
        return this.terminated ? this.buffer.capacity() : Integer.MAX_VALUE;
    }

    @Override
    public ByteBuf capacity(int i) {
        reject();
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.capacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buffer.alloc();
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuf clear() {
        reject();
        return this;
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf copy() {
        reject();
        return this;
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.copy(i, j);
    }

    @Override
    public ByteBuf discardReadBytes() {
        reject();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        reject();
        return this;
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf duplicate() {
        reject();
        return this;
    }

    @Override
    public boolean getBoolean(int i) {
        this.checkIndex(i, 1);
        return this.buffer.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        this.checkIndex(i, 1);
        return this.buffer.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        this.checkIndex(i, 1);
        return this.buffer.getUnsignedByte(i);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.getBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        this.checkIndex(i, abyte.length);
        this.buffer.getBytes(i, abyte);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        reject();
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.getBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        reject();
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) {
        reject();
        return this;
    }

    @Override
    public int getInt(int i) {
        this.checkIndex(i, 4);
        return this.buffer.getInt(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        this.checkIndex(i, 4);
        return this.buffer.getUnsignedInt(i);
    }

    @Override
    public long getLong(int i) {
        this.checkIndex(i, 8);
        return this.buffer.getLong(i);
    }

    @Override
    public int getMedium(int i) {
        this.checkIndex(i, 3);
        return this.buffer.getMedium(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        this.checkIndex(i, 3);
        return this.buffer.getUnsignedMedium(i);
    }

    @Override
    public short getShort(int i) {
        this.checkIndex(i, 2);
        return this.buffer.getShort(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        this.checkIndex(i, 2);
        return this.buffer.getUnsignedShort(i);
    }

    @Override
    public char getChar(int i) {
        this.checkIndex(i, 2);
        return this.buffer.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        this.checkIndex(i, 4);
        return this.buffer.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        this.checkIndex(i, 8);
        return this.buffer.getDouble(i);
    }

    @Override
    public int hashCode() {
        reject();
        return 0;
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        int k = this.buffer.indexOf(i, j, b0);

        if (k < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return k;
        }
    }

    @Override
    public int bytesBefore(byte b0) {
        int i = this.buffer.bytesBefore(b0);

        if (i < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return i;
        }
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        this.checkReadableBytes(i);
        int j = this.buffer.bytesBefore(i, b0);

        if (j < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return j;
        }
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        int k = this.buffer.bytesBefore(i, j, b0);

        if (k < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return k;
        }
    }

    @Override
    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        int i = this.buffer.forEachByte(bytebufprocessor);

        if (!this.terminated && i < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return i;
        }
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        int k = this.buffer.writerIndex();

        if (i >= k) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else if (i + j <= k) {
            return this.buffer.forEachByte(i, j, bytebufprocessor);
        } else {
            int l = this.buffer.forEachByte(i, k - i, bytebufprocessor);

            if (l < 0) {
                throw ReplayingDecoderBuffer.REPLAY;
            } else {
                return l;
            }
        }
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        if (this.terminated) {
            return this.buffer.forEachByteDesc(bytebufprocessor);
        } else {
            reject();
            return 0;
        }
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        if (i + j > this.buffer.writerIndex()) {
            throw ReplayingDecoderBuffer.REPLAY;
        } else {
            return this.buffer.forEachByteDesc(i, j, bytebufprocessor);
        }
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.buffer.markReaderIndex();
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        reject();
        return this;
    }

    @Override
    public ByteOrder order() {
        return this.buffer.order();
    }

    @Override
    public ByteBuf order(ByteOrder byteorder) {
        if (byteorder == null) {
            throw new NullPointerException("endianness");
        } else if (byteorder == this.order()) {
            return this;
        } else {
            SwappedByteBuf swappedbytebuf = this.swapped;

            if (swappedbytebuf == null) {
                this.swapped = swappedbytebuf = new SwappedByteBuf(this);
            }

            return swappedbytebuf;
        }
    }

    @Override
    public boolean isReadable() {
        return this.terminated ? this.buffer.isReadable() : true;
    }

    @Override
    public boolean isReadable(int i) {
        return this.terminated ? this.buffer.isReadable(i) : true;
    }

    @Override
    public int readableBytes() {
        return this.terminated ? this.buffer.readableBytes() : Integer.MAX_VALUE - this.buffer.readerIndex();
    }

    @Override
    public boolean readBoolean() {
        this.checkReadableBytes(1);
        return this.buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        this.checkReadableBytes(1);
        return this.buffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        this.checkReadableBytes(1);
        return this.buffer.readUnsignedByte();
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        this.checkReadableBytes(j);
        this.buffer.readBytes(abyte, i, j);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        this.checkReadableBytes(abyte.length);
        this.buffer.readBytes(abyte);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        reject();
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        this.checkReadableBytes(j);
        this.buffer.readBytes(bytebuf, i, j);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        this.checkReadableBytes(bytebuf.writableBytes());
        this.buffer.readBytes(bytebuf);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf readBytes(int i) {
        this.checkReadableBytes(i);
        return this.buffer.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        this.checkReadableBytes(i);
        return this.buffer.readSlice(i);
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) {
        reject();
        return this;
    }

    @Override
    public int readerIndex() {
        return this.buffer.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        this.buffer.readerIndex(i);
        return this;
    }

    @Override
    public int readInt() {
        this.checkReadableBytes(4);
        return this.buffer.readInt();
    }

    @Override
    public long readUnsignedInt() {
        this.checkReadableBytes(4);
        return this.buffer.readUnsignedInt();
    }

    @Override
    public long readLong() {
        this.checkReadableBytes(8);
        return this.buffer.readLong();
    }

    @Override
    public int readMedium() {
        this.checkReadableBytes(3);
        return this.buffer.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        this.checkReadableBytes(3);
        return this.buffer.readUnsignedMedium();
    }

    @Override
    public short readShort() {
        this.checkReadableBytes(2);
        return this.buffer.readShort();
    }

    @Override
    public int readUnsignedShort() {
        this.checkReadableBytes(2);
        return this.buffer.readUnsignedShort();
    }

    @Override
    public char readChar() {
        this.checkReadableBytes(2);
        return this.buffer.readChar();
    }

    @Override
    public float readFloat() {
        this.checkReadableBytes(4);
        return this.buffer.readFloat();
    }

    @Override
    public double readDouble() {
        this.checkReadableBytes(8);
        return this.buffer.readDouble();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.buffer.resetReaderIndex();
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        reject();
        return this;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        reject();
        return this;
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setChar(int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setFloat(int i, float f) {
        reject();
        return this;
    }

    @Override
    public ByteBuf setDouble(int i, double d0) {
        reject();
        return this;
    }

    @Override
    public ByteBuf skipBytes(int i) {
        this.checkReadableBytes(i);
        this.buffer.skipBytes(i);
        return this;
    }

    @Override
    public ByteBuf slice() {
        reject();
        return this;
    }

    @Override
    public ByteBuf slice(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.slice(i, j);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        reject();
        return null;
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        reject();
        return null;
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.nioBuffers(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.internalNioBuffer(i, j);
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        this.checkIndex(i, j);
        return this.buffer.toString(i, j, charset);
    }

    @Override
    public String toString(Charset charset) {
        reject();
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + "ridx=" + this.readerIndex() + ", " + "widx=" + this.writerIndex() + ')';
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int i) {
        return false;
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
    public ByteBuf writeBoolean(boolean flag) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeByte(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        reject();
        return this;
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) {
        reject();
        return 0;
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) {
        reject();
        return 0;
    }

    @Override
    public ByteBuf writeInt(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeLong(long i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeMedium(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeZero(int i) {
        reject();
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buffer.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeShort(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeChar(int i) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeFloat(float f) {
        reject();
        return this;
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        reject();
        return this;
    }

    private void checkIndex(int i, int j) {
        if (i + j > this.buffer.writerIndex()) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
    }

    private void checkReadableBytes(int i) {
        if (this.buffer.readableBytes() < i) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        reject();
        return this;
    }

    @Override
    public int refCnt() {
        return this.buffer.refCnt();
    }

    @Override
    public ByteBuf retain() {
        reject();
        return this;
    }

    @Override
    public ByteBuf retain(int i) {
        reject();
        return this;
    }

    @Override
    public boolean release() {
        reject();
        return false;
    }

    @Override
    public boolean release(int i) {
        reject();
        return false;
    }

    @Override
    public ByteBuf unwrap() {
        reject();
        return this;
    }

    private static void reject() {
        throw new UnsupportedOperationException("not a replayable operation");
    }

    static {
        ReplayingDecoderBuffer.EMPTY_BUFFER.terminate();
    }
}
