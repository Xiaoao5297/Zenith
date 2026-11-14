package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
public final class HashingInputStream extends FilterInputStream {

    private final Hasher hasher;

    public HashingInputStream(HashFunction hashfunction, InputStream inputstream) {
        super((InputStream) Preconditions.checkNotNull(inputstream));
        this.hasher = (Hasher) Preconditions.checkNotNull(hashfunction.newHasher());
    }

    public int read() throws IOException {
        int i = this.in.read();

        if (i != -1) {
            this.hasher.putByte((byte) i);
        }

        return i;
    }

    public int read(byte[] abyte, int i, int j) throws IOException {
        int k = this.in.read(abyte, i, j);

        if (k != -1) {
            this.hasher.putBytes(abyte, i, k);
        }

        return k;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int i) {}

    public void reset() throws IOException {
        throw new IOException("reset not supported");
    }

    public HashCode hash() {
        return this.hasher.hash();
    }
}
