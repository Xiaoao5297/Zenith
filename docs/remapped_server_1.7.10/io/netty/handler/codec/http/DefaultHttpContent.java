package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;

public class DefaultHttpContent extends DefaultHttpObject implements HttpContent {

    private final ByteBuf content;

    public DefaultHttpContent(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException("content");
        } else {
            this.content = bytebuf;
        }
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public HttpContent copy() {
        return new DefaultHttpContent(this.content.copy());
    }

    @Override
    public HttpContent duplicate() {
        return new DefaultHttpContent(this.content.duplicate());
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public HttpContent retain() {
        this.content.retain();
        return this;
    }

    @Override
    public HttpContent retain(int i) {
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

    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.content() + ", getDecoderResult: " + this.getDecoderResult() + ')';
    }
}
