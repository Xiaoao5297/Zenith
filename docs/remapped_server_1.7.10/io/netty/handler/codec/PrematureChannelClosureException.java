package io.netty.handler.codec;

public class PrematureChannelClosureException extends CodecException {

    private static final long serialVersionUID = 4907642202594703094L;

    public PrematureChannelClosureException() {}

    public PrematureChannelClosureException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PrematureChannelClosureException(String s) {
        super(s);
    }

    public PrematureChannelClosureException(Throwable throwable) {
        super(throwable);
    }
}
