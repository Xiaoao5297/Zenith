package io.netty.util.internal.logging;

import java.util.HashMap;
import java.util.Map;

final class MessageFormatter {

    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    static FormattingTuple format(String s, Object object) {
        return arrayFormat(s, new Object[]{object});
    }

    static FormattingTuple format(String s, Object object, Object object1) {
        return arrayFormat(s, new Object[]{object, object1});
    }

    static Throwable getThrowableCandidate(Object[] aobject) {
        if (aobject != null && aobject.length != 0) {
            Object object = aobject[aobject.length - 1];

            return object instanceof Throwable ? (Throwable) object : null;
        } else {
            return null;
        }
    }

    static FormattingTuple arrayFormat(String s, Object[] aobject) {
        Throwable throwable = getThrowableCandidate(aobject);

        if (s == null) {
            return new FormattingTuple((String) null, aobject, throwable);
        } else if (aobject == null) {
            return new FormattingTuple(s);
        } else {
            int i = 0;
            StringBuffer stringbuffer = new StringBuffer(s.length() + 50);

            int j;

            for (j = 0; j < aobject.length; ++j) {
                int k = s.indexOf("{}", i);

                if (k == -1) {
                    if (i == 0) {
                        return new FormattingTuple(s, aobject, throwable);
                    }

                    stringbuffer.append(s.substring(i, s.length()));
                    return new FormattingTuple(stringbuffer.toString(), aobject, throwable);
                }

                if (isEscapedDelimeter(s, k)) {
                    if (!isDoubleEscaped(s, k)) {
                        --j;
                        stringbuffer.append(s.substring(i, k - 1));
                        stringbuffer.append('{');
                        i = k + 1;
                    } else {
                        stringbuffer.append(s.substring(i, k - 1));
                        deeplyAppendParameter(stringbuffer, aobject[j], new HashMap());
                        i = k + 2;
                    }
                } else {
                    stringbuffer.append(s.substring(i, k));
                    deeplyAppendParameter(stringbuffer, aobject[j], new HashMap());
                    i = k + 2;
                }
            }

            stringbuffer.append(s.substring(i, s.length()));
            if (j < aobject.length - 1) {
                return new FormattingTuple(stringbuffer.toString(), aobject, throwable);
            } else {
                return new FormattingTuple(stringbuffer.toString(), aobject, (Throwable) null);
            }
        }
    }

    static boolean isEscapedDelimeter(String s, int i) {
        return i == 0 ? false : s.charAt(i - 1) == '\\';
    }

    static boolean isDoubleEscaped(String s, int i) {
        return i >= 2 && s.charAt(i - 2) == '\\';
    }

    private static void deeplyAppendParameter(StringBuffer stringbuffer, Object object, Map<Object[], Void> map) {
        if (object == null) {
            stringbuffer.append("null");
        } else {
            if (!object.getClass().isArray()) {
                safeObjectAppend(stringbuffer, object);
            } else if (object instanceof boolean[]) {
                booleanArrayAppend(stringbuffer, (boolean[]) ((boolean[]) object));
            } else if (object instanceof byte[]) {
                byteArrayAppend(stringbuffer, (byte[]) ((byte[]) object));
            } else if (object instanceof char[]) {
                charArrayAppend(stringbuffer, (char[]) ((char[]) object));
            } else if (object instanceof short[]) {
                shortArrayAppend(stringbuffer, (short[]) ((short[]) object));
            } else if (object instanceof int[]) {
                intArrayAppend(stringbuffer, (int[]) ((int[]) object));
            } else if (object instanceof long[]) {
                longArrayAppend(stringbuffer, (long[]) ((long[]) object));
            } else if (object instanceof float[]) {
                floatArrayAppend(stringbuffer, (float[]) ((float[]) object));
            } else if (object instanceof double[]) {
                doubleArrayAppend(stringbuffer, (double[]) ((double[]) object));
            } else {
                objectArrayAppend(stringbuffer, (Object[]) ((Object[]) object), map);
            }

        }
    }

    private static void safeObjectAppend(StringBuffer stringbuffer, Object object) {
        try {
            String s = object.toString();

            stringbuffer.append(s);
        } catch (Throwable throwable) {
            System.err.println("SLF4J: Failed toString() invocation on an object of type [" + object.getClass().getName() + ']');
            throwable.printStackTrace();
            stringbuffer.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(StringBuffer stringbuffer, Object[] aobject, Map<Object[], Void> map) {
        stringbuffer.append('[');
        if (!map.containsKey(aobject)) {
            map.put(aobject, (Object) null);
            int i = aobject.length;

            for (int j = 0; j < i; ++j) {
                deeplyAppendParameter(stringbuffer, aobject[j], map);
                if (j != i - 1) {
                    stringbuffer.append(", ");
                }
            }

            map.remove(aobject);
        } else {
            stringbuffer.append("...");
        }

        stringbuffer.append(']');
    }

    private static void booleanArrayAppend(StringBuffer stringbuffer, boolean[] aboolean) {
        stringbuffer.append('[');
        int i = aboolean.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(aboolean[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void byteArrayAppend(StringBuffer stringbuffer, byte[] abyte) {
        stringbuffer.append('[');
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(abyte[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void charArrayAppend(StringBuffer stringbuffer, char[] achar) {
        stringbuffer.append('[');
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(achar[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void shortArrayAppend(StringBuffer stringbuffer, short[] ashort) {
        stringbuffer.append('[');
        int i = ashort.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(ashort[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void intArrayAppend(StringBuffer stringbuffer, int[] aint) {
        stringbuffer.append('[');
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(aint[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void longArrayAppend(StringBuffer stringbuffer, long[] along) {
        stringbuffer.append('[');
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(along[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void floatArrayAppend(StringBuffer stringbuffer, float[] afloat) {
        stringbuffer.append('[');
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(afloat[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private static void doubleArrayAppend(StringBuffer stringbuffer, double[] adouble) {
        stringbuffer.append('[');
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            stringbuffer.append(adouble[j]);
            if (j != i - 1) {
                stringbuffer.append(", ");
            }
        }

        stringbuffer.append(']');
    }

    private MessageFormatter() {}
}
