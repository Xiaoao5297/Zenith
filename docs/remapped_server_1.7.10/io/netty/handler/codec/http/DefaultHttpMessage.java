package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;

public abstract class DefaultHttpMessage extends DefaultHttpObject implements HttpMessage {

    private HttpVersion version;
    private final HttpHeaders headers = new DefaultHttpHeaders();

    protected DefaultHttpMessage(HttpVersion httpversion) {
        if (httpversion == null) {
            throw new NullPointerException("version");
        } else {
            this.version = httpversion;
        }
    }

    @Override
    public HttpHeaders headers() {
        return this.headers;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return this.version;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("(version: ");
        stringbuilder.append(this.getProtocolVersion().text());
        stringbuilder.append(", keepAlive: ");
        stringbuilder.append(HttpHeaders.isKeepAlive(this));
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }

    @Override
    public HttpMessage setProtocolVersion(HttpVersion httpversion) {
        if (httpversion == null) {
            throw new NullPointerException("version");
        } else {
            this.version = httpversion;
            return this;
        }
    }

    void appendHeaders(StringBuilder stringbuilder) {
        Iterator iterator = this.headers().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            stringbuilder.append((String) entry.getKey());
            stringbuilder.append(": ");
            stringbuilder.append((String) entry.getValue());
            stringbuilder.append(StringUtil.NEWLINE);
        }

    }
}
