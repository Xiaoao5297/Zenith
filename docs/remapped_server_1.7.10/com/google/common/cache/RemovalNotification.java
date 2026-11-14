package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class RemovalNotification<K, V> implements Entry<K, V> {

    @Nullable
    private final K key;
    @Nullable
    private final V value;
    private final RemovalCause cause;
    private static final long serialVersionUID = 0L;

    RemovalNotification(@Nullable K k0, @Nullable V v0, RemovalCause removalcause) {
        this.key = k0;
        this.value = v0;
        this.cause = (RemovalCause) Preconditions.checkNotNull(removalcause);
    }

    public RemovalCause getCause() {
        return this.cause;
    }

    public boolean wasEvicted() {
        return this.cause.wasEvicted();
    }

    @Nullable
    public K getKey() {
        return this.key;
    }

    @Nullable
    public V getValue() {
        return this.value;
    }

    public final V setValue(V v0) {
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
