package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame extends DefaultSpdyHeadersFrame implements SpdySynStreamFrame {

    private int associatedToStreamId;
    private byte priority;
    private boolean unidirectional;

    public DefaultSpdySynStreamFrame(int i, int j, byte b0) {
        super(i);
        this.setAssociatedToStreamId(j);
        this.setPriority(b0);
    }

    @Override
    public SpdySynStreamFrame setStreamId(int i) {
        super.setStreamId(i);
        return this;
    }

    @Override
    public SpdySynStreamFrame setLast(boolean flag) {
        super.setLast(flag);
        return this;
    }

    @Override
    public SpdySynStreamFrame setInvalid() {
        super.setInvalid();
        return this;
    }

    @Override
    public int getAssociatedToStreamId() {
        return this.associatedToStreamId;
    }

    @Override
    public SpdySynStreamFrame setAssociatedToStreamId(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Associated-To-Stream-ID cannot be negative: " + i);
        } else {
            this.associatedToStreamId = i;
            return this;
        }
    }

    @Override
    public byte getPriority() {
        return this.priority;
    }

    @Override
    public SpdySynStreamFrame setPriority(byte b0) {
        if (b0 >= 0 && b0 <= 7) {
            this.priority = b0;
            return this;
        } else {
            throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + b0);
        }
    }

    @Override
    public boolean isUnidirectional() {
        return this.unidirectional;
    }

    @Override
    public SpdySynStreamFrame setUnidirectional(boolean flag) {
        this.unidirectional = flag;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("(last: ");
        stringbuilder.append(this.isLast());
        stringbuilder.append("; unidirectional: ");
        stringbuilder.append(this.isUnidirectional());
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Stream-ID = ");
        stringbuilder.append(this.getStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        if (this.associatedToStreamId != 0) {
            stringbuilder.append("--> Associated-To-Stream-ID = ");
            stringbuilder.append(this.getAssociatedToStreamId());
            stringbuilder.append(StringUtil.NEWLINE);
        }

        stringbuilder.append("--> Priority = ");
        stringbuilder.append(this.getPriority());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Headers:");
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }
}
