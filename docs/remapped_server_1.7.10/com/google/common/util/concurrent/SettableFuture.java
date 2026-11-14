package com.google.common.util.concurrent;

import javax.annotation.Nullable;

public final class SettableFuture<V> extends AbstractFuture<V> {

    public static <V> SettableFuture<V> create() {
        return new SettableFuture<>();
    }

    private SettableFuture() {}

    @Override
    public boolean set(@Nullable V v0) {
        return super.set(v0);
    }

    @Override
    public boolean setException(Throwable throwable) {
        return super.setException(throwable);
    }
}
