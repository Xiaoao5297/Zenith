package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;

@GwtCompatible
public final class SignedBytes {

    public static final byte MAX_POWER_OF_TWO = 64;

    private SignedBytes() {}

    public static byte checkedCast(long i) {
        byte b0 = (byte) ((int) i);

        if ((long) b0 != i) {
            throw new IllegalArgumentException("Out of range: " + i);
        } else {
            return b0;
        }
    }

    public static byte saturatedCast(long i) {
        return i > 127L ? 127 : (i < -128L ? Byte.MIN_VALUE : (byte) ((int) i));
    }

    public static int compare(byte b0, byte b1) {
        return b0 - b1;
    }

    public static byte min(byte... abyte) {
        Preconditions.checkArgument(abyte.length > 0);
        byte b0 = abyte[0];

        for (int i = 1; i < abyte.length; ++i) {
            if (abyte[i] < b0) {
                b0 = abyte[i];
            }
        }

        return b0;
    }

    public static byte max(byte... abyte) {
        Preconditions.checkArgument(abyte.length > 0);
        byte b0 = abyte[0];

        for (int i = 1; i < abyte.length; ++i) {
            if (abyte[i] > b0) {
                b0 = abyte[i];
            }
        }

        return b0;
    }

    public static String join(String s, byte... abyte) {
        Preconditions.checkNotNull(s);
        if (abyte.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(abyte.length * 5);

            stringbuilder.append(abyte[0]);

            for (int i = 1; i < abyte.length; ++i) {
                stringbuilder.append(s).append(abyte[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<byte[]> lexicographicalComparator() {
        return SignedBytes.LexicographicalComparator.INSTANCE;
    }

    private static enum LexicographicalComparator implements Comparator<byte[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(byte[] abyte, byte[] abyte1) {
            int i = Math.min(abyte.length, abyte1.length);

            for (int j = 0; j < i; ++j) {
                int k = SignedBytes.compare(abyte[j], abyte1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return abyte.length - abyte1.length;
        }
    }
}
