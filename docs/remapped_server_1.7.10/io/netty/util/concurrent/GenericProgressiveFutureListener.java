package io.netty.util.concurrent;

public interface GenericProgressiveFutureListener<F extends ProgressiveFuture<?>> extends GenericFutureListener<F> {

    void operationProgressed(F f0, long i, long j) throws Exception;
}
