package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nullable;

final class Murmur3_128HashFunction extends AbstractStreamingHashFunction implements Serializable {

    private final int seed;
    private static final long serialVersionUID = 0L;

    Murmur3_128HashFunction(int i) {
        this.seed = i;
    }

    @Override
    public int bits() {
        return 128;
    }

    @Override
    public Hasher newHasher() {
        return new Murmur3_128HashFunction.Murmur3_128Hasher(this.seed);
    }

    public String toString() {
        return "Hashing.murmur3_128(" + this.seed + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof Murmur3_128HashFunction) {
            Murmur3_128HashFunction murmur3_128hashfunction = (Murmur3_128HashFunction) object;

            return this.seed == murmur3_128hashfunction.seed;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }

    private static final class Murmur3_128Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher {

        private static final int CHUNK_SIZE = 16;
        private static final long C1 = -8663945395140668459L;
        private static final long C2 = 5545529020109919103L;
        private long h1;
        private long h2;
        private int length;

        Murmur3_128Hasher(int i) {
            super(16);
            this.h1 = (long) i;
            this.h2 = (long) i;
            this.length = 0;
        }

        @Override
        protected void process(ByteBuffer bytebuffer) {
            long i = bytebuffer.getLong();
            long j = bytebuffer.getLong();

            this.bmix64(i, j);
            this.length += 16;
        }

        private void bmix64(long i, long j) {
            this.h1 ^= mixK1(i);
            this.h1 = Long.rotateLeft(this.h1, 27);
            this.h1 += this.h2;
            this.h1 = this.h1 * 5L + 1390208809L;
            this.h2 ^= mixK2(j);
            this.h2 = Long.rotateLeft(this.h2, 31);
            this.h2 += this.h1;
            this.h2 = this.h2 * 5L + 944331445L;
        }

        @Override
        protected void processRemaining(ByteBuffer bytebuffer) {
            long i = 0L;
            long j = 0L;

            this.length += bytebuffer.remaining();
            switch (bytebuffer.remaining()) {
                case 7:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(6)) << 48;
                case 6:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(5)) << 40;
                case 5:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(4)) << 32;
                case 4:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(3)) << 24;
                case 3:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(2)) << 16;
                case 2:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(1)) << 8;
                case 1:
                    i ^= (long) UnsignedBytes.toInt(bytebuffer.get(0));
                    break;
                case 15:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(14)) << 48;
                case 14:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(13)) << 40;
                case 13:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(12)) << 32;
                case 12:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(11)) << 24;
                case 11:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(10)) << 16;
                case 10:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(9)) << 8;
                case 9:
                    j ^= (long) UnsignedBytes.toInt(bytebuffer.get(8));
                case 8:
                    i ^= bytebuffer.getLong();
                    break;
                default:
                    throw new AssertionError("Should never get here.");
            }

            this.h1 ^= mixK1(i);
            this.h2 ^= mixK2(j);
        }

        @Override
        public HashCode makeHash() {
            this.h1 ^= (long) this.length;
            this.h2 ^= (long) this.length;
            this.h1 += this.h2;
            this.h2 += this.h1;
            this.h1 = fmix64(this.h1);
            this.h2 = fmix64(this.h2);
            this.h1 += this.h2;
            this.h2 += this.h1;
            return HashCode.fromBytesNoCopy(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN).putLong(this.h1).putLong(this.h2).array());
        }

        private static long fmix64(long i) {
            i ^= i >>> 33;
            i *= -49064778989728563L;
            i ^= i >>> 33;
            i *= -4265267296055464877L;
            i ^= i >>> 33;
            return i;
        }

        private static long mixK1(long i) {
            i *= -8663945395140668459L;
            i = Long.rotateLeft(i, 31);
            i *= 5545529020109919103L;
            return i;
        }

        private static long mixK2(long i) {
            i *= 5545529020109919103L;
            i = Long.rotateLeft(i, 33);
            i *= -8663945395140668459L;
            return i;
        }
    }
}
