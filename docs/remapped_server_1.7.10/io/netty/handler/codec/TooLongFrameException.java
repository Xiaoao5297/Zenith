package io.netty.handler.codec;

public class TooLongFrameException extends DecoderException {

    private static final long serialVersionUID = -1995801950698951640L;

    public TooLongFrameException() {}

    public TooLongFrameException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TooLongFrameException(String s) {
        super(s);
    }

    public TooLongFrameException(Throwable throwable) {
        super(throwable);
    }
}
