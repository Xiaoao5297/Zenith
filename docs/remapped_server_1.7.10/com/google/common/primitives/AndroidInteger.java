package com.google.common.primitives;

import com.google.common.base.Preconditions;
import javax.annotation.CheckForNull;

final class AndroidInteger {

    @CheckForNull
    static Integer tryParse(String s) {
        return tryParse(s, 10);
    }

    @CheckForNull
    static Integer tryParse(String s, int i) {
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(i >= 2, "Invalid radix %s, min radix is %s", i, 2);
        Preconditions.checkArgument(i <= 36, "Invalid radix %s, max radix is %s", i, 36);
        int j = s.length();
        int k = 0;

        if (j == 0) {
            return null;
        } else {
            boolean flag = s.charAt(k) == '-';

            if (flag) {
                ++k;
                if (k == j) {
                    return null;
                }
            }

            return tryParse(s, k, i, flag);
        }
    }

    @CheckForNull
    private static Integer tryParse(String s, int i, int j, boolean flag) {
        int k = Integer.MIN_VALUE / j;
        int l = 0;

        int i1;

        for (int j1 = s.length(); i < j1; l = i1) {
            int k1 = Character.digit(s.charAt(i++), j);

            if (k1 == -1) {
                return null;
            }

            if (k > l) {
                return null;
            }

            i1 = l * j - k1;
            if (i1 > l) {
                return null;
            }
        }

        if (!flag) {
            l = -l;
            if (l < 0) {
                return null;
            }
        }

        if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
            return l;
        } else {
            return null;
        }
    }

    private AndroidInteger() {}
}
