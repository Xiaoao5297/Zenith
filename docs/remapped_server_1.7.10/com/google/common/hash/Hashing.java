package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.annotation.Nullable;

@Beta
public final class Hashing {

    private static final int GOOD_FAST_HASH_SEED = (int) System.currentTimeMillis();

    public static HashFunction goodFastHash(int i) {
        int j = checkPositiveAndMakeMultipleOf32(i);

        if (j == 32) {
            return Hashing.Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32;
        } else if (j <= 128) {
            return Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
        } else {
            int k = (j + 127) / 128;
            HashFunction[] ahashfunction = new HashFunction[k];

            ahashfunction[0] = Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
            int l = Hashing.GOOD_FAST_HASH_SEED;

            for (int i1 = 1; i1 < k; ++i1) {
                l += 1500450271;
                ahashfunction[i1] = murmur3_128(l);
            }

            return new Hashing.ConcatenatedHashFunction(ahashfunction);
        }
    }

    public static HashFunction murmur3_32(int i) {
        return new Murmur3_32HashFunction(i);
    }

    public static HashFunction murmur3_32() {
        return Hashing.Murmur3_32Holder.MURMUR3_32;
    }

    public static HashFunction murmur3_128(int i) {
        return new Murmur3_128HashFunction(i);
    }

    public static HashFunction murmur3_128() {
        return Hashing.Murmur3_128Holder.MURMUR3_128;
    }

    public static HashFunction sipHash24() {
        return Hashing.SipHash24Holder.SIP_HASH_24;
    }

    public static HashFunction sipHash24(long i, long j) {
        return new SipHashFunction(2, 4, i, j);
    }

    public static HashFunction md5() {
        return Hashing.Md5Holder.MD5;
    }

    public static HashFunction sha1() {
        return Hashing.Sha1Holder.SHA_1;
    }

    public static HashFunction sha256() {
        return Hashing.Sha256Holder.SHA_256;
    }

    public static HashFunction sha512() {
        return Hashing.Sha512Holder.SHA_512;
    }

    public static HashFunction crc32() {
        return Hashing.Crc32Holder.CRC_32;
    }

    public static HashFunction adler32() {
        return Hashing.Adler32Holder.ADLER_32;
    }

    private static HashFunction checksumHashFunction(Hashing.ChecksumType hashing_checksumtype, String s) {
        return new ChecksumHashFunction(hashing_checksumtype, hashing_checksumtype.bits, s);
    }

    public static int consistentHash(HashCode hashcode, int i) {
        return consistentHash(hashcode.padToLong(), i);
    }

    public static int consistentHash(long i, int j) {
        Preconditions.checkArgument(j > 0, "buckets must be positive: %s", j);
        Hashing.LinearCongruentialGenerator hashing_linearcongruentialgenerator = new Hashing.LinearCongruentialGenerator(i);
        int k = 0;

        while (true) {
            int l = (int) ((double) (k + 1) / hashing_linearcongruentialgenerator.nextDouble());

            if (l < 0 || l >= j) {
                return k;
            }

            k = l;
        }
    }

    public static HashCode combineOrdered(Iterable<HashCode> iterable) {
        Iterator iterator = iterable.iterator();

        Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
        int i = ((HashCode) iterator.next()).bits();
        byte[] abyte = new byte[i / 8];
        Iterator iterator1 = iterable.iterator();

        while (iterator1.hasNext()) {
            HashCode hashcode = (HashCode) iterator1.next();
            byte[] abyte1 = hashcode.asBytes();

            Preconditions.checkArgument(abyte1.length == abyte.length, "All hashcodes must have the same bit length.");

            for (int j = 0; j < abyte1.length; ++j) {
                abyte[j] = (byte) (abyte[j] * 37 ^ abyte1[j]);
            }
        }

        return HashCode.fromBytesNoCopy(abyte);
    }

    public static HashCode combineUnordered(Iterable<HashCode> iterable) {
        Iterator iterator = iterable.iterator();

        Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
        byte[] abyte = new byte[((HashCode) iterator.next()).bits() / 8];
        Iterator iterator1 = iterable.iterator();

        while (iterator1.hasNext()) {
            HashCode hashcode = (HashCode) iterator1.next();
            byte[] abyte1 = hashcode.asBytes();

            Preconditions.checkArgument(abyte1.length == abyte.length, "All hashcodes must have the same bit length.");

            for (int i = 0; i < abyte1.length; ++i) {
                abyte[i] += abyte1[i];
            }
        }

        return HashCode.fromBytesNoCopy(abyte);
    }

    static int checkPositiveAndMakeMultipleOf32(int i) {
        Preconditions.checkArgument(i > 0, "Number of bits must be positive");
        return i + 31 & -32;
    }

    private Hashing() {}

    private static final class LinearCongruentialGenerator {

        private long state;

        public LinearCongruentialGenerator(long i) {
            this.state = i;
        }

        public double nextDouble() {
            this.state = 2862933555777941757L * this.state + 1L;
            return (double) ((int) (this.state >>> 33) + 1) / 2.147483648E9D;
        }
    }

    @VisibleForTesting
    static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {

        private final int bits;

        ConcatenatedHashFunction(HashFunction... ahashfunction) {
            super(ahashfunction);
            int i = 0;
            HashFunction[] ahashfunction1 = ahashfunction;
            int j = ahashfunction.length;

            for (int k = 0; k < j; ++k) {
                HashFunction hashfunction = ahashfunction1[k];

                i += hashfunction.bits();
            }

            this.bits = i;
        }

        @Override
        HashCode makeHash(Hasher[] ahasher) {
            byte[] abyte = new byte[this.bits / 8];
            int i = 0;
            Hasher[] ahasher1 = ahasher;
            int j = ahasher.length;

            for (int k = 0; k < j; ++k) {
                Hasher hasher = ahasher1[k];
                HashCode hashcode = hasher.hash();

                i += hashcode.writeBytesTo(abyte, i, hashcode.bits() / 8);
            }

            return HashCode.fromBytesNoCopy(abyte);
        }

        @Override
        public int bits() {
            return this.bits;
        }

        public boolean equals(@Nullable Object object) {
            if (object instanceof Hashing.ConcatenatedHashFunction) {
                Hashing.ConcatenatedHashFunction hashing_concatenatedhashfunction = (Hashing.ConcatenatedHashFunction) object;

                if (this.bits == hashing_concatenatedhashfunction.bits && this.functions.length == hashing_concatenatedhashfunction.functions.length) {
                    for (int i = 0; i < this.functions.length; ++i) {
                        if (!this.functions[i].equals(hashing_concatenatedhashfunction.functions[i])) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            int i = this.bits;
            HashFunction[] ahashfunction = this.functions;
            int j = ahashfunction.length;

            for (int k = 0; k < j; ++k) {
                HashFunction hashfunction = ahashfunction[k];

                i ^= hashfunction.hashCode();
            }

            return i;
        }
    }

    static enum ChecksumType implements Supplier<Checksum> {

        CRC_32(32) {
            @Override
            public Checksum get() {
                return new CRC32();
            }
        },
        ADLER_32(32) {
            @Override
            public Checksum get() {
                return new Adler32();
            }
        };

        private final int bits;

        private ChecksumType(int i) {
            this.bits = i;
        }

        @Override
        public abstract Checksum get();
    }

    private static class Adler32Holder {

        static final HashFunction ADLER_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.ADLER_32, "Hashing.adler32()");

        private Adler32Holder() {}
    }

    private static class Crc32Holder {

        static final HashFunction CRC_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.CRC_32, "Hashing.crc32()");

        private Crc32Holder() {}
    }

    private static class Sha512Holder {

        static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512", "Hashing.sha512()");

        private Sha512Holder() {}
    }

    private static class Sha256Holder {

        static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256", "Hashing.sha256()");

        private Sha256Holder() {}
    }

    private static class Sha1Holder {

        static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");

        private Sha1Holder() {}
    }

    private static class Md5Holder {

        static final HashFunction MD5 = new MessageDigestHashFunction("MD5", "Hashing.md5()");

        private Md5Holder() {}
    }

    private static class SipHash24Holder {

        static final HashFunction SIP_HASH_24 = new SipHashFunction(2, 4, 506097522914230528L, 1084818905618843912L);

        private SipHash24Holder() {}
    }

    private static class Murmur3_128Holder {

        static final HashFunction MURMUR3_128 = new Murmur3_128HashFunction(0);
        static final HashFunction GOOD_FAST_HASH_FUNCTION_128 = Hashing.murmur3_128(Hashing.GOOD_FAST_HASH_SEED);

        private Murmur3_128Holder() {}
    }

    private static class Murmur3_32Holder {

        static final HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
        static final HashFunction GOOD_FAST_HASH_FUNCTION_32 = Hashing.murmur3_32(Hashing.GOOD_FAST_HASH_SEED);

        private Murmur3_32Holder() {}
    }
}
