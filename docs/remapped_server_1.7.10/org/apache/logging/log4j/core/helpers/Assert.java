package org.apache.logging.log4j.core.helpers;

public final class Assert {

    private Assert() {}

    public static <T> T isNotNull(T t0, String s) {
        if (t0 == null) {
            throw new NullPointerException(s + " is null");
        } else {
            return t0;
        }
    }
}
