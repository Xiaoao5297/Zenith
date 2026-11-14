package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@Beta
@GwtCompatible
public final class CharEscaperBuilder {

    private final Map<Character, String> map = new HashMap();
    private int max = -1;

    public CharEscaperBuilder() {}

    public CharEscaperBuilder addEscape(char c0, String s) {
        this.map.put(c0, Preconditions.checkNotNull(s));
        if (c0 > this.max) {
            this.max = c0;
        }

        return this;
    }

    public CharEscaperBuilder addEscapes(char[] achar, String s) {
        Preconditions.checkNotNull(s);
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar1[j];

            this.addEscape(c0, s);
        }

        return this;
    }

    public char[][] toArray() {
        char[][] achar = new char[this.max + 1][];

        Entry entry;

        for (Iterator iterator = this.map.entrySet().iterator(); iterator.hasNext(); achar[(Character) entry.getKey()] = ((String) entry.getValue()).toCharArray()) {
            entry = (Entry) iterator.next();
        }

        return achar;
    }

    public Escaper toEscaper() {
        return new CharEscaperBuilder.CharArrayDecorator(this.toArray());
    }

    private static class CharArrayDecorator extends CharEscaper {

        private final char[][] replacements;
        private final int replaceLength;

        CharArrayDecorator(char[][] achar) {
            this.replacements = achar;
            this.replaceLength = achar.length;
        }

        @Override
        public String escape(String s) {
            int i = s.length();

            for (int j = 0; j < i; ++j) {
                char c0 = s.charAt(j);

                if (c0 < this.replacements.length && this.replacements[c0] != null) {
                    return this.escapeSlow(s, j);
                }
            }

            return s;
        }

        @Override
        protected char[] escape(char c0) {
            return c0 < this.replaceLength ? this.replacements[c0] : null;
        }
    }
}
