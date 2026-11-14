package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract class ForwardingCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture<V> implements CheckedFuture<V, X> {

    public ForwardingCheckedFuture() {}

    @Override
    public V checkedGet() throws X {
        return this.delegate().checkedGet();
    }

    @Override
    public V checkedGet(long i, TimeUnit timeunit) throws TimeoutException, X {
        return this.delegate().checkedGet(i, timeunit);
    }

    @Override
    protected abstract CheckedFuture<V, X> delegate();

    @Beta
    public abstract static class SimpleForwardingCheckedFuture<V, X extends Exception> extends ForwardingCheckedFuture<V, X> {

        private final CheckedFuture<V, X> delegate;

        protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> checkedfuture) {
            this.delegate = (CheckedFuture) Preconditions.checkNotNull(checkedfuture);
        }

        @Override
        protected final CheckedFuture<V, X> delegate() {
            return this.delegate;
        }
    }
}
