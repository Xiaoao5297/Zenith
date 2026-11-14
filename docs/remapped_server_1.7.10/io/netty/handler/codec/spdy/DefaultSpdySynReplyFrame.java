package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdySynReplyFrame extends DefaultSpdyHeadersFrame implements SpdySynReplyFrame {

    public DefaultSpdySynReplyFrame(int i) {
        super(i);
    }

    @Override
    public SpdySynReplyFrame setStreamId(int i) {
        super.setStreamId(i);
        return this;
    }

    @Override
    public SpdySynReplyFrame setLast(boolean flag) {
        super.setLast(flag);
        return this;
    }

    @Override
    public SpdySynReplyFrame setInvalid() {
        super.setInvalid();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("(last: ");
        stringbuilder.append(this.isLast());
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Stream-ID = ");
        stringbuilder.append(this.getStreamId());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> Headers:");
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }
}
