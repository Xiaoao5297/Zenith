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

@GwtCompatible(emulated = true)
public final class Shorts {

    public static final int BYTES = 2;
    public static final short MAX_POWER_OF_TWO = 16384;

    private Shorts() {}

    public static int hashCode(short short0) {
        return short0;
    }

    public static short checkedCast(long i) {
        short short0 = (short) ((int) i);

        if ((long) short0 != i) {
            throw new IllegalArgumentException("Out of range: " + i);
        } else {
            return short0;
        }
    }

    public static short saturatedCast(long i) {
        return i > 32767L ? 32767 : (i < -32768L ? Short.MIN_VALUE : (short) ((int) i));
    }

    public static int compare(short short0, short short1) {
        return short0 - short1;
    }

    public static boolean contains(short[] ashort, short short0) {
        short[] ashort1 = ashort;
        int i = ashort.length;

        for (int j = 0; j < i; ++j) {
            short short1 = ashort1[j];

            if (short1 == short0) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(short[] ashort, short short0) {
        return indexOf(ashort, short0, 0, ashort.length);
    }

    private static int indexOf(short[] ashort, short short0, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (ashort[k] == short0) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(short[] ashort, short[] ashort1) {
        Preconditions.checkNotNull(ashort, "array");
        Preconditions.checkNotNull(ashort1, "target");
        if (ashort1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < ashort.length - ashort1.length + 1) {
                for (int j = 0; j < ashort1.length; ++j) {
                    if (ashort[i + j] != ashort1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(short[] ashort, short short0) {
        return lastIndexOf(ashort, short0, 0, ashort.length);
    }

    private static int lastIndexOf(short[] ashort, short short0, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (ashort[k] == short0) {
                return k;
            }
        }

        return -1;
    }

    public static short min(short... ashort) {
        Preconditions.checkArgument(ashort.length > 0);
        short short0 = ashort[0];

        for (int i = 1; i < ashort.length; ++i) {
            if (ashort[i] < short0) {
                short0 = ashort[i];
            }
        }

        return short0;
    }

    public static short max(short... ashort) {
        Preconditions.checkArgument(ashort.length > 0);
        short short0 = ashort[0];

        for (int i = 1; i < ashort.length; ++i) {
            if (ashort[i] > short0) {
                short0 = ashort[i];
            }
        }

        return short0;
    }

    public static short[] concat(short[]... ashort) {
        int i = 0;
        short[][] ashort1 = ashort;
        int j = ashort.length;

        for (int k = 0; k < j; ++k) {
            short[] ashort2 = ashort1[k];

            i += ashort2.length;
        }

        short[] ashort3 = new short[i];

        j = 0;
        short[][] ashort4 = ashort;
        int l = ashort.length;

        for (int i1 = 0; i1 < l; ++i1) {
            short[] ashort5 = ashort4[i1];

            System.arraycopy(ashort5, 0, ashort3, j, ashort5.length);
            j += ashort5.length;
        }

        return ashort3;
    }

    @GwtIncompatible("doesn't work")
    public static byte[] toByteArray(short short0) {
        return new byte[]{(byte) (short0 >> 8), (byte) short0};
    }

    @GwtIncompatible("doesn't work")
    public static short fromByteArray(byte[] abyte) {
        Preconditions.checkArgument(abyte.length >= 2, "array too small: %s < %s", abyte.length, 2);
        return fromBytes(abyte[0], abyte[1]);
    }

    @GwtIncompatible("doesn't work")
    public static short fromBytes(byte b0, byte b1) {
        return (short) (b0 << 8 | b1 & 255);
    }

    @Beta
    public static Converter<String, Short> stringConverter() {
        return Shorts.ShortConverter.INSTANCE;
    }

    public static short[] ensureCapacity(short[] ashort, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return ashort.length < i ? copyOf(ashort, i + j) : ashort;
    }

    private static short[] copyOf(short[] ashort, int i) {
        short[] ashort1 = new short[i];

        System.arraycopy(ashort, 0, ashort1, 0, Math.min(ashort.length, i));
        return ashort1;
    }

    public static String join(String s, short... ashort) {
        Preconditions.checkNotNull(s);
        if (ashort.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(ashort.length * 6);

            stringbuilder.append(ashort[0]);

            for (int i = 1; i < ashort.length; ++i) {
                stringbuilder.append(s).append(ashort[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<short[]> lexicographicalComparator() {
        return Shorts.LexicographicalComparator.INSTANCE;
    }

    public static short[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Shorts.ShortArrayAsList) {
            return ((Shorts.ShortArrayAsList) collection).toShortArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            short[] ashort = new short[i];

            for (int j = 0; j < i; ++j) {
                ashort[j] = ((Number) Preconditions.checkNotNull(aobject[j])).shortValue();
            }

            return ashort;
        }
    }

    public static List<Short> asList(short... ashort) {
        return (List) (ashort.length == 0 ? Collections.emptyList() : new Shorts.ShortArrayAsList(ashort));
    }

    @GwtCompatible
    private static class ShortArrayAsList extends AbstractList<Short> implements RandomAccess, Serializable {

        final short[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        ShortArrayAsList(short[] ashort) {
            this(ashort, 0, ashort.length);
        }

        ShortArrayAsList(short[] ashort, int i, int j) {
            this.array = ashort;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Short get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Short && Shorts.indexOf(this.array, (Short) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Short) {
                int i = Shorts.indexOf(this.array, (Short) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Short) {
                int i = Shorts.lastIndexOf(this.array, (Short) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Short set(int i, Short oshort) {
            Preconditions.checkElementIndex(i, this.size());
            short short0 = this.array[this.start + i];

            this.array[this.start + i] = (Short) Preconditions.checkNotNull(oshort);
            return short0;
        }

        public List<Short> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Shorts.ShortArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Shorts.ShortArrayAsList) {
                Shorts.ShortArrayAsList shorts_shortarrayaslist = (Shorts.ShortArrayAsList) object;
                int i = this.size();

                if (shorts_shortarrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != shorts_shortarrayaslist.array[shorts_shortarrayaslist.start + j]) {
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
                i = 31 * i + Shorts.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 6);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        short[] toShortArray() {
            int i = this.size();
            short[] ashort = new short[i];

            System.arraycopy(this.array, this.start, ashort, 0, i);
            return ashort;
        }
    }

    private static enum LexicographicalComparator implements Comparator<short[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(short[] ashort, short[] ashort1) {
            int i = Math.min(ashort.length, ashort1.length);

            for (int j = 0; j < i; ++j) {
                int k = Shorts.compare(ashort[j], ashort1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return ashort.length - ashort1.length;
        }
    }

    private static final class ShortConverter extends Converter<String, Short> implements Serializable {

        static final Shorts.ShortConverter INSTANCE = new Shorts.ShortConverter();
        private static final long serialVersionUID = 1L;

        private ShortConverter() {}

        protected Short doForward(String s) {
            return Short.decode(s);
        }

        protected String doBackward(Short oshort) {
            return oshort.toString();
        }

        public String toString() {
            return "Shorts.stringConverter()";
        }

        private Object readResolve() {
            return Shorts.ShortConverter.INSTANCE;
        }
    }
}
