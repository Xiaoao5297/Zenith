package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@GwtCompatible(emulated = true)
public final class BigIntegerMath {

    @VisibleForTesting
    static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
    @VisibleForTesting
    static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
    private static final double LN_10 = Math.log(10.0D);
    private static final double LN_2 = Math.log(2.0D);

    public static boolean isPowerOfTwo(BigInteger biginteger) {
        Preconditions.checkNotNull(biginteger);
        return biginteger.signum() > 0 && biginteger.getLowestSetBit() == biginteger.bitLength() - 1;
    }

    public static int log2(BigInteger biginteger, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", (BigInteger) Preconditions.checkNotNull(biginteger));
        int i = biginteger.bitLength() - 1;

        switch (roundingmode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(biginteger));
            case DOWN:
            case FLOOR:
                return i;
            case UP:
            case CEILING:
                return isPowerOfTwo(biginteger) ? i : i + 1;
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
                BigInteger biginteger1;

                if (i < 256) {
                    biginteger1 = BigIntegerMath.SQRT2_PRECOMPUTED_BITS.shiftRight(256 - i);
                    if (biginteger.compareTo(biginteger1) <= 0) {
                        return i;
                    }

                    return i + 1;
                }

                biginteger1 = biginteger.pow(2);
                int j = biginteger1.bitLength() - 1;

                return j < 2 * i + 1 ? i : i + 1;
            default:
                throw new AssertionError();
        }
    }

    @GwtIncompatible("TODO")
    public static int log10(BigInteger biginteger, RoundingMode roundingmode) {
        MathPreconditions.checkPositive("x", biginteger);
        if (fitsInLong(biginteger)) {
            return LongMath.log10(biginteger.longValue(), roundingmode);
        } else {
            int i = (int) ((double) log2(biginteger, RoundingMode.FLOOR) * BigIntegerMath.LN_2 / BigIntegerMath.LN_10);
            BigInteger biginteger1 = BigInteger.TEN.pow(i);
            int j = biginteger1.compareTo(biginteger);

            if (j > 0) {
                do {
                    --i;
                    biginteger1 = biginteger1.divide(BigInteger.TEN);
                    j = biginteger1.compareTo(biginteger);
                } while (j > 0);
            } else {
                BigInteger biginteger2 = BigInteger.TEN.multiply(biginteger1);

                for (int k = biginteger2.compareTo(biginteger); k <= 0; k = biginteger2.compareTo(biginteger)) {
                    ++i;
                    biginteger1 = biginteger2;
                    j = k;
                    biginteger2 = BigInteger.TEN.multiply(biginteger2);
                }
            }

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(j == 0);
                case DOWN:
                case FLOOR:
                    return i;
                case UP:
                case CEILING:
                    return biginteger1.equals(biginteger) ? i : i + 1;
                case HALF_DOWN:
                case HALF_UP:
                case HALF_EVEN:
                    BigInteger biginteger3 = biginteger.pow(2);
                    BigInteger biginteger4 = biginteger1.pow(2).multiply(BigInteger.TEN);

                    return biginteger3.compareTo(biginteger4) <= 0 ? i : i + 1;
                default:
                    throw new AssertionError();
            }
        }
    }

    @GwtIncompatible("TODO")
    public static BigInteger sqrt(BigInteger biginteger, RoundingMode roundingmode) {
        MathPreconditions.checkNonNegative("x", biginteger);
        if (fitsInLong(biginteger)) {
            return BigInteger.valueOf(LongMath.sqrt(biginteger.longValue(), roundingmode));
        } else {
            BigInteger biginteger1 = sqrtFloor(biginteger);

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(biginteger1.pow(2).equals(biginteger));
                case DOWN:
                case FLOOR:
                    return biginteger1;
                case UP:
                case CEILING:
                    return biginteger1.pow(2).equals(biginteger) ? biginteger1 : biginteger1.add(BigInteger.ONE);
                case HALF_DOWN:
                case HALF_UP:
                case HALF_EVEN:
                    BigInteger biginteger2 = biginteger1.pow(2).add(biginteger1);

                    return biginteger2.compareTo(biginteger) >= 0 ? biginteger1 : biginteger1.add(BigInteger.ONE);
                default:
                    throw new AssertionError();
            }
        }
    }

    @GwtIncompatible("TODO")
    private static BigInteger sqrtFloor(BigInteger biginteger) {
        int i = log2(biginteger, RoundingMode.FLOOR);
        BigInteger biginteger1;

        if (i < 1023) {
            biginteger1 = sqrtApproxWithDoubles(biginteger);
        } else {
            int j = i - 52 & -2;

            biginteger1 = sqrtApproxWithDoubles(biginteger.shiftRight(j)).shiftLeft(j >> 1);
        }

        BigInteger biginteger2 = biginteger1.add(biginteger.divide(biginteger1)).shiftRight(1);

        if (biginteger1.equals(biginteger2)) {
            return biginteger1;
        } else {
            do {
                biginteger1 = biginteger2;
                biginteger2 = biginteger2.add(biginteger.divide(biginteger2)).shiftRight(1);
            } while (biginteger2.compareTo(biginteger1) < 0);

            return biginteger1;
        }
    }

    @GwtIncompatible("TODO")
    private static BigInteger sqrtApproxWithDoubles(BigInteger biginteger) {
        return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(biginteger)), RoundingMode.HALF_EVEN);
    }

    @GwtIncompatible("TODO")
    public static BigInteger divide(BigInteger biginteger, BigInteger biginteger1, RoundingMode roundingmode) {
        BigDecimal bigdecimal = new BigDecimal(biginteger);
        BigDecimal bigdecimal1 = new BigDecimal(biginteger1);

        return bigdecimal.divide(bigdecimal1, 0, roundingmode).toBigIntegerExact();
    }

    public static BigInteger factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        if (i < LongMath.factorials.length) {
            return BigInteger.valueOf(LongMath.factorials[i]);
        } else {
            int j = IntMath.divide(i * IntMath.log2(i, RoundingMode.CEILING), 64, RoundingMode.CEILING);
            ArrayList arraylist = new ArrayList(j);
            int k = LongMath.factorials.length;
            long l = LongMath.factorials[k - 1];
            int i1 = Long.numberOfTrailingZeros(l);

            l >>= i1;
            int j1 = LongMath.log2(l, RoundingMode.FLOOR) + 1;
            int k1 = LongMath.log2((long) k, RoundingMode.FLOOR) + 1;
            int l1 = 1 << k1 - 1;

            for (long i2 = (long) k; i2 <= (long) i; ++i2) {
                if ((i2 & (long) l1) != 0L) {
                    l1 <<= 1;
                    ++k1;
                }

                int j2 = Long.numberOfTrailingZeros(i2);
                long k2 = i2 >> j2;

                i1 += j2;
                int l2 = k1 - j2;

                if (l2 + j1 >= 64) {
                    arraylist.add(BigInteger.valueOf(l));
                    l = 1L;
                    boolean flag = false;
                }

                l *= k2;
                j1 = LongMath.log2(l, RoundingMode.FLOOR) + 1;
            }

            if (l > 1L) {
                arraylist.add(BigInteger.valueOf(l));
            }

            return listProduct(arraylist).shiftLeft(i1);
        }
    }

    static BigInteger listProduct(List<BigInteger> list) {
        return listProduct(list, 0, list.size());
    }

    static BigInteger listProduct(List<BigInteger> list, int i, int j) {
        switch (j - i) {
            case 0:
                return BigInteger.ONE;
            case 1:
                return (BigInteger) list.get(i);
            case 2:
                return ((BigInteger) list.get(i)).multiply((BigInteger) list.get(i + 1));
            case 3:
                return ((BigInteger) list.get(i)).multiply((BigInteger) list.get(i + 1)).multiply((BigInteger) list.get(i + 2));
            default:
                int k = j + i >>> 1;

                return listProduct(list, i, k).multiply(listProduct(list, k, j));
        }
    }

    public static BigInteger binomial(int i, int j) {
        MathPreconditions.checkNonNegative("n", i);
        MathPreconditions.checkNonNegative("k", j);
        Preconditions.checkArgument(j <= i, "k (%s) > n (%s)", j, i);
        if (j > i >> 1) {
            j = i - j;
        }

        if (j < LongMath.biggestBinomials.length && i <= LongMath.biggestBinomials[j]) {
            return BigInteger.valueOf(LongMath.binomial(i, j));
        } else {
            BigInteger biginteger = BigInteger.ONE;
            long k = (long) i;
            long l = 1L;
            int i1 = LongMath.log2((long) i, RoundingMode.CEILING);
            int j1 = i1;

            for (int k1 = 1; k1 < j; ++k1) {
                int l1 = i - k1;
                int i2 = k1 + 1;

                if (j1 + i1 >= 63) {
                    biginteger = biginteger.multiply(BigInteger.valueOf(k)).divide(BigInteger.valueOf(l));
                    k = (long) l1;
                    l = (long) i2;
                    j1 = i1;
                } else {
                    k *= (long) l1;
                    l *= (long) i2;
                    j1 += i1;
                }
            }

            return biginteger.multiply(BigInteger.valueOf(k)).divide(BigInteger.valueOf(l));
        }
    }

    @GwtIncompatible("TODO")
    static boolean fitsInLong(BigInteger biginteger) {
        return biginteger.bitLength() <= 63;
    }

    private BigIntegerMath() {}
}
