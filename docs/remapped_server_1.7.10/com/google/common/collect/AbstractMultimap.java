package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultimap<K, V> implements Multimap<K, V> {

    private transient Collection<Entry<K, V>> entries;
    private transient Set<K> keySet;
    private transient Multiset<K> keys;
    private transient Collection<V> values;
    private transient Map<K, Collection<V>> asMap;

    AbstractMultimap() {}

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        Iterator iterator = this.asMap().values().iterator();

        Collection collection;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            collection = (Collection) iterator.next();
        } while (!collection.contains(object));

        return true;
    }

    @Override
    public boolean containsEntry(@Nullable Object object, @Nullable Object object1) {
        Collection collection = (Collection) this.asMap().get(object);

        return collection != null && collection.contains(object1);
    }

    @Override
    public boolean remove(@Nullable Object object, @Nullable Object object1) {
        Collection collection = (Collection) this.asMap().get(object);

        return collection != null && collection.remove(object1);
    }

    @Override
    public boolean put(@Nullable K k0, @Nullable V v0) {
        return this.get(k0).add(v0);
    }

    @Override
    public boolean putAll(@Nullable K k0, Iterable<? extends V> iterable) {
        Preconditions.checkNotNull(iterable);
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;

            return !collection.isEmpty() && this.get(k0).addAll(collection);
        } else {
            Iterator iterator = iterable.iterator();

            return iterator.hasNext() && Iterators.addAll(this.get(k0), iterator);
        }
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        boolean flag = false;

        Entry entry;

        for (Iterator iterator = multimap.entries().iterator(); iterator.hasNext(); flag |= this.put(entry.getKey(), entry.getValue())) {
            entry = (Entry) iterator.next();
        }

        return flag;
    }

    @Override
    public Collection<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        Preconditions.checkNotNull(iterable);
        Collection collection = this.removeAll(k0);

        this.putAll(k0, iterable);
        return collection;
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        Collection collection = this.entries;

        return collection == null ? (this.entries = this.createEntries()) : collection;
    }

    Collection<Entry<K, V>> createEntries() {
        return (Collection) (this instanceof SetMultimap ? new AbstractMultimap.EntrySet() : new AbstractMultimap.Entries());
    }

    abstract Iterator<Entry<K, V>> entryIterator();

    @Override
    public Set<K> keySet() {
        Set set = this.keySet;

        return set == null ? (this.keySet = this.createKeySet()) : set;
    }

    Set<K> createKeySet() {
        return new Maps.KeySet<>(this.asMap());
    }

    @Override
    public Multiset<K> keys() {
        Multiset multiset = this.keys;

        return multiset == null ? (this.keys = this.createKeys()) : multiset;
    }

    Multiset<K> createKeys() {
        return new Multimaps.Keys<>(this);
    }

    @Override
    public Collection<V> values() {
        Collection collection = this.values;

        return collection == null ? (this.values = this.createValues()) : collection;
    }

    Collection<V> createValues() {
        return new AbstractMultimap.Values();
    }

    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entries().iterator());
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        Map map = this.asMap;

        return map == null ? (this.asMap = this.createAsMap()) : map;
    }

    abstract Map<K, Collection<V>> createAsMap();

    @Override
    public boolean equals(@Nullable Object object) {
        return Multimaps.equalsImpl(this, object);
    }

    @Override
    public int hashCode() {
        return this.asMap().hashCode();
    }

    public String toString() {
        return this.asMap().toString();
    }

    class Values extends AbstractCollection<V> {

        Values() {}

        public Iterator<V> iterator() {
            return AbstractMultimap.this.valueIterator();
        }

        public int size() {
            return AbstractMultimap.this.size();
        }

        public boolean contains(@Nullable Object object) {
            return AbstractMultimap.this.containsValue(object);
        }

        public void clear() {
            AbstractMultimap.this.clear();
        }
    }

    private class EntrySet extends AbstractMultimap<K, V>.Entries implements Set<Entry<K, V>> {

        private EntrySet() {
            super(null);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }
    }

    private class Entries extends Multimaps.Entries<K, V> {

        private Entries() {}

        @Override
        Multimap<K, V> multimap() {
            return AbstractMultimap.this;
        }

        public Iterator<Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }
    }
}
