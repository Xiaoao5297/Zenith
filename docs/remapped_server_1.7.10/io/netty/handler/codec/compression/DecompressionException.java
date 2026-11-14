package io.netty.handler.codec.compression;

import io.netty.handler.codec.DecoderException;

public class DecompressionException extends DecoderException {

    private static final long serialVersionUID = 3546272712208105199L;

    public DecompressionException() {}

    public DecompressionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DecompressionException(String s) {
        super(s);
    }

    public DecompressionException(Throwable throwable) {
        super(throwable);
    }
}
