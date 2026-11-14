package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@Beta
@GwtCompatible
public abstract class CharEscaper extends Escaper {

    private static final int DEST_PAD = 32;

    protected CharEscaper() {}

    @Override
    public String escape(String s) {
        Preconditions.checkNotNull(s);
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            if (this.escape(s.charAt(j)) != null) {
                return this.escapeSlow(s, j);
            }
        }

        return s;
    }

    protected final String escapeSlow(String s, int i) {
        int j = s.length();
        char[] achar = Platform.charBufferFromThreadLocal();
        int k = achar.length;
        int l = 0;

        int i1;
        int j1;

        for (j1 = 0; i < j; ++i) {
            char[] achar1 = this.escape(s.charAt(i));

            if (achar1 != null) {
                i1 = achar1.length;
                int k1 = i - j1;
                int l1 = l + k1 + i1;

                if (k < l1) {
                    k = l1 + (j - i) + 32;
                    achar = growBuffer(achar, l, k);
                }

                if (k1 > 0) {
                    s.getChars(j1, i, achar, l);
                    l += k1;
                }

                if (i1 > 0) {
                    System.arraycopy(achar1, 0, achar, l, i1);
                    l += i1;
                }

                j1 = i + 1;
            }
        }

        int i2 = j - j1;

        if (i2 > 0) {
            i1 = l + i2;
            if (k < i1) {
                achar = growBuffer(achar, l, i1);
            }

            s.getChars(j1, j, achar, l);
            l = i1;
        }

        return new String(achar, 0, l);
    }

    protected abstract char[] escape(char c0);

    private static char[] growBuffer(char[] achar, int i, int j) {
        char[] achar1 = new char[j];

        if (i > 0) {
            System.arraycopy(achar, 0, achar1, 0, i);
        }

        return achar1;
    }
}
