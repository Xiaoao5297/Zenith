package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multimap<K, V> {

    int size();

    boolean isEmpty();

    boolean containsKey(@Nullable Object object);

    boolean containsValue(@Nullable Object object);

    boolean containsEntry(@Nullable Object object, @Nullable Object object1);

    boolean put(@Nullable K k0, @Nullable V v0);

    boolean remove(@Nullable Object object, @Nullable Object object1);

    boolean putAll(@Nullable K k0, Iterable<? extends V> iterable);

    boolean putAll(Multimap<? extends K, ? extends V> multimap);

    Collection<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable);

    Collection<V> removeAll(@Nullable Object object);

    void clear();

    Collection<V> get(@Nullable K k0);

    Set<K> keySet();

    Multiset<K> keys();

    Collection<V> values();

    Collection<Entry<K, V>> entries();

    Map<K, Collection<V>> asMap();

    boolean equals(@Nullable Object object);

    int hashCode();
}
