package io.netty.channel;

public class ChannelPipelineException extends ChannelException {

    private static final long serialVersionUID = 3379174210419885980L;

    public ChannelPipelineException() {}

    public ChannelPipelineException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ChannelPipelineException(String s) {
        super(s);
    }

    public ChannelPipelineException(Throwable throwable) {
        super(throwable);
    }
}
