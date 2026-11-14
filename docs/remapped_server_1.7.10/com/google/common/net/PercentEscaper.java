package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.UnicodeEscaper;

@Beta
@GwtCompatible
public final class PercentEscaper extends UnicodeEscaper {

    private static final char[] PLUS_SIGN = new char[]{'+'};
    private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private final boolean plusForSpace;
    private final boolean[] safeOctets;

    public PercentEscaper(String s, boolean flag) {
        Preconditions.checkNotNull(s);
        if (s.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
        } else {
            s = s + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            if (flag && s.contains(" ")) {
                throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
            } else {
                this.plusForSpace = flag;
                this.safeOctets = createSafeOctets(s);
            }
        }
    }

    private static boolean[] createSafeOctets(String s) {
        int i = -1;
        char[] achar = s.toCharArray();
        char[] achar1 = achar;
        int j = achar.length;

        int k;
        int l;

        for (l = 0; l < j; ++l) {
            k = achar1[l];
            i = Math.max(k, i);
        }

        boolean[] aboolean = new boolean[i + 1];
        char[] achar2 = achar;

        l = achar.length;

        for (k = 0; k < l; ++k) {
            char c0 = achar2[k];

            aboolean[c0] = true;
        }

        return aboolean;
    }

    @Override
    protected int nextEscapeIndex(CharSequence charsequence, int i, int j) {
        Preconditions.checkNotNull(charsequence);

        while (i < j) {
            char c0 = charsequence.charAt(i);

            if (c0 >= this.safeOctets.length || !this.safeOctets[c0]) {
                break;
            }

            ++i;
        }

        return i;
    }

    @Override
    public String escape(String s) {
        Preconditions.checkNotNull(s);
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            char c0 = s.charAt(j);

            if (c0 >= this.safeOctets.length || !this.safeOctets[c0]) {
                return this.escapeSlow(s, j);
            }
        }

        return s;
    }

    @Override
    protected char[] escape(int i) {
        if (i < this.safeOctets.length && this.safeOctets[i]) {
            return null;
        } else if (i == 32 && this.plusForSpace) {
            return PercentEscaper.PLUS_SIGN;
        } else {
            char[] achar;

            if (i <= 127) {
                achar = new char[]{'%', PercentEscaper.UPPER_HEX_DIGITS[i >>> 4], PercentEscaper.UPPER_HEX_DIGITS[i & 15]};
                return achar;
            } else if (i <= 2047) {
                achar = new char[]{'%', '\u0000', '\u0000', '%', '\u0000', PercentEscaper.UPPER_HEX_DIGITS[i & 15]};
                i >>>= 4;
                achar[4] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[2] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[1] = PercentEscaper.UPPER_HEX_DIGITS[12 | i];
                return achar;
            } else if (i <= 65535) {
                achar = new char[9];
                achar[0] = '%';
                achar[1] = 'E';
                achar[3] = '%';
                achar[6] = '%';
                achar[8] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[7] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[5] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[4] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[2] = PercentEscaper.UPPER_HEX_DIGITS[i];
                return achar;
            } else if (i <= 1114111) {
                achar = new char[12];
                achar[0] = '%';
                achar[1] = 'F';
                achar[3] = '%';
                achar[6] = '%';
                achar[9] = '%';
                achar[11] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[10] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[8] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[7] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[5] = PercentEscaper.UPPER_HEX_DIGITS[i & 15];
                i >>>= 4;
                achar[4] = PercentEscaper.UPPER_HEX_DIGITS[8 | i & 3];
                i >>>= 2;
                achar[2] = PercentEscaper.UPPER_HEX_DIGITS[i & 7];
                return achar;
            } else {
                throw new IllegalArgumentException("Invalid unicode character value " + i);
            }
        }
    }
}
