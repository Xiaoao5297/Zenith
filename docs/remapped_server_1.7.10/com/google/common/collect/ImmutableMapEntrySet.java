package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Entry<K, V>> {

    ImmutableMapEntrySet() {}

    abstract ImmutableMap<K, V> map();

    public int size() {
        return this.map().size();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (!(object instanceof Entry)) {
            return false;
        } else {
            Entry entry = (Entry) object;
            Object object1 = this.map().get(entry.getKey());

            return object1 != null && object1.equals(entry.getValue());
        }
    }

    @Override
    boolean isPartialView() {
        return this.map().isPartialView();
    }

    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new ImmutableMapEntrySet.EntrySetSerializedForm<>(this.map());
    }

    @GwtIncompatible("serialization")
    private static class EntrySetSerializedForm<K, V> implements Serializable {

        final ImmutableMap<K, V> map;
        private static final long serialVersionUID = 0L;

        EntrySetSerializedForm(ImmutableMap<K, V> immutablemap) {
            this.map = immutablemap;
        }

        Object readResolve() {
            return this.map.entrySet();
        }
    }
}
