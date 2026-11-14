package gnu.trove.impl;

public class Constants {

    private static final boolean VERBOSE = System.getProperty("gnu.trove.verbose", (String) null) != null;
    public static final int DEFAULT_CAPACITY = 10;
    public static final float DEFAULT_LOAD_FACTOR = 0.5F;
    public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
    public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
    public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
    public static final int DEFAULT_INT_NO_ENTRY_VALUE;
    public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
    public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
    public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

    public Constants() {}

    static {
        String s = System.getProperty("gnu.trove.no_entry.byte", "0");
        int i;

        if ("MAX_VALUE".equalsIgnoreCase(s)) {
            i = 127;
        } else if ("MIN_VALUE".equalsIgnoreCase(s)) {
            i = -128;
        } else {
            i = Byte.valueOf(s);
        }

        if (i > 127) {
            i = 127;
        } else if (i < -128) {
            i = -128;
        }

        DEFAULT_BYTE_NO_ENTRY_VALUE = (byte) i;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + Constants.DEFAULT_BYTE_NO_ENTRY_VALUE);
        }

        s = System.getProperty("gnu.trove.no_entry.short", "0");
        if ("MAX_VALUE".equalsIgnoreCase(s)) {
            i = 32767;
        } else if ("MIN_VALUE".equalsIgnoreCase(s)) {
            i = -32768;
        } else {
            i = Short.valueOf(s);
        }

        if (i > 32767) {
            i = 32767;
        } else if (i < -32768) {
            i = -32768;
        }

        DEFAULT_SHORT_NO_ENTRY_VALUE = (short) i;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + Constants.DEFAULT_SHORT_NO_ENTRY_VALUE);
        }

        s = System.getProperty("gnu.trove.no_entry.char", "\u0000");
        char c0;

        if ("MAX_VALUE".equalsIgnoreCase(s)) {
            c0 = '\uffff';
        } else if ("MIN_VALUE".equalsIgnoreCase(s)) {
            c0 = 0;
        } else {
            c0 = s.toCharArray()[0];
        }

        if (c0 > '\uffff') {
            c0 = '\uffff';
        } else if (c0 < 0) {
            c0 = 0;
        }

        DEFAULT_CHAR_NO_ENTRY_VALUE = c0;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf(c0));
        }

        s = System.getProperty("gnu.trove.no_entry.int", "0");
        if ("MAX_VALUE".equalsIgnoreCase(s)) {
            i = Integer.MAX_VALUE;
        } else if ("MIN_VALUE".equalsIgnoreCase(s)) {
            i = Integer.MIN_VALUE;
        } else {
            i = Integer.valueOf(s);
        }

        DEFAULT_INT_NO_ENTRY_VALUE = i;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + Constants.DEFAULT_INT_NO_ENTRY_VALUE);
        }

        String s1 = System.getProperty("gnu.trove.no_entry.long", "0");
        long j;

        if ("MAX_VALUE".equalsIgnoreCase(s1)) {
            j = Long.MAX_VALUE;
        } else if ("MIN_VALUE".equalsIgnoreCase(s1)) {
            j = Long.MIN_VALUE;
        } else {
            j = Long.valueOf(s1);
        }

        DEFAULT_LONG_NO_ENTRY_VALUE = j;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + Constants.DEFAULT_LONG_NO_ENTRY_VALUE);
        }

        s = System.getProperty("gnu.trove.no_entry.float", "0");
        float f;

        if ("MAX_VALUE".equalsIgnoreCase(s)) {
            f = Float.MAX_VALUE;
        } else if ("MIN_VALUE".equalsIgnoreCase(s)) {
            f = Float.MIN_VALUE;
        } else if ("MIN_NORMAL".equalsIgnoreCase(s)) {
            f = Float.MIN_NORMAL;
        } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(s)) {
            f = Float.NEGATIVE_INFINITY;
        } else if ("POSITIVE_INFINITY".equalsIgnoreCase(s)) {
            f = Float.POSITIVE_INFINITY;
        } else {
            f = Float.valueOf(s);
        }

        DEFAULT_FLOAT_NO_ENTRY_VALUE = f;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE);
        }

        s1 = System.getProperty("gnu.trove.no_entry.double", "0");
        double d0;

        if ("MAX_VALUE".equalsIgnoreCase(s1)) {
            d0 = Double.MAX_VALUE;
        } else if ("MIN_VALUE".equalsIgnoreCase(s1)) {
            d0 = Double.MIN_VALUE;
        } else if ("MIN_NORMAL".equalsIgnoreCase(s1)) {
            d0 = Double.MIN_NORMAL;
        } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(s1)) {
            d0 = Double.NEGATIVE_INFINITY;
        } else if ("POSITIVE_INFINITY".equalsIgnoreCase(s1)) {
            d0 = Double.POSITIVE_INFINITY;
        } else {
            d0 = Double.valueOf(s1);
        }

        DEFAULT_DOUBLE_NO_ENTRY_VALUE = d0;
        if (Constants.VERBOSE) {
            System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE);
        }

    }
}
