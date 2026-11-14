package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import java.math.RoundingMode;
import java.util.Arrays;

enum BloomFilterStrategies implements BloomFilter.Strategy {

    MURMUR128_MITZ_32 {
        @Override
        public <T> boolean put(T t0, Funnel<? super T> funnel, int i, BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray) {
            long j = Hashing.murmur3_128().hashObject(t0, funnel).asLong();
            int k = (int) j;
            int l = (int) (j >>> 32);
            boolean flag = false;

            for (int i1 = 1; i1 <= i; ++i1) {
                int j1 = k + i1 * l;

                if (j1 < 0) {
                    j1 = ~j1;
                }

                flag |= bloomfilterstrategies_bitarray.set(j1 % bloomfilterstrategies_bitarray.bitSize());
            }

            return flag;
        }

        @Override
        public <T> boolean mightContain(T t0, Funnel<? super T> funnel, int i, BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray) {
            long j = Hashing.murmur3_128().hashObject(t0, funnel).asLong();
            int k = (int) j;
            int l = (int) (j >>> 32);

            for (int i1 = 1; i1 <= i; ++i1) {
                int j1 = k + i1 * l;

                if (j1 < 0) {
                    j1 = ~j1;
                }

                if (!bloomfilterstrategies_bitarray.get(j1 % bloomfilterstrategies_bitarray.bitSize())) {
                    return false;
                }
            }

            return true;
        }
    };

    private BloomFilterStrategies() {}

    static class BitArray {

        final long[] data;
        int bitCount;

        BitArray(long i) {
            this(new long[Ints.checkedCast(LongMath.divide(i, 64L, RoundingMode.CEILING))]);
        }

        BitArray(long[] along) {
            Preconditions.checkArgument(along.length > 0, "data length is zero!");
            this.data = along;
            int i = 0;
            long[] along1 = along;
            int j = along.length;

            for (int k = 0; k < j; ++k) {
                long l = along1[k];

                i += Long.bitCount(l);
            }

            this.bitCount = i;
        }

        boolean set(int i) {
            if (!this.get(i)) {
                this.data[i >> 6] |= 1L << i;
                ++this.bitCount;
                return true;
            } else {
                return false;
            }
        }

        boolean get(int i) {
            return (this.data[i >> 6] & 1L << i) != 0L;
        }

        int bitSize() {
            return this.data.length * 64;
        }

        int bitCount() {
            return this.bitCount;
        }

        BloomFilterStrategies.BitArray copy() {
            return new BloomFilterStrategies.BitArray((long[]) this.data.clone());
        }

        void putAll(BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray) {
            Preconditions.checkArgument(this.data.length == bloomfilterstrategies_bitarray.data.length, "BitArrays must be of equal length (%s != %s)", this.data.length, bloomfilterstrategies_bitarray.data.length);
            this.bitCount = 0;

            for (int i = 0; i < this.data.length; ++i) {
                this.data[i] |= bloomfilterstrategies_bitarray.data[i];
                this.bitCount += Long.bitCount(this.data[i]);
            }

        }

        public boolean equals(Object object) {
            if (object instanceof BloomFilterStrategies.BitArray) {
                BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray = (BloomFilterStrategies.BitArray) object;

                return Arrays.equals(this.data, bloomfilterstrategies_bitarray.data);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Arrays.hashCode(this.data);
        }
    }
}
