package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Map.Entry;

@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableBiMap<K, V> extends ImmutableMap<K, V> implements BiMap<K, V> {

    private static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Entry[0];

    public static <K, V> ImmutableBiMap<K, V> of() {
        return EmptyImmutableBiMap.INSTANCE;
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k0, V v0) {
        return new SingletonImmutableBiMap<>(k0, v0);
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k0, V v0, K k1, V v1) {
        return new RegularImmutableBiMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1)});
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        return new RegularImmutableBiMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2)});
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        return new RegularImmutableBiMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3)});
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new RegularImmutableBiMap<>(new ImmutableMapEntry.TerminalEntry[]{entryOf(k0, v0), entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)});
    }

    public static <K, V> ImmutableBiMap.Builder<K, V> builder() {
        return new ImmutableBiMap.Builder<>();
    }

    public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableBiMap) {
            ImmutableBiMap immutablebimap = (ImmutableBiMap) map;

            if (!immutablebimap.isPartialView()) {
                return immutablebimap;
            }
        }

        Entry[] aentry = (Entry[]) map.entrySet().toArray(ImmutableBiMap.EMPTY_ENTRY_ARRAY);

        switch (aentry.length) {
            case 0:
                return of();
            case 1:
                Entry entry = aentry[0];

                return of(entry.getKey(), entry.getValue());
            default:
                return new RegularImmutableBiMap<>(aentry);
        }
    }

    ImmutableBiMap() {}

    @Override
    public abstract ImmutableBiMap<V, K> inverse();

    @Override
    public ImmutableSet<V> values() {
        return this.inverse().keySet();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public V forcePut(K k0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    Object writeReplace() {
        return new ImmutableBiMap.SerializedForm(this);
    }

    private static class SerializedForm extends ImmutableMap.SerializedForm {

        private static final long serialVersionUID = 0L;

        SerializedForm(ImmutableBiMap<?, ?> immutablebimap) {
            super(immutablebimap);
        }

        @Override
        Object readResolve() {
            ImmutableBiMap.Builder immutablebimap_builder = new ImmutableBiMap.Builder<>();

            return this.createMap(immutablebimap_builder);
        }
    }

    public static final class Builder<K, V> extends ImmutableMap.Builder<K, V> {

        public Builder() {}

        @Override
        public ImmutableBiMap.Builder<K, V> put(K k0, V v0) {
            super.put(k0, v0);
            return this;
        }

        @Override
        public ImmutableBiMap.Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
            super.putAll(map);
            return this;
        }

        @Override
        public ImmutableBiMap<K, V> build() {
            switch (this.size) {
                case 0:
                    return ImmutableBiMap.of();
                case 1:
                    return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
                default:
                    return new RegularImmutableBiMap<>(this.size, this.entries);
            }
        }
    }
}
