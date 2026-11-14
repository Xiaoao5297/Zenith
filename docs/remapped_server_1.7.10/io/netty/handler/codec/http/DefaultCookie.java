package io.netty.handler.codec.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DefaultCookie implements Cookie {

    private final String name;
    private String value;
    private String domain;
    private String path;
    private String comment;
    private String commentUrl;
    private boolean discard;
    private Set<Integer> ports = Collections.emptySet();
    private Set<Integer> unmodifiablePorts;
    private long maxAge;
    private int version;
    private boolean secure;
    private boolean httpOnly;

    public DefaultCookie(String s, String s1) {
        this.unmodifiablePorts = this.ports;
        this.maxAge = Long.MIN_VALUE;
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            s = s.trim();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty name");
            } else {
                int i = 0;

                while (i < s.length()) {
                    char c0 = s.charAt(i);

                    if (c0 > 127) {
                        throw new IllegalArgumentException("name contains non-ascii character: " + s);
                    }

                    switch (c0) {
                        case '\t':
                        case '\n':
                        case '\u000b':
                        case '\f':
                        case '\r':
                        case ' ':
                        case ',':
                        case ';':
                        case '=':
                            throw new IllegalArgumentException("name contains one of the following prohibited characters: =,; \\t\\r\\n\\v\\f: " + s);
                        default:
                            ++i;
                    }
                }

                if (s.charAt(0) == '$') {
                    throw new IllegalArgumentException("name starting with '$' not allowed: " + s);
                } else {
                    this.name = s;
                    this.setValue(s1);
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String s) {
        if (s == null) {
            throw new NullPointerException("value");
        } else {
            this.value = s;
        }
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setDomain(String s) {
        this.domain = validateValue("domain", s);
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String s) {
        this.path = validateValue("path", s);
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String s) {
        this.comment = validateValue("comment", s);
    }

    @Override
    public String getCommentUrl() {
        return this.commentUrl;
    }

    @Override
    public void setCommentUrl(String s) {
        this.commentUrl = validateValue("commentUrl", s);
    }

    @Override
    public boolean isDiscard() {
        return this.discard;
    }

    @Override
    public void setDiscard(boolean flag) {
        this.discard = flag;
    }

    @Override
    public Set<Integer> getPorts() {
        if (this.unmodifiablePorts == null) {
            this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
        }

        return this.unmodifiablePorts;
    }

    @Override
    public void setPorts(int... aint) {
        if (aint == null) {
            throw new NullPointerException("ports");
        } else {
            int[] aint1 = (int[]) aint.clone();

            if (aint1.length == 0) {
                this.unmodifiablePorts = this.ports = Collections.emptySet();
            } else {
                TreeSet treeset = new TreeSet();
                int[] aint2 = aint1;
                int i = aint1.length;
                int j = 0;

                while (true) {
                    if (j < i) {
                        int k = aint2[j];

                        if (k > 0 && k <= 65535) {
                            treeset.add(k);
                            ++j;
                            continue;
                        }

                        throw new IllegalArgumentException("port out of range: " + k);
                    }

                    this.ports = treeset;
                    this.unmodifiablePorts = null;
                    break;
                }
            }

        }
    }

    @Override
    public void setPorts(Iterable<Integer> iterable) {
        TreeSet treeset = new TreeSet();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            int i = (Integer) iterator.next();

            if (i <= 0 || i > 65535) {
                throw new IllegalArgumentException("port out of range: " + i);
            }

            treeset.add(i);
        }

        if (treeset.isEmpty()) {
            this.unmodifiablePorts = this.ports = Collections.emptySet();
        } else {
            this.ports = treeset;
            this.unmodifiablePorts = null;
        }

    }

    @Override
    public long getMaxAge() {
        return this.maxAge;
    }

    @Override
    public void setMaxAge(long i) {
        this.maxAge = i;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(int i) {
        this.version = i;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean flag) {
        this.secure = flag;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setHttpOnly(boolean flag) {
        this.httpOnly = flag;
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Cookie)) {
            return false;
        } else {
            Cookie cookie = (Cookie) object;

            if (!this.getName().equalsIgnoreCase(cookie.getName())) {
                return false;
            } else {
                if (this.getPath() == null) {
                    if (cookie.getPath() != null) {
                        return false;
                    }
                } else {
                    if (cookie.getPath() == null) {
                        return false;
                    }

                    if (!this.getPath().equals(cookie.getPath())) {
                        return false;
                    }
                }

                return this.getDomain() == null ? cookie.getDomain() == null : (cookie.getDomain() == null ? false : this.getDomain().equalsIgnoreCase(cookie.getDomain()));
            }
        }
    }

    public int compareTo(Cookie cookie) {
        int i = this.getName().compareToIgnoreCase(cookie.getName());

        if (i != 0) {
            return i;
        } else {
            if (this.getPath() == null) {
                if (cookie.getPath() != null) {
                    return -1;
                }
            } else {
                if (cookie.getPath() == null) {
                    return 1;
                }

                i = this.getPath().compareTo(cookie.getPath());
                if (i != 0) {
                    return i;
                }
            }

            if (this.getDomain() == null) {
                return cookie.getDomain() != null ? -1 : 0;
            } else if (cookie.getDomain() == null) {
                return 1;
            } else {
                i = this.getDomain().compareToIgnoreCase(cookie.getDomain());
                return i;
            }
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getName());
        stringbuilder.append('=');
        stringbuilder.append(this.getValue());
        if (this.getDomain() != null) {
            stringbuilder.append(", domain=");
            stringbuilder.append(this.getDomain());
        }

        if (this.getPath() != null) {
            stringbuilder.append(", path=");
            stringbuilder.append(this.getPath());
        }

        if (this.getComment() != null) {
            stringbuilder.append(", comment=");
            stringbuilder.append(this.getComment());
        }

        if (this.getMaxAge() >= 0L) {
            stringbuilder.append(", maxAge=");
            stringbuilder.append(this.getMaxAge());
            stringbuilder.append('s');
        }

        if (this.isSecure()) {
            stringbuilder.append(", secure");
        }

        if (this.isHttpOnly()) {
            stringbuilder.append(", HTTPOnly");
        }

        return stringbuilder.toString();
    }

    private static String validateValue(String s, String s1) {
        if (s1 == null) {
            return null;
        } else {
            s1 = s1.trim();
            if (s1.isEmpty()) {
                return null;
            } else {
                int i = 0;

                while (i < s1.length()) {
                    char c0 = s1.charAt(i);

                    switch (c0) {
                        case '\n':
                        case '\u000b':
                        case '\f':
                        case '\r':
                        case ';':
                            throw new IllegalArgumentException(s + " contains one of the following prohibited characters: " + ";\\r\\n\\f\\v (" + s1 + ')');
                        default:
                            ++i;
                    }
                }

                return s1;
            }
        }
    }
}
