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
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Floats {

    public static final int BYTES = 4;

    private Floats() {}

    public static int hashCode(float f) {
        return Float.valueOf(f).hashCode();
    }

    public static int compare(float f, float f1) {
        return Float.compare(f, f1);
    }

    public static boolean isFinite(float f) {
        return Float.NEGATIVE_INFINITY < f & f < Float.POSITIVE_INFINITY;
    }

    public static boolean contains(float[] afloat, float f) {
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f1 = afloat1[j];

            if (f1 == f) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(float[] afloat, float f) {
        return indexOf(afloat, f, 0, afloat.length);
    }

    private static int indexOf(float[] afloat, float f, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (afloat[k] == f) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(float[] afloat, float[] afloat1) {
        Preconditions.checkNotNull(afloat, "array");
        Preconditions.checkNotNull(afloat1, "target");
        if (afloat1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < afloat.length - afloat1.length + 1) {
                for (int j = 0; j < afloat1.length; ++j) {
                    if (afloat[i + j] != afloat1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(float[] afloat, float f) {
        return lastIndexOf(afloat, f, 0, afloat.length);
    }

    private static int lastIndexOf(float[] afloat, float f, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (afloat[k] == f) {
                return k;
            }
        }

        return -1;
    }

    public static float min(float... afloat) {
        Preconditions.checkArgument(afloat.length > 0);
        float f = afloat[0];

        for (int i = 1; i < afloat.length; ++i) {
            f = Math.min(f, afloat[i]);
        }

        return f;
    }

    public static float max(float... afloat) {
        Preconditions.checkArgument(afloat.length > 0);
        float f = afloat[0];

        for (int i = 1; i < afloat.length; ++i) {
            f = Math.max(f, afloat[i]);
        }

        return f;
    }

    public static float[] concat(float[]... afloat) {
        int i = 0;
        float[][] afloat1 = afloat;
        int j = afloat.length;

        for (int k = 0; k < j; ++k) {
            float[] afloat2 = afloat1[k];

            i += afloat2.length;
        }

        float[] afloat3 = new float[i];

        j = 0;
        float[][] afloat4 = afloat;
        int l = afloat.length;

        for (int i1 = 0; i1 < l; ++i1) {
            float[] afloat5 = afloat4[i1];

            System.arraycopy(afloat5, 0, afloat3, j, afloat5.length);
            j += afloat5.length;
        }

        return afloat3;
    }

    @Beta
    public static Converter<String, Float> stringConverter() {
        return Floats.FloatConverter.INSTANCE;
    }

    public static float[] ensureCapacity(float[] afloat, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return afloat.length < i ? copyOf(afloat, i + j) : afloat;
    }

    private static float[] copyOf(float[] afloat, int i) {
        float[] afloat1 = new float[i];

        System.arraycopy(afloat, 0, afloat1, 0, Math.min(afloat.length, i));
        return afloat1;
    }

    public static String join(String s, float... afloat) {
        Preconditions.checkNotNull(s);
        if (afloat.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(afloat.length * 12);

            stringbuilder.append(afloat[0]);

            for (int i = 1; i < afloat.length; ++i) {
                stringbuilder.append(s).append(afloat[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<float[]> lexicographicalComparator() {
        return Floats.LexicographicalComparator.INSTANCE;
    }

    public static float[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Floats.FloatArrayAsList) {
            return ((Floats.FloatArrayAsList) collection).toFloatArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            float[] afloat = new float[i];

            for (int j = 0; j < i; ++j) {
                afloat[j] = ((Number) Preconditions.checkNotNull(aobject[j])).floatValue();
            }

            return afloat;
        }
    }

    public static List<Float> asList(float... afloat) {
        return (List) (afloat.length == 0 ? Collections.emptyList() : new Floats.FloatArrayAsList(afloat));
    }

    @Nullable
    @GwtIncompatible("regular expressions")
    @Beta
    public static Float tryParse(String s) {
        if (Doubles.FLOATING_POINT_PATTERN.matcher(s).matches()) {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException numberformatexception) {
                ;
            }
        }

        return null;
    }

    @GwtCompatible
    private static class FloatArrayAsList extends AbstractList<Float> implements RandomAccess, Serializable {

        final float[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        FloatArrayAsList(float[] afloat) {
            this(afloat, 0, afloat.length);
        }

        FloatArrayAsList(float[] afloat, int i, int j) {
            this.array = afloat;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Float get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Float && Floats.indexOf(this.array, (Float) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Float) {
                int i = Floats.indexOf(this.array, (Float) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Float) {
                int i = Floats.lastIndexOf(this.array, (Float) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Float set(int i, Float ofloat) {
            Preconditions.checkElementIndex(i, this.size());
            float f = this.array[this.start + i];

            this.array[this.start + i] = (Float) Preconditions.checkNotNull(ofloat);
            return f;
        }

        public List<Float> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Floats.FloatArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Floats.FloatArrayAsList) {
                Floats.FloatArrayAsList floats_floatarrayaslist = (Floats.FloatArrayAsList) object;
                int i = this.size();

                if (floats_floatarrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != floats_floatarrayaslist.array[floats_floatarrayaslist.start + j]) {
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
                i = 31 * i + Floats.hashCode(this.array[j]);
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder(this.size() * 12);

            stringbuilder.append('[').append(this.array[this.start]);

            for (int i = this.start + 1; i < this.end; ++i) {
                stringbuilder.append(", ").append(this.array[i]);
            }

            return stringbuilder.append(']').toString();
        }

        float[] toFloatArray() {
            int i = this.size();
            float[] afloat = new float[i];

            System.arraycopy(this.array, this.start, afloat, 0, i);
            return afloat;
        }
    }

    private static enum LexicographicalComparator implements Comparator<float[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(float[] afloat, float[] afloat1) {
            int i = Math.min(afloat.length, afloat1.length);

            for (int j = 0; j < i; ++j) {
                int k = Floats.compare(afloat[j], afloat1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return afloat.length - afloat1.length;
        }
    }

    private static final class FloatConverter extends Converter<String, Float> implements Serializable {

        static final Floats.FloatConverter INSTANCE = new Floats.FloatConverter();
        private static final long serialVersionUID = 1L;

        private FloatConverter() {}

        protected Float doForward(String s) {
            return Float.valueOf(s);
        }

        protected String doBackward(Float ofloat) {
            return ofloat.toString();
        }

        public String toString() {
            return "Floats.stringConverter()";
        }

        private Object readResolve() {
            return Floats.FloatConverter.INSTANCE;
        }
    }
}
