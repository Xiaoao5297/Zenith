package io.netty.buffer;

import io.netty.util.Recycler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledDirectByteBuf extends PooledByteBuf<ByteBuffer> {

    private static final Recycler<PooledDirectByteBuf> RECYCLER = new Recycler<PooledDirectByteBuf>() {
        @Override
        protected PooledDirectByteBuf newObject(Recycler.Handle recycler_handle) {
            return new PooledDirectByteBuf(recycler_handle, 0);
        }
    };

    static PooledDirectByteBuf newInstance(int i) {
        PooledDirectByteBuf pooleddirectbytebuf = (PooledDirectByteBuf) PooledDirectByteBuf.RECYCLER.get();

        pooleddirectbytebuf.setRefCnt(1);
        pooleddirectbytebuf.maxCapacity(i);
        return pooleddirectbytebuf;
    }

    private PooledDirectByteBuf(Recycler.Handle recycler_handle, int i) {
        super(recycler_handle, i);
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
        return ((ByteBuffer) this.memory).get(this.idx(i));
    }

    @Override
    protected short _getShort(int i) {
        return ((ByteBuffer) this.memory).getShort(this.idx(i));
    }

    @Override
    protected int _getUnsignedMedium(int i) {
        i = this.idx(i);
        return (((ByteBuffer) this.memory).get(i) & 255) << 16 | (((ByteBuffer) this.memory).get(i + 1) & 255) << 8 | ((ByteBuffer) this.memory).get(i + 2) & 255;
    }

    @Override
    protected int _getInt(int i) {
        return ((ByteBuffer) this.memory).getInt(this.idx(i));
    }

    @Override
    protected long _getLong(int i) {
        return ((ByteBuffer) this.memory).getLong(this.idx(i));
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
        ByteBuffer bytebuffer;

        if (flag) {
            bytebuffer = this.internalNioBuffer();
        } else {
            bytebuffer = ((ByteBuffer) this.memory).duplicate();
        }

        i = this.idx(i);
        bytebuffer.clear().position(i).limit(i + k);
        bytebuffer.get(abyte, j, k);
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
        this.getBytes(i, outputstream, j, false);
        return this;
    }

    private void getBytes(int i, OutputStream outputstream, int j, boolean flag) throws IOException {
        this.checkIndex(i, j);
        if (j != 0) {
            byte[] abyte = new byte[j];
            ByteBuffer bytebuffer;

            if (flag) {
                bytebuffer = this.internalNioBuffer();
            } else {
                bytebuffer = ((ByteBuffer) this.memory).duplicate();
            }

            bytebuffer.clear().position(this.idx(i));
            bytebuffer.get(abyte);
            outputstream.write(abyte);
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
        ((ByteBuffer) this.memory).put(this.idx(i), (byte) j);
    }

    @Override
    protected void _setShort(int i, int j) {
        ((ByteBuffer) this.memory).putShort(this.idx(i), (short) j);
    }

    @Override
    protected void _setMedium(int i, int j) {
        i = this.idx(i);
        ((ByteBuffer) this.memory).put(i, (byte) (j >>> 16));
        ((ByteBuffer) this.memory).put(i + 1, (byte) (j >>> 8));
        ((ByteBuffer) this.memory).put(i + 2, (byte) j);
    }

    @Override
    protected void _setInt(int i, int j) {
        ((ByteBuffer) this.memory).putInt(this.idx(i), j);
    }

    @Override
    protected void _setLong(int i, long j) {
        ((ByteBuffer) this.memory).putLong(this.idx(i), j);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        this.checkSrcIndex(i, k, j, bytebuf.capacity());
        if (bytebuf.hasArray()) {
            this.setBytes(i, bytebuf.array(), bytebuf.arrayOffset() + j, k);
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

        i = this.idx(i);
        bytebuffer.clear().position(i).limit(i + k);
        bytebuffer.put(abyte, j, k);
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

        if (k <= 0) {
            return k;
        } else {
            ByteBuffer bytebuffer = this.internalNioBuffer();

            bytebuffer.clear().position(this.idx(i));
            bytebuffer.put(abyte, 0, k);
            return k;
        }
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
        ByteBuf bytebuf = this.alloc().directBuffer(j, this.maxCapacity());

        bytebuf.writeBytes((ByteBuf) this, i, j);
        return bytebuf;
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer(int i, int j) {
        this.checkIndex(i, j);
        i = this.idx(i);
        return (ByteBuffer) ((ByteBuffer) this.memory).duplicate().position(i).limit(i + j);
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int j) {
        return new ByteBuffer[]{this.nioBuffer(i, j)};
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
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Recycler<?> recycler() {
        return PooledDirectByteBuf.RECYCLER;
    }
}
