package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ImmutableTable<R, C, V> extends AbstractTable<R, C, V> {

    private static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable<>(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());

    public static <R, C, V> ImmutableTable<R, C, V> of() {
        return ImmutableTable.EMPTY;
    }

    public static <R, C, V> ImmutableTable<R, C, V> of(R r0, C c0, V v0) {
        return new SingletonImmutableTable<>(r0, c0, v0);
    }

    public static <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> table) {
        if (table instanceof ImmutableTable) {
            ImmutableTable immutabletable = (ImmutableTable) table;

            return immutabletable;
        } else {
            int i = table.size();

            switch (i) {
                case 0:
                    return of();
                case 1:
                    Table.Cell table_cell = (Table.Cell) Iterables.getOnlyElement(table.cellSet());

                    return of(table_cell.getRowKey(), table_cell.getColumnKey(), table_cell.getValue());
                default:
                    ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();
                    Iterator iterator = table.cellSet().iterator();

                    while (iterator.hasNext()) {
                        Table.Cell table_cell1 = (Table.Cell) iterator.next();

                        immutableset_builder.add((Object) cellOf(table_cell1.getRowKey(), table_cell1.getColumnKey(), table_cell1.getValue()));
                    }

                    return RegularImmutableTable.forCells(immutableset_builder.build());
            }
        }
    }

    public static <R, C, V> ImmutableTable.Builder<R, C, V> builder() {
        return new ImmutableTable.Builder<>();
    }

    static <R, C, V> Table.Cell<R, C, V> cellOf(R r0, C c0, V v0) {
        return Tables.immutableCell(Preconditions.checkNotNull(r0), Preconditions.checkNotNull(c0), Preconditions.checkNotNull(v0));
    }

    ImmutableTable() {}

    @Override
    public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
        return (ImmutableSet) super.cellSet();
    }

    @Override
    abstract ImmutableSet<Table.Cell<R, C, V>> createCellSet();

    @Override
    final UnmodifiableIterator<Table.Cell<R, C, V>> cellIterator() {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection) super.values();
    }

    @Override
    abstract ImmutableCollection<V> createValues();

    @Override
    final Iterator<V> valuesIterator() {
        throw new AssertionError("should never be called");
    }

    @Override
    public ImmutableMap<R, V> column(C c0) {
        Preconditions.checkNotNull(c0);
        return (ImmutableMap) Objects.firstNonNull((ImmutableMap) this.columnMap().get(c0), ImmutableMap.of());
    }

    @Override
    public ImmutableSet<C> columnKeySet() {
        return this.columnMap().keySet();
    }

    @Override
    public abstract ImmutableMap<C, Map<R, V>> columnMap();

    @Override
    public ImmutableMap<C, V> row(R r0) {
        Preconditions.checkNotNull(r0);
        return (ImmutableMap) Objects.firstNonNull((ImmutableMap) this.rowMap().get(r0), ImmutableMap.of());
    }

    @Override
    public ImmutableSet<R> rowKeySet() {
        return this.rowMap().keySet();
    }

    @Override
    public abstract ImmutableMap<R, Map<C, V>> rowMap();

    @Override
    public boolean contains(@Nullable Object object, @Nullable Object object1) {
        return this.get(object, object1) != null;
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.values().contains(object);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final V put(R r0, C c0, V v0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final V remove(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder<R, C, V> {

        private final List<Table.Cell<R, C, V>> cells = Lists.newArrayList();
        private Comparator<? super R> rowComparator;
        private Comparator<? super C> columnComparator;

        public Builder() {}

        public ImmutableTable.Builder<R, C, V> orderRowsBy(Comparator<? super R> comparator) {
            this.rowComparator = (Comparator) Preconditions.checkNotNull(comparator);
            return this;
        }

        public ImmutableTable.Builder<R, C, V> orderColumnsBy(Comparator<? super C> comparator) {
            this.columnComparator = (Comparator) Preconditions.checkNotNull(comparator);
            return this;
        }

        public ImmutableTable.Builder<R, C, V> put(R r0, C c0, V v0) {
            this.cells.add(ImmutableTable.cellOf(r0, c0, v0));
            return this;
        }

        public ImmutableTable.Builder<R, C, V> put(Table.Cell<? extends R, ? extends C, ? extends V> table_cell) {
            if (table_cell instanceof Tables.ImmutableCell) {
                Preconditions.checkNotNull(table_cell.getRowKey());
                Preconditions.checkNotNull(table_cell.getColumnKey());
                Preconditions.checkNotNull(table_cell.getValue());
                this.cells.add(table_cell);
            } else {
                this.put(table_cell.getRowKey(), table_cell.getColumnKey(), table_cell.getValue());
            }

            return this;
        }

        public ImmutableTable.Builder<R, C, V> putAll(Table<? extends R, ? extends C, ? extends V> table) {
            Iterator iterator = table.cellSet().iterator();

            while (iterator.hasNext()) {
                Table.Cell table_cell = (Table.Cell) iterator.next();

                this.put(table_cell);
            }

            return this;
        }

        public ImmutableTable<R, C, V> build() {
            int i = this.cells.size();

            switch (i) {
                case 0:
                    return ImmutableTable.of();
                case 1:
                    return new SingletonImmutableTable<>((Table.Cell) Iterables.getOnlyElement(this.cells));
                default:
                    return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
            }
        }
    }
}
