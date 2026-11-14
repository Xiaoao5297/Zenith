package io.netty.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFuture<V> implements Future<V> {

    public AbstractFuture() {}

    public V get() throws InterruptedException, ExecutionException {
        this.await();
        Throwable throwable = this.cause();

        if (throwable == null) {
            return this.getNow();
        } else {
            throw new ExecutionException(throwable);
        }
    }

    public V get(long i, TimeUnit timeunit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.await(i, timeunit)) {
            Throwable throwable = this.cause();

            if (throwable == null) {
                return this.getNow();
            } else {
                throw new ExecutionException(throwable);
            }
        } else {
            throw new TimeoutException();
        }
    }
}
