package io.netty.handler.codec.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

public class QueryStringEncoder {

    private final Charset charset;
    private final String uri;
    private final List<QueryStringEncoder.Param> params;

    public QueryStringEncoder(String s) {
        this(s, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringEncoder(String s, Charset charset) {
        this.params = new ArrayList();
        if (s == null) {
            throw new NullPointerException("getUri");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.uri = s;
            this.charset = charset;
        }
    }

    public void addParam(String s, String s1) {
        if (s == null) {
            throw new NullPointerException("name");
        } else if (s1 == null) {
            throw new NullPointerException("value");
        } else {
            this.params.add(new QueryStringEncoder.Param(s, s1));
        }
    }

    public URI toUri() throws URISyntaxException {
        return new URI(this.toString());
    }

    public String toString() {
        if (this.params.isEmpty()) {
            return this.uri;
        } else {
            StringBuilder stringbuilder = (new StringBuilder(this.uri)).append('?');

            for (int i = 0; i < this.params.size(); ++i) {
                QueryStringEncoder.Param querystringencoder_param = (QueryStringEncoder.Param) this.params.get(i);

                stringbuilder.append(encodeComponent(querystringencoder_param.name, this.charset));
                stringbuilder.append('=');
                stringbuilder.append(encodeComponent(querystringencoder_param.value, this.charset));
                if (i != this.params.size() - 1) {
                    stringbuilder.append('&');
                }
            }

            return stringbuilder.toString();
        }
    }

    private static String encodeComponent(String s, Charset charset) {
        try {
            return URLEncoder.encode(s, charset.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            throw new UnsupportedCharsetException(charset.name());
        }
    }

    private static final class Param {

        final String name;
        final String value;

        Param(String s, String s1) {
            this.value = s1;
            this.name = s;
        }
    }
}
