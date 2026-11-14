package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;

public class DefaultLastHttpContent extends DefaultHttpContent implements LastHttpContent {

    private final HttpHeaders trailingHeaders;

    public DefaultLastHttpContent() {
        this(Unpooled.buffer(0));
    }

    public DefaultLastHttpContent(ByteBuf bytebuf) {
        super(bytebuf);
        this.trailingHeaders = new DefaultHttpHeaders() {
            @Override
            void validateHeaderName0(String s) {
                super.validateHeaderName0(s);
                if (s.equalsIgnoreCase("Content-Length") || s.equalsIgnoreCase("Transfer-Encoding") || s.equalsIgnoreCase("Trailer")) {
                    throw new IllegalArgumentException("prohibited trailing header: " + s);
                }
            }
        };
    }

    @Override
    public LastHttpContent copy() {
        DefaultLastHttpContent defaultlasthttpcontent = new DefaultLastHttpContent(this.content().copy());

        defaultlasthttpcontent.trailingHeaders().set(this.trailingHeaders());
        return defaultlasthttpcontent;
    }

    @Override
    public LastHttpContent duplicate() {
        DefaultLastHttpContent defaultlasthttpcontent = new DefaultLastHttpContent(this.content().duplicate());

        defaultlasthttpcontent.trailingHeaders().set(this.trailingHeaders());
        return defaultlasthttpcontent;
    }

    @Override
    public LastHttpContent retain(int i) {
        super.retain(i);
        return this;
    }

    @Override
    public LastHttpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(super.toString());

        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }

    private void appendHeaders(StringBuilder stringbuilder) {
        Iterator iterator = this.trailingHeaders().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            stringbuilder.append((String) entry.getKey());
            stringbuilder.append(": ");
            stringbuilder.append((String) entry.getValue());
            stringbuilder.append(StringUtil.NEWLINE);
        }

    }
}
