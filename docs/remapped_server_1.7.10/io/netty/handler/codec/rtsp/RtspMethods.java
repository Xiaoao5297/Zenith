package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpMethod;
import java.util.HashMap;
import java.util.Map;

public final class RtspMethods {

    public static final HttpMethod OPTIONS = HttpMethod.OPTIONS;
    public static final HttpMethod DESCRIBE = new HttpMethod("DESCRIBE");
    public static final HttpMethod ANNOUNCE = new HttpMethod("ANNOUNCE");
    public static final HttpMethod SETUP = new HttpMethod("SETUP");
    public static final HttpMethod PLAY = new HttpMethod("PLAY");
    public static final HttpMethod PAUSE = new HttpMethod("PAUSE");
    public static final HttpMethod TEARDOWN = new HttpMethod("TEARDOWN");
    public static final HttpMethod GET_PARAMETER = new HttpMethod("GET_PARAMETER");
    public static final HttpMethod SET_PARAMETER = new HttpMethod("SET_PARAMETER");
    public static final HttpMethod REDIRECT = new HttpMethod("REDIRECT");
    public static final HttpMethod RECORD = new HttpMethod("RECORD");
    private static final Map<String, HttpMethod> methodMap = new HashMap();

    public static HttpMethod valueOf(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            s = s.trim().toUpperCase();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty name");
            } else {
                HttpMethod httpmethod = (HttpMethod) RtspMethods.methodMap.get(s);

                return httpmethod != null ? httpmethod : new HttpMethod(s);
            }
        }
    }

    private RtspMethods() {}

    static {
        RtspMethods.methodMap.put(RtspMethods.DESCRIBE.toString(), RtspMethods.DESCRIBE);
        RtspMethods.methodMap.put(RtspMethods.ANNOUNCE.toString(), RtspMethods.ANNOUNCE);
        RtspMethods.methodMap.put(RtspMethods.GET_PARAMETER.toString(), RtspMethods.GET_PARAMETER);
        RtspMethods.methodMap.put(RtspMethods.OPTIONS.toString(), RtspMethods.OPTIONS);
        RtspMethods.methodMap.put(RtspMethods.PAUSE.toString(), RtspMethods.PAUSE);
        RtspMethods.methodMap.put(RtspMethods.PLAY.toString(), RtspMethods.PLAY);
        RtspMethods.methodMap.put(RtspMethods.RECORD.toString(), RtspMethods.RECORD);
        RtspMethods.methodMap.put(RtspMethods.REDIRECT.toString(), RtspMethods.REDIRECT);
        RtspMethods.methodMap.put(RtspMethods.SETUP.toString(), RtspMethods.SETUP);
        RtspMethods.methodMap.put(RtspMethods.SET_PARAMETER.toString(), RtspMethods.SET_PARAMETER);
        RtspMethods.methodMap.put(RtspMethods.TEARDOWN.toString(), RtspMethods.TEARDOWN);
    }
}
