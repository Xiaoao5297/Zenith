package io.netty.handler.codec;

public class DecoderException extends CodecException {

    private static final long serialVersionUID = 6926716840699621852L;

    public DecoderException() {}

    public DecoderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DecoderException(String s) {
        super(s);
    }

    public DecoderException(Throwable throwable) {
        super(throwable);
    }
}
