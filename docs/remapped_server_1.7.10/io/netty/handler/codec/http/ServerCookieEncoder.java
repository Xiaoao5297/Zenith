package io.netty.handler.codec.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public final class ServerCookieEncoder {

    public static String encode(String s, String s1) {
        return encode((Cookie) (new DefaultCookie(s, s1)));
    }

    public static String encode(Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            CookieEncoderUtil.add(stringbuilder, cookie.getName(), cookie.getValue());
            if (cookie.getMaxAge() != Long.MIN_VALUE) {
                if (cookie.getVersion() == 0) {
                    CookieEncoderUtil.addUnquoted(stringbuilder, "Expires", HttpHeaderDateFormat.get().format(new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000L)));
                } else {
                    CookieEncoderUtil.add(stringbuilder, "Max-Age", cookie.getMaxAge());
                }
            }

            if (cookie.getPath() != null) {
                if (cookie.getVersion() > 0) {
                    CookieEncoderUtil.add(stringbuilder, "Path", cookie.getPath());
                } else {
                    CookieEncoderUtil.addUnquoted(stringbuilder, "Path", cookie.getPath());
                }
            }

            if (cookie.getDomain() != null) {
                if (cookie.getVersion() > 0) {
                    CookieEncoderUtil.add(stringbuilder, "Domain", cookie.getDomain());
                } else {
                    CookieEncoderUtil.addUnquoted(stringbuilder, "Domain", cookie.getDomain());
                }
            }

            if (cookie.isSecure()) {
                stringbuilder.append("Secure");
                stringbuilder.append(';');
                stringbuilder.append(' ');
            }

            if (cookie.isHttpOnly()) {
                stringbuilder.append("HTTPOnly");
                stringbuilder.append(';');
                stringbuilder.append(' ');
            }

            if (cookie.getVersion() >= 1) {
                if (cookie.getComment() != null) {
                    CookieEncoderUtil.add(stringbuilder, "Comment", cookie.getComment());
                }

                CookieEncoderUtil.add(stringbuilder, "Version", 1L);
                if (cookie.getCommentUrl() != null) {
                    CookieEncoderUtil.addQuoted(stringbuilder, "CommentURL", cookie.getCommentUrl());
                }

                if (!cookie.getPorts().isEmpty()) {
                    stringbuilder.append("Port");
                    stringbuilder.append('=');
                    stringbuilder.append('"');
                    Iterator iterator = cookie.getPorts().iterator();

                    while (iterator.hasNext()) {
                        int i = (Integer) iterator.next();

                        stringbuilder.append(i);
                        stringbuilder.append(',');
                    }

                    stringbuilder.setCharAt(stringbuilder.length() - 1, '"');
                    stringbuilder.append(';');
                    stringbuilder.append(' ');
                }

                if (cookie.isDiscard()) {
                    stringbuilder.append("Discard");
                    stringbuilder.append(';');
                    stringbuilder.append(' ');
                }
            }

            return CookieEncoderUtil.stripTrailingSeparator(stringbuilder);
        }
    }

    public static List<String> encode(Cookie... acookie) {
        if (acookie == null) {
            throw new NullPointerException("cookies");
        } else {
            ArrayList arraylist = new ArrayList(acookie.length);
            Cookie[] acookie1 = acookie;
            int i = acookie.length;

            for (int j = 0; j < i; ++j) {
                Cookie cookie = acookie1[j];

                if (cookie == null) {
                    break;
                }

                arraylist.add(encode(cookie));
            }

            return arraylist;
        }
    }

    public static List<String> encode(Collection<Cookie> collection) {
        if (collection == null) {
            throw new NullPointerException("cookies");
        } else {
            ArrayList arraylist = new ArrayList(collection.size());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Cookie cookie = (Cookie) iterator.next();

                if (cookie == null) {
                    break;
                }

                arraylist.add(encode(cookie));
            }

            return arraylist;
        }
    }

    public static List<String> encode(Iterable<Cookie> iterable) {
        if (iterable == null) {
            throw new NullPointerException("cookies");
        } else {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Cookie cookie = (Cookie) iterator.next();

                if (cookie == null) {
                    break;
                }

                arraylist.add(encode(cookie));
            }

            return arraylist;
        }
    }

    private ServerCookieEncoder() {}
}
