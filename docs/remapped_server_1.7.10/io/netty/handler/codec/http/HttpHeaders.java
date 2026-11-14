package io.netty.handler.codec.http;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class HttpHeaders implements Iterable<Entry<String, String>> {

    public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders() {
        @Override
        public String get(String s) {
            return null;
        }

        @Override
        public List<String> getAll(String s) {
            return Collections.emptyList();
        }

        @Override
        public List<Entry<String, String>> entries() {
            return Collections.emptyList();
        }

        @Override
        public boolean contains(String s) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Set<String> names() {
            return Collections.emptySet();
        }

        @Override
        public HttpHeaders add(String s, Object object) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders add(String s, Iterable<?> iterable) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders set(String s, Object object) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders set(String s, Iterable<?> iterable) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders remove(String s) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public HttpHeaders clear() {
            throw new UnsupportedOperationException("read only");
        }

        public Iterator<Entry<String, String>> iterator() {
            return this.entries().iterator();
        }
    };

    public static boolean isKeepAlive(HttpMessage httpmessage) {
        String s = httpmessage.headers().get("Connection");

        return "close".equalsIgnoreCase(s) ? false : (httpmessage.getProtocolVersion().isKeepAliveDefault() ? !"close".equalsIgnoreCase(s) : "keep-alive".equalsIgnoreCase(s));
    }

    public static void setKeepAlive(HttpMessage httpmessage, boolean flag) {
        HttpHeaders httpheaders = httpmessage.headers();

        if (httpmessage.getProtocolVersion().isKeepAliveDefault()) {
            if (flag) {
                httpheaders.remove("Connection");
            } else {
                httpheaders.set("Connection", (Object) "close");
            }
        } else if (flag) {
            httpheaders.set("Connection", (Object) "keep-alive");
        } else {
            httpheaders.remove("Connection");
        }

    }

    public static String getHeader(HttpMessage httpmessage, String s) {
        return httpmessage.headers().get(s);
    }

    public static String getHeader(HttpMessage httpmessage, String s, String s1) {
        String s2 = httpmessage.headers().get(s);

        return s2 == null ? s1 : s2;
    }

    public static void setHeader(HttpMessage httpmessage, String s, Object object) {
        httpmessage.headers().set(s, object);
    }

    public static void setHeader(HttpMessage httpmessage, String s, Iterable<?> iterable) {
        httpmessage.headers().set(s, iterable);
    }

    public static void addHeader(HttpMessage httpmessage, String s, Object object) {
        httpmessage.headers().add(s, object);
    }

    public static void removeHeader(HttpMessage httpmessage, String s) {
        httpmessage.headers().remove(s);
    }

    public static void clearHeaders(HttpMessage httpmessage) {
        httpmessage.headers().clear();
    }

    public static int getIntHeader(HttpMessage httpmessage, String s) {
        String s1 = getHeader(httpmessage, s);

        if (s1 == null) {
            throw new NumberFormatException("header not found: " + s);
        } else {
            return Integer.parseInt(s1);
        }
    }

    public static int getIntHeader(HttpMessage httpmessage, String s, int i) {
        String s1 = getHeader(httpmessage, s);

        if (s1 == null) {
            return i;
        } else {
            try {
                return Integer.parseInt(s1);
            } catch (NumberFormatException numberformatexception) {
                return i;
            }
        }
    }

    public static void setIntHeader(HttpMessage httpmessage, String s, int i) {
        httpmessage.headers().set(s, (Object) i);
    }

    public static void setIntHeader(HttpMessage httpmessage, String s, Iterable<Integer> iterable) {
        httpmessage.headers().set(s, iterable);
    }

    public static void addIntHeader(HttpMessage httpmessage, String s, int i) {
        httpmessage.headers().add(s, (Object) i);
    }

    public static Date getDateHeader(HttpMessage httpmessage, String s) throws ParseException {
        String s1 = getHeader(httpmessage, s);

        if (s1 == null) {
            throw new ParseException("header not found: " + s, 0);
        } else {
            return HttpHeaderDateFormat.get().parse(s1);
        }
    }

    public static Date getDateHeader(HttpMessage httpmessage, String s, Date date) {
        String s1 = getHeader(httpmessage, s);

        if (s1 == null) {
            return date;
        } else {
            try {
                return HttpHeaderDateFormat.get().parse(s1);
            } catch (ParseException parseexception) {
                return date;
            }
        }
    }

    public static void setDateHeader(HttpMessage httpmessage, String s, Date date) {
        if (date != null) {
            httpmessage.headers().set(s, (Object) HttpHeaderDateFormat.get().format(date));
        } else {
            httpmessage.headers().set(s, (Iterable) null);
        }

    }

    public static void setDateHeader(HttpMessage httpmessage, String s, Iterable<Date> iterable) {
        httpmessage.headers().set(s, iterable);
    }

    public static void addDateHeader(HttpMessage httpmessage, String s, Date date) {
        httpmessage.headers().add(s, (Object) date);
    }

    public static long getContentLength(HttpMessage httpmessage) {
        String s = getHeader(httpmessage, "Content-Length");

        if (s != null) {
            return Long.parseLong(s);
        } else {
            long i = (long) getWebSocketContentLength(httpmessage);

            if (i >= 0L) {
                return i;
            } else {
                throw new NumberFormatException("header not found: Content-Length");
            }
        }
    }

    public static long getContentLength(HttpMessage httpmessage, long i) {
        String s = httpmessage.headers().get("Content-Length");

        if (s != null) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException numberformatexception) {
                return i;
            }
        } else {
            long j = (long) getWebSocketContentLength(httpmessage);

            return j >= 0L ? j : i;
        }
    }

    private static int getWebSocketContentLength(HttpMessage httpmessage) {
        HttpHeaders httpheaders = httpmessage.headers();

        if (httpmessage instanceof HttpRequest) {
            HttpRequest httprequest = (HttpRequest) httpmessage;

            if (HttpMethod.GET.equals(httprequest.getMethod()) && httpheaders.contains("Sec-WebSocket-Key1") && httpheaders.contains("Sec-WebSocket-Key2")) {
                return 8;
            }
        } else if (httpmessage instanceof HttpResponse) {
            HttpResponse httpresponse = (HttpResponse) httpmessage;

            if (httpresponse.getStatus().code() == 101 && httpheaders.contains("Sec-WebSocket-Origin") && httpheaders.contains("Sec-WebSocket-Location")) {
                return 16;
            }
        }

        return -1;
    }

    public static void setContentLength(HttpMessage httpmessage, long i) {
        httpmessage.headers().set("Content-Length", (Object) i);
    }

    public static String getHost(HttpMessage httpmessage) {
        return httpmessage.headers().get("Host");
    }

    public static String getHost(HttpMessage httpmessage, String s) {
        return getHeader(httpmessage, "Host", s);
    }

    public static void setHost(HttpMessage httpmessage, String s) {
        httpmessage.headers().set("Host", (Object) s);
    }

    public static Date getDate(HttpMessage httpmessage) throws ParseException {
        return getDateHeader(httpmessage, "Date");
    }

    public static Date getDate(HttpMessage httpmessage, Date date) {
        return getDateHeader(httpmessage, "Date", date);
    }

    public static void setDate(HttpMessage httpmessage, Date date) {
        if (date != null) {
            httpmessage.headers().set("Date", (Object) HttpHeaderDateFormat.get().format(date));
        } else {
            httpmessage.headers().set("Date", (Iterable) null);
        }

    }

    public static boolean is100ContinueExpected(HttpMessage httpmessage) {
        if (!(httpmessage instanceof HttpRequest)) {
            return false;
        } else if (httpmessage.getProtocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            return false;
        } else {
            String s = httpmessage.headers().get("Expect");

            if (s == null) {
                return false;
            } else if ("100-continue".equalsIgnoreCase(s)) {
                return true;
            } else {
                Iterator iterator = httpmessage.headers().getAll("Expect").iterator();

                String s1;

                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    s1 = (String) iterator.next();
                } while (!"100-continue".equalsIgnoreCase(s1));

                return true;
            }
        }
    }

    public static void set100ContinueExpected(HttpMessage httpmessage) {
        set100ContinueExpected(httpmessage, true);
    }

    public static void set100ContinueExpected(HttpMessage httpmessage, boolean flag) {
        if (flag) {
            httpmessage.headers().set("Expect", (Object) "100-continue");
        } else {
            httpmessage.headers().remove("Expect");
        }

    }

    static void validateHeaderName(String s) {
        if (s == null) {
            throw new NullPointerException("Header names cannot be null");
        } else {
            int i = 0;

            while (i < s.length()) {
                char c0 = s.charAt(i);

                if (c0 > 127) {
                    throw new IllegalArgumentException("Header name cannot contain non-ASCII characters: " + s);
                }

                switch (c0) {
                    case '\t':
                    case '\n':
                    case '\u000b':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case ';':
                    case '=':
                        throw new IllegalArgumentException("Header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + s);
                    default:
                        ++i;
                }
            }

        }
    }

    static void validateHeaderValue(String s) {
        if (s == null) {
            throw new NullPointerException("Header values cannot be null");
        } else {
            byte b0 = 0;

            for (int i = 0; i < s.length(); ++i) {
                char c0 = s.charAt(i);

                switch (c0) {
                    case '\u000b':
                        throw new IllegalArgumentException("Header value contains a prohibited character '\\v': " + s);
                    case '\f':
                        throw new IllegalArgumentException("Header value contains a prohibited character '\\f': " + s);
                }

                switch (b0) {
                    case 0:
                        switch (c0) {
                            case '\n':
                                b0 = 2;
                                continue;
                            case '\r':
                                b0 = 1;
                            default:
                                continue;
                        }
                    case 1:
                        switch (c0) {
                            case '\n':
                                b0 = 2;
                                continue;
                            default:
                                throw new IllegalArgumentException("Only '\\n' is allowed after '\\r': " + s);
                        }
                    case 2:
                        switch (c0) {
                            case '\t':
                            case ' ':
                                b0 = 0;
                                break;
                            default:
                                throw new IllegalArgumentException("Only ' ' and '\\t' are allowed after '\\n': " + s);
                        }
                }
            }

            if (b0 != 0) {
                throw new IllegalArgumentException("Header value must not end with '\\r' or '\\n':" + s);
            }
        }
    }

    public static boolean isTransferEncodingChunked(HttpMessage httpmessage) {
        List list = httpmessage.headers().getAll("Transfer-Encoding");

        if (list.isEmpty()) {
            return false;
        } else {
            Iterator iterator = list.iterator();

            String s;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                s = (String) iterator.next();
            } while (!s.equalsIgnoreCase("chunked"));

            return true;
        }
    }

    public static void removeTransferEncodingChunked(HttpMessage httpmessage) {
        List list = httpmessage.headers().getAll("Transfer-Encoding");

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                if (s.equalsIgnoreCase("chunked")) {
                    iterator.remove();
                }
            }

            if (list.isEmpty()) {
                httpmessage.headers().remove("Transfer-Encoding");
            } else {
                httpmessage.headers().set("Transfer-Encoding", (Iterable) list);
            }

        }
    }

    public static void setTransferEncodingChunked(HttpMessage httpmessage) {
        addHeader(httpmessage, "Transfer-Encoding", "chunked");
        removeHeader(httpmessage, "Content-Length");
    }

    public static boolean isContentLengthSet(HttpMessage httpmessage) {
        List list = httpmessage.headers().getAll("Content-Length");

        return !list.isEmpty();
    }

    protected HttpHeaders() {}

    public abstract String get(String s);

    public abstract List<String> getAll(String s);

    public abstract List<Entry<String, String>> entries();

    public abstract boolean contains(String s);

    public abstract boolean isEmpty();

    public abstract Set<String> names();

    public abstract HttpHeaders add(String s, Object object);

    public abstract HttpHeaders add(String s, Iterable<?> iterable);

    public HttpHeaders add(HttpHeaders httpheaders) {
        if (httpheaders == null) {
            throw new NullPointerException("headers");
        } else {
            Iterator iterator = httpheaders.iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.add((String) entry.getKey(), entry.getValue());
            }

            return this;
        }
    }

    public abstract HttpHeaders set(String s, Object object);

    public abstract HttpHeaders set(String s, Iterable<?> iterable);

    public HttpHeaders set(HttpHeaders httpheaders) {
        if (httpheaders == null) {
            throw new NullPointerException("headers");
        } else {
            this.clear();
            Iterator iterator = httpheaders.iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                this.add((String) entry.getKey(), entry.getValue());
            }

            return this;
        }
    }

    public abstract HttpHeaders remove(String s);

    public abstract HttpHeaders clear();

    public static final class Values {

        public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String BASE64 = "base64";
        public static final String BINARY = "binary";
        public static final String BOUNDARY = "boundary";
        public static final String BYTES = "bytes";
        public static final String CHARSET = "charset";
        public static final String CHUNKED = "chunked";
        public static final String CLOSE = "close";
        public static final String COMPRESS = "compress";
        public static final String CONTINUE = "100-continue";
        public static final String DEFLATE = "deflate";
        public static final String GZIP = "gzip";
        public static final String IDENTITY = "identity";
        public static final String KEEP_ALIVE = "keep-alive";
        public static final String MAX_AGE = "max-age";
        public static final String MAX_STALE = "max-stale";
        public static final String MIN_FRESH = "min-fresh";
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
        public static final String MUST_REVALIDATE = "must-revalidate";
        public static final String NO_CACHE = "no-cache";
        public static final String NO_STORE = "no-store";
        public static final String NO_TRANSFORM = "no-transform";
        public static final String NONE = "none";
        public static final String ONLY_IF_CACHED = "only-if-cached";
        public static final String PRIVATE = "private";
        public static final String PROXY_REVALIDATE = "proxy-revalidate";
        public static final String PUBLIC = "public";
        public static final String QUOTED_PRINTABLE = "quoted-printable";
        public static final String S_MAXAGE = "s-maxage";
        public static final String TRAILERS = "trailers";
        public static final String UPGRADE = "Upgrade";
        public static final String WEBSOCKET = "WebSocket";

        private Values() {}
    }

    public static final class Names {

        public static final String ACCEPT = "Accept";
        public static final String ACCEPT_CHARSET = "Accept-Charset";
        public static final String ACCEPT_ENCODING = "Accept-Encoding";
        public static final String ACCEPT_LANGUAGE = "Accept-Language";
        public static final String ACCEPT_RANGES = "Accept-Ranges";
        public static final String ACCEPT_PATCH = "Accept-Patch";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
        public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
        public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
        public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
        public static final String AGE = "Age";
        public static final String ALLOW = "Allow";
        public static final String AUTHORIZATION = "Authorization";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String CONNECTION = "Connection";
        public static final String CONTENT_BASE = "Content-Base";
        public static final String CONTENT_ENCODING = "Content-Encoding";
        public static final String CONTENT_LANGUAGE = "Content-Language";
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_LOCATION = "Content-Location";
        public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
        public static final String CONTENT_MD5 = "Content-MD5";
        public static final String CONTENT_RANGE = "Content-Range";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String COOKIE = "Cookie";
        public static final String DATE = "Date";
        public static final String ETAG = "ETag";
        public static final String EXPECT = "Expect";
        public static final String EXPIRES = "Expires";
        public static final String FROM = "From";
        public static final String HOST = "Host";
        public static final String IF_MATCH = "If-Match";
        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
        public static final String IF_NONE_MATCH = "If-None-Match";
        public static final String IF_RANGE = "If-Range";
        public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
        public static final String LAST_MODIFIED = "Last-Modified";
        public static final String LOCATION = "Location";
        public static final String MAX_FORWARDS = "Max-Forwards";
        public static final String ORIGIN = "Origin";
        public static final String PRAGMA = "Pragma";
        public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
        public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
        public static final String RANGE = "Range";
        public static final String REFERER = "Referer";
        public static final String RETRY_AFTER = "Retry-After";
        public static final String SEC_WEBSOCKET_KEY1 = "Sec-WebSocket-Key1";
        public static final String SEC_WEBSOCKET_KEY2 = "Sec-WebSocket-Key2";
        public static final String SEC_WEBSOCKET_LOCATION = "Sec-WebSocket-Location";
        public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
        public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
        public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
        public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
        public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
        public static final String SERVER = "Server";
        public static final String SET_COOKIE = "Set-Cookie";
        public static final String SET_COOKIE2 = "Set-Cookie2";
        public static final String TE = "TE";
        public static final String TRAILER = "Trailer";
        public static final String TRANSFER_ENCODING = "Transfer-Encoding";
        public static final String UPGRADE = "Upgrade";
        public static final String USER_AGENT = "User-Agent";
        public static final String VARY = "Vary";
        public static final String VIA = "Via";
        public static final String WARNING = "Warning";
        public static final String WEBSOCKET_LOCATION = "WebSocket-Location";
        public static final String WEBSOCKET_ORIGIN = "WebSocket-Origin";
        public static final String WEBSOCKET_PROTOCOL = "WebSocket-Protocol";
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

        private Names() {}
    }
}
