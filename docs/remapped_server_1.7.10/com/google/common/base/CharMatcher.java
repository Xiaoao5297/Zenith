package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Arrays;
import java.util.BitSet;
import javax.annotation.CheckReturnValue;

@Beta
@GwtCompatible(emulated = true)
public abstract class CharMatcher implements Predicate<Character> {

    public static final CharMatcher BREAKING_WHITESPACE = new CharMatcher() {
        @Override
        public boolean matches(char c0) {
            switch (c0) {
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ' ':
                case '\u0085':
                case '\u1680':
                case '\u2028':
                case '\u2029':
                case '\u205f':
                case '\u3000':
                    return true;
                case '\u2007':
                    return false;
                default:
                    return c0 >= 8192 && c0 <= 8202;
            }
        }

        @Override
        public String toString() {
            return "CharMatcher.BREAKING_WHITESPACE";
        }
    };
    public static final CharMatcher ASCII = inRange('\u0000', '\u007f', "CharMatcher.ASCII");
    private static final String ZEROES = "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10";
    private static final String NINES;
    public static final CharMatcher DIGIT;
    public static final CharMatcher JAVA_DIGIT;
    public static final CharMatcher JAVA_LETTER;
    public static final CharMatcher JAVA_LETTER_OR_DIGIT;
    public static final CharMatcher JAVA_UPPER_CASE;
    public static final CharMatcher JAVA_LOWER_CASE;
    public static final CharMatcher JAVA_ISO_CONTROL;
    public static final CharMatcher INVISIBLE;
    public static final CharMatcher SINGLE_WIDTH;
    public static final CharMatcher ANY;
    public static final CharMatcher NONE;
    final String description;
    private static final int DISTINCT_CHARS = 65536;
    public static final CharMatcher WHITESPACE;

    private static String showCharacter(char c0) {
        String s = "0123456789ABCDEF";
        char[] achar = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};

        for (int i = 0; i < 4; ++i) {
            achar[5 - i] = s.charAt(c0 & 15);
            c0 = (char) (c0 >> 4);
        }

        return String.copyValueOf(achar);
    }

    public static CharMatcher is(final char c0) {
        String s = "CharMatcher.is('" + showCharacter(c0) + "')";

        return new CharMatcher.FastMatcher(s) {
            @Override
            public boolean matches(char c1) {
                return c1 == c0;
            }

            @Override
            public String replaceFrom(CharSequence charsequence, char c1) {
                return charsequence.toString().replace(c0, c1);
            }

            @Override
            public CharMatcher and(CharMatcher charmatcher) {
                return (CharMatcher) (charmatcher.matches(c0) ? this : null.NONE);
            }

            @Override
            public CharMatcher or(CharMatcher charmatcher) {
                return charmatcher.matches(c0) ? charmatcher : super.or(charmatcher);
            }

            @Override
            public CharMatcher negate() {
                return isNot(c0);
            }

            @GwtIncompatible("java.util.BitSet")
            @Override
            void setBits(BitSet bitset) {
                bitset.set(c0);
            }
        };
    }

    public static CharMatcher isNot(final char c0) {
        String s = "CharMatcher.isNot('" + showCharacter(c0) + "')";

        return new CharMatcher.FastMatcher(s) {
            @Override
            public boolean matches(char c1) {
                return c1 != c0;
            }

            @Override
            public CharMatcher and(CharMatcher charmatcher) {
                return charmatcher.matches(c0) ? super.and(charmatcher) : charmatcher;
            }

            @Override
            public CharMatcher or(CharMatcher charmatcher) {
                return (CharMatcher) (charmatcher.matches(c0) ? null.ANY : this);
            }

            @GwtIncompatible("java.util.BitSet")
            @Override
            void setBits(BitSet bitset) {
                bitset.set(0, c0);
                bitset.set(c0 + 1, 65536);
            }

            @Override
            public CharMatcher negate() {
                return is(c0);
            }
        };
    }

    public static CharMatcher anyOf(CharSequence charsequence) {
        switch (charsequence.length()) {
            case 0:
                return CharMatcher.NONE;
            case 1:
                return is(charsequence.charAt(0));
            case 2:
                return isEither(charsequence.charAt(0), charsequence.charAt(1));
            default:
                final char[] achar = charsequence.toString().toCharArray();

                Arrays.sort(achar);
                StringBuilder stringbuilder = new StringBuilder("CharMatcher.anyOf(\"");
                char[] achar1 = achar;
                int i = achar.length;

                for (int j = 0; j < i; ++j) {
                    char c0 = achar1[j];

                    stringbuilder.append(showCharacter(c0));
                }

                stringbuilder.append("\")");
                return new CharMatcher(stringbuilder.toString()) {
                    @Override
                    public boolean matches(char c1) {
                        return Arrays.binarySearch(achar, c1) >= 0;
                    }

                    @GwtIncompatible("java.util.BitSet")
                    @Override
                    void setBits(BitSet bitset) {
                        char[] achar2 = achar;
                        int k = achar2.length;

                        for (int l = 0; l < k; ++l) {
                            char c1 = achar2[l];

                            bitset.set(c1);
                        }

                    }
                };
        }
    }

    private static CharMatcher isEither(final char c0, final char c1) {
        String s = "CharMatcher.anyOf(\"" + showCharacter(c0) + showCharacter(c1) + "\")";

        return new CharMatcher.FastMatcher(s) {
            @Override
            public boolean matches(char c2) {
                return c2 == c0 || c2 == c1;
            }

            @GwtIncompatible("java.util.BitSet")
            @Override
            void setBits(BitSet bitset) {
                bitset.set(c0);
                bitset.set(c1);
            }
        };
    }

    public static CharMatcher noneOf(CharSequence charsequence) {
        return anyOf(charsequence).negate();
    }

    public static CharMatcher inRange(char c0, char c1) {
        Preconditions.checkArgument(c1 >= c0);
        String s = "CharMatcher.inRange('" + showCharacter(c0) + "', '" + showCharacter(c1) + "')";

        return inRange(c0, c1, s);
    }

    static CharMatcher inRange(final char c0, final char c1, String s) {
        return new CharMatcher.FastMatcher(s) {
            @Override
            public boolean matches(char c2) {
                return c0 <= c2 && c2 <= c1;
            }

            @GwtIncompatible("java.util.BitSet")
            @Override
            void setBits(BitSet bitset) {
                bitset.set(c0, c1 + 1);
            }
        };
    }

    public static CharMatcher forPredicate(final Predicate<? super Character> predicate) {
        Preconditions.checkNotNull(predicate);
        if (predicate instanceof CharMatcher) {
            return (CharMatcher) predicate;
        } else {
            String s = "CharMatcher.forPredicate(" + predicate + ")";

            return new CharMatcher(s) {
                @Override
                public boolean matches(char c0) {
                    return predicate.apply(c0);
                }

                @Override
                public boolean apply(Character character) {
                    return predicate.apply(Preconditions.checkNotNull(character));
                }
            };
        }
    }

    CharMatcher(String s) {
        this.description = s;
    }

    protected CharMatcher() {
        this.description = super.toString();
    }

    public abstract boolean matches(char c0);

    public CharMatcher negate() {
        return new CharMatcher.NegatedMatcher(this);
    }

    public CharMatcher and(CharMatcher charmatcher) {
        return new CharMatcher.And(this, (CharMatcher) Preconditions.checkNotNull(charmatcher));
    }

    public CharMatcher or(CharMatcher charmatcher) {
        return new CharMatcher.Or(this, (CharMatcher) Preconditions.checkNotNull(charmatcher));
    }

    public CharMatcher precomputed() {
        return Platform.precomputeCharMatcher(this);
    }

    CharMatcher withToString(String s) {
        throw new UnsupportedOperationException();
    }

    @GwtIncompatible("java.util.BitSet")
    CharMatcher precomputedInternal() {
        BitSet bitset = new BitSet();

        this.setBits(bitset);
        int i = bitset.cardinality();

        if (i * 2 <= 65536) {
            return precomputedPositive(i, bitset, this.description);
        } else {
            bitset.flip(0, 65536);
            int j = 65536 - i;
            String s = ".negate()";
            String s1 = this.description.endsWith(s) ? this.description.substring(0, this.description.length() - s.length()) : this.description + s;

            return new CharMatcher.NegatedFastMatcher(this.toString(), precomputedPositive(j, bitset, s1));
        }
    }

    @GwtIncompatible("java.util.BitSet")
    private static CharMatcher precomputedPositive(int i, BitSet bitset, String s) {
        switch (i) {
            case 0:
                return CharMatcher.NONE;
            case 1:
                return is((char) bitset.nextSetBit(0));
            case 2:
                char c0 = (char) bitset.nextSetBit(0);
                char c1 = (char) bitset.nextSetBit(c0 + 1);

                return isEither(c0, c1);
            default:
                return (CharMatcher) (isSmall(i, bitset.length()) ? SmallCharMatcher.from(bitset, s) : new CharMatcher.BitSetMatcher(bitset, s));
        }
    }

    private static boolean isSmall(int i, int j) {
        return i <= 1023 && j > i * 4 * 16;
    }

    @GwtIncompatible("java.util.BitSet")
    void setBits(BitSet bitset) {
        for (int i = 65535; i >= 0; --i) {
            if (this.matches((char) i)) {
                bitset.set(i);
            }
        }

    }

    public boolean matchesAnyOf(CharSequence charsequence) {
        return !this.matchesNoneOf(charsequence);
    }

    public boolean matchesAllOf(CharSequence charsequence) {
        for (int i = charsequence.length() - 1; i >= 0; --i) {
            if (!this.matches(charsequence.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean matchesNoneOf(CharSequence charsequence) {
        return this.indexIn(charsequence) == -1;
    }

    public int indexIn(CharSequence charsequence) {
        int i = charsequence.length();

        for (int j = 0; j < i; ++j) {
            if (this.matches(charsequence.charAt(j))) {
                return j;
            }
        }

        return -1;
    }

    public int indexIn(CharSequence charsequence, int i) {
        int j = charsequence.length();

        Preconditions.checkPositionIndex(i, j);

        for (int k = i; k < j; ++k) {
            if (this.matches(charsequence.charAt(k))) {
                return k;
            }
        }

        return -1;
    }

    public int lastIndexIn(CharSequence charsequence) {
        for (int i = charsequence.length() - 1; i >= 0; --i) {
            if (this.matches(charsequence.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    public int countIn(CharSequence charsequence) {
        int i = 0;

        for (int j = 0; j < charsequence.length(); ++j) {
            if (this.matches(charsequence.charAt(j))) {
                ++i;
            }
        }

        return i;
    }

    @CheckReturnValue
    public String removeFrom(CharSequence charsequence) {
        String s = charsequence.toString();
        int i = this.indexIn(s);

        if (i == -1) {
            return s;
        } else {
            char[] achar = s.toCharArray();
            int j = 1;

            label24:
            while (true) {
                ++i;

                while (i != achar.length) {
                    if (this.matches(achar[i])) {
                        ++j;
                        continue label24;
                    }

                    achar[i - j] = achar[i];
                    ++i;
                }

                return new String(achar, 0, i - j);
            }
        }
    }

    @CheckReturnValue
    public String retainFrom(CharSequence charsequence) {
        return this.negate().removeFrom(charsequence);
    }

    @CheckReturnValue
    public String replaceFrom(CharSequence charsequence, char c0) {
        String s = charsequence.toString();
        int i = this.indexIn(s);

        if (i == -1) {
            return s;
        } else {
            char[] achar = s.toCharArray();

            achar[i] = c0;

            for (int j = i + 1; j < achar.length; ++j) {
                if (this.matches(achar[j])) {
                    achar[j] = c0;
                }
            }

            return new String(achar);
        }
    }

    @CheckReturnValue
    public String replaceFrom(CharSequence charsequence, CharSequence charsequence1) {
        int i = charsequence1.length();

        if (i == 0) {
            return this.removeFrom(charsequence);
        } else if (i == 1) {
            return this.replaceFrom(charsequence, charsequence1.charAt(0));
        } else {
            String s = charsequence.toString();
            int j = this.indexIn(s);

            if (j == -1) {
                return s;
            } else {
                int k = s.length();
                StringBuilder stringbuilder = new StringBuilder(k * 3 / 2 + 16);
                int l = 0;

                do {
                    stringbuilder.append(s, l, j);
                    stringbuilder.append(charsequence1);
                    l = j + 1;
                    j = this.indexIn(s, l);
                } while (j != -1);

                stringbuilder.append(s, l, k);
                return stringbuilder.toString();
            }
        }
    }

    @CheckReturnValue
    public String trimFrom(CharSequence charsequence) {
        int i = charsequence.length();

        int j;

        for (j = 0; j < i && this.matches(charsequence.charAt(j)); ++j) {
            ;
        }

        int k;

        for (k = i - 1; k > j && this.matches(charsequence.charAt(k)); --k) {
            ;
        }

        return charsequence.subSequence(j, k + 1).toString();
    }

    @CheckReturnValue
    public String trimLeadingFrom(CharSequence charsequence) {
        int i = charsequence.length();

        for (int j = 0; j < i; ++j) {
            if (!this.matches(charsequence.charAt(j))) {
                return charsequence.subSequence(j, i).toString();
            }
        }

        return "";
    }

    @CheckReturnValue
    public String trimTrailingFrom(CharSequence charsequence) {
        int i = charsequence.length();

        for (int j = i - 1; j >= 0; --j) {
            if (!this.matches(charsequence.charAt(j))) {
                return charsequence.subSequence(0, j + 1).toString();
            }
        }

        return "";
    }

    @CheckReturnValue
    public String collapseFrom(CharSequence charsequence, char c0) {
        int i = charsequence.length();

        for (int j = 0; j < i; ++j) {
            char c1 = charsequence.charAt(j);

            if (this.matches(c1)) {
                if (c1 != c0 || j != i - 1 && this.matches(charsequence.charAt(j + 1))) {
                    StringBuilder stringbuilder = (new StringBuilder(i)).append(charsequence.subSequence(0, j)).append(c0);

                    return this.finishCollapseFrom(charsequence, j + 1, i, c0, stringbuilder, true);
                }

                ++j;
            }
        }

        return charsequence.toString();
    }

    @CheckReturnValue
    public String trimAndCollapseFrom(CharSequence charsequence, char c0) {
        int i = charsequence.length();

        int j;

        for (j = 0; j < i && this.matches(charsequence.charAt(j)); ++j) {
            ;
        }

        int k;

        for (k = i - 1; k > j && this.matches(charsequence.charAt(k)); --k) {
            ;
        }

        return j == 0 && k == i - 1 ? this.collapseFrom(charsequence, c0) : this.finishCollapseFrom(charsequence, j, k + 1, c0, new StringBuilder(k + 1 - j), false);
    }

    private String finishCollapseFrom(CharSequence charsequence, int i, int j, char c0, StringBuilder stringbuilder, boolean flag) {
        for (int k = i; k < j; ++k) {
            char c1 = charsequence.charAt(k);

            if (this.matches(c1)) {
                if (!flag) {
                    stringbuilder.append(c0);
                    flag = true;
                }
            } else {
                stringbuilder.append(c1);
                flag = false;
            }
        }

        return stringbuilder.toString();
    }

    /** @deprecated */
    @Deprecated
    public boolean apply(Character character) {
        return this.matches(character);
    }

    public String toString() {
        return this.description;
    }

    static {
        StringBuilder stringbuilder = new StringBuilder("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length());

        for (int i = 0; i < "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length(); ++i) {
            stringbuilder.append((char) ("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".charAt(i) + 9));
        }

        NINES = stringbuilder.toString();
        DIGIT = new CharMatcher.RangesMatcher("CharMatcher.DIGIT", "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray(), CharMatcher.NINES.toCharArray());
        JAVA_DIGIT = new CharMatcher("CharMatcher.JAVA_DIGIT") {
            @Override
            public boolean matches(char c0) {
                return Character.isDigit(c0);
            }
        };
        JAVA_LETTER = new CharMatcher("CharMatcher.JAVA_LETTER") {
            @Override
            public boolean matches(char c0) {
                return Character.isLetter(c0);
            }
        };
        JAVA_LETTER_OR_DIGIT = new CharMatcher("CharMatcher.JAVA_LETTER_OR_DIGIT") {
            @Override
            public boolean matches(char c0) {
                return Character.isLetterOrDigit(c0);
            }
        };
        JAVA_UPPER_CASE = new CharMatcher("CharMatcher.JAVA_UPPER_CASE") {
            @Override
            public boolean matches(char c0) {
                return Character.isUpperCase(c0);
            }
        };
        JAVA_LOWER_CASE = new CharMatcher("CharMatcher.JAVA_LOWER_CASE") {
            @Override
            public boolean matches(char c0) {
                return Character.isLowerCase(c0);
            }
        };
        JAVA_ISO_CONTROL = inRange('\u0000', '\u001f').or(inRange('\u007f', '\u009f')).withToString("CharMatcher.JAVA_ISO_CONTROL");
        INVISIBLE = new CharMatcher.RangesMatcher("CharMatcher.INVISIBLE", "\u0000\u007f\u00ad\u0600\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u206a\u3000\ud800\ufeff\ufff9\ufffa".toCharArray(), " \u00a0\u00ad\u0604\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u206f\u3000\uf8ff\ufeff\ufff9\ufffb".toCharArray());
        SINGLE_WIDTH = new CharMatcher.RangesMatcher("CharMatcher.SINGLE_WIDTH", "\u0000\u05be\u05d0\u05f3\u0600\u0750\u0e00\u1e00\u2100\ufb50\ufe70\uff61".toCharArray(), "\u04f9\u05be\u05ea\u05f4\u06ff\u077f\u0e7f\u20af\u213a\ufdff\ufeff\uffdc".toCharArray());
        ANY = new CharMatcher.FastMatcher("CharMatcher.ANY") {
            @Override
            public boolean matches(char c0) {
                return true;
            }

            @Override
            public int indexIn(CharSequence charsequence) {
                return charsequence.length() == 0 ? -1 : 0;
            }

            @Override
            public int indexIn(CharSequence charsequence, int j) {
                int k = charsequence.length();

                Preconditions.checkPositionIndex(j, k);
                return j == k ? -1 : j;
            }

            @Override
            public int lastIndexIn(CharSequence charsequence) {
                return charsequence.length() - 1;
            }

            @Override
            public boolean matchesAllOf(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return true;
            }

            @Override
            public boolean matchesNoneOf(CharSequence charsequence) {
                return charsequence.length() == 0;
            }

            @Override
            public String removeFrom(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return "";
            }

            @Override
            public String replaceFrom(CharSequence charsequence, char c0) {
                char[] achar = new char[charsequence.length()];

                Arrays.fill(achar, c0);
                return new String(achar);
            }

            @Override
            public String replaceFrom(CharSequence charsequence, CharSequence charsequence1) {
                StringBuilder stringbuilder1 = new StringBuilder(charsequence.length() * charsequence1.length());

                for (int j = 0; j < charsequence.length(); ++j) {
                    stringbuilder1.append(charsequence1);
                }

                return stringbuilder1.toString();
            }

            @Override
            public String collapseFrom(CharSequence charsequence, char c0) {
                return charsequence.length() == 0 ? "" : String.valueOf(c0);
            }

            @Override
            public String trimFrom(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return "";
            }

            @Override
            public int countIn(CharSequence charsequence) {
                return charsequence.length();
            }

            @Override
            public CharMatcher and(CharMatcher charmatcher) {
                return (CharMatcher) Preconditions.checkNotNull(charmatcher);
            }

            @Override
            public CharMatcher or(CharMatcher charmatcher) {
                Preconditions.checkNotNull(charmatcher);
                return this;
            }

            @Override
            public CharMatcher negate() {
                return null.NONE;
            }
        };
        NONE = new CharMatcher.FastMatcher("CharMatcher.NONE") {
            @Override
            public boolean matches(char c0) {
                return false;
            }

            @Override
            public int indexIn(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return -1;
            }

            @Override
            public int indexIn(CharSequence charsequence, int j) {
                int k = charsequence.length();

                Preconditions.checkPositionIndex(j, k);
                return -1;
            }

            @Override
            public int lastIndexIn(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return -1;
            }

            @Override
            public boolean matchesAllOf(CharSequence charsequence) {
                return charsequence.length() == 0;
            }

            @Override
            public boolean matchesNoneOf(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return true;
            }

            @Override
            public String removeFrom(CharSequence charsequence) {
                return charsequence.toString();
            }

            @Override
            public String replaceFrom(CharSequence charsequence, char c0) {
                return charsequence.toString();
            }

            @Override
            public String replaceFrom(CharSequence charsequence, CharSequence charsequence1) {
                Preconditions.checkNotNull(charsequence1);
                return charsequence.toString();
            }

            @Override
            public String collapseFrom(CharSequence charsequence, char c0) {
                return charsequence.toString();
            }

            @Override
            public String trimFrom(CharSequence charsequence) {
                return charsequence.toString();
            }

            @Override
            public String trimLeadingFrom(CharSequence charsequence) {
                return charsequence.toString();
            }

            @Override
            public String trimTrailingFrom(CharSequence charsequence) {
                return charsequence.toString();
            }

            @Override
            public int countIn(CharSequence charsequence) {
                Preconditions.checkNotNull(charsequence);
                return 0;
            }

            @Override
            public CharMatcher and(CharMatcher charmatcher) {
                Preconditions.checkNotNull(charmatcher);
                return this;
            }

            @Override
            public CharMatcher or(CharMatcher charmatcher) {
                return (CharMatcher) Preconditions.checkNotNull(charmatcher);
            }

            @Override
            public CharMatcher negate() {
                return null.ANY;
            }
        };
        WHITESPACE = new CharMatcher.FastMatcher("WHITESPACE") {
            private static final String TABLE = "\t\u3000\n\t\t\t\u202f\t\t\u2001\u2006\t\t\t\t\t\u180e\t\u2029\t\t\t\u2000\u2005\u200a\t\t\t\r\t\t\u2028\u1680\t\u00a0\t\u2004\u2009\t\t\t\f\u205f\t\t \t\t\u2003\u2008\t\t\t\u000b\u0085\t\t\t\t\t\t\u2002\u2007\t";

            @Override
            public boolean matches(char c0) {
                return "\t\u3000\n\t\t\t\u202f\t\t\u2001\u2006\t\t\t\t\t\u180e\t\u2029\t\t\t\u2000\u2005\u200a\t\t\t\r\t\t\u2028\u1680\t\u00a0\t\u2004\u2009\t\t\t\f\u205f\t\t \t\t\u2003\u2008\t\t\t\u000b\u0085\t\t\t\t\t\t\u2002\u2007\t".charAt(-844444961 * c0 >>> 26) == c0;
            }

            @GwtIncompatible("java.util.BitSet")
            @Override
            void setBits(BitSet bitset) {
                for (int j = 0; j < "\t\u3000\n\t\t\t\u202f\t\t\u2001\u2006\t\t\t\t\t\u180e\t\u2029\t\t\t\u2000\u2005\u200a\t\t\t\r\t\t\u2028\u1680\t\u00a0\t\u2004\u2009\t\t\t\f\u205f\t\t \t\t\u2003\u2008\t\t\t\u000b\u0085\t\t\t\t\t\t\u2002\u2007\t".length(); ++j) {
                    bitset.set("\t\u3000\n\t\t\t\u202f\t\t\u2001\u2006\t\t\t\t\t\u180e\t\u2029\t\t\t\u2000\u2005\u200a\t\t\t\r\t\t\u2028\u1680\t\u00a0\t\u2004\u2009\t\t\t\f\u205f\t\t \t\t\u2003\u2008\t\t\t\u000b\u0085\t\t\t\t\t\t\u2002\u2007\t".charAt(j));
                }

            }
        };
    }

    @GwtIncompatible("java.util.BitSet")
    private static class BitSetMatcher extends CharMatcher.FastMatcher {

        private final BitSet table;

        private BitSetMatcher(BitSet bitset, String s) {
            super(s);
            if (bitset.length() + 64 < bitset.size()) {
                bitset = (BitSet) bitset.clone();
            }

            this.table = bitset;
        }

        @Override
        public boolean matches(char c0) {
            return this.table.get(c0);
        }

        @Override
        void setBits(BitSet bitset) {
            bitset.or(this.table);
        }
    }

    static final class NegatedFastMatcher extends CharMatcher.NegatedMatcher {

        NegatedFastMatcher(CharMatcher charmatcher) {
            super(charmatcher);
        }

        NegatedFastMatcher(String s, CharMatcher charmatcher) {
            super(s, charmatcher);
        }

        @Override
        public final CharMatcher precomputed() {
            return this;
        }

        @Override
        CharMatcher withToString(String s) {
            return new CharMatcher.NegatedFastMatcher(s, this.original);
        }
    }

    abstract static class FastMatcher extends CharMatcher {

        FastMatcher() {}

        FastMatcher(String s) {
            super(s);
        }

        @Override
        public final CharMatcher precomputed() {
            return this;
        }

        @Override
        public CharMatcher negate() {
            return new CharMatcher.NegatedFastMatcher(this);
        }
    }

    private static class Or extends CharMatcher {

        final CharMatcher first;
        final CharMatcher second;

        Or(CharMatcher charmatcher, CharMatcher charmatcher1, String s) {
            super(s);
            this.first = (CharMatcher) Preconditions.checkNotNull(charmatcher);
            this.second = (CharMatcher) Preconditions.checkNotNull(charmatcher1);
        }

        Or(CharMatcher charmatcher, CharMatcher charmatcher1) {
            this(charmatcher, charmatcher1, "CharMatcher.or(" + charmatcher + ", " + charmatcher1 + ")");
        }

        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(BitSet bitset) {
            this.first.setBits(bitset);
            this.second.setBits(bitset);
        }

        @Override
        public boolean matches(char c0) {
            return this.first.matches(c0) || this.second.matches(c0);
        }

        @Override
        CharMatcher withToString(String s) {
            return new CharMatcher.Or(this.first, this.second, s);
        }
    }

    private static class And extends CharMatcher {

        final CharMatcher first;
        final CharMatcher second;

        And(CharMatcher charmatcher, CharMatcher charmatcher1) {
            this(charmatcher, charmatcher1, "CharMatcher.and(" + charmatcher + ", " + charmatcher1 + ")");
        }

        And(CharMatcher charmatcher, CharMatcher charmatcher1, String s) {
            super(s);
            this.first = (CharMatcher) Preconditions.checkNotNull(charmatcher);
            this.second = (CharMatcher) Preconditions.checkNotNull(charmatcher1);
        }

        @Override
        public boolean matches(char c0) {
            return this.first.matches(c0) && this.second.matches(c0);
        }

        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(BitSet bitset) {
            BitSet bitset1 = new BitSet();

            this.first.setBits(bitset1);
            BitSet bitset2 = new BitSet();

            this.second.setBits(bitset2);
            bitset1.and(bitset2);
            bitset.or(bitset1);
        }

        @Override
        CharMatcher withToString(String s) {
            return new CharMatcher.And(this.first, this.second, s);
        }
    }

    private static class NegatedMatcher extends CharMatcher {

        final CharMatcher original;

        NegatedMatcher(String s, CharMatcher charmatcher) {
            super(s);
            this.original = charmatcher;
        }

        NegatedMatcher(CharMatcher charmatcher) {
            this(charmatcher + ".negate()", charmatcher);
        }

        @Override
        public boolean matches(char c0) {
            return !this.original.matches(c0);
        }

        @Override
        public boolean matchesAllOf(CharSequence charsequence) {
            return this.original.matchesNoneOf(charsequence);
        }

        @Override
        public boolean matchesNoneOf(CharSequence charsequence) {
            return this.original.matchesAllOf(charsequence);
        }

        @Override
        public int countIn(CharSequence charsequence) {
            return charsequence.length() - this.original.countIn(charsequence);
        }

        @GwtIncompatible("java.util.BitSet")
        @Override
        void setBits(BitSet bitset) {
            BitSet bitset1 = new BitSet();

            this.original.setBits(bitset1);
            bitset1.flip(0, 65536);
            bitset.or(bitset1);
        }

        @Override
        public CharMatcher negate() {
            return this.original;
        }

        @Override
        CharMatcher withToString(String s) {
            return new CharMatcher.NegatedMatcher(s, this.original);
        }
    }

    private static class RangesMatcher extends CharMatcher {

        private final char[] rangeStarts;
        private final char[] rangeEnds;

        RangesMatcher(String s, char[] achar, char[] achar1) {
            super(s);
            this.rangeStarts = achar;
            this.rangeEnds = achar1;
            Preconditions.checkArgument(achar.length == achar1.length);

            for (int i = 0; i < achar.length; ++i) {
                Preconditions.checkArgument(achar[i] <= achar1[i]);
                if (i + 1 < achar.length) {
                    Preconditions.checkArgument(achar1[i] < achar[i + 1]);
                }
            }

        }

        @Override
        public boolean matches(char c0) {
            int i = Arrays.binarySearch(this.rangeStarts, c0);

            if (i >= 0) {
                return true;
            } else {
                i = ~i - 1;
                return i >= 0 && c0 <= this.rangeEnds[i];
            }
        }
    }
}
