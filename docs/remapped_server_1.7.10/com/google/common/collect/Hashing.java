package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
final class Hashing {

    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static int MAX_TABLE_SIZE = 1073741824;

    private Hashing() {}

    static int smear(int i) {
        return 461845907 * Integer.rotateLeft(i * -862048943, 15);
    }

    static int smearedHash(@Nullable Object object) {
        return smear(object == null ? 0 : object.hashCode());
    }

    static int closedTableSize(int i, double d0) {
        i = Math.max(i, 2);
        int j = Integer.highestOneBit(i);

        if (i > (int) (d0 * (double) j)) {
            j <<= 1;
            return j > 0 ? j : Hashing.MAX_TABLE_SIZE;
        } else {
            return j;
        }
    }

    static boolean needsResizing(int i, int j, double d0) {
        return (double) i > d0 * (double) j && j < Hashing.MAX_TABLE_SIZE;
    }
}
