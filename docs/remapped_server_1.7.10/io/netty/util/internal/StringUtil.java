package io.netty.util.internal;

import java.util.ArrayList;
import java.util.Formatter;

public final class StringUtil {

    public static final String NEWLINE;
    private static final String EMPTY_STRING = "";

    private StringUtil() {}

    public static String[] split(String s, char c0) {
        int i = s.length();
        ArrayList arraylist = new ArrayList();
        int j = 0;

        int k;

        for (k = 0; k < i; ++k) {
            if (s.charAt(k) == c0) {
                if (j == k) {
                    arraylist.add("");
                } else {
                    arraylist.add(s.substring(j, k));
                }

                j = k + 1;
            }
        }

        if (j == 0) {
            arraylist.add(s);
        } else if (j != i) {
            arraylist.add(s.substring(j, i));
        } else {
            for (k = arraylist.size() - 1; k >= 0 && ((String) arraylist.get(k)).isEmpty(); --k) {
                arraylist.remove(k);
            }
        }

        return (String[]) arraylist.toArray(new String[arraylist.size()]);
    }

    public static String simpleClassName(Object object) {
        return simpleClassName(object.getClass());
    }

    public static String simpleClassName(Class<?> oclass) {
        Package package = oclass.getPackage();

        return package != null ? oclass.getName().substring(package.getName().length() + 1) : oclass.getName();
    }

    static {
        String s;

        try {
            s = (new Formatter()).format("%n").toString();
        } catch (Exception exception) {
            s = "\n";
        }

        NEWLINE = s;
    }
}
