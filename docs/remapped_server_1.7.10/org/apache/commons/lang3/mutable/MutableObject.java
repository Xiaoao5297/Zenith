package org.apache.commons.lang3.mutable;

import java.io.Serializable;

public class MutableObject<T> implements Mutable<T>, Serializable {

    private static final long serialVersionUID = 86241875189L;
    private T value;

    public MutableObject() {}

    public MutableObject(T t0) {
        this.value = t0;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T t0) {
        this.value = t0;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (this == object) {
            return true;
        } else if (this.getClass() == object.getClass()) {
            MutableObject mutableobject = (MutableObject) object;

            return this.value.equals(mutableobject.value);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.value == null ? 0 : this.value.hashCode();
    }

    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }
}
