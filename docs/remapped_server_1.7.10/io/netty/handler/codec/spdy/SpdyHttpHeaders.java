package io.netty.handler.codec.spdy;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

public final class SpdyHttpHeaders {

    private SpdyHttpHeaders() {}

    public static void removeStreamId(HttpMessage httpmessage) {
        httpmessage.headers().remove("X-SPDY-Stream-ID");
    }

    public static int getStreamId(HttpMessage httpmessage) {
        return HttpHeaders.getIntHeader(httpmessage, "X-SPDY-Stream-ID");
    }

    public static void setStreamId(HttpMessage httpmessage, int i) {
        HttpHeaders.setIntHeader(httpmessage, "X-SPDY-Stream-ID", i);
    }

    public static void removeAssociatedToStreamId(HttpMessage httpmessage) {
        httpmessage.headers().remove("X-SPDY-Associated-To-Stream-ID");
    }

    public static int getAssociatedToStreamId(HttpMessage httpmessage) {
        return HttpHeaders.getIntHeader(httpmessage, "X-SPDY-Associated-To-Stream-ID", 0);
    }

    public static void setAssociatedToStreamId(HttpMessage httpmessage, int i) {
        HttpHeaders.setIntHeader(httpmessage, "X-SPDY-Associated-To-Stream-ID", i);
    }

    public static void removePriority(HttpMessage httpmessage) {
        httpmessage.headers().remove("X-SPDY-Priority");
    }

    public static byte getPriority(HttpMessage httpmessage) {
        return (byte) HttpHeaders.getIntHeader(httpmessage, "X-SPDY-Priority", 0);
    }

    public static void setPriority(HttpMessage httpmessage, byte b0) {
        HttpHeaders.setIntHeader(httpmessage, "X-SPDY-Priority", b0);
    }

    public static void removeUrl(HttpMessage httpmessage) {
        httpmessage.headers().remove("X-SPDY-URL");
    }

    public static String getUrl(HttpMessage httpmessage) {
        return httpmessage.headers().get("X-SPDY-URL");
    }

    public static void setUrl(HttpMessage httpmessage, String s) {
        httpmessage.headers().set("X-SPDY-URL", (Object) s);
    }

    public static void removeScheme(HttpMessage httpmessage) {
        httpmessage.headers().remove("X-SPDY-Scheme");
    }

    public static String getScheme(HttpMessage httpmessage) {
        return httpmessage.headers().get("X-SPDY-Scheme");
    }

    public static void setScheme(HttpMessage httpmessage, String s) {
        httpmessage.headers().set("X-SPDY-Scheme", (Object) s);
    }

    public static final class Names {

        public static final String STREAM_ID = "X-SPDY-Stream-ID";
        public static final String ASSOCIATED_TO_STREAM_ID = "X-SPDY-Associated-To-Stream-ID";
        public static final String PRIORITY = "X-SPDY-Priority";
        public static final String URL = "X-SPDY-URL";
        public static final String SCHEME = "X-SPDY-Scheme";

        private Names() {}
    }
}
