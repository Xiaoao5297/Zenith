package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class DuplicatedByteBuf extends AbstractDerivedByteBuf {

    private final ByteBuf buffer;

    public DuplicatedByteBuf(ByteBuf bytebuf) {
        super(bytebuf.maxCapacity());
        if (bytebuf instanceof DuplicatedByteBuf) {
            this.buffer = ((DuplicatedByteBuf) bytebuf).buffer;
        } else {
            this.buffer = bytebuf;
        }

        this.setIndex(bytebuf.readerIndex(), bytebuf.writerIndex());
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
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        this.buffer.capacity(i);
        return this;
    }

    @Override
    public boolean hasArray() {
        return this.buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buffer.memoryAddress();
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
    public ByteBuf copy(int i, int j) {
        return this.buffer.copy(i, j);
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return this.buffer.slice(i, j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.buffer.getBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.buffer.getBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.buffer.getBytes(i, bytebuffer);
        return this;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        this._setByte(i, j);
        return this;
    }

    @Override
    protected void _setByte(int i, int j) {
        this.buffer.setByte(i, j);
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        this._setShort(i, j);
        return this;
    }

    @Override
    protected void _setShort(int i, int j) {
        this.buffer.setShort(i, j);
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        this._setMedium(i, j);
        return this;
    }

    @Override
    protected void _setMedium(int i, int j) {
        this.buffer.setMedium(i, j);
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        this._setInt(i, j);
        return this;
    }

    @Override
    protected void _setInt(int i, int j) {
        this.buffer.setInt(i, j);
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        this._setLong(i, j);
        return this;
    }

    @Override
    protected void _setLong(int i, long j) {
        this.buffer.setLong(i, j);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.buffer.setBytes(i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.buffer.setBytes(i, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.buffer.setBytes(i, bytebuffer);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.buffer.getBytes(i, outputstream, j);
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.buffer.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.buffer.setBytes(i, inputstream, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.buffer.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
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
}
