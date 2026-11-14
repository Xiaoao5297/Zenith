package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V> {

    protected ForwardingMap() {}

    @Override
    protected abstract Map<K, V> delegate();

    public int size() {
        return this.delegate().size();
    }

    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }

    public V remove(Object object) {
        return this.delegate().remove(object);
    }

    public void clear() {
        this.delegate().clear();
    }

    public boolean containsKey(@Nullable Object object) {
        return this.delegate().containsKey(object);
    }

    public boolean containsValue(@Nullable Object object) {
        return this.delegate().containsValue(object);
    }

    public V get(@Nullable Object object) {
        return this.delegate().get(object);
    }

    public V put(K k0, V v0) {
        return this.delegate().put(k0, v0);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        this.delegate().putAll(map);
    }

    public Set<K> keySet() {
        return this.delegate().keySet();
    }

    public Collection<V> values() {
        return this.delegate().values();
    }

    public Set<Entry<K, V>> entrySet() {
        return this.delegate().entrySet();
    }

    public boolean equals(@Nullable Object object) {
        return object == this || this.delegate().equals(object);
    }

    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected void standardPutAll(Map<? extends K, ? extends V> map) {
        Maps.putAllImpl(this, map);
    }

    @Beta
    protected V standardRemove(@Nullable Object object) {
        Iterator iterator = this.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entry = (Entry) iterator.next();
        } while (!Objects.equal(entry.getKey(), object));

        Object object1 = entry.getValue();

        iterator.remove();
        return object1;
    }

    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }

    @Beta
    protected boolean standardContainsKey(@Nullable Object object) {
        return Maps.containsKeyImpl(this, object);
    }

    protected boolean standardContainsValue(@Nullable Object object) {
        return Maps.containsValueImpl(this, object);
    }

    protected boolean standardIsEmpty() {
        return !this.entrySet().iterator().hasNext();
    }

    protected boolean standardEquals(@Nullable Object object) {
        return Maps.equalsImpl(this, object);
    }

    protected int standardHashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }

    protected String standardToString() {
        return Maps.toStringImpl(this);
    }

    @Beta
    protected abstract class StandardEntrySet extends Maps.EntrySet<K, V> {

        public StandardEntrySet() {}

        @Override
        Map<K, V> map() {
            return ForwardingMap.this;
        }
    }

    @Beta
    protected class StandardValues extends Maps.Values<K, V> {

        public StandardValues() {
            super(ForwardingMap.this);
        }
    }

    @Beta
    protected class StandardKeySet extends Maps.KeySet<K, V> {

        public StandardKeySet() {
            super(ForwardingMap.this);
        }
    }
}
