package io.netty.handler.codec;

public class CodecException extends RuntimeException {

    private static final long serialVersionUID = -1464830400709348473L;

    public CodecException() {}

    public CodecException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CodecException(String s) {
        super(s);
    }

    public CodecException(Throwable throwable) {
        super(throwable);
    }
}
