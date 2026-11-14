package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public final class Utf8 {

    public static int encodedLength(CharSequence charsequence) {
        int i = charsequence.length();
        int j = i;

        int k;

        for (k = 0; k < i && charsequence.charAt(k) < 128; ++k) {
            ;
        }

        while (k < i) {
            char c0 = charsequence.charAt(k);

            if (c0 >= 2048) {
                j += encodedLengthGeneral(charsequence, k);
                break;
            }

            j += 127 - c0 >>> 31;
            ++k;
        }

        if (j < i) {
            throw new IllegalArgumentException("UTF-8 length does not fit in int: " + ((long) j + 4294967296L));
        } else {
            return j;
        }
    }

    private static int encodedLengthGeneral(CharSequence charsequence, int i) {
        int j = charsequence.length();
        int k = 0;

        for (int l = i; l < j; ++l) {
            char c0 = charsequence.charAt(l);

            if (c0 < 2048) {
                k += 127 - c0 >>> 31;
            } else {
                k += 2;
                if ('\ud800' <= c0 && c0 <= '\udfff') {
                    int i1 = Character.codePointAt(charsequence, l);

                    if (i1 < 65536) {
                        throw new IllegalArgumentException("Unpaired surrogate at index " + l);
                    }

                    ++l;
                }
            }
        }

        return k;
    }

    public static boolean isWellFormed(byte[] abyte) {
        return isWellFormed(abyte, 0, abyte.length);
    }

    public static boolean isWellFormed(byte[] abyte, int i, int j) {
        int k = i + j;

        Preconditions.checkPositionIndexes(i, k, abyte.length);

        for (int l = i; l < k; ++l) {
            if (abyte[l] < 0) {
                return isWellFormedSlowPath(abyte, l, k);
            }
        }

        return true;
    }

    private static boolean isWellFormedSlowPath(byte[] abyte, int i, int j) {
        int k = i;

        while (true) {
            byte b0;

            do {
                if (k >= j) {
                    return true;
                }
            } while ((b0 = abyte[k++]) >= 0);

            if (b0 < -32) {
                if (k == j) {
                    return false;
                }

                if (b0 < -62 || abyte[k++] > -65) {
                    return false;
                }
            } else {
                byte b1;

                if (b0 < -16) {
                    if (k + 1 >= j) {
                        return false;
                    }

                    b1 = abyte[k++];
                    if (b1 > -65 || b0 == -32 && b1 < -96 || b0 == -19 && -96 <= b1 || abyte[k++] > -65) {
                        return false;
                    }
                } else {
                    if (k + 2 >= j) {
                        return false;
                    }

                    b1 = abyte[k++];
                    if (b1 > -65 || (b0 << 28) + (b1 - -112) >> 30 != 0 || abyte[k++] > -65 || abyte[k++] > -65) {
                        return false;
                    }
                }
            }
        }
    }

    private Utf8() {}
}
