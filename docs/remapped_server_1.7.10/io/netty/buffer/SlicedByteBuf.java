package io.netty.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class SlicedByteBuf extends AbstractDerivedByteBuf {

    private final ByteBuf buffer;
    private final int adjustment;
    private final int length;

    public SlicedByteBuf(ByteBuf bytebuf, int i, int j) {
        super(j);
        if (i >= 0 && i <= bytebuf.capacity() - j) {
            if (bytebuf instanceof SlicedByteBuf) {
                this.buffer = ((SlicedByteBuf) bytebuf).buffer;
                this.adjustment = ((SlicedByteBuf) bytebuf).adjustment + i;
            } else if (bytebuf instanceof DuplicatedByteBuf) {
                this.buffer = bytebuf.unwrap();
                this.adjustment = i;
            } else {
                this.buffer = bytebuf;
                this.adjustment = i;
            }

            this.length = j;
            this.writerIndex(j);
        } else {
            throw new IndexOutOfBoundsException(bytebuf.toString() + ".slice(" + i + ", " + j + ')');
        }
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
        return this.length;
    }

    @Override
    public ByteBuf capacity(int i) {
        throw new UnsupportedOperationException("sliced buffer");
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
        return this.buffer.arrayOffset() + this.adjustment;
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buffer.memoryAddress() + (long) this.adjustment;
    }

    @Override
    protected byte _getByte(int i) {
        return this.buffer.getByte(i + this.adjustment);
    }

    @Override
    protected short _getShort(int i) {
        return this.buffer.getShort(i + this.adjustment);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        return this.buffer.getUnsignedMedium(i + this.adjustment);
    }

    @Override
    protected int _getInt(int i) {
        return this.buffer.getInt(i + this.adjustment);
    }

    @Override
    protected long _getLong(int i) {
        return this.buffer.getLong(i + this.adjustment);
    }

    @Override
    public ByteBuf duplicate() {
        ByteBuf bytebuf = this.buffer.slice(this.adjustment, this.length);

        bytebuf.setIndex(this.readerIndex(), this.writerIndex());
        return bytebuf;
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.copy(i + this.adjustment, j);
    }

    @Override
    public ByteBuf slice(int i, int j) {
        this.checkIndex(i, j);
        return j == 0 ? Unpooled.EMPTY_BUFFER : this.buffer.slice(i + this.adjustment, j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.getBytes(i + this.adjustment, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.getBytes(i + this.adjustment, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.checkIndex(i, bytebuffer.remaining());
        this.buffer.getBytes(i + this.adjustment, bytebuffer);
        return this;
    }

    @Override
    protected void _setByte(int i, int j) {
        this.buffer.setByte(i + this.adjustment, j);
    }

    @Override
    protected void _setShort(int i, int j) {
        this.buffer.setShort(i + this.adjustment, j);
    }

    @Override
    protected void _setMedium(int i, int j) {
        this.buffer.setMedium(i + this.adjustment, j);
    }

    @Override
    protected void _setInt(int i, int j) {
        this.buffer.setInt(i + this.adjustment, j);
    }

    @Override
    protected void _setLong(int i, long j) {
        this.buffer.setLong(i + this.adjustment, j);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.setBytes(i + this.adjustment, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        this.buffer.setBytes(i + this.adjustment, bytebuf, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.checkIndex(i, bytebuffer.remaining());
        this.buffer.setBytes(i + this.adjustment, bytebuffer);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.checkIndex(i, j);
        this.buffer.getBytes(i + this.adjustment, outputstream, j);
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        this.checkIndex(i, j);
        return this.buffer.getBytes(i + this.adjustment, gatheringbytechannel, j);
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.checkIndex(i, j);
        return this.buffer.setBytes(i + this.adjustment, inputstream, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.checkIndex(i, j);
        return this.buffer.setBytes(i + this.adjustment, scatteringbytechannel, j);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.nioBuffer(i + this.adjustment, j);
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        this.checkIndex(i, j);
        return this.buffer.nioBuffers(i + this.adjustment, j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        this.checkIndex(i, j);
        return this.nioBuffer(i, j);
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        int k = this.buffer.forEachByte(i + this.adjustment, j, bytebufprocessor);

        return k >= this.adjustment ? k - this.adjustment : -1;
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        int k = this.buffer.forEachByteDesc(i + this.adjustment, j, bytebufprocessor);

        return k >= this.adjustment ? k - this.adjustment : -1;
    }
}
