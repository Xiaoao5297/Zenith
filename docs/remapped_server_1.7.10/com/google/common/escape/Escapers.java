package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class Escapers {

    private static final Escaper NULL_ESCAPER = new CharEscaper() {
        @Override
        public String escape(String s) {
            return (String) Preconditions.checkNotNull(s);
        }

        @Override
        protected char[] escape(char c0) {
            return null;
        }
    };

    private Escapers() {}

    public static Escaper nullEscaper() {
        return Escapers.NULL_ESCAPER;
    }

    public static Escapers.Builder builder() {
        return new Escapers.Builder();
    }

    static UnicodeEscaper asUnicodeEscaper(Escaper escaper) {
        Preconditions.checkNotNull(escaper);
        if (escaper instanceof UnicodeEscaper) {
            return (UnicodeEscaper) escaper;
        } else if (escaper instanceof CharEscaper) {
            return wrap((CharEscaper) escaper);
        } else {
            throw new IllegalArgumentException("Cannot create a UnicodeEscaper from: " + escaper.getClass().getName());
        }
    }

    public static String computeReplacement(CharEscaper charescaper, char c0) {
        return stringOrNull(charescaper.escape(c0));
    }

    public static String computeReplacement(UnicodeEscaper unicodeescaper, int i) {
        return stringOrNull(unicodeescaper.escape(i));
    }

    private static String stringOrNull(char[] achar) {
        return achar == null ? null : new String(achar);
    }

    private static UnicodeEscaper wrap(final CharEscaper charescaper) {
        return new UnicodeEscaper() {
            @Override
            protected char[] escape(int i) {
                if (i < 65536) {
                    return charescaper.escape((char) i);
                } else {
                    char[] achar = new char[2];

                    Character.toChars(i, achar, 0);
                    char[] achar1 = charescaper.escape(achar[0]);
                    char[] achar2 = charescaper.escape(achar[1]);

                    if (achar1 == null && achar2 == null) {
                        return null;
                    } else {
                        int j = achar1 != null ? achar1.length : 1;
                        int k = achar2 != null ? achar2.length : 1;
                        char[] achar3 = new char[j + k];
                        int l;

                        if (achar1 != null) {
                            for (l = 0; l < achar1.length; ++l) {
                                achar3[l] = achar1[l];
                            }
                        } else {
                            achar3[0] = achar[0];
                        }

                        if (achar2 != null) {
                            for (l = 0; l < achar2.length; ++l) {
                                achar3[j + l] = achar2[l];
                            }
                        } else {
                            achar3[j] = achar[1];
                        }

                        return achar3;
                    }
                }
            }
        };
    }

    @Beta
    public static final class Builder {

        private final Map<Character, String> replacementMap;
        private char safeMin;
        private char safeMax;
        private String unsafeReplacement;

        private Builder() {
            this.replacementMap = new HashMap();
            this.safeMin = 0;
            this.safeMax = '\uffff';
            this.unsafeReplacement = null;
        }

        public Escapers.Builder setSafeRange(char c0, char c1) {
            this.safeMin = c0;
            this.safeMax = c1;
            return this;
        }

        public Escapers.Builder setUnsafeReplacement(@Nullable String s) {
            this.unsafeReplacement = s;
            return this;
        }

        public Escapers.Builder addEscape(char c0, String s) {
            Preconditions.checkNotNull(s);
            this.replacementMap.put(c0, s);
            return this;
        }

        public Escaper build() {
            return new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) {
                private final char[] replacementChars;

                {
                    this.replacementChars = Builder.this.unsafeReplacement != null ? Builder.this.unsafeReplacement.toCharArray() : null;
                }

                @Override
                protected char[] escapeUnsafe(char c0) {
                    return this.replacementChars;
                }
            };
        }
    }
}
