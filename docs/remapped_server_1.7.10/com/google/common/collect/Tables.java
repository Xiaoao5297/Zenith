package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Tables {

    private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER = new Function<Map<Object, Object>, Map<Object, Object>>() {
        public Map<Object, Object> apply(Map<Object, Object> map) {
            return Collections.unmodifiableMap(map);
        }
    };

    private Tables() {}

    public static <R, C, V> Table.Cell<R, C, V> immutableCell(@Nullable R r0, @Nullable C c0, @Nullable V v0) {
        return new Tables.ImmutableCell<>(r0, c0, v0);
    }

    public static <R, C, V> Table<C, R, V> transpose(Table<R, C, V> table) {
        return (Table) (table instanceof Tables.TransposeTable ? ((Tables.TransposeTable) table).original : new Tables.TransposeTable<>(table));
    }

    @Beta
    public static <R, C, V> Table<R, C, V> newCustomTable(Map<R, Map<C, V>> map, Supplier<? extends Map<C, V>> supplier) {
        Preconditions.checkArgument(map.isEmpty());
        Preconditions.checkNotNull(supplier);
        return new StandardTable<>(map, supplier);
    }

    @Beta
    public static <R, C, V1, V2> Table<R, C, V2> transformValues(Table<R, C, V1> table, Function<? super V1, V2> function) {
        return new Tables.TransformedTable<>(table, function);
    }

    public static <R, C, V> Table<R, C, V> unmodifiableTable(Table<? extends R, ? extends C, ? extends V> table) {
        return new Tables.UnmodifiableTable<>(table);
    }

    @Beta
    public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(RowSortedTable<R, ? extends C, ? extends V> rowsortedtable) {
        return new Tables.UnmodifiableRowSortedMap<>(rowsortedtable);
    }

    private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper() {
        return Tables.UNMODIFIABLE_WRAPPER;
    }

    static boolean equalsImpl(Table<?, ?, ?> table, @Nullable Object object) {
        if (object == table) {
            return true;
        } else if (object instanceof Table) {
            Table table1 = (Table) object;

            return table.cellSet().equals(table1.cellSet());
        } else {
            return false;
        }
    }

    static final class UnmodifiableRowSortedMap<R, C, V> extends Tables.UnmodifiableTable<R, C, V> implements RowSortedTable<R, C, V> {

        private static final long serialVersionUID = 0L;

        public UnmodifiableRowSortedMap(RowSortedTable<R, ? extends C, ? extends V> rowsortedtable) {
            super(rowsortedtable);
        }

        @Override
        protected RowSortedTable<R, C, V> delegate() {
            return (RowSortedTable) super.delegate();
        }

        @Override
        public SortedMap<R, Map<C, V>> rowMap() {
            Function function = Tables.unmodifiableWrapper();

            return Collections.unmodifiableSortedMap(Maps.transformValues(this.delegate().rowMap(), function));
        }

        @Override
        public SortedSet<R> rowKeySet() {
            return Collections.unmodifiableSortedSet(this.delegate().rowKeySet());
        }
    }

    private static class UnmodifiableTable<R, C, V> extends ForwardingTable<R, C, V> implements Serializable {

        final Table<? extends R, ? extends C, ? extends V> delegate;
        private static final long serialVersionUID = 0L;

        UnmodifiableTable(Table<? extends R, ? extends C, ? extends V> table) {
            this.delegate = (Table) Preconditions.checkNotNull(table);
        }

        @Override
        protected Table<R, C, V> delegate() {
            return this.delegate;
        }

        @Override
        public Set<Table.Cell<R, C, V>> cellSet() {
            return Collections.unmodifiableSet(super.cellSet());
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<R, V> column(@Nullable C c0) {
            return Collections.unmodifiableMap(super.column(c0));
        }

        @Override
        public Set<C> columnKeySet() {
            return Collections.unmodifiableSet(super.columnKeySet());
        }

        @Override
        public Map<C, Map<R, V>> columnMap() {
            Function function = Tables.unmodifiableWrapper();

            return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), function));
        }

        @Override
        public V put(@Nullable R r0, @Nullable C c0, @Nullable V v0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(@Nullable Object object, @Nullable Object object1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<C, V> row(@Nullable R r0) {
            return Collections.unmodifiableMap(super.row(r0));
        }

        @Override
        public Set<R> rowKeySet() {
            return Collections.unmodifiableSet(super.rowKeySet());
        }

        @Override
        public Map<R, Map<C, V>> rowMap() {
            Function function = Tables.unmodifiableWrapper();

            return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), function));
        }

        @Override
        public Collection<V> values() {
            return Collections.unmodifiableCollection(super.values());
        }
    }

    private static class TransformedTable<R, C, V1, V2> extends AbstractTable<R, C, V2> {

        final Table<R, C, V1> fromTable;
        final Function<? super V1, V2> function;

        TransformedTable(Table<R, C, V1> table, Function<? super V1, V2> function) {
            this.fromTable = (Table) Preconditions.checkNotNull(table);
            this.function = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        public boolean contains(Object object, Object object1) {
            return this.fromTable.contains(object, object1);
        }

        @Override
        public V2 get(Object object, Object object1) {
            return this.contains(object, object1) ? this.function.apply(this.fromTable.get(object, object1)) : null;
        }

        @Override
        public int size() {
            return this.fromTable.size();
        }

        @Override
        public void clear() {
            this.fromTable.clear();
        }

        @Override
        public V2 put(R r0, C c0, V2 v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Table<? extends R, ? extends C, ? extends V2> table) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V2 remove(Object object, Object object1) {
            return this.contains(object, object1) ? this.function.apply(this.fromTable.remove(object, object1)) : null;
        }

        @Override
        public Map<C, V2> row(R r0) {
            return Maps.transformValues(this.fromTable.row(r0), this.function);
        }

        @Override
        public Map<R, V2> column(C c0) {
            return Maps.transformValues(this.fromTable.column(c0), this.function);
        }

        Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>> cellFunction() {
            return new Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>>() {
                public Table.Cell<R, C, V2> apply(Table.Cell<R, C, V1> table_cell) {
                    return Tables.immutableCell(table_cell.getRowKey(), table_cell.getColumnKey(), TransformedTable.this.function.apply(table_cell.getValue()));
                }
            };
        }

        @Override
        Iterator<Table.Cell<R, C, V2>> cellIterator() {
            return Iterators.transform(this.fromTable.cellSet().iterator(), this.cellFunction());
        }

        @Override
        public Set<R> rowKeySet() {
            return this.fromTable.rowKeySet();
        }

        @Override
        public Set<C> columnKeySet() {
            return this.fromTable.columnKeySet();
        }

        @Override
        Collection<V2> createValues() {
            return Collections2.transform(this.fromTable.values(), this.function);
        }

        @Override
        public Map<R, Map<C, V2>> rowMap() {
            Function function = new Function<Map<C, V1>, Map<C, V2>>() {
                public Map<C, V2> apply(Map<C, V1> map) {
                    return Maps.transformValues(map, TransformedTable.this.function);
                }
            };

            return Maps.transformValues(this.fromTable.rowMap(), function);
        }

        @Override
        public Map<C, Map<R, V2>> columnMap() {
            Function function = new Function<Map<R, V1>, Map<R, V2>>() {
                public Map<R, V2> apply(Map<R, V1> map) {
                    return Maps.transformValues(map, TransformedTable.this.function);
                }
            };

            return Maps.transformValues(this.fromTable.columnMap(), function);
        }
    }

    private static class TransposeTable<C, R, V> extends AbstractTable<C, R, V> {

        final Table<R, C, V> original;
        private static final Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>> TRANSPOSE_CELL = new Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>>() {
            public Table.Cell<?, ?, ?> apply(Table.Cell<?, ?, ?> table_cell) {
                return Tables.immutableCell(table_cell.getColumnKey(), table_cell.getRowKey(), table_cell.getValue());
            }
        };

        TransposeTable(Table<R, C, V> table) {
            this.original = (Table) Preconditions.checkNotNull(table);
        }

        @Override
        public void clear() {
            this.original.clear();
        }

        @Override
        public Map<C, V> column(R r0) {
            return this.original.row(r0);
        }

        @Override
        public Set<R> columnKeySet() {
            return this.original.rowKeySet();
        }

        @Override
        public Map<R, Map<C, V>> columnMap() {
            return this.original.rowMap();
        }

        @Override
        public boolean contains(@Nullable Object object, @Nullable Object object1) {
            return this.original.contains(object1, object);
        }

        @Override
        public boolean containsColumn(@Nullable Object object) {
            return this.original.containsRow(object);
        }

        @Override
        public boolean containsRow(@Nullable Object object) {
            return this.original.containsColumn(object);
        }

        @Override
        public boolean containsValue(@Nullable Object object) {
            return this.original.containsValue(object);
        }

        @Override
        public V get(@Nullable Object object, @Nullable Object object1) {
            return this.original.get(object1, object);
        }

        @Override
        public V put(C c0, R r0, V v0) {
            return this.original.put(r0, c0, v0);
        }

        @Override
        public void putAll(Table<? extends C, ? extends R, ? extends V> table) {
            this.original.putAll(Tables.transpose(table));
        }

        @Override
        public V remove(@Nullable Object object, @Nullable Object object1) {
            return this.original.remove(object1, object);
        }

        @Override
        public Map<R, V> row(C c0) {
            return this.original.column(c0);
        }

        @Override
        public Set<C> rowKeySet() {
            return this.original.columnKeySet();
        }

        @Override
        public Map<C, Map<R, V>> rowMap() {
            return this.original.columnMap();
        }

        @Override
        public int size() {
            return this.original.size();
        }

        @Override
        public Collection<V> values() {
            return this.original.values();
        }

        @Override
        Iterator<Table.Cell<C, R, V>> cellIterator() {
            return Iterators.transform(this.original.cellSet().iterator(), Tables.TransposeTable.TRANSPOSE_CELL);
        }
    }

    abstract static class AbstractCell<R, C, V> implements Table.Cell<R, C, V> {

        AbstractCell() {}

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            } else if (!(object instanceof Table.Cell)) {
                return false;
            } else {
                Table.Cell table_cell = (Table.Cell) object;

                return Objects.equal(this.getRowKey(), table_cell.getRowKey()) && Objects.equal(this.getColumnKey(), table_cell.getColumnKey()) && Objects.equal(this.getValue(), table_cell.getValue());
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.getRowKey(), this.getColumnKey(), this.getValue());
        }

        public String toString() {
            return "(" + this.getRowKey() + "," + this.getColumnKey() + ")=" + this.getValue();
        }
    }

    static final class ImmutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> implements Serializable {

        private final R rowKey;
        private final C columnKey;
        private final V value;
        private static final long serialVersionUID = 0L;

        ImmutableCell(@Nullable R r0, @Nullable C c0, @Nullable V v0) {
            this.rowKey = r0;
            this.columnKey = c0;
            this.value = v0;
        }

        @Override
        public R getRowKey() {
            return this.rowKey;
        }

        @Override
        public C getColumnKey() {
            return this.columnKey;
        }

        @Override
        public V getValue() {
            return this.value;
        }
    }
}
