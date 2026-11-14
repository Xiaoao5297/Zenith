package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Table<R, C, V> {

    boolean contains(@Nullable Object object, @Nullable Object object1);

    boolean containsRow(@Nullable Object object);

    boolean containsColumn(@Nullable Object object);

    boolean containsValue(@Nullable Object object);

    V get(@Nullable Object object, @Nullable Object object1);

    boolean isEmpty();

    int size();

    boolean equals(@Nullable Object object);

    int hashCode();

    void clear();

    V put(R r0, C c0, V v0);

    void putAll(Table<? extends R, ? extends C, ? extends V> table);

    V remove(@Nullable Object object, @Nullable Object object1);

    Map<C, V> row(R r0);

    Map<R, V> column(C c0);

    Set<Table.Cell<R, C, V>> cellSet();

    Set<R> rowKeySet();

    Set<C> columnKeySet();

    Collection<V> values();

    Map<R, Map<C, V>> rowMap();

    Map<C, Map<R, V>> columnMap();

    public interface Cell<R, C, V> {

        R getRowKey();

        C getColumnKey();

        V getValue();

        boolean equals(@Nullable Object object);

        int hashCode();
    }
}
