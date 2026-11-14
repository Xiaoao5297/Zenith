package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform {

    private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal<char[]>() {
        protected char[] initialValue() {
            return new char[1024];
        }
    };

    private Platform() {}

    static char[] charBufferFromThreadLocal() {
        return (char[]) Platform.DEST_TL.get();
    }

    static long systemNanoTime() {
        return System.nanoTime();
    }

    static CharMatcher precomputeCharMatcher(CharMatcher charmatcher) {
        return charmatcher.precomputedInternal();
    }
}
