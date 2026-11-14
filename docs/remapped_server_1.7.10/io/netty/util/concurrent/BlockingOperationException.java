package io.netty.util.concurrent;

public class BlockingOperationException extends IllegalStateException {

    private static final long serialVersionUID = 2462223247762460301L;

    public BlockingOperationException() {}

    public BlockingOperationException(String s) {
        super(s);
    }

    public BlockingOperationException(Throwable throwable) {
        super(throwable);
    }

    public BlockingOperationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
