package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

public interface LastHttpContent extends HttpContent {

    LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent() {
        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public LastHttpContent copy() {
            return null.EMPTY_LAST_CONTENT;
        }

        @Override
        public LastHttpContent duplicate() {
            return this;
        }

        @Override
        public HttpHeaders trailingHeaders() {
            return HttpHeaders.EMPTY_HEADERS;
        }

        @Override
        public DecoderResult getDecoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Override
        public void setDecoderResult(DecoderResult decoderresult) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public LastHttpContent retain() {
            return this;
        }

        @Override
        public LastHttpContent retain(int i) {
            return this;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int i) {
            return false;
        }
    };

    HttpHeaders trailingHeaders();

    @Override
    LastHttpContent copy();

    @Override
    LastHttpContent retain(int i);

    @Override
    LastHttpContent retain();
}
