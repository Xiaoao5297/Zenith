package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

final class SipHashFunction extends AbstractStreamingHashFunction implements Serializable {

    private final int c;
    private final int d;
    private final long k0;
    private final long k1;
    private static final long serialVersionUID = 0L;

    SipHashFunction(int i, int j, long k, long l) {
        Preconditions.checkArgument(i > 0, "The number of SipRound iterations (c=%s) during Compression must be positive.", i);
        Preconditions.checkArgument(j > 0, "The number of SipRound iterations (d=%s) during Finalization must be positive.", j);
        this.c = i;
        this.d = j;
        this.k0 = k;
        this.k1 = l;
    }

    @Override
    public int bits() {
        return 64;
    }

    @Override
    public Hasher newHasher() {
        return new SipHashFunction.SipHasher(this.c, this.d, this.k0, this.k1);
    }

    public String toString() {
        return "Hashing.sipHash" + this.c + "" + this.d + "(" + this.k0 + ", " + this.k1 + ")";
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof SipHashFunction)) {
            return false;
        } else {
            SipHashFunction siphashfunction = (SipHashFunction) object;

            return this.c == siphashfunction.c && this.d == siphashfunction.d && this.k0 == siphashfunction.k0 && this.k1 == siphashfunction.k1;
        }
    }

    public int hashCode() {
        return (int) ((long) (this.getClass().hashCode() ^ this.c ^ this.d) ^ this.k0 ^ this.k1);
    }

    private static final class SipHasher extends AbstractStreamingHashFunction.AbstractStreamingHasher {

        private static final int CHUNK_SIZE = 8;
        private final int c;
        private final int d;
        private long v0 = 8317987319222330741L;
        private long v1 = 7237128888997146477L;
        private long v2 = 7816392313619706465L;
        private long v3 = 8387220255154660723L;
        private long b = 0L;
        private long finalM = 0L;

        SipHasher(int i, int j, long k, long l) {
            super(8);
            this.c = i;
            this.d = j;
            this.v0 ^= k;
            this.v1 ^= l;
            this.v2 ^= k;
            this.v3 ^= l;
        }

        @Override
        protected void process(ByteBuffer bytebuffer) {
            this.b += 8L;
            this.processM(bytebuffer.getLong());
        }

        @Override
        protected void processRemaining(ByteBuffer bytebuffer) {
            this.b += (long) bytebuffer.remaining();

            for (int i = 0; bytebuffer.hasRemaining(); i += 8) {
                this.finalM ^= ((long) bytebuffer.get() & 255L) << i;
            }

        }

        @Override
        public HashCode makeHash() {
            this.finalM ^= this.b << 56;
            this.processM(this.finalM);
            this.v2 ^= 255L;
            this.sipRound(this.d);
            return HashCode.fromLong(this.v0 ^ this.v1 ^ this.v2 ^ this.v3);
        }

        private void processM(long i) {
            this.v3 ^= i;
            this.sipRound(this.c);
            this.v0 ^= i;
        }

        private void sipRound(int i) {
            for (int j = 0; j < i; ++j) {
                this.v0 += this.v1;
                this.v2 += this.v3;
                this.v1 = Long.rotateLeft(this.v1, 13);
                this.v3 = Long.rotateLeft(this.v3, 16);
                this.v1 ^= this.v0;
                this.v3 ^= this.v2;
                this.v0 = Long.rotateLeft(this.v0, 32);
                this.v2 += this.v1;
                this.v0 += this.v3;
                this.v1 = Long.rotateLeft(this.v1, 17);
                this.v3 = Long.rotateLeft(this.v3, 21);
                this.v1 ^= this.v2;
                this.v3 ^= this.v0;
                this.v2 = Long.rotateLeft(this.v2, 32);
            }

        }
    }
}
