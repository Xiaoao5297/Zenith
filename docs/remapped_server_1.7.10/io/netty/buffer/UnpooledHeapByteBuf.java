package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledHeapByteBuf extends AbstractReferenceCountedByteBuf {

    private final ByteBufAllocator alloc;
    private byte[] array;
    private ByteBuffer tmpNioBuf;

    protected UnpooledHeapByteBuf(ByteBufAllocator bytebufallocator, int i, int j) {
        this(bytebufallocator, new byte[i], 0, 0, j);
    }

    protected UnpooledHeapByteBuf(ByteBufAllocator bytebufallocator, byte[] abyte, int i) {
        this(bytebufallocator, abyte, 0, abyte.length, i);
    }

    private UnpooledHeapByteBuf(ByteBufAllocator bytebufallocator, byte[] abyte, int i, int j, int k) {
        super(k);
        if (bytebufallocator == null) {
            throw new NullPointerException("alloc");
        } else if (abyte == null) {
            throw new NullPointerException("initialArray");
        } else if (abyte.length > k) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", abyte.length, k));
        } else {
            this.alloc = bytebufallocator;
            this.setArray(abyte);
            this.setIndex(i, j);
        }
    }

    private void setArray(byte[] abyte) {
        this.array = abyte;
        this.tmpNioBuf = null;
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
    public boolean isDirect() {
        return false;
    }

    @Override
    public int capacity() {
        this.ensureAccessible();
        return this.array.length;
    }

    @Override
    public ByteBuf capacity(int i) {
        this.ensureAccessible();
        if (i >= 0 && i <= this.maxCapacity()) {
            int j = this.array.length;
            byte[] abyte;

            if (i > j) {
                abyte = new byte[i];
                System.arraycopy(this.array, this.readerIndex(), abyte, this.readerIndex(), this.readableBytes());
                this.setArray(abyte);
            } else if (i < j) {
                abyte = new byte[i];
                int k = this.readerIndex();

                if (k < i) {
                    int l = this.writerIndex();

                    if (l > i) {
                        l = i;
                        this.writerIndex(i);
                    }

                    System.arraycopy(this.array, k, abyte, k, l - k);
                } else {
                    this.setIndex(i, i);
                }

                this.setArray(abyte);
            }

            return this;
        } else {
            throw new IllegalArgumentException("newCapacity: " + i);
        }
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        this.ensureAccessible();
        return this.array;
    }

    @Override
    public int arrayOffset() {
        return 0;
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
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkDstIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.array, i, bytebuf.memoryAddress() + (long) j, (long) k);
        } else if (bytebuf.hasArray()) {
            this.getBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
        } else {
            bytebuf.setBytes(j, this.array, i, k);
        }

        return this;
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        this.checkDstIndex(i, k, j, abyte.length);
        System.arraycopy(this.array, i, abyte, j, k);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        this.ensureAccessible();
        bytebuffer.put(this.array, i, Math.min(this.capacity() - i, bytebuffer.remaining()));
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        this.ensureAccessible();
        outputstream.write(this.array, i, j);
        return this;
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        this.ensureAccessible();
        return this.getBytes(i, gatheringbytechannel, j, false);
    }

    private int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j, boolean flag) throws IOException {
        this.ensureAccessible();
        ByteBuffer bytebuffer;

        if (flag) {
            bytebuffer = this.internalNioBuffer();
        } else {
            bytebuffer = ByteBuffer.wrap(this.array);
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
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkSrcIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasMemoryAddress()) {
            PlatformDependent.copyMemory(bytebuf.memoryAddress() + (long) j, this.array, i, (long) k);
        } else if (bytebuf.hasArray()) {
            this.setBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
        } else {
            bytebuf.getBytes(j, this.array, i, k);
        }

        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        this.checkSrcIndex(i, k, j, abyte.length);
        System.arraycopy(abyte, j, this.array, i, k);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        this.ensureAccessible();
        bytebuffer.get(this.array, i, bytebuffer.remaining());
        return this;
    }

    @Override
    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        this.ensureAccessible();
        return inputstream.read(this.array, i, j);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        this.ensureAccessible();

        try {
            return scatteringbytechannel.read((ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j));
        } catch (ClosedChannelException closedchannelexception) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.ensureAccessible();
        return ByteBuffer.wrap(this.array, i, j);
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return new ByteBuffer[]{this.nioBuffer(i, j)};
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int j) {
        return (ByteBuffer) this.internalNioBuffer().clear().position(i).limit(i + j);
    }

    @Override
    public byte getByte(int i) {
        this.ensureAccessible();
        return this._getByte(i);
    }

    @Override
    protected byte _getByte(int i) {
        return this.array[i];
    }

    @Override
    public short getShort(int i) {
        this.ensureAccessible();
        return this._getShort(i);
    }

    @Override
    protected short _getShort(int i) {
        return (short) (this.array[i] << 8 | this.array[i + 1] & 255);
    }

    @Override
    public int getUnsignedMedium(int i) {
        this.ensureAccessible();
        return this._getUnsignedMedium(i);
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        return (this.array[i] & 255) << 16 | (this.array[i + 1] & 255) << 8 | this.array[i + 2] & 255;
    }

    @Override
    public int getInt(int i) {
        this.ensureAccessible();
        return this._getInt(i);
    }

    @Override
    protected int _getInt(int i) {
        return (this.array[i] & 255) << 24 | (this.array[i + 1] & 255) << 16 | (this.array[i + 2] & 255) << 8 | this.array[i + 3] & 255;
    }

    @Override
    public long getLong(int i) {
        this.ensureAccessible();
        return this._getLong(i);
    }

    @Override
    protected long _getLong(int i) {
        return ((long) this.array[i] & 255L) << 56 | ((long) this.array[i + 1] & 255L) << 48 | ((long) this.array[i + 2] & 255L) << 40 | ((long) this.array[i + 3] & 255L) << 32 | ((long) this.array[i + 4] & 255L) << 24 | ((long) this.array[i + 5] & 255L) << 16 | ((long) this.array[i + 6] & 255L) << 8 | (long) this.array[i + 7] & 255L;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        this.ensureAccessible();
        this._setByte(i, j);
        return this;
    }

    @Override
    protected void _setByte(int i, int j) {
        this.array[i] = (byte) j;
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        this.ensureAccessible();
        this._setShort(i, j);
        return this;
    }

    @Override
    protected void _setShort(int i, int j) {
        this.array[i] = (byte) (j >>> 8);
        this.array[i + 1] = (byte) j;
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        this.ensureAccessible();
        this._setMedium(i, j);
        return this;
    }

    @Override
    protected void _setMedium(int i, int j) {
        this.array[i] = (byte) (j >>> 16);
        this.array[i + 1] = (byte) (j >>> 8);
        this.array[i + 2] = (byte) j;
    }

    @Override
    public ByteBuf setInt(int i, int j) {
        this.ensureAccessible();
        this._setInt(i, j);
        return this;
    }

    @Override
    protected void _setInt(int i, int j) {
        this.array[i] = (byte) (j >>> 24);
        this.array[i + 1] = (byte) (j >>> 16);
        this.array[i + 2] = (byte) (j >>> 8);
        this.array[i + 3] = (byte) j;
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        this.ensureAccessible();
        this._setLong(i, j);
        return this;
    }

    @Override
    protected void _setLong(int i, long j) {
        this.array[i] = (byte) ((int) (j >>> 56));
        this.array[i + 1] = (byte) ((int) (j >>> 48));
        this.array[i + 2] = (byte) ((int) (j >>> 40));
        this.array[i + 3] = (byte) ((int) (j >>> 32));
        this.array[i + 4] = (byte) ((int) (j >>> 24));
        this.array[i + 5] = (byte) ((int) (j >>> 16));
        this.array[i + 6] = (byte) ((int) (j >>> 8));
        this.array[i + 7] = (byte) ((int) j);
    }

    @Override
    public ByteBuf copy(int i, int j) {
        this.checkIndex(i, j);
        byte[] abyte = new byte[j];

        System.arraycopy(this.array, i, abyte, 0, j);
        return new UnpooledHeapByteBuf(this.alloc(), abyte, this.maxCapacity());
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer bytebuffer = this.tmpNioBuf;

        if (bytebuffer == null) {
            this.tmpNioBuf = bytebuffer = ByteBuffer.wrap(this.array);
        }

        return bytebuffer;
    }

    @Override
    protected void deallocate() {
        this.array = null;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}
