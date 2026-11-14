package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.ArrayList;

public class AdaptiveRecvByteBufAllocator implements RecvByteBufAllocator {

    static final int DEFAULT_MINIMUM = 64;
    static final int DEFAULT_INITIAL = 1024;
    static final int DEFAULT_MAXIMUM = 65536;
    private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    public static final AdaptiveRecvByteBufAllocator DEFAULT;
    private final int minIndex;
    private final int maxIndex;
    private final int initial;

    private static int getSizeTableIndex(int i) {
        int j = 0;
        int k = AdaptiveRecvByteBufAllocator.SIZE_TABLE.length - 1;

        while (k >= j) {
            if (k == j) {
                return k;
            }

            int l = j + k >>> 1;
            int i1 = AdaptiveRecvByteBufAllocator.SIZE_TABLE[l];
            int j1 = AdaptiveRecvByteBufAllocator.SIZE_TABLE[l + 1];

            if (i > j1) {
                j = l + 1;
            } else {
                if (i >= i1) {
                    if (i == i1) {
                        return l;
                    }

                    return l + 1;
                }

                k = l - 1;
            }
        }

        return j;
    }

    private AdaptiveRecvByteBufAllocator() {
        this(64, 1024, 65536);
    }

    public AdaptiveRecvByteBufAllocator(int i, int j, int k) {
        if (i <= 0) {
            throw new IllegalArgumentException("minimum: " + i);
        } else if (j < i) {
            throw new IllegalArgumentException("initial: " + j);
        } else if (k < j) {
            throw new IllegalArgumentException("maximum: " + k);
        } else {
            int l = getSizeTableIndex(i);

            if (AdaptiveRecvByteBufAllocator.SIZE_TABLE[l] < i) {
                this.minIndex = l + 1;
            } else {
                this.minIndex = l;
            }

            int i1 = getSizeTableIndex(k);

            if (AdaptiveRecvByteBufAllocator.SIZE_TABLE[i1] > k) {
                this.maxIndex = i1 - 1;
            } else {
                this.maxIndex = i1;
            }

            this.initial = j;
        }
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new AdaptiveRecvByteBufAllocator.HandleImpl(this.minIndex, this.maxIndex, this.initial);
    }

    static {
        ArrayList arraylist = new ArrayList();

        int i;

        for (i = 16; i < 512; i += 16) {
            arraylist.add(i);
        }

        for (i = 512; i > 0; i <<= 1) {
            arraylist.add(i);
        }

        SIZE_TABLE = new int[arraylist.size()];

        for (i = 0; i < AdaptiveRecvByteBufAllocator.SIZE_TABLE.length; ++i) {
            AdaptiveRecvByteBufAllocator.SIZE_TABLE[i] = (Integer) arraylist.get(i);
        }

        DEFAULT = new AdaptiveRecvByteBufAllocator();
    }

    private static final class HandleImpl implements RecvByteBufAllocator.Handle {

        private final int minIndex;
        private final int maxIndex;
        private int index;
        private int nextReceiveBufferSize;
        private boolean decreaseNow;

        HandleImpl(int i, int j, int k) {
            this.minIndex = i;
            this.maxIndex = j;
            this.index = AdaptiveRecvByteBufAllocator.getSizeTableIndex(k);
            this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
        }

        @Override
        public ByteBuf allocate(ByteBufAllocator bytebufallocator) {
            return bytebufallocator.ioBuffer(this.nextReceiveBufferSize);
        }

        @Override
        public int guess() {
            return this.nextReceiveBufferSize;
        }

        @Override
        public void record(int i) {
            if (i <= AdaptiveRecvByteBufAllocator.SIZE_TABLE[Math.max(0, this.index - 1 - 1)]) {
                if (this.decreaseNow) {
                    this.index = Math.max(this.index - 1, this.minIndex);
                    this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
                    this.decreaseNow = false;
                } else {
                    this.decreaseNow = true;
                }
            } else if (i >= this.nextReceiveBufferSize) {
                this.index = Math.min(this.index + 4, this.maxIndex);
                this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
                this.decreaseNow = false;
            }

        }
    }
}
