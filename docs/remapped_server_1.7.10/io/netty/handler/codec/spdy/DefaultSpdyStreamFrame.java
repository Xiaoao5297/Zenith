package io.netty.handler.codec.spdy;

public abstract class DefaultSpdyStreamFrame implements SpdyStreamFrame {

    private int streamId;
    private boolean last;

    protected DefaultSpdyStreamFrame(int i) {
        this.setStreamId(i);
    }

    @Override
    public int getStreamId() {
        return this.streamId;
    }

    @Override
    public SpdyStreamFrame setStreamId(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Stream-ID must be positive: " + i);
        } else {
            this.streamId = i;
            return this;
        }
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    @Override
    public SpdyStreamFrame setLast(boolean flag) {
        this.last = flag;
        return this;
    }
}
