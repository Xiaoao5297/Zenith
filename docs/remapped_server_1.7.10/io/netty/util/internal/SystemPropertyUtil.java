package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class SystemPropertyUtil {

    private static boolean initializedLogger = true;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);
    private static boolean loggedException;
    private static final Pattern INTEGER_PATTERN = Pattern.compile("-?[0-9]+");

    public static boolean contains(String s) {
        return get(s) != null;
    }

    public static String get(String s) {
        return get(s, (String) null);
    }

    public static String get(String s, String s1) {
        if (s == null) {
            throw new NullPointerException("key");
        } else if (s.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty.");
        } else {
            String s2 = null;

            try {
                s2 = System.getProperty(s);
            } catch (Exception exception) {
                if (!SystemPropertyUtil.loggedException) {
                    log("Unable to retrieve a system property '" + s + "'; default values will be used.", exception);
                    SystemPropertyUtil.loggedException = true;
                }
            }

            return s2 == null ? s1 : s2;
        }
    }

    public static boolean getBoolean(String s, boolean flag) {
        String s1 = get(s);

        if (s1 == null) {
            return flag;
        } else {
            s1 = s1.trim().toLowerCase();
            if (s1.isEmpty()) {
                return true;
            } else if (!"true".equals(s1) && !"yes".equals(s1) && !"1".equals(s1)) {
                if (!"false".equals(s1) && !"no".equals(s1) && !"0".equals(s1)) {
                    log("Unable to parse the boolean system property '" + s + "':" + s1 + " - " + "using the default value: " + flag);
                    return flag;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static int getInt(String s, int i) {
        String s1 = get(s);

        if (s1 == null) {
            return i;
        } else {
            s1 = s1.trim().toLowerCase();
            if (SystemPropertyUtil.INTEGER_PATTERN.matcher(s1).matches()) {
                try {
                    return Integer.parseInt(s1);
                } catch (Exception exception) {
                    ;
                }
            }

            log("Unable to parse the integer system property '" + s + "':" + s1 + " - " + "using the default value: " + i);
            return i;
        }
    }

    public static long getLong(String s, long i) {
        String s1 = get(s);

        if (s1 == null) {
            return i;
        } else {
            s1 = s1.trim().toLowerCase();
            if (SystemPropertyUtil.INTEGER_PATTERN.matcher(s1).matches()) {
                try {
                    return Long.parseLong(s1);
                } catch (Exception exception) {
                    ;
                }
            }

            log("Unable to parse the long integer system property '" + s + "':" + s1 + " - " + "using the default value: " + i);
            return i;
        }
    }

    private static void log(String s) {
        if (SystemPropertyUtil.initializedLogger) {
            SystemPropertyUtil.logger.warn(s);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, s);
        }

    }

    private static void log(String s, Exception exception) {
        if (SystemPropertyUtil.initializedLogger) {
            SystemPropertyUtil.logger.warn(s, (Throwable) exception);
        } else {
            Logger.getLogger(SystemPropertyUtil.class.getName()).log(Level.WARNING, s, exception);
        }

    }

    private SystemPropertyUtil() {}
}
