package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class UnreleasableByteBuf extends ByteBuf {

    private final ByteBuf buf;
    private SwappedByteBuf swappedBuf;

    UnreleasableByteBuf(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException("buf");
        } else {
            this.buf = bytebuf;
        }
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        this.buf.capacity(i);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder byteorder) {
        if (byteorder == null) {
            throw new NullPointerException("endianness");
        } else if (byteorder == this.order()) {
            return this;
        } else {
            SwappedByteBuf swappedbytebuf = this.swappedBuf;

            if (swappedbytebuf == null) {
                this.swappedBuf = swappedbytebuf = new SwappedByteBuf(this);
            }

            return swappedbytebuf;
        }
    }

    @Override
    public ByteBuf unwrap() {
        return this.buf;
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        this.buf.readerIndex(i);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        this.buf.writerIndex(i);
        return this;
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        this.buf.setIndex(i, j);
        return this;
    }

    @Override
    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public ByteBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        this.buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        this.buf.ensureWritable(i);
        return this;
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        return this.buf.ensureWritable(i, flag);
    }

    @Override
    public boolean getBoolean(int i) {
        return this.buf.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        return this.buf.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        return this.buf.getUnsignedByte(i);
    }

    @Override
    public short getShort(int i) {
        return this.buf.getShort(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        return this.buf.getUnsignedShort(i);
    }

    @Override
    public int getMedium(int i) {
        return this.buf.getMedium(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return this.buf.getUnsignedMedium(i);
    }

    @Override
    public int getInt(int i) {
        return this.buf.getInt(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        return this.buf.getUnsignedInt(i);
    }

    @Override
    public long getLong(int i) {
        return this.buf.getLong(i);
    }

    @Override
    public char getChar(int i) {
        return this.buf.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        return this.buf.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return this.buf.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        this.buf.getBytes(i, bytebuf);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        this.buf.getBytes(i, bytebuf, j);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.buf.getBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        this.buf.getBytes(i, abyte);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.buf.getBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.buf.getBytes(i, bytebuffer);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.buf.getBytes(i, outputstream, j);
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.buf.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        this.buf.setBoolean(i, flag);
        return this;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        this.buf.setByte(i, j);
        return this;
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        this.buf.setShort(i, j);
        return this;
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        this.buf.setMedium(i, j);
        return this;
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        this.buf.setInt(i, j);
        return this;
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        this.buf.setLong(i, j);
        return this;
    }

    @Override
    public ByteBuf setChar(int i, int j) {
        this.buf.setChar(i, j);
        return this;
    }

    @Override
    public ByteBuf setFloat(int i, float f) {
        this.buf.setFloat(i, f);
        return this;
    }

    @Override
    public ByteBuf setDouble(int i, double d0) {
        this.buf.setDouble(i, d0);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        this.buf.setBytes(i, bytebuf);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        this.buf.setBytes(i, bytebuf, j);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.buf.setBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        this.buf.setBytes(i, abyte);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.buf.setBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.buf.setBytes(i, bytebuffer);
        return this;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.buf.setBytes(i, inputstream, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.buf.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        this.buf.setZero(i, j);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return this.buf.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return new UnreleasableByteBuf(this.buf.readSlice(i));
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        this.buf.readBytes(bytebuf);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        this.buf.readBytes(bytebuf, i);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        this.buf.readBytes(bytebuf, i, j);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        this.buf.readBytes(abyte);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        this.buf.readBytes(abyte, i, j);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        this.buf.readBytes(bytebuffer);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        this.buf.readBytes(outputstream, i);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return this.buf.readBytes(gatheringbytechannel, i);
    }

    @Override
    public ByteBuf skipBytes(int i) {
        this.buf.skipBytes(i);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean flag) {
        this.buf.writeBoolean(flag);
        return this;
    }

    @Override
    public ByteBuf writeByte(int i) {
        this.buf.writeByte(i);
        return this;
    }

    @Override
    public ByteBuf writeShort(int i) {
        this.buf.writeShort(i);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int i) {
        this.buf.writeMedium(i);
        return this;
    }

    @Override
    public ByteBuf writeInt(int i) {
        this.buf.writeInt(i);
        return this;
    }

    @Override
    public ByteBuf writeLong(long i) {
        this.buf.writeLong(i);
        return this;
    }

    @Override
    public ByteBuf writeChar(int i) {
        this.buf.writeChar(i);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float f) {
        this.buf.writeFloat(f);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        this.buf.writeDouble(d0);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        this.buf.writeBytes(bytebuf);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        this.buf.writeBytes(bytebuf, i);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        this.buf.writeBytes(bytebuf, i, j);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        this.buf.writeBytes(abyte);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        this.buf.writeBytes(abyte, i, j);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        this.buf.writeBytes(bytebuffer);
        return this;
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return this.buf.writeBytes(inputstream, i);
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return this.buf.writeBytes(scatteringbytechannel, i);
    }

    @Override
    public ByteBuf writeZero(int i) {
        this.buf.writeZero(i);
        return this;
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        return this.buf.indexOf(i, j, b0);
    }

    @Override
    public int bytesBefore(byte b0) {
        return this.buf.bytesBefore(b0);
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        return this.buf.bytesBefore(i, b0);
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        return this.buf.bytesBefore(i, j, b0);
    }

    @Override
    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return this.buf.forEachByte(bytebufprocessor);
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.buf.forEachByte(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return this.buf.forEachByteDesc(bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.buf.forEachByteDesc(i, j, bytebufprocessor);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int i, int j) {
        return this.buf.copy(i, j);
    }

    @Override
    public ByteBuf slice() {
        return new UnreleasableByteBuf(this.buf.slice());
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return new UnreleasableByteBuf(this.buf.slice(i, j));
    }

    @Override
    public ByteBuf duplicate() {
        return new UnreleasableByteBuf(this.buf.duplicate());
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return this.buf.nioBuffer(i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.buf.nioBuffers(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.buf.internalNioBuffer(i, j);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public String toString(Charset charset) {
        return this.buf.toString(charset);
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        return this.buf.toString(i, j, charset);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.buf.equals(object);
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        return this.buf.compareTo(bytebuf);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName((Object) this) + '(' + this.buf.toString() + ')';
    }

    @Override
    public ByteBuf retain(int i) {
        return this;
    }

    @Override
    public ByteBuf retain() {
        return this;
    }

    @Override
    public boolean isReadable(int i) {
        return this.buf.isReadable(i);
    }

    @Override
    public boolean isWritable(int i) {
        return this.buf.isWritable(i);
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int i) {
        return false;
    }
}
