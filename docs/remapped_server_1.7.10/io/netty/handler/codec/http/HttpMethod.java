package io.netty.handler.codec.http;

import java.util.HashMap;
import java.util.Map;

public class HttpMethod implements Comparable<HttpMethod> {

    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    public static final HttpMethod GET = new HttpMethod("GET");
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    public static final HttpMethod POST = new HttpMethod("POST");
    public static final HttpMethod PUT = new HttpMethod("PUT");
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    public static final HttpMethod TRACE = new HttpMethod("TRACE");
    public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
    private static final Map<String, HttpMethod> methodMap = new HashMap();
    private final String name;

    public static HttpMethod valueOf(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            s = s.trim();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty name");
            } else {
                HttpMethod httpmethod = (HttpMethod) HttpMethod.methodMap.get(s);

                return httpmethod != null ? httpmethod : new HttpMethod(s);
            }
        }
    }

    public HttpMethod(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            s = s.trim();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty name");
            } else {
                for (int i = 0; i < s.length(); ++i) {
                    if (Character.isISOControl(s.charAt(i)) || Character.isWhitespace(s.charAt(i))) {
                        throw new IllegalArgumentException("invalid character in name");
                    }
                }

                this.name = s;
            }
        }
    }

    public String name() {
        return this.name;
    }

    public int hashCode() {
        return this.name().hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof HttpMethod)) {
            return false;
        } else {
            HttpMethod httpmethod = (HttpMethod) object;

            return this.name().equals(httpmethod.name());
        }
    }

    public String toString() {
        return this.name();
    }

    public int compareTo(HttpMethod httpmethod) {
        return this.name().compareTo(httpmethod.name());
    }

    static {
        HttpMethod.methodMap.put(HttpMethod.OPTIONS.toString(), HttpMethod.OPTIONS);
        HttpMethod.methodMap.put(HttpMethod.GET.toString(), HttpMethod.GET);
        HttpMethod.methodMap.put(HttpMethod.HEAD.toString(), HttpMethod.HEAD);
        HttpMethod.methodMap.put(HttpMethod.POST.toString(), HttpMethod.POST);
        HttpMethod.methodMap.put(HttpMethod.PUT.toString(), HttpMethod.PUT);
        HttpMethod.methodMap.put(HttpMethod.PATCH.toString(), HttpMethod.PATCH);
        HttpMethod.methodMap.put(HttpMethod.DELETE.toString(), HttpMethod.DELETE);
        HttpMethod.methodMap.put(HttpMethod.TRACE.toString(), HttpMethod.TRACE);
        HttpMethod.methodMap.put(HttpMethod.CONNECT.toString(), HttpMethod.CONNECT);
    }
}
