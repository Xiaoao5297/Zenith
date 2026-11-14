package io.netty.buffer;

import io.netty.util.ResourceLeak;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledDirectByteBuf extends AbstractReferenceCountedByteBuf {

    private final ResourceLeak leak;
    private final ByteBufAllocator alloc;
    private ByteBuffer buffer;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;

    protected UnpooledDirectByteBuf(ByteBufAllocator bytebufallocator, int i, int j) {
        super(j);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else if (i < 0) {
            throw new IllegalArgumentException("initialCapacity: " + i);
        } else if (j < 0) {
            throw new IllegalArgumentException("maxCapacity: " + j);
        } else if (i > j) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", i, j));
        } else {
            this.alloc = bytebufallocator;
            this.setByteBuffer(ByteBuffer.allocateDirect(i));
            this.leak = UnpooledDirectByteBuf.leakDetector.open(this);
        }
    }

    protected UnpooledDirectByteBuf(ByteBufAllocator bytebufallocator, ByteBuffer bytebuffer, int i) {
        super(i);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else if (bytebuffer == null) {
            throw new NullPointerException("initialBuffer");
        } else if (!bytebuffer.isDirect()) {
            throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
        } else if (bytebuffer.isReadOnly()) {
            throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
        } else {
            int j = bytebuffer.remaining();

            if (j > i) {
                throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", j, i));
            } else {
                this.alloc = bytebufallocator;
                this.doNotFree = true;
                this.setByteBuffer(bytebuffer.slice().order(ByteOrder.BIG_ENDIAN));
                this.writerIndex(j);
                this.leak = UnpooledDirectByteBuf.leakDetector.open(this);
            }
        }
    }

    private void setByteBuffer(ByteBuffer bytebuffer) {
        ByteBuffer bytebuffer1 = this.buffer;

        if (bytebuffer1 != null) {
            if (this.doNotFree) {
                this.doNotFree = false;
            } else {
                PlatformDependent.freeDirectBuffer(bytebuffer1);
            }
        }

        this.buffer = bytebuffer;
        this.tmpNioBuf = null;
        this.capacity = bytebuffer.remaining();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int i) {
        this.ensureAccessible();
        if (i >= 0 && i <= this.maxCapacity()) {
            int j = this.readerIndex();
            int k = this.writerIndex();
            int l = this.capacity;
            ByteBuffer bytebuffer;
            ByteBuffer bytebuffer1;

            if (i > l) {
                bytebuffer = this.buffer;
                bytebuffer1 = ByteBuffer.allocateDirect(i);
                bytebuffer.position(j).limit(k);
                bytebuffer1.position(j).limit(k);
                bytebuffer1.put(bytebuffer);
                bytebuffer1.clear();
                this.setByteBuffer(bytebuffer1);
            } else if (i < l) {
                bytebuffer = this.buffer;
                bytebuffer1 = ByteBuffer.allocateDirect(i);
                if (j < i) {
                    if (k > i) {
                        k = i;
                        this.writerIndex(i);
                    }

                    bytebuffer.position(j).limit(k);
                    bytebuffer1.position(j).limit(k);
                    bytebuffer1.put(bytebuffer);
                    bytebuffer1.clear();
                } else {
                    this.setIndex(i, i);
                }

                this.setByteBuffer(bytebuffer1);
            }

            return this;
        } else {
            throw new IllegalArgumentException("newCapacity: " + i);
        }
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException("direct buffer");
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
        this.getBytes(i, abyte, j, k, false);
        return this;
    }

    private void getBytes(int i, byte[] abyte, int j, int k, boolean flag) {
        this.checkDstIndex(i, k, j, abyte.length);
        if (j >= 0 && j <= abyte.length - k) {
            ByteBuffer bytebuffer;

            if (flag) {
                bytebuffer = this.internalNioBuffer();
            } else {
                bytebuffer = this.buffer.duplicate();
            }

            bytebuffer.clear().position(i).limit(i + k);
            bytebuffer.get(abyte, j, k);
        } else {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", j, k, abyte.length));
        }
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        this.checkReadableBytes(j);
        this.getBytes(this.readerIndex, abyte, i, j, true);
        this.readerIndex += j;
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.getBytes(i, bytebuffer, false);
        return this;
    }

    private void getBytes(int i, ByteBuffer bytebuffer, boolean flag) {
        this.checkIndex(i);
        if (bytebuffer == null) {
            throw new NullPointerException("dst");
        } else {
            int j = Math.min(this.capacity() - i, bytebuffer.remaining());
            ByteBuffer bytebuffer1;

            if (flag) {
                bytebuffer1 = this.internalNioBuffer();
            } else {
                bytebuffer1 = this.buffer.duplicate();
            }

            bytebuffer1.clear().position(i).limit(i + j);
            bytebuffer.put(bytebuffer1);
        }
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        int i = bytebuffer.remaining();

        this.checkReadableBytes(i);
        this.getBytes(this.readerIndex, bytebuffer, true);
        this.readerIndex += i;
        return this;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        this.ensureAccessible();
        this._setByte(i, j);
        return this;
    }

    @Override
    protected void _setByte(int i, int j) {
        this.buffer.put(i, (byte) j);
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        this.ensureAccessible();
        this._setShort(i, j);
        return this;
    }

    @Override
    protected void _setShort(int i, int j) {
        this.buffer.putShort(i, (short) j);
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        this.ensureAccessible();
        this._setMedium(i, j);
        return this;
    }

    @Override
    protected void _setMedium(int i, int j) {
        this.setByte(i, (byte) (j >>> 16));
        this.setByte(i + 1, (byte) (j >>> 8));
        this.setByte(i + 2, (byte) j);
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        this.ensureAccessible();
        this._setInt(i, j);
        return this;
    }

    @Override
    protected void _setInt(int i, int j) {
        this.buffer.putInt(i, j);
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        this.ensureAccessible();
        this._setLong(i, j);
        return this;
    }

    @Override
    protected void _setLong(int i, long j) {
        this.buffer.putLong(i, j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkSrcIndex(i, k, j, bytebuf.capacity());
        if (this.buffer.hasArray()) {
            bytebuf.getBytes(j, this.buffer.array(), i + this.buffer.arrayOffset(), k);
        } else if (bytebuf.nioBufferCount() > 0) {
            ByteBuffer[] abytebuffer = bytebuf.nioBuffers(j, k);
            int l = abytebuffer.length;

            for (int i1 = 0; i1 < l; ++i1) {
                ByteBuffer bytebuffer = abytebuffer[i1];
                int j1 = bytebuffer.remaining();

                this.setBytes(i, bytebuffer);
                i += j1;
            }
        } else {
            bytebuf.getBytes(j, (ByteBuf) this, i, k);
        }

        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkSrcIndex(i, k, j, abyte.length);
        ByteBuffer bytebuffer = this.internalNioBuffer();

        bytebuffer.clear().position(i).limit(i + k);
        bytebuffer.put(abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.ensureAccessible();
        ByteBuffer bytebuffer1 = this.internalNioBuffer();

        if (bytebuffer == bytebuffer1) {
            bytebuffer = bytebuffer.duplicate();
        }

        bytebuffer1.clear().position(i).limit(i + bytebuffer.remaining());
        bytebuffer1.put(bytebuffer);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.getBytes(i, outputstream, j, false);
        return this;
    }

    private void getBytes(int i, OutputStream outputstream, int j, boolean flag) throws IOException {
        this.ensureAccessible();
        if (j != 0) {
            if (this.buffer.hasArray()) {
                outputstream.write(this.buffer.array(), i + this.buffer.arrayOffset(), j);
            } else {
                byte[] abyte = new byte[j];
                ByteBuffer bytebuffer;

                if (flag) {
                    bytebuffer = this.internalNioBuffer();
                } else {
                    bytebuffer = this.buffer.duplicate();
                }

                bytebuffer.clear().position(i);
                bytebuffer.get(abyte);
                outputstream.write(abyte);
            }

        }
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        this.checkReadableBytes(i);
        this.getBytes(this.readerIndex, outputstream, i, true);
        this.readerIndex += i;
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.getBytes(i, gatheringbytechannel, j, false);
    }

    private int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j, boolean flag) throws IOException {
        this.ensureAccessible();
        if (j == 0) {
            return 0;
        } else {
            ByteBuffer bytebuffer;

            if (flag) {
                bytebuffer = this.internalNioBuffer();
            } else {
                bytebuffer = this.buffer.duplicate();
            }

            bytebuffer.clear().position(i).limit(i + j);
            return gatheringbytechannel.write(bytebuffer);
        }
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        this.checkReadableBytes(i);
        int j = this.getBytes(this.readerIndex, gatheringbytechannel, i, true);

        this.readerIndex += j;
        return j;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.ensureAccessible();
        if (this.buffer.hasArray()) {
            return inputstream.read(this.buffer.array(), this.buffer.arrayOffset() + i, j);
        } else {
            byte[] abyte = new byte[j];
            int k = inputstream.read(abyte);

            if (k <= 0) {
                return k;
            } else {
                ByteBuffer bytebuffer = this.internalNioBuffer();

                bytebuffer.clear().position(i);
                bytebuffer.put(abyte, 0, k);
                return k;
            }
        }
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.ensureAccessible();
        ByteBuffer bytebuffer = this.internalNioBuffer();

        bytebuffer.clear().position(i).limit(i + j);

        try {
            return scatteringbytechannel.read(this.tmpNioBuf);
        } catch (ClosedChannelException closedchannelexception) {
            return -1;
        }
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
    public ByteBuf copy(int i, int j) {
        this.ensureAccessible();

        ByteBuffer bytebuffer;

        try {
            bytebuffer = (ByteBuffer) this.buffer.duplicate().clear().position(i).limit(i + j);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (i + j));
        }

        ByteBuffer bytebuffer1 = bytebuffer.isDirect() ? ByteBuffer.allocateDirect(j) : ByteBuffer.allocate(j);

        bytebuffer1.put(bytebuffer);
        bytebuffer1.order(this.order());
        bytebuffer1.clear();
        return new UnpooledDirectByteBuf(this.alloc(), bytebuffer1, this.maxCapacity());
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer bytebuffer = this.tmpNioBuf;

        if (bytebuffer == null) {
            this.tmpNioBuf = bytebuffer = this.buffer.duplicate();
        }

        return bytebuffer;
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        return (ByteBuffer) this.buffer.duplicate().position(i).limit(i + j);
    }

    @Override
    protected void deallocate() {
        ByteBuffer bytebuffer = this.buffer;

        if (bytebuffer != null) {
            this.buffer = null;
            if (!this.doNotFree) {
                PlatformDependent.freeDirectBuffer(bytebuffer);
            }

            if (this.leak != null) {
                this.leak.close();
            }

        }
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}
