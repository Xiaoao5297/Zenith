package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
@Beta
public class TreeBasedTable<R, C, V> extends StandardRowSortedTable<R, C, V> {

    private final Comparator<? super C> columnComparator;
    private static final long serialVersionUID = 0L;

    public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create() {
        return new TreeBasedTable<>(Ordering.natural(), Ordering.natural());
    }

    public static <R, C, V> TreeBasedTable<R, C, V> create(Comparator<? super R> comparator, Comparator<? super C> comparator1) {
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(comparator1);
        return new TreeBasedTable<>(comparator, comparator1);
    }

    public static <R, C, V> TreeBasedTable<R, C, V> create(TreeBasedTable<R, C, ? extends V> treebasedtable) {
        TreeBasedTable treebasedtable1 = new TreeBasedTable<>(treebasedtable.rowComparator(), treebasedtable.columnComparator());

        treebasedtable1.putAll(treebasedtable);
        return treebasedtable1;
    }

    TreeBasedTable(Comparator<? super R> comparator, Comparator<? super C> comparator1) {
        super((SortedMap) (new TreeMap(comparator)), new TreeBasedTable.Factory<>(comparator1));
        this.columnComparator = comparator1;
    }

    public Comparator<? super R> rowComparator() {
        return this.rowKeySet().comparator();
    }

    public Comparator<? super C> columnComparator() {
        return this.columnComparator;
    }

    @Override
    public SortedMap<C, V> row(R r0) {
        return new TreeBasedTable.TreeRow(r0);
    }

    @Override
    public SortedSet<R> rowKeySet() {
        return super.rowKeySet();
    }

    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return super.rowMap();
    }

    @Override
    Iterator<C> createColumnKeyIterator() {
        final Comparator comparator = this.columnComparator();
        final UnmodifiableIterator unmodifiableiterator = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function<Map<C, V>, Iterator<C>>() {
            public Iterator<C> apply(Map<C, V> map) {
                return map.keySet().iterator();
            }
        }), comparator);

        return new AbstractIterator<C>() {
            C lastValue;

            @Override
            protected C computeNext() {
                Object object;
                boolean flag;

                do {
                    if (!unmodifiableiterator.hasNext()) {
                        this.lastValue = null;
                        return this.endOfData();
                    }

                    object = unmodifiableiterator.next();
                    flag = this.lastValue != null && comparator.compare(object, this.lastValue) == 0;
                } while (flag);

                this.lastValue = object;
                return this.lastValue;
            }
        };
    }

    private class TreeRow extends StandardTable<R, C, V>.Row implements SortedMap<C, V> {

        @Nullable
        final C lowerBound;
        @Nullable
        final C upperBound;
        transient SortedMap<C, V> wholeRow;

        TreeRow(R r0) {
            this(r0, (Object) null, (Object) null);
        }

        TreeRow(R r0, @Nullable C c0, @Nullable C c1) {
            super(r0);
            this.lowerBound = c0;
            this.upperBound = c1;
            Preconditions.checkArgument(c0 == null || c1 == null || this.compare(c0, c1) <= 0);
        }

        @Override
        public SortedSet<C> keySet() {
            return new Maps.SortedKeySet<>(this);
        }

        public Comparator<? super C> comparator() {
            return TreeBasedTable.this.columnComparator();
        }

        int compare(Object object, Object object1) {
            Comparator comparator = this.comparator();

            return comparator.compare(object, object1);
        }

        boolean rangeContains(@Nullable Object object) {
            return object != null && (this.lowerBound == null || this.compare(this.lowerBound, object) <= 0) && (this.upperBound == null || this.compare(this.upperBound, object) > 0);
        }

        public SortedMap<C, V> subMap(C c0, C c1) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c0)) && this.rangeContains(Preconditions.checkNotNull(c1)));
            return TreeBasedTable.this.new TreeRow(this.rowKey, c0, c1);
        }

        public SortedMap<C, V> headMap(C c0) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c0)));
            return TreeBasedTable.this.new TreeRow(this.rowKey, this.lowerBound, c0);
        }

        public SortedMap<C, V> tailMap(C c0) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c0)));
            return TreeBasedTable.this.new TreeRow(this.rowKey, c0, this.upperBound);
        }

        public C firstKey() {
            SortedMap sortedmap = this.backingRowMap();

            if (sortedmap == null) {
                throw new NoSuchElementException();
            } else {
                return this.backingRowMap().firstKey();
            }
        }

        public C lastKey() {
            SortedMap sortedmap = this.backingRowMap();

            if (sortedmap == null) {
                throw new NoSuchElementException();
            } else {
                return this.backingRowMap().lastKey();
            }
        }

        SortedMap<C, V> wholeRow() {
            if (this.wholeRow == null || this.wholeRow.isEmpty() && TreeBasedTable.this.backingMap.containsKey(this.rowKey)) {
                this.wholeRow = (SortedMap) TreeBasedTable.this.backingMap.get(this.rowKey);
            }

            return this.wholeRow;
        }

        @Override
        SortedMap<C, V> backingRowMap() {
            return (SortedMap) super.backingRowMap();
        }

        @Override
        SortedMap<C, V> computeBackingRowMap() {
            SortedMap sortedmap = this.wholeRow();

            if (sortedmap != null) {
                if (this.lowerBound != null) {
                    sortedmap = sortedmap.tailMap(this.lowerBound);
                }

                if (this.upperBound != null) {
                    sortedmap = sortedmap.headMap(this.upperBound);
                }

                return sortedmap;
            } else {
                return null;
            }
        }

        @Override
        void maintainEmptyInvariant() {
            if (this.wholeRow() != null && this.wholeRow.isEmpty()) {
                TreeBasedTable.this.backingMap.remove(this.rowKey);
                this.wholeRow = null;
                this.backingRowMap = null;
            }

        }

        @Override
        public boolean containsKey(Object object) {
            return this.rangeContains(object) && super.containsKey(object);
        }

        @Override
        public V put(C c0, V v0) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c0)));
            return super.put(c0, v0);
        }
    }

    private static class Factory<C, V> implements Supplier<TreeMap<C, V>>, Serializable {

        final Comparator<? super C> comparator;
        private static final long serialVersionUID = 0L;

        Factory(Comparator<? super C> comparator) {
            this.comparator = comparator;
        }

        @Override
        public TreeMap<C, V> get() {
            return new TreeMap(this.comparator);
        }
    }
}
