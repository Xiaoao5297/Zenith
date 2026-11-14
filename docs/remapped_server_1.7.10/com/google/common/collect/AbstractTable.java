package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTable<R, C, V> implements Table<R, C, V> {

    private transient Set<Table.Cell<R, C, V>> cellSet;
    private transient Collection<V> values;

    AbstractTable() {}

    @Override
    public boolean containsRow(@Nullable Object object) {
        return Maps.safeContainsKey(this.rowMap(), object);
    }

    @Override
    public boolean containsColumn(@Nullable Object object) {
        return Maps.safeContainsKey(this.columnMap(), object);
    }

    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return this.columnMap().keySet();
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        Iterator iterator = this.rowMap().values().iterator();

        Map map;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            map = (Map) iterator.next();
        } while (!map.containsValue(object));

        return true;
    }

    @Override
    public boolean contains(@Nullable Object object, @Nullable Object object1) {
        Map map = (Map) Maps.safeGet(this.rowMap(), object);

        return map != null && Maps.safeContainsKey(map, object1);
    }

    @Override
    public V get(@Nullable Object object, @Nullable Object object1) {
        Map map = (Map) Maps.safeGet(this.rowMap(), object);

        return map == null ? null : Maps.safeGet(map, object1);
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        Iterators.clear(this.cellSet().iterator());
    }

    @Override
    public V remove(@Nullable Object object, @Nullable Object object1) {
        Map map = (Map) Maps.safeGet(this.rowMap(), object);

        return map == null ? null : Maps.safeRemove(map, object1);
    }

    @Override
    public V put(R r0, C c0, V v0) {
        return this.row(r0).put(c0, v0);
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
        Iterator iterator = table.cellSet().iterator();

        while (iterator.hasNext()) {
            Table.Cell table_cell = (Table.Cell) iterator.next();

            this.put(table_cell.getRowKey(), table_cell.getColumnKey(), table_cell.getValue());
        }

    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        Set set = this.cellSet;

        return set == null ? (this.cellSet = this.createCellSet()) : set;
    }

    Set<Table.Cell<R, C, V>> createCellSet() {
        return new AbstractTable.CellSet();
    }

    abstract Iterator<Table.Cell<R, C, V>> cellIterator();

    @Override
    public Collection<V> values() {
        Collection collection = this.values;

        return collection == null ? (this.values = this.createValues()) : collection;
    }

    Collection<V> createValues() {
        return new AbstractTable.Values();
    }

    Iterator<V> valuesIterator() {
        return new TransformedIterator<Table.Cell<R, C, V>, V>(this.cellSet().iterator()) {
            V transform(Table.Cell<R, C, V> table_cell) {
                return table_cell.getValue();
            }
        };
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return Tables.equalsImpl(this, object);
    }

    @Override
    public int hashCode() {
        return this.cellSet().hashCode();
    }

    public String toString() {
        return this.rowMap().toString();
    }

    class Values extends AbstractCollection<V> {

        Values() {}

        public Iterator<V> iterator() {
            return AbstractTable.this.valuesIterator();
        }

        public boolean contains(Object object) {
            return AbstractTable.this.containsValue(object);
        }

        public void clear() {
            AbstractTable.this.clear();
        }

        public int size() {
            return AbstractTable.this.size();
        }
    }

    class CellSet extends AbstractSet<Table.Cell<R, C, V>> {

        CellSet() {}

        public boolean contains(Object object) {
            if (!(object instanceof Table.Cell)) {
                return false;
            } else {
                Table.Cell table_cell = (Table.Cell) object;
                Map map = (Map) Maps.safeGet(AbstractTable.this.rowMap(), table_cell.getRowKey());

                return map != null && Collections2.safeContains(map.entrySet(), Maps.immutableEntry(table_cell.getColumnKey(), table_cell.getValue()));
            }
        }

        public boolean remove(@Nullable Object object) {
            if (!(object instanceof Table.Cell)) {
                return false;
            } else {
                Table.Cell table_cell = (Table.Cell) object;
                Map map = (Map) Maps.safeGet(AbstractTable.this.rowMap(), table_cell.getRowKey());

                return map != null && Collections2.safeRemove(map.entrySet(), Maps.immutableEntry(table_cell.getColumnKey(), table_cell.getValue()));
            }
        }

        public void clear() {
            AbstractTable.this.clear();
        }

        public Iterator<Table.Cell<R, C, V>> iterator() {
            return AbstractTable.this.cellIterator();
        }

        public int size() {
            return AbstractTable.this.size();
        }
    }
}
