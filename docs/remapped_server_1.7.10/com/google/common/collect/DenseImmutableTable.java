package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class DenseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {

    private final ImmutableMap<R, Integer> rowKeyToIndex;
    private final ImmutableMap<C, Integer> columnKeyToIndex;
    private final ImmutableMap<R, Map<C, V>> rowMap;
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] rowCounts;
    private final int[] columnCounts;
    private final V[][] values;
    private final int[] iterationOrderRow;
    private final int[] iterationOrderColumn;

    private static <E> ImmutableMap<E, Integer> makeIndex(ImmutableSet<E> immutableset) {
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        int i = 0;

        for (Iterator iterator = immutableset.iterator(); iterator.hasNext(); ++i) {
            Object object = iterator.next();

            immutablemap_builder.put(object, i);
        }

        return immutablemap_builder.build();
    }

    DenseImmutableTable(ImmutableList<Table.Cell<R, C, V>> immutablelist, ImmutableSet<R> immutableset, ImmutableSet<C> immutableset1) {
        Object[][] aobject = (Object[][]) (new Object[immutableset.size()][immutableset1.size()]);

        this.values = aobject;
        this.rowKeyToIndex = makeIndex(immutableset);
        this.columnKeyToIndex = makeIndex(immutableset1);
        this.rowCounts = new int[this.rowKeyToIndex.size()];
        this.columnCounts = new int[this.columnKeyToIndex.size()];
        int[] aint = new int[immutablelist.size()];
        int[] aint1 = new int[immutablelist.size()];

        for (int i = 0; i < immutablelist.size(); ++i) {
            Table.Cell table_cell = (Table.Cell) immutablelist.get(i);
            Object object = table_cell.getRowKey();
            Object object1 = table_cell.getColumnKey();
            int j = (Integer) this.rowKeyToIndex.get(object);
            int k = (Integer) this.columnKeyToIndex.get(object1);
            Object object2 = this.values[j][k];

            Preconditions.checkArgument(object2 == null, "duplicate key: (%s, %s)", object, object1);
            this.values[j][k] = table_cell.getValue();
            int l = this.rowCounts[j]++;

            l = this.columnCounts[k]++;
            aint[i] = j;
            aint1[i] = k;
        }

        this.iterationOrderRow = aint;
        this.iterationOrderColumn = aint1;
        this.rowMap = new DenseImmutableTable.RowMap();
        this.columnMap = new DenseImmutableTable.ColumnMap();
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    @Override
    public V get(@Nullable Object object, @Nullable Object object1) {
        Integer integer = (Integer) this.rowKeyToIndex.get(object);
        Integer integer1 = (Integer) this.columnKeyToIndex.get(object1);

        return integer != null && integer1 != null ? this.values[integer][integer1] : null;
    }

    @Override
    public int size() {
        return this.iterationOrderRow.length;
    }

    @Override
    Table.Cell<R, C, V> getCell(int i) {
        int j = this.iterationOrderRow[i];
        int k = this.iterationOrderColumn[i];
        Object object = this.rowKeySet().asList().get(j);
        Object object1 = this.columnKeySet().asList().get(k);
        Object object2 = this.values[j][k];

        return cellOf(object, object1, object2);
    }

    @Override
    V getValue(int i) {
        return this.values[this.iterationOrderRow[i]][this.iterationOrderColumn[i]];
    }

    private final class ColumnMap extends DenseImmutableTable.ImmutableArrayMap<C, Map<R, V>> {

        private ColumnMap() {
            super(DenseImmutableTable.this.columnCounts.length);
        }

        @Override
        ImmutableMap<C, Integer> keyToIndex() {
            return DenseImmutableTable.this.columnKeyToIndex;
        }

        @Override
        Map<R, V> getValue(int i) {
            return DenseImmutableTable.this.new Column(i);
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }

    private final class RowMap extends DenseImmutableTable.ImmutableArrayMap<R, Map<C, V>> {

        private RowMap() {
            super(DenseImmutableTable.this.rowCounts.length);
        }

        @Override
        ImmutableMap<R, Integer> keyToIndex() {
            return DenseImmutableTable.this.rowKeyToIndex;
        }

        @Override
        Map<C, V> getValue(int i) {
            return DenseImmutableTable.this.new Row(i);
        }

        @Override
        boolean isPartialView() {
            return false;
        }
    }

    private final class Column extends DenseImmutableTable.ImmutableArrayMap<R, V> {

        private final int columnIndex;

        Column(int i) {
            super(DenseImmutableTable.this.columnCounts[i]);
            this.columnIndex = i;
        }

        @Override
        ImmutableMap<R, Integer> keyToIndex() {
            return DenseImmutableTable.this.rowKeyToIndex;
        }

        @Override
        V getValue(int i) {
            return DenseImmutableTable.this.values[i][this.columnIndex];
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private final class Row extends DenseImmutableTable.ImmutableArrayMap<C, V> {

        private final int rowIndex;

        Row(int i) {
            super(DenseImmutableTable.this.rowCounts[i]);
            this.rowIndex = i;
        }

        @Override
        ImmutableMap<C, Integer> keyToIndex() {
            return DenseImmutableTable.this.columnKeyToIndex;
        }

        @Override
        V getValue(int i) {
            return DenseImmutableTable.this.values[this.rowIndex][i];
        }

        @Override
        boolean isPartialView() {
            return true;
        }
    }

    private abstract static class ImmutableArrayMap<K, V> extends ImmutableMap<K, V> {

        private final int size;

        ImmutableArrayMap(int i) {
            this.size = i;
        }

        abstract ImmutableMap<K, Integer> keyToIndex();

        private boolean isFull() {
            return this.size == this.keyToIndex().size();
        }

        K getKey(int i) {
            return this.keyToIndex().keySet().asList().get(i);
        }

        @Nullable
        abstract V getValue(int i);

        @Override
        ImmutableSet<K> createKeySet() {
            return this.isFull() ? this.keyToIndex().keySet() : super.createKeySet();
        }

        public int size() {
            return this.size;
        }

        @Override
        public V get(@Nullable Object object) {
            Integer integer = (Integer) this.keyToIndex().get(object);

            return integer == null ? null : this.getValue(integer);
        }

        @Override
        ImmutableSet<Entry<K, V>> createEntrySet() {
            return new ImmutableMapEntrySet<K, V>() {
                @Override
                ImmutableMap<K, V> map() {
                    return ImmutableArrayMap.this;
                }

                @Override
                public UnmodifiableIterator<Entry<K, V>> iterator() {
                    return new AbstractIterator<Entry<K, V>>() {
                        private int index = -1;
                        private final int maxIndex = ImmutableArrayMap.this.keyToIndex().size();

                        @Override
                        protected Entry<K, V> computeNext() {
                            ++this.index;

                            while (this.index < this.maxIndex) {
                                Object object = ImmutableArrayMap.this.getValue(this.index);

                                if (object != null) {
                                    return Maps.immutableEntry(ImmutableArrayMap.this.getKey(this.index), object);
                                }

                                ++this.index;
                            }

                            return (Entry) this.endOfData();
                        }
                    };
                }
            };
        }
    }
}
