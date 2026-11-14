package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {

    private HttpResponseStatus status;

    public DefaultHttpResponse(HttpVersion httpversion, HttpResponseStatus httpresponsestatus) {
        super(httpversion);
        if (httpresponsestatus == null) {
            throw new NullPointerException("status");
        } else {
            this.status = httpresponsestatus;
        }
    }

    @Override
    public HttpResponseStatus getStatus() {
        return this.status;
    }

    @Override
    public HttpResponse setStatus(HttpResponseStatus httpresponsestatus) {
        if (httpresponsestatus == null) {
            throw new NullPointerException("status");
        } else {
            this.status = httpresponsestatus;
            return this;
        }
    }

    @Override
    public HttpResponse setProtocolVersion(HttpVersion httpversion) {
        super.setProtocolVersion(httpversion);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append("(decodeResult: ");
        stringbuilder.append(this.getDecoderResult());
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.getProtocolVersion().text());
        stringbuilder.append(' ');
        stringbuilder.append(this.getStatus().toString());
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }
}
