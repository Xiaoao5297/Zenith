package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {

    static final double MAX_LOAD_FACTOR = 1.2D;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    private final transient ImmutableMapEntry<K, V>[] valueTable;
    private final transient ImmutableMapEntry<K, V>[] entries;
    private final transient int mask;
    private final transient int hashCode;
    private transient ImmutableBiMap<V, K> inverse;

    RegularImmutableBiMap(ImmutableMapEntry.TerminalEntry<?, ?>... aimmutablemapentry_terminalentry) {
        this(aimmutablemapentry_terminalentry.length, aimmutablemapentry_terminalentry);
    }

    RegularImmutableBiMap(int i, ImmutableMapEntry.TerminalEntry<?, ?>[] aimmutablemapentry_terminalentry) {
        int j = Hashing.closedTableSize(i, 1.2D);

        this.mask = j - 1;
        ImmutableMapEntry[] aimmutablemapentry = createEntryArray(j);
        ImmutableMapEntry[] aimmutablemapentry1 = createEntryArray(j);
        ImmutableMapEntry[] aimmutablemapentry2 = createEntryArray(i);
        int k = 0;

        for (int l = 0; l < i; ++l) {
            ImmutableMapEntry.TerminalEntry immutablemapentry_terminalentry = aimmutablemapentry_terminalentry[l];
            Object object = immutablemapentry_terminalentry.getKey();
            Object object1 = immutablemapentry_terminalentry.getValue();
            int i1 = object.hashCode();
            int j1 = object1.hashCode();
            int k1 = Hashing.smear(i1) & this.mask;
            int l1 = Hashing.smear(j1) & this.mask;
            ImmutableMapEntry immutablemapentry = aimmutablemapentry[k1];

            ImmutableMapEntry immutablemapentry1;

            for (immutablemapentry1 = immutablemapentry; immutablemapentry1 != null; immutablemapentry1 = immutablemapentry1.getNextInKeyBucket()) {
                checkNoConflict(!object.equals(immutablemapentry1.getKey()), "key", immutablemapentry_terminalentry, immutablemapentry1);
            }

            immutablemapentry1 = aimmutablemapentry1[l1];

            for (ImmutableMapEntry immutablemapentry2 = immutablemapentry1; immutablemapentry2 != null; immutablemapentry2 = immutablemapentry2.getNextInValueBucket()) {
                checkNoConflict(!object1.equals(immutablemapentry2.getValue()), "value", immutablemapentry_terminalentry, immutablemapentry2);
            }

            Object object2 = immutablemapentry == null && immutablemapentry1 == null ? immutablemapentry_terminalentry : new RegularImmutableBiMap.NonTerminalBiMapEntry<>(immutablemapentry_terminalentry, immutablemapentry, immutablemapentry1);

            aimmutablemapentry[k1] = (ImmutableMapEntry) object2;
            aimmutablemapentry1[l1] = (ImmutableMapEntry) object2;
            aimmutablemapentry2[l] = (ImmutableMapEntry) object2;
            k += i1 ^ j1;
        }

        this.keyTable = aimmutablemapentry;
        this.valueTable = aimmutablemapentry1;
        this.entries = aimmutablemapentry2;
        this.hashCode = k;
    }

    RegularImmutableBiMap(Entry<?, ?>[] aentry) {
        int i = aentry.length;
        int j = Hashing.closedTableSize(i, 1.2D);

        this.mask = j - 1;
        ImmutableMapEntry[] aimmutablemapentry = createEntryArray(j);
        ImmutableMapEntry[] aimmutablemapentry1 = createEntryArray(j);
        ImmutableMapEntry[] aimmutablemapentry2 = createEntryArray(i);
        int k = 0;

        for (int l = 0; l < i; ++l) {
            Entry entry = aentry[l];
            Object object = entry.getKey();
            Object object1 = entry.getValue();

            CollectPreconditions.checkEntryNotNull(object, object1);
            int i1 = object.hashCode();
            int j1 = object1.hashCode();
            int k1 = Hashing.smear(i1) & this.mask;
            int l1 = Hashing.smear(j1) & this.mask;
            ImmutableMapEntry immutablemapentry = aimmutablemapentry[k1];

            ImmutableMapEntry immutablemapentry1;

            for (immutablemapentry1 = immutablemapentry; immutablemapentry1 != null; immutablemapentry1 = immutablemapentry1.getNextInKeyBucket()) {
                checkNoConflict(!object.equals(immutablemapentry1.getKey()), "key", entry, immutablemapentry1);
            }

            immutablemapentry1 = aimmutablemapentry1[l1];

            for (ImmutableMapEntry immutablemapentry2 = immutablemapentry1; immutablemapentry2 != null; immutablemapentry2 = immutablemapentry2.getNextInValueBucket()) {
                checkNoConflict(!object1.equals(immutablemapentry2.getValue()), "value", entry, immutablemapentry2);
            }

            Object object2 = immutablemapentry == null && immutablemapentry1 == null ? new ImmutableMapEntry.TerminalEntry<>(object, object1) : new RegularImmutableBiMap.NonTerminalBiMapEntry<>(object, object1, immutablemapentry, immutablemapentry1);

            aimmutablemapentry[k1] = (ImmutableMapEntry) object2;
            aimmutablemapentry1[l1] = (ImmutableMapEntry) object2;
            aimmutablemapentry2[l] = (ImmutableMapEntry) object2;
            k += i1 ^ j1;
        }

        this.keyTable = aimmutablemapentry;
        this.valueTable = aimmutablemapentry1;
        this.entries = aimmutablemapentry2;
        this.hashCode = k;
    }

    private static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int i) {
        return new ImmutableMapEntry[i];
    }

    @Nullable
    @Override
    public V get(@Nullable Object object) {
        if (object == null) {
            return null;
        } else {
            int i = Hashing.smear(object.hashCode()) & this.mask;

            for (ImmutableMapEntry immutablemapentry = this.keyTable[i]; immutablemapentry != null; immutablemapentry = immutablemapentry.getNextInKeyBucket()) {
                if (object.equals(immutablemapentry.getKey())) {
                    return immutablemapentry.getValue();
                }
            }

            return null;
        }
    }

    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new ImmutableMapEntrySet<K, V>() {
            @Override
            ImmutableMap<K, V> map() {
                return RegularImmutableBiMap.this;
            }

            @Override
            public UnmodifiableIterator<Entry<K, V>> iterator() {
                return this.asList().iterator();
            }

            @Override
            ImmutableList<Entry<K, V>> createAsList() {
                return new RegularImmutableAsList<>(this, RegularImmutableBiMap.this.entries);
            }

            @Override
            boolean isHashCodeFast() {
                return true;
            }

            @Override
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }
        };
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    public int size() {
        return this.entries.length;
    }

    @Override
    public ImmutableBiMap<V, K> inverse() {
        ImmutableBiMap immutablebimap = this.inverse;

        return immutablebimap == null ? (this.inverse = new RegularImmutableBiMap.Inverse()) : immutablebimap;
    }

    private static class InverseSerializedForm<K, V> implements Serializable {

        private final ImmutableBiMap<K, V> forward;
        private static final long serialVersionUID = 1L;

        InverseSerializedForm(ImmutableBiMap<K, V> immutablebimap) {
            this.forward = immutablebimap;
        }

        Object readResolve() {
            return this.forward.inverse();
        }
    }

    private final class Inverse extends ImmutableBiMap<V, K> {

        private Inverse() {}

        public int size() {
            return this.inverse().size();
        }

        @Override
        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }

        @Override
        public K get(@Nullable Object object) {
            if (object == null) {
                return null;
            } else {
                int i = Hashing.smear(object.hashCode()) & RegularImmutableBiMap.this.mask;

                for (ImmutableMapEntry immutablemapentry = RegularImmutableBiMap.this.valueTable[i]; immutablemapentry != null; immutablemapentry = immutablemapentry.getNextInValueBucket()) {
                    if (object.equals(immutablemapentry.getValue())) {
                        return immutablemapentry.getKey();
                    }
                }

                return null;
            }
        }

        @Override
        ImmutableSet<Entry<V, K>> createEntrySet() {
            return new RegularImmutableBiMap.Inverse.InverseEntrySet();
        }

        @Override
        boolean isPartialView() {
            return false;
        }

        @Override
        Object writeReplace() {
            return new RegularImmutableBiMap.InverseSerializedForm<>(RegularImmutableBiMap.this);
        }

        final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {

            InverseEntrySet() {}

            @Override
            ImmutableMap<V, K> map() {
                return Inverse.this;
            }

            @Override
            boolean isHashCodeFast() {
                return true;
            }

            @Override
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }

            @Override
            public UnmodifiableIterator<Entry<V, K>> iterator() {
                return this.asList().iterator();
            }

            @Override
            ImmutableList<Entry<V, K>> createAsList() {
                return new ImmutableAsList<Entry<V, K>>() {
                    public Entry<V, K> get(int i) {
                        ImmutableMapEntry immutablemapentry = RegularImmutableBiMap.this.entries[i];

                        return Maps.immutableEntry(immutablemapentry.getValue(), immutablemapentry.getKey());
                    }

                    @Override
                    ImmutableCollection<Entry<V, K>> delegateCollection() {
                        return InverseEntrySet.this;
                    }
                };
            }
        }
    }

    private static final class NonTerminalBiMapEntry<K, V> extends ImmutableMapEntry<K, V> {

        @Nullable
        private final ImmutableMapEntry<K, V> nextInKeyBucket;
        @Nullable
        private final ImmutableMapEntry<K, V> nextInValueBucket;

        NonTerminalBiMapEntry(K k0, V v0, @Nullable ImmutableMapEntry<K, V> immutablemapentry, @Nullable ImmutableMapEntry<K, V> immutablemapentry1) {
            super(k0, v0);
            this.nextInKeyBucket = immutablemapentry;
            this.nextInValueBucket = immutablemapentry1;
        }

        NonTerminalBiMapEntry(ImmutableMapEntry<K, V> immutablemapentry, @Nullable ImmutableMapEntry<K, V> immutablemapentry1, @Nullable ImmutableMapEntry<K, V> immutablemapentry2) {
            super(immutablemapentry);
            this.nextInKeyBucket = immutablemapentry1;
            this.nextInValueBucket = immutablemapentry2;
        }

        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return this.nextInKeyBucket;
        }

        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return this.nextInValueBucket;
        }
    }
}
