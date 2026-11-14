package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class RegularImmutableSortedMap<K, V> extends ImmutableSortedMap<K, V> {

    private final transient RegularImmutableSortedSet<K> keySet;
    private final transient ImmutableList<V> valueList;

    RegularImmutableSortedMap(RegularImmutableSortedSet<K> regularimmutablesortedset, ImmutableList<V> immutablelist) {
        this.keySet = regularimmutablesortedset;
        this.valueList = immutablelist;
    }

    RegularImmutableSortedMap(RegularImmutableSortedSet<K> regularimmutablesortedset, ImmutableList<V> immutablelist, ImmutableSortedMap<K, V> immutablesortedmap) {
        super(immutablesortedmap);
        this.keySet = regularimmutablesortedset;
        this.valueList = immutablelist;
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new RegularImmutableSortedMap.EntrySet();
    }

    @Override
    public ImmutableSortedSet<K> keySet() {
        return this.keySet;
    }

    @Override
    public ImmutableCollection<V> values() {
        return this.valueList;
    }

    @Override
    public V get(@Nullable Object object) {
        int i = this.keySet.indexOf(object);

        return i == -1 ? null : this.valueList.get(i);
    }

    private ImmutableSortedMap<K, V> getSubMap(int i, int j) {
        return (ImmutableSortedMap) (i == 0 && j == this.size() ? this : (i == j ? emptyMap(this.comparator()) : from(this.keySet.getSubSet(i, j), this.valueList.subList(i, j))));
    }

    @Override
    public ImmutableSortedMap<K, V> headMap(K k0, boolean flag) {
        return this.getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull(k0), flag));
    }

    @Override
    public ImmutableSortedMap<K, V> tailMap(K k0, boolean flag) {
        return this.getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull(k0), flag), this.size());
    }

    @Override
    ImmutableSortedMap<K, V> createDescendingMap() {
        return new RegularImmutableSortedMap<>((RegularImmutableSortedSet) this.keySet.descendingSet(), this.valueList.reverse(), this);
    }

    private class EntrySet extends ImmutableMapEntrySet<K, V> {

        private EntrySet() {}

        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.asList().iterator();
        }

        @Override
        ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableAsList<Entry<K, V>>() {
                private final ImmutableList<K> keyList = RegularImmutableSortedMap.this.keySet().asList();

                public Entry<K, V> get(int i) {
                    return Maps.immutableEntry(this.keyList.get(i), RegularImmutableSortedMap.this.valueList.get(i));
                }

                @Override
                ImmutableCollection<Entry<K, V>> delegateCollection() {
                    return EntrySet.this;
                }
            };
        }

        @Override
        ImmutableMap<K, V> map() {
            return RegularImmutableSortedMap.this;
        }
    }
}
