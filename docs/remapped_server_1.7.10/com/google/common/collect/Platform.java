package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(emulated = true)
final class Platform {

    static <T> T[] newArray(T[] at, int i) {
        Class oclass = at.getClass().getComponentType();
        Object[] aobject = (Object[]) ((Object[]) Array.newInstance(oclass, i));

        return aobject;
    }

    static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    static MapMaker tryWeakKeys(MapMaker mapmaker) {
        return mapmaker.weakKeys();
    }

    static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(SortedMap<K, V1> sortedmap, Maps.EntryTransformer<? super K, ? super V1, V2> maps_entrytransformer) {
        return (SortedMap) (sortedmap instanceof NavigableMap ? Maps.transformEntries((NavigableMap) sortedmap, maps_entrytransformer) : Maps.transformEntriesIgnoreNavigable(sortedmap, maps_entrytransformer));
    }

    static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> sortedset, Function<? super K, V> function) {
        return (SortedMap) (sortedset instanceof NavigableSet ? Maps.asMap((NavigableSet) sortedset, function) : Maps.asMapSortedIgnoreNavigable(sortedset, function));
    }

    static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> sortedset, Predicate<? super E> predicate) {
        return (SortedSet) (sortedset instanceof NavigableSet ? Sets.filter((NavigableSet) sortedset, predicate) : Sets.filterSortedIgnoreNavigable(sortedset, predicate));
    }

    static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> sortedmap, Predicate<? super Entry<K, V>> predicate) {
        return (SortedMap) (sortedmap instanceof NavigableMap ? Maps.filterEntries((NavigableMap) sortedmap, predicate) : Maps.filterSortedIgnoreNavigable(sortedmap, predicate));
    }

    private Platform() {}
}
