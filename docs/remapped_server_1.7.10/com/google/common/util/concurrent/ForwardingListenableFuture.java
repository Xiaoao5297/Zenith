package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V> implements ListenableFuture<V> {

    protected ForwardingListenableFuture() {}

    @Override
    protected abstract ListenableFuture<V> delegate();

    @Override
    public void addListener(Runnable runnable, Executor executor) {
        this.delegate().addListener(runnable, executor);
    }

    public abstract static class SimpleForwardingListenableFuture<V> extends ForwardingListenableFuture<V> {

        private final ListenableFuture<V> delegate;

        protected SimpleForwardingListenableFuture(ListenableFuture<V> listenablefuture) {
            this.delegate = (ListenableFuture) Preconditions.checkNotNull(listenablefuture);
        }

        @Override
        protected final ListenableFuture<V> delegate() {
            return this.delegate;
        }
    }
}
