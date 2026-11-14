package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedInts;
import java.io.Serializable;
import java.security.MessageDigest;
import javax.annotation.Nullable;

@Beta
public abstract class HashCode {

    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    HashCode() {}

    public abstract int bits();

    public abstract int asInt();

    public abstract long asLong();

    public abstract long padToLong();

    public abstract byte[] asBytes();

    public int writeBytesTo(byte[] abyte, int i, int j) {
        j = Ints.min(j, this.bits() / 8);
        Preconditions.checkPositionIndexes(i, i + j, abyte.length);
        this.writeBytesToImpl(abyte, i, j);
        return j;
    }

    abstract void writeBytesToImpl(byte[] abyte, int i, int j);

    public static HashCode fromInt(int i) {
        return new HashCode.IntHashCode(i);
    }

    public static HashCode fromLong(long i) {
        return new HashCode.LongHashCode(i);
    }

    public static HashCode fromBytes(byte[] abyte) {
        Preconditions.checkArgument(abyte.length >= 1, "A HashCode must contain at least 1 byte.");
        return fromBytesNoCopy((byte[]) abyte.clone());
    }

    static HashCode fromBytesNoCopy(byte[] abyte) {
        return new HashCode.BytesHashCode(abyte);
    }

    public static HashCode fromString(String s) {
        Preconditions.checkArgument(s.length() >= 2, "input string (%s) must have at least 2 characters", s);
        Preconditions.checkArgument(s.length() % 2 == 0, "input string (%s) must have an even number of characters", s);
        byte[] abyte = new byte[s.length() / 2];

        for (int i = 0; i < s.length(); i += 2) {
            int j = decode(s.charAt(i)) << 4;
            int k = decode(s.charAt(i + 1));

            abyte[i / 2] = (byte) (j + k);
        }

        return fromBytesNoCopy(abyte);
    }

    private static int decode(char c0) {
        if (c0 >= '0' && c0 <= '9') {
            return c0 - 48;
        } else if (c0 >= 'a' && c0 <= 'f') {
            return c0 - 97 + 10;
        } else {
            throw new IllegalArgumentException("Illegal hexadecimal character: " + c0);
        }
    }

    public final boolean equals(@Nullable Object object) {
        if (object instanceof HashCode) {
            HashCode hashcode = (HashCode) object;

            return MessageDigest.isEqual(this.asBytes(), hashcode.asBytes());
        } else {
            return false;
        }
    }

    public final int hashCode() {
        if (this.bits() >= 32) {
            return this.asInt();
        } else {
            byte[] abyte = this.asBytes();
            int i = abyte[0] & 255;

            for (int j = 1; j < abyte.length; ++j) {
                i |= (abyte[j] & 255) << j * 8;
            }

            return i;
        }
    }

    public final String toString() {
        byte[] abyte = this.asBytes();
        StringBuilder stringbuilder = new StringBuilder(2 * abyte.length);
        byte[] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte b0 = abyte1[j];

            stringbuilder.append(HashCode.hexDigits[b0 >> 4 & 15]).append(HashCode.hexDigits[b0 & 15]);
        }

        return stringbuilder.toString();
    }

    private static final class BytesHashCode extends HashCode implements Serializable {

        final byte[] bytes;
        private static final long serialVersionUID = 0L;

        BytesHashCode(byte[] abyte) {
            this.bytes = (byte[]) Preconditions.checkNotNull(abyte);
        }

        @Override
        public int bits() {
            return this.bytes.length * 8;
        }

        @Override
        public byte[] asBytes() {
            return (byte[]) this.bytes.clone();
        }

        @Override
        public int asInt() {
            Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", this.bytes.length);
            return this.bytes[0] & 255 | (this.bytes[1] & 255) << 8 | (this.bytes[2] & 255) << 16 | (this.bytes[3] & 255) << 24;
        }

        @Override
        public long asLong() {
            Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", this.bytes.length);
            return this.padToLong();
        }

        @Override
        public long padToLong() {
            long i = (long) (this.bytes[0] & 255);

            for (int j = 1; j < Math.min(this.bytes.length, 8); ++j) {
                i |= ((long) this.bytes[j] & 255L) << j * 8;
            }

            return i;
        }

        @Override
        void writeBytesToImpl(byte[] abyte, int i, int j) {
            System.arraycopy(this.bytes, 0, abyte, i, j);
        }
    }

    private static final class LongHashCode extends HashCode implements Serializable {

        final long hash;
        private static final long serialVersionUID = 0L;

        LongHashCode(long i) {
            this.hash = i;
        }

        @Override
        public int bits() {
            return 64;
        }

        @Override
        public byte[] asBytes() {
            return new byte[]{(byte) ((int) this.hash), (byte) ((int) (this.hash >> 8)), (byte) ((int) (this.hash >> 16)), (byte) ((int) (this.hash >> 24)), (byte) ((int) (this.hash >> 32)), (byte) ((int) (this.hash >> 40)), (byte) ((int) (this.hash >> 48)), (byte) ((int) (this.hash >> 56))};
        }

        @Override
        public int asInt() {
            return (int) this.hash;
        }

        @Override
        public long asLong() {
            return this.hash;
        }

        @Override
        public long padToLong() {
            return this.hash;
        }

        @Override
        void writeBytesToImpl(byte[] abyte, int i, int j) {
            for (int k = 0; k < j; ++k) {
                abyte[i + k] = (byte) ((int) (this.hash >> k * 8));
            }

        }
    }

    private static final class IntHashCode extends HashCode implements Serializable {

        final int hash;
        private static final long serialVersionUID = 0L;

        IntHashCode(int i) {
            this.hash = i;
        }

        @Override
        public int bits() {
            return 32;
        }

        @Override
        public byte[] asBytes() {
            return new byte[]{(byte) this.hash, (byte) (this.hash >> 8), (byte) (this.hash >> 16), (byte) (this.hash >> 24)};
        }

        @Override
        public int asInt() {
            return this.hash;
        }

        @Override
        public long asLong() {
            throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
        }

        @Override
        public long padToLong() {
            return UnsignedInts.toLong(this.hash);
        }

        @Override
        void writeBytesToImpl(byte[] abyte, int i, int j) {
            for (int k = 0; k < j; ++k) {
                abyte[i + k] = (byte) (this.hash >> k * 8);
            }

        }
    }
}
