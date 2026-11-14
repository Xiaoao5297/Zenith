package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.BitSet;

@GwtIncompatible("no precomputation is done in GWT")
final class SmallCharMatcher extends CharMatcher.FastMatcher {

    static final int MAX_SIZE = 1023;
    private final char[] table;
    private final boolean containsZero;
    private final long filter;
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static final double DESIRED_LOAD_FACTOR = 0.5D;

    private SmallCharMatcher(char[] achar, long i, boolean flag, String s) {
        super(s);
        this.table = achar;
        this.filter = i;
        this.containsZero = flag;
    }

    static int smear(int i) {
        return 461845907 * Integer.rotateLeft(i * -862048943, 15);
    }

    private boolean checkFilter(int i) {
        return 1L == (1L & this.filter >> i);
    }

    @VisibleForTesting
    static int chooseTableSize(int i) {
        if (i == 1) {
            return 2;
        } else {
            int j;

            for (j = Integer.highestOneBit(i - 1) << 1; (double) j * 0.5D < (double) i; j <<= 1) {
                ;
            }

            return j;
        }
    }

    @GwtIncompatible("java.util.BitSet")
    static CharMatcher from(BitSet bitset, String s) {
        long i = 0L;
        int j = bitset.cardinality();
        boolean flag = bitset.get(0);
        char[] achar = new char[chooseTableSize(j)];
        int k = achar.length - 1;

        for (int l = bitset.nextSetBit(0); l != -1; l = bitset.nextSetBit(l + 1)) {
            i |= 1L << l;

            int i1;

            for (i1 = smear(l) & k; achar[i1] != 0; i1 = i1 + 1 & k) {
                ;
            }

            achar[i1] = (char) l;
        }

        return new SmallCharMatcher(achar, i, flag, s);
    }

    @Override
    public boolean matches(char c0) {
        if (c0 == 0) {
            return this.containsZero;
        } else if (!this.checkFilter(c0)) {
            return false;
        } else {
            int i = this.table.length - 1;
            int j = smear(c0) & i;
            int k = j;

            while (this.table[k] != 0) {
                if (this.table[k] == c0) {
                    return true;
                }

                k = k + 1 & i;
                if (k == j) {
                    return false;
                }
            }

            return false;
        }
    }

    @GwtIncompatible("java.util.BitSet")
    @Override
    void setBits(BitSet bitset) {
        if (this.containsZero) {
            bitset.set(0);
        }

        char[] achar = this.table;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            if (c0 != 0) {
                bitset.set(c0);
            }
        }

    }
}
