package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Beta
public final class HashingOutputStream extends FilterOutputStream {

    private final Hasher hasher;

    public HashingOutputStream(HashFunction hashfunction, OutputStream outputstream) {
        super((OutputStream) Preconditions.checkNotNull(outputstream));
        this.hasher = (Hasher) Preconditions.checkNotNull(hashfunction.newHasher());
    }

    public void write(int i) throws IOException {
        this.hasher.putByte((byte) i);
        this.out.write(i);
    }

    public void write(byte[] abyte, int i, int j) throws IOException {
        this.hasher.putBytes(abyte, i, j);
        this.out.write(abyte, i, j);
    }

    public HashCode hash() {
        return this.hasher.hash();
    }
}
