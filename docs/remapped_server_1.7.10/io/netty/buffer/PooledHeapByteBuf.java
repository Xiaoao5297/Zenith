package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledHeapByteBuf extends PooledByteBuf<byte[]> {

    private static final Recycler<PooledHeapByteBuf> RECYCLER = new Recycler<PooledHeapByteBuf>() {
        @Override
        protected PooledHeapByteBuf newObject(Recycler.Handle recycler_handle) {
            return new PooledHeapByteBuf(recycler_handle, 0);
        }
    };

    static PooledHeapByteBuf newInstance(int i) {
        PooledHeapByteBuf pooledheapbytebuf = (PooledHeapByteBuf) PooledHeapByteBuf.RECYCLER.get();

        pooledheapbytebuf.setRefCnt(1);
        pooledheapbytebuf.maxCapacity(i);
        return pooledheapbytebuf;
    }

    private PooledHeapByteBuf(Recycler.Handle recycler_handle, int i) {
        super(recycler_handle, i);
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    protected byte _getByte(int i) {
        return ((byte[]) this.memory)[this.idx(i)];
    }

    @Override
    protected short _getShort(int i) {
        i = this.idx(i);
        return (short) (((byte[]) this.memory)[i] << 8 | ((byte[]) this.memory)[i + 1] & 255);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        i = this.idx(i);
        return (((byte[]) this.memory)[i] & 255) << 16 | (((byte[]) this.memory)[i + 1] & 255) << 8 | ((byte[]) this.memory)[i + 2] & 255;
    }

    @Override
    protected int _getInt(int i) {
        i = this.idx(i);
        return (((byte[]) this.memory)[i] & 255) << 24 | (((byte[]) this.memory)[i + 1] & 255) << 16 | (((byte[]) this.memory)[i + 2] & 255) << 8 | ((byte[]) this.memory)[i + 3] & 255;
    }

    @Override
    protected long _getLong(int i) {
        i = this.idx(i);
        return ((long) ((byte[]) this.memory)[i] & 255L) << 56 | ((long) ((byte[]) this.memory)[i + 1] & 255L) << 48 | ((long) ((byte[]) this.memory)[i + 2] & 255L) << 40 | ((long) ((byte[]) this.memory)[i + 3] & 255L) << 32 | ((long) ((byte[]) this.memory)[i + 4] & 255L) << 24 | ((long) ((byte[]) this.memory)[i + 5] & 255L) << 16 | ((long) ((byte[]) this.memory)[i + 6] & 255L) << 8 | (long) ((byte[]) this.memory)[i + 7] & 255L;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkDstIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasMemoryAddress()) {
            PlatformDependent.copyMemory((byte[]) this.memory, this.idx(i), bytebuf.memoryAddress() + (long) j, (long) k);
        } else if (bytebuf.hasArray()) {
            this.getBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
        } else {
            bytebuf.setBytes(j, (byte[]) this.memory, this.idx(i), k);
        }

        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkDstIndex(i, k, j, abyte.length);
        System.arraycopy(this.memory, this.idx(i), abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.checkIndex(i);
        bytebuffer.put((byte[]) this.memory, this.idx(i), Math.min(this.capacity() - i, bytebuffer.remaining()));
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.checkIndex(i, j);
        outputstream.write((byte[]) this.memory, this.idx(i), j);
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.getBytes(i, gatheringbytechannel, j, false);
    }

    private int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j, boolean flag) throws IOException {
        this.checkIndex(i, j);
        i = this.idx(i);
        ByteBuffer bytebuffer;

        if (flag) {
            bytebuffer = this.internalNioBuffer();
        } else {
            bytebuffer = ByteBuffer.wrap((byte[]) this.memory);
        }

        return gatheringbytechannel.write((ByteBuffer) bytebuffer.clear().position(i).limit(i + j));
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        this.checkReadableBytes(i);
        int j = this.getBytes(this.readerIndex, gatheringbytechannel, i, true);

        this.readerIndex += j;
        return j;
    }

    @Override
    protected void _setByte(int i, int j) {
        ((byte[]) this.memory)[this.idx(i)] = (byte) j;
    }

    @Override
    protected void _setShort(int i, int j) {
        i = this.idx(i);
        ((byte[]) this.memory)[i] = (byte) (j >>> 8);
        ((byte[]) this.memory)[i + 1] = (byte) j;
    }

    @Override
    protected void _setMedium(int i, int j) {
        i = this.idx(i);
        ((byte[]) this.memory)[i] = (byte) (j >>> 16);
        ((byte[]) this.memory)[i + 1] = (byte) (j >>> 8);
        ((byte[]) this.memory)[i + 2] = (byte) j;
    }

    @Override
    protected void _setInt(int i, int j) {
        i = this.idx(i);
        ((byte[]) this.memory)[i] = (byte) (j >>> 24);
        ((byte[]) this.memory)[i + 1] = (byte) (j >>> 16);
        ((byte[]) this.memory)[i + 2] = (byte) (j >>> 8);
        ((byte[]) this.memory)[i + 3] = (byte) j;
    }

    @Override
    protected void _setLong(int i, long j) {
        i = this.idx(i);
        ((byte[]) this.memory)[i] = (byte) ((int) (j >>> 56));
        ((byte[]) this.memory)[i + 1] = (byte) ((int) (j >>> 48));
        ((byte[]) this.memory)[i + 2] = (byte) ((int) (j >>> 40));
        ((byte[]) this.memory)[i + 3] = (byte) ((int) (j >>> 32));
        ((byte[]) this.memory)[i + 4] = (byte) ((int) (j >>> 24));
        ((byte[]) this.memory)[i + 5] = (byte) ((int) (j >>> 16));
        ((byte[]) this.memory)[i + 6] = (byte) ((int) (j >>> 8));
        ((byte[]) this.memory)[i + 7] = (byte) ((int) j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkSrcIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasMemoryAddress()) {
            PlatformDependent.copyMemory(bytebuf.memoryAddress() + (long) j, (byte[]) this.memory, this.idx(i), (long) k);
        } else if (bytebuf.hasArray()) {
            this.setBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
        } else {
            bytebuf.getBytes(j, (byte[]) this.memory, this.idx(i), k);
        }

        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkSrcIndex(i, k, j, abyte.length);
        System.arraycopy(abyte, j, this.memory, this.idx(i), k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        int j = bytebuffer.remaining();

        this.checkIndex(i, j);
        bytebuffer.get((byte[]) this.memory, this.idx(i), j);
        return this;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.checkIndex(i, j);
        return inputstream.read((byte[]) this.memory, this.idx(i), j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.checkIndex(i, j);
        i = this.idx(i);

        try {
            return scatteringbytechannel.read((ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j));
        } catch (ClosedChannelException closedchannelexception) {
            return -1;
        }
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        ByteBuf bytebuf = this.alloc().heapBuffer(j, this.maxCapacity());

        bytebuf.writeBytes((byte[]) this.memory, this.idx(i), j);
        return bytebuf;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return new ByteBuffer[]{this.nioBuffer(i, j)};
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.checkIndex(i, j);
        i = this.idx(i);
        return (ByteBuffer) ByteBuffer.wrap((byte[]) this.memory).position(i).limit(i + j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        this.checkIndex(i, j);
        i = this.idx(i);
        return (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return (byte[]) this.memory;
    }

    @Override
    public int arrayOffset() {
        return this.offset;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    protected ByteBuffer newInternalNioBuffer(byte[] abyte) {
        return ByteBuffer.wrap(abyte);
    }

    @Override
    protected Recycler<?> recycler() {
        return PooledHeapByteBuf.RECYCLER;
    }
}
