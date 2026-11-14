package com.google.common.util.concurrent;

import java.util.concurrent.Callable;

public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService implements ListeningExecutorService {

    protected ForwardingListeningExecutorService() {}

    @Override
    protected abstract ListeningExecutorService delegate();

    @Override
    public <T> ListenableFuture<T> submit(Callable<T> callable) {
        return this.delegate().submit(callable);
    }

    @Override
    public ListenableFuture<?> submit(Runnable runnable) {
        return this.delegate().submit(runnable);
    }

    @Override
    public <T> ListenableFuture<T> submit(Runnable runnable, T t0) {
        return this.delegate().submit(runnable, t0);
    }
}
