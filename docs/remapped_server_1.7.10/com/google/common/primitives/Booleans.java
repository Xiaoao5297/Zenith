package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Booleans {

    private Booleans() {}

    public static int hashCode(boolean flag) {
        return flag ? 1231 : 1237;
    }

    public static int compare(boolean flag, boolean flag1) {
        return flag == flag1 ? 0 : (flag ? 1 : -1);
    }

    public static boolean contains(boolean[] aboolean, boolean flag) {
        boolean[] aboolean1 = aboolean;
        int i = aboolean.length;

        for (int j = 0; j < i; ++j) {
            boolean flag1 = aboolean1[j];

            if (flag1 == flag) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(boolean[] aboolean, boolean flag) {
        return indexOf(aboolean, flag, 0, aboolean.length);
    }

    private static int indexOf(boolean[] aboolean, boolean flag, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (aboolean[k] == flag) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(boolean[] aboolean, boolean[] aboolean1) {
        Preconditions.checkNotNull(aboolean, "array");
        Preconditions.checkNotNull(aboolean1, "target");
        if (aboolean1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < aboolean.length - aboolean1.length + 1) {
                for (int j = 0; j < aboolean1.length; ++j) {
                    if (aboolean[i + j] != aboolean1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(boolean[] aboolean, boolean flag) {
        return lastIndexOf(aboolean, flag, 0, aboolean.length);
    }

    private static int lastIndexOf(boolean[] aboolean, boolean flag, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (aboolean[k] == flag) {
                return k;
            }
        }

        return -1;
    }

    public static boolean[] concat(boolean[]... aboolean) {
        int i = 0;
        boolean[][] aboolean1 = aboolean;
        int j = aboolean.length;

        for (int k = 0; k < j; ++k) {
            boolean[] aboolean2 = aboolean1[k];

            i += aboolean2.length;
        }

        boolean[] aboolean3 = new boolean[i];

        j = 0;
        boolean[][] aboolean4 = aboolean;
        int l = aboolean.length;

        for (int i1 = 0; i1 < l; ++i1) {
            boolean[] aboolean5 = aboolean4[i1];

            System.arraycopy(aboolean5, 0, aboolean3, j, aboolean5.length);
            j += aboolean5.length;
        }

        return aboolean3;
    }

    public static boolean[] ensureCapacity(boolean[] aboolean, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return aboolean.length < i ? copyOf(aboolean, i + j) : aboolean;
    }

    private static boolean[] copyOf(boolean[] aboolean, int i) {
        boolean[] aboolean1 = new boolean[i];

        System.arraycopy(aboolean, 0, aboolean1, 0, Math.min(aboolean.length, i));
        return aboolean1;
    }

    public static String join(String s, boolean... aboolean) {
        Preconditions.checkNotNull(s);
        if (aboolean.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(aboolean.length * 7);

            stringbuilder.append(aboolean[0]);

            for (int i = 1; i < aboolean.length; ++i) {
                stringbuilder.append(s).append(aboolean[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<boolean[]> lexicographicalComparator() {
        return Booleans.LexicographicalComparator.INSTANCE;
    }

    public static boolean[] toArray(Collection<Boolean> collection) {
        if (collection instanceof Booleans.BooleanArrayAsList) {
            return ((Booleans.BooleanArrayAsList) collection).toBooleanArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            boolean[] aboolean = new boolean[i];

            for (int j = 0; j < i; ++j) {
                aboolean[j] = (Boolean) Preconditions.checkNotNull(aobject[j]);
            }

            return aboolean;
        }
    }

    public static List<Boolean> asList(boolean... aboolean) {
        return (List) (aboolean.length == 0 ? Collections.emptyList() : new Booleans.BooleanArrayAsList(aboolean));
    }

    @Beta
    public static int countTrue(boolean... aboolean) {
        int i = 0;
        boolean[] aboolean1 = aboolean;
        int j = aboolean.length;

        for (int k = 0; k < j; ++k) {
            boolean flag = aboolean1[k];

            if (flag) {
                ++i;
            }
        }

        return i;
    }

    @GwtCompatible
    private static class BooleanArrayAsList extends AbstractList<Boolean> implements RandomAccess, Serializable {

        final boolean[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        BooleanArrayAsList(boolean[] aboolean) {
            this(aboolean, 0, aboolean.length);
        }

        BooleanArrayAsList(boolean[] aboolean, int i, int j) {
            this.array = aboolean;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Boolean get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Boolean && Booleans.indexOf(this.array, (Boolean) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Boolean) {
                int i = Booleans.indexOf(this.array, (Boolean) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Boolean) {
                int i = Booleans.lastIndexOf(this.array, (Boolean) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Boolean set(int i, Boolean obool) {
            Preconditions.checkElementIndex(i, this.size());
            boolean flag = this.array[this.start + i];

            this.array[this.start + i] = (Boolean) Preconditions.checkNotNull(obool);
            return flag;
        }

        public List<Boolean> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Booleans.BooleanArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Booleans.BooleanArrayAsList) {
                Booleans.BooleanArrayAsList booleans_booleanarrayaslist = (Booleans.BooleanArrayAsList) object;
                int i = this.size();

                if (booleans_booleanarrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != booleans_booleanarrayaslist.array[booleans_booleanarrayaslist.start + j]) {
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
                i = 31 * i + Booleans.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 7);

            stringbuilder.append(this.array[this.start] ? "[true" : "[false");

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(this.array[i] ? ", true" : ", false");
            }

            return stringbuilder.append(']').toString();
        }

        boolean[] toBooleanArray() {
            int i = this.size();
            boolean[] aboolean = new boolean[i];

            System.arraycopy(this.array, this.start, aboolean, 0, i);
            return aboolean;
        }
    }

    private static enum LexicographicalComparator implements Comparator<boolean[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(boolean[] aboolean, boolean[] aboolean1) {
            int i = Math.min(aboolean.length, aboolean1.length);

            for (int j = 0; j < i; ++j) {
                int k = Booleans.compare(aboolean[j], aboolean1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return aboolean.length - aboolean1.length;
        }
    }
}
