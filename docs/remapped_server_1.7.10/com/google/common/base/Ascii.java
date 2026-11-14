package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@GwtCompatible
public final class Ascii {

    public static final byte NUL = 0;
    public static final byte SOH = 1;
    public static final byte STX = 2;
    public static final byte ETX = 3;
    public static final byte EOT = 4;
    public static final byte ENQ = 5;
    public static final byte ACK = 6;
    public static final byte BEL = 7;
    public static final byte BS = 8;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final byte NL = 10;
    public static final byte VT = 11;
    public static final byte FF = 12;
    public static final byte CR = 13;
    public static final byte SO = 14;
    public static final byte SI = 15;
    public static final byte DLE = 16;
    public static final byte DC1 = 17;
    public static final byte XON = 17;
    public static final byte DC2 = 18;
    public static final byte DC3 = 19;
    public static final byte XOFF = 19;
    public static final byte DC4 = 20;
    public static final byte NAK = 21;
    public static final byte SYN = 22;
    public static final byte ETB = 23;
    public static final byte CAN = 24;
    public static final byte EM = 25;
    public static final byte SUB = 26;
    public static final byte ESC = 27;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte RS = 30;
    public static final byte US = 31;
    public static final byte SP = 32;
    public static final byte SPACE = 32;
    public static final byte DEL = Byte.MAX_VALUE;
    public static final char MIN = '\u0000';
    public static final char MAX = '\u007f';

    private Ascii() {}

    public static String toLowerCase(String s) {
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            if (isUpperCase(s.charAt(j))) {
                char[] achar;

                for (achar = s.toCharArray(); j < i; ++j) {
                    char c0 = achar[j];

                    if (isUpperCase(c0)) {
                        achar[j] = (char) (c0 ^ 32);
                    }
                }

                return String.valueOf(achar);
            }
        }

        return s;
    }

    public static String toLowerCase(CharSequence charsequence) {
        if (charsequence instanceof String) {
            return toLowerCase((String) charsequence);
        } else {
            int i = charsequence.length();
            StringBuilder stringbuilder = new StringBuilder(i);

            for (int j = 0; j < i; ++j) {
                stringbuilder.append(toLowerCase(charsequence.charAt(j)));
            }

            return stringbuilder.toString();
        }
    }

    public static char toLowerCase(char c0) {
        return isUpperCase(c0) ? (char) (c0 ^ 32) : c0;
    }

    public static String toUpperCase(String s) {
        int i = s.length();

        for (int j = 0; j < i; ++j) {
            if (isLowerCase(s.charAt(j))) {
                char[] achar;

                for (achar = s.toCharArray(); j < i; ++j) {
                    char c0 = achar[j];

                    if (isLowerCase(c0)) {
                        achar[j] = (char) (c0 & 95);
                    }
                }

                return String.valueOf(achar);
            }
        }

        return s;
    }

    public static String toUpperCase(CharSequence charsequence) {
        if (charsequence instanceof String) {
            return toUpperCase((String) charsequence);
        } else {
            int i = charsequence.length();
            StringBuilder stringbuilder = new StringBuilder(i);

            for (int j = 0; j < i; ++j) {
                stringbuilder.append(toUpperCase(charsequence.charAt(j)));
            }

            return stringbuilder.toString();
        }
    }

    public static char toUpperCase(char c0) {
        return isLowerCase(c0) ? (char) (c0 & 95) : c0;
    }

    public static boolean isLowerCase(char c0) {
        return c0 >= 'a' && c0 <= 'z';
    }

    public static boolean isUpperCase(char c0) {
        return c0 >= 'A' && c0 <= 'Z';
    }

    @CheckReturnValue
    @Beta
    public static String truncate(CharSequence charsequence, int i, String s) {
        Preconditions.checkNotNull(charsequence);
        int j = i - s.length();

        Preconditions.checkArgument(j >= 0, "maxLength (%s) must be >= length of the truncation indicator (%s)", i, s.length());
        if (((CharSequence) charsequence).length() <= i) {
            String s1 = ((CharSequence) charsequence).toString();

            if (s1.length() <= i) {
                return s1;
            }

            charsequence = s1;
        }

        return (new StringBuilder(i)).append((CharSequence) charsequence, 0, j).append(s).toString();
    }

    @Beta
    public static boolean equalsIgnoreCase(CharSequence charsequence, CharSequence charsequence1) {
        int i = charsequence.length();

        if (charsequence == charsequence1) {
            return true;
        } else if (i != charsequence1.length()) {
            return false;
        } else {
            for (int j = 0; j < i; ++j) {
                char c0 = charsequence.charAt(j);
                char c1 = charsequence1.charAt(j);

                if (c0 != c1) {
                    int k = getAlphaIndex(c0);

                    if (k >= 26 || k != getAlphaIndex(c1)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private static int getAlphaIndex(char c0) {
        return (char) ((c0 | 32) - 97);
    }
}
