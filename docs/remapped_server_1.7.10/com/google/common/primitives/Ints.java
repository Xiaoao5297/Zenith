package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.CheckForNull;

@GwtCompatible(emulated = true)
public final class Ints {

    public static final int BYTES = 4;
    public static final int MAX_POWER_OF_TWO = 1073741824;

    private Ints() {}

    public static int hashCode(int i) {
        return i;
    }

    public static int checkedCast(long i) {
        int j = (int) i;

        if ((long) j != i) {
            throw new IllegalArgumentException("Out of range: " + i);
        } else {
            return j;
        }
    }

    public static int saturatedCast(long i) {
        return i > 2147483647L ? Integer.MAX_VALUE : (i < -2147483648L ? Integer.MIN_VALUE : (int) i);
    }

    public static int compare(int i, int j) {
        return i < j ? -1 : (i > j ? 1 : 0);
    }

    public static boolean contains(int[] aint, int i) {
        int[] aint1 = aint;
        int j = aint.length;

        for (int k = 0; k < j; ++k) {
            int l = aint1[k];

            if (l == i) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(int[] aint, int i) {
        return indexOf(aint, i, 0, aint.length);
    }

    private static int indexOf(int[] aint, int i, int j, int k) {
        for (int l = j; l < k; ++l) {
            if (aint[l] == i) {
                return l;
            }
        }

        return -1;
    }

    public static int indexOf(int[] aint, int[] aint1) {
        Preconditions.checkNotNull(aint, "array");
        Preconditions.checkNotNull(aint1, "target");
        if (aint1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < aint.length - aint1.length + 1) {
                for (int j = 0; j < aint1.length; ++j) {
                    if (aint[i + j] != aint1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(int[] aint, int i) {
        return lastIndexOf(aint, i, 0, aint.length);
    }

    private static int lastIndexOf(int[] aint, int i, int j, int k) {
        for (int l = k - 1; l >= j; --l) {
            if (aint[l] == i) {
                return l;
            }
        }

        return -1;
    }

    public static int min(int... aint) {
        Preconditions.checkArgument(aint.length > 0);
        int i = aint[0];

        for (int j = 1; j < aint.length; ++j) {
            if (aint[j] < i) {
                i = aint[j];
            }
        }

        return i;
    }

    public static int max(int... aint) {
        Preconditions.checkArgument(aint.length > 0);
        int i = aint[0];

        for (int j = 1; j < aint.length; ++j) {
            if (aint[j] > i) {
                i = aint[j];
            }
        }

        return i;
    }

    public static int[] concat(int[]... aint) {
        int i = 0;
        int[][] aint1 = aint;
        int j = aint.length;

        for (int k = 0; k < j; ++k) {
            int[] aint2 = aint1[k];

            i += aint2.length;
        }

        int[] aint3 = new int[i];

        j = 0;
        int[][] aint4 = aint;
        int l = aint.length;

        for (int i1 = 0; i1 < l; ++i1) {
            int[] aint5 = aint4[i1];

            System.arraycopy(aint5, 0, aint3, j, aint5.length);
            j += aint5.length;
        }

        return aint3;
    }

    @GwtIncompatible("doesn't work")
    public static byte[] toByteArray(int i) {
        return new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i};
    }

    @GwtIncompatible("doesn't work")
    public static int fromByteArray(byte[] abyte) {
        Preconditions.checkArgument(abyte.length >= 4, "array too small: %s < %s", abyte.length, 4);
        return fromBytes(abyte[0], abyte[1], abyte[2], abyte[3]);
    }

    @GwtIncompatible("doesn't work")
    public static int fromBytes(byte b0, byte b1, byte b2, byte b3) {
        return b0 << 24 | (b1 & 255) << 16 | (b2 & 255) << 8 | b3 & 255;
    }

    @Beta
    public static Converter<String, Integer> stringConverter() {
        return Ints.IntConverter.INSTANCE;
    }

    public static int[] ensureCapacity(int[] aint, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return aint.length < i ? copyOf(aint, i + j) : aint;
    }

    private static int[] copyOf(int[] aint, int i) {
        int[] aint1 = new int[i];

        System.arraycopy(aint, 0, aint1, 0, Math.min(aint.length, i));
        return aint1;
    }

    public static String join(String s, int... aint) {
        Preconditions.checkNotNull(s);
        if (aint.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(aint.length * 5);

            stringbuilder.append(aint[0]);

            for (int i = 1; i < aint.length; ++i) {
                stringbuilder.append(s).append(aint[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<int[]> lexicographicalComparator() {
        return Ints.LexicographicalComparator.INSTANCE;
    }

    public static int[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Ints.IntArrayAsList) {
            return ((Ints.IntArrayAsList) collection).toIntArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            int[] aint = new int[i];

            for (int j = 0; j < i; ++j) {
                aint[j] = ((Number) Preconditions.checkNotNull(aobject[j])).intValue();
            }

            return aint;
        }
    }

    public static List<Integer> asList(int... aint) {
        return (List) (aint.length == 0 ? Collections.emptyList() : new Ints.IntArrayAsList(aint));
    }

    @CheckForNull
    @Beta
    @GwtIncompatible("TODO")
    public static Integer tryParse(String s) {
        return AndroidInteger.tryParse(s, 10);
    }

    @GwtCompatible
    private static class IntArrayAsList extends AbstractList<Integer> implements RandomAccess, Serializable {

        final int[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        IntArrayAsList(int[] aint) {
            this(aint, 0, aint.length);
        }

        IntArrayAsList(int[] aint, int i, int j) {
            this.array = aint;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Integer get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Integer && Ints.indexOf(this.array, (Integer) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Integer) {
                int i = Ints.indexOf(this.array, (Integer) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Integer) {
                int i = Ints.lastIndexOf(this.array, (Integer) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Integer set(int i, Integer integer) {
            Preconditions.checkElementIndex(i, this.size());
            int j = this.array[this.start + i];

            this.array[this.start + i] = (Integer) Preconditions.checkNotNull(integer);
            return j;
        }

        public List<Integer> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Ints.IntArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Ints.IntArrayAsList) {
                Ints.IntArrayAsList ints_intarrayaslist = (Ints.IntArrayAsList) object;
                int i = this.size();

                if (ints_intarrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != ints_intarrayaslist.array[ints_intarrayaslist.start + j]) {
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
                i = 31 * i + Ints.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 5);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        int[] toIntArray() {
            int i = this.size();
            int[] aint = new int[i];

            System.arraycopy(this.array, this.start, aint, 0, i);
            return aint;
        }
    }

    private static enum LexicographicalComparator implements Comparator<int[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(int[] aint, int[] aint1) {
            int i = Math.min(aint.length, aint1.length);

            for (int j = 0; j < i; ++j) {
                int k = Ints.compare(aint[j], aint1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return aint.length - aint1.length;
        }
    }

    private static final class IntConverter extends Converter<String, Integer> implements Serializable {

        static final Ints.IntConverter INSTANCE = new Ints.IntConverter();
        private static final long serialVersionUID = 1L;

        private IntConverter() {}

        protected Integer doForward(String s) {
            return Integer.decode(s);
        }

        protected String doBackward(Integer integer) {
            return integer.toString();
        }

        public String toString() {
            return "Ints.stringConverter()";
        }

        private Object readResolve() {
            return Ints.IntConverter.INSTANCE;
        }
    }
}
