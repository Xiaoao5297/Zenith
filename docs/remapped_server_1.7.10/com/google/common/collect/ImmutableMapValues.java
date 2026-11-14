package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {

    private final ImmutableMap<K, V> map;

    ImmutableMapValues(ImmutableMap<K, V> immutablemap) {
        this.map = immutablemap;
    }

    public int size() {
        return this.map.size();
    }

    @Override
    public UnmodifiableIterator<V> iterator() {
        return Maps.valueIterator(this.map.entrySet().iterator());
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return object != null && Iterators.contains(this.iterator(), object);
    }

    @Override
    boolean isPartialView() {
        return true;
    }

    @Override
    ImmutableList<V> createAsList() {
        final ImmutableList immutablelist = this.map.entrySet().asList();

        return new ImmutableAsList<V>() {
            public V get(int i) {
                return ((Entry) immutablelist.get(i)).getValue();
            }

            @Override
            ImmutableCollection<V> delegateCollection() {
                return ImmutableMapValues.this;
            }
        };
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new ImmutableMapValues.SerializedForm<>(this.map);
    }

    @GwtIncompatible("serialization")
    private static class SerializedForm<V> implements Serializable {

        final ImmutableMap<?, V> map;
        private static final long serialVersionUID = 0L;

        SerializedForm(ImmutableMap<?, V> immutablemap) {
            this.map = immutablemap;
        }

        Object readResolve() {
            return this.map.values();
        }
    }
}
