package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;

@GwtCompatible
public final class Strings {

    private Strings() {}

    public static String nullToEmpty(@Nullable String s) {
        return s == null ? "" : s;
    }

    @Nullable
    public static String emptyToNull(@Nullable String s) {
        return isNullOrEmpty(s) ? null : s;
    }

    public static boolean isNullOrEmpty(@Nullable String s) {
        return s == null || s.length() == 0;
    }

    public static String padStart(String s, int i, char c0) {
        Preconditions.checkNotNull(s);
        if (s.length() >= i) {
            return s;
        } else {
            StringBuilder stringbuilder = new StringBuilder(i);

            for (int j = s.length(); j < i; ++j) {
                stringbuilder.append(c0);
            }

            stringbuilder.append(s);
            return stringbuilder.toString();
        }
    }

    public static String padEnd(String s, int i, char c0) {
        Preconditions.checkNotNull(s);
        if (s.length() >= i) {
            return s;
        } else {
            StringBuilder stringbuilder = new StringBuilder(i);

            stringbuilder.append(s);

            for (int j = s.length(); j < i; ++j) {
                stringbuilder.append(c0);
            }

            return stringbuilder.toString();
        }
    }

    public static String repeat(String s, int i) {
        Preconditions.checkNotNull(s);
        if (i <= 1) {
            Preconditions.checkArgument(i >= 0, "invalid count: %s", i);
            return i == 0 ? "" : s;
        } else {
            int j = s.length();
            long k = (long) j * (long) i;
            int l = (int) k;

            if ((long) l != k) {
                throw new ArrayIndexOutOfBoundsException("Required array size too large: " + String.valueOf(k));
            } else {
                char[] achar = new char[l];

                s.getChars(0, j, achar, 0);

                int i1;

                for (i1 = j; i1 < l - i1; i1 <<= 1) {
                    System.arraycopy(achar, 0, achar, i1, i1);
                }

                System.arraycopy(achar, 0, achar, i1, l - i1);
                return new String(achar);
            }
        }
    }

    public static String commonPrefix(CharSequence charsequence, CharSequence charsequence1) {
        Preconditions.checkNotNull(charsequence);
        Preconditions.checkNotNull(charsequence1);
        int i = Math.min(charsequence.length(), charsequence1.length());

        int j;

        for (j = 0; j < i && charsequence.charAt(j) == charsequence1.charAt(j); ++j) {
            ;
        }

        if (validSurrogatePairAt(charsequence, j - 1) || validSurrogatePairAt(charsequence1, j - 1)) {
            --j;
        }

        return charsequence.subSequence(0, j).toString();
    }

    public static String commonSuffix(CharSequence charsequence, CharSequence charsequence1) {
        Preconditions.checkNotNull(charsequence);
        Preconditions.checkNotNull(charsequence1);
        int i = Math.min(charsequence.length(), charsequence1.length());

        int j;

        for (j = 0; j < i && charsequence.charAt(charsequence.length() - j - 1) == charsequence1.charAt(charsequence1.length() - j - 1); ++j) {
            ;
        }

        if (validSurrogatePairAt(charsequence, charsequence.length() - j - 1) || validSurrogatePairAt(charsequence1, charsequence1.length() - j - 1)) {
            --j;
        }

        return charsequence.subSequence(charsequence.length() - j, charsequence.length()).toString();
    }

    @VisibleForTesting
    static boolean validSurrogatePairAt(CharSequence charsequence, int i) {
        return i >= 0 && i <= charsequence.length() - 2 && Character.isHighSurrogate(charsequence.charAt(i)) && Character.isLowSurrogate(charsequence.charAt(i + 1));
    }
}
