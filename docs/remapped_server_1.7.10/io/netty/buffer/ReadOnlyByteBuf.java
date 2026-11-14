package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class ReadOnlyByteBuf extends AbstractDerivedByteBuf {

    private final ByteBuf buffer;

    public ReadOnlyByteBuf(ByteBuf bytebuf) {
        super(bytebuf.maxCapacity());
        if (!(bytebuf instanceof ReadOnlyByteBuf) && !(bytebuf instanceof DuplicatedByteBuf)) {
            this.buffer = bytebuf;
        } else {
            this.buffer = bytebuf.unwrap();
        }

        this.setIndex(bytebuf.readerIndex(), bytebuf.writerIndex());
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
    public ByteBuf unwrap() {
        return this.buffer;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buffer.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.buffer.order();
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
        throw new ReadOnlyBufferException();
    }

    @Override
    public int arrayOffset() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf discardReadBytes() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setByte(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int i, long j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.buffer.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.buffer.getBytes(i, outputstream, j);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.buffer.getBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.buffer.getBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.buffer.getBytes(i, bytebuffer);
        return this;
    }

    @Override
    public ByteBuf duplicate() {
        return new ReadOnlyByteBuf(this);
    }

    @Override
    public ByteBuf copy(int i, int j) {
        return this.buffer.copy(i, j);
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return Unpooled.unmodifiableBuffer(this.buffer.slice(i, j));
    }

    @Override
    public byte getByte(int i) {
        return this._getByte(i);
    }

    @Override
    protected byte _getByte(int i) {
        return this.buffer.getByte(i);
    }

    @Override
    public short getShort(int i) {
        return this._getShort(i);
    }

    @Override
    protected short _getShort(int i) {
        return this.buffer.getShort(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return this._getUnsignedMedium(i);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        return this.buffer.getUnsignedMedium(i);
    }

    @Override
    public int getInt(int i) {
        return this._getInt(i);
    }

    @Override
    protected int _getInt(int i) {
        return this.buffer.getInt(i);
    }

    @Override
    public long getLong(int i) {
        return this._getLong(i);
    }

    @Override
    protected long _getLong(int i) {
        return this.buffer.getLong(i);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return this.buffer.nioBuffer(i, j).asReadOnlyBuffer();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.buffer.nioBuffers(i, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.nioBuffer(i, j);
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.buffer.forEachByte(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.buffer.forEachByteDesc(i, j, bytebufprocessor);
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        throw new ReadOnlyBufferException();
    }
}
