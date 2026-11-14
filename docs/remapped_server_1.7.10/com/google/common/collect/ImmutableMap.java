package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable {

    private static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Entry[0];
    private transient ImmutableSet<Entry<K, V>> entrySet;
    private transient ImmutableSet<K> keySet;
    private transient ImmutableCollection<V> values;
    private transient ImmutableSetMultimap<K, V> multimapView;

    public static <K, V> ImmutableMap<K, V> of() {
        return ImmutableBiMap.of();
    }

    public static <K, V> ImmutableMap<K, V> of(K k0, V v0) {
        return ImmutableBiMap.of(k0, v0);
    }

    public static <K, V> ImmutableMap<K, V> of(K k0, V v0, K k1, V v1) {
        return new RegularImmutableMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1)});
    }

    public static <K, V> ImmutableMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        return new RegularImmutableMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2)});
    }

    public static <K, V> ImmutableMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        return new RegularImmutableMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3)});
    }

    public static <K, V> ImmutableMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new RegularImmutableMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)});
    }

    static <K, V> ImmutableMapEntry.TerminalEntry<K, V> entryOf(K k0, V v0) {
        CollectPreconditions.checkEntryNotNull(k0, v0);
        return new ImmutableMapEntry.TerminalEntry<>(k0, v0);
    }

    public static <K, V> ImmutableMap.Builder<K, V> builder() {
        return new ImmutableMap.Builder<>();
    }

    static void checkNoConflict(boolean flag, String s, Entry<?, ?> entry, Entry<?, ?> entry1) {
        if (!flag) {
            throw new IllegalArgumentException("Multiple entries with same " + s + ": " + entry + " and " + entry1);
        }
    }

    public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableMap && !(map instanceof ImmutableSortedMap)) {
            ImmutableMap immutablemap = (ImmutableMap) map;

            if (!immutablemap.isPartialView()) {
                return immutablemap;
            }
        } else if (map instanceof EnumMap) {
            return copyOfEnumMapUnsafe(map);
        }

        Entry[] aentry = (Entry[]) map.entrySet().toArray(ImmutableMap.EMPTY_ENTRY_ARRAY);

        switch (aentry.length) {
            case 0:
                return of();
            case 1:
                Entry entry = aentry[0];

                return of(entry.getKey(), entry.getValue());
            default:
                return new RegularImmutableMap<>(aentry);
        }
    }

    private static <K, V> ImmutableMap<K, V> copyOfEnumMapUnsafe(Map<? extends K, ? extends V> map) {
        return copyOfEnumMap((EnumMap) map);
    }

    private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(Map<K, ? extends V> map) {
        EnumMap enummap = new EnumMap(map);
        Iterator iterator = enummap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue());
        }

        return ImmutableEnumMap.asImmutable(enummap);
    }

    ImmutableMap() {}

    /** @deprecated */
    @Deprecated
    public final V put(K k0, V v0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final V remove(Object object) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean containsKey(@Nullable Object object) {
        return this.get(object) != null;
    }

    public boolean containsValue(@Nullable Object object) {
        return this.values().contains(object);
    }

    public abstract V get(@Nullable Object object);

    public ImmutableSet<Entry<K, V>> entrySet() {
        ImmutableSet immutableset = this.entrySet;

        return immutableset == null ? (this.entrySet = this.createEntrySet()) : immutableset;
    }

    abstract ImmutableSet<Entry<K, V>> createEntrySet();

    public ImmutableSet<K> keySet() {
        ImmutableSet immutableset = this.keySet;

        return immutableset == null ? (this.keySet = this.createKeySet()) : immutableset;
    }

    ImmutableSet<K> createKeySet() {
        return new ImmutableMapKeySet<>(this);
    }

    public ImmutableCollection<V> values() {
        ImmutableCollection immutablecollection = this.values;

        return immutablecollection == null ? (this.values = new ImmutableMapValues<>(this)) : immutablecollection;
    }

    @Beta
    public ImmutableSetMultimap<K, V> asMultimap() {
        ImmutableSetMultimap immutablesetmultimap = this.multimapView;

        return immutablesetmultimap == null ? (this.multimapView = this.createMultimapView()) : immutablesetmultimap;
    }

    private ImmutableSetMultimap<K, V> createMultimapView() {
        ImmutableMap immutablemap = this.viewMapValuesAsSingletonSets();

        return new ImmutableSetMultimap<>(immutablemap, immutablemap.size(), (Comparator) null);
    }

    private ImmutableMap<K, ImmutableSet<V>> viewMapValuesAsSingletonSets() {
        return new ImmutableMap.MapViewOfValuesAsSingletonSets<>(this);
    }

    public boolean equals(@Nullable Object object) {
        return Maps.equalsImpl(this, object);
    }

    abstract boolean isPartialView();

    public int hashCode() {
        return this.entrySet().hashCode();
    }

    public String toString() {
        return Maps.toStringImpl(this);
    }

    Object writeReplace() {
        return new ImmutableMap.SerializedForm(this);
    }

    static class SerializedForm implements Serializable {

        private final Object[] keys;
        private final Object[] values;
        private static final long serialVersionUID = 0L;

        SerializedForm(ImmutableMap<?, ?> immutablemap) {
            this.keys = new Object[immutablemap.size()];
            this.values = new Object[immutablemap.size()];
            int i = 0;

            for (Iterator iterator = immutablemap.entrySet().iterator(); iterator.hasNext(); ++i) {
                Entry entry = (Entry) iterator.next();

                this.keys[i] = entry.getKey();
                this.values[i] = entry.getValue();
            }

        }

        Object readResolve() {
            ImmutableMap.Builder immutablemap_builder = new ImmutableMap.Builder<>();

            return this.createMap(immutablemap_builder);
        }

        Object createMap(ImmutableMap.Builder<Object, Object> immutablemap_builder) {
            for (int i = 0; i < this.keys.length; ++i) {
                immutablemap_builder.put(this.keys[i], this.values[i]);
            }

            return immutablemap_builder.build();
        }
    }

    private static final class MapViewOfValuesAsSingletonSets<K, V> extends ImmutableMap<K, ImmutableSet<V>> {

        private final ImmutableMap<K, V> delegate;

        MapViewOfValuesAsSingletonSets(ImmutableMap<K, V> immutablemap) {
            this.delegate = (ImmutableMap) Preconditions.checkNotNull(immutablemap);
        }

        public int size() {
            return this.delegate.size();
        }

        @Override
        public boolean containsKey(@Nullable Object object) {
            return this.delegate.containsKey(object);
        }

        @Override
        public ImmutableSet<V> get(@Nullable Object object) {
            Object object1 = this.delegate.get(object);

            return object1 == null ? null : ImmutableSet.of(object1);
        }

        @Override
        boolean isPartialView() {
            return false;
        }

        @Override
        ImmutableSet<Entry<K, ImmutableSet<V>>> createEntrySet() {
            return new ImmutableMapEntrySet<K, ImmutableSet<V>>() {
                @Override
                ImmutableMap<K, ImmutableSet<V>> map() {
                    return MapViewOfValuesAsSingletonSets.this;
                }

                @Override
                public UnmodifiableIterator<Entry<K, ImmutableSet<V>>> iterator() {
                    final UnmodifiableIterator unmodifiableiterator = MapViewOfValuesAsSingletonSets.this.delegate.entrySet().iterator();

                    return new UnmodifiableIterator<Entry<K, ImmutableSet<V>>>() {
                        public boolean hasNext() {
                            return unmodifiableiterator.hasNext();
                        }

                        public Entry<K, ImmutableSet<V>> next() {
                            final Entry entry = (Entry) unmodifiableiterator.next();

                            return new AbstractMapEntry<K, ImmutableSet<V>>() {
                                @Override
                                public K getKey() {
                                    return entry.getKey();
                                }

                                @Override
                                public ImmutableSet<V> getValue() {
                                    return ImmutableSet.of(entry.getValue());
                                }
                            };
                        }
                    };
                }
            };
        }
    }

    public static class Builder<K, V> {

        ImmutableMapEntry.TerminalEntry<K, V>[] entries;
        int size;

        public Builder() {
            this(4);
        }

        Builder(int i) {
            this.entries = new ImmutableMapEntry.TerminalEntry[i];
            this.size = 0;
        }

        private void ensureCapacity(int i) {
            if (i > this.entries.length) {
                this.entries = (ImmutableMapEntry.TerminalEntry[]) ObjectArrays.arraysCopyOf(this.entries, ImmutableCollection.Builder.expandedCapacity(this.entries.length, i));
            }

        }

        public ImmutableMap.Builder<K, V> put(K k0, V v0) {
            this.ensureCapacity(this.size + 1);
            ImmutableMapEntry.TerminalEntry immutablemapentry_terminalentry = ImmutableMap.entryOf(k0, v0);

            this.entries[this.size++] = immutablemapentry_terminalentry;
            return this;
        }

        public ImmutableMap.Builder<K, V> put(Entry<? extends K, ? extends V> entry) {
            return this.put(entry.getKey(), entry.getValue());
        }

        public ImmutableMap.Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
            this.ensureCapacity(this.size + map.size());
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.put(entry);
            }

            return this;
        }

        public ImmutableMap<K, V> build() {
            switch (this.size) {
                case 0:
                    return ImmutableMap.of();
                case 1:
                    return ImmutableMap.of(this.entries[0].getKey(), this.entries[0].getValue());
                default:
                    return new RegularImmutableMap<>(this.size, this.entries);
            }
        }
    }
}
