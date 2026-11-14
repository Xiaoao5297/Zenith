package com.google.common.hash;

import com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public interface PrimitiveSink {

    PrimitiveSink putByte(byte b0);

    PrimitiveSink putBytes(byte[] abyte);

    PrimitiveSink putBytes(byte[] abyte, int i, int j);

    PrimitiveSink putShort(short short0);

    PrimitiveSink putInt(int i);

    PrimitiveSink putLong(long i);

    PrimitiveSink putFloat(float f);

    PrimitiveSink putDouble(double d0);

    PrimitiveSink putBoolean(boolean flag);

    PrimitiveSink putChar(char c0);

    PrimitiveSink putUnencodedChars(CharSequence charsequence);

    PrimitiveSink putString(CharSequence charsequence, Charset charset);
}
