package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class MapConstraints {

    private MapConstraints() {}

    public static MapConstraint<Object, Object> notNull() {
        return MapConstraints.NotNullMapConstraint.INSTANCE;
    }

    public static <K, V> Map<K, V> constrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedMap<>(map, mapconstraint);
    }

    public static <K, V> Multimap<K, V> constrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedMultimap<>(multimap, mapconstraint);
    }

    public static <K, V> ListMultimap<K, V> constrainedListMultimap(ListMultimap<K, V> listmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedListMultimap<>(listmultimap, mapconstraint);
    }

    public static <K, V> SetMultimap<K, V> constrainedSetMultimap(SetMultimap<K, V> setmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedSetMultimap<>(setmultimap, mapconstraint);
    }

    public static <K, V> SortedSetMultimap<K, V> constrainedSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedSortedSetMultimap<>(sortedsetmultimap, mapconstraint);
    }

    private static <K, V> Entry<K, V> constrainedEntry(final Entry<K, V> entry, final MapConstraint<? super K, ? super V> mapconstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapconstraint);
        return new ForwardingMapEntry<K, V>() {
            @Override
            protected Entry<K, V> delegate() {
                return entry;
            }

            @Override
            public V setValue(V v0) {
                mapconstraint.checkKeyValue(this.getKey(), v0);
                return entry.setValue(v0);
            }
        };
    }

    private static <K, V> Entry<K, Collection<V>> constrainedAsMapEntry(final Entry<K, Collection<V>> entry, final MapConstraint<? super K, ? super V> mapconstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapconstraint);
        return new ForwardingMapEntry<K, Collection<V>>() {
            @Override
            protected Entry<K, Collection<V>> delegate() {
                return entry;
            }

            @Override
            public Collection<V> getValue() {
                return Constraints.constrainedTypePreservingCollection((Collection) entry.getValue(), new Constraint<V>() {
                    @Override
                    public V checkElement(V v0) {
                        mapconstraint.checkKeyValue(getKey(), v0);
                        return v0;
                    }
                });
            }
        };
    }

    private static <K, V> Set<Entry<K, Collection<V>>> constrainedAsMapEntries(Set<Entry<K, Collection<V>>> set, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedAsMapEntries<>(set, mapconstraint);
    }

    private static <K, V> Collection<Entry<K, V>> constrainedEntries(Collection<Entry<K, V>> collection, MapConstraint<? super K, ? super V> mapconstraint) {
        return (Collection) (collection instanceof Set ? constrainedEntrySet((Set) collection, mapconstraint) : new MapConstraints.ConstrainedEntries<>(collection, mapconstraint));
    }

    private static <K, V> Set<Entry<K, V>> constrainedEntrySet(Set<Entry<K, V>> set, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedEntrySet<>(set, mapconstraint);
    }

    public static <K, V> BiMap<K, V> constrainedBiMap(BiMap<K, V> bimap, MapConstraint<? super K, ? super V> mapconstraint) {
        return new MapConstraints.ConstrainedBiMap<>(bimap, (BiMap) null, mapconstraint);
    }

    private static <K, V> Collection<V> checkValues(K k0, Iterable<? extends V> iterable, MapConstraint<? super K, ? super V> mapconstraint) {
        ArrayList arraylist = Lists.newArrayList(iterable);
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            mapconstraint.checkKeyValue(k0, object);
        }

        return arraylist;
    }

    private static <K, V> Map<K, V> checkMap(Map<? extends K, ? extends V> map, MapConstraint<? super K, ? super V> mapconstraint) {
        LinkedHashMap linkedhashmap = new LinkedHashMap(map);
        Iterator iterator = linkedhashmap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            mapconstraint.checkKeyValue(entry.getKey(), entry.getValue());
        }

        return linkedhashmap;
    }

    private static class ConstrainedSortedSetMultimap<K, V> extends MapConstraints.ConstrainedSetMultimap<K, V> implements SortedSetMultimap<K, V> {

        ConstrainedSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
            super((SetMultimap) sortedsetmultimap, mapconstraint);
        }

        @Override
        public SortedSet<V> get(K k0) {
            return (SortedSet) super.get(k0);
        }

        @Override
        public SortedSet<V> removeAll(Object object) {
            return (SortedSet) super.removeAll(object);
        }

        @Override
        public SortedSet<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            return (SortedSet) super.replaceValues(k0, iterable);
        }

        @Override
        public Comparator<? super V> valueComparator() {
            return ((SortedSetMultimap) this.delegate()).valueComparator();
        }
    }

    private static class ConstrainedSetMultimap<K, V> extends MapConstraints.ConstrainedMultimap<K, V> implements SetMultimap<K, V> {

        ConstrainedSetMultimap(SetMultimap<K, V> setmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
            super(setmultimap, mapconstraint);
        }

        @Override
        public Set<V> get(K k0) {
            return (Set) super.get(k0);
        }

        @Override
        public Set<Entry<K, V>> entries() {
            return (Set) super.entries();
        }

        @Override
        public Set<V> removeAll(Object object) {
            return (Set) super.removeAll(object);
        }

        @Override
        public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            return (Set) super.replaceValues(k0, iterable);
        }
    }

    private static class ConstrainedListMultimap<K, V> extends MapConstraints.ConstrainedMultimap<K, V> implements ListMultimap<K, V> {

        ConstrainedListMultimap(ListMultimap<K, V> listmultimap, MapConstraint<? super K, ? super V> mapconstraint) {
            super(listmultimap, mapconstraint);
        }

        @Override
        public List<V> get(K k0) {
            return (List) super.get(k0);
        }

        @Override
        public List<V> removeAll(Object object) {
            return (List) super.removeAll(object);
        }

        @Override
        public List<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            return (List) super.replaceValues(k0, iterable);
        }
    }

    static class ConstrainedAsMapEntries<K, V> extends ForwardingSet<Entry<K, Collection<V>>> {

        private final MapConstraint<? super K, ? super V> constraint;
        private final Set<Entry<K, Collection<V>>> entries;

        ConstrainedAsMapEntries(Set<Entry<K, Collection<V>>> set, MapConstraint<? super K, ? super V> mapconstraint) {
            this.entries = set;
            this.constraint = mapconstraint;
        }

        @Override
        protected Set<Entry<K, Collection<V>>> delegate() {
            return this.entries;
        }

        @Override
        public Iterator<Entry<K, Collection<V>>> iterator() {
            final Iterator iterator = this.entries.iterator();

            return new ForwardingIterator<Entry<K, Collection<V>>>() {
                @Override
                public Entry<K, Collection<V>> next() {
                    return MapConstraints.constrainedAsMapEntry((Entry) iterator.next(), ConstrainedAsMapEntries.this.constraint);
                }

                @Override
                protected Iterator<Entry<K, Collection<V>>> delegate() {
                    return iterator;
                }
            };
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public boolean contains(Object object) {
            return Maps.containsEntryImpl(this.delegate(), object);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return this.standardContainsAll(collection);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            return this.standardEquals(object);
        }

        @Override
        public int hashCode() {
            return this.standardHashCode();
        }

        @Override
        public boolean remove(Object object) {
            return Maps.removeEntryImpl(this.delegate(), object);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
    }

    static class ConstrainedEntrySet<K, V> extends MapConstraints.ConstrainedEntries<K, V> implements Set<Entry<K, V>> {

        ConstrainedEntrySet(Set<Entry<K, V>> set, MapConstraint<? super K, ? super V> mapconstraint) {
            super(set, mapconstraint);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    private static class ConstrainedEntries<K, V> extends ForwardingCollection<Entry<K, V>> {

        final MapConstraint<? super K, ? super V> constraint;
        final Collection<Entry<K, V>> entries;

        ConstrainedEntries(Collection<Entry<K, V>> collection, MapConstraint<? super K, ? super V> mapconstraint) {
            this.entries = collection;
            this.constraint = mapconstraint;
        }

        @Override
        protected Collection<Entry<K, V>> delegate() {
            return this.entries;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            final Iterator iterator = this.entries.iterator();

            return new ForwardingIterator<Entry<K, V>>() {
                @Override
                public Entry<K, V> next() {
                    return MapConstraints.constrainedEntry((Entry) iterator.next(), ConstrainedEntries.this.constraint);
                }

                @Override
                protected Iterator<Entry<K, V>> delegate() {
                    return iterator;
                }
            };
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public boolean contains(Object object) {
            return Maps.containsEntryImpl(this.delegate(), object);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return this.standardContainsAll(collection);
        }

        @Override
        public boolean remove(Object object) {
            return Maps.removeEntryImpl(this.delegate(), object);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
    }

    private static class ConstrainedAsMapValues<K, V> extends ForwardingCollection<Collection<V>> {

        final Collection<Collection<V>> delegate;
        final Set<Entry<K, Collection<V>>> entrySet;

        ConstrainedAsMapValues(Collection<Collection<V>> collection, Set<Entry<K, Collection<V>>> set) {
            this.delegate = collection;
            this.entrySet = set;
        }

        @Override
        protected Collection<Collection<V>> delegate() {
            return this.delegate;
        }

        @Override
        public Iterator<Collection<V>> iterator() {
            final Iterator iterator = this.entrySet.iterator();

            return new Iterator<Collection<V>>() {
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public Collection<V> next() {
                    return (Collection) ((Entry) iterator.next()).getValue();
                }

                public void remove() {
                    iterator.remove();
                }
            };
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public boolean contains(Object object) {
            return this.standardContains(object);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return this.standardContainsAll(collection);
        }

        @Override
        public boolean remove(Object object) {
            return this.standardRemove(object);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
    }

    private static class ConstrainedMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {

        final MapConstraint<? super K, ? super V> constraint;
        final Multimap<K, V> delegate;
        transient Collection<Entry<K, V>> entries;
        transient Map<K, Collection<V>> asMap;

        public ConstrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> mapconstraint) {
            this.delegate = (Multimap) Preconditions.checkNotNull(multimap);
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapconstraint);
        }

        @Override
        protected Multimap<K, V> delegate() {
            return this.delegate;
        }

        @Override
        public Map<K, Collection<V>> asMap() {
            Object object = this.asMap;

            if (object == null) {
                final Map map = this.delegate.asMap();

                this.asMap = (Map) (object = new ForwardingMap<K, Collection<V>>() {
                    Set<Entry<K, Collection<V>>> entrySet;
                    Collection<Collection<V>> values;

                    @Override
                    protected Map<K, Collection<V>> delegate() {
                        return map;
                    }

                    @Override
                    public Set<Entry<K, Collection<V>>> entrySet() {
                        Set set = this.entrySet;

                        if (set == null) {
                            this.entrySet = set = MapConstraints.constrainedAsMapEntries(map.entrySet(), ConstrainedMultimap.this.constraint);
                        }

                        return set;
                    }

                    @Override
                    public Collection<V> get(Object object1) {
                        try {
                            Collection collection = ConstrainedMultimap.this.get(object1);

                            return collection.isEmpty() ? null : collection;
                        } catch (ClassCastException classcastexception) {
                            return null;
                        }
                    }

                    @Override
                    public Collection<Collection<V>> values() {
                        Object object1 = this.values;

                        if (object1 == null) {
                            this.values = (Collection) (object1 = new MapConstraints.ConstrainedAsMapValues<>(this.delegate().values(), this.entrySet()));
                        }

                        return (Collection) object1;
                    }

                    @Override
                    public boolean containsValue(Object object1) {
                        return this.values().contains(object1);
                    }
                });
            }

            return (Map) object;
        }

        @Override
        public Collection<Entry<K, V>> entries() {
            Collection collection = this.entries;

            if (collection == null) {
                this.entries = collection = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint);
            }

            return collection;
        }

        @Override
        public Collection<V> get(final K k0) {
            return Constraints.constrainedTypePreservingCollection(this.delegate.get(k0), new Constraint<V>() {
                @Override
                public V checkElement(V v0) {
                    ConstrainedMultimap.this.constraint.checkKeyValue(k0, v0);
                    return v0;
                }
            });
        }

        @Override
        public boolean put(K k0, V v0) {
            this.constraint.checkKeyValue(k0, v0);
            return this.delegate.put(k0, v0);
        }

        @Override
        public boolean putAll(K k0, Iterable<? extends V> iterable) {
            return this.delegate.putAll(k0, MapConstraints.checkValues(k0, iterable, this.constraint));
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
        public Collection<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            return this.delegate.replaceValues(k0, MapConstraints.checkValues(k0, iterable, this.constraint));
        }
    }

    private static class InverseConstraint<K, V> implements MapConstraint<K, V> {

        final MapConstraint<? super V, ? super K> constraint;

        public InverseConstraint(MapConstraint<? super V, ? super K> mapconstraint) {
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapconstraint);
        }

        @Override
        public void checkKeyValue(K k0, V v0) {
            this.constraint.checkKeyValue(v0, k0);
        }
    }

    private static class ConstrainedBiMap<K, V> extends MapConstraints.ConstrainedMap<K, V> implements BiMap<K, V> {

        volatile BiMap<V, K> inverse;

        ConstrainedBiMap(BiMap<K, V> bimap, @Nullable BiMap<V, K> bimap1, MapConstraint<? super K, ? super V> mapconstraint) {
            super(bimap, mapconstraint);
            this.inverse = bimap1;
        }

        @Override
        protected BiMap<K, V> delegate() {
            return (BiMap) super.delegate();
        }

        @Override
        public V forcePut(K k0, V v0) {
            this.constraint.checkKeyValue(k0, v0);
            return this.delegate().forcePut(k0, v0);
        }

        @Override
        public BiMap<V, K> inverse() {
            if (this.inverse == null) {
                this.inverse = new MapConstraints.ConstrainedBiMap<>(this.delegate().inverse(), this, new MapConstraints.InverseConstraint<>(this.constraint));
            }

            return this.inverse;
        }

        @Override
        public Set<V> values() {
            return this.delegate().values();
        }
    }

    static class ConstrainedMap<K, V> extends ForwardingMap<K, V> {

        private final Map<K, V> delegate;
        final MapConstraint<? super K, ? super V> constraint;
        private transient Set<Entry<K, V>> entrySet;

        ConstrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> mapconstraint) {
            this.delegate = (Map) Preconditions.checkNotNull(map);
            this.constraint = (MapConstraint) Preconditions.checkNotNull(mapconstraint);
        }

        @Override
        protected Map<K, V> delegate() {
            return this.delegate;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            Set set = this.entrySet;

            if (set == null) {
                this.entrySet = set = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint);
            }

            return set;
        }

        @Override
        public V put(K k0, V v0) {
            this.constraint.checkKeyValue(k0, v0);
            return this.delegate.put(k0, v0);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            this.delegate.putAll(MapConstraints.checkMap(map, this.constraint));
        }
    }

    private static enum NotNullMapConstraint implements MapConstraint<Object, Object> {

        INSTANCE;

        private NotNullMapConstraint() {}

        @Override
        public void checkKeyValue(Object object, Object object1) {
            Preconditions.checkNotNull(object);
            Preconditions.checkNotNull(object1);
        }

        @Override
        public String toString() {
            return "Not null";
        }
    }
}
