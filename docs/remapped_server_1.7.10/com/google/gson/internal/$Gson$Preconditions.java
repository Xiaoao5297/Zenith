package com.google.gson.internal;

public final class $Gson$Preconditions {

    public $Gson$Preconditions() {}

    public static <T> T checkNotNull(T t0) {
        if (t0 == null) {
            throw new NullPointerException();
        } else {
            return t0;
        }
    }

    public static void checkArgument(boolean flag) {
        if (!flag) {
            throw new IllegalArgumentException();
        }
    }
}
