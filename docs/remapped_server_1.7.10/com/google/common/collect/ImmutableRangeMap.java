package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("NavigableMap")
public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V> {

    private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY = new ImmutableRangeMap<>(ImmutableList.of(), ImmutableList.of());
    private final ImmutableList<Range<K>> ranges;
    private final ImmutableList<V> values;

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
        return ImmutableRangeMap.EMPTY;
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> range, V v0) {
        return new ImmutableRangeMap<>(ImmutableList.of(range), ImmutableList.of(v0));
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(RangeMap<K, ? extends V> rangemap) {
        if (rangemap instanceof ImmutableRangeMap) {
            return (ImmutableRangeMap) rangemap;
        } else {
            Map map = rangemap.asMapOfRanges();
            ImmutableList.Builder immutablelist_builder = new ImmutableList.Builder<>(map.size());
            ImmutableList.Builder immutablelist_builder1 = new ImmutableList.Builder<>(map.size());
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                immutablelist_builder.add(entry.getKey());
                immutablelist_builder1.add(entry.getValue());
            }

            return new ImmutableRangeMap<>(immutablelist_builder.build(), immutablelist_builder1.build());
        }
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap.Builder<K, V> builder() {
        return new ImmutableRangeMap.Builder<>();
    }

    ImmutableRangeMap(ImmutableList<Range<K>> immutablelist, ImmutableList<V> immutablelist1) {
        this.ranges = immutablelist;
        this.values = immutablelist1;
    }

    @Nullable
    @Override
    public V get(K k0) {
        int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), (Comparable) Cut.belowValue(k0), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);

        if (i == -1) {
            return null;
        } else {
            Range range = (Range) this.ranges.get(i);

            return range.contains(k0) ? this.values.get(i) : null;
        }
    }

    @Nullable
    @Override
    public Entry<Range<K>, V> getEntry(K k0) {
        int i = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), (Comparable) Cut.belowValue(k0), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);

        if (i == -1) {
            return null;
        } else {
            Range range = (Range) this.ranges.get(i);

            return range.contains(k0) ? Maps.immutableEntry(range, this.values.get(i)) : null;
        }
    }

    @Override
    public Range<K> span() {
        if (this.ranges.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            Range range = (Range) this.ranges.get(0);
            Range range1 = (Range) this.ranges.get(this.ranges.size() - 1);

            return Range.create(range.lowerBound, range1.upperBound);
        }
    }

    @Override
    public void put(Range<K> range, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(RangeMap<K, V> rangemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Range<K> range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableMap<Range<K>, V> asMapOfRanges() {
        if (this.ranges.isEmpty()) {
            return ImmutableMap.of();
        } else {
            RegularImmutableSortedSet regularimmutablesortedset = new RegularImmutableSortedSet<>(this.ranges, Range.RANGE_LEX_ORDERING);

            return new RegularImmutableSortedMap<>(regularimmutablesortedset, this.values);
        }
    }

    @Override
    public ImmutableRangeMap<K, V> subRangeMap(final Range<K> range) {
        if (((Range) Preconditions.checkNotNull(range)).isEmpty()) {
            return of();
        } else if (!this.ranges.isEmpty() && !range.encloses(this.span())) {
            final int i = SortedLists.binarySearch(this.ranges, Range.upperBoundFn(), (Comparable) range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
            int j = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), (Comparable) range.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);

            if (i >= j) {
                return of();
            } else {
                final int k = j - i;
                ImmutableList immutablelist = new ImmutableList<Range<K>>() {
                    public int size() {
                        return k;
                    }

                    public Range<K> get(int l) {
                        Preconditions.checkElementIndex(l, k);
                        return l != 0 && l != k - 1 ? (Range) ImmutableRangeMap.this.ranges.get(l + i) : ((Range) ImmutableRangeMap.this.ranges.get(l + i)).intersection(range);
                    }

                    @Override
                    boolean isPartialView() {
                        return true;
                    }
                };

                return new ImmutableRangeMap<K, V>(immutablelist, this.values.subList(i, j)) {
                    @Override
                    public ImmutableRangeMap<K, V> subRangeMap(Range<K> range1) {
                        return range.isConnected(range1) ? ImmutableRangeMap.this.subRangeMap(range1.intersection(range)) : ImmutableRangeMap.of();
                    }
                };
            }
        } else {
            return this;
        }
    }

    @Override
    public int hashCode() {
        return this.asMapOfRanges().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object instanceof RangeMap) {
            RangeMap rangemap = (RangeMap) object;

            return this.asMapOfRanges().equals(rangemap.asMapOfRanges());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.asMapOfRanges().toString();
    }

    public static final class Builder<K extends Comparable<?>, V> {

        private final RangeSet<K> keyRanges = TreeRangeSet.create();
        private final RangeMap<K, V> rangeMap = TreeRangeMap.create();

        public Builder() {}

        public ImmutableRangeMap.Builder<K, V> put(Range<K> range, V v0) {
            Preconditions.checkNotNull(range);
            Preconditions.checkNotNull(v0);
            Preconditions.checkArgument(!range.isEmpty(), "Range must not be empty, but was %s", range);
            if (!this.keyRanges.complement().encloses(range)) {
                Iterator iterator = this.rangeMap.asMapOfRanges().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    Range range1 = (Range) entry.getKey();

                    if (range1.isConnected(range) && !range1.intersection(range).isEmpty()) {
                        throw new IllegalArgumentException("Overlapping ranges: range " + range + " overlaps with entry " + entry);
                    }
                }
            }

            this.keyRanges.add(range);
            this.rangeMap.put(range, v0);
            return this;
        }

        public ImmutableRangeMap.Builder<K, V> putAll(RangeMap<K, ? extends V> rangemap) {
            Iterator iterator = rangemap.asMapOfRanges().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.put((Range) entry.getKey(), entry.getValue());
            }

            return this;
        }

        public ImmutableRangeMap<K, V> build() {
            Map map = this.rangeMap.asMapOfRanges();
            ImmutableList.Builder immutablelist_builder = new ImmutableList.Builder<>(map.size());
            ImmutableList.Builder immutablelist_builder1 = new ImmutableList.Builder<>(map.size());
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                immutablelist_builder.add(entry.getKey());
                immutablelist_builder1.add(entry.getValue());
            }

            return new ImmutableRangeMap<>(immutablelist_builder.build(), immutablelist_builder1.build());
        }
    }
}
