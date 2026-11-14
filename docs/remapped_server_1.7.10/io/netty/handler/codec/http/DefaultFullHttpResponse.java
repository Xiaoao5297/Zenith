package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultFullHttpResponse extends DefaultHttpResponse implements FullHttpResponse {

    private final ByteBuf content;
    private final HttpHeaders trailingHeaders;

    public DefaultFullHttpResponse(HttpVersion httpversion, HttpResponseStatus httpresponsestatus) {
        this(httpversion, httpresponsestatus, Unpooled.buffer(0));
    }

    public DefaultFullHttpResponse(HttpVersion httpversion, HttpResponseStatus httpresponsestatus, ByteBuf bytebuf) {
        super(httpversion, httpresponsestatus);
        this.trailingHeaders = new DefaultHttpHeaders();
        if (bytebuf == null) {
            throw new NullPointerException("content");
        } else {
            this.content = bytebuf;
        }
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
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
    public FullHttpResponse retain() {
        this.content.retain();
        return this;
    }

    @Override
    public FullHttpResponse retain(int i) {
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
    public FullHttpResponse setProtocolVersion(HttpVersion httpversion) {
        super.setProtocolVersion(httpversion);
        return this;
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus httpresponsestatus) {
        super.setStatus(httpresponsestatus);
        return this;
    }

    @Override
    public FullHttpResponse copy() {
        DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().copy());

        defaultfullhttpresponse.headers().set(this.headers());
        defaultfullhttpresponse.trailingHeaders().set(this.trailingHeaders());
        return defaultfullhttpresponse;
    }

    @Override
    public FullHttpResponse duplicate() {
        DefaultFullHttpResponse defaultfullhttpresponse = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().duplicate());

        defaultfullhttpresponse.headers().set(this.headers());
        defaultfullhttpresponse.trailingHeaders().set(this.trailingHeaders());
        return defaultfullhttpresponse;
    }
}
