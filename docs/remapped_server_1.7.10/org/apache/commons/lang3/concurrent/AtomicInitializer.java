package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicInitializer<T> implements ConcurrentInitializer<T> {

    private final AtomicReference<T> reference = new AtomicReference();

    public AtomicInitializer() {}

    @Override
    public T get() throws ConcurrentException {
        Object object = this.reference.get();

        if (object == null) {
            object = this.initialize();
            if (!this.reference.compareAndSet((Object) null, object)) {
                object = this.reference.get();
            }
        }

        return object;
    }

    protected abstract T initialize() throws ConcurrentException;
}
