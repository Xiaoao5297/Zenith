package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@Beta
@GwtCompatible
public abstract class UnicodeEscaper extends Escaper {

    private static final int DEST_PAD = 32;

    protected UnicodeEscaper() {}

    protected abstract char[] escape(int i);

    protected int nextEscapeIndex(CharSequence charsequence, int i, int j) {
        int k;
        int l;

        for (l = i; l < j; l += Character.isSupplementaryCodePoint(k) ? 2 : 1) {
            k = codePointAt(charsequence, l, j);
            if (k < 0 || this.escape(k) != null) {
                break;
            }
        }

        return l;
    }

    @Override
    public String escape(String s) {
        Preconditions.checkNotNull(s);
        int i = s.length();
        int j = this.nextEscapeIndex(s, 0, i);

        return j == i ? s : this.escapeSlow(s, j);
    }

    protected final String escapeSlow(String s, int i) {
        int j = s.length();
        char[] achar = Platform.charBufferFromThreadLocal();
        int k = 0;

        int l;
        int i1;
        int j1;

        for (j1 = 0; i < j; i = this.nextEscapeIndex(s, i1, j)) {
            l = codePointAt(s, i, j);
            if (l < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }

            char[] achar1 = this.escape(l);

            i1 = i + (Character.isSupplementaryCodePoint(l) ? 2 : 1);
            if (achar1 != null) {
                int k1 = i - j1;
                int l1 = k + k1 + achar1.length;

                if (achar.length < l1) {
                    int i2 = l1 + (j - i) + 32;

                    achar = growBuffer(achar, k, i2);
                }

                if (k1 > 0) {
                    s.getChars(j1, i, achar, k);
                    k += k1;
                }

                if (achar1.length > 0) {
                    System.arraycopy(achar1, 0, achar, k, achar1.length);
                    k += achar1.length;
                }

                j1 = i1;
            }
        }

        l = j - j1;
        if (l > 0) {
            int j2 = k + l;

            if (achar.length < j2) {
                achar = growBuffer(achar, k, j2);
            }

            s.getChars(j1, j, achar, k);
            k = j2;
        }

        return new String(achar, 0, k);
    }

    protected static int codePointAt(CharSequence charsequence, int i, int j) {
        Preconditions.checkNotNull(charsequence);
        if (i < j) {
            char c0 = charsequence.charAt(i++);

            if (c0 >= '\ud800' && c0 <= '\udfff') {
                if (c0 <= '\udbff') {
                    if (i == j) {
                        return -c0;
                    } else {
                        char c1 = charsequence.charAt(i);

                        if (Character.isLowSurrogate(c1)) {
                            return Character.toCodePoint(c0, c1);
                        } else {
                            throw new IllegalArgumentException("Expected low surrogate but got char '" + c1 + "' with value " + c1 + " at index " + i + " in '" + charsequence + "'");
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected low surrogate character '" + c0 + "' with value " + c0 + " at index " + (i - 1) + " in '" + charsequence + "'");
                }
            } else {
                return c0;
            }
        } else {
            throw new IndexOutOfBoundsException("Index exceeds specified range");
        }
    }

    private static char[] growBuffer(char[] achar, int i, int j) {
        char[] achar1 = new char[j];

        if (i > 0) {
            System.arraycopy(achar, 0, achar1, 0, i);
        }

        return achar1;
    }
}
