package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Longs {

    public static final int BYTES = 8;
    public static final long MAX_POWER_OF_TWO = 4611686018427387904L;

    private Longs() {}

    public static int hashCode(long i) {
        return (int) (i ^ i >>> 32);
    }

    public static int compare(long i, long j) {
        return i < j ? -1 : (i > j ? 1 : 0);
    }

    public static boolean contains(long[] along, long i) {
        long[] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long l = along1[k];

            if (l == i) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(long[] along, long i) {
        return indexOf(along, i, 0, along.length);
    }

    private static int indexOf(long[] along, long i, int j, int k) {
        for (int l = j; l < k; ++l) {
            if (along[l] == i) {
                return l;
            }
        }

        return -1;
    }

    public static int indexOf(long[] along, long[] along1) {
        Preconditions.checkNotNull(along, "array");
        Preconditions.checkNotNull(along1, "target");
        if (along1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < along.length - along1.length + 1) {
                for (int j = 0; j < along1.length; ++j) {
                    if (along[i + j] != along1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(long[] along, long i) {
        return lastIndexOf(along, i, 0, along.length);
    }

    private static int lastIndexOf(long[] along, long i, int j, int k) {
        for (int l = k - 1; l >= j; --l) {
            if (along[l] == i) {
                return l;
            }
        }

        return -1;
    }

    public static long min(long... along) {
        Preconditions.checkArgument(along.length > 0);
        long i = along[0];

        for (int j = 1; j < along.length; ++j) {
            if (along[j] < i) {
                i = along[j];
            }
        }

        return i;
    }

    public static long max(long... along) {
        Preconditions.checkArgument(along.length > 0);
        long i = along[0];

        for (int j = 1; j < along.length; ++j) {
            if (along[j] > i) {
                i = along[j];
            }
        }

        return i;
    }

    public static long[] concat(long[]... along) {
        int i = 0;
        long[][] along1 = along;
        int j = along.length;

        for (int k = 0; k < j; ++k) {
            long[] along2 = along1[k];

            i += along2.length;
        }

        long[] along3 = new long[i];

        j = 0;
        long[][] along4 = along;
        int l = along.length;

        for (int i1 = 0; i1 < l; ++i1) {
            long[] along5 = along4[i1];

            System.arraycopy(along5, 0, along3, j, along5.length);
            j += along5.length;
        }

        return along3;
    }

    public static byte[] toByteArray(long i) {
        byte[] abyte = new byte[8];

        for (int j = 7; j >= 0; --j) {
            abyte[j] = (byte) ((int) (i & 255L));
            i >>= 8;
        }

        return abyte;
    }

    public static long fromByteArray(byte[] abyte) {
        Preconditions.checkArgument(abyte.length >= 8, "array too small: %s < %s", abyte.length, 8);
        return fromBytes(abyte[0], abyte[1], abyte[2], abyte[3], abyte[4], abyte[5], abyte[6], abyte[7]);
    }

    public static long fromBytes(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
        return ((long) b0 & 255L) << 56 | ((long) b1 & 255L) << 48 | ((long) b2 & 255L) << 40 | ((long) b3 & 255L) << 32 | ((long) b4 & 255L) << 24 | ((long) b5 & 255L) << 16 | ((long) b6 & 255L) << 8 | (long) b7 & 255L;
    }

    @Beta
    public static Long tryParse(String s) {
        if (((String) Preconditions.checkNotNull(s)).isEmpty()) {
            return null;
        } else {
            boolean flag = s.charAt(0) == '-';
            int i = flag ? 1 : 0;

            if (i == s.length()) {
                return null;
            } else {
                int j = s.charAt(i++) - 48;

                if (j >= 0 && j <= 9) {
                    long k;

                    for (k = (long) (-j); i < s.length(); k -= (long) j) {
                        j = s.charAt(i++) - 48;
                        if (j < 0 || j > 9 || k < -922337203685477580L) {
                            return null;
                        }

                        k *= 10L;
                        if (k < Long.MIN_VALUE + (long) j) {
                            return null;
                        }
                    }

                    if (flag) {
                        return k;
                    } else if (k == Long.MIN_VALUE) {
                        return null;
                    } else {
                        return -k;
                    }
                } else {
                    return null;
                }
            }
        }
    }

    @Beta
    public static Converter<String, Long> stringConverter() {
        return Longs.LongConverter.INSTANCE;
    }

    public static long[] ensureCapacity(long[] along, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return along.length < i ? copyOf(along, i + j) : along;
    }

    private static long[] copyOf(long[] along, int i) {
        long[] along1 = new long[i];

        System.arraycopy(along, 0, along1, 0, Math.min(along.length, i));
        return along1;
    }

    public static String join(String s, long... along) {
        Preconditions.checkNotNull(s);
        if (along.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(along.length * 10);

            stringbuilder.append(along[0]);

            for (int i = 1; i < along.length; ++i) {
                stringbuilder.append(s).append(along[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<long[]> lexicographicalComparator() {
        return Longs.LexicographicalComparator.INSTANCE;
    }

    public static long[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Longs.LongArrayAsList) {
            return ((Longs.LongArrayAsList) collection).toLongArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            long[] along = new long[i];

            for (int j = 0; j < i; ++j) {
                along[j] = ((Number) Preconditions.checkNotNull(aobject[j])).longValue();
            }

            return along;
        }
    }

    public static List<Long> asList(long... along) {
        return (List) (along.length == 0 ? Collections.emptyList() : new Longs.LongArrayAsList(along));
    }

    @GwtCompatible
    private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable {

        final long[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        LongArrayAsList(long[] along) {
            this(along, 0, along.length);
        }

        LongArrayAsList(long[] along, int i, int j) {
            this.array = along;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Long get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Long && Longs.indexOf(this.array, (Long) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Long) {
                int i = Longs.indexOf(this.array, (Long) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Long) {
                int i = Longs.lastIndexOf(this.array, (Long) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Long set(int i, Long olong) {
            Preconditions.checkElementIndex(i, this.size());
            long j = this.array[this.start + i];

            this.array[this.start + i] = (Long) Preconditions.checkNotNull(olong);
            return j;
        }

        public List<Long> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Longs.LongArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Longs.LongArrayAsList) {
                Longs.LongArrayAsList longs_longarrayaslist = (Longs.LongArrayAsList) object;
                int i = this.size();

                if (longs_longarrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != longs_longarrayaslist.array[longs_longarrayaslist.start + j]) {
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
                i = 31 * i + Longs.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 10);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        long[] toLongArray() {
            int i = this.size();
            long[] along = new long[i];

            System.arraycopy(this.array, this.start, along, 0, i);
            return along;
        }
    }

    private static enum LexicographicalComparator implements Comparator<long[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(long[] along, long[] along1) {
            int i = Math.min(along.length, along1.length);

            for (int j = 0; j < i; ++j) {
                int k = Longs.compare(along[j], along1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return along.length - along1.length;
        }
    }

    private static final class LongConverter extends Converter<String, Long> implements Serializable {

        static final Longs.LongConverter INSTANCE = new Longs.LongConverter();
        private static final long serialVersionUID = 1L;

        private LongConverter() {}

        protected Long doForward(String s) {
            return Long.decode(s);
        }

        protected String doBackward(Long olong) {
            return olong.toString();
        }

        public String toString() {
            return "Longs.stringConverter()";
        }

        private Object readResolve() {
            return Longs.LongConverter.INSTANCE;
        }
    }
}
