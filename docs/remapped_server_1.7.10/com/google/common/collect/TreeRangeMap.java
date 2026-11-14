package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("NavigableMap")
public final class TreeRangeMap<K extends Comparable, V> implements RangeMap<K, V> {

    private final NavigableMap<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> entriesByLowerBound = Maps.newTreeMap();
    private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap() {
        @Nullable
        @Override
        public Object get(Comparable comparable) {
            return null;
        }

        @Nullable
        @Override
        public Entry<Range, Object> getEntry(Comparable comparable) {
            return null;
        }

        @Override
        public Range span() {
            throw new NoSuchElementException();
        }

        @Override
        public void put(Range range, Object object) {
            Preconditions.checkNotNull(range);
            throw new IllegalArgumentException("Cannot insert range " + range + " into an empty subRangeMap");
        }

        @Override
        public void putAll(RangeMap rangemap) {
            if (!rangemap.asMapOfRanges().isEmpty()) {
                throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
            }
        }

        @Override
        public void clear() {}

        @Override
        public void remove(Range range) {
            Preconditions.checkNotNull(range);
        }

        @Override
        public Map<Range, Object> asMapOfRanges() {
            return Collections.emptyMap();
        }

        @Override
        public RangeMap subRangeMap(Range range) {
            Preconditions.checkNotNull(range);
            return this;
        }
    };

    public static <K extends Comparable, V> TreeRangeMap<K, V> create() {
        return new TreeRangeMap<>();
    }

    private TreeRangeMap() {}

    @Nullable
    @Override
    public V get(K k0) {
        Entry entry = this.getEntry(k0);

        return entry == null ? null : entry.getValue();
    }

    @Nullable
    @Override
    public Entry<Range<K>, V> getEntry(K k0) {
        Entry entry = this.entriesByLowerBound.floorEntry(Cut.belowValue(k0));

        return entry != null && ((TreeRangeMap.RangeMapEntry) entry.getValue()).contains(k0) ? (Entry) entry.getValue() : null;
    }

    @Override
    public void put(Range<K> range, V v0) {
        if (!range.isEmpty()) {
            Preconditions.checkNotNull(v0);
            this.remove(range);
            this.entriesByLowerBound.put(range.lowerBound, new TreeRangeMap.RangeMapEntry<>(range, v0));
        }

    }

    @Override
    public void putAll(RangeMap<K, V> rangemap) {
        Iterator iterator = rangemap.asMapOfRanges().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Range) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void clear() {
        this.entriesByLowerBound.clear();
    }

    @Override
    public Range<K> span() {
        Entry entry = this.entriesByLowerBound.firstEntry();
        Entry entry1 = this.entriesByLowerBound.lastEntry();

        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return Range.create(((TreeRangeMap.RangeMapEntry) entry.getValue()).getKey().lowerBound, ((TreeRangeMap.RangeMapEntry) entry1.getValue()).getKey().upperBound);
        }
    }

    private void putRangeMapEntry(Cut<K> cut, Cut<K> cut1, V v0) {
        this.entriesByLowerBound.put(cut, new TreeRangeMap.RangeMapEntry<>(cut, cut1, v0));
    }

    @Override
    public void remove(Range<K> range) {
        if (!range.isEmpty()) {
            Entry entry = this.entriesByLowerBound.lowerEntry(range.lowerBound);

            if (entry != null) {
                TreeRangeMap.RangeMapEntry treerangemap_rangemapentry = (TreeRangeMap.RangeMapEntry) entry.getValue();

                if (treerangemap_rangemapentry.getUpperBound().compareTo(range.lowerBound) > 0) {
                    if (treerangemap_rangemapentry.getUpperBound().compareTo(range.upperBound) > 0) {
                        this.putRangeMapEntry(range.upperBound, treerangemap_rangemapentry.getUpperBound(), ((TreeRangeMap.RangeMapEntry) entry.getValue()).getValue());
                    }

                    this.putRangeMapEntry(treerangemap_rangemapentry.getLowerBound(), range.lowerBound, ((TreeRangeMap.RangeMapEntry) entry.getValue()).getValue());
                }
            }

            Entry entry1 = this.entriesByLowerBound.lowerEntry(range.upperBound);

            if (entry1 != null) {
                TreeRangeMap.RangeMapEntry treerangemap_rangemapentry1 = (TreeRangeMap.RangeMapEntry) entry1.getValue();

                if (treerangemap_rangemapentry1.getUpperBound().compareTo(range.upperBound) > 0) {
                    this.putRangeMapEntry(range.upperBound, treerangemap_rangemapentry1.getUpperBound(), ((TreeRangeMap.RangeMapEntry) entry1.getValue()).getValue());
                    this.entriesByLowerBound.remove(range.lowerBound);
                }
            }

            this.entriesByLowerBound.subMap(range.lowerBound, range.upperBound).clear();
        }
    }

    @Override
    public Map<Range<K>, V> asMapOfRanges() {
        return new TreeRangeMap.AsMapOfRanges();
    }

    @Override
    public RangeMap<K, V> subRangeMap(Range<K> range) {
        return (RangeMap) (range.equals(Range.all()) ? this : new TreeRangeMap.SubRangeMap(range));
    }

    private RangeMap<K, V> emptySubRangeMap() {
        return TreeRangeMap.EMPTY_SUB_RANGE_MAP;
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
    public int hashCode() {
        return this.asMapOfRanges().hashCode();
    }

    @Override
    public String toString() {
        return this.entriesByLowerBound.values().toString();
    }

    private class SubRangeMap implements RangeMap<K, V> {

        private final Range<K> subRange;

        SubRangeMap(Range<K> range) {
            this.subRange = range;
        }

        @Nullable
        @Override
        public V get(K k0) {
            return this.subRange.contains(k0) ? TreeRangeMap.this.get(k0) : null;
        }

        @Nullable
        @Override
        public Entry<Range<K>, V> getEntry(K k0) {
            if (this.subRange.contains(k0)) {
                Entry entry = TreeRangeMap.this.getEntry(k0);

                if (entry != null) {
                    return Maps.immutableEntry(((Range) entry.getKey()).intersection(this.subRange), entry.getValue());
                }
            }

            return null;
        }

        @Override
        public Range<K> span() {
            Entry entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
            Cut cut;

            if (entry != null && ((TreeRangeMap.RangeMapEntry) entry.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0) {
                cut = this.subRange.lowerBound;
            } else {
                cut = (Cut) TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
                if (cut == null || cut.compareTo(this.subRange.upperBound) >= 0) {
                    throw new NoSuchElementException();
                }
            }

            Entry entry1 = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);

            if (entry1 == null) {
                throw new NoSuchElementException();
            } else {
                Cut cut1;

                if (((TreeRangeMap.RangeMapEntry) entry1.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
                    cut1 = this.subRange.upperBound;
                } else {
                    cut1 = ((TreeRangeMap.RangeMapEntry) entry1.getValue()).getUpperBound();
                }

                return Range.create(cut, cut1);
            }
        }

        @Override
        public void put(Range<K> range, V v0) {
            Preconditions.checkArgument(this.subRange.encloses(range), "Cannot put range %s into a subRangeMap(%s)", range, this.subRange);
            TreeRangeMap.this.put(range, v0);
        }

        @Override
        public void putAll(RangeMap<K, V> rangemap) {
            if (!rangemap.asMapOfRanges().isEmpty()) {
                Range range = rangemap.span();

                Preconditions.checkArgument(this.subRange.encloses(range), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", range, this.subRange);
                TreeRangeMap.this.putAll(rangemap);
            }
        }

        @Override
        public void clear() {
            TreeRangeMap.this.remove(this.subRange);
        }

        @Override
        public void remove(Range<K> range) {
            if (range.isConnected(this.subRange)) {
                TreeRangeMap.this.remove(range.intersection(this.subRange));
            }

        }

        @Override
        public RangeMap<K, V> subRangeMap(Range<K> range) {
            return !range.isConnected(this.subRange) ? TreeRangeMap.this.emptySubRangeMap() : TreeRangeMap.this.subRangeMap(range.intersection(this.subRange));
        }

        @Override
        public Map<Range<K>, V> asMapOfRanges() {
            return new TreeRangeMap.SubRangeMap.SubRangeMapAsMap();
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
        public int hashCode() {
            return this.asMapOfRanges().hashCode();
        }

        @Override
        public String toString() {
            return this.asMapOfRanges().toString();
        }

        class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {

            SubRangeMapAsMap() {}

            public boolean containsKey(Object object) {
                return this.get(object) != null;
            }

            public V get(Object object) {
                try {
                    if (object instanceof Range) {
                        Range range = (Range) object;

                        if (!SubRangeMap.this.subRange.encloses(range) || range.isEmpty()) {
                            return null;
                        }

                        TreeRangeMap.RangeMapEntry treerangemap_rangemapentry = null;

                        if (range.lowerBound.compareTo(SubRangeMap.this.subRange.lowerBound) == 0) {
                            Entry entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(range.lowerBound);

                            if (entry != null) {
                                treerangemap_rangemapentry = (TreeRangeMap.RangeMapEntry) entry.getValue();
                            }
                        } else {
                            treerangemap_rangemapentry = (TreeRangeMap.RangeMapEntry) TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);
                        }

                        if (treerangemap_rangemapentry != null && treerangemap_rangemapentry.getKey().isConnected(SubRangeMap.this.subRange) && treerangemap_rangemapentry.getKey().intersection(SubRangeMap.this.subRange).equals(range)) {
                            return treerangemap_rangemapentry.getValue();
                        }
                    }

                    return null;
                } catch (ClassCastException classcastexception) {
                    return null;
                }
            }

            public V remove(Object object) {
                Object object1 = this.get(object);

                if (object1 != null) {
                    Range range = (Range) object;

                    TreeRangeMap.this.remove(range);
                    return object1;
                } else {
                    return null;
                }
            }

            public void clear() {
                SubRangeMap.this.clear();
            }

            private boolean removeEntryIf(Predicate<? super Entry<Range<K>, V>> predicate) {
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = this.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (predicate.apply(entry)) {
                        arraylist.add(entry.getKey());
                    }
                }

                iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    Range range = (Range) iterator.next();

                    TreeRangeMap.this.remove(range);
                }

                return !arraylist.isEmpty();
            }

            public Set<Range<K>> keySet() {
                return new Maps.KeySet<Range<K>, V>(this) {
                    @Override
                    public boolean remove(@Nullable Object object) {
                        return SubRangeMapAsMap.this.remove(object) != null;
                    }

                    @Override
                    public boolean retainAll(Collection<?> collection) {
                        return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(collection)), Maps.keyFunction()));
                    }
                };
            }

            public Set<Entry<Range<K>, V>> entrySet() {
                return new Maps.EntrySet<Range<K>, V>() {
                    @Override
                    Map<Range<K>, V> map() {
                        return SubRangeMapAsMap.this;
                    }

                    public Iterator<Entry<Range<K>, V>> iterator() {
                        if (SubRangeMap.this.subRange.isEmpty()) {
                            return Iterators.emptyIterator();
                        } else {
                            Cut cut = (Cut) Objects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(SubRangeMap.this.subRange.lowerBound), SubRangeMap.this.subRange.lowerBound);
                            final Iterator iterator = TreeRangeMap.this.entriesByLowerBound.tailMap(cut, true).values().iterator();

                            return new AbstractIterator<Entry<Range<K>, V>>() {
                                @Override
                                protected Entry<Range<K>, V> computeNext() {
                                    while (true) {
                                        if (iterator.hasNext()) {
                                            TreeRangeMap.RangeMapEntry treerangemap_rangemapentry = (TreeRangeMap.RangeMapEntry) iterator.next();

                                            if (treerangemap_rangemapentry.getLowerBound().compareTo(SubRangeMap.this.subRange.upperBound) < 0) {
                                                if (treerangemap_rangemapentry.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) <= 0) {
                                                    continue;
                                                }

                                                return Maps.immutableEntry(treerangemap_rangemapentry.getKey().intersection(SubRangeMap.this.subRange), treerangemap_rangemapentry.getValue());
                                            }
                                        }

                                        return (Entry) this.endOfData();
                                    }
                                }
                            };
                        }
                    }

                    @Override
                    public boolean retainAll(Collection<?> collection) {
                        return SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(collection)));
                    }

                    @Override
                    public int size() {
                        return Iterators.size(this.iterator());
                    }

                    @Override
                    public boolean isEmpty() {
                        return !this.iterator().hasNext();
                    }
                };
            }

            public Collection<V> values() {
                return new Maps.Values<Range<K>, V>(this) {
                    @Override
                    public boolean removeAll(Collection<?> collection) {
                        return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(collection), Maps.valueFunction()));
                    }

                    @Override
                    public boolean retainAll(Collection<?> collection) {
                        return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(collection)), Maps.valueFunction()));
                    }
                };
            }
        }
    }

    private final class AsMapOfRanges extends AbstractMap<Range<K>, V> {

        private AsMapOfRanges() {}

        public boolean containsKey(@Nullable Object object) {
            return this.get(object) != null;
        }

        public V get(@Nullable Object object) {
            if (object instanceof Range) {
                Range range = (Range) object;
                TreeRangeMap.RangeMapEntry treerangemap_rangemapentry = (TreeRangeMap.RangeMapEntry) TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);

                if (treerangemap_rangemapentry != null && treerangemap_rangemapentry.getKey().equals(range)) {
                    return treerangemap_rangemapentry.getValue();
                }
            }

            return null;
        }

        public Set<Entry<Range<K>, V>> entrySet() {
            return new AbstractSet<Entry<Range<K>, V>>() {
                public Iterator<Entry<Range<K>, V>> iterator() {
                    return TreeRangeMap.this.entriesByLowerBound.values().iterator();
                }

                public int size() {
                    return TreeRangeMap.this.entriesByLowerBound.size();
                }
            };
        }
    }

    private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V> {

        private final Range<K> range;
        private final V value;

        RangeMapEntry(Cut<K> cut, Cut<K> cut1, V v0) {
            this(Range.create(cut, cut1), v0);
        }

        RangeMapEntry(Range<K> range, V v0) {
            this.range = range;
            this.value = v0;
        }

        @Override
        public Range<K> getKey() {
            return this.range;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        public boolean contains(K k0) {
            return this.range.contains(k0);
        }

        Cut<K> getLowerBound() {
            return this.range.lowerBound;
        }

        Cut<K> getUpperBound() {
            return this.range.upperBound;
        }
    }
}
