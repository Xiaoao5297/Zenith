package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Comparator<T> comparator;
    private final T minimum;
    private final T maximum;
    private transient int hashCode;
    private transient String toString;

    public static <T extends Comparable<T>> Range<T> is(T t0) {
        return between(t0, t0, (Comparator) null);
    }

    public static <T> Range<T> is(T t0, Comparator<T> comparator) {
        return between(t0, t0, comparator);
    }

    public static <T extends Comparable<T>> Range<T> between(T t0, T t1) {
        return between(t0, t1, (Comparator) null);
    }

    public static <T> Range<T> between(T t0, T t1, Comparator<T> comparator) {
        return new Range<>(t0, t1, comparator);
    }

    private Range(T t0, T t1, Comparator<T> comparator) {
        if (t0 != null && t1 != null) {
            if (comparator == null) {
                comparator = Range.ComparableComparator.INSTANCE;
            }

            if (((Comparator) comparator).compare(t0, t1) < 1) {
                this.minimum = t0;
                this.maximum = t1;
            } else {
                this.minimum = t1;
                this.maximum = t0;
            }

            this.comparator = (Comparator) comparator;
        } else {
            throw new IllegalArgumentException("Elements in a range must not be null: element1=" + t0 + ", element2=" + t1);
        }
    }

    public T getMinimum() {
        return this.minimum;
    }

    public T getMaximum() {
        return this.maximum;
    }

    public Comparator<T> getComparator() {
        return this.comparator;
    }

    public boolean isNaturalOrdering() {
        return this.comparator == Range.ComparableComparator.INSTANCE;
    }

    public boolean contains(T t0) {
        return t0 == null ? false : this.comparator.compare(t0, this.minimum) > -1 && this.comparator.compare(t0, this.maximum) < 1;
    }

    public boolean isAfter(T t0) {
        return t0 == null ? false : this.comparator.compare(t0, this.minimum) < 0;
    }

    public boolean isStartedBy(T t0) {
        return t0 == null ? false : this.comparator.compare(t0, this.minimum) == 0;
    }

    public boolean isEndedBy(T t0) {
        return t0 == null ? false : this.comparator.compare(t0, this.maximum) == 0;
    }

    public boolean isBefore(T t0) {
        return t0 == null ? false : this.comparator.compare(t0, this.maximum) > 0;
    }

    public int elementCompareTo(T t0) {
        if (t0 == null) {
            throw new NullPointerException("Element is null");
        } else {
            return this.isAfter(t0) ? -1 : (this.isBefore(t0) ? 1 : 0);
        }
    }

    public boolean containsRange(Range<T> range) {
        return range == null ? false : this.contains(range.minimum) && this.contains(range.maximum);
    }

    public boolean isAfterRange(Range<T> range) {
        return range == null ? false : this.isAfter(range.maximum);
    }

    public boolean isOverlappedBy(Range<T> range) {
        return range == null ? false : range.contains(this.minimum) || range.contains(this.maximum) || this.contains(range.minimum);
    }

    public boolean isBeforeRange(Range<T> range) {
        return range == null ? false : this.isBefore(range.minimum);
    }

    public Range<T> intersectionWith(Range<T> range) {
        if (!this.isOverlappedBy(range)) {
            throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", range));
        } else if (this.equals(range)) {
            return this;
        } else {
            Object object = this.getComparator().compare(this.minimum, range.minimum) < 0 ? range.minimum : this.minimum;
            Object object1 = this.getComparator().compare(this.maximum, range.maximum) < 0 ? this.maximum : range.maximum;

            return between(object, object1, this.getComparator());
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object != null && object.getClass() == this.getClass()) {
            Range range = (Range) object;

            return this.minimum.equals(range.minimum) && this.maximum.equals(range.maximum);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.hashCode;

        if (this.hashCode == 0) {
            byte b0 = 17;

            i = 37 * b0 + this.getClass().hashCode();
            i = 37 * i + this.minimum.hashCode();
            i = 37 * i + this.maximum.hashCode();
            this.hashCode = i;
        }

        return i;
    }

    public String toString() {
        String s = this.toString;

        if (s == null) {
            StringBuilder stringbuilder = new StringBuilder(32);

            stringbuilder.append('[');
            stringbuilder.append(this.minimum);
            stringbuilder.append("..");
            stringbuilder.append(this.maximum);
            stringbuilder.append(']');
            s = stringbuilder.toString();
            this.toString = s;
        }

        return s;
    }

    public String toString(String s) {
        return String.format(s, this.minimum, this.maximum, this.comparator);
    }

    private static enum ComparableComparator implements Comparator {

        INSTANCE;

        private ComparableComparator() {}

        public int compare(Object object, Object object1) {
            return ((Comparable) object).compareTo(object1);
        }
    }
}
