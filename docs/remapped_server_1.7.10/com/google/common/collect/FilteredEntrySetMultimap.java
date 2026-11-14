package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
final class FilteredEntrySetMultimap<K, V> extends FilteredEntryMultimap<K, V> implements FilteredSetMultimap<K, V> {

    FilteredEntrySetMultimap(SetMultimap<K, V> setmultimap, Predicate<? super Entry<K, V>> predicate) {
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
    Set<Entry<K, V>> createEntries() {
        return Sets.filter(this.unfiltered().entries(), this.entryPredicate());
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return (Set) super.entries();
    }
}
