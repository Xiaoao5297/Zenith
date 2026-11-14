package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map;

@Beta
@GwtCompatible
public abstract class ArrayBasedCharEscaper extends CharEscaper {

    private final char[][] replacements;
    private final int replacementsLength;
    private final char safeMin;
    private final char safeMax;

    protected ArrayBasedCharEscaper(Map<Character, String> map, char c0, char c1) {
        this(ArrayBasedEscaperMap.create(map), c0, c1);
    }

    protected ArrayBasedCharEscaper(ArrayBasedEscaperMap arraybasedescapermap, char c0, char c1) {
        Preconditions.checkNotNull(arraybasedescapermap);
        this.replacements = arraybasedescapermap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (c1 < c0) {
            c1 = 0;
            c0 = '\uffff';
        }

        this.safeMin = c0;
        this.safeMax = c1;
    }

    @Override
    public final String escape(String s) {
        Preconditions.checkNotNull(s);

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 < this.replacementsLength && this.replacements[c0] != null || c0 > this.safeMax || c0 < this.safeMin) {
                return this.escapeSlow(s, i);
            }
        }

        return s;
    }

    @Override
    protected final char[] escape(char c0) {
        if (c0 < this.replacementsLength) {
            char[] achar = this.replacements[c0];

            if (achar != null) {
                return achar;
            }
        }

        return c0 >= this.safeMin && c0 <= this.safeMax ? null : this.escapeUnsafe(c0);
    }

    protected abstract char[] escapeUnsafe(char c0);
}
