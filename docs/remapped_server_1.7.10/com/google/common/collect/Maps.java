package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Maps {

    static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");

    private Maps() {}

    static <K> Function<Entry<K, ?>, K> keyFunction() {
        return Maps.EntryFunction.KEY;
    }

    static <V> Function<Entry<?, V>, V> valueFunction() {
        return Maps.EntryFunction.VALUE;
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> iterator) {
        return Iterators.transform(iterator, keyFunction());
    }

    static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> iterator) {
        return Iterators.transform(iterator, valueFunction());
    }

    static <K, V> UnmodifiableIterator<V> valueIterator(final UnmodifiableIterator<Entry<K, V>> unmodifiableiterator) {
        return new UnmodifiableIterator<V>() {
            public boolean hasNext() {
                return unmodifiableiterator.hasNext();
            }

            public V next() {
                return ((Entry) unmodifiableiterator.next()).getValue();
            }
        };
    }

    @GwtCompatible(serializable = true)
    @Beta
    public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
        if (map instanceof ImmutableEnumMap) {
            ImmutableEnumMap immutableenummap = (ImmutableEnumMap) map;

            return immutableenummap;
        } else if (map.isEmpty()) {
            return ImmutableMap.of();
        } else {
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                Preconditions.checkNotNull(entry.getKey());
                Preconditions.checkNotNull(entry.getValue());
            }

            return ImmutableEnumMap.asImmutable(new EnumMap(map));
        }
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int i) {
        return new HashMap(capacity(i));
    }

    static int capacity(int i) {
        if (i < 3) {
            CollectPreconditions.checkNonnegative(i, "expectedSize");
            return i + 1;
        } else {
            return i < 1073741824 ? i + i / 3 : Integer.MAX_VALUE;
        }
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
        return new HashMap(map);
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap(map);
    }

    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return (new MapMaker()).makeMap();
    }

    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> sortedmap) {
        return new TreeMap(sortedmap);
    }

    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<C> comparator) {
        return new TreeMap(comparator);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> oclass) {
        return new EnumMap((Class) Preconditions.checkNotNull(oclass));
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap(map);
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap();
    }

    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map1) {
        if (map instanceof SortedMap) {
            SortedMap sortedmap = (SortedMap) map;
            SortedMapDifference sortedmapdifference = difference(sortedmap, map1);

            return sortedmapdifference;
        } else {
            return difference(map, map1, Equivalence.equals());
        }
    }

    @Beta
    public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map1, Equivalence<? super V> equivalence) {
        Preconditions.checkNotNull(equivalence);
        HashMap hashmap = newHashMap();
        HashMap hashmap1 = new HashMap(map1);
        HashMap hashmap2 = newHashMap();
        HashMap hashmap3 = newHashMap();

        doDifference(map, map1, equivalence, hashmap, hashmap1, hashmap2, hashmap3);
        return new Maps.MapDifferenceImpl<>(hashmap, hashmap1, hashmap2, hashmap3);
    }

    private static <K, V> void doDifference(Map<? extends K, ? extends V> map, Map<? extends K, ? extends V> map1, Equivalence<? super V> equivalence, Map<K, V> map2, Map<K, V> map3, Map<K, V> map4, Map<K, MapDifference.ValueDifference<V>> map5) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();
            Object object1 = entry.getValue();

            if (map1.containsKey(object)) {
                Object object2 = map3.remove(object);

                if (equivalence.equivalent(object1, object2)) {
                    map4.put(object, object1);
                } else {
                    map5.put(object, Maps.ValueDifferenceImpl.create(object1, object2));
                }
            } else {
                map2.put(object, object1);
            }
        }

    }

    private static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        return (Map) (map instanceof SortedMap ? Collections.unmodifiableSortedMap((SortedMap) map) : Collections.unmodifiableMap(map));
    }

    public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> sortedmap, Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(sortedmap);
        Preconditions.checkNotNull(map);
        Comparator comparator = orNaturalOrder(sortedmap.comparator());
        TreeMap treemap = newTreeMap(comparator);
        TreeMap treemap1 = newTreeMap(comparator);

        treemap1.putAll(map);
        TreeMap treemap2 = newTreeMap(comparator);
        TreeMap treemap3 = newTreeMap(comparator);

        doDifference(sortedmap, map, Equivalence.equals(), treemap, treemap1, treemap2, treemap3);
        return new Maps.SortedMapDifferenceImpl<>(treemap, treemap1, treemap2, treemap3);
    }

    static <E> Comparator<? super E> orNaturalOrder(@Nullable Comparator<? super E> comparator) {
        return (Comparator) (comparator != null ? comparator : Ordering.natural());
    }

    @Beta
    public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
        return (Map) (set instanceof SortedSet ? asMap((SortedSet) set, function) : new Maps.AsMapView<>(set, function));
    }

    @Beta
    public static <K, V> SortedMap<K, V> asMap(SortedSet<K> sortedset, Function<? super K, V> function) {
        return Platform.mapsAsMapSortedSet(sortedset, function);
    }

    static <K, V> SortedMap<K, V> asMapSortedIgnoreNavigable(SortedSet<K> sortedset, Function<? super K, V> function) {
        return new Maps.SortedAsMapView<>(sortedset, function);
    }

    @Beta
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> navigableset, Function<? super K, V> function) {
        return new Maps.NavigableAsMapView<>(navigableset, function);
    }

    static <K, V> Iterator<Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
        return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
            @Override
            Entry<K, V> transform(K k0) {
                return Maps.immutableEntry(k0, function.apply(k0));
            }
        };
    }

    private static <E> Set<E> removeOnlySet(final Set<E> set) {
        return new ForwardingSet<E>() {
            @Override
            protected Set<E> delegate() {
                return set;
            }

            @Override
            public boolean add(E e0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> sortedset) {
        return new ForwardingSortedSet<E>() {
            @Override
            protected SortedSet<E> delegate() {
                return sortedset;
            }

            @Override
            public boolean add(E e0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SortedSet<E> headSet(E e0) {
                return Maps.removeOnlySortedSet(super.headSet(e0));
            }

            @Override
            public SortedSet<E> subSet(E e0, E e1) {
                return Maps.removeOnlySortedSet(super.subSet(e0, e1));
            }

            @Override
            public SortedSet<E> tailSet(E e0) {
                return Maps.removeOnlySortedSet(super.tailSet(e0));
            }
        };
    }

    @GwtIncompatible("NavigableSet")
    private static <E> NavigableSet<E> removeOnlyNavigableSet(final NavigableSet<E> navigableset) {
        return new ForwardingNavigableSet<E>() {
            @Override
            protected NavigableSet<E> delegate() {
                return navigableset;
            }

            @Override
            public boolean add(E e0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SortedSet<E> headSet(E e0) {
                return Maps.removeOnlySortedSet(super.headSet(e0));
            }

            @Override
            public SortedSet<E> subSet(E e0, E e1) {
                return Maps.removeOnlySortedSet(super.subSet(e0, e1));
            }

            @Override
            public SortedSet<E> tailSet(E e0) {
                return Maps.removeOnlySortedSet(super.tailSet(e0));
            }

            @Override
            public NavigableSet<E> headSet(E e0, boolean flag) {
                return Maps.removeOnlyNavigableSet(super.headSet(e0, flag));
            }

            @Override
            public NavigableSet<E> tailSet(E e0, boolean flag) {
                return Maps.removeOnlyNavigableSet(super.tailSet(e0, flag));
            }

            @Override
            public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
                return Maps.removeOnlyNavigableSet(super.subSet(e0, flag, e1, flag1));
            }

            @Override
            public NavigableSet<E> descendingSet() {
                return Maps.removeOnlyNavigableSet(super.descendingSet());
            }
        };
    }

    @Beta
    public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> iterable, Function<? super K, V> function) {
        return toMap(iterable.iterator(), function);
    }

    @Beta
    public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> iterator, Function<? super K, V> function) {
        Preconditions.checkNotNull(function);
        LinkedHashMap linkedhashmap = newLinkedHashMap();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            linkedhashmap.put(object, function.apply(object));
        }

        return ImmutableMap.copyOf(linkedhashmap);
    }

    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> iterable, Function<? super V, K> function) {
        return uniqueIndex(iterable.iterator(), function);
    }

    public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> iterator, Function<? super V, K> function) {
        Preconditions.checkNotNull(function);
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            immutablemap_builder.put(function.apply(object), object);
        }

        return immutablemap_builder.build();
    }

    @GwtIncompatible("java.util.Properties")
    public static ImmutableMap<String, String> fromProperties(Properties properties) {
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        Enumeration enumeration = properties.propertyNames();

        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();

            immutablemap_builder.put(s, properties.getProperty(s));
        }

        return immutablemap_builder.build();
    }

    @GwtCompatible(serializable = true)
    public static <K, V> Entry<K, V> immutableEntry(@Nullable K k0, @Nullable V v0) {
        return new ImmutableEntry<>(k0, v0);
    }

    static <K, V> Set<Entry<K, V>> unmodifiableEntrySet(Set<Entry<K, V>> set) {
        return new Maps.UnmodifiableEntrySet<>(Collections.unmodifiableSet(set));
    }

    static <K, V> Entry<K, V> unmodifiableEntry(final Entry<? extends K, ? extends V> entry) {
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V>() {
            @Override
            public K getKey() {
                return entry.getKey();
            }

            @Override
            public V getValue() {
                return entry.getValue();
            }
        };
    }

    @Beta
    public static <A, B> Converter<A, B> asConverter(BiMap<A, B> bimap) {
        return new Maps.BiMapConverter<>(bimap);
    }

    public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
        return Synchronized.biMap(bimap, (Object) null);
    }

    public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
        return new Maps.UnmodifiableBiMap<>(bimap, (BiMap) null);
    }

    public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> map, Function<? super V1, V2> function) {
        return transformEntries(map, asEntryTransformer(function));
    }

    public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> sortedmap, Function<? super V1, V2> function) {
        return transformEntries(sortedmap, asEntryTransformer(function));
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> navigablemap, Function<? super V1, V2> function) {
        return transformEntries(navigablemap, asEntryTransformer(function));
    }

    public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> map, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return (Map) (map instanceof SortedMap ? transformEntries((SortedMap) map, maps_entrytransformer) : new Maps.TransformedEntriesMap<>(map, maps_entrytransformer));
    }

    public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> sortedmap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return Platform.mapsTransformEntriesSortedMap(sortedmap, maps_entrytransformer);
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> navigablemap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return new Maps.TransformedEntriesNavigableMap<>(navigablemap, maps_entrytransformer);
    }

    static <K, V1, V2> SortedMap<K, V2> transformEntriesIgnoreNavigable(SortedMap<K, V1> sortedmap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return new Maps.TransformedEntriesSortedMap<>(sortedmap, maps_entrytransformer);
    }

    static <K, V1, V2> Maps.EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        return new Maps.EntryTransformer<K, V1, V2>() {
            @Override
            public V2 transformEntry(K k0, V1 v1) {
                return function.apply(v1);
            }
        };
    }

    static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final Maps.EntryTransformer<? super K, V1, V2> maps_entrytransformer, final K k0) {
        Preconditions.checkNotNull(maps_entrytransformer);
        return new Function<V1, V2>() {
            @Override
            public V2 apply(@Nullable V1 v1) {
                return maps_entrytransformer.transformEntry(k0, v1);
            }
        };
    }

    static <K, V1, V2> Function<Entry<K, V1>, V2> asEntryToValueFunction(final Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        Preconditions.checkNotNull(maps_entrytransformer);
        return new Function<Entry<K, V1>, V2>() {
            public V2 apply(Entry<K, V1> entry) {
                return maps_entrytransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <V2, K, V1> Entry<K, V2> transformEntry(final Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer, final Entry<K, V1> entry) {
        Preconditions.checkNotNull(maps_entrytransformer);
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V2>() {
            @Override
            public K getKey() {
                return entry.getKey();
            }

            @Override
            public V2 getValue() {
                return maps_entrytransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }

    static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> asEntryToEntryFunction(final Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        Preconditions.checkNotNull(maps_entrytransformer);
        return new Function<Entry<K, V1>, Entry<K, V2>>() {
            public Entry<K, V2> apply(Entry<K, V1> entry) {
                return Maps.transformEntry(maps_entrytransformer, entry);
            }
        };
    }

    static <K> Predicate<Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> predicate) {
        return Predicates.compose(predicate, keyFunction());
    }

    static <V> Predicate<Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> predicate) {
        return Predicates.compose(predicate, valueFunction());
    }

    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, Predicate<? super K> predicate) {
        if (map instanceof SortedMap) {
            return filterKeys((SortedMap) map, predicate);
        } else if (map instanceof BiMap) {
            return filterKeys((BiMap) map, predicate);
        } else {
            Preconditions.checkNotNull(predicate);
            Predicate predicate1 = keyPredicateOnEntries(predicate);

            return (Map) (map instanceof Maps.AbstractFilteredMap ? filterFiltered((Maps.AbstractFilteredMap) map, predicate1) : new Maps.FilteredKeyMap<>((Map) Preconditions.checkNotNull(map), predicate, predicate1));
        }
    }

    public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> sortedmap, Predicate<? super K> predicate) {
        return filterEntries(sortedmap, keyPredicateOnEntries(predicate));
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> navigablemap, Predicate<? super K> predicate) {
        return filterEntries(navigablemap, keyPredicateOnEntries(predicate));
    }

    public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> bimap, Predicate<? super K> predicate) {
        Preconditions.checkNotNull(predicate);
        return filterEntries(bimap, keyPredicateOnEntries(predicate));
    }

    public static <K, V> Map<K, V> filterValues(Map<K, V> map, Predicate<? super V> predicate) {
        return (Map) (map instanceof SortedMap ? filterValues((SortedMap) map, predicate) : (map instanceof BiMap ? filterValues((BiMap) map, predicate) : filterEntries(map, valuePredicateOnEntries(predicate))));
    }

    public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> sortedmap, Predicate<? super V> predicate) {
        return filterEntries(sortedmap, valuePredicateOnEntries(predicate));
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> navigablemap, Predicate<? super V> predicate) {
        return filterEntries(navigablemap, valuePredicateOnEntries(predicate));
    }

    public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> bimap, Predicate<? super V> predicate) {
        return filterEntries(bimap, valuePredicateOnEntries(predicate));
    }

    public static <K, V> Map<K, V> filterEntries(Map<K, V> map, Predicate<? super Entry<K, V>> predicate) {
        if (map instanceof SortedMap) {
            return filterEntries((SortedMap) map, predicate);
        } else if (map instanceof BiMap) {
            return filterEntries((BiMap) map, predicate);
        } else {
            Preconditions.checkNotNull(predicate);
            return (Map) (map instanceof Maps.AbstractFilteredMap ? filterFiltered((Maps.AbstractFilteredMap) map, predicate) : new Maps.FilteredEntryMap<>((Map) Preconditions.checkNotNull(map), predicate));
        }
    }

    public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> sortedmap, Predicate<? super Entry<K, V>> predicate) {
        return Platform.mapsFilterSortedMap(sortedmap, predicate);
    }

    static <K, V> SortedMap<K, V> filterSortedIgnoreNavigable(SortedMap<K, V> sortedmap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        return (SortedMap) (sortedmap instanceof Maps.FilteredEntrySortedMap ? filterFiltered((Maps.FilteredEntrySortedMap) sortedmap, predicate) : new Maps.FilteredEntrySortedMap<>((SortedMap) Preconditions.checkNotNull(sortedmap), predicate));
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> navigablemap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        return (NavigableMap) (navigablemap instanceof Maps.FilteredEntryNavigableMap ? filterFiltered((Maps.FilteredEntryNavigableMap) navigablemap, predicate) : new Maps.FilteredEntryNavigableMap<>((NavigableMap) Preconditions.checkNotNull(navigablemap), predicate));
    }

    public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> bimap, Predicate<? super Entry<K, V>> predicate) {
        Preconditions.checkNotNull(bimap);
        Preconditions.checkNotNull(predicate);
        return (BiMap) (bimap instanceof Maps.FilteredEntryBiMap ? filterFiltered((Maps.FilteredEntryBiMap) bimap, predicate) : new Maps.FilteredEntryBiMap<>(bimap, predicate));
    }

    private static <K, V> Map<K, V> filterFiltered(Maps.AbstractFilteredMap<K, V> maps_abstractfilteredmap, Predicate<? super Entry<K, V>> predicate) {
        return new Maps.FilteredEntryMap<>(maps_abstractfilteredmap.unfiltered, Predicates.and(maps_abstractfilteredmap.predicate, predicate));
    }

    private static <K, V> SortedMap<K, V> filterFiltered(Maps.FilteredEntrySortedMap<K, V> maps_filteredentrysortedmap, Predicate<? super Entry<K, V>> predicate) {
        Predicate predicate1 = Predicates.and(maps_filteredentrysortedmap.predicate, predicate);

        return new Maps.FilteredEntrySortedMap<>(maps_filteredentrysortedmap.sortedMap(), predicate1);
    }

    @GwtIncompatible("NavigableMap")
    private static <K, V> NavigableMap<K, V> filterFiltered(Maps.FilteredEntryNavigableMap<K, V> maps_filteredentrynavigablemap, Predicate<? super Entry<K, V>> predicate) {
        Predicate predicate1 = Predicates.and(maps_filteredentrynavigablemap.entryPredicate, predicate);

        return new Maps.FilteredEntryNavigableMap<>(maps_filteredentrynavigablemap.unfiltered, predicate1);
    }

    private static <K, V> BiMap<K, V> filterFiltered(Maps.FilteredEntryBiMap<K, V> maps_filteredentrybimap, Predicate<? super Entry<K, V>> predicate) {
        Predicate predicate1 = Predicates.and(maps_filteredentrybimap.predicate, predicate);

        return new Maps.FilteredEntryBiMap<>(maps_filteredentrybimap.unfiltered(), predicate1);
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, V> navigablemap) {
        Preconditions.checkNotNull(navigablemap);
        return (NavigableMap) (navigablemap instanceof Maps.UnmodifiableNavigableMap ? navigablemap : new Maps.UnmodifiableNavigableMap<>(navigablemap));
    }

    @Nullable
    private static <K, V> Entry<K, V> unmodifiableOrNull(@Nullable Entry<K, V> entry) {
        return entry == null ? null : unmodifiableEntry(entry);
    }

    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigablemap) {
        return Synchronized.navigableMap(navigablemap);
    }

    static <V> V safeGet(Map<?, V> map, @Nullable Object object) {
        Preconditions.checkNotNull(map);

        try {
            return map.get(object);
        } catch (ClassCastException classcastexception) {
            return null;
        } catch (NullPointerException nullpointerexception) {
            return null;
        }
    }

    static boolean safeContainsKey(Map<?, ?> map, Object object) {
        Preconditions.checkNotNull(map);

        try {
            return map.containsKey(object);
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    static <V> V safeRemove(Map<?, V> map, Object object) {
        Preconditions.checkNotNull(map);

        try {
            return map.remove(object);
        } catch (ClassCastException classcastexception) {
            return null;
        } catch (NullPointerException nullpointerexception) {
            return null;
        }
    }

    static boolean containsKeyImpl(Map<?, ?> map, @Nullable Object object) {
        return Iterators.contains(keyIterator(map.entrySet().iterator()), object);
    }

    static boolean containsValueImpl(Map<?, ?> map, @Nullable Object object) {
        return Iterators.contains(valueIterator(map.entrySet().iterator()), object);
    }

    static <K, V> boolean containsEntryImpl(Collection<Entry<K, V>> collection, Object object) {
        return !(object instanceof Entry) ? false : collection.contains(unmodifiableEntry((Entry) object));
    }

    static <K, V> boolean removeEntryImpl(Collection<Entry<K, V>> collection, Object object) {
        return !(object instanceof Entry) ? false : collection.remove(unmodifiableEntry((Entry) object));
    }

    static boolean equalsImpl(Map<?, ?> map, Object object) {
        if (map == object) {
            return true;
        } else if (object instanceof Map) {
            Map map1 = (Map) object;

            return map.entrySet().equals(map1.entrySet());
        } else {
            return false;
        }
    }

    static String toStringImpl(Map<?, ?> map) {
        StringBuilder stringbuilder = Collections2.newStringBuilderForCollection(map.size()).append('{');

        Maps.STANDARD_JOINER.appendTo(stringbuilder, map);
        return stringbuilder.append('}').toString();
    }

    static <K, V> void putAllImpl(Map<K, V> map, Map<? extends K, ? extends V> map1) {
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            map.put(entry.getKey(), entry.getValue());
        }

    }

    @Nullable
    static <K> K keyOrNull(@Nullable Entry<K, ?> entry) {
        return entry == null ? null : entry.getKey();
    }

    @Nullable
    static <V> V valueOrNull(@Nullable Entry<?, V> entry) {
        return entry == null ? null : entry.getValue();
    }

    @GwtIncompatible("NavigableMap")
    abstract static class DescendingMap<K, V> extends ForwardingMap<K, V> implements NavigableMap<K, V> {

        private transient Comparator<? super K> comparator;
        private transient Set<Entry<K, V>> entrySet;
        private transient NavigableSet<K> navigableKeySet;

        DescendingMap() {}

        abstract NavigableMap<K, V> forward();

        @Override
        protected final Map<K, V> delegate() {
            return this.forward();
        }

        public Comparator<? super K> comparator() {
            Comparator comparator = this.comparator;

            if (comparator == null) {
                Object object = this.forward().comparator();

                if (object == null) {
                    object = Ordering.natural();
                }

                comparator = this.comparator = reverse((Comparator) object);
            }

            return comparator;
        }

        private static <T> Ordering<T> reverse(Comparator<T> comparator) {
            return Ordering.from(comparator).reverse();
        }

        public K firstKey() {
            return this.forward().lastKey();
        }

        public K lastKey() {
            return this.forward().firstKey();
        }

        public Entry<K, V> lowerEntry(K k0) {
            return this.forward().higherEntry(k0);
        }

        public K lowerKey(K k0) {
            return this.forward().higherKey(k0);
        }

        public Entry<K, V> floorEntry(K k0) {
            return this.forward().ceilingEntry(k0);
        }

        public K floorKey(K k0) {
            return this.forward().ceilingKey(k0);
        }

        public Entry<K, V> ceilingEntry(K k0) {
            return this.forward().floorEntry(k0);
        }

        public K ceilingKey(K k0) {
            return this.forward().floorKey(k0);
        }

        public Entry<K, V> higherEntry(K k0) {
            return this.forward().lowerEntry(k0);
        }

        public K higherKey(K k0) {
            return this.forward().lowerKey(k0);
        }

        public Entry<K, V> firstEntry() {
            return this.forward().lastEntry();
        }

        public Entry<K, V> lastEntry() {
            return this.forward().firstEntry();
        }

        public Entry<K, V> pollFirstEntry() {
            return this.forward().pollLastEntry();
        }

        public Entry<K, V> pollLastEntry() {
            return this.forward().pollFirstEntry();
        }

        public NavigableMap<K, V> descendingMap() {
            return this.forward();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            Set set = this.entrySet;

            return set == null ? (this.entrySet = this.createEntrySet()) : set;
        }

        abstract Iterator<Entry<K, V>> entryIterator();

        Set<Entry<K, V>> createEntrySet() {
            return new Maps.EntrySet<K, V>() {
                @Override
                Map<K, V> map() {
                    return DescendingMap.this;
                }

                public Iterator<Entry<K, V>> iterator() {
                    return DescendingMap.this.entryIterator();
                }
            };
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            NavigableSet navigableset = this.navigableKeySet;

            return navigableset == null ? (this.navigableKeySet = new Maps.NavigableKeySet<>(this)) : navigableset;
        }

        public NavigableSet<K> descendingKeySet() {
            return this.forward().navigableKeySet();
        }

        public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return this.forward().subMap(k1, flag1, k0, flag).descendingMap();
        }

        public NavigableMap<K, V> headMap(K k0, boolean flag) {
            return this.forward().tailMap(k0, flag).descendingMap();
        }

        public NavigableMap<K, V> tailMap(K k0, boolean flag) {
            return this.forward().headMap(k0, flag).descendingMap();
        }

        public SortedMap<K, V> subMap(K k0, K k1) {
            return this.subMap(k0, true, k1, false);
        }

        public SortedMap<K, V> headMap(K k0) {
            return this.headMap(k0, false);
        }

        public SortedMap<K, V> tailMap(K k0) {
            return this.tailMap(k0, true);
        }

        @Override
        public Collection<V> values() {
            return new Maps.Values<>(this);
        }

        @Override
        public String toString() {
            return this.standardToString();
        }
    }

    abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Entry<K, V>> {

        EntrySet() {}

        abstract Map<K, V> map();

        public int size() {
            return this.map().size();
        }

        public void clear() {
            this.map().clear();
        }

        public boolean contains(Object object) {
            if (!(object instanceof Entry)) {
                return false;
            } else {
                Entry entry = (Entry) object;
                Object object1 = entry.getKey();
                Object object2 = Maps.safeGet(this.map(), object1);

                return Objects.equal(object2, entry.getValue()) && (object2 != null || this.map().containsKey(object1));
            }
        }

        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        public boolean remove(Object object) {
            if (this.contains(object)) {
                Entry entry = (Entry) object;

                return this.map().keySet().remove(entry.getKey());
            } else {
                return false;
            }
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unsupportedoperationexception) {
                return Sets.removeAllImpl(this, collection.iterator());
            }
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unsupportedoperationexception) {
                HashSet hashset = Sets.newHashSetWithExpectedSize(collection.size());
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    if (this.contains(object)) {
                        Entry entry = (Entry) object;

                        hashset.add(entry.getKey());
                    }
                }

                return this.map().keySet().retainAll(hashset);
            }
        }
    }

    static class Values<K, V> extends AbstractCollection<V> {

        final Map<K, V> map;

        Values(Map<K, V> map) {
            this.map = (Map) Preconditions.checkNotNull(map);
        }

        final Map<K, V> map() {
            return this.map;
        }

        public Iterator<V> iterator() {
            return Maps.valueIterator(this.map().entrySet().iterator());
        }

        public boolean remove(Object object) {
            try {
                return super.remove(object);
            } catch (UnsupportedOperationException unsupportedoperationexception) {
                Iterator iterator = this.map().entrySet().iterator();

                Entry entry;

                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entry = (Entry) iterator.next();
                } while (!Objects.equal(object, entry.getValue()));

                this.map().remove(entry.getKey());
                return true;
            }
        }

        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unsupportedoperationexception) {
                HashSet hashset = Sets.newHashSet();
                Iterator iterator = this.map().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (collection.contains(entry.getValue())) {
                        hashset.add(entry.getKey());
                    }
                }

                return this.map().keySet().removeAll(hashset);
            }
        }

        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException unsupportedoperationexception) {
                HashSet hashset = Sets.newHashSet();
                Iterator iterator = this.map().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (collection.contains(entry.getValue())) {
                        hashset.add(entry.getKey());
                    }
                }

                return this.map().keySet().retainAll(hashset);
            }
        }

        public int size() {
            return this.map().size();
        }

        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        public boolean contains(@Nullable Object object) {
            return this.map().containsValue(object);
        }

        public void clear() {
            this.map().clear();
        }
    }

    @GwtIncompatible("NavigableMap")
    static class NavigableKeySet<K, V> extends Maps.SortedKeySet<K, V> implements NavigableSet<K> {

        NavigableKeySet(NavigableMap<K, V> navigablemap) {
            super((SortedMap) navigablemap);
        }

        @Override
        NavigableMap<K, V> map() {
            return (NavigableMap) this.map;
        }

        public K lower(K k0) {
            return this.map().lowerKey(k0);
        }

        public K floor(K k0) {
            return this.map().floorKey(k0);
        }

        public K ceiling(K k0) {
            return this.map().ceilingKey(k0);
        }

        public K higher(K k0) {
            return this.map().higherKey(k0);
        }

        public K pollFirst() {
            return Maps.keyOrNull(this.map().pollFirstEntry());
        }

        public K pollLast() {
            return Maps.keyOrNull(this.map().pollLastEntry());
        }

        public NavigableSet<K> descendingSet() {
            return this.map().descendingKeySet();
        }

        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }

        public NavigableSet<K> subSet(K k0, boolean flag, K k1, boolean flag1) {
            return this.map().subMap(k0, flag, k1, flag1).navigableKeySet();
        }

        public NavigableSet<K> headSet(K k0, boolean flag) {
            return this.map().headMap(k0, flag).navigableKeySet();
        }

        public NavigableSet<K> tailSet(K k0, boolean flag) {
            return this.map().tailMap(k0, flag).navigableKeySet();
        }

        @Override
        public SortedSet<K> subSet(K k0, K k1) {
            return this.subSet(k0, true, k1, false);
        }

        @Override
        public SortedSet<K> headSet(K k0) {
            return this.headSet(k0, false);
        }

        @Override
        public SortedSet<K> tailSet(K k0) {
            return this.tailSet(k0, true);
        }
    }

    static class SortedKeySet<K, V> extends Maps.KeySet<K, V> implements SortedSet<K> {

        SortedKeySet(SortedMap<K, V> sortedmap) {
            super(sortedmap);
        }

        @Override
        SortedMap<K, V> map() {
            return (SortedMap) super.map();
        }

        public Comparator<? super K> comparator() {
            return this.map().comparator();
        }

        public SortedSet<K> subSet(K k0, K k1) {
            return new Maps.SortedKeySet<>(this.map().subMap(k0, k1));
        }

        public SortedSet<K> headSet(K k0) {
            return new Maps.SortedKeySet<>(this.map().headMap(k0));
        }

        public SortedSet<K> tailSet(K k0) {
            return new Maps.SortedKeySet<>(this.map().tailMap(k0));
        }

        public K first() {
            return this.map().firstKey();
        }

        public K last() {
            return this.map().lastKey();
        }
    }

    static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {

        final Map<K, V> map;

        KeySet(Map<K, V> map) {
            this.map = (Map) Preconditions.checkNotNull(map);
        }

        Map<K, V> map() {
            return this.map;
        }

        public Iterator<K> iterator() {
            return Maps.keyIterator(this.map().entrySet().iterator());
        }

        public int size() {
            return this.map().size();
        }

        public boolean isEmpty() {
            return this.map().isEmpty();
        }

        public boolean contains(Object object) {
            return this.map().containsKey(object);
        }

        public boolean remove(Object object) {
            if (this.contains(object)) {
                this.map().remove(object);
                return true;
            } else {
                return false;
            }
        }

        public void clear() {
            this.map().clear();
        }
    }

    @GwtCompatible
    abstract static class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {

        private transient Set<Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private transient Collection<V> values;

        ImprovedAbstractMap() {}

        abstract Set<Entry<K, V>> createEntrySet();

        public Set<Entry<K, V>> entrySet() {
            Set set = this.entrySet;

            return set == null ? (this.entrySet = this.createEntrySet()) : set;
        }

        public Set<K> keySet() {
            Set set = this.keySet;

            return set == null ? (this.keySet = this.createKeySet()) : set;
        }

        Set<K> createKeySet() {
            return new Maps.KeySet<>(this);
        }

        public Collection<V> values() {
            Collection collection = this.values;

            return collection == null ? (this.values = this.createValues()) : collection;
        }

        Collection<V> createValues() {
            return new Maps.Values<>(this);
        }
    }

    @GwtIncompatible("NavigableMap")
    static class UnmodifiableNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V>, Serializable {

        private final NavigableMap<K, V> delegate;
        private transient Maps.UnmodifiableNavigableMap<K, V> descendingMap;

        UnmodifiableNavigableMap(NavigableMap<K, V> navigablemap) {
            this.delegate = navigablemap;
        }

        UnmodifiableNavigableMap(NavigableMap<K, V> navigablemap, Maps.UnmodifiableNavigableMap<K, V> maps_unmodifiablenavigablemap) {
            this.delegate = navigablemap;
            this.descendingMap = maps_unmodifiablenavigablemap;
        }

        @Override
        protected SortedMap<K, V> delegate() {
            return Collections.unmodifiableSortedMap(this.delegate);
        }

        public Entry<K, V> lowerEntry(K k0) {
            return Maps.unmodifiableOrNull(this.delegate.lowerEntry(k0));
        }

        public K lowerKey(K k0) {
            return this.delegate.lowerKey(k0);
        }

        public Entry<K, V> floorEntry(K k0) {
            return Maps.unmodifiableOrNull(this.delegate.floorEntry(k0));
        }

        public K floorKey(K k0) {
            return this.delegate.floorKey(k0);
        }

        public Entry<K, V> ceilingEntry(K k0) {
            return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(k0));
        }

        public K ceilingKey(K k0) {
            return this.delegate.ceilingKey(k0);
        }

        public Entry<K, V> higherEntry(K k0) {
            return Maps.unmodifiableOrNull(this.delegate.higherEntry(k0));
        }

        public K higherKey(K k0) {
            return this.delegate.higherKey(k0);
        }

        public Entry<K, V> firstEntry() {
            return Maps.unmodifiableOrNull(this.delegate.firstEntry());
        }

        public Entry<K, V> lastEntry() {
            return Maps.unmodifiableOrNull(this.delegate.lastEntry());
        }

        public final Entry<K, V> pollFirstEntry() {
            throw new UnsupportedOperationException();
        }

        public final Entry<K, V> pollLastEntry() {
            throw new UnsupportedOperationException();
        }

        public NavigableMap<K, V> descendingMap() {
            Maps.UnmodifiableNavigableMap maps_unmodifiablenavigablemap = this.descendingMap;

            return maps_unmodifiablenavigablemap == null ? (this.descendingMap = new Maps.UnmodifiableNavigableMap<>(this.delegate.descendingMap(), this)) : maps_unmodifiablenavigablemap;
        }

        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
        }

        public NavigableSet<K> descendingKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
        }

        @Override
        public SortedMap<K, V> subMap(K k0, K k1) {
            return this.subMap(k0, true, k1, false);
        }

        @Override
        public SortedMap<K, V> headMap(K k0) {
            return this.headMap(k0, false);
        }

        @Override
        public SortedMap<K, V> tailMap(K k0) {
            return this.tailMap(k0, true);
        }

        public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return Maps.unmodifiableNavigableMap(this.delegate.subMap(k0, flag, k1, flag1));
        }

        public NavigableMap<K, V> headMap(K k0, boolean flag) {
            return Maps.unmodifiableNavigableMap(this.delegate.headMap(k0, flag));
        }

        public NavigableMap<K, V> tailMap(K k0, boolean flag) {
            return Maps.unmodifiableNavigableMap(this.delegate.tailMap(k0, flag));
        }
    }

    static final class FilteredEntryBiMap<K, V> extends Maps.FilteredEntryMap<K, V> implements BiMap<K, V> {

        private final BiMap<V, K> inverse;

        private static <K, V> Predicate<Entry<V, K>> inversePredicate(final Predicate<? super Entry<K, V>> predicate) {
            return new Predicate<Entry<V, K>>() {
                public boolean apply(Entry<V, K> entry) {
                    return predicate.apply(Maps.immutableEntry(entry.getValue(), entry.getKey()));
                }
            };
        }

        FilteredEntryBiMap(BiMap<K, V> bimap, Predicate<? super Entry<K, V>> predicate) {
            super(bimap, predicate);
            this.inverse = new Maps.FilteredEntryBiMap<>(bimap.inverse(), inversePredicate(predicate), this);
        }

        private FilteredEntryBiMap(BiMap<K, V> bimap, Predicate<? super Entry<K, V>> predicate, BiMap<V, K> bimap1) {
            super(bimap, predicate);
            this.inverse = bimap1;
        }

        BiMap<K, V> unfiltered() {
            return (BiMap) this.unfiltered;
        }

        @Override
        public V forcePut(@Nullable K k0, @Nullable V v0) {
            Preconditions.checkArgument(this.apply(k0, v0));
            return this.unfiltered().forcePut(k0, v0);
        }

        @Override
        public BiMap<V, K> inverse() {
            return this.inverse;
        }

        @Override
        public Set<V> values() {
            return this.inverse.keySet();
        }
    }

    @GwtIncompatible("NavigableMap")
    private static class FilteredEntryNavigableMap<K, V> extends AbstractNavigableMap<K, V> {

        private final NavigableMap<K, V> unfiltered;
        private final Predicate<? super Entry<K, V>> entryPredicate;
        private final Map<K, V> filteredDelegate;

        FilteredEntryNavigableMap(NavigableMap<K, V> navigablemap, Predicate<? super Entry<K, V>> predicate) {
            this.unfiltered = (NavigableMap) Preconditions.checkNotNull(navigablemap);
            this.entryPredicate = predicate;
            this.filteredDelegate = new Maps.FilteredEntryMap<>(navigablemap, predicate);
        }

        public Comparator<? super K> comparator() {
            return this.unfiltered.comparator();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return new Maps.NavigableKeySet<K, V>(this) {
                @Override
                public boolean removeAll(Collection<?> collection) {
                    return Iterators.removeIf(FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, Maps.keyPredicateOnEntries(Predicates.in(collection))));
                }

                @Override
                public boolean retainAll(Collection<?> collection) {
                    return Iterators.removeIf(FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(collection)))));
                }
            };
        }

        public Collection<V> values() {
            return new Maps.FilteredMapValues<>(this, this.unfiltered, this.entryPredicate);
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return Iterators.filter(this.unfiltered.entrySet().iterator(), this.entryPredicate);
        }

        @Override
        Iterator<Entry<K, V>> descendingEntryIterator() {
            return Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), this.entryPredicate);
        }

        @Override
        public int size() {
            return this.filteredDelegate.size();
        }

        @Nullable
        @Override
        public V get(@Nullable Object object) {
            return this.filteredDelegate.get(object);
        }

        public boolean containsKey(@Nullable Object object) {
            return this.filteredDelegate.containsKey(object);
        }

        public V put(K k0, V v0) {
            return this.filteredDelegate.put(k0, v0);
        }

        public V remove(@Nullable Object object) {
            return this.filteredDelegate.remove(object);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            this.filteredDelegate.putAll(map);
        }

        public void clear() {
            this.filteredDelegate.clear();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return this.filteredDelegate.entrySet();
        }

        @Override
        public Entry<K, V> pollFirstEntry() {
            return (Entry) Iterables.removeFirstMatching(this.unfiltered.entrySet(), this.entryPredicate);
        }

        @Override
        public Entry<K, V> pollLastEntry() {
            return (Entry) Iterables.removeFirstMatching(this.unfiltered.descendingMap().entrySet(), this.entryPredicate);
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
        }

        public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return Maps.filterEntries(this.unfiltered.subMap(k0, flag, k1, flag1), this.entryPredicate);
        }

        public NavigableMap<K, V> headMap(K k0, boolean flag) {
            return Maps.filterEntries(this.unfiltered.headMap(k0, flag), this.entryPredicate);
        }

        public NavigableMap<K, V> tailMap(K k0, boolean flag) {
            return Maps.filterEntries(this.unfiltered.tailMap(k0, flag), this.entryPredicate);
        }
    }

    private static class FilteredEntrySortedMap<K, V> extends Maps.FilteredEntryMap<K, V> implements SortedMap<K, V> {

        FilteredEntrySortedMap(SortedMap<K, V> sortedmap, Predicate<? super Entry<K, V>> predicate) {
            super(sortedmap, predicate);
        }

        SortedMap<K, V> sortedMap() {
            return (SortedMap) this.unfiltered;
        }

        @Override
        public SortedSet<K> keySet() {
            return (SortedSet) super.keySet();
        }

        @Override
        SortedSet<K> createKeySet() {
            return new Maps.FilteredEntrySortedMap.SortedKeySet();
        }

        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }

        public K firstKey() {
            return this.keySet().iterator().next();
        }

        public K lastKey() {
            SortedMap sortedmap = this.sortedMap();

            while (true) {
                Object object = sortedmap.lastKey();

                if (this.apply(object, this.unfiltered.get(object))) {
                    return object;
                }

                sortedmap = this.sortedMap().headMap(object);
            }
        }

        public SortedMap<K, V> headMap(K k0) {
            return new Maps.FilteredEntrySortedMap<>(this.sortedMap().headMap(k0), this.predicate);
        }

        public SortedMap<K, V> subMap(K k0, K k1) {
            return new Maps.FilteredEntrySortedMap<>(this.sortedMap().subMap(k0, k1), this.predicate);
        }

        public SortedMap<K, V> tailMap(K k0) {
            return new Maps.FilteredEntrySortedMap<>(this.sortedMap().tailMap(k0), this.predicate);
        }

        class SortedKeySet extends Maps.FilteredEntryMap<K, V>.KeySet implements SortedSet<K> {

            SortedKeySet() {
                super();
            }

            public Comparator<? super K> comparator() {
                return FilteredEntrySortedMap.this.sortedMap().comparator();
            }

            public SortedSet<K> subSet(K k0, K k1) {
                return (SortedSet) FilteredEntrySortedMap.this.subMap(k0, k1).keySet();
            }

            public SortedSet<K> headSet(K k0) {
                return (SortedSet) FilteredEntrySortedMap.this.headMap(k0).keySet();
            }

            public SortedSet<K> tailSet(K k0) {
                return (SortedSet) FilteredEntrySortedMap.this.tailMap(k0).keySet();
            }

            public K first() {
                return FilteredEntrySortedMap.this.firstKey();
            }

            public K last() {
                return FilteredEntrySortedMap.this.lastKey();
            }
        }
    }

    static class FilteredEntryMap<K, V> extends Maps.AbstractFilteredMap<K, V> {

        final Set<Entry<K, V>> filteredEntrySet;

        FilteredEntryMap(Map<K, V> map, Predicate<? super Entry<K, V>> predicate) {
            super(map, predicate);
            this.filteredEntrySet = Sets.filter(map.entrySet(), this.predicate);
        }

        @Override
        protected Set<Entry<K, V>> createEntrySet() {
            return new Maps.FilteredEntryMap.EntrySet();
        }

        @Override
        Set<K> createKeySet() {
            return new Maps.FilteredEntryMap.KeySet();
        }

        class KeySet extends Maps.KeySet<K, V> {

            KeySet() {
                super(FilteredEntryMap.this);
            }

            @Override
            public boolean remove(Object object) {
                if (FilteredEntryMap.this.containsKey(object)) {
                    FilteredEntryMap.this.unfiltered.remove(object);
                    return true;
                } else {
                    return false;
                }
            }

            private boolean removeIf(Predicate<? super K> predicate) {
                return Iterables.removeIf(FilteredEntryMap.this.unfiltered.entrySet(), Predicates.and(FilteredEntryMap.this.predicate, Maps.keyPredicateOnEntries(predicate)));
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return this.removeIf(Predicates.in(collection));
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return this.removeIf(Predicates.not(Predicates.in(collection)));
            }

            public Object[] toArray() {
                return Lists.newArrayList(this.iterator()).toArray();
            }

            public <T> T[] toArray(T[] at) {
                return Lists.newArrayList(this.iterator()).toArray(at);
            }
        }

        private class EntrySet extends ForwardingSet<Entry<K, V>> {

            private EntrySet() {}

            @Override
            protected Set<Entry<K, V>> delegate() {
                return FilteredEntryMap.this.filteredEntrySet;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new TransformedIterator<Entry<K, V>, Entry<K, V>>(FilteredEntryMap.this.filteredEntrySet.iterator()) {
                    Entry<K, V> transform(final Entry<K, V> entry) {
                        return new ForwardingMapEntry<K, V>() {
                            @Override
                            protected Entry<K, V> delegate() {
                                return entry;
                            }

                            @Override
                            public V setValue(V v0) {
                                Preconditions.checkArgument(FilteredEntryMap.this.apply(this.getKey(), v0));
                                return super.setValue(v0);
                            }
                        };
                    }
                };
            }
        }
    }

    private static class FilteredKeyMap<K, V> extends Maps.AbstractFilteredMap<K, V> {

        Predicate<? super K> keyPredicate;

        FilteredKeyMap(Map<K, V> map, Predicate<? super K> predicate, Predicate<? super Entry<K, V>> predicate1) {
            super(map, predicate1);
            this.keyPredicate = predicate;
        }

        @Override
        protected Set<Entry<K, V>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), this.predicate);
        }

        @Override
        Set<K> createKeySet() {
            return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
        }

        @Override
        public boolean containsKey(Object object) {
            return this.unfiltered.containsKey(object) && this.keyPredicate.apply(object);
        }
    }

    private static final class FilteredMapValues<K, V> extends Maps.Values<K, V> {

        Map<K, V> unfiltered;
        Predicate<? super Entry<K, V>> predicate;

        FilteredMapValues(Map<K, V> map, Map<K, V> map1, Predicate<? super Entry<K, V>> predicate) {
            super(map);
            this.unfiltered = map1;
            this.predicate = predicate;
        }

        @Override
        public boolean remove(Object object) {
            return Iterables.removeFirstMatching(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(Predicates.equalTo(object)))) != null;
        }

        private boolean removeIf(Predicate<? super V> predicate) {
            return Iterables.removeIf(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(predicate)));
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.removeIf(Predicates.in(collection));
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.removeIf(Predicates.not(Predicates.in(collection)));
        }

        public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
        }

        public <T> T[] toArray(T[] at) {
            return Lists.newArrayList(this.iterator()).toArray(at);
        }
    }

    private abstract static class AbstractFilteredMap<K, V> extends Maps.ImprovedAbstractMap<K, V> {

        final Map<K, V> unfiltered;
        final Predicate<? super Entry<K, V>> predicate;

        AbstractFilteredMap(Map<K, V> map, Predicate<? super Entry<K, V>> predicate) {
            this.unfiltered = map;
            this.predicate = predicate;
        }

        boolean apply(@Nullable Object object, @Nullable V v0) {
            return this.predicate.apply(Maps.immutableEntry(object, v0));
        }

        public V put(K k0, V v0) {
            Preconditions.checkArgument(this.apply(k0, v0));
            return this.unfiltered.put(k0, v0);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                Preconditions.checkArgument(this.apply(entry.getKey(), entry.getValue()));
            }

            this.unfiltered.putAll(map);
        }

        public boolean containsKey(Object object) {
            return this.unfiltered.containsKey(object) && this.apply(object, this.unfiltered.get(object));
        }

        public V get(Object object) {
            Object object1 = this.unfiltered.get(object);

            return object1 != null && this.apply(object, object1) ? object1 : null;
        }

        public boolean isEmpty() {
            return this.entrySet().isEmpty();
        }

        public V remove(Object object) {
            return this.containsKey(object) ? this.unfiltered.remove(object) : null;
        }

        @Override
        Collection<V> createValues() {
            return new Maps.FilteredMapValues<>(this, this.unfiltered, this.predicate);
        }
    }

    @GwtIncompatible("NavigableMap")
    private static class TransformedEntriesNavigableMap<K, V1, V2> extends Maps.TransformedEntriesSortedMap<K, V1, V2> implements NavigableMap<K, V2> {

        TransformedEntriesNavigableMap(NavigableMap<K, V1> navigablemap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
            super((SortedMap) navigablemap, maps_entrytransformer);
        }

        public Entry<K, V2> ceilingEntry(K k0) {
            return this.transformEntry(this.fromMap().ceilingEntry(k0));
        }

        public K ceilingKey(K k0) {
            return this.fromMap().ceilingKey(k0);
        }

        public NavigableSet<K> descendingKeySet() {
            return this.fromMap().descendingKeySet();
        }

        public NavigableMap<K, V2> descendingMap() {
            return Maps.transformEntries(this.fromMap().descendingMap(), this.transformer);
        }

        public Entry<K, V2> firstEntry() {
            return this.transformEntry(this.fromMap().firstEntry());
        }

        public Entry<K, V2> floorEntry(K k0) {
            return this.transformEntry(this.fromMap().floorEntry(k0));
        }

        public K floorKey(K k0) {
            return this.fromMap().floorKey(k0);
        }

        @Override
        public NavigableMap<K, V2> headMap(K k0) {
            return this.headMap(k0, false);
        }

        public NavigableMap<K, V2> headMap(K k0, boolean flag) {
            return Maps.transformEntries(this.fromMap().headMap(k0, flag), this.transformer);
        }

        public Entry<K, V2> higherEntry(K k0) {
            return this.transformEntry(this.fromMap().higherEntry(k0));
        }

        public K higherKey(K k0) {
            return this.fromMap().higherKey(k0);
        }

        public Entry<K, V2> lastEntry() {
            return this.transformEntry(this.fromMap().lastEntry());
        }

        public Entry<K, V2> lowerEntry(K k0) {
            return this.transformEntry(this.fromMap().lowerEntry(k0));
        }

        public K lowerKey(K k0) {
            return this.fromMap().lowerKey(k0);
        }

        public NavigableSet<K> navigableKeySet() {
            return this.fromMap().navigableKeySet();
        }

        public Entry<K, V2> pollFirstEntry() {
            return this.transformEntry(this.fromMap().pollFirstEntry());
        }

        public Entry<K, V2> pollLastEntry() {
            return this.transformEntry(this.fromMap().pollLastEntry());
        }

        public NavigableMap<K, V2> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return Maps.transformEntries(this.fromMap().subMap(k0, flag, k1, flag1), this.transformer);
        }

        @Override
        public NavigableMap<K, V2> subMap(K k0, K k1) {
            return this.subMap(k0, true, k1, false);
        }

        @Override
        public NavigableMap<K, V2> tailMap(K k0) {
            return this.tailMap(k0, true);
        }

        public NavigableMap<K, V2> tailMap(K k0, boolean flag) {
            return Maps.transformEntries(this.fromMap().tailMap(k0, flag), this.transformer);
        }

        @Nullable
        private Entry<K, V2> transformEntry(@Nullable Entry<K, V1> entry) {
            return entry == null ? null : Maps.transformEntry(this.transformer, entry);
        }

        @Override
        protected NavigableMap<K, V1> fromMap() {
            return (NavigableMap) super.fromMap();
        }
    }

    static class TransformedEntriesSortedMap<K, V1, V2> extends Maps.TransformedEntriesMap<K, V1, V2> implements SortedMap<K, V2> {

        protected SortedMap<K, V1> fromMap() {
            return (SortedMap) this.fromMap;
        }

        TransformedEntriesSortedMap(SortedMap<K, V1> sortedmap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
            super(sortedmap, maps_entrytransformer);
        }

        public Comparator<? super K> comparator() {
            return this.fromMap().comparator();
        }

        public K firstKey() {
            return this.fromMap().firstKey();
        }

        public SortedMap<K, V2> headMap(K k0) {
            return Maps.transformEntries(this.fromMap().headMap(k0), this.transformer);
        }

        public K lastKey() {
            return this.fromMap().lastKey();
        }

        public SortedMap<K, V2> subMap(K k0, K k1) {
            return Maps.transformEntries(this.fromMap().subMap(k0, k1), this.transformer);
        }

        public SortedMap<K, V2> tailMap(K k0) {
            return Maps.transformEntries(this.fromMap().tailMap(k0), this.transformer);
        }
    }

    static class TransformedEntriesMap<K, V1, V2> extends Maps.ImprovedAbstractMap<K, V2> {

        final Map<K, V1> fromMap;
        final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;

        TransformedEntriesMap(Map<K, V1> map, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
            this.fromMap = (Map) Preconditions.checkNotNull(map);
            this.transformer = (Maps.EntryTransformer) Preconditions.checkNotNull(maps_entrytransformer);
        }

        public int size() {
            return this.fromMap.size();
        }

        public boolean containsKey(Object object) {
            return this.fromMap.containsKey(object);
        }

        public V2 get(Object object) {
            Object object1 = this.fromMap.get(object);

            return object1 == null && !this.fromMap.containsKey(object) ? null : this.transformer.transformEntry(object, object1);
        }

        public V2 remove(Object object) {
            return this.fromMap.containsKey(object) ? this.transformer.transformEntry(object, this.fromMap.remove(object)) : null;
        }

        public void clear() {
            this.fromMap.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.fromMap.keySet();
        }

        @Override
        protected Set<Entry<K, V2>> createEntrySet() {
            return new Maps.EntrySet<K, V2>() {
                @Override
                Map<K, V2> map() {
                    return TransformedEntriesMap.this;
                }

                public Iterator<Entry<K, V2>> iterator() {
                    return Iterators.transform(TransformedEntriesMap.this.fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(TransformedEntriesMap.this.transformer));
                }
            };
        }
    }

    public interface EntryTransformer<K, V1, V2> {

        V2 transformEntry(@Nullable K k0, @Nullable V1 v1);
    }

    private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {

        final Map<K, V> unmodifiableMap;
        final BiMap<? extends K, ? extends V> delegate;
        BiMap<V, K> inverse;
        transient Set<V> values;
        private static final long serialVersionUID = 0L;

        UnmodifiableBiMap(BiMap<? extends K, ? extends V> bimap, @Nullable BiMap<V, K> bimap1) {
            this.unmodifiableMap = Collections.unmodifiableMap(bimap);
            this.delegate = bimap;
            this.inverse = bimap1;
        }

        @Override
        protected Map<K, V> delegate() {
            return this.unmodifiableMap;
        }

        @Override
        public V forcePut(K k0, V v0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BiMap<V, K> inverse() {
            BiMap bimap = this.inverse;

            return bimap == null ? (this.inverse = new Maps.UnmodifiableBiMap<>(this.delegate.inverse(), this)) : bimap;
        }

        @Override
        public Set<V> values() {
            Set set = this.values;

            return set == null ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : set;
        }
    }

    private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable {

        private final BiMap<A, B> bimap;
        private static final long serialVersionUID = 0L;

        BiMapConverter(BiMap<A, B> bimap) {
            this.bimap = (BiMap) Preconditions.checkNotNull(bimap);
        }

        @Override
        protected B doForward(A a0) {
            return convert(this.bimap, a0);
        }

        @Override
        protected A doBackward(B b0) {
            return convert(this.bimap.inverse(), b0);
        }

        private static <X, Y> Y convert(BiMap<X, Y> bimap, X x0) {
            Object object = bimap.get(x0);

            Preconditions.checkArgument(object != null, "No non-null mapping present for input: %s", x0);
            return object;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Maps.BiMapConverter) {
                Maps.BiMapConverter maps_bimapconverter = (Maps.BiMapConverter) object;

                return this.bimap.equals(maps_bimapconverter.bimap);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.bimap.hashCode();
        }

        public String toString() {
            return "Maps.asConverter(" + this.bimap + ")";
        }
    }

    static class UnmodifiableEntrySet<K, V> extends Maps.UnmodifiableEntries<K, V> implements Set<Entry<K, V>> {

        UnmodifiableEntrySet(Set<Entry<K, V>> set) {
            super(set);
        }

        public boolean equals(@Nullable Object object) {
            return Sets.equalsImpl(this, object);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    static class UnmodifiableEntries<K, V> extends ForwardingCollection<Entry<K, V>> {

        private final Collection<Entry<K, V>> entries;

        UnmodifiableEntries(Collection<Entry<K, V>> collection) {
            this.entries = collection;
        }

        @Override
        protected Collection<Entry<K, V>> delegate() {
            return this.entries;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            final Iterator iterator = super.iterator();

            return new UnmodifiableIterator<Entry<K, V>>() {
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public Entry<K, V> next() {
                    return Maps.unmodifiableEntry((Entry) iterator.next());
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
    }

    @GwtIncompatible("NavigableMap")
    private static final class NavigableAsMapView<K, V> extends AbstractNavigableMap<K, V> {

        private final NavigableSet<K> set;
        private final Function<? super K, V> function;

        NavigableAsMapView(NavigableSet<K> navigableset, Function<? super K, V> function) {
            this.set = (NavigableSet) Preconditions.checkNotNull(navigableset);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        public NavigableMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
            return Maps.asMap(this.set.subSet(k0, flag, k1, flag1), this.function);
        }

        public NavigableMap<K, V> headMap(K k0, boolean flag) {
            return Maps.asMap(this.set.headSet(k0, flag), this.function);
        }

        public NavigableMap<K, V> tailMap(K k0, boolean flag) {
            return Maps.asMap(this.set.tailSet(k0, flag), this.function);
        }

        public Comparator<? super K> comparator() {
            return this.set.comparator();
        }

        @Nullable
        @Override
        public V get(@Nullable Object object) {
            return Collections2.safeContains(this.set, object) ? this.function.apply(object) : null;
        }

        public void clear() {
            this.set.clear();
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return Maps.asMapEntryIterator(this.set, this.function);
        }

        @Override
        Iterator<Entry<K, V>> descendingEntryIterator() {
            return this.descendingMap().entrySet().iterator();
        }

        @Override
        public NavigableSet<K> navigableKeySet() {
            return Maps.removeOnlyNavigableSet(this.set);
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.asMap(this.set.descendingSet(), this.function);
        }
    }

    private static class SortedAsMapView<K, V> extends Maps.AsMapView<K, V> implements SortedMap<K, V> {

        SortedAsMapView(SortedSet<K> sortedset, Function<? super K, V> function) {
            super(sortedset, function);
        }

        @Override
        SortedSet<K> backingSet() {
            return (SortedSet) super.backingSet();
        }

        public Comparator<? super K> comparator() {
            return this.backingSet().comparator();
        }

        @Override
        public Set<K> keySet() {
            return Maps.removeOnlySortedSet(this.backingSet());
        }

        public SortedMap<K, V> subMap(K k0, K k1) {
            return Maps.asMap(this.backingSet().subSet(k0, k1), this.function);
        }

        public SortedMap<K, V> headMap(K k0) {
            return Maps.asMap(this.backingSet().headSet(k0), this.function);
        }

        public SortedMap<K, V> tailMap(K k0) {
            return Maps.asMap(this.backingSet().tailSet(k0), this.function);
        }

        public K firstKey() {
            return this.backingSet().first();
        }

        public K lastKey() {
            return this.backingSet().last();
        }
    }

    private static class AsMapView<K, V> extends Maps.ImprovedAbstractMap<K, V> {

        private final Set<K> set;
        final Function<? super K, V> function;

        Set<K> backingSet() {
            return this.set;
        }

        AsMapView(Set<K> set, Function<? super K, V> function) {
            this.set = (Set) Preconditions.checkNotNull(set);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        public Set<K> createKeySet() {
            return Maps.removeOnlySet(this.backingSet());
        }

        @Override
        Collection<V> createValues() {
            return Collections2.transform(this.set, this.function);
        }

        public int size() {
            return this.backingSet().size();
        }

        public boolean containsKey(@Nullable Object object) {
            return this.backingSet().contains(object);
        }

        public V get(@Nullable Object object) {
            return Collections2.safeContains(this.backingSet(), object) ? this.function.apply(object) : null;
        }

        public V remove(@Nullable Object object) {
            return this.backingSet().remove(object) ? this.function.apply(object) : null;
        }

        public void clear() {
            this.backingSet().clear();
        }

        @Override
        protected Set<Entry<K, V>> createEntrySet() {
            return new Maps.EntrySet<K, V>() {
                @Override
                Map<K, V> map() {
                    return AsMapView.this;
                }

                public Iterator<Entry<K, V>> iterator() {
                    return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
                }
            };
        }
    }

    static class SortedMapDifferenceImpl<K, V> extends Maps.MapDifferenceImpl<K, V> implements SortedMapDifference<K, V> {

        SortedMapDifferenceImpl(SortedMap<K, V> sortedmap, SortedMap<K, V> sortedmap1, SortedMap<K, V> sortedmap2, SortedMap<K, MapDifference.ValueDifference<V>> sortedmap3) {
            super(sortedmap, sortedmap1, sortedmap2, sortedmap3);
        }

        @Override
        public SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering() {
            return (SortedMap) super.entriesDiffering();
        }

        @Override
        public SortedMap<K, V> entriesInCommon() {
            return (SortedMap) super.entriesInCommon();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnLeft() {
            return (SortedMap) super.entriesOnlyOnLeft();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnRight() {
            return (SortedMap) super.entriesOnlyOnRight();
        }
    }

    static class ValueDifferenceImpl<V> implements MapDifference.ValueDifference<V> {

        private final V left;
        private final V right;

        static <V> MapDifference.ValueDifference<V> create(@Nullable V v0, @Nullable V v1) {
            return new Maps.ValueDifferenceImpl<>(v0, v1);
        }

        private ValueDifferenceImpl(@Nullable V v0, @Nullable V v1) {
            this.left = v0;
            this.right = v1;
        }

        @Override
        public V leftValue() {
            return this.left;
        }

        @Override
        public V rightValue() {
            return this.right;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof MapDifference.ValueDifference)) {
                return false;
            } else {
                MapDifference.ValueDifference mapdifference_valuedifference = (MapDifference.ValueDifference) object;

                return Objects.equal(this.left, mapdifference_valuedifference.leftValue()) && Objects.equal(this.right, mapdifference_valuedifference.rightValue());
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left + ", " + this.right + ")";
        }
    }

    static class MapDifferenceImpl<K, V> implements MapDifference<K, V> {

        final Map<K, V> onlyOnLeft;
        final Map<K, V> onlyOnRight;
        final Map<K, V> onBoth;
        final Map<K, MapDifference.ValueDifference<V>> differences;

        MapDifferenceImpl(Map<K, V> map, Map<K, V> map1, Map<K, V> map2, Map<K, MapDifference.ValueDifference<V>> map3) {
            this.onlyOnLeft = Maps.unmodifiableMap(map);
            this.onlyOnRight = Maps.unmodifiableMap(map1);
            this.onBoth = Maps.unmodifiableMap(map2);
            this.differences = Maps.unmodifiableMap(map3);
        }

        @Override
        public boolean areEqual() {
            return this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty();
        }

        @Override
        public Map<K, V> entriesOnlyOnLeft() {
            return this.onlyOnLeft;
        }

        @Override
        public Map<K, V> entriesOnlyOnRight() {
            return this.onlyOnRight;
        }

        @Override
        public Map<K, V> entriesInCommon() {
            return this.onBoth;
        }

        @Override
        public Map<K, MapDifference.ValueDifference<V>> entriesDiffering() {
            return this.differences;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (!(object instanceof MapDifference)) {
                return false;
            } else {
                MapDifference mapdifference = (MapDifference) object;

                return this.entriesOnlyOnLeft().equals(mapdifference.entriesOnlyOnLeft()) && this.entriesOnlyOnRight().equals(mapdifference.entriesOnlyOnRight()) && this.entriesInCommon().equals(mapdifference.entriesInCommon()) && this.entriesDiffering().equals(mapdifference.entriesDiffering());
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.entriesOnlyOnLeft(), this.entriesOnlyOnRight(), this.entriesInCommon(), this.entriesDiffering());
        }

        public String toString() {
            if (this.areEqual()) {
                return "equal";
            } else {
                StringBuilder stringbuilder = new StringBuilder("not equal");

                if (!this.onlyOnLeft.isEmpty()) {
                    stringbuilder.append(": only on left=").append(this.onlyOnLeft);
                }

                if (!this.onlyOnRight.isEmpty()) {
                    stringbuilder.append(": only on right=").append(this.onlyOnRight);
                }

                if (!this.differences.isEmpty()) {
                    stringbuilder.append(": value differences=").append(this.differences);
                }

                return stringbuilder.toString();
            }
        }
    }

    private static enum EntryFunction implements Function<Entry<?, ?>, Object> {

        KEY {
            @Nullable
            public Object apply(Entry<?, ?> entry) {
                return entry.getKey();
            }
        },
        VALUE {
            @Nullable
            public Object apply(Entry<?, ?> entry) {
                return entry.getValue();
            }
        };

        private EntryFunction() {}
    }
}
