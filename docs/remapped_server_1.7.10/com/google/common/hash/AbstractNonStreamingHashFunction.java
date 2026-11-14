package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction implements HashFunction {

    AbstractNonStreamingHashFunction() {}

    @Override
    public Hasher newHasher() {
        return new AbstractNonStreamingHashFunction.BufferingHasher(32);
    }

    @Override
    public Hasher newHasher(int i) {
        Preconditions.checkArgument(i >= 0);
        return new AbstractNonStreamingHashFunction.BufferingHasher(i);
    }

    @Override
    public <T> HashCode hashObject(T t0, Funnel<? super T> funnel) {
        return this.newHasher().putObject(t0, funnel).hash();
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence charsequence) {
        int i = charsequence.length();
        Hasher hasher = this.newHasher(i * 2);

        for (int j = 0; j < i; ++j) {
            hasher.putChar(charsequence.charAt(j));
        }

        return hasher.hash();
    }

    @Override
    public HashCode hashString(CharSequence charsequence, Charset charset) {
        return this.hashBytes(charsequence.toString().getBytes(charset));
    }

    @Override
    public HashCode hashInt(int i) {
        return this.newHasher(4).putInt(i).hash();
    }

    @Override
    public HashCode hashLong(long i) {
        return this.newHasher(8).putLong(i).hash();
    }

    @Override
    public HashCode hashBytes(byte[] abyte) {
        return this.hashBytes(abyte, 0, abyte.length);
    }

    private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {

        ExposedByteArrayOutputStream(int i) {
            super(i);
        }

        byte[] byteArray() {
            return this.buf;
        }

        int length() {
            return this.count;
        }
    }

    private final class BufferingHasher extends AbstractHasher {

        final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
        static final int BOTTOM_BYTE = 255;

        BufferingHasher(int i) {
            this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(i);
        }

        @Override
        public Hasher putByte(byte b0) {
            this.stream.write(b0);
            return this;
        }

        @Override
        public Hasher putBytes(byte[] abyte) {
            try {
                this.stream.write(abyte);
                return this;
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }
        }

        @Override
        public Hasher putBytes(byte[] abyte, int i, int j) {
            this.stream.write(abyte, i, j);
            return this;
        }

        @Override
        public Hasher putShort(short short0) {
            this.stream.write(short0 & 255);
            this.stream.write(short0 >>> 8 & 255);
            return this;
        }

        @Override
        public Hasher putInt(int i) {
            this.stream.write(i & 255);
            this.stream.write(i >>> 8 & 255);
            this.stream.write(i >>> 16 & 255);
            this.stream.write(i >>> 24 & 255);
            return this;
        }

        @Override
        public Hasher putLong(long i) {
            for (int j = 0; j < 64; j += 8) {
                this.stream.write((byte) ((int) (i >>> j & 255L)));
            }

            return this;
        }

        @Override
        public Hasher putChar(char c0) {
            this.stream.write(c0 & 255);
            this.stream.write(c0 >>> 8 & 255);
            return this;
        }

        @Override
        public <T> Hasher putObject(T t0, Funnel<? super T> funnel) {
            funnel.funnel(t0, this);
            return this;
        }

        @Override
        public HashCode hash() {
            return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
        }
    }
}
