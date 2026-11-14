package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

final class ComposedLastHttpContent implements LastHttpContent {

    private final HttpHeaders trailingHeaders;
    private DecoderResult result;

    ComposedLastHttpContent(HttpHeaders httpheaders) {
        this.trailingHeaders = httpheaders;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public LastHttpContent copy() {
        DefaultLastHttpContent defaultlasthttpcontent = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);

        defaultlasthttpcontent.trailingHeaders().set(this.trailingHeaders());
        return defaultlasthttpcontent;
    }

    @Override
    public LastHttpContent retain(int i) {
        return this;
    }

    @Override
    public LastHttpContent retain() {
        return this;
    }

    @Override
    public HttpContent duplicate() {
        return this.copy();
    }

    @Override
    public ByteBuf content() {
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return this.result;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderresult) {
        this.result = decoderresult;
    }

    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int i) {
        return false;
    }
}
