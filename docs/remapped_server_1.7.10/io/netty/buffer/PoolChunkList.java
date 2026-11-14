package io.netty.buffer;

import io.netty.util.internal.StringUtil;

final class PoolChunkList<T> {

    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    PoolChunkList<T> prevList;
    private final int minUsage;
    private final int maxUsage;
    private PoolChunk<T> head;

    PoolChunkList(PoolArena<T> poolarena, PoolChunkList<T> poolchunklist, int i, int j) {
        this.arena = poolarena;
        this.nextList = poolchunklist;
        this.minUsage = i;
        this.maxUsage = j;
    }

    boolean allocate(PooledByteBuf<T> pooledbytebuf, int i, int j) {
        if (this.head == null) {
            return false;
        } else {
            PoolChunk poolchunk = this.head;

            do {
                long k = poolchunk.allocate(j);

                if (k >= 0L) {
                    poolchunk.initBuf(pooledbytebuf, k, i);
                    if (poolchunk.usage() >= this.maxUsage) {
                        this.remove(poolchunk);
                        this.nextList.add(poolchunk);
                    }

                    return true;
                }

                poolchunk = poolchunk.next;
            } while (poolchunk != null);

            return false;
        }
    }

    void free(PoolChunk<T> poolchunk, long i) {
        poolchunk.free(i);
        if (poolchunk.usage() < this.minUsage) {
            this.remove(poolchunk);
            if (this.prevList == null) {
                assert poolchunk.usage() == 0;

                this.arena.destroyChunk(poolchunk);
            } else {
                this.prevList.add(poolchunk);
            }
        }

    }

    void add(PoolChunk<T> poolchunk) {
        if (poolchunk.usage() >= this.maxUsage) {
            this.nextList.add(poolchunk);
        } else {
            poolchunk.parent = this;
            if (this.head == null) {
                this.head = poolchunk;
                poolchunk.prev = null;
                poolchunk.next = null;
            } else {
                poolchunk.prev = null;
                poolchunk.next = this.head;
                this.head.prev = poolchunk;
                this.head = poolchunk;
            }

        }
    }

    private void remove(PoolChunk<T> poolchunk) {
        if (poolchunk == this.head) {
            this.head = poolchunk.next;
            if (this.head != null) {
                this.head.prev = null;
            }
        } else {
            PoolChunk poolchunk1 = poolchunk.next;

            poolchunk.prev.next = poolchunk1;
            if (poolchunk1 != null) {
                poolchunk1.prev = poolchunk.prev;
            }
        }

    }

    public String toString() {
        if (this.head == null) {
            return "none";
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            PoolChunk poolchunk = this.head;

            while (true) {
                stringbuilder.append(poolchunk);
                poolchunk = poolchunk.next;
                if (poolchunk == null) {
                    return stringbuilder.toString();
                }

                stringbuilder.append(StringUtil.NEWLINE);
            }
        }
    }
}
