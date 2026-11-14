package com.google.common.hash;

import java.nio.charset.Charset;

abstract class AbstractHasher implements Hasher {

    AbstractHasher() {}

    @Override
    public final Hasher putBoolean(boolean flag) {
        return this.putByte((byte) (flag ? 1 : 0));
    }

    @Override
    public final Hasher putDouble(double d0) {
        return this.putLong(Double.doubleToRawLongBits(d0));
    }

    @Override
    public final Hasher putFloat(float f) {
        return this.putInt(Float.floatToRawIntBits(f));
    }

    @Override
    public Hasher putUnencodedChars(CharSequence charsequence) {
        int i = 0;

        for (int j = charsequence.length(); i < j; ++i) {
            this.putChar(charsequence.charAt(i));
        }

        return this;
    }

    @Override
    public Hasher putString(CharSequence charsequence, Charset charset) {
        return this.putBytes(charsequence.toString().getBytes(charset));
    }
}
