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
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Doubles {

    public static final int BYTES = 8;
    @GwtIncompatible("regular expressions")
    static final Pattern FLOATING_POINT_PATTERN = fpPattern();

    private Doubles() {}

    public static int hashCode(double d0) {
        return Double.valueOf(d0).hashCode();
    }

    public static int compare(double d0, double d1) {
        return Double.compare(d0, d1);
    }

    public static boolean isFinite(double d0) {
        return Double.NEGATIVE_INFINITY < d0 & d0 < Double.POSITIVE_INFINITY;
    }

    public static boolean contains(double[] adouble, double d0) {
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d1 = adouble1[j];

            if (d1 == d0) {
                return true;
            }
        }

        return false;
    }

    public static int indexOf(double[] adouble, double d0) {
        return indexOf(adouble, d0, 0, adouble.length);
    }

    private static int indexOf(double[] adouble, double d0, int i, int j) {
        for (int k = i; k < j; ++k) {
            if (adouble[k] == d0) {
                return k;
            }
        }

        return -1;
    }

    public static int indexOf(double[] adouble, double[] adouble1) {
        Preconditions.checkNotNull(adouble, "array");
        Preconditions.checkNotNull(adouble1, "target");
        if (adouble1.length == 0) {
            return 0;
        } else {
            int i = 0;

            label27:
            while (i < adouble.length - adouble1.length + 1) {
                for (int j = 0; j < adouble1.length; ++j) {
                    if (adouble[i + j] != adouble1[j]) {
                        ++i;
                        continue label27;
                    }
                }

                return i;
            }

            return -1;
        }
    }

    public static int lastIndexOf(double[] adouble, double d0) {
        return lastIndexOf(adouble, d0, 0, adouble.length);
    }

    private static int lastIndexOf(double[] adouble, double d0, int i, int j) {
        for (int k = j - 1; k >= i; --k) {
            if (adouble[k] == d0) {
                return k;
            }
        }

        return -1;
    }

    public static double min(double... adouble) {
        Preconditions.checkArgument(adouble.length > 0);
        double d0 = adouble[0];

        for (int i = 1; i < adouble.length; ++i) {
            d0 = Math.min(d0, adouble[i]);
        }

        return d0;
    }

    public static double max(double... adouble) {
        Preconditions.checkArgument(adouble.length > 0);
        double d0 = adouble[0];

        for (int i = 1; i < adouble.length; ++i) {
            d0 = Math.max(d0, adouble[i]);
        }

        return d0;
    }

    public static double[] concat(double[]... adouble) {
        int i = 0;
        double[][] adouble1 = adouble;
        int j = adouble.length;

        for (int k = 0; k < j; ++k) {
            double[] adouble2 = adouble1[k];

            i += adouble2.length;
        }

        double[] adouble3 = new double[i];

        j = 0;
        double[][] adouble4 = adouble;
        int l = adouble.length;

        for (int i1 = 0; i1 < l; ++i1) {
            double[] adouble5 = adouble4[i1];

            System.arraycopy(adouble5, 0, adouble3, j, adouble5.length);
            j += adouble5.length;
        }

        return adouble3;
    }

    @Beta
    public static Converter<String, Double> stringConverter() {
        return Doubles.DoubleConverter.INSTANCE;
    }

    public static double[] ensureCapacity(double[] adouble, int i, int j) {
        Preconditions.checkArgument(i >= 0, "Invalid minLength: %s", i);
        Preconditions.checkArgument(j >= 0, "Invalid padding: %s", j);
        return adouble.length < i ? copyOf(adouble, i + j) : adouble;
    }

    private static double[] copyOf(double[] adouble, int i) {
        double[] adouble1 = new double[i];

        System.arraycopy(adouble, 0, adouble1, 0, Math.min(adouble.length, i));
        return adouble1;
    }

    public static String join(String s, double... adouble) {
        Preconditions.checkNotNull(s);
        if (adouble.length == 0) {
            return "";
        } else {
            StringBuilder stringbuilder = new StringBuilder(adouble.length * 12);

            stringbuilder.append(adouble[0]);

            for (int i = 1; i < adouble.length; ++i) {
                stringbuilder.append(s).append(adouble[i]);
            }

            return stringbuilder.toString();
        }
    }

    public static Comparator<double[]> lexicographicalComparator() {
        return Doubles.LexicographicalComparator.INSTANCE;
    }

    public static double[] toArray(Collection<? extends Number> collection) {
        if (collection instanceof Doubles.DoubleArrayAsList) {
            return ((Doubles.DoubleArrayAsList) collection).toDoubleArray();
        } else {
            Object[] aobject = collection.toArray();
            int i = aobject.length;
            double[] adouble = new double[i];

            for (int j = 0; j < i; ++j) {
                adouble[j] = ((Number) Preconditions.checkNotNull(aobject[j])).doubleValue();
            }

            return adouble;
        }
    }

    public static List<Double> asList(double... adouble) {
        return (List) (adouble.length == 0 ? Collections.emptyList() : new Doubles.DoubleArrayAsList(adouble));
    }

    @GwtIncompatible("regular expressions")
    private static Pattern fpPattern() {
        String s = "(?:\\d++(?:\\.\\d*+)?|\\.\\d++)";
        String s1 = s + "(?:[eE][+-]?\\d++)?[fFdD]?";
        String s2 = "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)";
        String s3 = "0[xX]" + s2 + "[pP][+-]?\\d++[fFdD]?";
        String s4 = "[+-]?(?:NaN|Infinity|" + s1 + "|" + s3 + ")";

        return Pattern.compile(s4);
    }

    @Nullable
    @GwtIncompatible("regular expressions")
    @Beta
    public static Double tryParse(String s) {
        if (Doubles.FLOATING_POINT_PATTERN.matcher(s).matches()) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException numberformatexception) {
                ;
            }
        }

        return null;
    }

    @GwtCompatible
    private static class DoubleArrayAsList extends AbstractList<Double> implements RandomAccess, Serializable {

        final double[] array;
        final int start;
        final int end;
        private static final long serialVersionUID = 0L;

        DoubleArrayAsList(double[] adouble) {
            this(adouble, 0, adouble.length);
        }

        DoubleArrayAsList(double[] adouble, int i, int j) {
            this.array = adouble;
            this.start = i;
            this.end = j;
        }

        public int size() {
            return this.end - this.start;
        }

        public boolean isEmpty() {
            return false;
        }

        public Double get(int i) {
            Preconditions.checkElementIndex(i, this.size());
            return this.array[this.start + i];
        }

        public boolean contains(Object object) {
            return object instanceof Double && Doubles.indexOf(this.array, (Double) object, this.start, this.end) != -1;
        }

        public int indexOf(Object object) {
            if (object instanceof Double) {
                int i = Doubles.indexOf(this.array, (Double) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public int lastIndexOf(Object object) {
            if (object instanceof Double) {
                int i = Doubles.lastIndexOf(this.array, (Double) object, this.start, this.end);

                if (i >= 0) {
                    return i - this.start;
                }
            }

            return -1;
        }

        public Double set(int i, Double odouble) {
            Preconditions.checkElementIndex(i, this.size());
            double d0 = this.array[this.start + i];

            this.array[this.start + i] = (Double) Preconditions.checkNotNull(odouble);
            return d0;
        }

        public List<Double> subList(int i, int j) {
            int k = this.size();

            Preconditions.checkPositionIndexes(i, j, k);
            return (List) (i == j ? Collections.emptyList() : new Doubles.DoubleArrayAsList(this.array, this.start + i, this.start + j));
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (object instanceof Doubles.DoubleArrayAsList) {
                Doubles.DoubleArrayAsList doubles_doublearrayaslist = (Doubles.DoubleArrayAsList) object;
                int i = this.size();

                if (doubles_doublearrayaslist.size() != i) {
                    return false;
                } else {
                    for (int j = 0; j < i; ++j) {
                        if (this.array[this.start + j] != doubles_doublearrayaslist.array[doubles_doublearrayaslist.start + j]) {
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
                i = 31 * i + Doubles.hashCode(this.array[j]);
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

        double[] toDoubleArray() {
            int i = this.size();
            double[] adouble = new double[i];

            System.arraycopy(this.array, this.start, adouble, 0, i);
            return adouble;
        }
    }

    private static enum LexicographicalComparator implements Comparator<double[]> {

        INSTANCE;

        private LexicographicalComparator() {}

        public int compare(double[] adouble, double[] adouble1) {
            int i = Math.min(adouble.length, adouble1.length);

            for (int j = 0; j < i; ++j) {
                int k = Doubles.compare(adouble[j], adouble1[j]);

                if (k != 0) {
                    return k;
                }
            }

            return adouble.length - adouble1.length;
        }
    }

    private static final class DoubleConverter extends Converter<String, Double> implements Serializable {

        static final Doubles.DoubleConverter INSTANCE = new Doubles.DoubleConverter();
        private static final long serialVersionUID = 1L;

        private DoubleConverter() {}

        protected Double doForward(String s) {
            return Double.valueOf(s);
        }

        protected String doBackward(Double odouble) {
            return odouble.toString();
        }

        public String toString() {
            return "Doubles.stringConverter()";
        }

        private Object readResolve() {
            return Doubles.DoubleConverter.INSTANCE;
        }
    }
}
