package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible(emulated = true)
public final class Chars {

    public static final int BYTES = 2;

    private Chars() {}

    public static int hashCode(char c0) {
        return c0;
    }

    public static char checkedCast(long i) {
        char c0 = (char) ((int) i);

        if ((long) c0 != i) {
            throw new IllegalArgumentException("Out of range: " + i);
        } else {
            return c0;
        }
    }

    public static char saturatedCast(long i) {
        return i > 65535L ? '\uffff' : (i < 0L ? '\u0000' : (char) ((int) i));
    }

    public static int compare(char c0, char c1) {
        return c0 - c1;
    }

    public static boolean contains(char[] achar, char c0) {
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c1 = achar1[j];

            if (c1 == c0) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(char[] achar, char c0) {
        return indexOf(achar, c0, 0, achar.length);
    }

    private static int indexOf(char[] achar, char c0, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (achar[k] == c0) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(char[] achar, char[] achar1) {
        Preconditions.checkNotNull(achar, "array");
        Preconditions.checkNotNull(achar1, "target");
        if (achar1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < achar.length - achar1.length + 1) {
                for (int j = 0; j < achar1.length; ++j) {
                    if (achar[i + j] != achar1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(char[] achar, char c0) {
        return lastIndexOf(achar, c0, 0, achar.length);
    }

    private static int lastIndexOf(char[] achar, char c0, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (achar[k] == c0) {
                return k;
            }
        }

        return -1;
    }

    public static char min(char... achar) {
        Preconditions.checkArgument(achar.length > 0);
        char c0 = achar[0];

        for (int i = 1; i < achar.length; ++i) {
            if (achar[i] < c0) {
                c0 = achar[i];
            }
        }

        return c0;
    }

    public static char max(char... achar) {
        Preconditions.checkArgument(achar.length > 0);
        char c0 = achar[0];

        for (int i = 1; i < achar.length; ++i) {
            if (achar[i] > c0) {
                c0 = achar[i];
            }
        }

        return c0;
    }

    public static char[] concat(char[]... achar) {
        int i = 0;
        char[][] achar1 = achar;
        int j = achar.length;

        for (int k = 0; k < j; ++k) {
            char[] achar2 = achar1[k];

            i += achar2.length;
        }

        char[] achar3 = new char[i];

        j = 0;
        char[][] achar4 = achar;
        int l = achar.length;

        for (int i1 = 0; i1 < l; ++i1) {
            char[] achar5 = achar4[i1];

            System.arraycopy(achar5, 0, achar3, j, achar5.length);
            j += achar5.length;
        }

        return achar3;
    }

    @GwtIncompatible("doesn't work")
    public static byte[] toByteArray(char c0) {
        return new byte[]{(byte) (c0 >> 8), (byte) c0};
    }

    @GwtIncompatible("doesn't work")
    public static char fromByteArray(byte[] abyte) {
        Preconditions.checkArgument(abyte.length >= 2, "array too small: %s < %s", abyte.length, 2);
        return fromBytes(abyte[0], abyte[1]);
    }

    @GwtIncompatible("doesn't work")
    public static char fromBytes(byte b0, byte b1) {
        return (char) (b0 << 8 | b1 & 255);
    }

    public static char[] ensureCapacity(char[] achar, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return achar.length < i ? copyOf(achar, i + j) : achar;
    }

    private static char[] copyOf(char[] achar, int i) {
        char[] achar1 = new char[i];

        System.arraycopy(achar, 0, achar1, 0, Math.min(achar.length, i));
        return achar1;
    }

    public static String join(String s, char... achar) {
        Preconditions.checkNotNull(s);
        int i = achar.length;

        if (i == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(i + s.length() * (i - 1));

            stringbuilder.append(achar[0]);

            for (int j = 1; j < i; ++j) {
                stringbuilder.append(s).append(achar[j]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<char[]> lexicographicalComparator() {
        return Chars.LexicographicalComparator.INSTANCE;
    }

    public static char[] toArray(Collection<Character> collection) {
        if (collection instanceof Chars.CharArrayAsList) {
            return ((Chars.CharArrayAsList) collection).toCharArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            char[] achar = new char[i];

            for (int j = 0; j < i; ++j) {
                achar[j] = (Character) Preconditions.checkNotNull(aobject[j]);
            }

            return achar;
        }
    }

    public static List<Character> asList(char... achar) {
        return (List) (achar.length == 0 ? Collections.emptyList() : new Chars.CharArrayAsList(achar));
    }

    @GwtCompatible
    private static class CharArrayAsList extends AbstractList<Character> implements RandomAccess, Serializable {

        final char[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        CharArrayAsList(char[] achar) {
            this(achar, 0, achar.length);
        }

        CharArrayAsList(char[] achar, int i, int j) {
            this.array = achar;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Character get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Character && Chars.indexOf(this.array, (Character) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Character) {
                int i = Chars.indexOf(this.array, (Character) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Character) {
                int i = Chars.lastIndexOf(this.array, (Character) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Character set(int i, Character character) {
            Preconditions.checkElementIndex(i, this.size());
            char c0 = this.array[this.start + i];

            this.array[this.start + i] = (Character) Preconditions.checkNotNull(character);
            return c0;
        }

        public List<Character> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Chars.CharArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Chars.CharArrayAsList) {
                Chars.CharArrayAsList chars_chararrayaslist = (Chars.CharArrayAsList) object;
                int i = this.size();

                if (chars_chararrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != chars_chararrayaslist.array[chars_chararrayaslist.start + j]) {
                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return super.equals(object);
            }
        }

        public int hashCode() {
            int i = 1;

            for (int j = this.start; j < this.end; ++j) {
                i = 31 * i + Chars.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 3);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        char[] toCharArray() {
            int i = this.size();
            char[] achar = new char[i];

            System.arraycopy(this.array, this.start, achar, 0, i);
            return achar;
        }
    }

    private static enum LexicographicalComparator implements Comparator<char[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(char[] achar, char[] achar1) {
            int i = Math.min(achar.length, achar1.length);

            for (int j = 0; j < i; ++j) {
                int k = Chars.compare(achar[j], achar1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return achar.length - achar1.length;
        }
    }
}
