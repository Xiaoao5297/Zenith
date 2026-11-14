package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@Beta
@GwtCompatible
public final class ArrayBasedEscaperMap {

    private final char[][] replacementArray;
    private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];

    public static ArrayBasedEscaperMap create(Map<Character, String> map) {
        return new ArrayBasedEscaperMap(createReplacementArray(map));
    }

    private ArrayBasedEscaperMap(char[][] achar) {
        this.replacementArray = achar;
    }

    char[][] getReplacementArray() {
        return this.replacementArray;
    }

    @VisibleForTesting
    static char[][] createReplacementArray(Map<Character, String> map) {
        Preconditions.checkNotNull(map);
        if (map.isEmpty()) {
            return ArrayBasedEscaperMap.EMPTY_REPLACEMENT_ARRAY;
        } else {
            char c0 = (Character) Collections.max(map.keySet());
            char[][] achar = new char[c0 + 1][];

            char c1;

            for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); achar[c1] = ((String) map.get(c1)).toCharArray()) {
                c1 = (Character) iterator.next();
            }

            return achar;
        }
    }
}
