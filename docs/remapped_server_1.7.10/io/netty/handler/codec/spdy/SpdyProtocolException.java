package io.netty.handler.codec.spdy;

public class SpdyProtocolException extends Exception {

    private static final long serialVersionUID = 7870000537743847264L;

    public SpdyProtocolException() {}

    public SpdyProtocolException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SpdyProtocolException(String s) {
        super(s);
    }

    public SpdyProtocolException(Throwable throwable) {
        super(throwable);
    }
}
