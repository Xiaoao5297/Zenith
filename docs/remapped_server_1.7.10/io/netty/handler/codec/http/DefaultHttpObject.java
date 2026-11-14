package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public class DefaultHttpObject implements HttpObject {

    private DecoderResult decoderResult;

    protected DefaultHttpObject() {
        this.decoderResult = DecoderResult.SUCCESS;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return this.decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderresult) {
        if (decoderresult == null) {
            throw new NullPointerException("decoderResult");
        } else {
            this.decoderResult = decoderresult;
        }
    }
}
