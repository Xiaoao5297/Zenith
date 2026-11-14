package com.google.common.hash;

import com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public interface HashFunction {

    Hasher newHasher();

    Hasher newHasher(int i);

    HashCode hashInt(int i);

    HashCode hashLong(long i);

    HashCode hashBytes(byte[] abyte);

    HashCode hashBytes(byte[] abyte, int i, int j);

    HashCode hashUnencodedChars(CharSequence charsequence);

    HashCode hashString(CharSequence charsequence, Charset charset);

    <T> HashCode hashObject(T t0, Funnel<? super T> funnel);

    int bits();
}
