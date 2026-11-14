package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredKeyMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {

    final Multimap<K, V> unfiltered;
    final Predicate<? super K> keyPredicate;

    FilteredKeyMultimap(Multimap<K, V> multimap, Predicate<? super K> predicate) {
        this.unfiltered = (Multimap) Preconditions.checkNotNull(multimap);
        this.keyPredicate = (Predicate) Preconditions.checkNotNull(predicate);
    }

    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }

    @Override
    public Predicate<? super Entry<K, V>> entryPredicate() {
        return Maps.keyPredicateOnEntries(this.keyPredicate);
    }

    @Override
    public int size() {
        int i = 0;

        Collection collection;

        for (Iterator iterator = this.asMap().values().iterator(); iterator.hasNext(); i += collection.size()) {
            collection = (Collection) iterator.next();
        }

        return i;
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.unfiltered.containsKey(object) ? this.keyPredicate.apply(object) : false;
    }

    @Override
    public Collection<V> removeAll(Object object) {
        return this.containsKey(object) ? this.unfiltered.removeAll(object) : this.unmodifiableEmptyCollection();
    }

    Collection<V> unmodifiableEmptyCollection() {
        return (Collection) (this.unfiltered instanceof SetMultimap ? ImmutableSet.of() : ImmutableList.of());
    }

    @Override
    public void clear() {
        this.keySet().clear();
    }

    @Override
    Set<K> createKeySet() {
        return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
    }

    @Override
    public Collection<V> get(K k0) {
        return (Collection) (this.keyPredicate.apply(k0) ? this.unfiltered.get(k0) : (this.unfiltered instanceof SetMultimap ? new FilteredKeyMultimap.AddRejectingSet<>(k0) : new FilteredKeyMultimap.AddRejectingList<>(k0)));
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
        throw new AssertionError("should never be called");
    }

    @Override
    Collection<Entry<K, V>> createEntries() {
        return new FilteredKeyMultimap.Entries();
    }

    @Override
    Collection<V> createValues() {
        return new FilteredMultimapValues<>(this);
    }

    @Override
    Map<K, Collection<V>> createAsMap() {
        return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
    }

    @Override
    Multiset<K> createKeys() {
        return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
    }

    class Entries extends ForwardingCollection<Entry<K, V>> {

        Entries() {}

        @Override
        protected Collection<Entry<K, V>> delegate() {
            return Collections2.filter(FilteredKeyMultimap.this.unfiltered.entries(), FilteredKeyMultimap.this.entryPredicate());
        }

        @Override
        public boolean remove(@Nullable Object object) {
            if (object instanceof Entry) {
                Entry entry = (Entry) object;

                if (FilteredKeyMultimap.this.unfiltered.containsKey(entry.getKey()) && FilteredKeyMultimap.this.keyPredicate.apply(entry.getKey())) {
                    return FilteredKeyMultimap.this.unfiltered.remove(entry.getKey(), entry.getValue());
                }
            }

            return false;
        }
    }

    static class AddRejectingList<K, V> extends ForwardingList<V> {

        final K key;

        AddRejectingList(K k0) {
            this.key = k0;
        }

        @Override
        public boolean add(V v0) {
            this.add(0, v0);
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends V> collection) {
            this.addAll(0, collection);
            return true;
        }

        @Override
        public void add(int i, V v0) {
            Preconditions.checkPositionIndex(i, 0);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }

        @Override
        public boolean addAll(int i, Collection<? extends V> collection) {
            Preconditions.checkNotNull(collection);
            Preconditions.checkPositionIndex(i, 0);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }

        @Override
        protected List<V> delegate() {
            return Collections.emptyList();
        }
    }

    static class AddRejectingSet<K, V> extends ForwardingSet<V> {

        final K key;

        AddRejectingSet(K k0) {
            this.key = k0;
        }

        @Override
        public boolean add(V v0) {
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }

        @Override
        public boolean addAll(Collection<? extends V> collection) {
            Preconditions.checkNotNull(collection);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }

        @Override
        protected Set<V> delegate() {
            return Collections.emptySet();
        }
    }
}
