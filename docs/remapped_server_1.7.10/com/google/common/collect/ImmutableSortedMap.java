package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements NavigableMap<K, V> {

    private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
    private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new EmptyImmutableSortedMap<>(ImmutableSortedMap.NATURAL_ORDER);
    private transient ImmutableSortedMap<K, V> descendingMap;
    private static final long serialVersionUID = 0L;

    static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator) {
        return (ImmutableSortedMap) (Ordering.natural().equals(comparator) ? of() : new EmptyImmutableSortedMap<>(comparator));
    }

    static <K, V> ImmutableSortedMap<K, V> fromSortedEntries(Comparator<? super K> comparator, int i, Entry<K, V>[] aentry) {
        if (i == 0) {
            return emptyMap(comparator);
        } else {
            ImmutableList.Builder immutablelist_builder = ImmutableList.builder();
            ImmutableList.Builder immutablelist_builder1 = ImmutableList.builder();

            for (int j = 0; j < i; ++j) {
                Entry entry = aentry[j];

                immutablelist_builder.add(entry.getKey());
                immutablelist_builder1.add(entry.getValue());
            }

            return new RegularImmutableSortedMap<>(new RegularImmutableSortedSet<>(immutablelist_builder.build(), comparator), immutablelist_builder1.build());
        }
    }

    static <K, V> ImmutableSortedMap<K, V> from(ImmutableSortedSet<K> immutablesortedset, ImmutableList<V> immutablelist) {
        return (ImmutableSortedMap) (immutablesortedset.isEmpty() ? emptyMap(immutablesortedset.comparator()) : new RegularImmutableSortedMap<>((RegularImmutableSortedSet) immutablesortedset, immutablelist));
    }

    public static <K, V> ImmutableSortedMap<K, V> of() {
        return ImmutableSortedMap.NATURAL_EMPTY_MAP;
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k0, V v0) {
        return from(ImmutableSortedSet.of(k0), ImmutableList.of(v0));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1) {
        return fromEntries(Ordering.natural(), false, 2, entryOf(k0, v0), entryOf(k1, v1));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        return fromEntries(Ordering.natural(), false, 3, entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        return fromEntries(Ordering.natural(), false, 4, entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3));
    }

    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return fromEntries(Ordering.natural(), false, 5, entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4));
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        Ordering ordering = Ordering.natural();

        return copyOfInternal(map, ordering);
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
        return copyOfInternal(map, (Comparator) Preconditions.checkNotNull(comparator));
    }

    public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> sortedmap) {
        Comparator comparator = sortedmap.comparator();

        if (comparator == null) {
            comparator = ImmutableSortedMap.NATURAL_ORDER;
        }

        return copyOfInternal(sortedmap, comparator);
    }

    private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
        boolean flag = false;

        if (map instanceof SortedMap) {
            SortedMap sortedmap = (SortedMap) map;
            Comparator comparator1 = sortedmap.comparator();

            flag = comparator1 == null ? comparator == ImmutableSortedMap.NATURAL_ORDER : comparator.equals(comparator1);
        }

        if (flag && map instanceof ImmutableSortedMap) {
            ImmutableSortedMap immutablesortedmap = (ImmutableSortedMap) map;

            if (!immutablesortedmap.isPartialView()) {
                return immutablesortedmap;
            }
        }

        Entry[] aentry = (Entry[]) map.entrySet().toArray(new Entry[0]);

        return fromEntries(comparator, flag, aentry.length, aentry);
    }

    static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean flag, int i, Entry<K, V>... aentry) {
        for (int j = 0; j < i; ++j) {
            Entry entry = aentry[j];

            aentry[j] = entryOf(entry.getKey(), entry.getValue());
        }

        if (!flag) {
            sortEntries(comparator, i, aentry);
            validateEntries(i, aentry, comparator);
        }

        return fromSortedEntries(comparator, i, aentry);
    }

    private static <K, V> void sortEntries(Comparator<? super K> comparator, int i, Entry<K, V>[] aentry) {
        Arrays.sort(aentry, 0, i, Ordering.from(comparator).onKeys());
    }

    private static <K, V> void validateEntries(int i, Entry<K, V>[] aentry, Comparator<? super K> comparator) {
        for (int j = 1; j < i; ++j) {
            checkNoConflict(comparator.compare(aentry[j - 1].getKey(), aentry[j].getKey()) != 0, "key", aentry[j - 1], aentry[j]);
        }

    }

    public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> naturalOrder() {
        return new ImmutableSortedMap.Builder<>(Ordering.natural());
    }

    public static <K, V> ImmutableSortedMap.Builder<K, V> orderedBy(Comparator<K> comparator) {
        return new ImmutableSortedMap.Builder<>(comparator);
    }

    public static <K extends Comparable<?>, V> ImmutableSortedMap.Builder<K, V> reverseOrder() {
        return new ImmutableSortedMap.Builder<>(Ordering.natural().reverse());
    }

    ImmutableSortedMap() {}

    ImmutableSortedMap(ImmutableSortedMap<K, V> immutablesortedmap) {
        this.descendingMap = immutablesortedmap;
    }

    public int size() {
        return this.values().size();
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.values().contains(object);
    }

    @Override
    boolean isPartialView() {
        return this.keySet().isPartialView() || this.values().isPartialView();
    }

    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        return super.entrySet();
    }

    @Override
    public abstract ImmutableSortedSet<K> keySet();

    @Override
    public abstract ImmutableCollection<V> values();

    public Comparator<? super K> comparator() {
        return this.keySet().comparator();
    }

    public K firstKey() {
        return this.keySet().first();
    }

    public K lastKey() {
        return this.keySet().last();
    }

    public ImmutableSortedMap<K, V> headMap(K k0) {
        return this.headMap(k0, false);
    }

    public abstract ImmutableSortedMap<K, V> headMap(K k0, boolean flag);

    public ImmutableSortedMap<K, V> subMap(K k0, K k1) {
        return this.subMap(k0, true, k1, false);
    }

    public ImmutableSortedMap<K, V> subMap(K k0, boolean flag, K k1, boolean flag1) {
        Preconditions.checkNotNull(k0);
        Preconditions.checkNotNull(k1);
        Preconditions.checkArgument(this.comparator().compare(k0, k1) <= 0, "expected fromKey <= toKey but %s > %s", k0, k1);
        return this.headMap(k1, flag1).tailMap(k0, flag);
    }

    public ImmutableSortedMap<K, V> tailMap(K k0) {
        return this.tailMap(k0, true);
    }

    public abstract ImmutableSortedMap<K, V> tailMap(K k0, boolean flag);

    public Entry<K, V> lowerEntry(K k0) {
        return this.headMap(k0, false).lastEntry();
    }

    public K lowerKey(K k0) {
        return Maps.keyOrNull(this.lowerEntry(k0));
    }

    public Entry<K, V> floorEntry(K k0) {
        return this.headMap(k0, true).lastEntry();
    }

    public K floorKey(K k0) {
        return Maps.keyOrNull(this.floorEntry(k0));
    }

    public Entry<K, V> ceilingEntry(K k0) {
        return this.tailMap(k0, true).firstEntry();
    }

    public K ceilingKey(K k0) {
        return Maps.keyOrNull(this.ceilingEntry(k0));
    }

    public Entry<K, V> higherEntry(K k0) {
        return this.tailMap(k0, false).firstEntry();
    }

    public K higherKey(K k0) {
        return Maps.keyOrNull(this.higherEntry(k0));
    }

    public Entry<K, V> firstEntry() {
        return this.isEmpty() ? null : (Entry) this.entrySet().asList().get(0);
    }

    public Entry<K, V> lastEntry() {
        return this.isEmpty() ? null : (Entry) this.entrySet().asList().get(this.size() - 1);
    }

    /** @deprecated */
    @Deprecated
    public final Entry<K, V> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final Entry<K, V> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    public ImmutableSortedMap<K, V> descendingMap() {
        ImmutableSortedMap immutablesortedmap = this.descendingMap;

        if (immutablesortedmap == null) {
            immutablesortedmap = this.descendingMap = this.createDescendingMap();
        }

        return immutablesortedmap;
    }

    abstract ImmutableSortedMap<K, V> createDescendingMap();

    public ImmutableSortedSet<K> navigableKeySet() {
        return this.keySet();
    }

    public ImmutableSortedSet<K> descendingKeySet() {
        return this.keySet().descendingSet();
    }

    @Override
    Object writeReplace() {
        return new ImmutableSortedMap.SerializedForm(this);
    }

    private static class SerializedForm extends ImmutableMap.SerializedForm {

        private final Comparator<Object> comparator;
        private static final long serialVersionUID = 0L;

        SerializedForm(ImmutableSortedMap<?, ?> immutablesortedmap) {
            super(immutablesortedmap);
            this.comparator = immutablesortedmap.comparator();
        }

        @Override
        Object readResolve() {
            ImmutableSortedMap.Builder immutablesortedmap_builder = new ImmutableSortedMap.Builder<>(this.comparator);

            return this.createMap(immutablesortedmap_builder);
        }
    }

    public static class Builder<K, V> extends ImmutableMap.Builder<K, V> {

        private final Comparator<? super K> comparator;

        public Builder(Comparator<? super K> comparator) {
            this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
        }

        @Override
        public ImmutableSortedMap.Builder<K, V> put(K k0, V v0) {
            super.put(k0, v0);
            return this;
        }

        @Override
        public ImmutableSortedMap.Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
            super.put(entry);
            return this;
        }

        @Override
        public ImmutableSortedMap.Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
            super.putAll(map);
            return this;
        }

        @Override
        public ImmutableSortedMap<K, V> build() {
            return ImmutableSortedMap.fromEntries(this.comparator, false, this.size, this.entries);
        }
    }
}
