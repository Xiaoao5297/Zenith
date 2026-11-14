package org.apache.logging.log4j.util;

import java.util.Locale;

public final class EnglishEnums {

    private EnglishEnums() {}

    public static <T extends Enum<T>> T valueOf(Class<T> oclass, String s) {
        return valueOf(oclass, s, (Enum) null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> oclass, String s, T t0) {
        return s == null ? t0 : Enum.valueOf(oclass, s.toUpperCase(Locale.ENGLISH));
    }
}
