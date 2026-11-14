package io.netty.buffer;

import io.netty.util.ResourceLeak;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class ReadOnlyByteBufferBuf extends AbstractReferenceCountedByteBuf {

    private final ResourceLeak leak;
    protected final ByteBuffer buffer;
    private final ByteBufAllocator allocator;
    private ByteBuffer tmpNioBuf;

    public ReadOnlyByteBufferBuf(ByteBufAllocator bytebufallocator, ByteBuffer bytebuffer) {
        super(bytebuffer.remaining());
        if (!bytebuffer.isReadOnly()) {
            throw new IllegalArgumentException("must be a readonly buffer: " + bytebuffer.getClass().getSimpleName());
        } else {
            this.allocator = bytebufallocator;
            this.buffer = bytebuffer.slice().order(ByteOrder.BIG_ENDIAN);
            this.writerIndex(this.buffer.limit());
            this.leak = ReadOnlyByteBufferBuf.leakDetector.open(this);
        }
    }

    @Override
    protected void deallocate() {
        if (this.leak != null) {
            this.leak.close();
        }

    }

    @Override
    public byte getByte(int i) {
        this.ensureAccessible();
        return this._getByte(i);
    }

    @Override
    protected byte _getByte(int i) {
        return this.buffer.get(i);
    }

    @Override
    public short getShort(int i) {
        this.ensureAccessible();
        return this._getShort(i);
    }

    @Override
    protected short _getShort(int i) {
        return this.buffer.getShort(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        this.ensureAccessible();
        return this._getUnsignedMedium(i);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        return (this.getByte(i) & 255) << 16 | (this.getByte(i + 1) & 255) << 8 | this.getByte(i + 2) & 255;
    }

    @Override
    public int getInt(int i) {
        this.ensureAccessible();
        return this._getInt(i);
    }

    @Override
    protected int _getInt(int i) {
        return this.buffer.getInt(i);
    }

    @Override
    public long getLong(int i) {
        this.ensureAccessible();
        return this._getLong(i);
    }

    @Override
    protected long _getLong(int i) {
        return this.buffer.getLong(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkDstIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasArray()) {
            this.getBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
        } else if (bytebuf.nioBufferCount() > 0) {
            ByteBuffer[] abytebuffer = bytebuf.nioBuffers(j, k);
            int l = abytebuffer.length;

            for (int i1 = 0; i1 < l; ++i1) {
                ByteBuffer bytebuffer = abytebuffer[i1];
                int j1 = bytebuffer.remaining();

                this.getBytes(i, bytebuffer);
                i += j1;
            }
        } else {
            bytebuf.setBytes(j, (ByteBuf) this, i, k);
        }

        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkDstIndex(i, k, j, abyte.length);
        if (j >= 0 && j <= abyte.length - k) {
            ByteBuffer bytebuffer = this.internalNioBuffer();

            bytebuffer.clear().position(i).limit(i + k);
            bytebuffer.get(abyte, j, k);
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
    protected void _setByte(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int i, int j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int i, long j) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int capacity() {
        return this.maxCapacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.allocator;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.ensureAccessible();
        if (j == 0) {
            return this;
        } else {
            if (this.buffer.hasArray()) {
                outputstream.write(this.buffer.array(), i + this.buffer.arrayOffset(), j);
            } else {
                byte[] abyte = new byte[j];
                ByteBuffer bytebuffer = this.internalNioBuffer();

                bytebuffer.clear().position(i);
                bytebuffer.get(abyte);
                outputstream.write(abyte);
            }

            return this;
        }
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        this.ensureAccessible();
        if (j == 0) {
            return 0;
        } else {
            ByteBuffer bytebuffer = this.internalNioBuffer();

            bytebuffer.clear().position(i).limit(i + j);
            return gatheringbytechannel.write(bytebuffer);
        }
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
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        throw new ReadOnlyBufferException();
    }

    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer bytebuffer = this.tmpNioBuf;

        if (bytebuffer == null) {
            this.tmpNioBuf = bytebuffer = this.buffer.duplicate();
        }

        return bytebuffer;
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.ensureAccessible();

        ByteBuffer bytebuffer;

        try {
            bytebuffer = (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (i + j));
        }

        ByteBuffer bytebuffer1 = ByteBuffer.allocateDirect(j);

        bytebuffer1.put(bytebuffer);
        bytebuffer1.order(this.order());
        bytebuffer1.clear();
        return new UnpooledDirectByteBuf(this.alloc(), bytebuffer1, this.maxCapacity());
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
        return (ByteBuffer) this.buffer.duplicate().position(i).limit(j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        this.ensureAccessible();
        return (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
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
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }
}
