package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {

    private final transient ImmutableMapEntry<K, V>[] entries;
    private final transient ImmutableMapEntry<K, V>[] table;
    private final transient int mask;
    private static final double MAX_LOAD_FACTOR = 1.2D;
    private static final long serialVersionUID = 0L;

    RegularImmutableMap(ImmutableMapEntry.TerminalEntry<?, ?>... aimmutablemapentry_terminalentry) {
        this(aimmutablemapentry_terminalentry.length, aimmutablemapentry_terminalentry);
    }

    RegularImmutableMap(int i, ImmutableMapEntry.TerminalEntry<?, ?>[] aimmutablemapentry_terminalentry) {
        this.entries = this.createEntryArray(i);
        int j = Hashing.closedTableSize(i, 1.2D);

        this.table = this.createEntryArray(j);
        this.mask = j - 1;

        for (int k = 0; k < i; ++k) {
            ImmutableMapEntry.TerminalEntry immutablemapentry_terminalentry = aimmutablemapentry_terminalentry[k];
            Object object = immutablemapentry_terminalentry.getKey();
            int l = Hashing.smear(object.hashCode()) & this.mask;
            ImmutableMapEntry immutablemapentry = this.table[l];
            Object object1 = immutablemapentry == null ? immutablemapentry_terminalentry : new RegularImmutableMap.NonTerminalMapEntry<>(immutablemapentry_terminalentry, immutablemapentry);

            this.table[l] = (ImmutableMapEntry) object1;
            this.entries[k] = (ImmutableMapEntry) object1;
            this.checkNoConflictInBucket(object, (ImmutableMapEntry) object1, immutablemapentry);
        }

    }

    RegularImmutableMap(Entry<?, ?>[] aentry) {
        int i = aentry.length;

        this.entries = this.createEntryArray(i);
        int j = Hashing.closedTableSize(i, 1.2D);

        this.table = this.createEntryArray(j);
        this.mask = j - 1;

        for (int k = 0; k < i; ++k) {
            Entry entry = aentry[k];
            Object object = entry.getKey();
            Object object1 = entry.getValue();

            CollectPreconditions.checkEntryNotNull(object, object1);
            int l = Hashing.smear(object.hashCode()) & this.mask;
            ImmutableMapEntry immutablemapentry = this.table[l];
            Object object2 = immutablemapentry == null ? new ImmutableMapEntry.TerminalEntry<>(object, object1) : new RegularImmutableMap.NonTerminalMapEntry<>(object, object1, immutablemapentry);

            this.table[l] = (ImmutableMapEntry) object2;
            this.entries[k] = (ImmutableMapEntry) object2;
            this.checkNoConflictInBucket(object, (ImmutableMapEntry) object2, immutablemapentry);
        }

    }

    private void checkNoConflictInBucket(K k0, ImmutableMapEntry<K, V> immutablemapentry, ImmutableMapEntry<K, V> immutablemapentry1) {
        while (immutablemapentry1 != null) {
            checkNoConflict(!k0.equals(immutablemapentry1.getKey()), "key", immutablemapentry, immutablemapentry1);
            immutablemapentry1 = immutablemapentry1.getNextInKeyBucket();
        }

    }

    private ImmutableMapEntry<K, V>[] createEntryArray(int i) {
        return new ImmutableMapEntry[i];
    }

    @Override
    public V get(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = Hashing.smear(object.hashCode()) & this.mask;

            for (ImmutableMapEntry immutablemapentry = this.table[i]; immutablemapentry != null; immutablemapentry = immutablemapentry.getNextInKeyBucket()) {
                Object object1 = immutablemapentry.getKey();

                if (object.equals(object1)) {
                    return immutablemapentry.getValue();
                }
            }

            return null;
        }
    }

    public int size() {
        return this.entries.length;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new RegularImmutableMap.EntrySet();
    }

    private class EntrySet extends ImmutableMapEntrySet<K, V> {

        private EntrySet() {}

        @Override
        ImmutableMap<K, V> map() {
            return RegularImmutableMap.this;
        }

        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return this.asList().iterator();
        }

        @Override
        ImmutableList<Entry<K, V>> createAsList() {
            return new RegularImmutableAsList<>(this, RegularImmutableMap.this.entries);
        }
    }

    private static final class NonTerminalMapEntry<K, V> extends ImmutableMapEntry<K, V> {

        private final ImmutableMapEntry<K, V> nextInKeyBucket;

        NonTerminalMapEntry(K k0, V v0, ImmutableMapEntry<K, V> immutablemapentry) {
            super(k0, v0);
            this.nextInKeyBucket = immutablemapentry;
        }

        NonTerminalMapEntry(ImmutableMapEntry<K, V> immutablemapentry, ImmutableMapEntry<K, V> immutablemapentry1) {
            super(immutablemapentry);
            this.nextInKeyBucket = immutablemapentry1;
        }

        @Override
        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return this.nextInKeyBucket;
        }

        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return null;
        }
    }
}
