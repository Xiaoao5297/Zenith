package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true, emulated = true)
public final class LinkedHashMultimap<K, V> extends AbstractSetMultimap<K, V> {

    private static final int DEFAULT_KEY_CAPACITY = 16;
    private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
    @VisibleForTesting
    static final double VALUE_SET_LOAD_FACTOR = 1.0D;
    @VisibleForTesting
    transient int valueSetCapacity = 2;
    private transient LinkedHashMultimap.ValueEntry<K, V> multimapHeaderEntry;
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 1L;

    public static <K, V> LinkedHashMultimap<K, V> create() {
        return new LinkedHashMultimap<>(16, 2);
    }

    public static <K, V> LinkedHashMultimap<K, V> create(int i, int j) {
        return new LinkedHashMultimap<>(Maps.capacity(i), Maps.capacity(j));
    }

    public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        LinkedHashMultimap linkedhashmultimap = create(multimap.keySet().size(), 2);

        linkedhashmultimap.putAll(multimap);
        return linkedhashmultimap;
    }

    private static <K, V> void succeedsInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink, LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink1) {
        linkedhashmultimap_valuesetlink.setSuccessorInValueSet(linkedhashmultimap_valuesetlink1);
        linkedhashmultimap_valuesetlink1.setPredecessorInValueSet(linkedhashmultimap_valuesetlink);
    }

    private static <K, V> void succeedsInMultimap(LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry, LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry1) {
        linkedhashmultimap_valueentry.setSuccessorInMultimap(linkedhashmultimap_valueentry1);
        linkedhashmultimap_valueentry1.setPredecessorInMultimap(linkedhashmultimap_valueentry);
    }

    private static <K, V> void deleteFromValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink) {
        succeedsInValueSet(linkedhashmultimap_valuesetlink.getPredecessorInValueSet(), linkedhashmultimap_valuesetlink.getSuccessorInValueSet());
    }

    private static <K, V> void deleteFromMultimap(LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry) {
        succeedsInMultimap(linkedhashmultimap_valueentry.getPredecessorInMultimap(), linkedhashmultimap_valueentry.getSuccessorInMultimap());
    }

    private LinkedHashMultimap(int i, int j) {
        super(new LinkedHashMap(i));
        CollectPreconditions.checkNonnegative(j, "expectedValuesPerKey");
        this.valueSetCapacity = j;
        this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry<>((Object) null, (Object) null, 0, (LinkedHashMultimap.ValueEntry) null);
        succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    }

    @Override
    Set<V> createCollection() {
        return new LinkedHashSet(this.valueSetCapacity);
    }

    @Override
    Collection<V> createCollection(K k0) {
        return new LinkedHashMultimap.ValueSet(k0, this.valueSetCapacity);
    }

    @Override
    public Set<V> replaceValues(@Nullable K k0, Iterable<? extends V> iterable) {
        return super.replaceValues(k0, iterable);
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return super.entries();
    }

    @Override
    public Collection<V> values() {
        return super.values();
    }

    @Override
    Iterator<Entry<K, V>> entryIterator() {
        return new Iterator<Entry<K, V>>() {
            LinkedHashMultimap.ValueEntry<K, V> nextEntry;
            LinkedHashMultimap.ValueEntry<K, V> toRemove;

            {
                this.nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
            }

            public boolean hasNext() {
                return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
            }

            public Entry<K, V> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = this.nextEntry;

                    this.toRemove = linkedhashmultimap_valueentry;
                    this.nextEntry = this.nextEntry.successorInMultimap;
                    return linkedhashmultimap_valueentry;
                }
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.toRemove != null);
                LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
                this.toRemove = null;
            }
        };
    }

    @Override
    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entryIterator());
    }

    @Override
    public void clear() {
        super.clear();
        succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(this.valueSetCapacity);
        objectoutputstream.writeInt(this.keySet().size());
        Iterator iterator = this.keySet().iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            objectoutputstream.writeObject(object);
        }

        objectoutputstream.writeInt(this.size());
        iterator = this.entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            objectoutputstream.writeObject(entry.getKey());
            objectoutputstream.writeObject(entry.getValue());
        }

    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry<>((Object) null, (Object) null, 0, (LinkedHashMultimap.ValueEntry) null);
        succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
        this.valueSetCapacity = objectinputstream.readInt();
        int i = objectinputstream.readInt();
        LinkedHashMap linkedhashmap = new LinkedHashMap(Maps.capacity(i));

        int j;

        for (j = 0; j < i; ++j) {
            Object object = objectinputstream.readObject();

            linkedhashmap.put(object, this.createCollection(object));
        }

        j = objectinputstream.readInt();

        for (int k = 0; k < j; ++k) {
            Object object1 = objectinputstream.readObject();
            Object object2 = objectinputstream.readObject();

            ((Collection) linkedhashmap.get(object1)).add(object2);
        }

        this.setMap(linkedhashmap);
    }

    @VisibleForTesting
    final class ValueSet extends Sets.ImprovedAbstractSet<V> implements LinkedHashMultimap.ValueSetLink<K, V> {

        private final K key;
        @VisibleForTesting
        LinkedHashMultimap.ValueEntry<K, V>[] hashTable;
        private int size = 0;
        private int modCount = 0;
        private LinkedHashMultimap.ValueSetLink<K, V> firstEntry;
        private LinkedHashMultimap.ValueSetLink<K, V> lastEntry;

        ValueSet(K k0, int i) {
            this.key = k0;
            this.firstEntry = this;
            this.lastEntry = this;
            int j = Hashing.closedTableSize(i, 1.0D);
            LinkedHashMultimap.ValueEntry[] alinkedhashmultimap_valueentry = new LinkedHashMultimap.ValueEntry[j];

            this.hashTable = alinkedhashmultimap_valueentry;
        }

        private int mask() {
            return this.hashTable.length - 1;
        }

        @Override
        public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.lastEntry;
        }

        @Override
        public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.firstEntry;
        }

        @Override
        public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink) {
            this.lastEntry = linkedhashmultimap_valuesetlink;
        }

        @Override
        public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink) {
            this.firstEntry = linkedhashmultimap_valuesetlink;
        }

        public Iterator<V> iterator() {
            return new Iterator<V>() {
                LinkedHashMultimap.ValueSetLink<K, V> nextEntry;
                LinkedHashMultimap.ValueEntry<K, V> toRemove;
                int expectedModCount;

                {
                    this.nextEntry = ValueSet.this.firstEntry;
                    this.expectedModCount = ValueSet.this.modCount;
                }

                private void checkForComodification() {
                    if (ValueSet.this.modCount != this.expectedModCount) {
                        throw new ConcurrentModificationException();
                    }
                }

                public boolean hasNext() {
                    this.checkForComodification();
                    return this.nextEntry != ValueSet.this;
                }

                public V next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    } else {
                        LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = (LinkedHashMultimap.ValueEntry) this.nextEntry;
                        Object object = linkedhashmultimap_valueentry.getValue();

                        this.toRemove = linkedhashmultimap_valueentry;
                        this.nextEntry = linkedhashmultimap_valueentry.getSuccessorInValueSet();
                        return object;
                    }
                }

                public void remove() {
                    this.checkForComodification();
                    CollectPreconditions.checkRemove(this.toRemove != null);
                    ValueSet.this.remove(this.toRemove.getValue());
                    this.expectedModCount = ValueSet.this.modCount;
                    this.toRemove = null;
                }
            };
        }

        public int size() {
            return this.size;
        }

        public boolean contains(@Nullable Object object) {
            int i = Hashing.smearedHash(object);

            for (LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = this.hashTable[i & this.mask()]; linkedhashmultimap_valueentry != null; linkedhashmultimap_valueentry = linkedhashmultimap_valueentry.nextInValueBucket) {
                if (linkedhashmultimap_valueentry.matchesValue(object, i)) {
                    return true;
                }
            }

            return false;
        }

        public boolean add(@Nullable V v0) {
            int i = Hashing.smearedHash(v0);
            int j = i & this.mask();
            LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = this.hashTable[j];

            LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry1;

            for (linkedhashmultimap_valueentry1 = linkedhashmultimap_valueentry; linkedhashmultimap_valueentry1 != null; linkedhashmultimap_valueentry1 = linkedhashmultimap_valueentry1.nextInValueBucket) {
                if (linkedhashmultimap_valueentry1.matchesValue(v0, i)) {
                    return false;
                }
            }

            linkedhashmultimap_valueentry1 = new LinkedHashMultimap.ValueEntry<>(this.key, v0, i, linkedhashmultimap_valueentry);
            LinkedHashMultimap.succeedsInValueSet(this.lastEntry, linkedhashmultimap_valueentry1);
            LinkedHashMultimap.succeedsInValueSet(linkedhashmultimap_valueentry1, this);
            LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), linkedhashmultimap_valueentry1);
            LinkedHashMultimap.succeedsInMultimap(linkedhashmultimap_valueentry1, LinkedHashMultimap.this.multimapHeaderEntry);
            this.hashTable[j] = linkedhashmultimap_valueentry1;
            ++this.size;
            ++this.modCount;
            this.rehashIfNecessary();
            return true;
        }

        private void rehashIfNecessary() {
            if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0D)) {
                LinkedHashMultimap.ValueEntry[] alinkedhashmultimap_valueentry = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];

                this.hashTable = alinkedhashmultimap_valueentry;
                int i = alinkedhashmultimap_valueentry.length - 1;

                for (LinkedHashMultimap.ValueSetLink linkedhashmultimap_valuesetlink = this.firstEntry; linkedhashmultimap_valuesetlink != this; linkedhashmultimap_valuesetlink = linkedhashmultimap_valuesetlink.getSuccessorInValueSet()) {
                    LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = (LinkedHashMultimap.ValueEntry) linkedhashmultimap_valuesetlink;
                    int j = linkedhashmultimap_valueentry.smearedValueHash & i;

                    linkedhashmultimap_valueentry.nextInValueBucket = alinkedhashmultimap_valueentry[j];
                    alinkedhashmultimap_valueentry[j] = linkedhashmultimap_valueentry;
                }
            }

        }

        public boolean remove(@Nullable Object object) {
            int i = Hashing.smearedHash(object);
            int j = i & this.mask();
            LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = null;

            for (LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry1 = this.hashTable[j]; linkedhashmultimap_valueentry1 != null; linkedhashmultimap_valueentry1 = linkedhashmultimap_valueentry1.nextInValueBucket) {
                if (linkedhashmultimap_valueentry1.matchesValue(object, i)) {
                    if (linkedhashmultimap_valueentry == null) {
                        this.hashTable[j] = linkedhashmultimap_valueentry1.nextInValueBucket;
                    } else {
                        linkedhashmultimap_valueentry.nextInValueBucket = linkedhashmultimap_valueentry1.nextInValueBucket;
                    }

                    LinkedHashMultimap.deleteFromValueSet(linkedhashmultimap_valueentry1);
                    LinkedHashMultimap.deleteFromMultimap(linkedhashmultimap_valueentry1);
                    --this.size;
                    ++this.modCount;
                    return true;
                }

                linkedhashmultimap_valueentry = linkedhashmultimap_valueentry1;
            }

            return false;
        }

        public void clear() {
            Arrays.fill(this.hashTable, (Object) null);
            this.size = 0;

            for (LinkedHashMultimap.ValueSetLink linkedhashmultimap_valuesetlink = this.firstEntry; linkedhashmultimap_valuesetlink != this; linkedhashmultimap_valuesetlink = linkedhashmultimap_valuesetlink.getSuccessorInValueSet()) {
                LinkedHashMultimap.ValueEntry linkedhashmultimap_valueentry = (LinkedHashMultimap.ValueEntry) linkedhashmultimap_valuesetlink;

                LinkedHashMultimap.deleteFromMultimap(linkedhashmultimap_valueentry);
            }

            LinkedHashMultimap.succeedsInValueSet(this, this);
            ++this.modCount;
        }
    }

    @VisibleForTesting
    static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements LinkedHashMultimap.ValueSetLink<K, V> {

        final int smearedValueHash;
        @Nullable
        LinkedHashMultimap.ValueEntry<K, V> nextInValueBucket;
        LinkedHashMultimap.ValueSetLink<K, V> predecessorInValueSet;
        LinkedHashMultimap.ValueSetLink<K, V> successorInValueSet;
        LinkedHashMultimap.ValueEntry<K, V> predecessorInMultimap;
        LinkedHashMultimap.ValueEntry<K, V> successorInMultimap;

        ValueEntry(@Nullable K k0, @Nullable V v0, int i, @Nullable LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry) {
            super(k0, v0);
            this.smearedValueHash = i;
            this.nextInValueBucket = linkedhashmultimap_valueentry;
        }

        boolean matchesValue(@Nullable Object object, int i) {
            return this.smearedValueHash == i && Objects.equal(this.getValue(), object);
        }

        @Override
        public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.predecessorInValueSet;
        }

        @Override
        public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.successorInValueSet;
        }

        @Override
        public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink) {
            this.predecessorInValueSet = linkedhashmultimap_valuesetlink;
        }

        @Override
        public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink) {
            this.successorInValueSet = linkedhashmultimap_valuesetlink;
        }

        public LinkedHashMultimap.ValueEntry<K, V> getPredecessorInMultimap() {
            return this.predecessorInMultimap;
        }

        public LinkedHashMultimap.ValueEntry<K, V> getSuccessorInMultimap() {
            return this.successorInMultimap;
        }

        public void setSuccessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry) {
            this.successorInMultimap = linkedhashmultimap_valueentry;
        }

        public void setPredecessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> linkedhashmultimap_valueentry) {
            this.predecessorInMultimap = linkedhashmultimap_valueentry;
        }
    }

    private interface ValueSetLink<K, V> {

        LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet();

        LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet();

        void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink);

        void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> linkedhashmultimap_valuesetlink);
    }
}
