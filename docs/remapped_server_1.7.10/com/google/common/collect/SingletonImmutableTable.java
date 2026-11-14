package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map;

@GwtCompatible
class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {

    final R singleRowKey;
    final C singleColumnKey;
    final V singleValue;

    SingletonImmutableTable(R r0, C c0, V v0) {
        this.singleRowKey = Preconditions.checkNotNull(r0);
        this.singleColumnKey = Preconditions.checkNotNull(c0);
        this.singleValue = Preconditions.checkNotNull(v0);
    }

    SingletonImmutableTable(Table.Cell<R, C, V> table_cell) {
        this(table_cell.getRowKey(), table_cell.getColumnKey(), table_cell.getValue());
    }

    @Override
    public ImmutableMap<R, V> column(C c0) {
        Preconditions.checkNotNull(c0);
        return this.containsColumn(c0) ? ImmutableMap.of(this.singleRowKey, this.singleValue) : ImmutableMap.of();
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
        return ImmutableSet.of(cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
    }

    @Override
    ImmutableCollection<V> createValues() {
        return ImmutableSet.of(this.singleValue);
    }
}
