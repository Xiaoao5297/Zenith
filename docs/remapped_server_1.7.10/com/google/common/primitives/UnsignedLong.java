package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.math.BigInteger;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
public final class UnsignedLong extends Number implements Comparable<UnsignedLong>, Serializable {

    private static final long UNSIGNED_MASK = Long.MAX_VALUE;
    public static final UnsignedLong ZERO = new UnsignedLong(0L);
    public static final UnsignedLong ONE = new UnsignedLong(1L);
    public static final UnsignedLong MAX_VALUE = new UnsignedLong(-1L);
    private final long value;

    private UnsignedLong(long i) {
        this.value = i;
    }

    public static UnsignedLong fromLongBits(long i) {
        return new UnsignedLong(i);
    }

    public static UnsignedLong valueOf(long i) {
        Preconditions.checkArgument(i >= 0L, "value (%s) is outside the range for an unsigned long value", i);
        return fromLongBits(i);
    }

    public static UnsignedLong valueOf(BigInteger biginteger) {
        Preconditions.checkNotNull(biginteger);
        Preconditions.checkArgument(biginteger.signum() >= 0 && biginteger.bitLength() <= 64, "value (%s) is outside the range for an unsigned long value", biginteger);
        return fromLongBits(biginteger.longValue());
    }

    public static UnsignedLong valueOf(String s) {
        return valueOf(s, 10);
    }

    public static UnsignedLong valueOf(String s, int i) {
        return fromLongBits(UnsignedLongs.parseUnsignedLong(s, i));
    }

    public UnsignedLong plus(UnsignedLong unsignedlong) {
        return fromLongBits(this.value + ((UnsignedLong) Preconditions.checkNotNull(unsignedlong)).value);
    }

    public UnsignedLong minus(UnsignedLong unsignedlong) {
        return fromLongBits(this.value - ((UnsignedLong) Preconditions.checkNotNull(unsignedlong)).value);
    }

    @CheckReturnValue
    public UnsignedLong times(UnsignedLong unsignedlong) {
        return fromLongBits(this.value * ((UnsignedLong) Preconditions.checkNotNull(unsignedlong)).value);
    }

    @CheckReturnValue
    public UnsignedLong dividedBy(UnsignedLong unsignedlong) {
        return fromLongBits(UnsignedLongs.divide(this.value, ((UnsignedLong) Preconditions.checkNotNull(unsignedlong)).value));
    }

    @CheckReturnValue
    public UnsignedLong mod(UnsignedLong unsignedlong) {
        return fromLongBits(UnsignedLongs.remainder(this.value, ((UnsignedLong) Preconditions.checkNotNull(unsignedlong)).value));
    }

    public int intValue() {
        return (int) this.value;
    }

    public long longValue() {
        return this.value;
    }

    public float floatValue() {
        float f = (float) (this.value & Long.MAX_VALUE);

        if (this.value < 0L) {
            f += 9.223372E18F;
        }

        return f;
    }

    public double doubleValue() {
        double d0 = (double) (this.value & Long.MAX_VALUE);

        if (this.value < 0L) {
            d0 += 9.223372036854776E18D;
        }

        return d0;
    }

    public BigInteger bigIntegerValue() {
        BigInteger biginteger = BigInteger.valueOf(this.value & Long.MAX_VALUE);

        if (this.value < 0L) {
            biginteger = biginteger.setBit(63);
        }

        return biginteger;
    }

    public int compareTo(UnsignedLong unsignedlong) {
        Preconditions.checkNotNull(unsignedlong);
        return UnsignedLongs.compare(this.value, unsignedlong.value);
    }

    public int hashCode() {
        return Longs.hashCode(this.value);
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof UnsignedLong) {
            UnsignedLong unsignedlong = (UnsignedLong) object;

            return this.value == unsignedlong.value;
        } else {
            return false;
        }
    }

    public String toString() {
        return UnsignedLongs.toString(this.value);
    }

    public String toString(int i) {
        return UnsignedLongs.toString(this.value, i);
    }
}
