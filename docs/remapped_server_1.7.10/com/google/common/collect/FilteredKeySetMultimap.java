package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredKeySetMultimap<K, V> extends FilteredKeyMultimap<K, V> implements FilteredSetMultimap<K, V> {

    FilteredKeySetMultimap(SetMultimap<K, V> setmultimap, Predicate<? super K> predicate) {
        super(setmultimap, predicate);
    }

    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap) this.unfiltered;
    }

    @Override
    public Set<V> get(K k0) {
        return (Set) super.get(k0);
    }

    @Override
    public Set<V> removeAll(Object object) {
        return (Set) super.removeAll(object);
    }

    @Override
    public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return (Set) super.replaceValues(k0, iterable);
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return (Set) super.entries();
    }

    @Override
    Set<Entry<K, V>> createEntries() {
        return new FilteredKeySetMultimap.EntrySet();
    }

    class EntrySet extends FilteredKeyMultimap<K, V>.Entries implements Set<Entry<K, V>> {

        EntrySet() {
            super();
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }
    }
}
