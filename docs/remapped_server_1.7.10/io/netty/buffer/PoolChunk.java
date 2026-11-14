package io.netty.buffer;

final class PoolChunk<T> {

    private static final int ST_UNUSED = 0;
    private static final int ST_BRANCH = 1;
    private static final int ST_ALLOCATED = 2;
    private static final int ST_ALLOCATED_SUBPAGE = 3;
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 281474976710655L;
    final PoolArena<T> arena;
    final T memory;
    final boolean unpooled;
    private final int[] memoryMap;
    private final PoolSubpage<T>[] subpages;
    private final int subpageOverflowMask;
    private final int pageSize;
    private final int pageShifts;
    private final int chunkSize;
    private final int maxSubpageAllocs;
    private long random = (System.nanoTime() ^ 25214903917L) & 281474976710655L;
    private int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;

    PoolChunk(PoolArena<T> poolarena, T t0, int i, int j, int k, int l) {
        this.unpooled = false;
        this.arena = poolarena;
        this.memory = t0;
        this.pageSize = i;
        this.pageShifts = k;
        this.chunkSize = l;
        this.subpageOverflowMask = ~(i - 1);
        this.freeBytes = l;
        int i1 = l >>> k;

        this.maxSubpageAllocs = 1 << j;
        this.memoryMap = new int[this.maxSubpageAllocs << 1];
        int j1 = 1;

        for (int k1 = 0; k1 <= j; ++k1) {
            int l1 = i1 >>> k1;

            for (int i2 = 0; i2 < i1; i2 += l1) {
                this.memoryMap[j1++] = i2 << 17 | l1 << 2 | 0;
            }
        }

        this.subpages = this.newSubpageArray(this.maxSubpageAllocs);
    }

    PoolChunk(PoolArena<T> poolarena, T t0, int i) {
        this.unpooled = true;
        this.arena = poolarena;
        this.memory = t0;
        this.memoryMap = null;
        this.subpages = null;
        this.subpageOverflowMask = 0;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.chunkSize = i;
        this.maxSubpageAllocs = 0;
    }

    private PoolSubpage<T>[] newSubpageArray(int i) {
        return new PoolSubpage[i];
    }

    int usage() {
        if (this.freeBytes == 0) {
            return 100;
        } else {
            int i = (int) ((long) this.freeBytes * 100L / (long) this.chunkSize);

            return i == 0 ? 99 : 100 - i;
        }
    }

    long allocate(int i) {
        int j = this.memoryMap[1];

        return (i & this.subpageOverflowMask) != 0 ? this.allocateRun(i, 1, j) : this.allocateSubpage(i, 1, j);
    }

    private long allocateRun(int i, int j, int k) {
        while ((k & 2) == 0) {
            if ((k & 1) == 0) {
                return this.allocateRunSimple(i, j, k);
            }

            int l = j << 1 ^ this.nextRandom();
            long i1 = this.allocateRun(i, l, this.memoryMap[l]);

            if (i1 > 0L) {
                return i1;
            }

            j = l ^ 1;
            k = this.memoryMap[j];
        }

        return -1L;
    }

    private long allocateRunSimple(int i, int j, int k) {
        int l = this.runLength(k);

        if (i > l) {
            return -1L;
        } else {
            while (i != l) {
                int i1 = j << 1 ^ this.nextRandom();
                int j1 = i1 ^ 1;

                this.memoryMap[j] = k & -4 | 1;
                this.memoryMap[j1] = this.memoryMap[j1] & -4 | 0;
                l >>>= 1;
                j = i1;
                k = this.memoryMap[i1];
            }

            this.memoryMap[j] = k & -4 | 2;
            this.freeBytes -= l;
            return (long) j;
        }
    }

    private long allocateSubpage(int i, int j, int k) {
        int l = k & 3;

        if (l == 1) {
            int i1 = j << 1 ^ this.nextRandom();
            long j1 = this.branchSubpage(i, i1);

            return j1 > 0L ? j1 : this.branchSubpage(i, i1 ^ 1);
        } else if (l == 0) {
            return this.allocateSubpageSimple(i, j, k);
        } else if (l == 3) {
            PoolSubpage poolsubpage = this.subpages[this.subpageIdx(j)];
            int k1 = poolsubpage.elemSize;

            return i != k1 ? -1L : poolsubpage.allocate();
        } else {
            return -1L;
        }
    }

    private long allocateSubpageSimple(int i, int j, int k) {
        int l;
        int i1;

        for (i1 = this.runLength(k); i1 != this.pageSize; k = this.memoryMap[l]) {
            l = j << 1 ^ this.nextRandom();
            int j1 = l ^ 1;

            this.memoryMap[j] = k & -4 | 1;
            this.memoryMap[j1] = this.memoryMap[j1] & -4 | 0;
            i1 >>>= 1;
            j = l;
        }

        this.memoryMap[j] = k & -4 | 3;
        this.freeBytes -= i1;
        l = this.subpageIdx(j);
        PoolSubpage poolsubpage = this.subpages[l];

        if (poolsubpage == null) {
            poolsubpage = new PoolSubpage<>(this, j, this.runOffset(k), this.pageSize, i);
            this.subpages[l] = poolsubpage;
        } else {
            poolsubpage.init(i);
        }

        return poolsubpage.allocate();
    }

    private long branchSubpage(int i, int j) {
        int k = this.memoryMap[j];

        return (k & 3) != 2 ? this.allocateSubpage(i, j, k) : -1L;
    }

    void free(long i) {
        int j = (int) i;
        int k = (int) (i >>> 32);
        int l = this.memoryMap[j];
        int i1 = l & 3;

        if (i1 == 3) {
            assert k != 0;

            PoolSubpage poolsubpage = this.subpages[this.subpageIdx(j)];

            assert poolsubpage != null && poolsubpage.doNotDestroy;

            if (poolsubpage.free(k & 1073741823)) {
                return;
            }
        } else {
            assert i1 == 2 : "state: " + i1;

            assert k == 0;
        }

        this.freeBytes += this.runLength(l);

        while (true) {
            this.memoryMap[j] = l & -4 | 0;
            if (j == 1) {
                assert this.freeBytes == this.chunkSize;

                return;
            }

            if ((this.memoryMap[siblingIdx(j)] & 3) != 0) {
                return;
            }

            j = parentIdx(j);
            l = this.memoryMap[j];
        }
    }

    void initBuf(PooledByteBuf<T> pooledbytebuf, long i, int j) {
        int k = (int) i;
        int l = (int) (i >>> 32);

        if (l == 0) {
            int i1 = this.memoryMap[k];

            assert (i1 & 3) == 2 : String.valueOf(i1 & 3);

            pooledbytebuf.init(this, i, this.runOffset(i1), j, this.runLength(i1));
        } else {
            this.initBufWithSubpage(pooledbytebuf, i, l, j);
        }

    }

    void initBufWithSubpage(PooledByteBuf<T> pooledbytebuf, long i, int j) {
        this.initBufWithSubpage(pooledbytebuf, i, (int) (i >>> 32), j);
    }

    private void initBufWithSubpage(PooledByteBuf<T> pooledbytebuf, long i, int j, int k) {
        assert j != 0;

        int l = (int) i;
        int i1 = this.memoryMap[l];

        assert (i1 & 3) == 3;

        PoolSubpage poolsubpage = this.subpages[this.subpageIdx(l)];

        assert poolsubpage.doNotDestroy;

        assert k <= poolsubpage.elemSize;

        pooledbytebuf.init(this, i, this.runOffset(i1) + (j & 1073741823) * poolsubpage.elemSize, k, poolsubpage.elemSize);
    }

    private static int parentIdx(int i) {
        return i >>> 1;
    }

    private static int siblingIdx(int i) {
        return i ^ 1;
    }

    private int runLength(int i) {
        return (i >>> 2 & 32767) << this.pageShifts;
    }

    private int runOffset(int i) {
        return i >>> 17 << this.pageShifts;
    }

    private int subpageIdx(int i) {
        return i - this.maxSubpageAllocs;
    }

    private int nextRandom() {
        this.random = this.random * 25214903917L + 11L & 281474976710655L;
        return (int) (this.random >>> 47) & 1;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("Chunk(");
        stringbuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringbuilder.append(": ");
        stringbuilder.append(this.usage());
        stringbuilder.append("%, ");
        stringbuilder.append(this.chunkSize - this.freeBytes);
        stringbuilder.append('/');
        stringbuilder.append(this.chunkSize);
        stringbuilder.append(')');
        return stringbuilder.toString();
    }
}
