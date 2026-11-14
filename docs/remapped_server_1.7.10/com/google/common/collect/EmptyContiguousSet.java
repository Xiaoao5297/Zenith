package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C> {

    EmptyContiguousSet(DiscreteDomain<C> discretedomain) {
        super(discretedomain);
    }

    @Override
    public C first() {
        throw new NoSuchElementException();
    }

    @Override
    public C last() {
        throw new NoSuchElementException();
    }

    public int size() {
        return 0;
    }

    @Override
    public ContiguousSet<C> intersection(ContiguousSet<C> contiguousset) {
        return this;
    }

    @Override
    public Range<C> range() {
        throw new NoSuchElementException();
    }

    @Override
    public Range<C> range(BoundType boundtype, BoundType boundtype1) {
        throw new NoSuchElementException();
    }

    @Override
    ContiguousSet<C> headSetImpl(C c0, boolean flag) {
        return this;
    }

    @Override
    ContiguousSet<C> subSetImpl(C c0, boolean flag, C c1, boolean flag1) {
        return this;
    }

    @Override
    ContiguousSet<C> tailSetImpl(C c0, boolean flag) {
        return this;
    }

    @GwtIncompatible("not used by GWT emulation")
    @Override
    int indexOf(Object object) {
        return -1;
    }

    @Override
    public UnmodifiableIterator<C> iterator() {
        return Iterators.emptyIterator();
    }

    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<C> descendingIterator() {
        return Iterators.emptyIterator();
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableList<C> asList() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object instanceof Set) {
            Set set = (Set) object;

            return set.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new EmptyContiguousSet.SerializedForm<>(this.domain);
    }

    @GwtIncompatible("NavigableSet")
    @Override
    ImmutableSortedSet<C> createDescendingSet() {
        return new EmptyImmutableSortedSet<>(Ordering.natural().reverse());
    }

    @GwtIncompatible("serialization")
    private static final class SerializedForm<C extends Comparable> implements Serializable {

        private final DiscreteDomain<C> domain;
        private static final long serialVersionUID = 0L;

        private SerializedForm(DiscreteDomain<C> discretedomain) {
            this.domain = discretedomain;
        }

        private Object readResolve() {
            return new EmptyContiguousSet<>(this.domain);
        }
    }
}
