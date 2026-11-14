package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(emulated = true)
public final class ArrayTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {

    private final ImmutableList<R> rowList;
    private final ImmutableList<C> columnList;
    private final ImmutableMap<R, Integer> rowKeyToIndex;
    private final ImmutableMap<C, Integer> columnKeyToIndex;
    private final V[][] array;
    private transient ArrayTable<R, C, V>.ColumnMap columnMap;
    private transient ArrayTable<R, C, V>.RowMap rowMap;
    private static final long serialVersionUID = 0L;

    public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> iterable, Iterable<? extends C> iterable1) {
        return new ArrayTable<>(iterable, iterable1);
    }

    public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, V> table) {
        return table instanceof ArrayTable ? new ArrayTable<>((ArrayTable) table) : new ArrayTable<>(table);
    }

    private ArrayTable(Iterable<? extends R> iterable, Iterable<? extends C> iterable1) {
        this.rowList = ImmutableList.copyOf(iterable);
        this.columnList = ImmutableList.copyOf(iterable1);
        Preconditions.checkArgument(!this.rowList.isEmpty());
        Preconditions.checkArgument(!this.columnList.isEmpty());
        this.rowKeyToIndex = index(this.rowList);
        this.columnKeyToIndex = index(this.columnList);
        Object[][] aobject = (Object[][]) (new Object[this.rowList.size()][this.columnList.size()]);

        this.array = aobject;
        this.eraseAll();
    }

    private static <E> ImmutableMap<E, Integer> index(List<E> list) {
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();

        for (int i = 0; i < list.size(); ++i) {
            immutablemap_builder.put(list.get(i), i);
        }

        return immutablemap_builder.build();
    }

    private ArrayTable(Table<R, C, V> table) {
        this(table.rowKeySet(), table.columnKeySet());
        this.putAll(table);
    }

    private ArrayTable(ArrayTable<R, C, V> arraytable) {
        this.rowList = arraytable.rowList;
        this.columnList = arraytable.columnList;
        this.rowKeyToIndex = arraytable.rowKeyToIndex;
        this.columnKeyToIndex = arraytable.columnKeyToIndex;
        Object[][] aobject = (Object[][]) (new Object[this.rowList.size()][this.columnList.size()]);

        this.array = aobject;
        this.eraseAll();

        for (int i = 0; i < this.rowList.size(); ++i) {
            System.arraycopy(arraytable.array[i], 0, aobject[i], 0, arraytable.array[i].length);
        }

    }

    public ImmutableList<R> rowKeyList() {
        return this.rowList;
    }

    public ImmutableList<C> columnKeyList() {
        return this.columnList;
    }

    public V at(int i, int j) {
        Preconditions.checkElementIndex(i, this.rowList.size());
        Preconditions.checkElementIndex(j, this.columnList.size());
        return this.array[i][j];
    }

    public V set(int i, int j, @Nullable V v0) {
        Preconditions.checkElementIndex(i, this.rowList.size());
        Preconditions.checkElementIndex(j, this.columnList.size());
        Object object = this.array[i][j];

        this.array[i][j] = v0;
        return object;
    }

    @GwtIncompatible("reflection")
    public V[][] toArray(Class<V> oclass) {
        Object[][] aobject = (Object[][]) ((Object[][]) Array.newInstance(oclass, new int[]{this.rowList.size(), this.columnList.size()}));

        for (int i = 0; i < this.rowList.size(); ++i) {
            System.arraycopy(this.array[i], 0, aobject[i], 0, this.array[i].length);
        }

        return aobject;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void eraseAll() {
        Object[][] aobject = this.array;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object[] aobject1 = aobject[j];

            Arrays.fill(aobject1, (Object) null);
        }

    }

    @Override
    public boolean contains(@Nullable Object object, @Nullable Object object1) {
        return this.containsRow(object) && this.containsColumn(object1);
    }

    @Override
    public boolean containsColumn(@Nullable Object object) {
        return this.columnKeyToIndex.containsKey(object);
    }

    @Override
    public boolean containsRow(@Nullable Object object) {
        return this.rowKeyToIndex.containsKey(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        Object[][] aobject = this.array;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object[] aobject1 = aobject[j];
            Object[] aobject2 = aobject1;
            int k = aobject1.length;

            for (int l = 0; l < k; ++l) {
                Object object1 = aobject2[l];

                if (Objects.equal(object, object1)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(@Nullable Object object, @Nullable Object object1) {
        Integer integer = (Integer) this.rowKeyToIndex.get(object);
        Integer integer1 = (Integer) this.columnKeyToIndex.get(object1);

        return integer != null && integer1 != null ? this.at(integer, integer1) : null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public V put(R r0, C c0, @Nullable V v0) {
        Preconditions.checkNotNull(r0);
        Preconditions.checkNotNull(c0);
        Integer integer = (Integer) this.rowKeyToIndex.get(r0);

        Preconditions.checkArgument(integer != null, "Row %s not in %s", r0, this.rowList);
        Integer integer1 = (Integer) this.columnKeyToIndex.get(c0);

        Preconditions.checkArgument(integer1 != null, "Column %s not in %s", c0, this.columnList);
        return this.set(integer, integer1, v0);
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        super.putAll(table);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public V remove(Object object, Object object1) {
        throw new UnsupportedOperationException();
    }

    public V erase(@Nullable Object object, @Nullable Object object1) {
        Integer integer = (Integer) this.rowKeyToIndex.get(object);
        Integer integer1 = (Integer) this.columnKeyToIndex.get(object1);

        return integer != null && integer1 != null ? this.set(integer, integer1, (Object) null) : null;
    }

    @Override
    public int size() {
        return this.rowList.size() * this.columnList.size();
    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        return super.cellSet();
    }

    @Override
    Iterator<Table.Cell<R, C, V>> cellIterator() {
        return new AbstractIndexedListIterator<Table.Cell<R, C, V>>(this.size()) {
            @Override
            protected Table.Cell<R, C, V> get(final int i) {
                return new Tables.AbstractCell<R, C, V>() {
                    final int rowIndex;
                    final int columnIndex;

                    {
                        this.rowIndex = i / ArrayTable.this.columnList.size();
                        this.columnIndex = i % ArrayTable.this.columnList.size();
                    }

                    @Override
                    public R getRowKey() {
                        return ArrayTable.this.rowList.get(this.rowIndex);
                    }

                    @Override
                    public C getColumnKey() {
                        return ArrayTable.this.columnList.get(this.columnIndex);
                    }

                    @Override
                    public V getValue() {
                        return ArrayTable.this.at(this.rowIndex, this.columnIndex);
                    }
                };
            }
        };
    }

    @Override
    public Map<R, V> column(C c0) {
        Preconditions.checkNotNull(c0);
        Integer integer = (Integer) this.columnKeyToIndex.get(c0);

        return (Map) (integer == null ? ImmutableMap.of() : new ArrayTable.Column(integer));
    }

    @Override
    public ImmutableSet<C> columnKeySet() {
        return this.columnKeyToIndex.keySet();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        ArrayTable.ColumnMap arraytable_columnmap = this.columnMap;

        return arraytable_columnmap == null ? (this.columnMap = new ArrayTable.ColumnMap()) : arraytable_columnmap;
    }

    @Override
    public Map<C, V> row(R r0) {
        Preconditions.checkNotNull(r0);
        Integer integer = (Integer) this.rowKeyToIndex.get(r0);

        return (Map) (integer == null ? ImmutableMap.of() : new ArrayTable.Row(integer));
    }

    @Override
    public ImmutableSet<R> rowKeySet() {
        return this.rowKeyToIndex.keySet();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        ArrayTable.RowMap arraytable_rowmap = this.rowMap;

        return arraytable_rowmap == null ? (this.rowMap = new ArrayTable.RowMap()) : arraytable_rowmap;
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    private class RowMap extends ArrayTable.ArrayMap<R, Map<C, V>> {

        private RowMap() {
            super(ArrayTable.this.rowKeyToIndex, null);
        }

        @Override
        String getKeyRole() {
            return "Row";
        }

        @Override
        Map<C, V> getValue(int i) {
            return ArrayTable.this.new Row(i);
        }

        Map<C, V> setValue(int i, Map<C, V> map) {
            throw new UnsupportedOperationException();
        }

        public Map<C, V> put(R r0, Map<C, V> map) {
            throw new UnsupportedOperationException();
        }
    }

    private class Row extends ArrayTable.ArrayMap<C, V> {

        final int rowIndex;

        Row(int i) {
            super(ArrayTable.this.columnKeyToIndex, null);
            this.rowIndex = i;
        }

        @Override
        String getKeyRole() {
            return "Column";
        }

        @Override
        V getValue(int i) {
            return ArrayTable.this.at(this.rowIndex, i);
        }

        @Override
        V setValue(int i, V v0) {
            return ArrayTable.this.set(this.rowIndex, i, v0);
        }
    }

    private class ColumnMap extends ArrayTable.ArrayMap<C, Map<R, V>> {

        private ColumnMap() {
            super(ArrayTable.this.columnKeyToIndex, null);
        }

        @Override
        String getKeyRole() {
            return "Column";
        }

        @Override
        Map<R, V> getValue(int i) {
            return ArrayTable.this.new Column(i);
        }

        Map<R, V> setValue(int i, Map<R, V> map) {
            throw new UnsupportedOperationException();
        }

        public Map<R, V> put(C c0, Map<R, V> map) {
            throw new UnsupportedOperationException();
        }
    }

    private class Column extends ArrayTable.ArrayMap<R, V> {

        final int columnIndex;

        Column(int i) {
            super(ArrayTable.this.rowKeyToIndex, null);
            this.columnIndex = i;
        }

        @Override
        String getKeyRole() {
            return "Row";
        }

        @Override
        V getValue(int i) {
            return ArrayTable.this.at(i, this.columnIndex);
        }

        @Override
        V setValue(int i, V v0) {
            return ArrayTable.this.set(i, this.columnIndex, v0);
        }
    }

    private abstract static class ArrayMap<K, V> extends Maps.ImprovedAbstractMap<K, V> {

        private final ImmutableMap<K, Integer> keyIndex;

        private ArrayMap(ImmutableMap<K, Integer> immutablemap) {
            this.keyIndex = immutablemap;
        }

        @Override
        public Set<K> keySet() {
            return this.keyIndex.keySet();
        }

        K getKey(int i) {
            return this.keyIndex.keySet().asList().get(i);
        }

        abstract String getKeyRole();

        @Nullable
        abstract V getValue(int i);

        @Nullable
        abstract V setValue(int i, V v0);

        public int size() {
            return this.keyIndex.size();
        }

        public boolean isEmpty() {
            return this.keyIndex.isEmpty();
        }

        @Override
        protected Set<Entry<K, V>> createEntrySet() {
            return new Maps.EntrySet<K, V>() {
                @Override
                Map<K, V> map() {
                    return ArrayMap.this;
                }

                public Iterator<Entry<K, V>> iterator() {
                    return new AbstractIndexedListIterator<Entry<K, V>>(this.size()) {
                        @Override
                        protected Entry<K, V> get(final int i) {
                            return new AbstractMapEntry<K, V>() {
                                @Override
                                public K getKey() {
                                    return ArrayMap.this.getKey(i);
                                }

                                @Override
                                public V getValue() {
                                    return ArrayMap.this.getValue(i);
                                }

                                @Override
                                public V setValue(V v0) {
                                    return ArrayMap.this.setValue(i, v0);
                                }
                            };
                        }
                    };
                }
            };
        }

        public boolean containsKey(@Nullable Object object) {
            return this.keyIndex.containsKey(object);
        }

        public V get(@Nullable Object object) {
            Integer integer = (Integer) this.keyIndex.get(object);

            return integer == null ? null : this.getValue(integer);
        }

        public V put(K k0, V v0) {
            Integer integer = (Integer) this.keyIndex.get(k0);

            if (integer == null) {
                throw new IllegalArgumentException(this.getKeyRole() + " " + k0 + " not in " + this.keyIndex.keySet());
            } else {
                return this.setValue(integer, v0);
            }
        }

        public V remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
