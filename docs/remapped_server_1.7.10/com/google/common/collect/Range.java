package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Range<C extends Comparable> implements Predicate<C>, Serializable {

    private static final Function<Range, Cut> LOWER_BOUND_FN = new Function<Range, Cut>() {
        public Cut apply(Range range) {
            return range.lowerBound;
        }
    };
    private static final Function<Range, Cut> UPPER_BOUND_FN = new Function<Range, Cut>() {
        public Cut apply(Range range) {
            return range.upperBound;
        }
    };
    static final Ordering<Range<?>> RANGE_LEX_ORDERING = new Ordering<Range<?>>() {
        public int compare(Range<?> range, Range<?> range1) {
            return ComparisonChain.start().compare(range.lowerBound, range1.lowerBound).compare(range.upperBound, range1.upperBound).result();
        }
    };
    private static final Range<Comparable> ALL = new Range<>(Cut.belowAll(), Cut.aboveAll());
    final Cut<C> lowerBound;
    final Cut<C> upperBound;
    private static final long serialVersionUID = 0L;

    static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn() {
        return Range.LOWER_BOUND_FN;
    }

    static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn() {
        return Range.UPPER_BOUND_FN;
    }

    static <C extends Comparable<?>> Range<C> create(Cut<C> cut, Cut<C> cut1) {
        return new Range<>(cut, cut1);
    }

    public static <C extends Comparable<?>> Range<C> open(C c0, C c1) {
        return create(Cut.aboveValue(c0), Cut.belowValue(c1));
    }

    public static <C extends Comparable<?>> Range<C> closed(C c0, C c1) {
        return create(Cut.belowValue(c0), Cut.aboveValue(c1));
    }

    public static <C extends Comparable<?>> Range<C> closedOpen(C c0, C c1) {
        return create(Cut.belowValue(c0), Cut.belowValue(c1));
    }

    public static <C extends Comparable<?>> Range<C> openClosed(C c0, C c1) {
        return create(Cut.aboveValue(c0), Cut.aboveValue(c1));
    }

    public static <C extends Comparable<?>> Range<C> range(C c0, BoundType boundtype, C c1, BoundType boundtype1) {
        Preconditions.checkNotNull(boundtype);
        Preconditions.checkNotNull(boundtype1);
        Cut cut = boundtype == BoundType.OPEN ? Cut.aboveValue(c0) : Cut.belowValue(c0);
        Cut cut1 = boundtype1 == BoundType.OPEN ? Cut.belowValue(c1) : Cut.aboveValue(c1);

        return create(cut, cut1);
    }

    public static <C extends Comparable<?>> Range<C> lessThan(C c0) {
        return create(Cut.belowAll(), Cut.belowValue(c0));
    }

    public static <C extends Comparable<?>> Range<C> atMost(C c0) {
        return create(Cut.belowAll(), Cut.aboveValue(c0));
    }

    public static <C extends Comparable<?>> Range<C> upTo(C c0, BoundType boundtype) {
        switch (boundtype) {
            case OPEN:
                return lessThan(c0);
            case CLOSED:
                return atMost(c0);
            default:
                throw new AssertionError();
        }
    }

    public static <C extends Comparable<?>> Range<C> greaterThan(C c0) {
        return create(Cut.aboveValue(c0), Cut.aboveAll());
    }

    public static <C extends Comparable<?>> Range<C> atLeast(C c0) {
        return create(Cut.belowValue(c0), Cut.aboveAll());
    }

    public static <C extends Comparable<?>> Range<C> downTo(C c0, BoundType boundtype) {
        switch (boundtype) {
            case OPEN:
                return greaterThan(c0);
            case CLOSED:
                return atLeast(c0);
            default:
                throw new AssertionError();
        }
    }

    public static <C extends Comparable<?>> Range<C> all() {
        return Range.ALL;
    }

    public static <C extends Comparable<?>> Range<C> singleton(C c0) {
        return closed(c0, c0);
    }

    public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> iterable) {
        Preconditions.checkNotNull(iterable);
        if (iterable instanceof ContiguousSet) {
            return ((ContiguousSet) iterable).range();
        } else {
            Iterator iterator = iterable.iterator();
            Comparable comparable = (Comparable) Preconditions.checkNotNull(iterator.next());

            Comparable comparable1;
            Comparable comparable2;

            for (comparable2 = comparable; iterator.hasNext(); comparable2 = (Comparable) Ordering.natural().max(comparable2, comparable1)) {
                comparable1 = (Comparable) Preconditions.checkNotNull(iterator.next());
                comparable = (Comparable) Ordering.natural().min(comparable, comparable1);
            }

            return closed(comparable, comparable2);
        }
    }

    private Range(Cut<C> cut, Cut<C> cut1) {
        if (cut.compareTo(cut1) <= 0 && cut != Cut.aboveAll() && cut1 != Cut.belowAll()) {
            this.lowerBound = (Cut) Preconditions.checkNotNull(cut);
            this.upperBound = (Cut) Preconditions.checkNotNull(cut1);
        } else {
            throw new IllegalArgumentException("Invalid range: " + toString(cut, cut1));
        }
    }

    public boolean hasLowerBound() {
        return this.lowerBound != Cut.belowAll();
    }

    public C lowerEndpoint() {
        return this.lowerBound.endpoint();
    }

    public BoundType lowerBoundType() {
        return this.lowerBound.typeAsLowerBound();
    }

    public boolean hasUpperBound() {
        return this.upperBound != Cut.aboveAll();
    }

    public C upperEndpoint() {
        return this.upperBound.endpoint();
    }

    public BoundType upperBoundType() {
        return this.upperBound.typeAsUpperBound();
    }

    public boolean isEmpty() {
        return this.lowerBound.equals(this.upperBound);
    }

    public boolean contains(C c0) {
        Preconditions.checkNotNull(c0);
        return this.lowerBound.isLessThan(c0) && !this.upperBound.isLessThan(c0);
    }

    /** @deprecated */
    @Deprecated
    public boolean apply(C c0) {
        return this.contains(c0);
    }

    public boolean containsAll(Iterable<? extends C> iterable) {
        if (Iterables.isEmpty(iterable)) {
            return true;
        } else {
            if (iterable instanceof SortedSet) {
                SortedSet sortedset = cast(iterable);
                Comparator comparator = sortedset.comparator();

                if (Ordering.natural().equals(comparator) || comparator == null) {
                    return this.contains((Comparable) sortedset.first()) && this.contains((Comparable) sortedset.last());
                }
            }

            Iterator iterator = iterable.iterator();

            Comparable comparable;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                comparable = (Comparable) iterator.next();
            } while (this.contains(comparable));

            return false;
        }
    }

    public boolean encloses(Range<C> range) {
        return this.lowerBound.compareTo(range.lowerBound) <= 0 && this.upperBound.compareTo(range.upperBound) >= 0;
    }

    public boolean isConnected(Range<C> range) {
        return this.lowerBound.compareTo(range.upperBound) <= 0 && range.lowerBound.compareTo(this.upperBound) <= 0;
    }

    public Range<C> intersection(Range<C> range) {
        int i = this.lowerBound.compareTo(range.lowerBound);
        int j = this.upperBound.compareTo(range.upperBound);

        if (i >= 0 && j <= 0) {
            return this;
        } else if (i <= 0 && j >= 0) {
            return range;
        } else {
            Cut cut = i >= 0 ? this.lowerBound : range.lowerBound;
            Cut cut1 = j <= 0 ? this.upperBound : range.upperBound;

            return create(cut, cut1);
        }
    }

    public Range<C> span(Range<C> range) {
        int i = this.lowerBound.compareTo(range.lowerBound);
        int j = this.upperBound.compareTo(range.upperBound);

        if (i <= 0 && j >= 0) {
            return this;
        } else if (i >= 0 && j <= 0) {
            return range;
        } else {
            Cut cut = i <= 0 ? this.lowerBound : range.lowerBound;
            Cut cut1 = j >= 0 ? this.upperBound : range.upperBound;

            return create(cut, cut1);
        }
    }

    public Range<C> canonical(DiscreteDomain<C> discretedomain) {
        Preconditions.checkNotNull(discretedomain);
        Cut cut = this.lowerBound.canonical(discretedomain);
        Cut cut1 = this.upperBound.canonical(discretedomain);

        return cut == this.lowerBound && cut1 == this.upperBound ? this : create(cut, cut1);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Range)) {
            return false;
        } else {
            Range range = (Range) object;

            return this.lowerBound.equals(range.lowerBound) && this.upperBound.equals(range.upperBound);
        }
    }

    public int hashCode() {
        return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
    }

    public String toString() {
        return toString(this.lowerBound, this.upperBound);
    }

    private static String toString(Cut<?> cut, Cut<?> cut1) {
        StringBuilder stringbuilder = new StringBuilder(16);

        cut.describeAsLowerBound(stringbuilder);
        stringbuilder.append('\u2025');
        cut1.describeAsUpperBound(stringbuilder);
        return stringbuilder.toString();
    }

    private static <T> SortedSet<T> cast(Iterable<T> iterable) {
        return (SortedSet) iterable;
    }

    Object readResolve() {
        return this.equals(Range.ALL) ? all() : this;
    }

    static int compareOrThrow(Comparable comparable, Comparable comparable1) {
        return comparable.compareTo(comparable1);
    }
}
