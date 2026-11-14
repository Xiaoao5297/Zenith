package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public enum CaseFormat {

    LOWER_HYPHEN(CharMatcher.is('-'), "-") {
        @Override
        String normalizeWord(String s) {
            return Ascii.toLowerCase(s);
        }

        @Override
        String convert(CaseFormat caseformat, String s) {
            return caseformat == null.LOWER_UNDERSCORE ? s.replace('-', '_') : (caseformat == null.UPPER_UNDERSCORE ? Ascii.toUpperCase(s.replace('-', '_')) : super.convert(caseformat, s));
        }
    },
    LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
        @Override
        String normalizeWord(String s) {
            return Ascii.toLowerCase(s);
        }

        @Override
        String convert(CaseFormat caseformat, String s) {
            return caseformat == null.LOWER_HYPHEN ? s.replace('_', '-') : (caseformat == null.UPPER_UNDERSCORE ? Ascii.toUpperCase(s) : super.convert(caseformat, s));
        }
    },
    LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        @Override
        String normalizeWord(String s) {
            return CaseFormat.firstCharOnlyToUpper(s);
        }
    },
    UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
        @Override
        String normalizeWord(String s) {
            return CaseFormat.firstCharOnlyToUpper(s);
        }
    },
    UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
        @Override
        String normalizeWord(String s) {
            return Ascii.toUpperCase(s);
        }

        @Override
        String convert(CaseFormat caseformat, String s) {
            return caseformat == null.LOWER_HYPHEN ? Ascii.toLowerCase(s.replace('_', '-')) : (caseformat == null.LOWER_UNDERSCORE ? Ascii.toLowerCase(s) : super.convert(caseformat, s));
        }
    };

    private final CharMatcher wordBoundary;
    private final String wordSeparator;

    private CaseFormat(CharMatcher charmatcher, String s) {
        this.wordBoundary = charmatcher;
        this.wordSeparator = s;
    }

    public final String to(CaseFormat caseformat, String s) {
        Preconditions.checkNotNull(caseformat);
        Preconditions.checkNotNull(s);
        return caseformat == this ? s : this.convert(caseformat, s);
    }

    String convert(CaseFormat caseformat, String s) {
        StringBuilder stringbuilder = null;
        int i = 0;
        int j = -1;

        while (true) {
            ++j;
            if ((j = this.wordBoundary.indexIn(s, j)) == -1) {
                return i == 0 ? caseformat.normalizeFirstWord(s) : stringbuilder.append(caseformat.normalizeWord(s.substring(i))).toString();
            }

            if (i == 0) {
                stringbuilder = new StringBuilder(s.length() + 4 * this.wordSeparator.length());
                stringbuilder.append(caseformat.normalizeFirstWord(s.substring(i, j)));
            } else {
                stringbuilder.append(caseformat.normalizeWord(s.substring(i, j)));
            }

            stringbuilder.append(caseformat.wordSeparator);
            i = j + this.wordSeparator.length();
        }
    }

    @Beta
    public Converter<String, String> converterTo(CaseFormat caseformat) {
        return new CaseFormat.StringConverter(this, caseformat);
    }

    abstract String normalizeWord(String s);

    private String normalizeFirstWord(String s) {
        return this == CaseFormat.LOWER_CAMEL ? Ascii.toLowerCase(s) : this.normalizeWord(s);
    }

    private static String firstCharOnlyToUpper(String s) {
        return s.isEmpty() ? s : (new StringBuilder(s.length())).append(Ascii.toUpperCase(s.charAt(0))).append(Ascii.toLowerCase(s.substring(1))).toString();
    }

    private static final class StringConverter extends Converter<String, String> implements Serializable {

        private final CaseFormat sourceFormat;
        private final CaseFormat targetFormat;
        private static final long serialVersionUID = 0L;

        StringConverter(CaseFormat caseformat, CaseFormat caseformat1) {
            this.sourceFormat = (CaseFormat) Preconditions.checkNotNull(caseformat);
            this.targetFormat = (CaseFormat) Preconditions.checkNotNull(caseformat1);
        }

        protected String doForward(String s) {
            return s == null ? null : this.sourceFormat.to(this.targetFormat, s);
        }

        protected String doBackward(String s) {
            return s == null ? null : this.targetFormat.to(this.sourceFormat, s);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof CaseFormat.StringConverter)) {
                return false;
            } else {
                CaseFormat.StringConverter caseformat_stringconverter = (CaseFormat.StringConverter) object;

                return this.sourceFormat.equals(caseformat_stringconverter.sourceFormat) && this.targetFormat.equals(caseformat_stringconverter.targetFormat);
            }
        }

        public int hashCode() {
            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
        }

        public String toString() {
            return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
        }
    }
}
