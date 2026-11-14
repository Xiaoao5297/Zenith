package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class ReferenceMap<K, V> implements Map<K, V> {

    private final Map<K, Reference<V>> delegate;

    protected ReferenceMap(Map<K, Reference<V>> map) {
        this.delegate = map;
    }

    abstract Reference<V> fold(V v0);

    private V unfold(Reference<V> reference) {
        return reference == null ? null : reference.get();
    }

    public int size() {
        return this.delegate.size();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public boolean containsKey(Object object) {
        return this.delegate.containsKey(object);
    }

    public boolean containsValue(Object object) {
        throw new UnsupportedOperationException();
    }

    public V get(Object object) {
        return this.unfold((Reference) this.delegate.get(object));
    }

    public V put(K k0, V v0) {
        return this.unfold((Reference) this.delegate.put(k0, this.fold(v0)));
    }

    public V remove(Object object) {
        return this.unfold((Reference) this.delegate.remove(object));
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.delegate.put(entry.getKey(), this.fold(entry.getValue()));
        }

    }

    public void clear() {
        this.delegate.clear();
    }

    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
