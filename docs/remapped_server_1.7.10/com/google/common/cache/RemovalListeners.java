package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

@Beta
public final class RemovalListeners {

    private RemovalListeners() {}

    public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> removallistener, final Executor executor) {
        Preconditions.checkNotNull(removallistener);
        Preconditions.checkNotNull(executor);
        return new RemovalListener<K, V>() {
            @Override
            public void onRemoval(final RemovalNotification<K, V> removalnotification) {
                executor.execute(new Runnable() {
                    public void run() {
                        removallistener.onRemoval(removalnotification);
                    }
                });
            }
        };
    }
}
