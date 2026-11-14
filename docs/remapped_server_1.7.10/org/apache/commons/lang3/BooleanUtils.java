package org.apache.commons.lang3;

import org.apache.commons.lang3.math.NumberUtils;

public class BooleanUtils {

    public BooleanUtils() {}

    public static Boolean negate(Boolean obool) {
        return obool == null ? null : (obool ? Boolean.FALSE : Boolean.TRUE);
    }

    public static boolean isTrue(Boolean obool) {
        return Boolean.TRUE.equals(obool);
    }

    public static boolean isNotTrue(Boolean obool) {
        return !isTrue(obool);
    }

    public static boolean isFalse(Boolean obool) {
        return Boolean.FALSE.equals(obool);
    }

    public static boolean isNotFalse(Boolean obool) {
        return !isFalse(obool);
    }

    public static boolean toBoolean(Boolean obool) {
        return obool != null && obool;
    }

    public static boolean toBooleanDefaultIfNull(Boolean obool, boolean flag) {
        return obool == null ? flag : obool;
    }

    public static boolean toBoolean(int i) {
        return i != 0;
    }

    public static Boolean toBooleanObject(int i) {
        return i == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Boolean toBooleanObject(Integer integer) {
        return integer == null ? null : (integer == 0 ? Boolean.FALSE : Boolean.TRUE);
    }

    public static boolean toBoolean(int i, int j, int k) {
        if (i == j) {
            return true;
        } else if (i == k) {
            return false;
        } else {
            throw new IllegalArgumentException("The Integer did not match either specified value");
        }
    }

    public static boolean toBoolean(Integer integer, Integer integer1, Integer integer2) {
        if (integer == null) {
            if (integer1 == null) {
                return true;
            }

            if (integer2 == null) {
                return false;
            }
        } else {
            if (integer.equals(integer1)) {
                return true;
            }

            if (integer.equals(integer2)) {
                return false;
            }
        }

        throw new IllegalArgumentException("The Integer did not match either specified value");
    }

    public static Boolean toBooleanObject(int i, int j, int k, int l) {
        if (i == j) {
            return Boolean.TRUE;
        } else if (i == k) {
            return Boolean.FALSE;
        } else if (i == l) {
            return null;
        } else {
            throw new IllegalArgumentException("The Integer did not match any specified value");
        }
    }

    public static Boolean toBooleanObject(Integer integer, Integer integer1, Integer integer2, Integer integer3) {
        if (integer == null) {
            if (integer1 == null) {
                return Boolean.TRUE;
            }

            if (integer2 == null) {
                return Boolean.FALSE;
            }

            if (integer3 == null) {
                return null;
            }
        } else {
            if (integer.equals(integer1)) {
                return Boolean.TRUE;
            }

            if (integer.equals(integer2)) {
                return Boolean.FALSE;
            }

            if (integer.equals(integer3)) {
                return null;
            }
        }

        throw new IllegalArgumentException("The Integer did not match any specified value");
    }

    public static int toInteger(boolean flag) {
        return flag ? 1 : 0;
    }

    public static Integer toIntegerObject(boolean flag) {
        return flag ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
    }

    public static Integer toIntegerObject(Boolean obool) {
        return obool == null ? null : (obool ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO);
    }

    public static int toInteger(boolean flag, int i, int j) {
        return flag ? i : j;
    }

    public static int toInteger(Boolean obool, int i, int j, int k) {
        return obool == null ? k : (obool ? i : j);
    }

    public static Integer toIntegerObject(boolean flag, Integer integer, Integer integer1) {
        return flag ? integer : integer1;
    }

    public static Integer toIntegerObject(Boolean obool, Integer integer, Integer integer1, Integer integer2) {
        return obool == null ? integer2 : (obool ? integer : integer1);
    }

    public static Boolean toBooleanObject(String s) {
        if (s == "true") {
            return Boolean.TRUE;
        } else if (s == null) {
            return null;
        } else {
            char c0;
            char c1;
            char c2;
            char c3;

            switch (s.length()) {
                case 1:
                    c0 = s.charAt(0);
                    if (c0 == 'y' || c0 == 'Y' || c0 == 't' || c0 == 'T') {
                        return Boolean.TRUE;
                    }

                    if (c0 == 'n' || c0 == 'N' || c0 == 'f' || c0 == 'F') {
                        return Boolean.FALSE;
                    }
                    break;
                case 2:
                    c0 = s.charAt(0);
                    c1 = s.charAt(1);
                    if ((c0 == 'o' || c0 == 'O') && (c1 == 'n' || c1 == 'N')) {
                        return Boolean.TRUE;
                    }

                    if ((c0 == 'n' || c0 == 'N') && (c1 == 'o' || c1 == 'O')) {
                        return Boolean.FALSE;
                    }
                    break;
                case 3:
                    c0 = s.charAt(0);
                    c1 = s.charAt(1);
                    c2 = s.charAt(2);
                    if ((c0 == 'y' || c0 == 'Y') && (c1 == 'e' || c1 == 'E') && (c2 == 's' || c2 == 'S')) {
                        return Boolean.TRUE;
                    }

                    if ((c0 == 'o' || c0 == 'O') && (c1 == 'f' || c1 == 'F') && (c2 == 'f' || c2 == 'F')) {
                        return Boolean.FALSE;
                    }
                    break;
                case 4:
                    c0 = s.charAt(0);
                    c1 = s.charAt(1);
                    c2 = s.charAt(2);
                    c3 = s.charAt(3);
                    if ((c0 == 't' || c0 == 'T') && (c1 == 'r' || c1 == 'R') && (c2 == 'u' || c2 == 'U') && (c3 == 'e' || c3 == 'E')) {
                        return Boolean.TRUE;
                    }
                    break;
                case 5:
                    c0 = s.charAt(0);
                    c1 = s.charAt(1);
                    c2 = s.charAt(2);
                    c3 = s.charAt(3);
                    char c4 = s.charAt(4);

                    if ((c0 == 'f' || c0 == 'F') && (c1 == 'a' || c1 == 'A') && (c2 == 'l' || c2 == 'L') && (c3 == 's' || c3 == 'S') && (c4 == 'e' || c4 == 'E')) {
                        return Boolean.FALSE;
                    }
            }

            return null;
        }
    }

    public static Boolean toBooleanObject(String s, String s1, String s2, String s3) {
        if (s == null) {
            if (s1 == null) {
                return Boolean.TRUE;
            }

            if (s2 == null) {
                return Boolean.FALSE;
            }

            if (s3 == null) {
                return null;
            }
        } else {
            if (s.equals(s1)) {
                return Boolean.TRUE;
            }

            if (s.equals(s2)) {
                return Boolean.FALSE;
            }

            if (s.equals(s3)) {
                return null;
            }
        }

        throw new IllegalArgumentException("The String did not match any specified value");
    }

    public static boolean toBoolean(String s) {
        return toBooleanObject(s) == Boolean.TRUE;
    }

    public static boolean toBoolean(String s, String s1, String s2) {
        if (s == s1) {
            return true;
        } else if (s == s2) {
            return false;
        } else {
            if (s != null) {
                if (s.equals(s1)) {
                    return true;
                }

                if (s.equals(s2)) {
                    return false;
                }
            }

            throw new IllegalArgumentException("The String did not match either specified value");
        }
    }

    public static String toStringTrueFalse(Boolean obool) {
        return toString(obool, "true", "false", (String) null);
    }

    public static String toStringOnOff(Boolean obool) {
        return toString(obool, "on", "off", (String) null);
    }

    public static String toStringYesNo(Boolean obool) {
        return toString(obool, "yes", "no", (String) null);
    }

    public static String toString(Boolean obool, String s, String s1, String s2) {
        return obool == null ? s2 : (obool ? s : s1);
    }

    public static String toStringTrueFalse(boolean flag) {
        return toString(flag, "true", "false");
    }

    public static String toStringOnOff(boolean flag) {
        return toString(flag, "on", "off");
    }

    public static String toStringYesNo(boolean flag) {
        return toString(flag, "yes", "no");
    }

    public static String toString(boolean flag, String s, String s1) {
        return flag ? s : s1;
    }

    public static boolean and(boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            boolean[] aboolean1 = aboolean;
            int i = aboolean.length;

            for (int j = 0; j < i; ++j) {
                boolean flag = aboolean1[j];

                if (!flag) {
                    return false;
                }
            }

            return true;
        }
    }

    public static Boolean and(Boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            try {
                boolean[] aboolean1 = ArrayUtils.toPrimitive(aboolean);

                return and(aboolean1) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException nullpointerexception) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        }
    }

    public static boolean or(boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            boolean[] aboolean1 = aboolean;
            int i = aboolean.length;

            for (int j = 0; j < i; ++j) {
                boolean flag = aboolean1[j];

                if (flag) {
                    return true;
                }
            }

            return false;
        }
    }

    public static Boolean or(Boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            try {
                boolean[] aboolean1 = ArrayUtils.toPrimitive(aboolean);

                return or(aboolean1) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException nullpointerexception) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        }
    }

    public static boolean xor(boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            boolean flag = false;
            boolean[] aboolean1 = aboolean;
            int i = aboolean.length;

            for (int j = 0; j < i; ++j) {
                boolean flag1 = aboolean1[j];

                flag ^= flag1;
            }

            return flag;
        }
    }

    public static Boolean xor(Boolean... aboolean) {
        if (aboolean == null) {
            throw new IllegalArgumentException("The Array must not be null");
        } else if (aboolean.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        } else {
            try {
                boolean[] aboolean1 = ArrayUtils.toPrimitive(aboolean);

                return xor(aboolean1) ? Boolean.TRUE : Boolean.FALSE;
            } catch (NullPointerException nullpointerexception) {
                throw new IllegalArgumentException("The array must not contain any null elements");
            }
        }
    }
}
