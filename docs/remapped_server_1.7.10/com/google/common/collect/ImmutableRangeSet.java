package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@Beta
public final class ImmutableRangeSet<C extends Comparable> extends AbstractRangeSet<C> implements Serializable {

    private static final ImmutableRangeSet<Comparable<?>> EMPTY = new ImmutableRangeSet<>(ImmutableList.of());
    private static final ImmutableRangeSet<Comparable<?>> ALL = new ImmutableRangeSet<>(ImmutableList.of(Range.all()));
    private final transient ImmutableList<Range<C>> ranges;
    private transient ImmutableRangeSet<C> complement;

    public static <C extends Comparable> ImmutableRangeSet<C> of() {
        return ImmutableRangeSet.EMPTY;
    }

    static <C extends Comparable> ImmutableRangeSet<C> all() {
        return ImmutableRangeSet.ALL;
    }

    public static <C extends Comparable> ImmutableRangeSet<C> of(Range<C> range) {
        Preconditions.checkNotNull(range);
        return range.isEmpty() ? of() : (range.equals(Range.all()) ? all() : new ImmutableRangeSet<>(ImmutableList.of(range)));
    }

    public static <C extends Comparable> ImmutableRangeSet<C> copyOf(RangeSet<C> rangeset) {
        Preconditions.checkNotNull(rangeset);
        if (rangeset.isEmpty()) {
            return of();
        } else if (rangeset.encloses(Range.all())) {
            return all();
        } else {
            if (rangeset instanceof ImmutableRangeSet) {
                ImmutableRangeSet immutablerangeset = (ImmutableRangeSet) rangeset;

                if (!immutablerangeset.isPartialView()) {
                    return immutablerangeset;
                }
            }

            return new ImmutableRangeSet<>(ImmutableList.copyOf((Collection) rangeset.asRanges()));
        }
    }

    ImmutableRangeSet(ImmutableList<Range<C>> immutablelist) {
        this.ranges = immutablelist;
    }

    private ImmutableRangeSet(ImmutableList<Range<C>> immutablelist, ImmutableRangeSet<C> immutablerangeset) {
        this.ranges = immutablelist;
        this.complement = immutablerangeset;
    }

    @Override
    public boolean encloses(Range<C> range) {
        int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), range.lowerBound, Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);

        return i != -1 && ((Range) this.ranges.get(i)).encloses(range);
    }

    @Override
    public Range<C> rangeContaining(C c0) {
        int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(c0), Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);

        if (i != -1) {
            Range range = (Range) this.ranges.get(i);

            return range.contains(c0) ? range : null;
        } else {
            return null;
        }
    }

    @Override
    public Range<C> span() {
        if (this.ranges.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return Range.create(((Range) this.ranges.get(0)).lowerBound, ((Range) this.ranges.get(this.ranges.size() - 1)).upperBound);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.ranges.isEmpty();
    }

    @Override
    public void add(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAll(RangeSet<C> rangeset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Range<C> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(RangeSet<C> rangeset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableSet<Range<C>> asRanges() {
        return (ImmutableSet) (this.ranges.isEmpty() ? ImmutableSet.of() : new RegularImmutableSortedSet<>(this.ranges, Range.RANGE_LEX_ORDERING));
    }

    @Override
    public ImmutableRangeSet<C> complement() {
        ImmutableRangeSet immutablerangeset = this.complement;

        if (immutablerangeset != null) {
            return immutablerangeset;
        } else if (this.ranges.isEmpty()) {
            return this.complement = all();
        } else if (this.ranges.size() == 1 && ((Range) this.ranges.get(0)).equals(Range.all())) {
            return this.complement = of();
        } else {
            ImmutableRangeSet.ComplementRanges immutablerangeset_complementranges = new ImmutableRangeSet.ComplementRanges();

            immutablerangeset = this.complement = new ImmutableRangeSet<>(immutablerangeset_complementranges, this);
            return immutablerangeset;
        }
    }

    private ImmutableList<Range<C>> intersectRanges(final Range<C> range) {
        if (!this.ranges.isEmpty() && !range.isEmpty()) {
            if (range.encloses(this.span())) {
                return this.ranges;
            } else {
                final int i;

                if (range.hasLowerBound()) {
                    i = SortedLists.binarySearch(this.ranges, Range.upperBoundFn(), (Comparable) range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
                } else {
                    i = 0;
                }

                int j;

                if (range.hasUpperBound()) {
                    j = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), (Comparable) range.upperBound, SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
                } else {
                    j = this.ranges.size();
                }

                final int k = j - i;

                return k == 0 ? ImmutableList.of() : new ImmutableList<Range<C>>() {
                    public int size() {
                        return k;
                    }

                    public Range<C> get(int l) {
                        Preconditions.checkElementIndex(l, k);
                        return l != 0 && l != k - 1 ? (Range) ImmutableRangeSet.this.ranges.get(l + i) : ((Range) ImmutableRangeSet.this.ranges.get(l + i)).intersection(range);
                    }

                    @Override
                    boolean isPartialView() {
                        return true;
                    }
                };
            }
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public ImmutableRangeSet<C> subRangeSet(Range<C> range) {
        if (!this.isEmpty()) {
            Range range1 = this.span();

            if (range.encloses(range1)) {
                return this;
            }

            if (range.isConnected(range1)) {
                return new ImmutableRangeSet<>(this.intersectRanges(range));
            }
        }

        return of();
    }

    public ImmutableSortedSet<C> asSet(DiscreteDomain<C> discretedomain) {
        Preconditions.checkNotNull(discretedomain);
        if (this.isEmpty()) {
            return ImmutableSortedSet.of();
        } else {
            Range range = this.span().canonical(discretedomain);

            if (!range.hasLowerBound()) {
                throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded below");
            } else {
                if (!range.hasUpperBound()) {
                    try {
                        discretedomain.maxValue();
                    } catch (NoSuchElementException nosuchelementexception) {
                        throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded above");
                    }
                }

                return new ImmutableRangeSet.AsSet(discretedomain);
            }
        }
    }

    boolean isPartialView() {
        return this.ranges.isPartialView();
    }

    public static <C extends Comparable<?>> ImmutableRangeSet.Builder<C> builder() {
        return new ImmutableRangeSet.Builder<>();
    }

    Object writeReplace() {
        return new ImmutableRangeSet.SerializedForm<>(this.ranges);
    }

    private static final class SerializedForm<C extends Comparable> implements Serializable {

        private final ImmutableList<Range<C>> ranges;

        SerializedForm(ImmutableList<Range<C>> immutablelist) {
            this.ranges = immutablelist;
        }

        Object readResolve() {
            return this.ranges.isEmpty() ? ImmutableRangeSet.of() : (this.ranges.equals(ImmutableList.of(Range.all())) ? ImmutableRangeSet.all() : new ImmutableRangeSet<>(this.ranges));
        }
    }

    public static class Builder<C extends Comparable<?>> {

        private final RangeSet<C> rangeSet = TreeRangeSet.create();

        public Builder() {}

        public ImmutableRangeSet.Builder<C> add(Range<C> range) {
            if (range.isEmpty()) {
                throw new IllegalArgumentException("range must not be empty, but was " + range);
            } else if (this.rangeSet.complement().encloses(range)) {
                this.rangeSet.add(range);
                return this;
            } else {
                Iterator iterator = this.rangeSet.asRanges().iterator();

                while (iterator.hasNext()) {
                    Range range1 = (Range) iterator.next();

                    Preconditions.checkArgument(!range1.isConnected(range) || range1.intersection(range).isEmpty(), "Ranges may not overlap, but received %s and %s", range1, range);
                }

                throw new AssertionError("should have thrown an IAE above");
            }
        }

        public ImmutableRangeSet.Builder<C> addAll(RangeSet<C> rangeset) {
            Iterator iterator = rangeset.asRanges().iterator();

            while (iterator.hasNext()) {
                Range range = (Range) iterator.next();

                this.add(range);
            }

            return this;
        }

        public ImmutableRangeSet<C> build() {
            return ImmutableRangeSet.copyOf(this.rangeSet);
        }
    }

    private static class AsSetSerializedForm<C extends Comparable> implements Serializable {

        private final ImmutableList<Range<C>> ranges;
        private final DiscreteDomain<C> domain;

        AsSetSerializedForm(ImmutableList<Range<C>> immutablelist, DiscreteDomain<C> discretedomain) {
            this.ranges = immutablelist;
            this.domain = discretedomain;
        }

        Object readResolve() {
            return (new ImmutableRangeSet<>(this.ranges)).asSet(this.domain);
        }
    }

    private final class AsSet extends ImmutableSortedSet<C> {

        private final DiscreteDomain<C> domain;
        private transient Integer size;

        AsSet(DiscreteDomain<C> discretedomain) {
            super(Ordering.natural());
            this.domain = discretedomain;
        }

        public int size() {
            Integer integer = this.size;

            if (integer == null) {
                long i = 0L;
                Iterator iterator = ImmutableRangeSet.this.ranges.iterator();

                while (iterator.hasNext()) {
                    Range range = (Range) iterator.next();

                    i += (long) ContiguousSet.create(range, this.domain).size();
                    if (i >= 2147483647L) {
                        break;
                    }
                }

                integer = this.size = Ints.saturatedCast(i);
            }

            return integer;
        }

        @Override
        public UnmodifiableIterator<C> iterator() {
            return new AbstractIterator<C>() {
                final Iterator<Range<C>> rangeItr;
                Iterator<C> elemItr;

                {
                    this.rangeItr = ImmutableRangeSet.this.ranges.iterator();
                    this.elemItr = Iterators.emptyIterator();
                }

                @Override
                protected C computeNext() {
                    while (true) {
                        if (!this.elemItr.hasNext()) {
                            if (this.rangeItr.hasNext()) {
                                this.elemItr = ContiguousSet.create((Range) this.rangeItr.next(), AsSet.this.domain).iterator();
                                continue;
                            }

                            return (Comparable) this.endOfData();
                        }

                        return (Comparable) this.elemItr.next();
                    }
                }
            };
        }

        @GwtIncompatible("NavigableSet")
        @Override
        public UnmodifiableIterator<C> descendingIterator() {
            return new AbstractIterator<C>() {
                final Iterator<Range<C>> rangeItr;
                Iterator<C> elemItr;

                {
                    this.rangeItr = ImmutableRangeSet.this.ranges.reverse().iterator();
                    this.elemItr = Iterators.emptyIterator();
                }

                @Override
                protected C computeNext() {
                    while (true) {
                        if (!this.elemItr.hasNext()) {
                            if (this.rangeItr.hasNext()) {
                                this.elemItr = ContiguousSet.create((Range) this.rangeItr.next(), AsSet.this.domain).descendingIterator();
                                continue;
                            }

                            return (Comparable) this.endOfData();
                        }

                        return (Comparable) this.elemItr.next();
                    }
                }
            };
        }

        ImmutableSortedSet<C> subSet(Range<C> range) {
            return ImmutableRangeSet.this.subRangeSet(range).asSet(this.domain);
        }

        ImmutableSortedSet<C> headSetImpl(C c0, boolean flag) {
            return this.subSet(Range.upTo(c0, BoundType.forBoolean(flag)));
        }

        ImmutableSortedSet<C> subSetImpl(C c0, boolean flag, C c1, boolean flag1) {
            return !flag && !flag1 && Range.compareOrThrow(c0, c1) == 0 ? ImmutableSortedSet.of() : this.subSet(Range.range(c0, BoundType.forBoolean(flag), c1, BoundType.forBoolean(flag1)));
        }

        ImmutableSortedSet<C> tailSetImpl(C c0, boolean flag) {
            return this.subSet(Range.downTo(c0, BoundType.forBoolean(flag)));
        }

        @Override
        public boolean contains(@Nullable Object object) {
            if (object == null) {
                return false;
            } else {
                try {
                    Comparable comparable = (Comparable) object;

                    return ImmutableRangeSet.this.contains(comparable);
                } catch (ClassCastException classcastexception) {
                    return false;
                }
            }
        }

        @Override
        int indexOf(Object object) {
            if (this.contains(object)) {
                Comparable comparable = (Comparable) object;
                long i = 0L;

                Range range;

                for (Iterator iterator = ImmutableRangeSet.this.ranges.iterator(); iterator.hasNext(); i += (long) ContiguousSet.create(range, this.domain).size()) {
                    range = (Range) iterator.next();
                    if (range.contains(comparable)) {
                        return Ints.saturatedCast(i + (long) ContiguousSet.create(range, this.domain).indexOf(comparable));
                    }
                }

                throw new AssertionError("impossible");
            } else {
                return -1;
            }
        }

        @Override
        boolean isPartialView() {
            return ImmutableRangeSet.this.ranges.isPartialView();
        }

        public String toString() {
            return ImmutableRangeSet.this.ranges.toString();
        }

        @Override
        Object writeReplace() {
            return new ImmutableRangeSet.AsSetSerializedForm<>(ImmutableRangeSet.this.ranges, this.domain);
        }
    }

    private final class ComplementRanges extends ImmutableList<Range<C>> {

        private final boolean positiveBoundedBelow;
        private final boolean positiveBoundedAbove;
        private final int size;

        ComplementRanges() {
            this.positiveBoundedBelow = ((Range) ImmutableRangeSet.this.ranges.get(0)).hasLowerBound();
            this.positiveBoundedAbove = ((Range) Iterables.getLast(ImmutableRangeSet.this.ranges)).hasUpperBound();
            int i = ImmutableRangeSet.this.ranges.size() - 1;

            if (this.positiveBoundedBelow) {
                ++i;
            }

            if (this.positiveBoundedAbove) {
                ++i;
            }

            this.size = i;
        }

        public int size() {
            return this.size;
        }

        public Range<C> get(int i) {
            Preconditions.checkElementIndex(i, this.size);
            Cut cut;

            if (this.positiveBoundedBelow) {
                cut = i == 0 ? Cut.belowAll() : ((Range) ImmutableRangeSet.this.ranges.get(i - 1)).upperBound;
            } else {
                cut = ((Range) ImmutableRangeSet.this.ranges.get(i)).upperBound;
            }

            Cut cut1;

            if (this.positiveBoundedAbove && i == this.size - 1) {
                cut1 = Cut.aboveAll();
            } else {
                cut1 = ((Range) ImmutableRangeSet.this.ranges.get(i + (this.positiveBoundedBelow ? 0 : 1))).lowerBound;
            }

            return Range.create(cut, cut1);
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }
}
