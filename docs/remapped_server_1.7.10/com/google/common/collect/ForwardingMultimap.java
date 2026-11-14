package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultimap<K, V> extends ForwardingObject implements Multimap<K, V> {

    protected ForwardingMultimap() {}

    @Override
    protected abstract Multimap<K, V> delegate();

    @Override
    public Map<K, Collection<V>> asMap() {
        return this.delegate().asMap();
    }

    @Override
    public void clear() {
        this.delegate().clear();
    }

    @Override
    public boolean containsEntry(@Nullable Object object, @Nullable Object object1) {
        return this.delegate().containsEntry(object, object1);
    }

    @Override
    public boolean containsKey(@Nullable Object object) {
        return this.delegate().containsKey(object);
    }

    @Override
    public boolean containsValue(@Nullable Object object) {
        return this.delegate().containsValue(object);
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return this.delegate().entries();
    }

    @Override
    public Collection<V> get(@Nullable K k0) {
        return this.delegate().get(k0);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    @Override
    public Multiset<K> keys() {
        return this.delegate().keys();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate().keySet();
    }

    @Override
    public boolean put(K k0, V v0) {
        return this.delegate().put(k0, v0);
    }

    @Override
    public boolean putAll(K k0, Iterable<? extends V> iterable) {
        return this.delegate().putAll(k0, iterable);
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        return this.delegate().putAll(multimap);
    }

    @Override
    public boolean remove(@Nullable Object object, @Nullable Object object1) {
        return this.delegate().remove(object, object1);
    }

    @Override
    public Collection<V> removeAll(@Nullable Object object) {
        return this.delegate().removeAll(object);
    }

    @Override
    public Collection<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k0, iterable);
    }

    @Override
    public int size() {
        return this.delegate().size();
    }

    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return object == this || this.delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
}
