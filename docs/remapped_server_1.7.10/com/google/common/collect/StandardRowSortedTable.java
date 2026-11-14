package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
class StandardRowSortedTable<R, C, V> extends StandardTable<R, C, V> implements RowSortedTable<R, C, V> {

    private static final long serialVersionUID = 0L;

    StandardRowSortedTable(SortedMap<R, Map<C, V>> sortedmap, Supplier<? extends Map<C, V>> supplier) {
        super(sortedmap, supplier);
    }

    private SortedMap<R, Map<C, V>> sortedBackingMap() {
        return (SortedMap) this.backingMap;
    }

    @Override
    public SortedSet<R> rowKeySet() {
        return (SortedSet) this.rowMap().keySet();
    }

    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return (SortedMap) super.rowMap();
    }

    @Override
    SortedMap<R, Map<C, V>> createRowMap() {
        return new StandardRowSortedTable.RowSortedMap();
    }

    private class RowSortedMap extends StandardTable<R, C, V>.RowMap implements SortedMap<R, Map<C, V>> {

        private RowSortedMap() {
            super();
        }

        @Override
        public SortedSet<R> keySet() {
            return (SortedSet) super.keySet();
        }

        @Override
        SortedSet<R> createKeySet() {
            return new Maps.SortedKeySet<>(this);
        }

        public Comparator<? super R> comparator() {
            return StandardRowSortedTable.this.sortedBackingMap().comparator();
        }

        public R firstKey() {
            return StandardRowSortedTable.this.sortedBackingMap().firstKey();
        }

        public R lastKey() {
            return StandardRowSortedTable.this.sortedBackingMap().lastKey();
        }

        public SortedMap<R, Map<C, V>> headMap(R r0) {
            Preconditions.checkNotNull(r0);
            return (new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().headMap(r0), StandardRowSortedTable.this.factory)).rowMap();
        }

        public SortedMap<R, Map<C, V>> subMap(R r0, R r1) {
            Preconditions.checkNotNull(r0);
            Preconditions.checkNotNull(r1);
            return (new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().subMap(r0, r1), StandardRowSortedTable.this.factory)).rowMap();
        }

        public SortedMap<R, Map<C, V>> tailMap(R r0) {
            Preconditions.checkNotNull(r0);
            return (new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().tailMap(r0), StandardRowSortedTable.this.factory)).rowMap();
        }
    }
}
