package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class HashBiMap<K, V> extends AbstractMap<K, V> implements BiMap<K, V>, Serializable {

    private static final double LOAD_FACTOR = 1.0D;
    private transient HashBiMap.BiEntry<K, V>[] hashTableKToV;
    private transient HashBiMap.BiEntry<K, V>[] hashTableVToK;
    private transient int size;
    private transient int mask;
    private transient int modCount;
    private transient BiMap<V, K> inverse;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;

    public static <K, V> HashBiMap<K, V> create() {
        return create(16);
    }

    public static <K, V> HashBiMap<K, V> create(int i) {
        return new HashBiMap<>(i);
    }

    public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> map) {
        HashBiMap hashbimap = create(map.size());

        hashbimap.putAll(map);
        return hashbimap;
    }

    private HashBiMap(int i) {
        this.init(i);
    }

    private void init(int i) {
        CollectPreconditions.checkNonnegative(i, "expectedSize");
        int j = Hashing.closedTableSize(i, 1.0D);

        this.hashTableKToV = this.createTable(j);
        this.hashTableVToK = this.createTable(j);
        this.mask = j - 1;
        this.modCount = 0;
        this.size = 0;
    }

    private void delete(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
        int i = hashbimap_bientry.keyHash & this.mask;
        HashBiMap.BiEntry hashbimap_bientry1 = null;

        for (HashBiMap.BiEntry hashbimap_bientry2 = this.hashTableKToV[i]; hashbimap_bientry2 != hashbimap_bientry; hashbimap_bientry2 = hashbimap_bientry2.nextInKToVBucket) {
            hashbimap_bientry1 = hashbimap_bientry2;
        }

        if (hashbimap_bientry1 == null) {
            this.hashTableKToV[i] = hashbimap_bientry.nextInKToVBucket;
        } else {
            hashbimap_bientry1.nextInKToVBucket = hashbimap_bientry.nextInKToVBucket;
        }

        int j = hashbimap_bientry.valueHash & this.mask;

        hashbimap_bientry1 = null;

        for (HashBiMap.BiEntry hashbimap_bientry3 = this.hashTableVToK[j]; hashbimap_bientry3 != hashbimap_bientry; hashbimap_bientry3 = hashbimap_bientry3.nextInVToKBucket) {
            hashbimap_bientry1 = hashbimap_bientry3;
        }

        if (hashbimap_bientry1 == null) {
            this.hashTableVToK[j] = hashbimap_bientry.nextInVToKBucket;
        } else {
            hashbimap_bientry1.nextInVToKBucket = hashbimap_bientry.nextInVToKBucket;
        }

        --this.size;
        ++this.modCount;
    }

    private void insert(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
        int i = hashbimap_bientry.keyHash & this.mask;

        hashbimap_bientry.nextInKToVBucket = this.hashTableKToV[i];
        this.hashTableKToV[i] = hashbimap_bientry;
        int j = hashbimap_bientry.valueHash & this.mask;

        hashbimap_bientry.nextInVToKBucket = this.hashTableVToK[j];
        this.hashTableVToK[j] = hashbimap_bientry;
        ++this.size;
        ++this.modCount;
    }

    private static int hash(@Nullable Object object) {
        return Hashing.smear(object == null ? 0 : object.hashCode());
    }

    private HashBiMap.BiEntry<K, V> seekByKey(@Nullable Object object, int i) {
        for (HashBiMap.BiEntry hashbimap_bientry = this.hashTableKToV[i & this.mask]; hashbimap_bientry != null; hashbimap_bientry = hashbimap_bientry.nextInKToVBucket) {
            if (i == hashbimap_bientry.keyHash && Objects.equal(object, hashbimap_bientry.key)) {
                return hashbimap_bientry;
            }
        }

        return null;
    }

    private HashBiMap.BiEntry<K, V> seekByValue(@Nullable Object object, int i) {
        for (HashBiMap.BiEntry hashbimap_bientry = this.hashTableVToK[i & this.mask]; hashbimap_bientry != null; hashbimap_bientry = hashbimap_bientry.nextInVToKBucket) {
            if (i == hashbimap_bientry.valueHash && Objects.equal(object, hashbimap_bientry.value)) {
                return hashbimap_bientry;
            }
        }

        return null;
    }

    public boolean containsKey(@Nullable Object object) {
        return this.seekByKey(object, hash(object)) != null;
    }

    public boolean containsValue(@Nullable Object object) {
        return this.seekByValue(object, hash(object)) != null;
    }

    @Nullable
    public V get(@Nullable Object object) {
        HashBiMap.BiEntry hashbimap_bientry = this.seekByKey(object, hash(object));

        return hashbimap_bientry == null ? null : hashbimap_bientry.value;
    }

    @Override
    public V put(@Nullable K k0, @Nullable V v0) {
        return this.put(k0, v0, false);
    }

    @Override
    public V forcePut(@Nullable K k0, @Nullable V v0) {
        return this.put(k0, v0, true);
    }

    private V put(@Nullable K k0, @Nullable V v0, boolean flag) {
        int i = hash(k0);
        int j = hash(v0);
        HashBiMap.BiEntry hashbimap_bientry = this.seekByKey(k0, i);

        if (hashbimap_bientry != null && j == hashbimap_bientry.valueHash && Objects.equal(v0, hashbimap_bientry.value)) {
            return v0;
        } else {
            HashBiMap.BiEntry hashbimap_bientry1 = this.seekByValue(v0, j);

            if (hashbimap_bientry1 != null) {
                if (!flag) {
                    throw new IllegalArgumentException("value already present: " + v0);
                }

                this.delete(hashbimap_bientry1);
            }

            if (hashbimap_bientry != null) {
                this.delete(hashbimap_bientry);
            }

            HashBiMap.BiEntry hashbimap_bientry2 = new HashBiMap.BiEntry<>(k0, i, v0, j);

            this.insert(hashbimap_bientry2);
            this.rehashIfNecessary();
            return hashbimap_bientry == null ? null : hashbimap_bientry.value;
        }
    }

    @Nullable
    private K putInverse(@Nullable V v0, @Nullable K k0, boolean flag) {
        int i = hash(v0);
        int j = hash(k0);
        HashBiMap.BiEntry hashbimap_bientry = this.seekByValue(v0, i);

        if (hashbimap_bientry != null && j == hashbimap_bientry.keyHash && Objects.equal(k0, hashbimap_bientry.key)) {
            return k0;
        } else {
            HashBiMap.BiEntry hashbimap_bientry1 = this.seekByKey(k0, j);

            if (hashbimap_bientry1 != null) {
                if (!flag) {
                    throw new IllegalArgumentException("value already present: " + k0);
                }

                this.delete(hashbimap_bientry1);
            }

            if (hashbimap_bientry != null) {
                this.delete(hashbimap_bientry);
            }

            HashBiMap.BiEntry hashbimap_bientry2 = new HashBiMap.BiEntry<>(k0, j, v0, i);

            this.insert(hashbimap_bientry2);
            this.rehashIfNecessary();
            return hashbimap_bientry == null ? null : hashbimap_bientry.key;
        }
    }

    private void rehashIfNecessary() {
        HashBiMap.BiEntry[] ahashbimap_bientry = this.hashTableKToV;

        if (Hashing.needsResizing(this.size, ahashbimap_bientry.length, 1.0D)) {
            int i = ahashbimap_bientry.length * 2;

            this.hashTableKToV = this.createTable(i);
            this.hashTableVToK = this.createTable(i);
            this.mask = i - 1;
            this.size = 0;

            HashBiMap.BiEntry hashbimap_bientry;

            for (int j = 0; j < ahashbimap_bientry.length; ++j) {
                for (HashBiMap.BiEntry hashbimap_bientry1 = ahashbimap_bientry[j]; hashbimap_bientry1 != null; hashbimap_bientry1 = hashbimap_bientry) {
                    hashbimap_bientry = hashbimap_bientry1.nextInKToVBucket;
                    this.insert(hashbimap_bientry1);
                }
            }

            ++this.modCount;
        }

    }

    private HashBiMap.BiEntry<K, V>[] createTable(int i) {
        return new HashBiMap.BiEntry[i];
    }

    public V remove(@Nullable Object object) {
        HashBiMap.BiEntry hashbimap_bientry = this.seekByKey(object, hash(object));

        if (hashbimap_bientry == null) {
            return null;
        } else {
            this.delete(hashbimap_bientry);
            return hashbimap_bientry.value;
        }
    }

    public void clear() {
        this.size = 0;
        Arrays.fill(this.hashTableKToV, (Object) null);
        Arrays.fill(this.hashTableVToK, (Object) null);
        ++this.modCount;
    }

    public int size() {
        return this.size;
    }

    public Set<K> keySet() {
        return new HashBiMap.KeySet();
    }

    @Override
    public Set<V> values() {
        return this.inverse().keySet();
    }

    public Set<Entry<K, V>> entrySet() {
        return new HashBiMap.EntrySet();
    }

    @Override
    public BiMap<V, K> inverse() {
        return this.inverse == null ? (this.inverse = new HashBiMap.Inverse()) : this.inverse;
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        Serialization.writeMap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        int i = Serialization.readCount(objectinputstream);

        this.init(i);
        Serialization.populateMap(this, objectinputstream, i);
    }

    private static final class InverseSerializedForm<K, V> implements Serializable {

        private final HashBiMap<K, V> bimap;

        InverseSerializedForm(HashBiMap<K, V> hashbimap) {
            this.bimap = hashbimap;
        }

        Object readResolve() {
            return this.bimap.inverse();
        }
    }

    private final class Inverse extends AbstractMap<V, K> implements BiMap<V, K>, Serializable {

        private Inverse() {}

        BiMap<K, V> forward() {
            return HashBiMap.this;
        }

        public int size() {
            return HashBiMap.this.size;
        }

        public void clear() {
            this.forward().clear();
        }

        public boolean containsKey(@Nullable Object object) {
            return this.forward().containsValue(object);
        }

        public K get(@Nullable Object object) {
            HashBiMap.BiEntry hashbimap_bientry = HashBiMap.this.seekByValue(object, HashBiMap.hash(object));

            return hashbimap_bientry == null ? null : hashbimap_bientry.key;
        }

        @Override
        public K put(@Nullable V v0, @Nullable K k0) {
            return HashBiMap.this.putInverse(v0, k0, false);
        }

        @Override
        public K forcePut(@Nullable V v0, @Nullable K k0) {
            return HashBiMap.this.putInverse(v0, k0, true);
        }

        public K remove(@Nullable Object object) {
            HashBiMap.BiEntry hashbimap_bientry = HashBiMap.this.seekByValue(object, HashBiMap.hash(object));

            if (hashbimap_bientry == null) {
                return null;
            } else {
                HashBiMap.this.delete(hashbimap_bientry);
                return hashbimap_bientry.key;
            }
        }

        @Override
        public BiMap<K, V> inverse() {
            return this.forward();
        }

        public Set<V> keySet() {
            return new HashBiMap.Inverse.InverseKeySet();
        }

        @Override
        public Set<K> values() {
            return this.forward().keySet();
        }

        public Set<Entry<V, K>> entrySet() {
            return new Maps.EntrySet<V, K>() {
                @Override
                Map<V, K> map() {
                    return Inverse.this;
                }

                public Iterator<Entry<V, K>> iterator() {
                    return new HashBiMap<K, V>.Itr<Entry<V, K>>() {
                        @Override
                        Entry<V, K> output(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                            return new null.InverseEntry(hashbimap_bientry);
                        }

                        class InverseEntry extends AbstractMapEntry<V, K> {

                            HashBiMap.BiEntry<K, V> delegate;

                            InverseEntry(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                                this.delegate = hashbimap_bientry;
                            }

                            @Override
                            public V getKey() {
                                return this.delegate.value;
                            }

                            @Override
                            public K getValue() {
                                return this.delegate.key;
                            }

                            @Override
                            public K setValue(K k0) {
                                Object object = this.delegate.key;
                                int i = HashBiMap.hash(k0);

                                if (i == this.delegate.keyHash && Objects.equal(k0, object)) {
                                    return k0;
                                } else {
                                    Preconditions.checkArgument(HashBiMap.this.seekByKey(k0, i) == null, "value already present: %s", k0);
                                    HashBiMap.this.delete(this.delegate);
                                    HashBiMap.BiEntry hashbimap_bientry = new HashBiMap.BiEntry<>(k0, i, this.delegate.value, this.delegate.valueHash);

                                    HashBiMap.this.insert(hashbimap_bientry);
                                    expectedModCount = HashBiMap.this.modCount;
                                    return object;
                                }
                            }
                        }
                    };
                }
            };
        }

        Object writeReplace() {
            return new HashBiMap.InverseSerializedForm<>(HashBiMap.this);
        }

        private final class InverseKeySet extends Maps.KeySet<V, K> {

            InverseKeySet() {
                super(Inverse.this);
            }

            @Override
            public boolean remove(@Nullable Object object) {
                HashBiMap.BiEntry hashbimap_bientry = HashBiMap.this.seekByValue(object, HashBiMap.hash(object));

                if (hashbimap_bientry == null) {
                    return false;
                } else {
                    HashBiMap.this.delete(hashbimap_bientry);
                    return true;
                }
            }

            @Override
            public Iterator<V> iterator() {
                return new HashBiMap<K, V>.Itr<V>() {
                    @Override
                    V output(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                        return hashbimap_bientry.value;
                    }
                };
            }
        }
    }

    private final class EntrySet extends Maps.EntrySet<K, V> {

        private EntrySet() {}

        @Override
        Map<K, V> map() {
            return HashBiMap.this;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new HashBiMap<K, V>.Itr<Entry<K, V>>() {
                @Override
                Entry<K, V> output(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                    return new null.MapEntry(hashbimap_bientry);
                }

                class MapEntry extends AbstractMapEntry<K, V> {

                    HashBiMap.BiEntry<K, V> delegate;

                    MapEntry(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                        this.delegate = hashbimap_bientry;
                    }

                    @Override
                    public K getKey() {
                        return this.delegate.key;
                    }

                    @Override
                    public V getValue() {
                        return this.delegate.value;
                    }

                    @Override
                    public V setValue(V v0) {
                        Object object = this.delegate.value;
                        int i = HashBiMap.hash(v0);

                        if (i == this.delegate.valueHash && Objects.equal(v0, object)) {
                            return v0;
                        } else {
                            Preconditions.checkArgument(HashBiMap.this.seekByValue(v0, i) == null, "value already present: %s", v0);
                            HashBiMap.this.delete(this.delegate);
                            HashBiMap.BiEntry hashbimap_bientry = new HashBiMap.BiEntry<>(this.delegate.key, this.delegate.keyHash, v0, i);

                            HashBiMap.this.insert(hashbimap_bientry);
                            expectedModCount = HashBiMap.this.modCount;
                            if (toRemove == this.delegate) {
                                toRemove = hashbimap_bientry;
                            }

                            this.delegate = hashbimap_bientry;
                            return object;
                        }
                    }
                }
            };
        }
    }

    private final class KeySet extends Maps.KeySet<K, V> {

        KeySet() {
            super(HashBiMap.this);
        }

        @Override
        public Iterator<K> iterator() {
            return new HashBiMap<K, V>.Itr<K>() {
                @Override
                K output(HashBiMap.BiEntry<K, V> hashbimap_bientry) {
                    return hashbimap_bientry.key;
                }
            };
        }

        @Override
        public boolean remove(@Nullable Object object) {
            HashBiMap.BiEntry hashbimap_bientry = HashBiMap.this.seekByKey(object, HashBiMap.hash(object));

            if (hashbimap_bientry == null) {
                return false;
            } else {
                HashBiMap.this.delete(hashbimap_bientry);
                return true;
            }
        }
    }

    abstract class Itr<T> implements Iterator<T> {

        int nextBucket = 0;
        HashBiMap.BiEntry<K, V> next = null;
        HashBiMap.BiEntry<K, V> toRemove = null;
        int expectedModCount;

        Itr() {
            this.expectedModCount = HashBiMap.this.modCount;
        }

        private void checkForConcurrentModification() {
            if (HashBiMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            this.checkForConcurrentModification();
            if (this.next != null) {
                return true;
            } else {
                while (this.nextBucket < HashBiMap.this.hashTableKToV.length) {
                    if (HashBiMap.this.hashTableKToV[this.nextBucket] != null) {
                        this.next = HashBiMap.this.hashTableKToV[this.nextBucket++];
                        return true;
                    }

                    ++this.nextBucket;
                }

                return false;
            }
        }

        public T next() {
            this.checkForConcurrentModification();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                HashBiMap.BiEntry hashbimap_bientry = this.next;

                this.next = hashbimap_bientry.nextInKToVBucket;
                this.toRemove = hashbimap_bientry;
                return this.output(hashbimap_bientry);
            }
        }

        public void remove() {
            this.checkForConcurrentModification();
            CollectPreconditions.checkRemove(this.toRemove != null);
            HashBiMap.this.delete(this.toRemove);
            this.expectedModCount = HashBiMap.this.modCount;
            this.toRemove = null;
        }

        abstract T output(HashBiMap.BiEntry<K, V> hashbimap_bientry);
    }

    private static final class BiEntry<K, V> extends ImmutableEntry<K, V> {

        final int keyHash;
        final int valueHash;
        @Nullable
        HashBiMap.BiEntry<K, V> nextInKToVBucket;
        @Nullable
        HashBiMap.BiEntry<K, V> nextInVToKBucket;

        BiEntry(K k0, int i, V v0, int j) {
            super(k0, v0);
            this.keyHash = i;
            this.valueHash = j;
        }
    }
}
