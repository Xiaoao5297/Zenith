package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicSafeInitializer<T> implements ConcurrentInitializer<T> {

    private final AtomicReference<AtomicSafeInitializer<T>> factory = new AtomicReference();
    private final AtomicReference<T> reference = new AtomicReference();

    public AtomicSafeInitializer() {}

    @Override
    public final T get() throws ConcurrentException {
        Object object;

        while ((object = this.reference.get()) == null) {
            if (this.factory.compareAndSet((Object) null, this)) {
                this.reference.set(this.initialize());
            }
        }

        return object;
    }

    protected abstract T initialize() throws ConcurrentException;
}
