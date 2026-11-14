package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
public class HashBasedTable<R, C, V> extends StandardTable<R, C, V> {

    private static final long serialVersionUID = 0L;

    public static <R, C, V> HashBasedTable<R, C, V> create() {
        return new HashBasedTable<>(new HashMap(), new HashBasedTable.Factory<>(0));
    }

    public static <R, C, V> HashBasedTable<R, C, V> create(int i, int j) {
        CollectPreconditions.checkNonnegative(j, "expectedCellsPerRow");
        HashMap hashmap = Maps.newHashMapWithExpectedSize(i);

        return new HashBasedTable<>(hashmap, new HashBasedTable.Factory<>(j));
    }

    public static <R, C, V> HashBasedTable<R, C, V> create(Table<? extends R, ? extends C, ? extends V> table) {
        HashBasedTable hashbasedtable = create();

        hashbasedtable.putAll(table);
        return hashbasedtable;
    }

    HashBasedTable(Map<R, Map<C, V>> map, HashBasedTable.Factory<C, V> hashbasedtable_factory) {
        super(map, hashbasedtable_factory);
    }

    @Override
    public boolean contains(@Nullable Object object, @Nullable Object object1) {
        return super.contains(object, object1);
    }

    @Override
    public boolean containsColumn(@Nullable Object object) {
        return super.containsColumn(object);
    }

    @Override
    public boolean containsRow(@Nullable Object object) {
        return super.containsRow(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return super.containsValue(object);
    }

    @Override
    public V get(@Nullable Object object, @Nullable Object object1) {
        return super.get(object, object1);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return super.equals(object);
    }

    @Override
    public V remove(@Nullable Object object, @Nullable Object object1) {
        return super.remove(object, object1);
    }

    private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable {

        final int expectedSize;
        private static final long serialVersionUID = 0L;

        Factory(int i) {
            this.expectedSize = i;
        }

        @Override
        public Map<C, V> get() {
            return Maps.newHashMapWithExpectedSize(this.expectedSize);
        }
    }
}
