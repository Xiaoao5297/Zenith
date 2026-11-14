package org.apache.commons.lang3.concurrent;

public abstract class LazyInitializer<T> implements ConcurrentInitializer<T> {

    private volatile T object;

    public LazyInitializer() {}

    @Override
    public T get() throws ConcurrentException {
        Object object = this.object;

        if (object == null) {
            synchronized (this) {
                object = this.object;
                if (object == null) {
                    this.object = object = this.initialize();
                }
            }
        }

        return object;
    }

    protected abstract T initialize() throws ConcurrentException;
}
