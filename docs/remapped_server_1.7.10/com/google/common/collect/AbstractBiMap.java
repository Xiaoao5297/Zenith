package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
abstract class AbstractBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {

    private transient Map<K, V> delegate;
    transient AbstractBiMap<V, K> inverse;
    private transient Set<K> keySet;
    private transient Set<V> valueSet;
    private transient Set<Entry<K, V>> entrySet;
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;

    AbstractBiMap(Map<K, V> map, Map<V, K> map1) {
        this.setDelegates(map, map1);
    }

    private AbstractBiMap(Map<K, V> map, AbstractBiMap<V, K> abstractbimap) {
        this.delegate = map;
        this.inverse = abstractbimap;
    }

    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }

    K checkKey(@Nullable K k0) {
        return k0;
    }

    V checkValue(@Nullable V v0) {
        return v0;
    }

    void setDelegates(Map<K, V> map, Map<V, K> map1) {
        Preconditions.checkState(this.delegate == null);
        Preconditions.checkState(this.inverse == null);
        Preconditions.checkArgument(map.isEmpty());
        Preconditions.checkArgument(map1.isEmpty());
        Preconditions.checkArgument(map != map1);
        this.delegate = map;
        this.inverse = new AbstractBiMap.Inverse<>(map1, this);
    }

    void setInverse(AbstractBiMap<V, K> abstractbimap) {
        this.inverse = abstractbimap;
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.inverse.containsKey(object);
    }

    @Override
    public V put(@Nullable K k0, @Nullable V v0) {
        return this.putInBothMaps(k0, v0, false);
    }

    @Override
    public V forcePut(@Nullable K k0, @Nullable V v0) {
        return this.putInBothMaps(k0, v0, true);
    }

    private V putInBothMaps(@Nullable K k0, @Nullable V v0, boolean flag) {
        this.checkKey(k0);
        this.checkValue(v0);
        boolean flag1 = this.containsKey(k0);

        if (flag1 && Objects.equal(v0, this.get(k0))) {
            return v0;
        } else {
            if (flag) {
                this.inverse().remove(v0);
            } else {
                Preconditions.checkArgument(!this.containsValue(v0), "value already present: %s", v0);
            }

            Object object = this.delegate.put(k0, v0);

            this.updateInverseMap(k0, flag1, object, v0);
            return object;
        }
    }

    private void updateInverseMap(K k0, boolean flag, V v0, V v1) {
        if (flag) {
            this.removeFromInverseMap(v0);
        }

        this.inverse.delegate.put(v1, k0);
    }

    @Override
    public V remove(@Nullable Object object) {
        return this.containsKey(object) ? this.removeFromBothMaps(object) : null;
    }

    private V removeFromBothMaps(Object object) {
        Object object1 = this.delegate.remove(object);

        this.removeFromInverseMap(object1);
        return object1;
    }

    private void removeFromInverseMap(V v0) {
        this.inverse.delegate.remove(v0);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void clear() {
        this.delegate.clear();
        this.inverse.delegate.clear();
    }

    @Override
    public BiMap<V, K> inverse() {
        return this.inverse;
    }

    @Override
    public Set<K> keySet() {
        Set set = this.keySet;

        return set == null ? (this.keySet = new AbstractBiMap.KeySet()) : set;
    }

    @Override
    public Set<V> values() {
        Set set = this.valueSet;

        return set == null ? (this.valueSet = new AbstractBiMap.ValueSet()) : set;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set set = this.entrySet;

        return set == null ? (this.entrySet = new AbstractBiMap.EntrySet()) : set;
    }

    private static class Inverse<K, V> extends AbstractBiMap<K, V> {

        @GwtIncompatible("Not needed in emulated source.")
        private static final long serialVersionUID = 0L;

        private Inverse(Map<K, V> map, AbstractBiMap<V, K> abstractbimap) {
            super(map, abstractbimap, null);
        }

        @Override
        K checkKey(K k0) {
            return this.inverse.checkValue(k0);
        }

        @Override
        V checkValue(V v0) {
            return this.inverse.checkKey(v0);
        }

        @GwtIncompatible("java.io.ObjectOuputStream")
        private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
            objectoutputstream.defaultWriteObject();
            objectoutputstream.writeObject(this.inverse());
        }

        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
            objectinputstream.defaultReadObject();
            this.setInverse((AbstractBiMap) objectinputstream.readObject());
        }

        @GwtIncompatible("Not needed in the emulated source.")
        Object readResolve() {
            return this.inverse().inverse();
        }
    }

    private class EntrySet extends ForwardingSet<Entry<K, V>> {

        final Set<Entry<K, V>> esDelegate;

        private EntrySet() {
            this.esDelegate = AbstractBiMap.this.delegate.entrySet();
        }

        @Override
        protected Set<Entry<K, V>> delegate() {
            return this.esDelegate;
        }

        @Override
        public void clear() {
            AbstractBiMap.this.clear();
        }

        @Override
        public boolean remove(Object object) {
            if (!this.esDelegate.contains(object)) {
                return false;
            } else {
                Entry entry = (Entry) object;

                AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
                this.esDelegate.remove(entry);
                return true;
            }
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            final Iterator iterator = this.esDelegate.iterator();

            return new Iterator<Entry<K, V>>() {
                Entry<K, V> entry;

                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public Entry<K, V> next() {
                    this.entry = (Entry) iterator.next();
                    final Entry entry = this.entry;

                    return new ForwardingMapEntry<K, V>() {
                        @Override
                        protected Entry<K, V> delegate() {
                            return entry;
                        }

                        @Override
                        public V setValue(V v0) {
                            Preconditions.checkState(EntrySet.this.contains(this), "entry no longer in map");
                            if (Objects.equal(v0, this.getValue())) {
                                return v0;
                            } else {
                                Preconditions.checkArgument(!AbstractBiMap.this.containsValue(v0), "value already present: %s", v0);
                                Object object = entry.setValue(v0);

                                Preconditions.checkState(Objects.equal(v0, AbstractBiMap.this.get(this.getKey())), "entry no longer in map");
                                AbstractBiMap.this.updateInverseMap(this.getKey(), true, object, v0);
                                return object;
                            }
                        }
                    };
                }

                public void remove() {
                    CollectPreconditions.checkRemove(this.entry != null);
                    Object object = this.entry.getValue();

                    iterator.remove();
                    AbstractBiMap.this.removeFromInverseMap(object);
                }
            };
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public boolean contains(Object object) {
            return Maps.containsEntryImpl(this.delegate(), object);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return this.standardContainsAll(collection);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
    }

    private class ValueSet extends ForwardingSet<V> {

        final Set<V> valuesDelegate;

        private ValueSet() {
            this.valuesDelegate = AbstractBiMap.this.inverse.keySet();
        }

        @Override
        protected Set<V> delegate() {
            return this.valuesDelegate;
        }

        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
        }

        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }

        @Override
        public <T> T[] toArray(T[] at) {
            return this.standardToArray(at);
        }

        @Override
        public String toString() {
            return this.standardToString();
        }
    }

    private class KeySet extends ForwardingSet<K> {

        private KeySet() {}

        @Override
        protected Set<K> delegate() {
            return AbstractBiMap.this.delegate.keySet();
        }

        @Override
        public void clear() {
            AbstractBiMap.this.clear();
        }

        @Override
        public boolean remove(Object object) {
            if (!this.contains(object)) {
                return false;
            } else {
                AbstractBiMap.this.removeFromBothMaps(object);
                return true;
            }
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return this.standardRetainAll(collection);
        }

        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
        }
    }
}
