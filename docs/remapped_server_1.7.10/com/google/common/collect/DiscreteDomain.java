package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@GwtCompatible
@Beta
public abstract class DiscreteDomain<C extends Comparable> {

    public static DiscreteDomain<Integer> integers() {
        return DiscreteDomain.IntegerDomain.INSTANCE;
    }

    public static DiscreteDomain<Long> longs() {
        return DiscreteDomain.LongDomain.INSTANCE;
    }

    public static DiscreteDomain<BigInteger> bigIntegers() {
        return DiscreteDomain.BigIntegerDomain.INSTANCE;
    }

    protected DiscreteDomain() {}

    public abstract C next(C c0);

    public abstract C previous(C c0);

    public abstract long distance(C c0, C c1);

    public C minValue() {
        throw new NoSuchElementException();
    }

    public C maxValue() {
        throw new NoSuchElementException();
    }

    private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {

        private static final DiscreteDomain.BigIntegerDomain INSTANCE = new DiscreteDomain.BigIntegerDomain();
        private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        private static final long serialVersionUID = 0L;

        private BigIntegerDomain() {}

        public BigInteger next(BigInteger biginteger) {
            return biginteger.add(BigInteger.ONE);
        }

        public BigInteger previous(BigInteger biginteger) {
            return biginteger.subtract(BigInteger.ONE);
        }

        public long distance(BigInteger biginteger, BigInteger biginteger1) {
            return biginteger1.subtract(biginteger).max(DiscreteDomain.BigIntegerDomain.MIN_LONG).min(DiscreteDomain.BigIntegerDomain.MAX_LONG).longValue();
        }

        private Object readResolve() {
            return DiscreteDomain.BigIntegerDomain.INSTANCE;
        }

        public String toString() {
            return "DiscreteDomain.bigIntegers()";
        }
    }

    private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {

        private static final DiscreteDomain.LongDomain INSTANCE = new DiscreteDomain.LongDomain();
        private static final long serialVersionUID = 0L;

        private LongDomain() {}

        public Long next(Long olong) {
            long i = olong;

            return i == Long.MAX_VALUE ? null : i + 1L;
        }

        public Long previous(Long olong) {
            long i = olong;

            return i == Long.MIN_VALUE ? null : i - 1L;
        }

        public long distance(Long olong, Long olong1) {
            long i = olong1 - olong;

            return olong1 > olong && i < 0L ? Long.MAX_VALUE : (olong1 < olong && i > 0L ? Long.MIN_VALUE : i);
        }

        @Override
        public Long minValue() {
            return Long.MIN_VALUE;
        }

        @Override
        public Long maxValue() {
            return Long.MAX_VALUE;
        }

        private Object readResolve() {
            return DiscreteDomain.LongDomain.INSTANCE;
        }

        public String toString() {
            return "DiscreteDomain.longs()";
        }
    }

    private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {

        private static final DiscreteDomain.IntegerDomain INSTANCE = new DiscreteDomain.IntegerDomain();
        private static final long serialVersionUID = 0L;

        private IntegerDomain() {}

        public Integer next(Integer integer) {
            int i = integer;

            return i == Integer.MAX_VALUE ? null : i + 1;
        }

        public Integer previous(Integer integer) {
            int i = integer;

            return i == Integer.MIN_VALUE ? null : i - 1;
        }

        public long distance(Integer integer, Integer integer1) {
            return (long) integer1 - (long) integer;
        }

        @Override
        public Integer minValue() {
            return Integer.MIN_VALUE;
        }

        @Override
        public Integer maxValue() {
            return Integer.MAX_VALUE;
        }

        private Object readResolve() {
            return DiscreteDomain.IntegerDomain.INSTANCE;
        }

        public String toString() {
            return "DiscreteDomain.integers()";
        }
    }
}
