package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Multimaps {

    private Multimaps() {}

    public static <K, V> Multimap<K, V> newMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> supplier) {
        return new Multimaps.CustomMultimap<>(map, supplier);
    }

    public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> supplier) {
        return new Multimaps.CustomListMultimap<>(map, supplier);
    }

    public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> supplier) {
        return new Multimaps.CustomSetMultimap<>(map, supplier);
    }

    public static <K, V> SortedSetMultimap<K, V> newSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> supplier) {
        return new Multimaps.CustomSortedSetMultimap<>(map, supplier);
    }

    public static <K, V, M extends Multimap<K, V>> M invertFrom(Multimap<? extends V, ? extends K> multimap, M m0) {
        Preconditions.checkNotNull(m0);
        Iterator iterator = multimap.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            m0.put(entry.getValue(), entry.getKey());
        }

        return m0;
    }

    public static <K, V> Multimap<K, V> synchronizedMultimap(Multimap<K, V> multimap) {
        return Synchronized.multimap(multimap, (Object) null);
    }

    public static <K, V> Multimap<K, V> unmodifiableMultimap(Multimap<K, V> multimap) {
        return (Multimap) (!(multimap instanceof Multimaps.UnmodifiableMultimap) && !(multimap instanceof ImmutableMultimap) ? new Multimaps.UnmodifiableMultimap<>(multimap) : multimap);
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> Multimap<K, V> unmodifiableMultimap(ImmutableMultimap<K, V> immutablemultimap) {
        return (Multimap) Preconditions.checkNotNull(immutablemultimap);
    }

    public static <K, V> SetMultimap<K, V> synchronizedSetMultimap(SetMultimap<K, V> setmultimap) {
        return Synchronized.setMultimap(setmultimap, (Object) null);
    }

    public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(SetMultimap<K, V> setmultimap) {
        return (SetMultimap) (!(setmultimap instanceof Multimaps.UnmodifiableSetMultimap) && !(setmultimap instanceof ImmutableSetMultimap) ? new Multimaps.UnmodifiableSetMultimap<>(setmultimap) : setmultimap);
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(ImmutableSetMultimap<K, V> immutablesetmultimap) {
        return (SetMultimap) Preconditions.checkNotNull(immutablesetmultimap);
    }

    public static <K, V> SortedSetMultimap<K, V> synchronizedSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap) {
        return Synchronized.sortedSetMultimap(sortedsetmultimap, (Object) null);
    }

    public static <K, V> SortedSetMultimap<K, V> unmodifiableSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap) {
        return (SortedSetMultimap) (sortedsetmultimap instanceof Multimaps.UnmodifiableSortedSetMultimap ? sortedsetmultimap : new Multimaps.UnmodifiableSortedSetMultimap<>(sortedsetmultimap));
    }

    public static <K, V> ListMultimap<K, V> synchronizedListMultimap(ListMultimap<K, V> listmultimap) {
        return Synchronized.listMultimap(listmultimap, (Object) null);
    }

    public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ListMultimap<K, V> listmultimap) {
        return (ListMultimap) (!(listmultimap instanceof Multimaps.UnmodifiableListMultimap) && !(listmultimap instanceof ImmutableListMultimap) ? new Multimaps.UnmodifiableListMultimap<>(listmultimap) : listmultimap);
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ImmutableListMultimap<K, V> immutablelistmultimap) {
        return (ListMultimap) Preconditions.checkNotNull(immutablelistmultimap);
    }

    private static <V> Collection<V> unmodifiableValueCollection(Collection<V> collection) {
        return (Collection) (collection instanceof SortedSet ? Collections.unmodifiableSortedSet((SortedSet) collection) : (collection instanceof Set ? Collections.unmodifiableSet((Set) collection) : (collection instanceof List ? Collections.unmodifiableList((List) collection) : Collections.unmodifiableCollection(collection))));
    }

    private static <K, V> Collection<Entry<K, V>> unmodifiableEntries(Collection<Entry<K, V>> collection) {
        return (Collection) (collection instanceof Set ? Maps.unmodifiableEntrySet((Set) collection) : new Maps.UnmodifiableEntries<>(Collections.unmodifiableCollection(collection)));
    }

    @Beta
    public static <K, V> Map<K, List<V>> asMap(ListMultimap<K, V> listmultimap) {
        return listmultimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, Set<V>> asMap(SetMultimap<K, V> setmultimap) {
        return setmultimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, SortedSet<V>> asMap(SortedSetMultimap<K, V> sortedsetmultimap) {
        return sortedsetmultimap.asMap();
    }

    @Beta
    public static <K, V> Map<K, Collection<V>> asMap(Multimap<K, V> multimap) {
        return multimap.asMap();
    }

    public static <K, V> SetMultimap<K, V> forMap(Map<K, V> map) {
        return new Multimaps.MapMultimap<>(map);
    }

    public static <K, V1, V2> Multimap<K, V2> transformValues(Multimap<K, V1> multimap, Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        Maps.EntryTransformer maps_entrytransformer = Maps.asEntryTransformer(function);

        return transformEntries(multimap, maps_entrytransformer);
    }

    public static <K, V1, V2> Multimap<K, V2> transformEntries(Multimap<K, V1> multimap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return new Multimaps.TransformedEntriesMultimap<>(multimap, maps_entrytransformer);
    }

    public static <K, V1, V2> ListMultimap<K, V2> transformValues(ListMultimap<K, V1> listmultimap, Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        Maps.EntryTransformer maps_entrytransformer = Maps.asEntryTransformer(function);

        return transformEntries(listmultimap, maps_entrytransformer);
    }

    public static <K, V1, V2> ListMultimap<K, V2> transformEntries(ListMultimap<K, V1> listmultimap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return new Multimaps.TransformedEntriesListMultimap<>(listmultimap, maps_entrytransformer);
    }

    public static <K, V> ImmutableListMultimap<K, V> index(Iterable<V> iterable, Function<? super V, K> function) {
        return index(iterable.iterator(), function);
    }

    public static <K, V> ImmutableListMultimap<K, V> index(Iterator<V> iterator, Function<? super V, K> function) {
        Preconditions.checkNotNull(function);
        ImmutableListMultimap.Builder immutablelistmultimap_builder = ImmutableListMultimap.builder();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            Preconditions.checkNotNull(object, iterator);
            immutablelistmultimap_builder.put(function.apply(object), object);
        }

        return immutablelistmultimap_builder.build();
    }

    public static <K, V> Multimap<K, V> filterKeys(Multimap<K, V> multimap, Predicate<? super K> predicate) {
        if (multimap instanceof SetMultimap) {
            return filterKeys((SetMultimap) multimap, predicate);
        } else if (multimap instanceof ListMultimap) {
            return filterKeys((ListMultimap) multimap, predicate);
        } else if (multimap instanceof FilteredKeyMultimap) {
            FilteredKeyMultimap filteredkeymultimap = (FilteredKeyMultimap) multimap;

            return new FilteredKeyMultimap<>(filteredkeymultimap.unfiltered, Predicates.and(filteredkeymultimap.keyPredicate, predicate));
        } else if (multimap instanceof FilteredMultimap) {
            FilteredMultimap filteredmultimap = (FilteredMultimap) multimap;

            return filterFiltered(filteredmultimap, Maps.keyPredicateOnEntries(predicate));
        } else {
            return new FilteredKeyMultimap<>(multimap, predicate);
        }
    }

    public static <K, V> SetMultimap<K, V> filterKeys(SetMultimap<K, V> setmultimap, Predicate<? super K> predicate) {
        if (setmultimap instanceof FilteredKeySetMultimap) {
            FilteredKeySetMultimap filteredkeysetmultimap = (FilteredKeySetMultimap) setmultimap;

            return new FilteredKeySetMultimap<>(filteredkeysetmultimap.unfiltered(), Predicates.and(filteredkeysetmultimap.keyPredicate, predicate));
        } else if (setmultimap instanceof FilteredSetMultimap) {
            FilteredSetMultimap filteredsetmultimap = (FilteredSetMultimap) setmultimap;

            return filterFiltered(filteredsetmultimap, Maps.keyPredicateOnEntries(predicate));
        } else {
            return new FilteredKeySetMultimap<>(setmultimap, predicate);
        }
    }

    public static <K, V> ListMultimap<K, V> filterKeys(ListMultimap<K, V> listmultimap, Predicate<? super K> predicate) {
        if (listmultimap instanceof FilteredKeyListMultimap) {
            FilteredKeyListMultimap filteredkeylistmultimap = (FilteredKeyListMultimap) listmultimap;

            return new FilteredKeyListMultimap<>(filteredkeylistmultimap.unfiltered(), Predicates.and(filteredkeylistmultimap.keyPredicate, predicate));
        } else {
            return new FilteredKeyListMultimap<>(listmultimap, predicate);
        }
    }

    public static <K, V> Multimap<K, V> filterValues(Multimap<K, V> multimap, Predicate<? super V> predicate) {
        return filterEntries(multimap, Maps.valuePredicateOnEntries(predicate));
    }

    public static <K, V> SetMultimap<K, V> filterValues(SetMultimap<K, V> setmultimap, Predicate<? super V> predicate) {
        return filterEntries(setmultimap, Maps.valuePredicateOnEntries(predicate));
    }

    public static <K, V> Multimap<K, V> filterEntries(Multimap<K, V> multimap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        return (Multimap) (multimap instanceof SetMultimap ? filterEntries((SetMultimap) multimap, predicate) : (multimap instanceof FilteredMultimap ? filterFiltered((FilteredMultimap) multimap, predicate) : new FilteredEntryMultimap<>((Multimap) Preconditions.checkNotNull(multimap), predicate)));
    }

    public static <K, V> SetMultimap<K, V> filterEntries(SetMultimap<K, V> setmultimap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        return (SetMultimap) (setmultimap instanceof FilteredSetMultimap ? filterFiltered((FilteredSetMultimap) setmultimap, predicate) : new FilteredEntrySetMultimap<>((SetMultimap) Preconditions.checkNotNull(setmultimap), predicate));
    }

    private static <K, V> Multimap<K, V> filterFiltered(FilteredMultimap<K, V> filteredmultimap, Predicate<? super Entry<K, V>> predicate) {
        Predicate predicate1 = Predicates.and(filteredmultimap.entryPredicate(), predicate);

        return new FilteredEntryMultimap<>(filteredmultimap.unfiltered(), predicate1);
    }

    private static <K, V> SetMultimap<K, V> filterFiltered(FilteredSetMultimap<K, V> filteredsetmultimap, Predicate<? super Entry<K, V>> predicate) {
        Predicate predicate1 = Predicates.and(filteredsetmultimap.entryPredicate(), predicate);

        return new FilteredEntrySetMultimap<>(filteredsetmultimap.unfiltered(), predicate1);
    }

    static boolean equalsImpl(Multimap<?, ?> multimap, @Nullable Object object) {
        if (object == multimap) {
            return true;
        } else if (object instanceof Multimap) {
            Multimap multimap1 = (Multimap) object;

            return multimap.asMap().equals(multimap1.asMap());
        } else {
            return false;
        }
    }

    static final class AsMap<K, V> extends Maps.ImprovedAbstractMap<K, Collection<V>> {

        private final Multimap<K, V> multimap;

        AsMap(Multimap<K, V> multimap) {
            this.multimap = (Multimap) Preconditions.checkNotNull(multimap);
        }

        public int size() {
            return this.multimap.keySet().size();
        }

        @Override
        protected Set<Entry<K, Collection<V>>> createEntrySet() {
            return new Multimaps.AsMap.EntrySet();
        }

        void removeValuesForKey(Object object) {
            this.multimap.keySet().remove(object);
        }

        public Collection<V> get(Object object) {
            return this.containsKey(object) ? this.multimap.get(object) : null;
        }

        public Collection<V> remove(Object object) {
            return this.containsKey(object) ? this.multimap.removeAll(object) : null;
        }

        @Override
        public Set<K> keySet() {
            return this.multimap.keySet();
        }

        public boolean isEmpty() {
            return this.multimap.isEmpty();
        }

        public boolean containsKey(Object object) {
            return this.multimap.containsKey(object);
        }

        public void clear() {
            this.multimap.clear();
        }

        class EntrySet extends Maps.EntrySet<K, Collection<V>> {

            EntrySet() {}

            @Override
            Map<K, Collection<V>> map() {
                return AsMap.this;
            }

            public Iterator<Entry<K, Collection<V>>> iterator() {
                return Maps.asMapEntryIterator(AsMap.this.multimap.keySet(), new Function<K, Collection<V>>() {
                    @Override
                    public Collection<V> apply(K k0) {
                        return AsMap.this.multimap.get(k0);
                    }
                });
            }

            @Override
            public boolean remove(Object object) {
                if (!this.contains(object)) {
                    return false;
                } else {
                    Entry entry = (Entry) object;

                    AsMap.this.removeValuesForKey(entry.getKey());
                    return true;
                }
            }
        }
    }

    abstract static class Entries<K, V> extends AbstractCollection<Entry<K, V>> {

        Entries() {}

        abstract Multimap<K, V> multimap();

        public int size() {
            return this.multimap().size();
        }

        public boolean contains(@Nullable Object object) {
            if (object instanceof Entry) {
                Entry entry = (Entry) object;

                return this.multimap().containsEntry(entry.getKey(), entry.getValue());
            } else {
                return false;
            }
        }

        public boolean remove(@Nullable Object object) {
            if (object instanceof Entry) {
                Entry entry = (Entry) object;

                return this.multimap().remove(entry.getKey(), entry.getValue());
            } else {
                return false;
            }
        }

        public void clear() {
            this.multimap().clear();
        }
    }

    static class Keys<K, V> extends AbstractMultiset<K> {

        final Multimap<K, V> multimap;

        Keys(Multimap<K, V> multimap) {
            this.multimap = multimap;
        }

        @Override
        Iterator<Multiset.Entry<K>> entryIterator() {
            return new TransformedIterator<Entry<K, Collection<V>>, Multiset.Entry<K>>(this.multimap.asMap().entrySet().iterator()) {
                Multiset.Entry<K> transform(final Entry<K, Collection<V>> entry) {
                    return new Multisets.AbstractEntry<K>() {
                        @Override
                        public K getElement() {
                            return entry.getKey();
                        }

                        @Override
                        public int getCount() {
                            return ((Collection) entry.getValue()).size();
                        }
                    };
                }
            };
        }

        @Override
        int distinctElements() {
            return this.multimap.asMap().size();
        }

        @Override
        Set<Multiset.Entry<K>> createEntrySet() {
            return new Multimaps.Keys.KeysEntrySet();
        }

        @Override
        public boolean contains(@Nullable Object object) {
            return this.multimap.containsKey(object);
        }

        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(this.multimap.entries().iterator());
        }

        @Override
        public int count(@Nullable Object object) {
            Collection collection = (Collection) Maps.safeGet(this.multimap.asMap(), object);

            return collection == null ? 0 : collection.size();
        }

        @Override
        public int remove(@Nullable Object object, int i) {
            CollectPreconditions.checkNonnegative(i, "occurrences");
            if (i == 0) {
                return this.count(object);
            } else {
                Collection collection = (Collection) Maps.safeGet(this.multimap.asMap(), object);

                if (collection == null) {
                    return 0;
                } else {
                    int j = collection.size();

                    if (i >= j) {
                        collection.clear();
                    } else {
                        Iterator iterator = collection.iterator();

                        for (int k = 0; k < i; ++k) {
                            iterator.next();
                            iterator.remove();
                        }
                    }

                    return j;
                }
            }
        }

        @Override
        public void clear() {
            this.multimap.clear();
        }

        @Override
        public Set<K> elementSet() {
            return this.multimap.keySet();
        }

        class KeysEntrySet extends Multisets.EntrySet<K> {

            KeysEntrySet() {}

            @Override
            Multiset<K> multiset() {
                return Keys.this;
            }

            public Iterator<Multiset.Entry<K>> iterator() {
                return Keys.this.entryIterator();
            }

            public int size() {
                return Keys.this.distinctElements();
            }

            public boolean isEmpty() {
                return Keys.this.multimap.isEmpty();
            }

            @Override
            public boolean contains(@Nullable Object object) {
                if (!(object instanceof Multiset.Entry)) {
                    return false;
                } else {
                    Multiset.Entry multiset_entry = (Multiset.Entry) object;
                    Collection collection = (Collection) Keys.this.multimap.asMap().get(multiset_entry.getElement());

                    return collection != null && collection.size() == multiset_entry.getCount();
                }
            }

            @Override
            public boolean remove(@Nullable Object object) {
                if (object instanceof Multiset.Entry) {
                    Multiset.Entry multiset_entry = (Multiset.Entry) object;
                    Collection collection = (Collection) Keys.this.multimap.asMap().get(multiset_entry.getElement());

                    if (collection != null && collection.size() == multiset_entry.getCount()) {
                        collection.clear();
                        return true;
                    }
                }

                return false;
            }
        }
    }

    private static final class TransformedEntriesListMultimap<K, V1, V2> extends Multimaps.TransformedEntriesMultimap<K, V1, V2> implements ListMultimap<K, V2> {

        TransformedEntriesListMultimap(ListMultimap<K, V1> listmultimap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
            super(listmultimap, maps_entrytransformer);
        }

        @Override
        List<V2> transform(K k0, Collection<V1> collection) {
            return Lists.transform((List) collection, Maps.asValueToValueFunction(this.transformer, k0));
        }

        @Override
        public List<V2> get(K k0) {
            return this.transform(k0, this.fromMultimap.get(k0));
        }

        @Override
        public List<V2> removeAll(Object object) {
            return this.transform(object, this.fromMultimap.removeAll(object));
        }

        @Override
        public List<V2> replaceValues(K k0, Iterable<? extends V2> iterable) {
            throw new UnsupportedOperationException();
        }
    }

    private static class TransformedEntriesMultimap<K, V1, V2> extends AbstractMultimap<K, V2> {

        final Multimap<K, V1> fromMultimap;
        final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;

        TransformedEntriesMultimap(Multimap<K, V1> multimap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
            this.fromMultimap = (Multimap) Preconditions.checkNotNull(multimap);
            this.transformer = (Maps.EntryTransformer) Preconditions.checkNotNull(maps_entrytransformer);
        }

        Collection<V2> transform(K k0, Collection<V1> collection) {
            Function function = Maps.asValueToValueFunction(this.transformer, k0);

            return (Collection) (collection instanceof List ? Lists.transform((List) collection, function) : Collections2.transform(collection, function));
        }

        @Override
        Map<K, Collection<V2>> createAsMap() {
            return Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer<K, Collection<V1>, Collection<V2>>() {
                public Collection<V2> transformEntry(K k0, Collection<V1> collection) {
                    return TransformedEntriesMultimap.this.transform(k0, collection);
                }
            });
        }

        @Override
        public void clear() {
            this.fromMultimap.clear();
        }

        @Override
        public boolean containsKey(Object object) {
            return this.fromMultimap.containsKey(object);
        }

        @Override
        Iterator<Entry<K, V2>> entryIterator() {
            return Iterators.transform(this.fromMultimap.entries().iterator(), Maps.asEntryToEntryFunction(this.transformer));
        }

        @Override
        public Collection<V2> get(K k0) {
            return this.transform(k0, this.fromMultimap.get(k0));
        }

        @Override
        public boolean isEmpty() {
            return this.fromMultimap.isEmpty();
        }

        @Override
        public Set<K> keySet() {
            return this.fromMultimap.keySet();
        }

        @Override
        public Multiset<K> keys() {
            return this.fromMultimap.keys();
        }

        @Override
        public boolean put(K k0, V2 v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(K k0, Iterable<? extends V2> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V2> multimap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object, Object object1) {
            return this.get(object).remove(object1);
        }

        @Override
        public Collection<V2> removeAll(Object object) {
            return this.transform(object, this.fromMultimap.removeAll(object));
        }

        @Override
        public Collection<V2> replaceValues(K k0, Iterable<? extends V2> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return this.fromMultimap.size();
        }

        @Override
        Collection<V2> createValues() {
            return Collections2.transform(this.fromMultimap.entries(), Maps.asEntryToValueFunction(this.transformer));
        }
    }

    private static class MapMultimap<K, V> extends AbstractMultimap<K, V> implements SetMultimap<K, V>, Serializable {

        final Map<K, V> map;
        private static final long serialVersionUID = 7845222491160860175L;

        MapMultimap(Map<K, V> map) {
            this.map = (Map) Preconditions.checkNotNull(map);
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean containsKey(Object object) {
            return this.map.containsKey(object);
        }

        @Override
        public boolean containsValue(Object object) {
            return this.map.containsValue(object);
        }

        @Override
        public boolean containsEntry(Object object, Object object1) {
            return this.map.entrySet().contains(Maps.immutableEntry(object, object1));
        }

        @Override
        public Set<V> get(final K k0) {
            return new Sets.ImprovedAbstractSet<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        int i;

                        public boolean hasNext() {
                            return this.i == 0 && MapMultimap.this.map.containsKey(k0);
                        }

                        public V next() {
                            if (!this.hasNext()) {
                                throw new NoSuchElementException();
                            } else {
                                ++this.i;
                                return MapMultimap.this.map.get(k0);
                            }
                        }

                        public void remove() {
                            CollectPreconditions.checkRemove(this.i == 1);
                            this.i = -1;
                            MapMultimap.this.map.remove(k0);
                        }
                    };
                }

                public int size() {
                    return MapMultimap.this.map.containsKey(k0) ? 1 : 0;
                }
            };
        }

        @Override
        public boolean put(K k0, V v0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object, Object object1) {
            return this.map.entrySet().remove(Maps.immutableEntry(object, object1));
        }

        @Override
        public Set<V> removeAll(Object object) {
            HashSet hashset = new HashSet(2);

            if (!this.map.containsKey(object)) {
                return hashset;
            } else {
                hashset.add(this.map.remove(object));
                return hashset;
            }
        }

        @Override
        public void clear() {
            this.map.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.map.keySet();
        }

        @Override
        public Collection<V> values() {
            return this.map.values();
        }

        @Override
        public Set<Entry<K, V>> entries() {
            return this.map.entrySet();
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return this.map.entrySet().iterator();
        }

        @Override
        Map<K, Collection<V>> createAsMap() {
            return new Multimaps.AsMap<>(this);
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }
    }

    private static class UnmodifiableSortedSetMultimap<K, V> extends Multimaps.UnmodifiableSetMultimap<K, V> implements SortedSetMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        UnmodifiableSortedSetMultimap(SortedSetMultimap<K, V> sortedsetmultimap) {
            super((SetMultimap) sortedsetmultimap);
        }

        @Override
        public SortedSetMultimap<K, V> delegate() {
            return (SortedSetMultimap) super.delegate();
        }

        @Override
        public SortedSet<V> get(K k0) {
            return Collections.unmodifiableSortedSet(this.delegate().get(k0));
        }

        @Override
        public SortedSet<V> removeAll(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SortedSet<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<? super V> valueComparator() {
            return this.delegate().valueComparator();
        }
    }

    private static class UnmodifiableSetMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V> implements SetMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        UnmodifiableSetMultimap(SetMultimap<K, V> setmultimap) {
            super(setmultimap);
        }

        @Override
        public SetMultimap<K, V> delegate() {
            return (SetMultimap) super.delegate();
        }

        @Override
        public Set<V> get(K k0) {
            return Collections.unmodifiableSet(this.delegate().get(k0));
        }

        @Override
        public Set<Entry<K, V>> entries() {
            return Maps.unmodifiableEntrySet(this.delegate().entries());
        }

        @Override
        public Set<V> removeAll(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }
    }

    private static class UnmodifiableListMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V> implements ListMultimap<K, V> {

        private static final long serialVersionUID = 0L;

        UnmodifiableListMultimap(ListMultimap<K, V> listmultimap) {
            super(listmultimap);
        }

        @Override
        public ListMultimap<K, V> delegate() {
            return (ListMultimap) super.delegate();
        }

        @Override
        public List<V> get(K k0) {
            return Collections.unmodifiableList(this.delegate().get(k0));
        }

        @Override
        public List<V> removeAll(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }
    }

    private static class UnmodifiableMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {

        final Multimap<K, V> delegate;
        transient Collection<Entry<K, V>> entries;
        transient Multiset<K> keys;
        transient Set<K> keySet;
        transient Collection<V> values;
        transient Map<K, Collection<V>> map;
        private static final long serialVersionUID = 0L;

        UnmodifiableMultimap(Multimap<K, V> multimap) {
            this.delegate = (Multimap) Preconditions.checkNotNull(multimap);
        }

        @Override
        protected Multimap<K, V> delegate() {
            return this.delegate;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<K, Collection<V>> asMap() {
            Map map = this.map;

            if (map == null) {
                map = this.map = Collections.unmodifiableMap(Maps.transformValues(this.delegate.asMap(), new Function<Collection<V>, Collection<V>>() {
                    public Collection<V> apply(Collection<V> collection) {
                        return Multimaps.unmodifiableValueCollection(collection);
                    }
                }));
            }

            return map;
        }

        @Override
        public Collection<Entry<K, V>> entries() {
            Collection collection = this.entries;

            if (collection == null) {
                this.entries = collection = Multimaps.unmodifiableEntries(this.delegate.entries());
            }

            return collection;
        }

        @Override
        public Collection<V> get(K k0) {
            return Multimaps.unmodifiableValueCollection(this.delegate.get(k0));
        }

        @Override
        public Multiset<K> keys() {
            Multiset multiset = this.keys;

            if (multiset == null) {
                this.keys = multiset = Multisets.unmodifiableMultiset(this.delegate.keys());
            }

            return multiset;
        }

        @Override
        public Set<K> keySet() {
            Set set = this.keySet;

            if (set == null) {
                this.keySet = set = Collections.unmodifiableSet(this.delegate.keySet());
            }

            return set;
        }

        @Override
        public boolean put(K k0, V v0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object object, Object object1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<V> removeAll(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<V> replaceValues(K k0, Iterable<? extends V> iterable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<V> values() {
            Collection collection = this.values;

            if (collection == null) {
                this.values = collection = Collections.unmodifiableCollection(this.delegate.values());
            }

            return collection;
        }
    }

    private static class CustomSortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {

        transient Supplier<? extends SortedSet<V>> factory;
        transient Comparator<? super V> valueComparator;
        @GwtIncompatible("not needed in emulated source")
        private static final long serialVersionUID = 0L;

        CustomSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> supplier) {
            super(map);
            this.factory = (Supplier) Preconditions.checkNotNull(supplier);
            this.valueComparator = ((SortedSet) supplier.get()).comparator();
        }

        @Override
        protected SortedSet<V> createCollection() {
            return (SortedSet) this.factory.get();
        }

        @Override
        public Comparator<? super V> valueComparator() {
            return this.valueComparator;
        }

        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            objectoutputstream.writeObject(this.factory);
            objectoutputstream.writeObject(this.backingMap());
        }

        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            this.factory = (Supplier) objectinputstream.readObject();
            this.valueComparator = ((SortedSet) this.factory.get()).comparator();
            Map map = (Map) objectinputstream.readObject();

            this.setMap(map);
        }
    }

    private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V> {

        transient Supplier<? extends Set<V>> factory;
        @GwtIncompatible("not needed in emulated source")
        private static final long serialVersionUID = 0L;

        CustomSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> supplier) {
            super(map);
            this.factory = (Supplier) Preconditions.checkNotNull(supplier);
        }

        @Override
        protected Set<V> createCollection() {
            return (Set) this.factory.get();
        }

        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            objectoutputstream.writeObject(this.factory);
            objectoutputstream.writeObject(this.backingMap());
        }

        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            this.factory = (Supplier) objectinputstream.readObject();
            Map map = (Map) objectinputstream.readObject();

            this.setMap(map);
        }
    }

    private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V> {

        transient Supplier<? extends List<V>> factory;
        @GwtIncompatible("java serialization not supported")
        private static final long serialVersionUID = 0L;

        CustomListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> supplier) {
            super(map);
            this.factory = (Supplier) Preconditions.checkNotNull(supplier);
        }

        @Override
        protected List<V> createCollection() {
            return (List) this.factory.get();
        }

        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            objectoutputstream.writeObject(this.factory);
            objectoutputstream.writeObject(this.backingMap());
        }

        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            this.factory = (Supplier) objectinputstream.readObject();
            Map map = (Map) objectinputstream.readObject();

            this.setMap(map);
        }
    }

    private static class CustomMultimap<K, V> extends AbstractMapBasedMultimap<K, V> {

        transient Supplier<? extends Collection<V>> factory;
        @GwtIncompatible("java serialization not supported")
        private static final long serialVersionUID = 0L;

        CustomMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> supplier) {
            super(map);
            this.factory = (Supplier) Preconditions.checkNotNull(supplier);
        }

        @Override
        protected Collection<V> createCollection() {
            return (Collection) this.factory.get();
        }

        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            objectoutputstream.writeObject(this.factory);
            objectoutputstream.writeObject(this.backingMap());
        }

        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            this.factory = (Supplier) objectinputstream.readObject();
            Map map = (Map) objectinputstream.readObject();

            this.setMap(map);
        }
    }
}
