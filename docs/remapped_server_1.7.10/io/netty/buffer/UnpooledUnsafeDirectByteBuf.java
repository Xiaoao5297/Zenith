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

public class UnpooledUnsafeDirectByteBuf extends AbstractReferenceCountedByteBuf {

    private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private final ResourceLeak leak;
    private final ByteBufAllocator alloc;
    private long memoryAddress;
    private ByteBuffer buffer;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;

    protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator bytebufallocator, int i, int j) {
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
            this.leak = UnpooledUnsafeDirectByteBuf.leakDetector.open(this);
        }
    }

    protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator bytebufallocator, ByteBuffer bytebuffer, int i) {
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
                this.leak = UnpooledUnsafeDirectByteBuf.leakDetector.open(this);
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
        this.memoryAddress = PlatformDependent.directBufferAddress(bytebuffer);
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
        return true;
    }

    @Override
    public long memoryAddress() {
        return this.memoryAddress;
    }

    @Override
    protected byte _getByte(int i) {
        return PlatformDependent.getByte(this.addr(i));
    }

    @Override
    protected short _getShort(int i) {
        short short0 = PlatformDependent.getShort(this.addr(i));

        return UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? short0 : Short.reverseBytes(short0);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        long j = this.addr(i);

        return (PlatformDependent.getByte(j) & 255) << 16 | (PlatformDependent.getByte(j + 1L) & 255) << 8 | PlatformDependent.getByte(j + 2L) & 255;
    }

    @Override
    protected int _getInt(int i) {
        int j = PlatformDependent.getInt(this.addr(i));

        return UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Integer.reverseBytes(j);
    }

    @Override
    protected long _getLong(int i) {
        long j = PlatformDependent.getLong(this.addr(i));

        return UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Long.reverseBytes(j);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkIndex(i, k);
        if (bytebuf == null) {
            throw new NullPointerException("dst");
        } else if (j >= 0 && j <= bytebuf.capacity() - k) {
            if (bytebuf.hasMemoryAddress()) {
                PlatformDependent.copyMemory(this.addr(i), bytebuf.memoryAddress() + (long) j, (long) k);
            } else if (bytebuf.hasArray()) {
                PlatformDependent.copyMemory(this.addr(i), bytebuf.array(), bytebuf.arrayOffset() + j, (long) k);
            } else {
                bytebuf.setBytes(j, (ByteBuf) this, i, k);
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
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", j, k, abyte.length));
        }
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
    protected void _setByte(int i, int j) {
        PlatformDependent.putByte(this.addr(i), (byte) j);
    }

    @Override
    protected void _setShort(int i, int j) {
        PlatformDependent.putShort(this.addr(i), UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? (short) j : Short.reverseBytes((short) j));
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
        PlatformDependent.putInt(this.addr(i), UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Integer.reverseBytes(j));
    }

    @Override
    protected void _setLong(int i, long j) {
        PlatformDependent.putLong(this.addr(i), UnpooledUnsafeDirectByteBuf.NATIVE_ORDER ? j : Long.reverseBytes(j));
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
                } else if (this.buffer.hasArray()) {
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
        this.ensureAccessible();
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
        this.ensureAccessible();
        ByteBuffer bytebuffer = this.internalNioBuffer();

        bytebuffer.clear().position(i).limit(i + j);

        try {
            return scatteringbytechannel.read(bytebuffer);
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
        this.checkIndex(i, j);
        UnpooledUnsafeDirectByteBuf unpooledunsafedirectbytebuf = (UnpooledUnsafeDirectByteBuf) this.alloc().directBuffer(j, this.maxCapacity());

        if (j != 0) {
            PlatformDependent.copyMemory(this.addr(i), unpooledunsafedirectbytebuf.addr(0), (long) j);
            unpooledunsafedirectbytebuf.setIndex(0, j);
        }

        return unpooledunsafedirectbytebuf;
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

    long addr(int i) {
        return this.memoryAddress + (long) i;
    }
}
