package io.netty.buffer;

import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class ByteBuf implements ReferenceCounted, Comparable<ByteBuf> {

    public ByteBuf() {}

    public abstract int capacity();

    public abstract ByteBuf capacity(int i);

    public abstract int maxCapacity();

    public abstract ByteBufAllocator alloc();

    public abstract ByteOrder order();

    public abstract ByteBuf order(ByteOrder byteorder);

    public abstract ByteBuf unwrap();

    public abstract boolean isDirect();

    public abstract int readerIndex();

    public abstract ByteBuf readerIndex(int i);

    public abstract int writerIndex();

    public abstract ByteBuf writerIndex(int i);

    public abstract ByteBuf setIndex(int i, int j);

    public abstract int readableBytes();

    public abstract int writableBytes();

    public abstract int maxWritableBytes();

    public abstract boolean isReadable();

    public abstract boolean isReadable(int i);

    public abstract boolean isWritable();

    public abstract boolean isWritable(int i);

    public abstract ByteBuf clear();

    public abstract ByteBuf markReaderIndex();

    public abstract ByteBuf resetReaderIndex();

    public abstract ByteBuf markWriterIndex();

    public abstract ByteBuf resetWriterIndex();

    public abstract ByteBuf discardReadBytes();

    public abstract ByteBuf discardSomeReadBytes();

    public abstract ByteBuf ensureWritable(int i);

    public abstract int ensureWritable(int i, boolean flag);

    public abstract boolean getBoolean(int i);

    public abstract byte getByte(int i);

    public abstract short getUnsignedByte(int i);

    public abstract short getShort(int i);

    public abstract int getUnsignedShort(int i);

    public abstract int getMedium(int i);

    public abstract int getUnsignedMedium(int i);

    public abstract int getInt(int i);

    public abstract long getUnsignedInt(int i);

    public abstract long getLong(int i);

    public abstract char getChar(int i);

    public abstract float getFloat(int i);

    public abstract double getDouble(int i);

    public abstract ByteBuf getBytes(int i, ByteBuf bytebuf);

    public abstract ByteBuf getBytes(int i, ByteBuf bytebuf, int j);

    public abstract ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k);

    public abstract ByteBuf getBytes(int i, byte[] abyte);

    public abstract ByteBuf getBytes(int i, byte[] abyte, int j, int k);

    public abstract ByteBuf getBytes(int i, ByteBuffer bytebuffer);

    public abstract ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException;

    public abstract int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException;

    public abstract ByteBuf setBoolean(int i, boolean flag);

    public abstract ByteBuf setByte(int i, int j);

    public abstract ByteBuf setShort(int i, int j);

    public abstract ByteBuf setMedium(int i, int j);

    public abstract ByteBuf setInt(int i, int j);

    public abstract ByteBuf setLong(int i, long j);

    public abstract ByteBuf setChar(int i, int j);

    public abstract ByteBuf setFloat(int i, float f);

    public abstract ByteBuf setDouble(int i, double d0);

    public abstract ByteBuf setBytes(int i, ByteBuf bytebuf);

    public abstract ByteBuf setBytes(int i, ByteBuf bytebuf, int j);

    public abstract ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k);

    public abstract ByteBuf setBytes(int i, byte[] abyte);

    public abstract ByteBuf setBytes(int i, byte[] abyte, int j, int k);

    public abstract ByteBuf setBytes(int i, ByteBuffer bytebuffer);

    public abstract int setBytes(int i, InputStream inputstream, int j) throws IOException;

    public abstract int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException;

    public abstract ByteBuf setZero(int i, int j);

    public abstract boolean readBoolean();

    public abstract byte readByte();

    public abstract short readUnsignedByte();

    public abstract short readShort();

    public abstract int readUnsignedShort();

    public abstract int readMedium();

    public abstract int readUnsignedMedium();

    public abstract int readInt();

    public abstract long readUnsignedInt();

    public abstract long readLong();

    public abstract char readChar();

    public abstract float readFloat();

    public abstract double readDouble();

    public abstract ByteBuf readBytes(int i);

    public abstract ByteBuf readSlice(int i);

    public abstract ByteBuf readBytes(ByteBuf bytebuf);

    public abstract ByteBuf readBytes(ByteBuf bytebuf, int i);

    public abstract ByteBuf readBytes(ByteBuf bytebuf, int i, int j);

    public abstract ByteBuf readBytes(byte[] abyte);

    public abstract ByteBuf readBytes(byte[] abyte, int i, int j);

    public abstract ByteBuf readBytes(ByteBuffer bytebuffer);

    public abstract ByteBuf readBytes(OutputStream outputstream, int i) throws IOException;

    public abstract int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException;

    public abstract ByteBuf skipBytes(int i);

    public abstract ByteBuf writeBoolean(boolean flag);

    public abstract ByteBuf writeByte(int i);

    public abstract ByteBuf writeShort(int i);

    public abstract ByteBuf writeMedium(int i);

    public abstract ByteBuf writeInt(int i);

    public abstract ByteBuf writeLong(long i);

    public abstract ByteBuf writeChar(int i);

    public abstract ByteBuf writeFloat(float f);

    public abstract ByteBuf writeDouble(double d0);

    public abstract ByteBuf writeBytes(ByteBuf bytebuf);

    public abstract ByteBuf writeBytes(ByteBuf bytebuf, int i);

    public abstract ByteBuf writeBytes(ByteBuf bytebuf, int i, int j);

    public abstract ByteBuf writeBytes(byte[] abyte);

    public abstract ByteBuf writeBytes(byte[] abyte, int i, int j);

    public abstract ByteBuf writeBytes(ByteBuffer bytebuffer);

    public abstract int writeBytes(InputStream inputstream, int i) throws IOException;

    public abstract int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException;

    public abstract ByteBuf writeZero(int i);

    public abstract int indexOf(int i, int j, byte b0);

    public abstract int bytesBefore(byte b0);

    public abstract int bytesBefore(int i, byte b0);

    public abstract int bytesBefore(int i, int j, byte b0);

    public abstract int forEachByte(ByteBufProcessor bytebufprocessor);

    public abstract int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor);

    public abstract int forEachByteDesc(ByteBufProcessor bytebufprocessor);

    public abstract int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor);

    public abstract ByteBuf copy();

    public abstract ByteBuf copy(int i, int j);

    public abstract ByteBuf slice();

    public abstract ByteBuf slice(int i, int j);

    public abstract ByteBuf duplicate();

    public abstract int nioBufferCount();

    public abstract ByteBuffer nioBuffer();

    public abstract ByteBuffer nioBuffer(int i, int j);

    public abstract ByteBuffer internalNioBuffer(int i, int j);

    public abstract ByteBuffer[] nioBuffers();

    public abstract ByteBuffer[] nioBuffers(int i, int j);

    public abstract boolean hasArray();

    public abstract byte[] array();

    public abstract int arrayOffset();

    public abstract boolean hasMemoryAddress();

    public abstract long memoryAddress();

    public abstract String toString(Charset charset);

    public abstract String toString(int i, int j, Charset charset);

    public abstract int hashCode();

    public abstract boolean equals(Object object);

    public abstract int compareTo(ByteBuf bytebuf);

    public abstract String toString();

    @Override
    public abstract ByteBuf retain(int i);

    @Override
    public abstract ByteBuf retain();
}
