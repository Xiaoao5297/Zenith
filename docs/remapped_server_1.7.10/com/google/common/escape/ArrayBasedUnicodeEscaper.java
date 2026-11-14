package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract class ArrayBasedUnicodeEscaper extends UnicodeEscaper {

    private final char[][] replacements;
    private final int replacementsLength;
    private final int safeMin;
    private final int safeMax;
    private final char safeMinChar;
    private final char safeMaxChar;

    protected ArrayBasedUnicodeEscaper(Map<Character, String> map, int i, int j, @Nullable String s) {
        this(ArrayBasedEscaperMap.create(map), i, j, s);
    }

    protected ArrayBasedUnicodeEscaper(ArrayBasedEscaperMap arraybasedescapermap, int i, int j, @Nullable String s) {
        Preconditions.checkNotNull(arraybasedescapermap);
        this.replacements = arraybasedescapermap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (j < i) {
            j = -1;
            i = Integer.MAX_VALUE;
        }

        this.safeMin = i;
        this.safeMax = j;
        if (i >= 55296) {
            this.safeMinChar = '\uffff';
            this.safeMaxChar = 0;
        } else {
            this.safeMinChar = (char) i;
            this.safeMaxChar = (char) Math.min(j, 55295);
        }

    }

    @Override
    public final String escape(String s) {
        Preconditions.checkNotNull(s);

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 < this.replacementsLength && this.replacements[c0] != null || c0 > this.safeMaxChar || c0 < this.safeMinChar) {
                return this.escapeSlow(s, i);
            }
        }

        return s;
    }

    @Override
    protected final int nextEscapeIndex(CharSequence charsequence, int i, int j) {
        while (true) {
            if (i < j) {
                char c0 = charsequence.charAt(i);

                if ((c0 >= this.replacementsLength || this.replacements[c0] == null) && c0 <= this.safeMaxChar && c0 >= this.safeMinChar) {
                    ++i;
                    continue;
                }
            }

            return i;
        }
    }

    @Override
    protected final char[] escape(int i) {
        if (i < this.replacementsLength) {
            char[] achar = this.replacements[i];

            if (achar != null) {
                return achar;
            }
        }

        return i >= this.safeMin && i <= this.safeMax ? null : this.escapeUnsafe(i);
    }

    protected abstract char[] escapeUnsafe(int i);
}
