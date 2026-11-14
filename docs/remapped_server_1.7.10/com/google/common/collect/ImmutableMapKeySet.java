package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class ImmutableMapKeySet<K, V> extends ImmutableSet<K> {

    private final ImmutableMap<K, V> map;

    ImmutableMapKeySet(ImmutableMap<K, V> immutablemap) {
        this.map = immutablemap;
    }

    public int size() {
        return this.map.size();
    }

    @Override
    public UnmodifiableIterator<K> iterator() {
        return this.asList().iterator();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.map.containsKey(object);
    }

    @Override
    ImmutableList<K> createAsList() {
        final ImmutableList immutablelist = this.map.entrySet().asList();

        return new ImmutableAsList<K>() {
            public K get(int i) {
                return ((Entry) immutablelist.get(i)).getKey();
            }

            @Override
            ImmutableCollection<K> delegateCollection() {
                return ImmutableMapKeySet.this;
            }
        };
    }

    @Override
    boolean isPartialView() {
        return true;
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new ImmutableMapKeySet.KeySetSerializedForm<>(this.map);
    }

    @GwtIncompatible("serialization")
    private static class KeySetSerializedForm<K> implements Serializable {

        final ImmutableMap<K, ?> map;
        private static final long serialVersionUID = 0L;

        KeySetSerializedForm(ImmutableMap<K, ?> immutablemap) {
            this.map = immutablemap;
        }

        Object readResolve() {
            return this.map.keySet();
        }
    }
}
