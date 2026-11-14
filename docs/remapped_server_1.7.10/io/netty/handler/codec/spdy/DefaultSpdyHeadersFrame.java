package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;

public class DefaultSpdyHeadersFrame extends DefaultSpdyStreamFrame implements SpdyHeadersFrame {

    private boolean invalid;
    private boolean truncated;
    private final SpdyHeaders headers = new DefaultSpdyHeaders();

    public DefaultSpdyHeadersFrame(int i) {
        super(i);
    }

    @Override
    public SpdyHeadersFrame setStreamId(int i) {
        super.setStreamId(i);
        return this;
    }

    @Override
    public SpdyHeadersFrame setLast(boolean flag) {
        super.setLast(flag);
        return this;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public SpdyHeadersFrame setInvalid() {
        this.invalid = true;
        return this;
    }

    @Override
    public boolean isTruncated() {
        return this.truncated;
    }

    @Override
    public SpdyHeadersFrame setTruncated() {
        this.truncated = true;
        return this;
    }

    @Override
    public SpdyHeaders headers() {
        return this.headers;
    }

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

    protected void appendHeaders(StringBuilder stringbuilder) {
        Iterator iterator = this.headers().entries().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            stringbuilder.append("    ");
            stringbuilder.append((String) entry.getKey());
            stringbuilder.append(": ");
            stringbuilder.append((String) entry.getValue());
            stringbuilder.append(StringUtil.NEWLINE);
        }

    }
}
