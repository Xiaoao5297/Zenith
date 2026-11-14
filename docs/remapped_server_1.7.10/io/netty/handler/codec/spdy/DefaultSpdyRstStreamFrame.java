package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyRstStreamFrame extends DefaultSpdyStreamFrame implements SpdyRstStreamFrame {

    private SpdyStreamStatus status;

    public DefaultSpdyRstStreamFrame(int i, int j) {
        this(i, SpdyStreamStatus.valueOf(j));
    }

    public DefaultSpdyRstStreamFrame(int i, SpdyStreamStatus spdystreamstatus) {
        super(i);
        this.setStatus(spdystreamstatus);
    }

    @Override
    public SpdyRstStreamFrame setStreamId(int i) {
        super.setStreamId(i);
        return this;
    }

    @Override
    public SpdyRstStreamFrame setLast(boolean flag) {
        super.setLast(flag);
        return this;
    }

    @Override
    public SpdyStreamStatus getStatus() {
        return this.status;
    }

    @Override
    public SpdyRstStreamFrame setStatus(SpdyStreamStatus spdystreamstatus) {
        this.status = spdystreamstatus;
        return this;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Stream-ID = ");
        stringbuilder.append(this.getStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Status: ");
        stringbuilder.append(this.getStatus().toString());
        return stringbuilder.toString();
    }
}
