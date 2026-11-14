package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledUnsafeDirectByteBuf extends PooledByteBuf<ByteBuffer> {

    private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private static final Recycler<PooledUnsafeDirectByteBuf> RECYCLER = new Recycler<PooledUnsafeDirectByteBuf>() {
        @Override
        protected PooledUnsafeDirectByteBuf newObject(Recycler.Handle recycler_handle) {
            return new PooledUnsafeDirectByteBuf(recycler_handle, 0);
        }
    };
    private long memoryAddress;

    static PooledUnsafeDirectByteBuf newInstance(int i) {
        PooledUnsafeDirectByteBuf pooledunsafedirectbytebuf = (PooledUnsafeDirectByteBuf) PooledUnsafeDirectByteBuf.RECYCLER.get();

        pooledunsafedirectbytebuf.setRefCnt(1);
        pooledunsafedirectbytebuf.maxCapacity(i);
        return pooledunsafedirectbytebuf;
    }

    private PooledUnsafeDirectByteBuf(Recycler.Handle recycler_handle, int i) {
        super(recycler_handle, i);
    }

    @Override
    void init(PoolChunk<ByteBuffer> poolchunk, long i, int j, int k, int l) {
        super.init(poolchunk, i, j, k, l);
        this.initMemoryAddress();
    }

    @Override
    void initUnpooled(PoolChunk<ByteBuffer> poolchunk, int i) {
        super.initUnpooled(poolchunk, i);
        this.initMemoryAddress();
    }

    private void initMemoryAddress() {
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer) this.memory) + (long) this.offset;
    }

    protected ByteBuffer newInternalNioBuffer(ByteBuffer bytebuffer) {
        return bytebuffer.duplicate();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    protected byte _getByte(int i) {
        return PlatformDependent.getByte(this.addr(i));
    }

    @Override
    protected short _getShort(int i) {
        short short0 = PlatformDependent.getShort(this.addr(i));

        return PooledUnsafeDirectByteBuf.NATIVE_ORDER ? short0 : Short.reverseBytes(short0);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        long j = this.addr(i);

        return (PlatformDependent.getByte(j) & 255) << 16 | (PlatformDependent.getByte(j + 1L) & 255) << 8 | PlatformDependent.getByte(j + 2L) & 255;
    }

    @Override
    protected int _getInt(int i) {
        int j = PlatformDependent.getInt(this.addr(i));

        return PooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Integer.reverseBytes(j);
    }

    @Override
    protected long _getLong(int i) {
        long j = PlatformDependent.getLong(this.addr(i));

        return PooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Long.reverseBytes(j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        if (bytebuf == null) {
            throw new NullPointerException("dst");
        } else if (j >= 0 && j <= bytebuf.capacity() - k) {
            if (k != 0) {
                if (bytebuf.hasMemoryAddress()) {
                    PlatformDependent.copyMemory(this.addr(i), bytebuf.memoryAddress() + (long) j, (long) k);
                } else if (bytebuf.hasArray()) {
                    PlatformDependent.copyMemory(this.addr(i), bytebuf.array(), bytebuf.arrayOffset() + j, (long) k);
                } else {
                    bytebuf.setBytes(j, (ByteBuf) this, i, k);
                }
            }

            return this;
        } else {
            throw new IndexOutOfBoundsException("dstIndex: " + j);
        }
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        if (abyte == null) {
            throw new NullPointerException("dst");
        } else if (j >= 0 && j <= abyte.length - k) {
            if (k != 0) {
                PlatformDependent.copyMemory(this.addr(i), abyte, j, (long) k);
            }

            return this;
        } else {
            throw new IndexOutOfBoundsException("dstIndex: " + j);
        }
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.getBytes(i, bytebuffer, false);
        return this;
    }

    private void getBytes(int i, ByteBuffer bytebuffer, boolean flag) {
        this.checkIndex(i);
        int j = Math.min(this.capacity() - i, bytebuffer.remaining());
        ByteBuffer bytebuffer1;

        if (flag) {
            bytebuffer1 = this.internalNioBuffer();
        } else {
            bytebuffer1 = ((ByteBuffer) this.memory).duplicate();
        }

        i = this.idx(i);
        bytebuffer1.clear().position(i).limit(i + j);
        bytebuffer.put(bytebuffer1);
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
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.checkIndex(i, j);
        if (j != 0) {
            byte[] abyte = new byte[j];

            PlatformDependent.copyMemory(this.addr(i), abyte, 0, (long) j);
            outputstream.write(abyte);
        }

        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.getBytes(i, gatheringbytechannel, j, false);
    }

    private int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j, boolean flag) throws IOException {
        this.checkIndex(i, j);
        if (j == 0) {
            return 0;
        } else {
            ByteBuffer bytebuffer;

            if (flag) {
                bytebuffer = this.internalNioBuffer();
            } else {
                bytebuffer = ((ByteBuffer) this.memory).duplicate();
            }

            i = this.idx(i);
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
    protected void _setByte(int i, int j) {
        PlatformDependent.putByte(this.addr(i), (byte) j);
    }

    @Override
    protected void _setShort(int i, int j) {
        PlatformDependent.putShort(this.addr(i), PooledUnsafeDirectByteBuf.NATIVE_ORDER ? (short) j : Short.reverseBytes((short) j));
    }

    @Override
    protected void _setMedium(int i, int j) {
        long k = this.addr(i);

        PlatformDependent.putByte(k, (byte) (j >>> 16));
        PlatformDependent.putByte(k + 1L, (byte) (j >>> 8));
        PlatformDependent.putByte(k + 2L, (byte) j);
    }

    @Override
    protected void _setInt(int i, int j) {
        PlatformDependent.putInt(this.addr(i), PooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Integer.reverseBytes(j));
    }

    @Override
    protected void _setLong(int i, long j) {
        PlatformDependent.putLong(this.addr(i), PooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Long.reverseBytes(j));
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else if (j >= 0 && j <= bytebuf.capacity() - k) {
            if (k != 0) {
                if (bytebuf.hasMemoryAddress()) {
                    PlatformDependent.copyMemory(bytebuf.memoryAddress() + (long) j, this.addr(i), (long) k);
                } else if (bytebuf.hasArray()) {
                    PlatformDependent.copyMemory(bytebuf.array(), bytebuf.arrayOffset() + j, this.addr(i), (long) k);
                } else {
                    bytebuf.getBytes(j, (ByteBuf) this, i, k);
                }
            }

            return this;
        } else {
            throw new IndexOutOfBoundsException("srcIndex: " + j);
        }
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkIndex(i, k);
        if (k != 0) {
            PlatformDependent.copyMemory(abyte, j, this.addr(i), (long) k);
        }

        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.checkIndex(i);
        ByteBuffer bytebuffer1 = this.internalNioBuffer();

        if (bytebuffer == bytebuffer1) {
            bytebuffer = bytebuffer.duplicate();
        }

        i = this.idx(i);
        bytebuffer1.clear().position(i).limit(i + bytebuffer.remaining());
        bytebuffer1.put(bytebuffer);
        return this;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.checkIndex(i, j);
        byte[] abyte = new byte[j];
        int k = inputstream.read(abyte);

        if (k > 0) {
            PlatformDependent.copyMemory(abyte, 0, this.addr(i), (long) k);
        }

        return k;
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.checkIndex(i, j);
        ByteBuffer bytebuffer = this.internalNioBuffer();

        i = this.idx(i);
        bytebuffer.clear().position(i).limit(i + j);

        try {
            return scatteringbytechannel.read(bytebuffer);
        } catch (ClosedChannelException closedchannelexception) {
            return -1;
        }
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        PooledUnsafeDirectByteBuf pooledunsafedirectbytebuf = (PooledUnsafeDirectByteBuf) this.alloc().directBuffer(j, this.maxCapacity());

        if (j != 0) {
            PlatformDependent.copyMemory(this.addr(i), pooledunsafedirectbytebuf.addr(0), (long) j);
            pooledunsafedirectbytebuf.setIndex(0, j);
        }

        return pooledunsafedirectbytebuf;
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
        return (ByteBuffer) ((ByteBuffer) this.memory).duplicate().position(i).limit(i + j);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        this.checkIndex(i, j);
        i = this.idx(i);
        return (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
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
        return true;
    }

    @Override
    public long memoryAddress() {
        return this.memoryAddress;
    }

    private long addr(int i) {
        return this.memoryAddress + (long) i;
    }

    @Override
    protected Recycler<?> recycler() {
        return PooledUnsafeDirectByteBuf.RECYCLER;
    }
}
