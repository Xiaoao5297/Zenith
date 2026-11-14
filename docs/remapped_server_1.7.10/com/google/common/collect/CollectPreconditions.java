package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@GwtCompatible
final class CollectPreconditions {

    CollectPreconditions() {}

    static void checkEntryNotNull(Object object, Object object1) {
        if (object == null) {
            throw new NullPointerException("null key in entry: null=" + object1);
        } else if (object1 == null) {
            throw new NullPointerException("null value in entry: " + object + "=null");
        }
    }

    static int checkNonnegative(int i, String s) {
        if (i < 0) {
            throw new IllegalArgumentException(s + " cannot be negative but was: " + i);
        } else {
            return i;
        }
    }

    static void checkRemove(boolean flag) {
        Preconditions.checkState(flag, "no calls to next() since the last call to remove()");
    }
}
