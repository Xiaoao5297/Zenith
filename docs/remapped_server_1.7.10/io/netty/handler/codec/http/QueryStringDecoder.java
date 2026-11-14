package io.netty.handler.codec.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {

    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final boolean hasPath;
    private final int maxParams;
    private String path;
    private Map<String, List<String>> params;
    private int nParams;

    public QueryStringDecoder(String s) {
        this(s, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(String s, boolean flag) {
        this(s, HttpConstants.DEFAULT_CHARSET, flag);
    }

    public QueryStringDecoder(String s, Charset charset) {
        this(s, charset, true);
    }

    public QueryStringDecoder(String s, Charset charset, boolean flag) {
        this(s, charset, flag, 1024);
    }

    public QueryStringDecoder(String s, Charset charset, boolean flag, int i) {
        if (s == null) {
            throw new NullPointerException("getUri");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (i <= 0) {
            throw new IllegalArgumentException("maxParams: " + i + " (expected: a positive integer)");
        } else {
            this.uri = s;
            this.charset = charset;
            this.maxParams = i;
            this.hasPath = flag;
        }
    }

    public QueryStringDecoder(URI uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, 1024);
    }

    public QueryStringDecoder(URI uri, Charset charset, int i) {
        if (uri == null) {
            throw new NullPointerException("getUri");
        } else if (charset == null) {
            throw new NullPointerException("charset");
        } else if (i <= 0) {
            throw new IllegalArgumentException("maxParams: " + i + " (expected: a positive integer)");
        } else {
            String s = uri.getRawPath();

            if (s != null) {
                this.hasPath = true;
            } else {
                s = "";
                this.hasPath = false;
            }

            this.uri = s + '?' + uri.getRawQuery();
            this.charset = charset;
            this.maxParams = i;
        }
    }

    public String path() {
        if (this.path == null) {
            if (!this.hasPath) {
                return this.path = "";
            }

            int i = this.uri.indexOf(63);

            if (i >= 0) {
                return this.path = this.uri.substring(0, i);
            }

            this.path = this.uri;
        }

        return this.path;
    }

    public Map<String, List<String>> parameters() {
        if (this.params == null) {
            if (this.hasPath) {
                int i = this.path().length();

                if (this.uri.length() == i) {
                    return Collections.emptyMap();
                }

                this.decodeParams(this.uri.substring(i + 1));
            } else {
                if (this.uri.isEmpty()) {
                    return Collections.emptyMap();
                }

                this.decodeParams(this.uri);
            }
        }

        return this.params;
    }

    private void decodeParams(String s) {
        Map map = this.params = new LinkedHashMap();

        this.nParams = 0;
        String s1 = null;
        int i = 0;

        int j;

        for (j = 0; j < s.length(); ++j) {
            char c0 = s.charAt(j);

            if (c0 == '=' && s1 == null) {
                if (i != j) {
                    s1 = decodeComponent(s.substring(i, j), this.charset);
                }

                i = j + 1;
            } else if (c0 == '&' || c0 == ';') {
                if (s1 == null && i != j) {
                    if (!this.addParam(map, decodeComponent(s.substring(i, j), this.charset), "")) {
                        return;
                    }
                } else if (s1 != null) {
                    if (!this.addParam(map, s1, decodeComponent(s.substring(i, j), this.charset))) {
                        return;
                    }

                    s1 = null;
                }

                i = j + 1;
            }
        }

        if (i != j) {
            if (s1 == null) {
                this.addParam(map, decodeComponent(s.substring(i, j), this.charset), "");
            } else {
                this.addParam(map, s1, decodeComponent(s.substring(i, j), this.charset));
            }
        } else if (s1 != null) {
            this.addParam(map, s1, "");
        }

    }

    private boolean addParam(Map<String, List<String>> map, String s, String s1) {
        if (this.nParams >= this.maxParams) {
            return false;
        } else {
            Object object = (List) map.get(s);

            if (object == null) {
                object = new ArrayList(1);
                map.put(s, object);
            }

            ((List) object).add(s1);
            ++this.nParams;
            return true;
        }
    }

    public static String decodeComponent(String s) {
        return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s == null) {
            return "";
        } else {
            int i = s.length();
            boolean flag = false;
            int j = 0;

            int k;

            while (j < i) {
                k = s.charAt(j);
                switch (k) {
                    case 37:
                        ++j;
                    case 43:
                        flag = true;
                    default:
                        ++j;
                }
            }

            if (!flag) {
                return s;
            } else {
                byte[] abyte = new byte[i];

                k = 0;

                for (int l = 0; l < i; ++l) {
                    char c0 = s.charAt(l);

                    switch (c0) {
                        case '%':
                            if (l == i - 1) {
                                throw new IllegalArgumentException("unterminated escape sequence at end of string: " + s);
                            }

                            ++l;
                            c0 = s.charAt(l);
                            if (c0 == '%') {
                                abyte[k++] = 37;
                                break;
                            } else {
                                if (l == i - 1) {
                                    throw new IllegalArgumentException("partial escape sequence at end of string: " + s);
                                }

                                c0 = decodeHexNibble(c0);
                                ++l;
                                char c1 = decodeHexNibble(s.charAt(l));

                                if (c0 == '\uffff' || c1 == '\uffff') {
                                    throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(l - 1) + s.charAt(l) + "' at index " + (l - 2) + " of: " + s);
                                }

                                c0 = (char) (c0 * 16 + c1);
                            }
                        default:
                            abyte[k++] = (byte) c0;
                            break;
                        case '+':
                            abyte[k++] = 32;
                    }
                }

                return new String(abyte, 0, k, charset);
            }
        }
    }

    private static char decodeHexNibble(char c0) {
        return '0' <= c0 && c0 <= '9' ? (char) (c0 - 48) : ('a' <= c0 && c0 <= 'f' ? (char) (c0 - 97 + 10) : ('A' <= c0 && c0 <= 'F' ? (char) (c0 - 65 + 10) : '\uffff'));
    }
}
