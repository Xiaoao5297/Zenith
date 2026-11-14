package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import javax.annotation.Nullable;

final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {

    private final transient RegularImmutableSortedSet<E> elementSet;
    private final transient int[] counts;
    private final transient long[] cumulativeCounts;
    private final transient int offset;
    private final transient int length;

    RegularImmutableSortedMultiset(RegularImmutableSortedSet<E> regularimmutablesortedset, int[] aint, long[] along, int i, int j) {
        this.elementSet = regularimmutablesortedset;
        this.counts = aint;
        this.cumulativeCounts = along;
        this.offset = i;
        this.length = j;
    }

    @Override
    Multiset.Entry<E> getEntry(int i) {
        return Multisets.immutableEntry(this.elementSet.asList().get(i), this.counts[this.offset + i]);
    }

    @Override
    public Multiset.Entry<E> firstEntry() {
        return this.getEntry(0);
    }

    @Override
    public Multiset.Entry<E> lastEntry() {
        return this.getEntry(this.length - 1);
    }

    @Override
    public int count(@Nullable Object object) {
        int i = this.elementSet.indexOf(object);

        return i == -1 ? 0 : this.counts[i + this.offset];
    }

    public int size() {
        long i = this.cumulativeCounts[this.offset + this.length] - this.cumulativeCounts[this.offset];

        return Ints.saturatedCast(i);
    }

    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.elementSet;
    }

    @Override
    public ImmutableSortedMultiset<E> headMultiset(E e0, BoundType boundtype) {
        return this.getSubMultiset(0, this.elementSet.headIndex(e0, Preconditions.checkNotNull(boundtype) == BoundType.CLOSED));
    }

    @Override
    public ImmutableSortedMultiset<E> tailMultiset(E e0, BoundType boundtype) {
        return this.getSubMultiset(this.elementSet.tailIndex(e0, Preconditions.checkNotNull(boundtype) == BoundType.CLOSED), this.length);
    }

    ImmutableSortedMultiset<E> getSubMultiset(int i, int j) {
        Preconditions.checkPositionIndexes(i, j, this.length);
        if (i == j) {
            return emptyMultiset(this.comparator());
        } else if (i == 0 && j == this.length) {
            return this;
        } else {
            RegularImmutableSortedSet regularimmutablesortedset = (RegularImmutableSortedSet) this.elementSet.getSubSet(i, j);

            return new RegularImmutableSortedMultiset<>(regularimmutablesortedset, this.counts, this.cumulativeCounts, this.offset + i, j - i);
        }
    }

    @Override
    boolean isPartialView() {
        return this.offset > 0 || this.length < this.counts.length;
    }
}
