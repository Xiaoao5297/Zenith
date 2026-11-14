package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
public final class BloomFilter<T> implements Predicate<T>, Serializable {

    private final BloomFilterStrategies.BitArray bits;
    private final int numHashFunctions;
    private final Funnel<T> funnel;
    private final BloomFilter.Strategy strategy;

    private BloomFilter(BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray, int i, Funnel<T> funnel, BloomFilter.Strategy bloomfilter_strategy) {
        Preconditions.checkArgument(i > 0, "numHashFunctions (%s) must be > 0", i);
        Preconditions.checkArgument(i <= 255, "numHashFunctions (%s) must be <= 255", i);
        this.bits = (BloomFilterStrategies.BitArray) Preconditions.checkNotNull(bloomfilterstrategies_bitarray);
        this.numHashFunctions = i;
        this.funnel = (Funnel) Preconditions.checkNotNull(funnel);
        this.strategy = (BloomFilter.Strategy) Preconditions.checkNotNull(bloomfilter_strategy);
    }

    public BloomFilter<T> copy() {
        return new BloomFilter<>(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
    }

    public boolean mightContain(T t0) {
        return this.strategy.mightContain(t0, this.funnel, this.numHashFunctions, this.bits);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean apply(T t0) {
        return this.mightContain(t0);
    }

    public boolean put(T t0) {
        return this.strategy.put(t0, this.funnel, this.numHashFunctions, this.bits);
    }

    public double expectedFpp() {
        return Math.pow((double) this.bits.bitCount() / (double) this.bitSize(), (double) this.numHashFunctions);
    }

    @VisibleForTesting
    long bitSize() {
        return (long) this.bits.bitSize();
    }

    public boolean isCompatible(BloomFilter<T> bloomfilter) {
        Preconditions.checkNotNull(bloomfilter);
        return this != bloomfilter && this.numHashFunctions == bloomfilter.numHashFunctions && this.bitSize() == bloomfilter.bitSize() && this.strategy.equals(bloomfilter.strategy) && this.funnel.equals(bloomfilter.funnel);
    }

    public void putAll(BloomFilter<T> bloomfilter) {
        Preconditions.checkNotNull(bloomfilter);
        Preconditions.checkArgument(this != bloomfilter, "Cannot combine a BloomFilter with itself.");
        Preconditions.checkArgument(this.numHashFunctions == bloomfilter.numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", this.numHashFunctions, bloomfilter.numHashFunctions);
        Preconditions.checkArgument(this.bitSize() == bloomfilter.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", this.bitSize(), bloomfilter.bitSize());
        Preconditions.checkArgument(this.strategy.equals(bloomfilter.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, bloomfilter.strategy);
        Preconditions.checkArgument(this.funnel.equals(bloomfilter.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, bloomfilter.funnel);
        this.bits.putAll(bloomfilter.bits);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof BloomFilter)) {
            return false;
        } else {
            BloomFilter bloomfilter = (BloomFilter) object;

            return this.numHashFunctions == bloomfilter.numHashFunctions && this.funnel.equals(bloomfilter.funnel) && this.bits.equals(bloomfilter.bits) && this.strategy.equals(bloomfilter.strategy);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.numHashFunctions, this.funnel, this.strategy, this.bits);
    }

    public static <T> BloomFilter<T> create(Funnel<T> funnel, int i, double d0) {
        Preconditions.checkNotNull(funnel);
        Preconditions.checkArgument(i >= 0, "Expected insertions (%s) must be >= 0", i);
        Preconditions.checkArgument(d0 > 0.0D, "False positive probability (%s) must be > 0.0", d0);
        Preconditions.checkArgument(d0 < 1.0D, "False positive probability (%s) must be < 1.0", d0);
        if (i == 0) {
            i = 1;
        }

        long j = optimalNumOfBits((long) i, d0);
        int k = optimalNumOfHashFunctions((long) i, j);

        try {
            return new BloomFilter<>(new BloomFilterStrategies.BitArray(j), k, funnel, BloomFilterStrategies.MURMUR128_MITZ_32);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new IllegalArgumentException("Could not create BloomFilter of " + j + " bits", illegalargumentexception);
        }
    }

    public static <T> BloomFilter<T> create(Funnel<T> funnel, int i) {
        return create(funnel, i, 0.03D);
    }

    @VisibleForTesting
    static int optimalNumOfHashFunctions(long i, long j) {
        return Math.max(1, (int) Math.round((double) (j / i) * Math.log(2.0D)));
    }

    @VisibleForTesting
    static long optimalNumOfBits(long i, double d0) {
        if (d0 == 0.0D) {
            d0 = Double.MIN_VALUE;
        }

        return (long) ((double) (-i) * Math.log(d0) / (Math.log(2.0D) * Math.log(2.0D)));
    }

    private Object writeReplace() {
        return new BloomFilter.SerialForm<>(this);
    }

    private static class SerialForm<T> implements Serializable {

        final long[] data;
        final int numHashFunctions;
        final Funnel<T> funnel;
        final BloomFilter.Strategy strategy;
        private static final long serialVersionUID = 1L;

        SerialForm(BloomFilter<T> bloomfilter) {
            this.data = bloomfilter.bits.data;
            this.numHashFunctions = bloomfilter.numHashFunctions;
            this.funnel = bloomfilter.funnel;
            this.strategy = bloomfilter.strategy;
        }

        Object readResolve() {
            return new BloomFilter<>(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
        }
    }

    interface Strategy extends Serializable {

        <T> boolean put(T t0, Funnel<? super T> funnel, int i, BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray);

        <T> boolean mightContain(T t0, Funnel<? super T> funnel, int i, BloomFilterStrategies.BitArray bloomfilterstrategies_bitarray);

        int ordinal();
    }
}
