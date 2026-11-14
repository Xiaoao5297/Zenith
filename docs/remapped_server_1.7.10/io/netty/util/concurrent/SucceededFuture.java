package io.netty.util.concurrent;

public final class SucceededFuture<V> extends CompleteFuture<V> {

    private final V result;

    public SucceededFuture(EventExecutor eventexecutor, V v0) {
        super(eventexecutor);
        this.result = v0;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public V getNow() {
        return this.result;
    }
}
