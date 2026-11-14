package org.apache.commons.lang3.text;

import java.util.Map;
import java.util.Properties;

public abstract class StrLookup<V> {

    private static final StrLookup<String> NONE_LOOKUP = new StrLookup.MapStrLookup<>((Map) null);
    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP;

    public static StrLookup<?> noneLookup() {
        return StrLookup.NONE_LOOKUP;
    }

    public static StrLookup<String> systemPropertiesLookup() {
        return StrLookup.SYSTEM_PROPERTIES_LOOKUP;
    }

    public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
        return new StrLookup.MapStrLookup<>(map);
    }

    protected StrLookup() {}

    public abstract String lookup(String s);

    static {
        Object object = null;

        try {
            Properties properties = System.getProperties();

            object = new StrLookup.MapStrLookup<>(properties);
        } catch (SecurityException securityexception) {
            object = StrLookup.NONE_LOOKUP;
        }

        SYSTEM_PROPERTIES_LOOKUP = (StrLookup) object;
    }

    static class MapStrLookup<V> extends StrLookup<V> {

        private final Map<String, V> map;

        MapStrLookup(Map<String, V> map) {
            this.map = map;
        }

        @Override
        public String lookup(String s) {
            if (this.map == null) {
                return null;
            } else {
                Object object = this.map.get(s);

                return object == null ? null : object.toString();
            }
        }
    }
}
