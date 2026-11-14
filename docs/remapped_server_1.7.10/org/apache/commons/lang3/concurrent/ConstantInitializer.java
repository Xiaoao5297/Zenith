package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.ObjectUtils;

public class ConstantInitializer<T> implements ConcurrentInitializer<T> {

    private static final String FMT_TO_STRING = "ConstantInitializer@%d [ object = %s ]";
    private final T object;

    public ConstantInitializer(T t0) {
        this.object = t0;
    }

    public final T getObject() {
        return this.object;
    }

    @Override
    public T get() throws ConcurrentException {
        return this.getObject();
    }

    public int hashCode() {
        return this.getObject() != null ? this.getObject().hashCode() : 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ConstantInitializer)) {
            return false;
        } else {
            ConstantInitializer constantinitializer = (ConstantInitializer) object;

            return ObjectUtils.equals(this.getObject(), constantinitializer.getObject());
        }
    }

    public String toString() {
        return String.format("ConstantInitializer@%d [ object = %s ]", System.identityHashCode(this), String.valueOf(this.getObject()));
    }
}
