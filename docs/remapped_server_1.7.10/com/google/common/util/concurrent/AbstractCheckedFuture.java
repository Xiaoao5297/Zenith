package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract class AbstractCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements CheckedFuture<V, X> {

    protected AbstractCheckedFuture(ListenableFuture<V> listenablefuture) {
        super(listenablefuture);
    }

    protected abstract X mapException(Exception exception);

    @Override
    public V checkedGet() throws X {
        try {
            return this.get();
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
            throw this.mapException(interruptedexception);
        } catch (CancellationException cancellationexception) {
            throw this.mapException(cancellationexception);
        } catch (ExecutionException executionexception) {
            throw this.mapException(executionexception);
        }
    }

    @Override
    public V checkedGet(long i, TimeUnit timeunit) throws TimeoutException, X {
        try {
            return this.get(i, timeunit);
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
            throw this.mapException(interruptedexception);
        } catch (CancellationException cancellationexception) {
            throw this.mapException(cancellationexception);
        } catch (ExecutionException executionexception) {
            throw this.mapException(executionexception);
        }
    }
}
