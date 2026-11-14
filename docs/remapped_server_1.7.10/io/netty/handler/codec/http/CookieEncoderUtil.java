package io.netty.handler.codec.http;

final class CookieEncoderUtil {

    static String stripTrailingSeparator(StringBuilder stringbuilder) {
        if (stringbuilder.length() > 0) {
            stringbuilder.setLength(stringbuilder.length() - 2);
        }

        return stringbuilder.toString();
    }

    static void add(StringBuilder stringbuilder, String s, String s1) {
        if (s1 == null) {
            addQuoted(stringbuilder, s, "");
        } else {
            int i = 0;

            while (i < s1.length()) {
                char c0 = s1.charAt(i);

                switch (c0) {
                    case '\t':
                    case ' ':
                    case '"':
                    case '(':
                    case ')':
                    case ',':
                    case '/':
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case '[':
                    case '\\':
                    case ']':
                    case '{':
                    case '}':
                        addQuoted(stringbuilder, s, s1);
                        return;
                    default:
                        ++i;
                }
            }

            addUnquoted(stringbuilder, s, s1);
        }
    }

    static void addUnquoted(StringBuilder stringbuilder, String s, String s1) {
        stringbuilder.append(s);
        stringbuilder.append('=');
        stringbuilder.append(s1);
        stringbuilder.append(';');
        stringbuilder.append(' ');
    }

    static void addQuoted(StringBuilder stringbuilder, String s, String s1) {
        if (s1 == null) {
            s1 = "";
        }

        stringbuilder.append(s);
        stringbuilder.append('=');
        stringbuilder.append('"');
        stringbuilder.append(s1.replace("\\", "\\\\").replace("\"", "\\\""));
        stringbuilder.append('"');
        stringbuilder.append(';');
        stringbuilder.append(' ');
    }

    static void add(StringBuilder stringbuilder, String s, long i) {
        stringbuilder.append(s);
        stringbuilder.append('=');
        stringbuilder.append(i);
        stringbuilder.append(';');
        stringbuilder.append(' ');
    }

    private CookieEncoderUtil() {}
}
