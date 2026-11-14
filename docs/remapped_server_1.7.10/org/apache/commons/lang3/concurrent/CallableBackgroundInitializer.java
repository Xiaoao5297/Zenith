package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T> {

    private final Callable<T> callable;

    public CallableBackgroundInitializer(Callable<T> callable) {
        this.checkCallable(callable);
        this.callable = callable;
    }

    public CallableBackgroundInitializer(Callable<T> callable, ExecutorService executorservice) {
        super(executorservice);
        this.checkCallable(callable);
        this.callable = callable;
    }

    @Override
    protected T initialize() throws Exception {
        return this.callable.call();
    }

    private void checkCallable(Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException("Callable must not be null!");
        }
    }
}
