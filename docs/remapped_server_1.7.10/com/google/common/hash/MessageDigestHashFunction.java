package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class MessageDigestHashFunction extends AbstractStreamingHashFunction implements Serializable {

    private final MessageDigest prototype;
    private final int bytes;
    private final boolean supportsClone;
    private final String toString;

    MessageDigestHashFunction(String s, String s1) {
        this.prototype = getMessageDigest(s);
        this.bytes = this.prototype.getDigestLength();
        this.toString = (String) Preconditions.checkNotNull(s1);
        this.supportsClone = this.supportsClone();
    }

    MessageDigestHashFunction(String s, int i, String s1) {
        this.toString = (String) Preconditions.checkNotNull(s1);
        this.prototype = getMessageDigest(s);
        int j = this.prototype.getDigestLength();

        Preconditions.checkArgument(i >= 4 && i <= j, "bytes (%s) must be >= 4 and < %s", i, j);
        this.bytes = i;
        this.supportsClone = this.supportsClone();
    }

    private boolean supportsClone() {
        try {
            this.prototype.clone();
            return true;
        } catch (CloneNotSupportedException clonenotsupportedexception) {
            return false;
        }
    }

    @Override
    public int bits() {
        return this.bytes * 8;
    }

    public String toString() {
        return this.toString;
    }

    private static MessageDigest getMessageDigest(String s) {
        try {
            return MessageDigest.getInstance(s);
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new AssertionError(nosuchalgorithmexception);
        }
    }

    @Override
    public Hasher newHasher() {
        if (this.supportsClone) {
            try {
                return new MessageDigestHashFunction.MessageDigestHasher((MessageDigest) this.prototype.clone(), this.bytes);
            } catch (CloneNotSupportedException clonenotsupportedexception) {
                ;
            }
        }

        return new MessageDigestHashFunction.MessageDigestHasher(getMessageDigest(this.prototype.getAlgorithm()), this.bytes);
    }

    Object writeReplace() {
        return new MessageDigestHashFunction.SerializedForm(this.prototype.getAlgorithm(), this.bytes, this.toString);
    }

    private static final class MessageDigestHasher extends AbstractByteHasher {

        private final MessageDigest digest;
        private final int bytes;
        private boolean done;

        private MessageDigestHasher(MessageDigest messagedigest, int i) {
            this.digest = messagedigest;
            this.bytes = i;
        }

        @Override
        protected void update(byte b0) {
            this.checkNotDone();
            this.digest.update(b0);
        }

        @Override
        protected void update(byte[] abyte) {
            this.checkNotDone();
            this.digest.update(abyte);
        }

        @Override
        protected void update(byte[] abyte, int i, int j) {
            this.checkNotDone();
            this.digest.update(abyte, i, j);
        }

        private void checkNotDone() {
            Preconditions.checkState(!this.done, "Cannot use Hasher after calling #hash() on it");
        }

        @Override
        public HashCode hash() {
            this.done = true;
            return this.bytes == this.digest.getDigestLength() ? HashCode.fromBytesNoCopy(this.digest.digest()) : HashCode.fromBytesNoCopy(Arrays.copyOf(this.digest.digest(), this.bytes));
        }
    }

    private static final class SerializedForm implements Serializable {

        private final String algorithmName;
        private final int bytes;
        private final String toString;
        private static final long serialVersionUID = 0L;

        private SerializedForm(String s, int i, String s1) {
            this.algorithmName = s;
            this.bytes = i;
            this.toString = s1;
        }

        private Object readResolve() {
            return new MessageDigestHashFunction(this.algorithmName, this.bytes, this.toString);
        }
    }
}
