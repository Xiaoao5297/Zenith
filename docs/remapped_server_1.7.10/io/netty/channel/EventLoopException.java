package io.netty.channel;

public class EventLoopException extends ChannelException {

    private static final long serialVersionUID = -8969100344583703616L;

    public EventLoopException() {}

    public EventLoopException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventLoopException(String s) {
        super(s);
    }

    public EventLoopException(Throwable throwable) {
        super(throwable);
    }
}
