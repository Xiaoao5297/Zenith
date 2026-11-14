package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Entry<K, V> {

    protected ForwardingMapEntry() {}

    @Override
    protected abstract Entry<K, V> delegate();

    public K getKey() {
        return this.delegate().getKey();
    }

    public V getValue() {
        return this.delegate().getValue();
    }

    public V setValue(V v0) {
        return this.delegate().setValue(v0);
    }

    public boolean equals(@Nullable Object object) {
        return this.delegate().equals(object);
    }

    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected boolean standardEquals(@Nullable Object object) {
        if (!(object instanceof Entry)) {
            return false;
        } else {
            Entry entry = (Entry) object;

            return Objects.equal(this.getKey(), entry.getKey()) && Objects.equal(this.getValue(), entry.getValue());
        }
    }

    protected int standardHashCode() {
        Object object = this.getKey();
        Object object1 = this.getValue();

        return (object == null ? 0 : object.hashCode()) ^ (object1 == null ? 0 : object1.hashCode());
    }

    @Beta
    protected String standardToString() {
        return this.getKey() + "=" + this.getValue();
    }
}
