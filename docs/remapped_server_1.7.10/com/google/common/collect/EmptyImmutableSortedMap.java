package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class EmptyImmutableSortedMap<K, V> extends ImmutableSortedMap<K, V> {

    private final transient ImmutableSortedSet<K> keySet;

    EmptyImmutableSortedMap(Comparator<? super K> comparator) {
        this.keySet = ImmutableSortedSet.emptySet(comparator);
    }

    EmptyImmutableSortedMap(Comparator<? super K> comparator, ImmutableSortedMap<K, V> immutablesortedmap) {
        super(immutablesortedmap);
        this.keySet = ImmutableSortedSet.emptySet(comparator);
    }

    @Override
    public V get(@Nullable Object object) {
        return null;
    }

    @Override
    public ImmutableSortedSet<K> keySet() {
        return this.keySet;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableCollection<V> values() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "{}";
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        return ImmutableSet.of();
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableSetMultimap<K, V> asMultimap() {
        return ImmutableSetMultimap.of();
    }

    @Override
    public ImmutableSortedMap<K, V> headMap(K k0, boolean flag) {
        Preconditions.checkNotNull(k0);
        return this;
    }

    @Override
    public ImmutableSortedMap<K, V> tailMap(K k0, boolean flag) {
        Preconditions.checkNotNull(k0);
        return this;
    }

    @Override
    ImmutableSortedMap<K, V> createDescendingMap() {
        return new EmptyImmutableSortedMap<>(Ordering.from(this.comparator()).reverse(), this);
    }
}
