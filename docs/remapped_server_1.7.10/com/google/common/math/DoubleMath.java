package com.google.common.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;

public final class DoubleMath {

    private static final double MIN_INT_AS_DOUBLE = -2.147483648E9D;
    private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
    private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E18D;
    private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18D;
    private static final double LN_2 = Math.log(2.0D);
    @VisibleForTesting
    static final int MAX_FACTORIAL = 170;
    @VisibleForTesting
    static final double[] everySixteenthFactorial = new double[]{1.0D, 2.0922789888E13D, 2.631308369336935E35D, 1.2413915592536073E61D, 1.2688693218588417E89D, 7.156945704626381E118D, 9.916779348709496E149D, 1.974506857221074E182D, 3.856204823625804E215D, 5.5502938327393044E249D, 4.7147236359920616E284D};

    static double roundIntermediate(double d0, RoundingMode roundingmode) {
        if (!DoubleUtils.isFinite(d0)) {
            throw new ArithmeticException("input is infinite or NaN");
        } else {
            double d1;

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(d0));
                    return d0;
                case FLOOR:
                    if (d0 < 0.0D && !isMathematicalInteger(d0)) {
                        return d0 - 1.0D;
                    }

                    return d0;
                case CEILING:
                    if (d0 > 0.0D && !isMathematicalInteger(d0)) {
                        return d0 + 1.0D;
                    }

                    return d0;
                case DOWN:
                    return d0;
                case UP:
                    if (isMathematicalInteger(d0)) {
                        return d0;
                    }

                    return d0 + Math.copySign(1.0D, d0);
                case HALF_EVEN:
                    return Math.rint(d0);
                case HALF_UP:
                    d1 = Math.rint(d0);
                    if (Math.abs(d0 - d1) == 0.5D) {
                        return d0 + Math.copySign(0.5D, d0);
                    }

                    return d1;
                case HALF_DOWN:
                    d1 = Math.rint(d0);
                    if (Math.abs(d0 - d1) == 0.5D) {
                        return d0;
                    }

                    return d1;
                default:
                    throw new AssertionError();
            }
        }
    }

    public static int roundToInt(double d0, RoundingMode roundingmode) {
        double d1 = roundIntermediate(d0, roundingmode);

        MathPreconditions.checkInRange(d1 > -2.147483649E9D & d1 < 2.147483648E9D);
        return (int) d1;
    }

    public static long roundToLong(double d0, RoundingMode roundingmode) {
        double d1 = roundIntermediate(d0, roundingmode);

        MathPreconditions.checkInRange(-9.223372036854776E18D - d1 < 1.0D & d1 < 9.223372036854776E18D);
        return (long) d1;
    }

    public static BigInteger roundToBigInteger(double d0, RoundingMode roundingmode) {
        d0 = roundIntermediate(d0, roundingmode);
        if (-9.223372036854776E18D - d0 < 1.0D & d0 < 9.223372036854776E18D) {
            return BigInteger.valueOf((long) d0);
        } else {
            int i = Math.getExponent(d0);
            long j = DoubleUtils.getSignificand(d0);
            BigInteger biginteger = BigInteger.valueOf(j).shiftLeft(i - 52);

            return d0 < 0.0D ? biginteger.negate() : biginteger;
        }
    }

    public static boolean isPowerOfTwo(double d0) {
        return d0 > 0.0D && DoubleUtils.isFinite(d0) && LongMath.isPowerOfTwo(DoubleUtils.getSignificand(d0));
    }

    public static double log2(double d0) {
        return Math.log(d0) / DoubleMath.LN_2;
    }

    public static int log2(double d0, RoundingMode roundingmode) {
        Preconditions.checkArgument(d0 > 0.0D && DoubleUtils.isFinite(d0), "x must be positive and finite");
        int i = Math.getExponent(d0);

        if (!DoubleUtils.isNormal(d0)) {
            return log2(d0 * 4.503599627370496E15D, roundingmode) - 52;
        } else {
            boolean flag;

            switch (roundingmode) {
                case UNNECESSARY:
                    MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(d0));
                case FLOOR:
                    flag = false;
                    break;
                case CEILING:
                    flag = !isPowerOfTwo(d0);
                    break;
                case DOWN:
                    flag = i < 0 & !isPowerOfTwo(d0);
                    break;
                case UP:
                    flag = i >= 0 & !isPowerOfTwo(d0);
                    break;
                case HALF_EVEN:
                case HALF_UP:
                case HALF_DOWN:
                    double d1 = DoubleUtils.scaleNormalize(d0);

                    flag = d1 * d1 > 2.0D;
                    break;
                default:
                    throw new AssertionError();
            }

            return flag ? i + 1 : i;
        }
    }

    public static boolean isMathematicalInteger(double d0) {
        return DoubleUtils.isFinite(d0) && (d0 == 0.0D || 52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(d0)) <= Math.getExponent(d0));
    }

    public static double factorial(int i) {
        MathPreconditions.checkNonNegative("n", i);
        if (i > 170) {
            return Double.POSITIVE_INFINITY;
        } else {
            double d0 = 1.0D;

            for (int j = 1 + (i & -16); j <= i; ++j) {
                d0 *= (double) j;
            }

            return d0 * DoubleMath.everySixteenthFactorial[i >> 4];
        }
    }

    public static boolean fuzzyEquals(double d0, double d1, double d2) {
        MathPreconditions.checkNonNegative("tolerance", d2);
        return Math.copySign(d0 - d1, 1.0D) <= d2 || d0 == d1 || Double.isNaN(d0) && Double.isNaN(d1);
    }

    public static int fuzzyCompare(double d0, double d1, double d2) {
        return fuzzyEquals(d0, d1, d2) ? 0 : (d0 < d1 ? -1 : (d0 > d1 ? 1 : Booleans.compare(Double.isNaN(d0), Double.isNaN(d1))));
    }

    public static double mean(double... adouble) {
        DoubleMath.MeanAccumulator doublemath_meanaccumulator = new DoubleMath.MeanAccumulator();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            doublemath_meanaccumulator.add(d0);
        }

        return doublemath_meanaccumulator.mean();
    }

    public static double mean(int... aint) {
        DoubleMath.MeanAccumulator doublemath_meanaccumulator = new DoubleMath.MeanAccumulator();
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            doublemath_meanaccumulator.add((double) k);
        }

        return doublemath_meanaccumulator.mean();
    }

    public static double mean(long... along) {
        DoubleMath.MeanAccumulator doublemath_meanaccumulator = new DoubleMath.MeanAccumulator();
        long[] along1 = along;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along1[j];

            doublemath_meanaccumulator.add((double) k);
        }

        return doublemath_meanaccumulator.mean();
    }

    public static double mean(Iterable<? extends Number> iterable) {
        DoubleMath.MeanAccumulator doublemath_meanaccumulator = new DoubleMath.MeanAccumulator();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Number number = (Number) iterator.next();

            doublemath_meanaccumulator.add(number.doubleValue());
        }

        return doublemath_meanaccumulator.mean();
    }

    public static double mean(Iterator<? extends Number> iterator) {
        DoubleMath.MeanAccumulator doublemath_meanaccumulator = new DoubleMath.MeanAccumulator();

        while (iterator.hasNext()) {
            doublemath_meanaccumulator.add(((Number) iterator.next()).doubleValue());
        }

        return doublemath_meanaccumulator.mean();
    }

    private DoubleMath() {}

    private static final class MeanAccumulator {

        private long count;
        private double mean;

        private MeanAccumulator() {
            this.count = 0L;
            this.mean = 0.0D;
        }

        void add(double d0) {
            Preconditions.checkArgument(DoubleUtils.isFinite(d0));
            ++this.count;
            this.mean += (d0 - this.mean) / (double) this.count;
        }

        double mean() {
            Preconditions.checkArgument(this.count > 0L, "Cannot take mean of 0 values");
            return this.mean;
        }
    }
}
