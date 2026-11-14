package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
public interface RangeMap<K extends Comparable, V> {

    @Nullable
    V get(K k0);

    @Nullable
    Entry<Range<K>, V> getEntry(K k0);

    Range<K> span();

    void put(Range<K> range, V v0);

    void putAll(RangeMap<K, V> rangemap);

    void clear();

    void remove(Range<K> range);

    Map<Range<K>, V> asMapOfRanges();

    RangeMap<K, V> subRangeMap(Range<K> range);

    boolean equals(@Nullable Object object);

    int hashCode();

    String toString();
}
