package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

final class Murmur3_32HashFunction extends AbstractStreamingHashFunction implements Serializable {

    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private final int seed;
    private static final long serialVersionUID = 0L;

    Murmur3_32HashFunction(int i) {
        this.seed = i;
    }

    @Override
    public int bits() {
        return 32;
    }

    @Override
    public Hasher newHasher() {
        return new Murmur3_32HashFunction.Murmur3_32Hasher(this.seed);
    }

    public String toString() {
        return "Hashing.murmur3_32(" + this.seed + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof Murmur3_32HashFunction) {
            Murmur3_32HashFunction murmur3_32hashfunction = (Murmur3_32HashFunction) object;

            return this.seed == murmur3_32hashfunction.seed;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }

    @Override
    public HashCode hashInt(int i) {
        int j = mixK1(i);
        int k = mixH1(this.seed, j);

        return fmix(k, 4);
    }

    @Override
    public HashCode hashLong(long i) {
        int j = (int) i;
        int k = (int) (i >>> 32);
        int l = mixK1(j);
        int i1 = mixH1(this.seed, l);

        l = mixK1(k);
        i1 = mixH1(i1, l);
        return fmix(i1, 8);
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence charsequence) {
        int i = this.seed;

        int j;

        for (j = 1; j < charsequence.length(); j += 2) {
            int k = charsequence.charAt(j - 1) | charsequence.charAt(j) << 16;

            k = mixK1(k);
            i = mixH1(i, k);
        }

        if ((charsequence.length() & 1) == 1) {
            char c0 = charsequence.charAt(charsequence.length() - 1);

            j = mixK1(c0);
            i ^= j;
        }

        return fmix(i, 2 * charsequence.length());
    }

    private static int mixK1(int i) {
        i *= -862048943;
        i = Integer.rotateLeft(i, 15);
        i *= 461845907;
        return i;
    }

    private static int mixH1(int i, int j) {
        i ^= j;
        i = Integer.rotateLeft(i, 13);
        i = i * 5 + -430675100;
        return i;
    }

    private static HashCode fmix(int i, int j) {
        i ^= j;
        i ^= i >>> 16;
        i *= -2048144789;
        i ^= i >>> 13;
        i *= -1028477387;
        i ^= i >>> 16;
        return HashCode.fromInt(i);
    }

    private static final class Murmur3_32Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher {

        private static final int CHUNK_SIZE = 4;
        private int h1;
        private int length;

        Murmur3_32Hasher(int i) {
            super(4);
            this.h1 = i;
            this.length = 0;
        }

        @Override
        protected void process(ByteBuffer bytebuffer) {
            int i = Murmur3_32HashFunction.mixK1(bytebuffer.getInt());

            this.h1 = Murmur3_32HashFunction.mixH1(this.h1, i);
            this.length += 4;
        }

        @Override
        protected void processRemaining(ByteBuffer bytebuffer) {
            this.length += bytebuffer.remaining();
            int i = 0;

            for (int j = 0; bytebuffer.hasRemaining(); j += 8) {
                i ^= UnsignedBytes.toInt(bytebuffer.get()) << j;
            }

            this.h1 ^= Murmur3_32HashFunction.mixK1(i);
        }

        @Override
        public HashCode makeHash() {
            return Murmur3_32HashFunction.fmix(this.h1, this.length);
        }
    }
}
