package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.math.BigInteger;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class UnsignedInteger extends Number implements Comparable<UnsignedInteger> {

    public static final UnsignedInteger ZERO = fromIntBits(0);
    public static final UnsignedInteger ONE = fromIntBits(1);
    public static final UnsignedInteger MAX_VALUE = fromIntBits(-1);
    private final int value;

    private UnsignedInteger(int i) {
        this.value = i & -1;
    }

    public static UnsignedInteger fromIntBits(int i) {
        return new UnsignedInteger(i);
    }

    public static UnsignedInteger valueOf(long i) {
        Preconditions.checkArgument((i & 4294967295L) == i, "value (%s) is outside the range for an unsigned integer value", i);
        return fromIntBits((int) i);
    }

    public static UnsignedInteger valueOf(BigInteger biginteger) {
        Preconditions.checkNotNull(biginteger);
        Preconditions.checkArgument(biginteger.signum() >= 0 && biginteger.bitLength() <= 32, "value (%s) is outside the range for an unsigned integer value", biginteger);
        return fromIntBits(biginteger.intValue());
    }

    public static UnsignedInteger valueOf(String s) {
        return valueOf(s, 10);
    }

    public static UnsignedInteger valueOf(String s, int i) {
        return fromIntBits(UnsignedInts.parseUnsignedInt(s, i));
    }

    @CheckReturnValue
    public UnsignedInteger plus(UnsignedInteger unsignedinteger) {
        return fromIntBits(this.value + ((UnsignedInteger) Preconditions.checkNotNull(unsignedinteger)).value);
    }

    @CheckReturnValue
    public UnsignedInteger minus(UnsignedInteger unsignedinteger) {
        return fromIntBits(this.value - ((UnsignedInteger) Preconditions.checkNotNull(unsignedinteger)).value);
    }

    @CheckReturnValue
    @GwtIncompatible("Does not truncate correctly")
    public UnsignedInteger times(UnsignedInteger unsignedinteger) {
        return fromIntBits(this.value * ((UnsignedInteger) Preconditions.checkNotNull(unsignedinteger)).value);
    }

    @CheckReturnValue
    public UnsignedInteger dividedBy(UnsignedInteger unsignedinteger) {
        return fromIntBits(UnsignedInts.divide(this.value, ((UnsignedInteger) Preconditions.checkNotNull(unsignedinteger)).value));
    }

    @CheckReturnValue
    public UnsignedInteger mod(UnsignedInteger unsignedinteger) {
        return fromIntBits(UnsignedInts.remainder(this.value, ((UnsignedInteger) Preconditions.checkNotNull(unsignedinteger)).value));
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return UnsignedInts.toLong(this.value);
    }

    public float floatValue() {
        return (float) this.longValue();
    }

    public double doubleValue() {
        return (double) this.longValue();
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(this.longValue());
    }

    public int compareTo(UnsignedInteger unsignedinteger) {
        Preconditions.checkNotNull(unsignedinteger);
        return UnsignedInts.compare(this.value, unsignedinteger.value);
    }

    public int hashCode() {
        return this.value;
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof UnsignedInteger) {
            UnsignedInteger unsignedinteger = (UnsignedInteger) object;

            return this.value == unsignedinteger.value;
        } else {
            return false;
        }
    }

    public String toString() {
        return this.toString(10);
    }

    public String toString(int i) {
        return UnsignedInts.toString(this.value, i);
    }
}
