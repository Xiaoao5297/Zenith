package io.netty.handler.codec;

public class EncoderException extends CodecException {

    private static final long serialVersionUID = -5086121160476476774L;

    public EncoderException() {}

    public EncoderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EncoderException(String s) {
        super(s);
    }

    public EncoderException(Throwable throwable) {
        super(throwable);
    }
}
