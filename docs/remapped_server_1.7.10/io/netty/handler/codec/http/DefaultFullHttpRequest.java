package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultFullHttpRequest extends DefaultHttpRequest implements FullHttpRequest {

    private final ByteBuf content;
    private final HttpHeaders trailingHeader;

    public DefaultFullHttpRequest(HttpVersion httpversion, HttpMethod httpmethod, String s) {
        this(httpversion, httpmethod, s, Unpooled.buffer(0));
    }

    public DefaultFullHttpRequest(HttpVersion httpversion, HttpMethod httpmethod, String s, ByteBuf bytebuf) {
        super(httpversion, httpmethod, s);
        this.trailingHeader = new DefaultHttpHeaders();
        if (bytebuf == null) {
            throw new NullPointerException("content");
        } else {
            this.content = bytebuf;
        }
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeader;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public FullHttpRequest retain() {
        this.content.retain();
        return this;
    }

    @Override
    public FullHttpRequest retain(int i) {
        this.content.retain(i);
        return this;
    }

    @Override
    public boolean release() {
        return this.content.release();
    }

    @Override
    public boolean release(int i) {
        return this.content.release(i);
    }

    @Override
    public FullHttpRequest setProtocolVersion(HttpVersion httpversion) {
        super.setProtocolVersion(httpversion);
        return this;
    }

    @Override
    public FullHttpRequest setMethod(HttpMethod httpmethod) {
        super.setMethod(httpmethod);
        return this;
    }

    @Override
    public FullHttpRequest setUri(String s) {
        super.setUri(s);
        return this;
    }

    @Override
    public FullHttpRequest copy() {
        DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().copy());

        defaultfullhttprequest.headers().set(this.headers());
        defaultfullhttprequest.trailingHeaders().set(this.trailingHeaders());
        return defaultfullhttprequest;
    }

    @Override
    public FullHttpRequest duplicate() {
        DefaultFullHttpRequest defaultfullhttprequest = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().duplicate());

        defaultfullhttprequest.headers().set(this.headers());
        defaultfullhttprequest.trailingHeaders().set(this.trailingHeaders());
        return defaultfullhttprequest;
    }
}
