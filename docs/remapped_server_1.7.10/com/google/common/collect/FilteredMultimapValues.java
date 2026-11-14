package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredMultimapValues<K, V> extends AbstractCollection<V> {

    private final FilteredMultimap<K, V> multimap;

    FilteredMultimapValues(FilteredMultimap<K, V> filteredmultimap) {
        this.multimap = (FilteredMultimap) Preconditions.checkNotNull(filteredmultimap);
    }

    public Iterator<V> iterator() {
        return Maps.valueIterator(this.multimap.entries().iterator());
    }

    public boolean contains(@Nullable Object object) {
        return this.multimap.containsValue(object);
    }

    public int size() {
        return this.multimap.size();
    }

    public boolean remove(@Nullable Object object) {
        Predicate predicate = this.multimap.entryPredicate();
        Iterator iterator = this.multimap.unfiltered().entries().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entry = (Entry) iterator.next();
        } while (!predicate.apply(entry) || !Objects.equal(entry.getValue(), object));

        iterator.remove();
        return true;
    }

    public boolean removeAll(Collection<?> collection) {
        return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.in(collection))));
    }

    public boolean retainAll(Collection<?> collection) {
        return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(collection)))));
    }

    public void clear() {
        this.multimap.clear();
    }
}
