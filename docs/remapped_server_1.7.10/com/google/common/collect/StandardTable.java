package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class StandardTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {

    @GwtTransient
    final Map<R, Map<C, V>> backingMap;
    @GwtTransient
    final Supplier<? extends Map<C, V>> factory;
    private transient Set<C> columnKeySet;
    private transient Map<R, Map<C, V>> rowMap;
    private transient StandardTable<R, C, V>.ColumnMap columnMap;
    private static final long serialVersionUID = 0L;

    StandardTable(Map<R, Map<C, V>> map, Supplier<? extends Map<C, V>> supplier) {
        this.backingMap = map;
        this.factory = supplier;
    }

    @Override
    public boolean contains(@Nullable Object object, @Nullable Object object1) {
        return object != null && object1 != null && super.contains(object, object1);
    }

    @Override
    public boolean containsColumn(@Nullable Object object) {
        if (object == null) {
            return false;
        } else {
            Iterator iterator = this.backingMap.values().iterator();

            Map map;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                map = (Map) iterator.next();
            } while (!Maps.safeContainsKey(map, object));

            return true;
        }
    }

    @Override
    public boolean containsRow(@Nullable Object object) {
        return object != null && Maps.safeContainsKey(this.backingMap, object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return object != null && super.containsValue(object);
    }

    @Override
    public V get(@Nullable Object object, @Nullable Object object1) {
        return object != null && object1 != null ? super.get(object, object1) : null;
    }

    @Override
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    @Override
    public int size() {
        int i = 0;

        Map map;

        for (Iterator iterator = this.backingMap.values().iterator(); iterator.hasNext(); i += map.size()) {
            map = (Map) iterator.next();
        }

        return i;
    }

    @Override
    public void clear() {
        this.backingMap.clear();
    }

    private Map<C, V> getOrCreate(R r0) {
        Map map = (Map) this.backingMap.get(r0);

        if (map == null) {
            map = (Map) this.factory.get();
            this.backingMap.put(r0, map);
        }

        return map;
    }

    @Override
    public V put(R r0, C c0, V v0) {
        Preconditions.checkNotNull(r0);
        Preconditions.checkNotNull(c0);
        Preconditions.checkNotNull(v0);
        return this.getOrCreate(r0).put(c0, v0);
    }

    @Override
    public V remove(@Nullable Object object, @Nullable Object object1) {
        if (object != null && object1 != null) {
            Map map = (Map) Maps.safeGet(this.backingMap, object);

            if (map == null) {
                return null;
            } else {
                Object object2 = map.remove(object1);

                if (map.isEmpty()) {
                    this.backingMap.remove(object);
                }

                return object2;
            }
        } else {
            return null;
        }
    }

    private Map<R, V> removeColumn(Object object) {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Iterator iterator = this.backingMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object1 = ((Map) entry.getValue()).remove(object);

            if (object1 != null) {
                linkedhashmap.put(entry.getKey(), object1);
                if (((Map) entry.getValue()).isEmpty()) {
                    iterator.remove();
                }
            }
        }

        return linkedhashmap;
    }

    private boolean containsMapping(Object object, Object object1, Object object2) {
        return object2 != null && object2.equals(this.get(object, object1));
    }

    private boolean removeMapping(Object object, Object object1, Object object2) {
        if (this.containsMapping(object, object1, object2)) {
            this.remove(object, object1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<Table.Cell<R, C, V>> cellSet() {
        return super.cellSet();
    }

    @Override
    Iterator<Table.Cell<R, C, V>> cellIterator() {
        return new StandardTable.CellIterator();
    }

    @Override
    public Map<C, V> row(R r0) {
        return new StandardTable.Row(r0);
    }

    @Override
    public Map<R, V> column(C c0) {
        return new StandardTable.Column(c0);
    }

    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        Set set = this.columnKeySet;

        return set == null ? (this.columnKeySet = new StandardTable.ColumnKeySet()) : set;
    }

    Iterator<C> createColumnKeyIterator() {
        return new StandardTable.ColumnKeyIterator();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        Map map = this.rowMap;

        return map == null ? (this.rowMap = this.createRowMap()) : map;
    }

    Map<R, Map<C, V>> createRowMap() {
        return new StandardTable.RowMap();
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        StandardTable.ColumnMap standardtable_columnmap = this.columnMap;

        return standardtable_columnmap == null ? (this.columnMap = new StandardTable.ColumnMap()) : standardtable_columnmap;
    }

    private class ColumnMap extends Maps.ImprovedAbstractMap<C, Map<R, V>> {

        private ColumnMap() {}

        public Map<R, V> get(Object object) {
            return StandardTable.this.containsColumn(object) ? StandardTable.this.column(object) : null;
        }

        public boolean containsKey(Object object) {
            return StandardTable.this.containsColumn(object);
        }

        public Map<R, V> remove(Object object) {
            return StandardTable.this.containsColumn(object) ? StandardTable.this.removeColumn(object) : null;
        }

        @Override
        public Set<Entry<C, Map<R, V>>> createEntrySet() {
            return new StandardTable.ColumnMap.ColumnMapEntrySet();
        }

        @Override
        public Set<C> keySet() {
            return StandardTable.this.columnKeySet();
        }

        @Override
        Collection<Map<R, V>> createValues() {
            return new StandardTable.ColumnMap.ColumnMapValues();
        }

        private class ColumnMapValues extends Maps.Values<C, Map<R, V>> {

            ColumnMapValues() {
                super(ColumnMap.this);
            }

            @Override
            public boolean remove(Object object) {
                Iterator iterator = ColumnMap.this.entrySet().iterator();

                Entry entry;

                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entry = (Entry) iterator.next();
                } while (!((Map) entry.getValue()).equals(object));

                StandardTable.this.removeColumn(entry.getKey());
                return true;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                Preconditions.checkNotNull(collection);
                boolean flag = false;
                Iterator iterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    if (collection.contains(StandardTable.this.column(object))) {
                        StandardTable.this.removeColumn(object);
                        flag = true;
                    }
                }

                return flag;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                Preconditions.checkNotNull(collection);
                boolean flag = false;
                Iterator iterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    if (!collection.contains(StandardTable.this.column(object))) {
                        StandardTable.this.removeColumn(object);
                        flag = true;
                    }
                }

                return flag;
            }
        }

        class ColumnMapEntrySet extends StandardTable<R, C, V>.TableSet<Entry<C, Map<R, V>>> {

            ColumnMapEntrySet() {
                super(null);
            }

            public Iterator<Entry<C, Map<R, V>>> iterator() {
                return Maps.asMapEntryIterator(StandardTable.this.columnKeySet(), new Function<C, Map<R, V>>() {
                    @Override
                    public Map<R, V> apply(C c0) {
                        return StandardTable.this.column(c0);
                    }
                });
            }

            public int size() {
                return StandardTable.this.columnKeySet().size();
            }

            public boolean contains(Object object) {
                if (object instanceof Entry) {
                    Entry entry = (Entry) object;

                    if (StandardTable.this.containsColumn(entry.getKey())) {
                        Object object1 = entry.getKey();

                        return ColumnMap.this.get(object1).equals(entry.getValue());
                    }
                }

                return false;
            }

            public boolean remove(Object object) {
                if (this.contains(object)) {
                    Entry entry = (Entry) object;

                    StandardTable.this.removeColumn(entry.getKey());
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                Preconditions.checkNotNull(collection);
                return Sets.removeAllImpl(this, collection.iterator());
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                Preconditions.checkNotNull(collection);
                boolean flag = false;
                Iterator iterator = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

                while (iterator.hasNext()) {
                    Object object = iterator.next();

                    if (!collection.contains(Maps.immutableEntry(object, StandardTable.this.column(object)))) {
                        StandardTable.this.removeColumn(object);
                        flag = true;
                    }
                }

                return flag;
            }
        }
    }

    class RowMap extends Maps.ImprovedAbstractMap<R, Map<C, V>> {

        RowMap() {}

        public boolean containsKey(Object object) {
            return StandardTable.this.containsRow(object);
        }

        public Map<C, V> get(Object object) {
            return StandardTable.this.containsRow(object) ? StandardTable.this.row(object) : null;
        }

        public Map<C, V> remove(Object object) {
            return object == null ? null : (Map) StandardTable.this.backingMap.remove(object);
        }

        @Override
        protected Set<Entry<R, Map<C, V>>> createEntrySet() {
            return new StandardTable.RowMap.EntrySet();
        }

        class EntrySet extends StandardTable<R, C, V>.TableSet<Entry<R, Map<C, V>>> {

            EntrySet() {
                super(null);
            }

            public Iterator<Entry<R, Map<C, V>>> iterator() {
                return Maps.asMapEntryIterator(StandardTable.this.backingMap.keySet(), new Function<R, Map<C, V>>() {
                    @Override
                    public Map<C, V> apply(R r0) {
                        return StandardTable.this.row(r0);
                    }
                });
            }

            public int size() {
                return StandardTable.this.backingMap.size();
            }

            public boolean contains(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Entry entry = (Entry) object;

                    return entry.getKey() != null && entry.getValue() instanceof Map && Collections2.safeContains(StandardTable.this.backingMap.entrySet(), entry);
                }
            }

            public boolean remove(Object object) {
                if (!(object instanceof Entry)) {
                    return false;
                } else {
                    Entry entry = (Entry) object;

                    return entry.getKey() != null && entry.getValue() instanceof Map && StandardTable.this.backingMap.entrySet().remove(entry);
                }
            }
        }
    }

    private class ColumnKeyIterator extends AbstractIterator<C> {

        final Map<C, V> seen;
        final Iterator<Map<C, V>> mapIterator;
        Iterator<Entry<C, V>> entryIterator;

        private ColumnKeyIterator() {
            this.seen = (Map) StandardTable.this.factory.get();
            this.mapIterator = StandardTable.this.backingMap.values().iterator();
            this.entryIterator = Iterators.emptyIterator();
        }

        @Override
        protected C computeNext() {
            while (true) {
                if (this.entryIterator.hasNext()) {
                    Entry entry = (Entry) this.entryIterator.next();

                    if (!this.seen.containsKey(entry.getKey())) {
                        this.seen.put(entry.getKey(), entry.getValue());
                        return entry.getKey();
                    }
                } else {
                    if (!this.mapIterator.hasNext()) {
                        return this.endOfData();
                    }

                    this.entryIterator = ((Map) this.mapIterator.next()).entrySet().iterator();
                }
            }
        }
    }

    private class ColumnKeySet extends StandardTable<R, C, V>.TableSet<C> {

        private ColumnKeySet() {
            super(null);
        }

        public Iterator<C> iterator() {
            return StandardTable.this.createColumnKeyIterator();
        }

        public int size() {
            return Iterators.size(this.iterator());
        }

        public boolean remove(Object object) {
            if (object == null) {
                return false;
            } else {
                boolean flag = false;
                Iterator iterator = StandardTable.this.backingMap.values().iterator();

                while (iterator.hasNext()) {
                    Map map = (Map) iterator.next();

                    if (map.keySet().remove(object)) {
                        flag = true;
                        if (map.isEmpty()) {
                            iterator.remove();
                        }
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            boolean flag = false;
            Iterator iterator = StandardTable.this.backingMap.values().iterator();

            while (iterator.hasNext()) {
                Map map = (Map) iterator.next();

                if (Iterators.removeAll(map.keySet().iterator(), collection)) {
                    flag = true;
                    if (map.isEmpty()) {
                        iterator.remove();
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            boolean flag = false;
            Iterator iterator = StandardTable.this.backingMap.values().iterator();

            while (iterator.hasNext()) {
                Map map = (Map) iterator.next();

                if (map.keySet().retainAll(collection)) {
                    flag = true;
                    if (map.isEmpty()) {
                        iterator.remove();
                    }
                }
            }

            return flag;
        }

        public boolean contains(Object object) {
            return StandardTable.this.containsColumn(object);
        }
    }

    private class Column extends Maps.ImprovedAbstractMap<R, V> {

        final C columnKey;

        Column(C c0) {
            this.columnKey = Preconditions.checkNotNull(c0);
        }

        public V put(R r0, V v0) {
            return StandardTable.this.put(r0, this.columnKey, v0);
        }

        public V get(Object object) {
            return StandardTable.this.get(object, this.columnKey);
        }

        public boolean containsKey(Object object) {
            return StandardTable.this.contains(object, this.columnKey);
        }

        public V remove(Object object) {
            return StandardTable.this.remove(object, this.columnKey);
        }

        boolean removeFromColumnIf(Predicate<? super Entry<R, V>> predicate) {
            boolean flag = false;
            Iterator iterator = StandardTable.this.backingMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Map map = (Map) entry.getValue();
                Object object = map.get(this.columnKey);

                if (object != null && predicate.apply(Maps.immutableEntry(entry.getKey(), object))) {
                    map.remove(this.columnKey);
                    flag = true;
                    if (map.isEmpty()) {
                        iterator.remove();
                    }
                }
            }

            return flag;
        }

        @Override
        Set<Entry<R, V>> createEntrySet() {
            return new StandardTable.Column.EntrySet();
        }

        @Override
        Set<R> createKeySet() {
            return new StandardTable.Column.KeySet();
        }

        @Override
        Collection<V> createValues() {
            return new StandardTable.Column.Values();
        }

        private class Values extends Maps.Values<R, V> {

            Values() {
                super(Column.this);
            }

            @Override
            public boolean remove(Object object) {
                return object != null && Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.equalTo(object)));
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.in(collection)));
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(collection))));
            }
        }

        private class KeySet extends Maps.KeySet<R, V> {

            KeySet() {
                super(Column.this);
            }

            @Override
            public boolean contains(Object object) {
                return StandardTable.this.contains(object, Column.this.columnKey);
            }

            @Override
            public boolean remove(Object object) {
                return StandardTable.this.remove(object, Column.this.columnKey) != null;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return Column.this.removeFromColumnIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(collection))));
            }
        }

        private class EntrySetIterator extends AbstractIterator<Entry<R, V>> {

            final Iterator<Entry<R, Map<C, V>>> iterator;

            private EntrySetIterator() {
                this.iterator = StandardTable.this.backingMap.entrySet().iterator();
            }

            @Override
            protected Entry<R, V> computeNext() {
                while (true) {
                    if (this.iterator.hasNext()) {
                        final Entry entry = (Entry) this.iterator.next();

                        if (!((Map) entry.getValue()).containsKey(Column.this.columnKey)) {
                            continue;
                        }

                        return new AbstractMapEntry<R, V>() {
                            @Override
                            public R getKey() {
                                return entry.getKey();
                            }

                            @Override
                            public V getValue() {
                                return ((Map) entry.getValue()).get(Column.this.columnKey);
                            }

                            @Override
                            public V setValue(V v0) {
                                return ((Map) entry.getValue()).put(Column.this.columnKey, Preconditions.checkNotNull(v0));
                            }
                        };
                    }

                    return (Entry) this.endOfData();
                }
            }
        }

        private class EntrySet extends Sets.ImprovedAbstractSet<Entry<R, V>> {

            private EntrySet() {}

            public Iterator<Entry<R, V>> iterator() {
                return Column.this.new EntrySetIterator();
            }

            public int size() {
                int i = 0;
                Iterator iterator = StandardTable.this.backingMap.values().iterator();

                while (iterator.hasNext()) {
                    Map map = (Map) iterator.next();

                    if (map.containsKey(Column.this.columnKey)) {
                        ++i;
                    }
                }

                return i;
            }

            public boolean isEmpty() {
                return !StandardTable.this.containsColumn(Column.this.columnKey);
            }

            public void clear() {
                Column.this.removeFromColumnIf(Predicates.alwaysTrue());
            }

            public boolean contains(Object object) {
                if (object instanceof Entry) {
                    Entry entry = (Entry) object;

                    return StandardTable.this.containsMapping(entry.getKey(), Column.this.columnKey, entry.getValue());
                } else {
                    return false;
                }
            }

            public boolean remove(Object object) {
                if (object instanceof Entry) {
                    Entry entry = (Entry) object;

                    return StandardTable.this.removeMapping(entry.getKey(), Column.this.columnKey, entry.getValue());
                } else {
                    return false;
                }
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return Column.this.removeFromColumnIf(Predicates.not(Predicates.in(collection)));
            }
        }
    }

    class Row extends Maps.ImprovedAbstractMap<C, V> {

        final R rowKey;
        Map<C, V> backingRowMap;

        Row(R r0) {
            this.rowKey = Preconditions.checkNotNull(r0);
        }

        Map<C, V> backingRowMap() {
            return this.backingRowMap != null && (!this.backingRowMap.isEmpty() || !StandardTable.this.backingMap.containsKey(this.rowKey)) ? this.backingRowMap : (this.backingRowMap = this.computeBackingRowMap());
        }

        Map<C, V> computeBackingRowMap() {
            return (Map) StandardTable.this.backingMap.get(this.rowKey);
        }

        void maintainEmptyInvariant() {
            if (this.backingRowMap() != null && this.backingRowMap.isEmpty()) {
                StandardTable.this.backingMap.remove(this.rowKey);
                this.backingRowMap = null;
            }

        }

        public boolean containsKey(Object object) {
            Map map = this.backingRowMap();

            return object != null && map != null && Maps.safeContainsKey(map, object);
        }

        public V get(Object object) {
            Map map = this.backingRowMap();

            return object != null && map != null ? Maps.safeGet(map, object) : null;
        }

        public V put(C c0, V v0) {
            Preconditions.checkNotNull(c0);
            Preconditions.checkNotNull(v0);
            return this.backingRowMap != null && !this.backingRowMap.isEmpty() ? this.backingRowMap.put(c0, v0) : StandardTable.this.put(this.rowKey, c0, v0);
        }

        public V remove(Object object) {
            Map map = this.backingRowMap();

            if (map == null) {
                return null;
            } else {
                Object object1 = Maps.safeRemove(map, object);

                this.maintainEmptyInvariant();
                return object1;
            }
        }

        public void clear() {
            Map map = this.backingRowMap();

            if (map != null) {
                map.clear();
            }

            this.maintainEmptyInvariant();
        }

        @Override
        protected Set<Entry<C, V>> createEntrySet() {
            return new StandardTable.Row.RowEntrySet();
        }

        private final class RowEntrySet extends Maps.EntrySet<C, V> {

            private RowEntrySet() {}

            @Override
            Map<C, V> map() {
                return Row.this;
            }

            @Override
            public int size() {
                Map map = Row.this.backingRowMap();

                return map == null ? 0 : map.size();
            }

            public Iterator<Entry<C, V>> iterator() {
                Map map = Row.this.backingRowMap();

                if (map == null) {
                    return Iterators.emptyModifiableIterator();
                } else {
                    final Iterator iterator = map.entrySet().iterator();

                    return new Iterator<Entry<C, V>>() {
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }

                        public Entry<C, V> next() {
                            final Entry entry = (Entry) iterator.next();

                            return new ForwardingMapEntry<C, V>() {
                                @Override
                                protected Entry<C, V> delegate() {
                                    return entry;
                                }

                                @Override
                                public V setValue(V v0) {
                                    return super.setValue(Preconditions.checkNotNull(v0));
                                }

                                @Override
                                public boolean equals(Object object) {
                                    return this.standardEquals(object);
                                }
                            };
                        }

                        public void remove() {
                            iterator.remove();
                            Row.this.maintainEmptyInvariant();
                        }
                    };
                }
            }
        }
    }

    private class CellIterator implements Iterator<Table.Cell<R, C, V>> {

        final Iterator<Entry<R, Map<C, V>>> rowIterator;
        Entry<R, Map<C, V>> rowEntry;
        Iterator<Entry<C, V>> columnIterator;

        private CellIterator() {
            this.rowIterator = StandardTable.this.backingMap.entrySet().iterator();
            this.columnIterator = Iterators.emptyModifiableIterator();
        }

        public boolean hasNext() {
            return this.rowIterator.hasNext() || this.columnIterator.hasNext();
        }

        public Table.Cell<R, C, V> next() {
            if (!this.columnIterator.hasNext()) {
                this.rowEntry = (Entry) this.rowIterator.next();
                this.columnIterator = ((Map) this.rowEntry.getValue()).entrySet().iterator();
            }

            Entry entry = (Entry) this.columnIterator.next();

            return Tables.immutableCell(this.rowEntry.getKey(), entry.getKey(), entry.getValue());
        }

        public void remove() {
            this.columnIterator.remove();
            if (((Map) this.rowEntry.getValue()).isEmpty()) {
                this.rowIterator.remove();
            }

        }
    }

    private abstract class TableSet<T> extends Sets.ImprovedAbstractSet<T> {

        private TableSet() {}

        public boolean isEmpty() {
            return StandardTable.this.backingMap.isEmpty();
        }

        public void clear() {
            StandardTable.this.backingMap.clear();
        }
    }
}
