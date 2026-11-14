package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Entry<K, V> {

    AbstractMapEntry() {}

    public abstract K getKey();

    public abstract V getValue();

    public V setValue(V v0) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Entry)) {
            return false;
        } else {
            Entry entry = (Entry) object;

            return Objects.equal(this.getKey(), entry.getKey()) && Objects.equal(this.getValue(), entry.getValue());
        }
    }

    public int hashCode() {
        Object object = this.getKey();
        Object object1 = this.getValue();

        return (object == null ? 0 : object.hashCode()) ^ (object1 == null ? 0 : object1.hashCode());
    }

    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
