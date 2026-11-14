package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class UsingToStringOrdering extends Ordering<Object> implements Serializable {

    static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
    private static final long serialVersionUID = 0L;

    @Override
    public int compare(Object object, Object object1) {
        return object.toString().compareTo(object1.toString());
    }

    private Object readResolve() {
        return UsingToStringOrdering.INSTANCE;
    }

    public String toString() {
        return "Ordering.usingToString()";
    }

    private UsingToStringOrdering() {}
}
