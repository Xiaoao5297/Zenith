package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.zip.Checksum;

final class ChecksumHashFunction extends AbstractStreamingHashFunction implements Serializable {

    private final Supplier<? extends Checksum> checksumSupplier;
    private final int bits;
    private final String toString;
    private static final long serialVersionUID = 0L;

    ChecksumHashFunction(Supplier<? extends Checksum> supplier, int i, String s) {
        this.checksumSupplier = (Supplier) Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(i == 32 || i == 64, "bits (%s) must be either 32 or 64", i);
        this.bits = i;
        this.toString = (String) Preconditions.checkNotNull(s);
    }

    @Override
    public int bits() {
        return this.bits;
    }

    @Override
    public Hasher newHasher() {
        return new ChecksumHashFunction.ChecksumHasher((Checksum) this.checksumSupplier.get());
    }

    public String toString() {
        return this.toString;
    }

    private final class ChecksumHasher extends AbstractByteHasher {

        private final Checksum checksum;

        private ChecksumHasher(Checksum checksum) {
            this.checksum = (Checksum) Preconditions.checkNotNull(checksum);
        }

        @Override
        protected void update(byte b0) {
            this.checksum.update(b0);
        }

        @Override
        protected void update(byte[] abyte, int i, int j) {
            this.checksum.update(abyte, i, j);
        }

        @Override
        public HashCode hash() {
            long i = this.checksum.getValue();

            return ChecksumHashFunction.this.bits == 32 ? HashCode.fromInt((int) i) : HashCode.fromLong(i);
        }
    }
}
