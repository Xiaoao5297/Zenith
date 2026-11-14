package io.netty.handler.codec.spdy;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class SpdyHeaders implements Iterable<Entry<String, String>> {

    public static final SpdyHeaders EMPTY_HEADERS = new SpdyHeaders() {
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
        public SpdyHeaders add(String s, Object object) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders add(String s, Iterable<?> iterable) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders set(String s, Object object) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders set(String s, Iterable<?> iterable) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders remove(String s) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SpdyHeaders clear() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            return this.entries().iterator();
        }

        @Override
        public String get(String s) {
            return null;
        }
    };

    public SpdyHeaders() {}

    public static String getHeader(SpdyHeadersFrame spdyheadersframe, String s) {
        return spdyheadersframe.headers().get(s);
    }

    public static String getHeader(SpdyHeadersFrame spdyheadersframe, String s, String s1) {
        String s2 = spdyheadersframe.headers().get(s);

        return s2 == null ? s1 : s2;
    }

    public static void setHeader(SpdyHeadersFrame spdyheadersframe, String s, Object object) {
        spdyheadersframe.headers().set(s, object);
    }

    public static void setHeader(SpdyHeadersFrame spdyheadersframe, String s, Iterable<?> iterable) {
        spdyheadersframe.headers().set(s, iterable);
    }

    public static void addHeader(SpdyHeadersFrame spdyheadersframe, String s, Object object) {
        spdyheadersframe.headers().add(s, object);
    }

    public static void removeHost(SpdyHeadersFrame spdyheadersframe) {
        spdyheadersframe.headers().remove(":host");
    }

    public static String getHost(SpdyHeadersFrame spdyheadersframe) {
        return spdyheadersframe.headers().get(":host");
    }

    public static void setHost(SpdyHeadersFrame spdyheadersframe, String s) {
        spdyheadersframe.headers().set(":host", (Object) s);
    }

    public static void removeMethod(int i, SpdyHeadersFrame spdyheadersframe) {
        if (i < 3) {
            spdyheadersframe.headers().remove("method");
        } else {
            spdyheadersframe.headers().remove(":method");
        }

    }

    public static HttpMethod getMethod(int i, SpdyHeadersFrame spdyheadersframe) {
        try {
            return i < 3 ? HttpMethod.valueOf(spdyheadersframe.headers().get("method")) : HttpMethod.valueOf(spdyheadersframe.headers().get(":method"));
        } catch (Exception exception) {
            return null;
        }
    }

    public static void setMethod(int i, SpdyHeadersFrame spdyheadersframe, HttpMethod httpmethod) {
        if (i < 3) {
            spdyheadersframe.headers().set("method", (Object) httpmethod.name());
        } else {
            spdyheadersframe.headers().set(":method", (Object) httpmethod.name());
        }

    }

    public static void removeScheme(int i, SpdyHeadersFrame spdyheadersframe) {
        if (i < 2) {
            spdyheadersframe.headers().remove("scheme");
        } else {
            spdyheadersframe.headers().remove(":scheme");
        }

    }

    public static String getScheme(int i, SpdyHeadersFrame spdyheadersframe) {
        return i < 3 ? spdyheadersframe.headers().get("scheme") : spdyheadersframe.headers().get(":scheme");
    }

    public static void setScheme(int i, SpdyHeadersFrame spdyheadersframe, String s) {
        if (i < 3) {
            spdyheadersframe.headers().set("scheme", (Object) s);
        } else {
            spdyheadersframe.headers().set(":scheme", (Object) s);
        }

    }

    public static void removeStatus(int i, SpdyHeadersFrame spdyheadersframe) {
        if (i < 3) {
            spdyheadersframe.headers().remove("status");
        } else {
            spdyheadersframe.headers().remove(":status");
        }

    }

    public static HttpResponseStatus getStatus(int i, SpdyHeadersFrame spdyheadersframe) {
        try {
            String s;

            if (i < 3) {
                s = spdyheadersframe.headers().get("status");
            } else {
                s = spdyheadersframe.headers().get(":status");
            }

            int j = s.indexOf(32);

            if (j == -1) {
                return HttpResponseStatus.valueOf(Integer.parseInt(s));
            } else {
                int k = Integer.parseInt(s.substring(0, j));
                String s1 = s.substring(j + 1);
                HttpResponseStatus httpresponsestatus = HttpResponseStatus.valueOf(k);

                return httpresponsestatus.reasonPhrase().equals(s1) ? httpresponsestatus : new HttpResponseStatus(k, s1);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public static void setStatus(int i, SpdyHeadersFrame spdyheadersframe, HttpResponseStatus httpresponsestatus) {
        if (i < 3) {
            spdyheadersframe.headers().set("status", (Object) httpresponsestatus.toString());
        } else {
            spdyheadersframe.headers().set(":status", (Object) httpresponsestatus.toString());
        }

    }

    public static void removeUrl(int i, SpdyHeadersFrame spdyheadersframe) {
        if (i < 3) {
            spdyheadersframe.headers().remove("url");
        } else {
            spdyheadersframe.headers().remove(":path");
        }

    }

    public static String getUrl(int i, SpdyHeadersFrame spdyheadersframe) {
        return i < 3 ? spdyheadersframe.headers().get("url") : spdyheadersframe.headers().get(":path");
    }

    public static void setUrl(int i, SpdyHeadersFrame spdyheadersframe, String s) {
        if (i < 3) {
            spdyheadersframe.headers().set("url", (Object) s);
        } else {
            spdyheadersframe.headers().set(":path", (Object) s);
        }

    }

    public static void removeVersion(int i, SpdyHeadersFrame spdyheadersframe) {
        if (i < 3) {
            spdyheadersframe.headers().remove("version");
        } else {
            spdyheadersframe.headers().remove(":version");
        }

    }

    public static HttpVersion getVersion(int i, SpdyHeadersFrame spdyheadersframe) {
        try {
            return i < 3 ? HttpVersion.valueOf(spdyheadersframe.headers().get("version")) : HttpVersion.valueOf(spdyheadersframe.headers().get(":version"));
        } catch (Exception exception) {
            return null;
        }
    }

    public static void setVersion(int i, SpdyHeadersFrame spdyheadersframe, HttpVersion httpversion) {
        if (i < 3) {
            spdyheadersframe.headers().set("version", (Object) httpversion.text());
        } else {
            spdyheadersframe.headers().set(":version", (Object) httpversion.text());
        }

    }

    public Iterator<Entry<String, String>> iterator() {
        return this.entries().iterator();
    }

    public abstract String get(String s);

    public abstract List<String> getAll(String s);

    public abstract List<Entry<String, String>> entries();

    public abstract boolean contains(String s);

    public abstract Set<String> names();

    public abstract SpdyHeaders add(String s, Object object);

    public abstract SpdyHeaders add(String s, Iterable<?> iterable);

    public abstract SpdyHeaders set(String s, Object object);

    public abstract SpdyHeaders set(String s, Iterable<?> iterable);

    public abstract SpdyHeaders remove(String s);

    public abstract SpdyHeaders clear();

    public abstract boolean isEmpty();

    public static final class Spdy2HttpNames {

        public static final String METHOD = "method";
        public static final String SCHEME = "scheme";
        public static final String STATUS = "status";
        public static final String URL = "url";
        public static final String VERSION = "version";

        private Spdy2HttpNames() {}
    }

    public static final class HttpNames {

        public static final String HOST = ":host";
        public static final String METHOD = ":method";
        public static final String PATH = ":path";
        public static final String SCHEME = ":scheme";
        public static final String STATUS = ":status";
        public static final String VERSION = ":version";

        private HttpNames() {}
    }
}
