package io.netty.buffer;

final class PoolSubpage<T> {

    final PoolChunk<T> chunk;
    final int memoryMapIdx;
    final int runOffset;
    final int pageSize;
    final long[] bitmap;
    PoolSubpage<T> prev;
    PoolSubpage<T> next;
    boolean doNotDestroy;
    int elemSize;
    int maxNumElems;
    int nextAvail;
    int bitmapLength;
    int numAvail;

    PoolSubpage(int i) {
        this.chunk = null;
        this.memoryMapIdx = -1;
        this.runOffset = -1;
        this.elemSize = -1;
        this.pageSize = i;
        this.bitmap = null;
    }

    PoolSubpage(PoolChunk<T> poolchunk, int i, int j, int k, int l) {
        this.chunk = poolchunk;
        this.memoryMapIdx = i;
        this.runOffset = j;
        this.pageSize = k;
        this.bitmap = new long[k >>> 10];
        this.init(l);
    }

    void init(int i) {
        this.doNotDestroy = true;
        this.elemSize = i;
        if (i != 0) {
            this.maxNumElems = this.numAvail = this.pageSize / i;
            this.nextAvail = 0;
            this.bitmapLength = this.maxNumElems >>> 6;
            if ((this.maxNumElems & 63) != 0) {
                ++this.bitmapLength;
            }

            for (int j = 0; j < this.bitmapLength; ++j) {
                this.bitmap[j] = 0L;
            }
        }

        this.addToPool();
    }

    long allocate() {
        if (this.elemSize == 0) {
            return this.toHandle(0);
        } else if (this.numAvail != 0 && this.doNotDestroy) {
            int i = this.nextAvail;
            int j = i >>> 6;
            int k = i & 63;

            assert (this.bitmap[j] >>> k & 1L) == 0L;

            this.bitmap[j] |= 1L << k;
            if (--this.numAvail == 0) {
                this.removeFromPool();
                this.nextAvail = -1;
            } else {
                this.nextAvail = this.findNextAvailable();
            }

            return this.toHandle(i);
        } else {
            return -1L;
        }
    }

    boolean free(int i) {
        if (this.elemSize == 0) {
            return true;
        } else {
            int j = i >>> 6;
            int k = i & 63;

            assert (this.bitmap[j] >>> k & 1L) != 0L;

            this.bitmap[j] ^= 1L << k;
            if (this.numAvail++ == 0) {
                this.nextAvail = i;
                this.addToPool();
                return true;
            } else if (this.numAvail != this.maxNumElems) {
                return true;
            } else if (this.prev == this.next) {
                return true;
            } else {
                this.doNotDestroy = false;
                this.removeFromPool();
                return false;
            }
        }
    }

    private void addToPool() {
        PoolSubpage poolsubpage = this.chunk.arena.findSubpagePoolHead(this.elemSize);

        assert this.prev == null && this.next == null;

        this.prev = poolsubpage;
        this.next = poolsubpage.next;
        this.next.prev = this;
        poolsubpage.next = this;
    }

    private void removeFromPool() {
        assert this.prev != null && this.next != null;

        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
    }

    private int findNextAvailable() {
        int i = -1;

        for (int j = 0; j < this.bitmapLength; ++j) {
            long k = this.bitmap[j];

            if (~k != 0L) {
                for (int l = 0; l < 64; ++l) {
                    if ((k & 1L) == 0L) {
                        i = j << 6 | l;
                        return i < this.maxNumElems ? i : -1;
                    }

                    k >>>= 1;
                }
            }
        }

        return i < this.maxNumElems ? i : -1;
    }

    private long toHandle(int i) {
        return 4611686018427387904L | (long) i << 32 | (long) this.memoryMapIdx;
    }

    public String toString() {
        return !this.doNotDestroy ? "(" + this.memoryMapIdx + ": not in use)" : String.valueOf('(') + this.memoryMapIdx + ": " + (this.maxNumElems - this.numAvail) + '/' + this.maxNumElems + ", offset: " + this.runOffset + ", length: " + this.pageSize + ", elemSize: " + this.elemSize + ')';
    }
}
