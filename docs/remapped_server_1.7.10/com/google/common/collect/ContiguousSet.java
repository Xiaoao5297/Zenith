package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;

@Beta
@GwtCompatible(emulated = true)
public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C> {

    final DiscreteDomain<C> domain;

    public static <C extends Comparable> ContiguousSet<C> create(Range<C> range, DiscreteDomain<C> discretedomain) {
        Preconditions.checkNotNull(range);
        Preconditions.checkNotNull(discretedomain);
        Range range1 = range;

        try {
            if (!range.hasLowerBound()) {
                range1 = range1.intersection(Range.atLeast(discretedomain.minValue()));
            }

            if (!range.hasUpperBound()) {
                range1 = range1.intersection(Range.atMost(discretedomain.maxValue()));
            }
        } catch (NoSuchElementException nosuchelementexception) {
            throw new IllegalArgumentException(nosuchelementexception);
        }

        boolean flag = range1.isEmpty() || Range.compareOrThrow(range.lowerBound.leastValueAbove(discretedomain), range.upperBound.greatestValueBelow(discretedomain)) > 0;

        return (ContiguousSet) (flag ? new EmptyContiguousSet<>(discretedomain) : new RegularContiguousSet<>(range1, discretedomain));
    }

    ContiguousSet(DiscreteDomain<C> discretedomain) {
        super(Ordering.natural());
        this.domain = discretedomain;
    }

    public ContiguousSet<C> headSet(C c0) {
        return this.headSetImpl((Comparable) Preconditions.checkNotNull(c0), false);
    }

    @GwtIncompatible("NavigableSet")
    public ContiguousSet<C> headSet(C c0, boolean flag) {
        return this.headSetImpl((Comparable) Preconditions.checkNotNull(c0), flag);
    }

    public ContiguousSet<C> subSet(C c0, C c1) {
        Preconditions.checkNotNull(c0);
        Preconditions.checkNotNull(c1);
        Preconditions.checkArgument(this.comparator().compare(c0, c1) <= 0);
        return this.subSetImpl(c0, true, c1, false);
    }

    @GwtIncompatible("NavigableSet")
    public ContiguousSet<C> subSet(C c0, boolean flag, C c1, boolean flag1) {
        Preconditions.checkNotNull(c0);
        Preconditions.checkNotNull(c1);
        Preconditions.checkArgument(this.comparator().compare(c0, c1) <= 0);
        return this.subSetImpl(c0, flag, c1, flag1);
    }

    public ContiguousSet<C> tailSet(C c0) {
        return this.tailSetImpl((Comparable) Preconditions.checkNotNull(c0), true);
    }

    @GwtIncompatible("NavigableSet")
    public ContiguousSet<C> tailSet(C c0, boolean flag) {
        return this.tailSetImpl((Comparable) Preconditions.checkNotNull(c0), flag);
    }

    abstract ContiguousSet<C> headSetImpl(C c0, boolean flag);

    abstract ContiguousSet<C> subSetImpl(C c0, boolean flag, C c1, boolean flag1);

    abstract ContiguousSet<C> tailSetImpl(C c0, boolean flag);

    public abstract ContiguousSet<C> intersection(ContiguousSet<C> contiguousset);

    public abstract Range<C> range();

    public abstract Range<C> range(BoundType boundtype, BoundType boundtype1);

    public String toString() {
        return this.range().toString();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }
}
