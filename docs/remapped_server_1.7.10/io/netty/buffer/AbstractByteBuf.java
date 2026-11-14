package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractByteBuf extends ByteBuf {

    static final ResourceLeakDetector<ByteBuf> leakDetector = new ResourceLeakDetector<>(ByteBuf.class);
    int readerIndex;
    private int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    private int maxCapacity;
    private SwappedByteBuf swappedBuf;

    protected AbstractByteBuf(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("maxCapacity: " + i + " (expected: >= 0)");
        } else {
            this.maxCapacity = i;
        }
    }

    @Override
    public int maxCapacity() {
        return this.maxCapacity;
    }

    protected final void maxCapacity(int i) {
        this.maxCapacity = i;
    }

    @Override
    public int readerIndex() {
        return this.readerIndex;
    }

    @Override
    public ByteBuf readerIndex(int i) {
        if (i >= 0 && i <= this.writerIndex) {
            this.readerIndex = i;
            return this;
        } else {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", i, this.writerIndex));
        }
    }

    @Override
    public int writerIndex() {
        return this.writerIndex;
    }

    @Override
    public ByteBuf writerIndex(int i) {
        if (i >= this.readerIndex && i <= this.capacity()) {
            this.writerIndex = i;
            return this;
        } else {
            throw new IndexOutOfBoundsException(String.format("writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))", i, this.readerIndex, this.capacity()));
        }
    }

    @Override
    public ByteBuf setIndex(int i, int j) {
        if (i >= 0 && i <= j && j <= this.capacity()) {
            this.readerIndex = i;
            this.writerIndex = j;
            return this;
        } else {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", i, j, this.capacity()));
        }
    }

    @Override
    public ByteBuf clear() {
        this.readerIndex = this.writerIndex = 0;
        return this;
    }

    @Override
    public boolean isReadable() {
        return this.writerIndex > this.readerIndex;
    }

    @Override
    public boolean isReadable(int i) {
        return this.writerIndex - this.readerIndex >= i;
    }

    @Override
    public boolean isWritable() {
        return this.capacity() > this.writerIndex;
    }

    @Override
    public boolean isWritable(int i) {
        return this.capacity() - this.writerIndex >= i;
    }

    @Override
    public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    @Override
    public int writableBytes() {
        return this.capacity() - this.writerIndex;
    }

    @Override
    public int maxWritableBytes() {
        return this.maxCapacity() - this.writerIndex;
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.readerIndex(this.markedReaderIndex);
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        this.writerIndex = this.markedWriterIndex;
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        } else {
            if (this.readerIndex != this.writerIndex) {
                this.setBytes(0, (ByteBuf) this, this.readerIndex, this.writerIndex - this.readerIndex);
                this.writerIndex -= this.readerIndex;
                this.adjustMarkers(this.readerIndex);
                this.readerIndex = 0;
            } else {
                this.adjustMarkers(this.readerIndex);
                this.writerIndex = this.readerIndex = 0;
            }

            return this;
        }
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        } else if (this.readerIndex == this.writerIndex) {
            this.adjustMarkers(this.readerIndex);
            this.writerIndex = this.readerIndex = 0;
            return this;
        } else {
            if (this.readerIndex >= this.capacity() >>> 1) {
                this.setBytes(0, (ByteBuf) this, this.readerIndex, this.writerIndex - this.readerIndex);
                this.writerIndex -= this.readerIndex;
                this.adjustMarkers(this.readerIndex);
                this.readerIndex = 0;
            }

            return this;
        }
    }

    protected final void adjustMarkers(int i) {
        int j = this.markedReaderIndex;

        if (j <= i) {
            this.markedReaderIndex = 0;
            int k = this.markedWriterIndex;

            if (k <= i) {
                this.markedWriterIndex = 0;
            } else {
                this.markedWriterIndex = k - i;
            }
        } else {
            this.markedReaderIndex = j - i;
            this.markedWriterIndex -= i;
        }

    }

    @Override
    public ByteBuf ensureWritable(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", i));
        } else if (i <= this.writableBytes()) {
            return this;
        } else if (i > this.maxCapacity - this.writerIndex) {
            throw new IndexOutOfBoundsException(String.format("writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", this.writerIndex, i, this.maxCapacity, this));
        } else {
            int j = this.calculateNewCapacity(this.writerIndex + i);

            this.capacity(j);
            return this;
        }
    }

    @Override
    public int ensureWritable(int i, boolean flag) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", i));
        } else if (i <= this.writableBytes()) {
            return 0;
        } else if (i > this.maxCapacity - this.writerIndex && flag) {
            if (this.capacity() == this.maxCapacity()) {
                return 1;
            } else {
                this.capacity(this.maxCapacity());
                return 3;
            }
        } else {
            int j = this.calculateNewCapacity(this.writerIndex + i);

            this.capacity(j);
            return 2;
        }
    }

    private int calculateNewCapacity(int i) {
        int j = this.maxCapacity;
        int k = 4194304;

        if (i == 4194304) {
            return 4194304;
        } else {
            int l;

            if (i > 4194304) {
                l = i / 4194304 * 4194304;
                if (l > j - 4194304) {
                    l = j;
                } else {
                    l += 4194304;
                }

                return l;
            } else {
                for (l = 64; l < i; l <<= 1) {
                    ;
                }

                return Math.min(l, j);
            }
        }
    }

    @Override
    public ByteBuf order(ByteOrder byteorder) {
        if (byteorder == null) {
            throw new NullPointerException("endianness");
        } else if (byteorder == this.order()) {
            return this;
        } else {
            SwappedByteBuf swappedbytebuf = this.swappedBuf;

            if (swappedbytebuf == null) {
                this.swappedBuf = swappedbytebuf = new SwappedByteBuf(this);
            }

            return swappedbytebuf;
        }
    }

    @Override
    public byte getByte(int i) {
        this.checkIndex(i);
        return this._getByte(i);
    }

    protected abstract byte _getByte(int i);

    @Override
    public boolean getBoolean(int i) {
        return this.getByte(i) != 0;
    }

    @Override
    public short getUnsignedByte(int i) {
        return (short) (this.getByte(i) & 255);
    }

    @Override
    public short getShort(int i) {
        this.checkIndex(i, 2);
        return this._getShort(i);
    }

    protected abstract short _getShort(int i);

    @Override
    public int getUnsignedShort(int i) {
        return this.getShort(i) & '\uffff';
    }

    @Override
    public int getUnsignedMedium(int i) {
        this.checkIndex(i, 3);
        return this._getUnsignedMedium(i);
    }

    protected abstract int _getUnsignedMedium(int i);

    @Override
    public int getMedium(int i) {
        int j = this.getUnsignedMedium(i);

        if ((j & 8388608) != 0) {
            j |= -16777216;
        }

        return j;
    }

    @Override
    public int getInt(int i) {
        this.checkIndex(i, 4);
        return this._getInt(i);
    }

    protected abstract int _getInt(int i);

    @Override
    public long getUnsignedInt(int i) {
        return (long) this.getInt(i) & 4294967295L;
    }

    @Override
    public long getLong(int i) {
        this.checkIndex(i, 8);
        return this._getLong(i);
    }

    protected abstract long _getLong(int i);

    @Override
    public char getChar(int i) {
        return (char) this.getShort(i);
    }

    @Override
    public float getFloat(int i) {
        return Float.intBitsToFloat(this.getInt(i));
    }

    @Override
    public double getDouble(int i) {
        return Double.longBitsToDouble(this.getLong(i));
    }

    @Override
    public ByteBuf getBytes(int i, byte[] abyte) {
        this.getBytes(i, abyte, 0, abyte.length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        this.getBytes(i, bytebuf, bytebuf.writableBytes());
        return this;
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        this.getBytes(i, bytebuf, bytebuf.writerIndex(), j);
        bytebuf.writerIndex(bytebuf.writerIndex() + j);
        return this;
    }

    @Override
    public ByteBuf setByte(int i, int j) {
        this.checkIndex(i);
        this._setByte(i, j);
        return this;
    }

    protected abstract void _setByte(int i, int j);

    @Override
    public ByteBuf setBoolean(int i, boolean flag) {
        this.setByte(i, flag ? 1 : 0);
        return this;
    }

    @Override
    public ByteBuf setShort(int i, int j) {
        this.checkIndex(i, 2);
        this._setShort(i, j);
        return this;
    }

    protected abstract void _setShort(int i, int j);

    @Override
    public ByteBuf setChar(int i, int j) {
        this.setShort(i, j);
        return this;
    }

    @Override
    public ByteBuf setMedium(int i, int j) {
        this.checkIndex(i, 3);
        this._setMedium(i, j);
        return this;
    }

    protected abstract void _setMedium(int i, int j);

    @Override
    public ByteBuf setInt(int i, int j) {
        this.checkIndex(i, 4);
        this._setInt(i, j);
        return this;
    }

    protected abstract void _setInt(int i, int j);

    @Override
    public ByteBuf setFloat(int i, float f) {
        this.setInt(i, Float.floatToRawIntBits(f));
        return this;
    }

    @Override
    public ByteBuf setLong(int i, long j) {
        this.checkIndex(i, 8);
        this._setLong(i, j);
        return this;
    }

    protected abstract void _setLong(int i, long j);

    @Override
    public ByteBuf setDouble(int i, double d0) {
        this.setLong(i, Double.doubleToRawLongBits(d0));
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, byte[] abyte) {
        this.setBytes(i, abyte, 0, abyte.length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        this.setBytes(i, bytebuf, bytebuf.readableBytes());
        return this;
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        this.checkIndex(i, j);
        if (bytebuf == null) {
            throw new NullPointerException("src");
        } else if (j > bytebuf.readableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", j, bytebuf.readableBytes(), bytebuf));
        } else {
            this.setBytes(i, bytebuf, bytebuf.readerIndex(), j);
            bytebuf.readerIndex(bytebuf.readerIndex() + j);
            return this;
        }
    }

    @Override
    public ByteBuf setZero(int i, int j) {
        if (j == 0) {
            return this;
        } else {
            this.checkIndex(i, j);
            int k = j >>> 3;
            int l = j & 7;

            int i1;

            for (i1 = k; i1 > 0; --i1) {
                this.setLong(i, 0L);
                i += 8;
            }

            if (l == 4) {
                this.setInt(i, 0);
            } else if (l < 4) {
                for (i1 = l; i1 > 0; --i1) {
                    this.setByte(i, 0);
                    ++i;
                }
            } else {
                this.setInt(i, 0);
                i += 4;

                for (i1 = l - 4; i1 > 0; --i1) {
                    this.setByte(i, 0);
                    ++i;
                }
            }

            return this;
        }
    }

    @Override
    public byte readByte() {
        this.checkReadableBytes(1);
        int i = this.readerIndex;
        byte b0 = this.getByte(i);

        this.readerIndex = i + 1;
        return b0;
    }

    @Override
    public boolean readBoolean() {
        return this.readByte() != 0;
    }

    @Override
    public short readUnsignedByte() {
        return (short) (this.readByte() & 255);
    }

    @Override
    public short readShort() {
        this.checkReadableBytes(2);
        short short0 = this._getShort(this.readerIndex);

        this.readerIndex += 2;
        return short0;
    }

    @Override
    public int readUnsignedShort() {
        return this.readShort() & '\uffff';
    }

    @Override
    public int readMedium() {
        int i = this.readUnsignedMedium();

        if ((i & 8388608) != 0) {
            i |= -16777216;
        }

        return i;
    }

    @Override
    public int readUnsignedMedium() {
        this.checkReadableBytes(3);
        int i = this._getUnsignedMedium(this.readerIndex);

        this.readerIndex += 3;
        return i;
    }

    @Override
    public int readInt() {
        this.checkReadableBytes(4);
        int i = this._getInt(this.readerIndex);

        this.readerIndex += 4;
        return i;
    }

    @Override
    public long readUnsignedInt() {
        return (long) this.readInt() & 4294967295L;
    }

    @Override
    public long readLong() {
        this.checkReadableBytes(8);
        long i = this._getLong(this.readerIndex);

        this.readerIndex += 8;
        return i;
    }

    @Override
    public char readChar() {
        return (char) this.readShort();
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public ByteBuf readBytes(int i) {
        this.checkReadableBytes(i);
        if (i == 0) {
            return Unpooled.EMPTY_BUFFER;
        } else {
            ByteBuf bytebuf = Unpooled.buffer(i, this.maxCapacity);

            bytebuf.writeBytes((ByteBuf) this, this.readerIndex, i);
            this.readerIndex += i;
            return bytebuf;
        }
    }

    @Override
    public ByteBuf readSlice(int i) {
        ByteBuf bytebuf = this.slice(this.readerIndex, i);

        this.readerIndex += i;
        return bytebuf;
    }

    @Override
    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        this.checkReadableBytes(j);
        this.getBytes(this.readerIndex, abyte, i, j);
        this.readerIndex += j;
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] abyte) {
        this.readBytes(abyte, 0, abyte.length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf) {
        this.readBytes(bytebuf, bytebuf.writableBytes());
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        if (i > bytebuf.writableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds dst.writableBytes(%d) where dst is: %s", i, bytebuf.writableBytes(), bytebuf));
        } else {
            this.readBytes(bytebuf, bytebuf.writerIndex(), i);
            bytebuf.writerIndex(bytebuf.writerIndex() + i);
            return this;
        }
    }

    @Override
    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        this.checkReadableBytes(j);
        this.getBytes(this.readerIndex, bytebuf, i, j);
        this.readerIndex += j;
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        int i = bytebuffer.remaining();

        this.checkReadableBytes(i);
        this.getBytes(this.readerIndex, bytebuffer);
        this.readerIndex += i;
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        this.checkReadableBytes(i);
        int j = this.getBytes(this.readerIndex, gatheringbytechannel, i);

        this.readerIndex += j;
        return j;
    }

    @Override
    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        this.checkReadableBytes(i);
        this.getBytes(this.readerIndex, outputstream, i);
        this.readerIndex += i;
        return this;
    }

    @Override
    public ByteBuf skipBytes(int i) {
        this.checkReadableBytes(i);
        int j = this.readerIndex + i;

        if (j > this.writerIndex) {
            throw new IndexOutOfBoundsException(String.format("length: %d (expected: readerIndex(%d) + length <= writerIndex(%d))", i, this.readerIndex, this.writerIndex));
        } else {
            this.readerIndex = j;
            return this;
        }
    }

    @Override
    public ByteBuf writeBoolean(boolean flag) {
        this.writeByte(flag ? 1 : 0);
        return this;
    }

    @Override
    public ByteBuf writeByte(int i) {
        this.ensureWritable(1);
        this.setByte(this.writerIndex++, i);
        return this;
    }

    @Override
    public ByteBuf writeShort(int i) {
        this.ensureWritable(2);
        this._setShort(this.writerIndex, i);
        this.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeMedium(int i) {
        this.ensureWritable(3);
        this._setMedium(this.writerIndex, i);
        this.writerIndex += 3;
        return this;
    }

    @Override
    public ByteBuf writeInt(int i) {
        this.ensureWritable(4);
        this._setInt(this.writerIndex, i);
        this.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeLong(long i) {
        this.ensureWritable(8);
        this._setLong(this.writerIndex, i);
        this.writerIndex += 8;
        return this;
    }

    @Override
    public ByteBuf writeChar(int i) {
        this.writeShort(i);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float f) {
        this.writeInt(Float.floatToRawIntBits(f));
        return this;
    }

    @Override
    public ByteBuf writeDouble(double d0) {
        this.writeLong(Double.doubleToRawLongBits(d0));
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        this.ensureWritable(j);
        this.setBytes(this.writerIndex, abyte, i, j);
        this.writerIndex += j;
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] abyte) {
        this.writeBytes(abyte, 0, abyte.length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf) {
        this.writeBytes(bytebuf, bytebuf.readableBytes());
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        if (i > bytebuf.readableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", i, bytebuf.readableBytes(), bytebuf));
        } else {
            this.writeBytes(bytebuf, bytebuf.readerIndex(), i);
            bytebuf.readerIndex(bytebuf.readerIndex() + i);
            return this;
        }
    }

    @Override
    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        this.ensureWritable(j);
        this.setBytes(this.writerIndex, bytebuf, i, j);
        this.writerIndex += j;
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        int i = bytebuffer.remaining();

        this.ensureWritable(i);
        this.setBytes(this.writerIndex, bytebuffer);
        this.writerIndex += i;
        return this;
    }

    @Override
    public int writeBytes(InputStream inputstream, int i) throws IOException {
        this.ensureWritable(i);
        int j = this.setBytes(this.writerIndex, inputstream, i);

        if (j > 0) {
            this.writerIndex += j;
        }

        return j;
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        this.ensureWritable(i);
        int j = this.setBytes(this.writerIndex, scatteringbytechannel, i);

        if (j > 0) {
            this.writerIndex += j;
        }

        return j;
    }

    @Override
    public ByteBuf writeZero(int i) {
        if (i == 0) {
            return this;
        } else {
            this.ensureWritable(i);
            this.checkIndex(this.writerIndex, i);
            int j = i >>> 3;
            int k = i & 7;

            int l;

            for (l = j; l > 0; --l) {
                this.writeLong(0L);
            }

            if (k == 4) {
                this.writeInt(0);
            } else if (k < 4) {
                for (l = k; l > 0; --l) {
                    this.writeByte(0);
                }
            } else {
                this.writeInt(0);

                for (l = k - 4; l > 0; --l) {
                    this.writeByte(0);
                }
            }

            return this;
        }
    }

    @Override
    public ByteBuf copy() {
        return this.copy(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuf duplicate() {
        return new DuplicatedByteBuf(this);
    }

    @Override
    public ByteBuf slice() {
        return this.slice(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuf slice(int i, int j) {
        return (ByteBuf) (j == 0 ? Unpooled.EMPTY_BUFFER : new SlicedByteBuf(this, i, j));
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.nioBuffer(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(this.readerIndex, this.readableBytes());
    }

    @Override
    public String toString(Charset charset) {
        return this.toString(this.readerIndex, this.readableBytes(), charset);
    }

    @Override
    public String toString(int i, int j, Charset charset) {
        if (j == 0) {
            return "";
        } else {
            ByteBuffer bytebuffer;

            if (this.nioBufferCount() == 1) {
                bytebuffer = this.nioBuffer(i, j);
            } else {
                bytebuffer = ByteBuffer.allocate(j);
                this.getBytes(i, bytebuffer);
                bytebuffer.flip();
            }

            return ByteBufUtil.decodeString(bytebuffer, charset);
        }
    }

    @Override
    public int indexOf(int i, int j, byte b0) {
        return ByteBufUtil.indexOf(this, i, j, b0);
    }

    @Override
    public int bytesBefore(byte b0) {
        return this.bytesBefore(this.readerIndex(), this.readableBytes(), b0);
    }

    @Override
    public int bytesBefore(int i, byte b0) {
        this.checkReadableBytes(i);
        return this.bytesBefore(this.readerIndex(), i, b0);
    }

    @Override
    public int bytesBefore(int i, int j, byte b0) {
        int k = this.indexOf(i, i + j, b0);

        return k < 0 ? -1 : k - i;
    }

    @Override
    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        int i = this.readerIndex;
        int j = this.writerIndex - i;

        return this.forEachByteAsc0(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        this.checkIndex(i, j);
        return this.forEachByteAsc0(i, j, bytebufprocessor);
    }

    private int forEachByteAsc0(int i, int j, ByteBufProcessor bytebufprocessor) {
        if (bytebufprocessor == null) {
            throw new NullPointerException("processor");
        } else if (j == 0) {
            return -1;
        } else {
            int k = i + j;
            int l = i;

            while (true) {
                try {
                    if (!bytebufprocessor.process(this._getByte(l))) {
                        return l;
                    }

                    ++l;
                    if (l < k) {
                        continue;
                    }
                } catch (Exception exception) {
                    PlatformDependent.throwException(exception);
                }

                return -1;
            }
        }
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        int i = this.readerIndex;
        int j = this.writerIndex - i;

        return this.forEachByteDesc0(i, j, bytebufprocessor);
    }

    @Override
    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        this.checkIndex(i, j);
        return this.forEachByteDesc0(i, j, bytebufprocessor);
    }

    private int forEachByteDesc0(int i, int j, ByteBufProcessor bytebufprocessor) {
        if (bytebufprocessor == null) {
            throw new NullPointerException("processor");
        } else if (j == 0) {
            return -1;
        } else {
            int k = i + j - 1;

            while (true) {
                try {
                    if (!bytebufprocessor.process(this._getByte(k))) {
                        return k;
                    }

                    --k;
                    if (k >= i) {
                        continue;
                    }
                } catch (Exception exception) {
                    PlatformDependent.throwException(exception);
                }

                return -1;
            }
        }
    }

    @Override
    public int hashCode() {
        return ByteBufUtil.hashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return this == object ? true : (object instanceof ByteBuf ? ByteBufUtil.equals(this, (ByteBuf) object) : false);
    }

    @Override
    public int compareTo(ByteBuf bytebuf) {
        return ByteBufUtil.compare(this, bytebuf);
    }

    @Override
    public String toString() {
        if (this.refCnt() == 0) {
            return this.getClass().getSimpleName() + "(freed)";
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            stringbuilder.append(this.getClass().getSimpleName());
            stringbuilder.append("(ridx: ");
            stringbuilder.append(this.readerIndex);
            stringbuilder.append(", widx: ");
            stringbuilder.append(this.writerIndex);
            stringbuilder.append(", cap: ");
            stringbuilder.append(this.capacity());
            if (this.maxCapacity != Integer.MAX_VALUE) {
                stringbuilder.append('/');
                stringbuilder.append(this.maxCapacity);
            }

            ByteBuf bytebuf = this.unwrap();

            if (bytebuf != null) {
                stringbuilder.append(", unwrapped: ");
                stringbuilder.append(bytebuf);
            }

            stringbuilder.append(')');
            return stringbuilder.toString();
        }
    }

    protected final void checkIndex(int i) {
        this.ensureAccessible();
        if (i < 0 || i >= this.capacity()) {
            throw new IndexOutOfBoundsException(String.format("index: %d (expected: range(0, %d))", i, this.capacity()));
        }
    }

    protected final void checkIndex(int i, int j) {
        this.ensureAccessible();
        if (j < 0) {
            throw new IllegalArgumentException("length: " + j + " (expected: >= 0)");
        } else if (i < 0 || i > this.capacity() - j) {
            throw new IndexOutOfBoundsException(String.format("index: %d, length: %d (expected: range(0, %d))", i, j, this.capacity()));
        }
    }

    protected final void checkSrcIndex(int i, int j, int k, int l) {
        this.checkIndex(i, j);
        if (k < 0 || k > l - j) {
            throw new IndexOutOfBoundsException(String.format("srcIndex: %d, length: %d (expected: range(0, %d))", k, j, l));
        }
    }

    protected final void checkDstIndex(int i, int j, int k, int l) {
        this.checkIndex(i, j);
        if (k < 0 || k > l - j) {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", k, j, l));
        }
    }

    protected final void checkReadableBytes(int i) {
        this.ensureAccessible();
        if (i < 0) {
            throw new IllegalArgumentException("minimumReadableBytes: " + i + " (expected: >= 0)");
        } else if (this.readerIndex > this.writerIndex - i) {
            throw new IndexOutOfBoundsException(String.format("readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", this.readerIndex, i, this.writerIndex, this));
        }
    }

    protected final void ensureAccessible() {
        if (this.refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
        }
    }
}
