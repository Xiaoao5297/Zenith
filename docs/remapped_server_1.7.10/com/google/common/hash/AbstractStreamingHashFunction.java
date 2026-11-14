package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction implements HashFunction {

    AbstractStreamingHashFunction() {}

    @Override
    public <T> HashCode hashObject(T t0, Funnel<? super T> funnel) {
        return this.newHasher().putObject(t0, funnel).hash();
    }

    @Override
    public HashCode hashUnencodedChars(CharSequence charsequence) {
        return this.newHasher().putUnencodedChars(charsequence).hash();
    }

    @Override
    public HashCode hashString(CharSequence charsequence, Charset charset) {
        return this.newHasher().putString(charsequence, charset).hash();
    }

    @Override
    public HashCode hashInt(int i) {
        return this.newHasher().putInt(i).hash();
    }

    @Override
    public HashCode hashLong(long i) {
        return this.newHasher().putLong(i).hash();
    }

    @Override
    public HashCode hashBytes(byte[] abyte) {
        return this.newHasher().putBytes(abyte).hash();
    }

    @Override
    public HashCode hashBytes(byte[] abyte, int i, int j) {
        return this.newHasher().putBytes(abyte, i, j).hash();
    }

    @Override
    public Hasher newHasher(int i) {
        Preconditions.checkArgument(i >= 0);
        return this.newHasher();
    }

    protected abstract static class AbstractStreamingHasher extends AbstractHasher {

        private final ByteBuffer buffer;
        private final int bufferSize;
        private final int chunkSize;

        protected AbstractStreamingHasher(int i) {
            this(i, i);
        }

        protected AbstractStreamingHasher(int i, int j) {
            Preconditions.checkArgument(j % i == 0);
            this.buffer = ByteBuffer.allocate(j + 7).order(ByteOrder.LITTLE_ENDIAN);
            this.bufferSize = j;
            this.chunkSize = i;
        }

        protected abstract void process(ByteBuffer bytebuffer);

        protected void processRemaining(ByteBuffer bytebuffer) {
            bytebuffer.position(bytebuffer.limit());
            bytebuffer.limit(this.chunkSize + 7);

            while (bytebuffer.position() < this.chunkSize) {
                bytebuffer.putLong(0L);
            }

            bytebuffer.limit(this.chunkSize);
            bytebuffer.flip();
            this.process(bytebuffer);
        }

        @Override
        public final Hasher putBytes(byte[] abyte) {
            return this.putBytes(abyte, 0, abyte.length);
        }

        @Override
        public final Hasher putBytes(byte[] abyte, int i, int j) {
            return this.putBytes(ByteBuffer.wrap(abyte, i, j).order(ByteOrder.LITTLE_ENDIAN));
        }

        private Hasher putBytes(ByteBuffer bytebuffer) {
            if (bytebuffer.remaining() <= this.buffer.remaining()) {
                this.buffer.put(bytebuffer);
                this.munchIfFull();
                return this;
            } else {
                int i = this.bufferSize - this.buffer.position();

                for (int j = 0; j < i; ++j) {
                    this.buffer.put(bytebuffer.get());
                }

                this.munch();

                while (bytebuffer.remaining() >= this.chunkSize) {
                    this.process(bytebuffer);
                }

                this.buffer.put(bytebuffer);
                return this;
            }
        }

        @Override
        public final Hasher putUnencodedChars(CharSequence charsequence) {
            for (int i = 0; i < charsequence.length(); ++i) {
                this.putChar(charsequence.charAt(i));
            }

            return this;
        }

        @Override
        public final Hasher putByte(byte b0) {
            this.buffer.put(b0);
            this.munchIfFull();
            return this;
        }

        @Override
        public final Hasher putShort(short short0) {
            this.buffer.putShort(short0);
            this.munchIfFull();
            return this;
        }

        @Override
        public final Hasher putChar(char c0) {
            this.buffer.putChar(c0);
            this.munchIfFull();
            return this;
        }

        @Override
        public final Hasher putInt(int i) {
            this.buffer.putInt(i);
            this.munchIfFull();
            return this;
        }

        @Override
        public final Hasher putLong(long i) {
            this.buffer.putLong(i);
            this.munchIfFull();
            return this;
        }

        @Override
        public final <T> Hasher putObject(T t0, Funnel<? super T> funnel) {
            funnel.funnel(t0, this);
            return this;
        }

        @Override
        public final HashCode hash() {
            this.munch();
            this.buffer.flip();
            if (this.buffer.remaining() > 0) {
                this.processRemaining(this.buffer);
            }

            return this.makeHash();
        }

        abstract HashCode makeHash();

        private void munchIfFull() {
            if (this.buffer.remaining() < 8) {
                this.munch();
            }

        }

        private void munch() {
            this.buffer.flip();

            while (this.buffer.remaining() >= this.chunkSize) {
                this.process(this.buffer);
            }

            this.buffer.compact();
        }
    }
}
