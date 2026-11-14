package com.google.common.hash;

import com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public interface Hasher extends PrimitiveSink {

    @Override
    Hasher putByte(byte b0);

    @Override
    Hasher putBytes(byte[] abyte);

    @Override
    Hasher putBytes(byte[] abyte, int i, int j);

    @Override
    Hasher putShort(short short0);

    @Override
    Hasher putInt(int i);

    @Override
    Hasher putLong(long i);

    @Override
    Hasher putFloat(float f);

    @Override
    Hasher putDouble(double d0);

    @Override
    Hasher putBoolean(boolean flag);

    @Override
    Hasher putChar(char c0);

    @Override
    Hasher putUnencodedChars(CharSequence charsequence);

    @Override
    Hasher putString(CharSequence charsequence, Charset charset);

    <T> Hasher putObject(T t0, Funnel<? super T> funnel);

    HashCode hash();
}
