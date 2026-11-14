package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;

abstract class PoolArena<T> {

    final PooledByteBufAllocator parent;
    private final int pageSize;
    private final int maxOrder;
    private final int pageShifts;
    private final int chunkSize;
    private final int subpageOverflowMask;
    private final PoolSubpage<T>[] tinySubpagePools;
    private final PoolSubpage<T>[] smallSubpagePools;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> qInit;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;

    protected PoolArena(PooledByteBufAllocator pooledbytebufallocator, int i, int j, int k, int l) {
        this.parent = pooledbytebufallocator;
        this.pageSize = i;
        this.maxOrder = j;
        this.pageShifts = k;
        this.chunkSize = l;
        this.subpageOverflowMask = ~(i - 1);
        this.tinySubpagePools = this.newSubpagePoolArray(32);

        int i1;

        for (i1 = 0; i1 < this.tinySubpagePools.length; ++i1) {
            this.tinySubpagePools[i1] = this.newSubpagePoolHead(i);
        }

        this.smallSubpagePools = this.newSubpagePoolArray(k - 9);

        for (i1 = 0; i1 < this.smallSubpagePools.length; ++i1) {
            this.smallSubpagePools[i1] = this.newSubpagePoolHead(i);
        }

        this.q100 = new PoolChunkList<>(this, (PoolChunkList) null, 100, Integer.MAX_VALUE);
        this.q075 = new PoolChunkList<>(this, this.q100, 75, 100);
        this.q050 = new PoolChunkList<>(this, this.q075, 50, 100);
        this.q025 = new PoolChunkList<>(this, this.q050, 25, 75);
        this.q000 = new PoolChunkList<>(this, this.q025, 1, 50);
        this.qInit = new PoolChunkList<>(this, this.q000, Integer.MIN_VALUE, 25);
        this.q100.prevList = this.q075;
        this.q075.prevList = this.q050;
        this.q050.prevList = this.q025;
        this.q025.prevList = this.q000;
        this.q000.prevList = null;
        this.qInit.prevList = this.qInit;
    }

    private PoolSubpage<T> newSubpagePoolHead(int i) {
        PoolSubpage poolsubpage = new PoolSubpage<>(i);

        poolsubpage.prev = poolsubpage;
        poolsubpage.next = poolsubpage;
        return poolsubpage;
    }

    private PoolSubpage<T>[] newSubpagePoolArray(int i) {
        return new PoolSubpage[i];
    }

    PooledByteBuf<T> allocate(PoolThreadCache poolthreadcache, int i, int j) {
        PooledByteBuf pooledbytebuf = this.newByteBuf(j);

        this.allocate(poolthreadcache, pooledbytebuf, i);
        return pooledbytebuf;
    }

    private void allocate(PoolThreadCache poolthreadcache, PooledByteBuf<T> pooledbytebuf, int i) {
        int j = this.normalizeCapacity(i);

        if ((j & this.subpageOverflowMask) == 0) {
            int k;
            PoolSubpage[] apoolsubpage;

            if ((j & -512) == 0) {
                k = j >>> 4;
                apoolsubpage = this.tinySubpagePools;
            } else {
                k = 0;

                for (int l = j >>> 10; l != 0; ++k) {
                    l >>>= 1;
                }

                apoolsubpage = this.smallSubpagePools;
            }

            synchronized (this) {
                PoolSubpage poolsubpage = apoolsubpage[k];
                PoolSubpage poolsubpage1 = poolsubpage.next;

                if (poolsubpage1 != poolsubpage) {
                    assert poolsubpage1.doNotDestroy && poolsubpage1.elemSize == j;

                    long i1 = poolsubpage1.allocate();

                    assert i1 >= 0L;

                    poolsubpage1.chunk.initBufWithSubpage(pooledbytebuf, i1, i);
                    return;
                }
            }
        } else if (j > this.chunkSize) {
            this.allocateHuge(pooledbytebuf, i);
            return;
        }

        this.allocateNormal(pooledbytebuf, i, j);
    }

    private synchronized void allocateNormal(PooledByteBuf<T> pooledbytebuf, int i, int j) {
        if (!this.q050.allocate(pooledbytebuf, i, j) && !this.q025.allocate(pooledbytebuf, i, j) && !this.q000.allocate(pooledbytebuf, i, j) && !this.qInit.allocate(pooledbytebuf, i, j) && !this.q075.allocate(pooledbytebuf, i, j) && !this.q100.allocate(pooledbytebuf, i, j)) {
            PoolChunk poolchunk = this.newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
            long k = poolchunk.allocate(j);

            assert k > 0L;

            poolchunk.initBuf(pooledbytebuf, k, i);
            this.qInit.add(poolchunk);
        }
    }

    private void allocateHuge(PooledByteBuf<T> pooledbytebuf, int i) {
        pooledbytebuf.initUnpooled(this.newUnpooledChunk(i), i);
    }

    synchronized void free(PoolChunk<T> poolchunk, long i) {
        if (poolchunk.unpooled) {
            this.destroyChunk(poolchunk);
        } else {
            poolchunk.parent.free(poolchunk, i);
        }

    }

    PoolSubpage<T> findSubpagePoolHead(int i) {
        int j;
        PoolSubpage[] apoolsubpage;

        if ((i & -512) == 0) {
            j = i >>> 4;
            apoolsubpage = this.tinySubpagePools;
        } else {
            j = 0;

            for (i >>>= 10; i != 0; ++j) {
                i >>>= 1;
            }

            apoolsubpage = this.smallSubpagePools;
        }

        return apoolsubpage[j];
    }

    private int normalizeCapacity(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("capacity: " + i + " (expected: 0+)");
        } else if (i >= this.chunkSize) {
            return i;
        } else if ((i & -512) != 0) {
            int j = i | i >>> 1;

            j |= j >>> 2;
            j |= j >>> 4;
            j |= j >>> 8;
            j |= j >>> 16;
            ++j;
            if (j < 0) {
                j >>>= 1;
            }

            return j;
        } else {
            return (i & 15) == 0 ? i : (i & -16) + 16;
        }
    }

    void reallocate(PooledByteBuf<T> pooledbytebuf, int i, boolean flag) {
        if (i >= 0 && i <= pooledbytebuf.maxCapacity()) {
            int j = pooledbytebuf.length;

            if (j != i) {
                PoolChunk poolchunk = pooledbytebuf.chunk;
                long k = pooledbytebuf.handle;
                Object object = pooledbytebuf.memory;
                int l = pooledbytebuf.offset;
                int i1 = pooledbytebuf.readerIndex();
                int j1 = pooledbytebuf.writerIndex();

                this.allocate((PoolThreadCache) this.parent.threadCache.get(), pooledbytebuf, i);
                if (i > j) {
                    this.memoryCopy(object, l + i1, pooledbytebuf.memory, pooledbytebuf.offset + i1, j1 - i1);
                } else if (i < j) {
                    if (i1 < i) {
                        if (j1 > i) {
                            j1 = i;
                        }

                        this.memoryCopy(object, l + i1, pooledbytebuf.memory, pooledbytebuf.offset + i1, j1 - i1);
                    } else {
                        j1 = i;
                        i1 = i;
                    }
                }

                pooledbytebuf.setIndex(i1, j1);
                if (flag) {
                    this.free(poolchunk, k);
                }

            }
        } else {
            throw new IllegalArgumentException("newCapacity: " + i);
        }
    }

    protected abstract PoolChunk<T> newChunk(int i, int j, int k, int l);

    protected abstract PoolChunk<T> newUnpooledChunk(int i);

    protected abstract PooledByteBuf<T> newByteBuf(int i);

    protected abstract void memoryCopy(T t0, int i, T t1, int j, int k);

    protected abstract void destroyChunk(PoolChunk<T> poolchunk);

    public synchronized String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("Chunk(s) at 0~25%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.qInit);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("Chunk(s) at 0~50%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.q000);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("Chunk(s) at 25~75%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.q025);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("Chunk(s) at 50~100%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.q050);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("Chunk(s) at 75~100%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.q075);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("Chunk(s) at 100%:");
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.q100);
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("tiny subpages:");

        PoolSubpage poolsubpage;
        int i;
        PoolSubpage poolsubpage1;

        for (i = 1; i < this.tinySubpagePools.length; ++i) {
            poolsubpage = this.tinySubpagePools[i];
            if (poolsubpage.next != poolsubpage) {
                stringbuilder.append(StringUtil.NEWLINE);
                stringbuilder.append(i);
                stringbuilder.append(": ");
                poolsubpage1 = poolsubpage.next;

                do {
                    stringbuilder.append(poolsubpage1);
                    poolsubpage1 = poolsubpage1.next;
                } while (poolsubpage1 != poolsubpage);
            }
        }

        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("small subpages:");

        for (i = 1; i < this.smallSubpagePools.length; ++i) {
            poolsubpage = this.smallSubpagePools[i];
            if (poolsubpage.next != poolsubpage) {
                stringbuilder.append(StringUtil.NEWLINE);
                stringbuilder.append(i);
                stringbuilder.append(": ");
                poolsubpage1 = poolsubpage.next;

                do {
                    stringbuilder.append(poolsubpage1);
                    poolsubpage1 = poolsubpage1.next;
                } while (poolsubpage1 != poolsubpage);
            }
        }

        stringbuilder.append(StringUtil.NEWLINE);
        return stringbuilder.toString();
    }

    static final class DirectArena extends PoolArena<ByteBuffer> {

        private static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();

        DirectArena(PooledByteBufAllocator pooledbytebufallocator, int i, int j, int k, int l) {
            super(pooledbytebufallocator, i, j, k, l);
        }

        @Override
        protected PoolChunk<ByteBuffer> newChunk(int i, int j, int k, int l) {
            return new PoolChunk<>(this, ByteBuffer.allocateDirect(l), i, j, k, l);
        }

        @Override
        protected PoolChunk<ByteBuffer> newUnpooledChunk(int i) {
            return new PoolChunk<>(this, ByteBuffer.allocateDirect(i), i);
        }

        @Override
        protected void destroyChunk(PoolChunk<ByteBuffer> poolchunk) {
            PlatformDependent.freeDirectBuffer((ByteBuffer) poolchunk.memory);
        }

        @Override
        protected PooledByteBuf<ByteBuffer> newByteBuf(int i) {
            return (PooledByteBuf) (PoolArena.DirectArena.HAS_UNSAFE ? PooledUnsafeDirectByteBuf.newInstance(i) : PooledDirectByteBuf.newInstance(i));
        }

        protected void memoryCopy(ByteBuffer bytebuffer, int i, ByteBuffer bytebuffer1, int j, int k) {
            if (k != 0) {
                if (PoolArena.DirectArena.HAS_UNSAFE) {
                    PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(bytebuffer) + (long) i, PlatformDependent.directBufferAddress(bytebuffer1) + (long) j, (long) k);
                } else {
                    bytebuffer = bytebuffer.duplicate();
                    bytebuffer1 = bytebuffer1.duplicate();
                    bytebuffer.position(i).limit(i + k);
                    bytebuffer1.position(j);
                    bytebuffer1.put(bytebuffer);
                }

            }
        }
    }

    static final class HeapArena extends PoolArena<byte[]> {

        HeapArena(PooledByteBufAllocator pooledbytebufallocator, int i, int j, int k, int l) {
            super(pooledbytebufallocator, i, j, k, l);
        }

        @Override
        protected PoolChunk<byte[]> newChunk(int i, int j, int k, int l) {
            return new PoolChunk<>(this, new byte[l], i, j, k, l);
        }

        @Override
        protected PoolChunk<byte[]> newUnpooledChunk(int i) {
            return new PoolChunk<>(this, new byte[i], i);
        }

        @Override
        protected void destroyChunk(PoolChunk<byte[]> poolchunk) {}

        @Override
        protected PooledByteBuf<byte[]> newByteBuf(int i) {
            return PooledHeapByteBuf.newInstance(i);
        }

        protected void memoryCopy(byte[] abyte, int i, byte[] abyte1, int j, int k) {
            if (k != 0) {
                System.arraycopy(abyte, i, abyte1, j, k);
            }
        }
    }
}
