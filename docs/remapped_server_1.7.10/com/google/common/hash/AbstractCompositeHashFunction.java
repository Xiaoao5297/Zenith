package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction {

    final HashFunction[] functions;
    private static final long serialVersionUID = 0L;

    AbstractCompositeHashFunction(HashFunction... ahashfunction) {
        HashFunction[] ahashfunction1 = ahashfunction;
        int i = ahashfunction.length;

        for (int j = 0; j < i; ++j) {
            HashFunction hashfunction = ahashfunction1[j];

            Preconditions.checkNotNull(hashfunction);
        }

        this.functions = ahashfunction;
    }

    abstract HashCode makeHash(Hasher[] ahasher);

    @Override
    public Hasher newHasher() {
        final Hasher[] ahasher = new Hasher[this.functions.length];

        for (int i = 0; i < ahasher.length; ++i) {
            ahasher[i] = this.functions[i].newHasher();
        }

        return new Hasher() {
            @Override
            public Hasher putByte(byte b0) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putByte(b0);
                }

                return this;
            }

            @Override
            public Hasher putBytes(byte[] abyte) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putBytes(abyte);
                }

                return this;
            }

            @Override
            public Hasher putBytes(byte[] abyte, int j, int k) {
                Hasher[] ahasher1 = ahasher;
                int l = ahasher1.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    Hasher hasher = ahasher1[i1];

                    hasher.putBytes(abyte, j, k);
                }

                return this;
            }

            @Override
            public Hasher putShort(short short0) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putShort(short0);
                }

                return this;
            }

            @Override
            public Hasher putInt(int j) {
                Hasher[] ahasher1 = ahasher;
                int k = ahasher1.length;

                for (int l = 0; l < k; ++l) {
                    Hasher hasher = ahasher1[l];

                    hasher.putInt(j);
                }

                return this;
            }

            @Override
            public Hasher putLong(long j) {
                Hasher[] ahasher1 = ahasher;
                int k = ahasher1.length;

                for (int l = 0; l < k; ++l) {
                    Hasher hasher = ahasher1[l];

                    hasher.putLong(j);
                }

                return this;
            }

            @Override
            public Hasher putFloat(float f) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putFloat(f);
                }

                return this;
            }

            @Override
            public Hasher putDouble(double d0) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putDouble(d0);
                }

                return this;
            }

            @Override
            public Hasher putBoolean(boolean flag) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putBoolean(flag);
                }

                return this;
            }

            @Override
            public Hasher putChar(char c0) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putChar(c0);
                }

                return this;
            }

            @Override
            public Hasher putUnencodedChars(CharSequence charsequence) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putUnencodedChars(charsequence);
                }

                return this;
            }

            @Override
            public Hasher putString(CharSequence charsequence, Charset charset) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putString(charsequence, charset);
                }

                return this;
            }

            @Override
            public <T> Hasher putObject(T t0, Funnel<? super T> funnel) {
                Hasher[] ahasher1 = ahasher;
                int j = ahasher1.length;

                for (int k = 0; k < j; ++k) {
                    Hasher hasher = ahasher1[k];

                    hasher.putObject(t0, funnel);
                }

                return this;
            }

            @Override
            public HashCode hash() {
                return AbstractCompositeHashFunction.this.makeHash(ahasher);
            }
        };
    }
}
