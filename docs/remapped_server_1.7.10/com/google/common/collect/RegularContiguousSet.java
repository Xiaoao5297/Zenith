package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class RegularContiguousSet<C extends Comparable> extends ContiguousSet<C> {

    private final Range<C> range;
    private static final long serialVersionUID = 0L;

    RegularContiguousSet(Range<C> range, DiscreteDomain<C> discretedomain) {
        super(discretedomain);
        this.range = range;
    }

    private ContiguousSet<C> intersectionInCurrentDomain(Range<C> range) {
        return (ContiguousSet) (this.range.isConnected(range) ? ContiguousSet.create(this.range.intersection(range), this.domain) : new EmptyContiguousSet<>(this.domain));
    }

    @Override
    ContiguousSet<C> headSetImpl(C c0, boolean flag) {
        return this.intersectionInCurrentDomain(Range.upTo(c0, BoundType.forBoolean(flag)));
    }

    @Override
    ContiguousSet<C> subSetImpl(C c0, boolean flag, C c1, boolean flag1) {
        return (ContiguousSet) (c0.compareTo(c1) == 0 && !flag && !flag1 ? new EmptyContiguousSet<>(this.domain) : this.intersectionInCurrentDomain(Range.range(c0, BoundType.forBoolean(flag), c1, BoundType.forBoolean(flag1))));
    }

    @Override
    ContiguousSet<C> tailSetImpl(C c0, boolean flag) {
        return this.intersectionInCurrentDomain(Range.downTo(c0, BoundType.forBoolean(flag)));
    }

    @GwtIncompatible("not used by GWT emulation")
    @Override
    int indexOf(Object object) {
        return this.contains(object) ? (int) this.domain.distance(this.first(), (Comparable) object) : -1;
    }

    @Override
    public UnmodifiableIterator<C> iterator() {
        return new AbstractSequentialIterator<C>(this.first()) {
            final C last = RegularContiguousSet.this.last();

            protected C computeNext(C c0) {
                return RegularContiguousSet.equalsOrThrow(c0, this.last) ? null : RegularContiguousSet.this.domain.next(c0);
            }
        };
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<C> descendingIterator() {
        return new AbstractSequentialIterator<C>(this.last()) {
            final C first = RegularContiguousSet.this.first();

            protected C computeNext(C c0) {
                return RegularContiguousSet.equalsOrThrow(c0, this.first) ? null : RegularContiguousSet.this.domain.previous(c0);
            }
        };
    }

    private static boolean equalsOrThrow(Comparable<?> comparable, @Nullable Comparable<?> comparable1) {
        return comparable1 != null && Range.compareOrThrow(comparable, comparable1) == 0;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public C first() {
        return this.range.lowerBound.leastValueAbove(this.domain);
    }

    @Override
    public C last() {
        return this.range.upperBound.greatestValueBelow(this.domain);
    }

    public int size() {
        long i = this.domain.distance(this.first(), this.last());

        return i >= 2147483647L ? Integer.MAX_VALUE : (int) i + 1;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (object == null) {
            return false;
        } else {
            try {
                return this.range.contains((Comparable) object);
            } catch (ClassCastException classcastexception) {
                return false;
            }
        }
    }

    public boolean containsAll(Collection<?> collection) {
        return Collections2.containsAllImpl(this, collection);
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public ContiguousSet<C> intersection(ContiguousSet<C> contiguousset) {
        Preconditions.checkNotNull(contiguousset);
        Preconditions.checkArgument(this.domain.equals(contiguousset.domain));
        if (contiguousset.isEmpty()) {
            return contiguousset;
        } else {
            Comparable comparable = (Comparable) Ordering.natural().max(this.first(), contiguousset.first());
            Comparable comparable1 = (Comparable) Ordering.natural().min(this.last(), contiguousset.last());

            return (ContiguousSet) (comparable.compareTo(comparable1) < 0 ? ContiguousSet.create(Range.closed(comparable, comparable1), this.domain) : new EmptyContiguousSet<>(this.domain));
        }
    }

    @Override
    public Range<C> range() {
        return this.range(BoundType.CLOSED, BoundType.CLOSED);
    }

    @Override
    public Range<C> range(BoundType boundtype, BoundType boundtype1) {
        return Range.create(this.range.lowerBound.withLowerBoundType(boundtype, this.domain), this.range.upperBound.withUpperBoundType(boundtype1, this.domain));
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else {
            if (object instanceof RegularContiguousSet) {
                RegularContiguousSet regularcontiguousset = (RegularContiguousSet) object;

                if (this.domain.equals(regularcontiguousset.domain)) {
                    return this.first().equals(regularcontiguousset.first()) && this.last().equals(regularcontiguousset.last());
                }
            }

            return super.equals(object);
        }
    }

    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this);
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new RegularContiguousSet.SerializedForm<>(this.range, this.domain);
    }

    @GwtIncompatible("serialization")
    private static final class SerializedForm<C extends Comparable> implements Serializable {

        final Range<C> range;
        final DiscreteDomain<C> domain;

        private SerializedForm(Range<C> range, DiscreteDomain<C> discretedomain) {
            this.range = range;
            this.domain = discretedomain;
        }

        private Object readResolve() {
            return new RegularContiguousSet<>(this.range, this.domain);
        }
    }
}
