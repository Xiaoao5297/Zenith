package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible("unnecessary")
abstract class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V> {

    ImmutableMapEntry(K k0, V v0) {
        super(k0, v0);
        CollectPreconditions.checkEntryNotNull(k0, v0);
    }

    ImmutableMapEntry(ImmutableMapEntry<K, V> immutablemapentry) {
        super(immutablemapentry.getKey(), immutablemapentry.getValue());
    }

    @Nullable
    abstract ImmutableMapEntry<K, V> getNextInKeyBucket();

    @Nullable
    abstract ImmutableMapEntry<K, V> getNextInValueBucket();

    static final class TerminalEntry<K, V> extends ImmutableMapEntry<K, V> {

        TerminalEntry(ImmutableMapEntry<K, V> immutablemapentry) {
            super(immutablemapentry);
        }

        TerminalEntry(K k0, V v0) {
            super(k0, v0);
        }

        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return null;
        }

        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return null;
        }
    }
}
