package io.netty.handler.codec.compression;

import io.netty.handler.codec.EncoderException;

public class CompressionException extends EncoderException {

    private static final long serialVersionUID = 5603413481274811897L;

    public CompressionException() {}

    public CompressionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CompressionException(String s) {
        super(s);
    }

    public CompressionException(Throwable throwable) {
        super(throwable);
    }
}
