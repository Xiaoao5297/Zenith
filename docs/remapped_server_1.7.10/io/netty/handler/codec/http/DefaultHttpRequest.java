package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;

public class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {

    private HttpMethod method;
    private String uri;

    public DefaultHttpRequest(HttpVersion httpversion, HttpMethod httpmethod, String s) {
        super(httpversion);
        if (httpmethod == null) {
            throw new NullPointerException("getMethod");
        } else if (s == null) {
            throw new NullPointerException("getUri");
        } else {
            this.method = httpmethod;
            this.uri = s;
        }
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public HttpRequest setMethod(HttpMethod httpmethod) {
        if (httpmethod == null) {
            throw new NullPointerException("method");
        } else {
            this.method = httpmethod;
            return this;
        }
    }

    @Override
    public HttpRequest setUri(String s) {
        if (s == null) {
            throw new NullPointerException("method");
        } else {
            this.uri = s;
            return this;
        }
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion httpversion) {
        super.setProtocolVersion(httpversion);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(", decodeResult: ");
        stringbuilder.append(this.getDecoderResult());
        stringbuilder.append(')');
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append(this.getMethod().toString());
        stringbuilder.append(' ');
        stringbuilder.append(this.getUri());
        stringbuilder.append(' ');
        stringbuilder.append(this.getProtocolVersion().text());
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendHeaders(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }
}
