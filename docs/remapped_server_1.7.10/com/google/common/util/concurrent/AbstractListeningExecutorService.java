package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {

    public AbstractListeningExecutorService() {}

    protected final <T> ListenableFutureTask<T> newTaskFor(Runnable runnable, T t0) {
        return ListenableFutureTask.create(runnable, t0);
    }

    protected final <T> ListenableFutureTask<T> newTaskFor(Callable<T> callable) {
        return ListenableFutureTask.create(callable);
    }

    @Override
    public ListenableFuture<?> submit(Runnable runnable) {
        return (ListenableFuture) super.submit(runnable);
    }

    @Override
    public <T> ListenableFuture<T> submit(Runnable runnable, @Nullable T t0) {
        return (ListenableFuture) super.submit(runnable, t0);
    }

    @Override
    public <T> ListenableFuture<T> submit(Callable<T> callable) {
        return (ListenableFuture) super.submit(callable);
    }
}
