package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame {

    private int streamId;
    private int deltaWindowSize;

    public DefaultSpdyWindowUpdateFrame(int i, int j) {
        this.setStreamId(i);
        this.setDeltaWindowSize(j);
    }

    @Override
    public int getStreamId() {
        return this.streamId;
    }

    @Override
    public SpdyWindowUpdateFrame setStreamId(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Stream-ID cannot be negative: " + i);
        } else {
            this.streamId = i;
            return this;
        }
    }

    @Override
    public int getDeltaWindowSize() {
        return this.deltaWindowSize;
    }

    @Override
    public SpdyWindowUpdateFrame setDeltaWindowSize(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Delta-Window-Size must be positive: " + i);
        } else {
            this.deltaWindowSize = i;
            return this;
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Stream-ID = ");
        stringbuilder.append(this.getStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Delta-Window-Size = ");
        stringbuilder.append(this.getDeltaWindowSize());
        return stringbuilder.toString();
    }
}
