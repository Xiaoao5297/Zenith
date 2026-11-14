package io.netty.util;

public class IllegalReferenceCountException extends IllegalStateException {

    private static final long serialVersionUID = -2507492394288153468L;

    public IllegalReferenceCountException() {}

    public IllegalReferenceCountException(int i) {
        this("refCnt: " + i);
    }

    public IllegalReferenceCountException(int i, int j) {
        this("refCnt: " + i + ", " + (j > 0 ? "increment: " + j : "decrement: " + -j));
    }

    public IllegalReferenceCountException(String s) {
        super(s);
    }

    public IllegalReferenceCountException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IllegalReferenceCountException(Throwable throwable) {
        super(throwable);
    }
}
