package io.netty.handler.codec.http;

import java.util.Iterator;

public final class ClientCookieEncoder {

    public static String encode(String s, String s1) {
        return encode((Cookie) (new DefaultCookie(s, s1)));
    }

    public static String encode(Cookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie");
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            encode(stringbuilder, cookie);
            return CookieEncoderUtil.stripTrailingSeparator(stringbuilder);
        }
    }

    public static String encode(Cookie... acookie) {
        if (acookie == null) {
            throw new NullPointerException("cookies");
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            Cookie[] acookie1 = acookie;
            int i = acookie.length;

            for (int j = 0; j < i; ++j) {
                Cookie cookie = acookie1[j];

                if (cookie == null) {
                    break;
                }

                encode(stringbuilder, cookie);
            }

            return CookieEncoderUtil.stripTrailingSeparator(stringbuilder);
        }
    }

    public static String encode(Iterable<Cookie> iterable) {
        if (iterable == null) {
            throw new NullPointerException("cookies");
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Cookie cookie = (Cookie) iterator.next();

                if (cookie == null) {
                    break;
                }

                encode(stringbuilder, cookie);
            }

            return CookieEncoderUtil.stripTrailingSeparator(stringbuilder);
        }
    }

    private static void encode(StringBuilder stringbuilder, Cookie cookie) {
        if (cookie.getVersion() >= 1) {
            CookieEncoderUtil.add(stringbuilder, "$Version", 1L);
        }

        CookieEncoderUtil.add(stringbuilder, cookie.getName(), cookie.getValue());
        if (cookie.getPath() != null) {
            CookieEncoderUtil.add(stringbuilder, "$Path", cookie.getPath());
        }

        if (cookie.getDomain() != null) {
            CookieEncoderUtil.add(stringbuilder, "$Domain", cookie.getDomain());
        }

        if (cookie.getVersion() >= 1 && !cookie.getPorts().isEmpty()) {
            stringbuilder.append('$');
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

    }

    private ClientCookieEncoder() {}
}
