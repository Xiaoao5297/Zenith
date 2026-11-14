package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.ResourceLeak;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf {

    private final ResourceLeak leak;
    private final Recycler.Handle recyclerHandle;
    protected PoolChunk<T> chunk;
    protected long handle;
    protected T memory;
    protected int offset;
    protected int length;
    private int maxLength;
    private ByteBuffer tmpNioBuf;

    protected PooledByteBuf(Recycler.Handle recycler_handle, int i) {
        super(i);
        this.leak = PooledByteBuf.leakDetector.open(this);
        this.recyclerHandle = recycler_handle;
    }

    void init(PoolChunk<T> poolchunk, long i, int j, int k, int l) {
        assert i >= 0L;

        assert poolchunk != null;

        this.chunk = poolchunk;
        this.handle = i;
        this.memory = poolchunk.memory;
        this.offset = j;
        this.length = k;
        this.maxLength = l;
        this.setIndex(0, 0);
        this.tmpNioBuf = null;
    }

    void initUnpooled(PoolChunk<T> poolchunk, int i) {
        assert poolchunk != null;

        this.chunk = poolchunk;
        this.handle = 0L;
        this.memory = poolchunk.memory;
        this.offset = 0;
        this.length = this.maxLength = i;
        this.setIndex(0, 0);
        this.tmpNioBuf = null;
    }

    @Override
    public final int capacity() {
        return this.length;
    }

    @Override
    public final ByteBuf capacity(int i) {
        this.ensureAccessible();
        if (this.chunk.unpooled) {
            if (i == this.length) {
                return this;
            }
        } else if (i > this.length) {
            if (i <= this.maxLength) {
                this.length = i;
                return this;
            }
        } else {
            if (i >= this.length) {
                return this;
            }

            if (i > this.maxLength >>> 1) {
                if (this.maxLength > 512) {
                    this.length = i;
                    this.setIndex(Math.min(this.readerIndex(), i), Math.min(this.writerIndex(), i));
                    return this;
                }

                if (i > this.maxLength - 16) {
                    this.length = i;
                    this.setIndex(Math.min(this.readerIndex(), i), Math.min(this.writerIndex(), i));
                    return this;
                }
            }
        }

        this.chunk.arena.reallocate(this, i, true);
        return this;
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.chunk.arena.parent;
    }

    @Override
    public final ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public final ByteBuf unwrap() {
        return null;
    }

    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer bytebuffer = this.tmpNioBuf;

        if (bytebuffer == null) {
            this.tmpNioBuf = bytebuffer = this.newInternalNioBuffer(this.memory);
        }

        return bytebuffer;
    }

    protected abstract ByteBuffer newInternalNioBuffer(T t0);

    @Override
    protected final void deallocate() {
        if (this.handle >= 0L) {
            long i = this.handle;

            this.handle = -1L;
            this.memory = null;
            this.chunk.arena.free(this.chunk, i);
            if (this.leak != null) {
                this.leak.close();
            } else {
                this.recycle();
            }
        }

    }

    private void recycle() {
        Recycler.Handle recycler_handle = this.recyclerHandle;

        if (recycler_handle != null) {
            this.recycler().recycle(this, recycler_handle);
        }

    }

    protected abstract Recycler<?> recycler();

    protected final int idx(int i) {
        return this.offset + i;
    }
}
