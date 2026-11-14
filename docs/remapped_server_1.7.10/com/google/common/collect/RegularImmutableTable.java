package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {

    RegularImmutableTable() {}

    abstract Table.Cell<R, C, V> getCell(int i);

    @Override
    final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
        return (ImmutableSet) (this.isEmpty() ? ImmutableSet.of() : new RegularImmutableTable.CellSet());
    }

    abstract V getValue(int i);

    @Override
    final ImmutableCollection<V> createValues() {
        return (ImmutableCollection) (this.isEmpty() ? ImmutableList.of() : new RegularImmutableTable.Values());
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> list, @Nullable final Comparator<? super R> comparator, @Nullable final Comparator<? super C> comparator1) {
        Preconditions.checkNotNull(list);
        if (comparator != null || comparator1 != null) {
            Comparator comparator2 = new Comparator<Table.Cell<R, C, V>>() {
                public int compare(Table.Cell<R, C, V> table_cell, Table.Cell<R, C, V> table_cell1) {
                    int i = comparator == null ? 0 : comparator.compare(table_cell.getRowKey(), table_cell1.getRowKey());

                    return i != 0 ? i : (comparator1 == null ? 0 : comparator1.compare(table_cell.getColumnKey(), table_cell1.getColumnKey()));
                }
            };

            Collections.sort(list, comparator2);
        }

        return forCellsInternal(list, comparator, comparator1);
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> iterable) {
        return forCellsInternal(iterable, (Comparator) null, (Comparator) null);
    }

    private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> iterable, @Nullable Comparator<? super R> comparator, @Nullable Comparator<? super C> comparator1) {
        ImmutableSet.Builder immutableset_builder = ImmutableSet.builder();
        ImmutableSet.Builder immutableset_builder1 = ImmutableSet.builder();
        ImmutableList immutablelist = ImmutableList.copyOf(iterable);
        Iterator iterator = immutablelist.iterator();

        while (iterator.hasNext()) {
            Table.Cell table_cell = (Table.Cell) iterator.next();

            immutableset_builder.add(table_cell.getRowKey());
            immutableset_builder1.add(table_cell.getColumnKey());
        }

        ImmutableSet immutableset = immutableset_builder.build();

        if (comparator != null) {
            ArrayList arraylist = Lists.newArrayList((Iterable) immutableset);

            Collections.sort(arraylist, comparator);
            immutableset = ImmutableSet.copyOf((Collection) arraylist);
        }

        ImmutableSet immutableset1 = immutableset_builder1.build();

        if (comparator1 != null) {
            ArrayList arraylist1 = Lists.newArrayList((Iterable) immutableset1);

            Collections.sort(arraylist1, comparator1);
            immutableset1 = ImmutableSet.copyOf((Collection) arraylist1);
        }

        return (RegularImmutableTable) ((long) immutablelist.size() > (long) immutableset.size() * (long) immutableset1.size() / 2L ? new DenseImmutableTable<>(immutablelist, immutableset, immutableset1) : new SparseImmutableTable<>(immutablelist, immutableset, immutableset1));
    }

    private final class Values extends ImmutableList<V> {

        private Values() {}

        public int size() {
            return RegularImmutableTable.this.size();
        }

        public V get(int i) {
            return RegularImmutableTable.this.getValue(i);
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private final class CellSet extends ImmutableSet<Table.Cell<R, C, V>> {

        private CellSet() {}

        public int size() {
            return RegularImmutableTable.this.size();
        }

        @Override
        public UnmodifiableIterator<Table.Cell<R, C, V>> iterator() {
            return this.asList().iterator();
        }

        @Override
        ImmutableList<Table.Cell<R, C, V>> createAsList() {
            return new ImmutableAsList<Table.Cell<R, C, V>>() {
                public Table.Cell<R, C, V> get(int i) {
                    return RegularImmutableTable.this.getCell(i);
                }

                @Override
                ImmutableCollection<Table.Cell<R, C, V>> delegateCollection() {
                    return CellSet.this;
                }
            };
        }

        @Override
        public boolean contains(@Nullable Object object) {
            if (!(object instanceof Table.Cell)) {
                return false;
            } else {
                Table.Cell table_cell = (Table.Cell) object;
                Object object1 = RegularImmutableTable.this.get(table_cell.getRowKey(), table_cell.getColumnKey());

                return object1 != null && object1.equals(table_cell.getValue());
            }
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }
}
