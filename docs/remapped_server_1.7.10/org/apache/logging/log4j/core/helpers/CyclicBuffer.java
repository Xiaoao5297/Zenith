package org.apache.logging.log4j.core.helpers;

import java.lang.reflect.Array;

public class CyclicBuffer<T> {

    private final T[] ring;
    private int first = 0;
    private int last = 0;
    private int numElems = 0;
    private final Class<T> clazz;

    public CyclicBuffer(Class<T> oclass, int i) throws IllegalArgumentException {
        if (i < 1) {
            throw new IllegalArgumentException("The maxSize argument (" + i + ") is not a positive integer.");
        } else {
            this.ring = this.makeArray(oclass, i);
            this.clazz = oclass;
        }
    }

    private T[] makeArray(Class<T> oclass, int i) {
        return (Object[]) ((Object[]) Array.newInstance(oclass, i));
    }

    public synchronized void add(T t0) {
        this.ring[this.last] = t0;
        if (++this.last == this.ring.length) {
            this.last = 0;
        }

        if (this.numElems < this.ring.length) {
            ++this.numElems;
        } else if (++this.first == this.ring.length) {
            this.first = 0;
        }

    }

    public synchronized T[] removeAll() {
        Object[] aobject = this.makeArray(this.clazz, this.numElems);
        int i = 0;

        while (this.numElems > 0) {
            --this.numElems;
            aobject[i++] = this.ring[this.first];
            this.ring[this.first] = null;
            if (++this.first == this.ring.length) {
                this.first = 0;
            }
        }

        return aobject;
    }

    public boolean isEmpty() {
        return 0 == this.numElems;
    }
}
