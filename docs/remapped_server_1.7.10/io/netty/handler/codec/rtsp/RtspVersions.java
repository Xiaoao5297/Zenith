package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpVersion;

public final class RtspVersions {

    public static final HttpVersion RTSP_1_0 = new HttpVersion("RTSP", 1, 0, true);

    public static HttpVersion valueOf(String s) {
        if (s == null) {
            throw new NullPointerException("text");
        } else {
            s = s.trim().toUpperCase();
            return "RTSP/1.0".equals(s) ? RtspVersions.RTSP_1_0 : new HttpVersion(s, true);
        }
    }

    private RtspVersions() {}
}
