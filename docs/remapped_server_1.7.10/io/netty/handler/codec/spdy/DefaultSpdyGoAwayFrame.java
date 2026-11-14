package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyGoAwayFrame implements SpdyGoAwayFrame {

    private int lastGoodStreamId;
    private SpdySessionStatus status;

    public DefaultSpdyGoAwayFrame(int i) {
        this(i, 0);
    }

    public DefaultSpdyGoAwayFrame(int i, int j) {
        this(i, SpdySessionStatus.valueOf(j));
    }

    public DefaultSpdyGoAwayFrame(int i, SpdySessionStatus spdysessionstatus) {
        this.setLastGoodStreamId(i);
        this.setStatus(spdysessionstatus);
    }

    @Override
    public int getLastGoodStreamId() {
        return this.lastGoodStreamId;
    }

    @Override
    public SpdyGoAwayFrame setLastGoodStreamId(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Last-good-stream-ID cannot be negative: " + i);
        } else {
            this.lastGoodStreamId = i;
            return this;
        }
    }

    @Override
    public SpdySessionStatus getStatus() {
        return this.status;
    }

    @Override
    public SpdyGoAwayFrame setStatus(SpdySessionStatus spdysessionstatus) {
        this.status = spdysessionstatus;
        return this;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Last-good-stream-ID = ");
        stringbuilder.append(this.getLastGoodStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Status: ");
        stringbuilder.append(this.getStatus().toString());
        return stringbuilder.toString();
    }
}
