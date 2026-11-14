package io.netty.handler.codec.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpVersion implements Comparable<HttpVersion> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\S+)/(\\d+)\\.(\\d+)");
    private static final String HTTP_1_0_STRING = "HTTP/1.0";
    private static final String HTTP_1_1_STRING = "HTTP/1.1";
    public static final HttpVersion HTTP_1_0 = new HttpVersion("HTTP", 1, 0, false);
    public static final HttpVersion HTTP_1_1 = new HttpVersion("HTTP", 1, 1, true);
    private final String protocolName;
    private final int majorVersion;
    private final int minorVersion;
    private final String text;
    private final boolean keepAliveDefault;

    public static HttpVersion valueOf(String s) {
        if (s == null) {
            throw new NullPointerException("text");
        } else {
            s = s.trim();
            HttpVersion httpversion = version0(s);

            if (httpversion == null) {
                s = s.toUpperCase();
                httpversion = version0(s);
                if (httpversion == null) {
                    httpversion = new HttpVersion(s, true);
                }
            }

            return httpversion;
        }
    }

    private static HttpVersion version0(String s) {
        return "HTTP/1.1".equals(s) ? HttpVersion.HTTP_1_1 : ("HTTP/1.0".equals(s) ? HttpVersion.HTTP_1_0 : null);
    }

    public HttpVersion(String s, boolean flag) {
        if (s == null) {
            throw new NullPointerException("text");
        } else {
            s = s.trim().toUpperCase();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty text");
            } else {
                Matcher matcher = HttpVersion.VERSION_PATTERN.matcher(s);

                if (!matcher.matches()) {
                    throw new IllegalArgumentException("invalid version format: " + s);
                } else {
                    this.protocolName = matcher.group(1);
                    this.majorVersion = Integer.parseInt(matcher.group(2));
                    this.minorVersion = Integer.parseInt(matcher.group(3));
                    this.text = this.protocolName + '/' + this.majorVersion + '.' + this.minorVersion;
                    this.keepAliveDefault = flag;
                }
            }
        }
    }

    public HttpVersion(String s, int i, int j, boolean flag) {
        if (s == null) {
            throw new NullPointerException("protocolName");
        } else {
            s = s.trim().toUpperCase();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty protocolName");
            } else {
                for (int k = 0; k < s.length(); ++k) {
                    if (Character.isISOControl(s.charAt(k)) || Character.isWhitespace(s.charAt(k))) {
                        throw new IllegalArgumentException("invalid character in protocolName");
                    }
                }

                if (i < 0) {
                    throw new IllegalArgumentException("negative majorVersion");
                } else if (j < 0) {
                    throw new IllegalArgumentException("negative minorVersion");
                } else {
                    this.protocolName = s;
                    this.majorVersion = i;
                    this.minorVersion = j;
                    this.text = s + '/' + i + '.' + j;
                    this.keepAliveDefault = flag;
                }
            }
        }
    }

    public String protocolName() {
        return this.protocolName;
    }

    public int majorVersion() {
        return this.majorVersion;
    }

    public int minorVersion() {
        return this.minorVersion;
    }

    public String text() {
        return this.text;
    }

    public boolean isKeepAliveDefault() {
        return this.keepAliveDefault;
    }

    public String toString() {
        return this.text();
    }

    public int hashCode() {
        return (this.protocolName().hashCode() * 31 + this.majorVersion()) * 31 + this.minorVersion();
    }

    public boolean equals(Object object) {
        if (!(object instanceof HttpVersion)) {
            return false;
        } else {
            HttpVersion httpversion = (HttpVersion) object;

            return this.minorVersion() == httpversion.minorVersion() && this.majorVersion() == httpversion.majorVersion() && this.protocolName().equals(httpversion.protocolName());
        }
    }

    public int compareTo(HttpVersion httpversion) {
        int i = this.protocolName().compareTo(httpversion.protocolName());

        if (i != 0) {
            return i;
        } else {
            i = this.majorVersion() - httpversion.majorVersion();
            return i != 0 ? i : this.minorVersion() - httpversion.minorVersion();
        }
    }
}
