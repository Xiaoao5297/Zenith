package io.netty.handler.codec.http;

import io.netty.util.internal.StringUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class CookieDecoder {

    private static final char COMMA = ',';

    public static Set<Cookie> decode(String s) {
        ArrayList arraylist = new ArrayList(8);
        ArrayList arraylist1 = new ArrayList(8);

        extractKeyValuePairs(s, arraylist, arraylist1);
        if (arraylist.isEmpty()) {
            return Collections.emptySet();
        } else {
            int i = 0;
            int j;

            if (((String) arraylist.get(0)).equalsIgnoreCase("Version")) {
                try {
                    i = Integer.parseInt((String) arraylist1.get(0));
                } catch (NumberFormatException numberformatexception) {
                    ;
                }

                j = 1;
            } else {
                j = 0;
            }

            if (arraylist.size() <= j) {
                return Collections.emptySet();
            } else {
                TreeSet treeset = new TreeSet();

                while (j < arraylist.size()) {
                    String s1 = (String) arraylist.get(j);
                    String s2 = (String) arraylist1.get(j);

                    if (s2 == null) {
                        s2 = "";
                    }

                    DefaultCookie defaultcookie = new DefaultCookie(s1, s2);
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;
                    String s3 = null;
                    String s4 = null;
                    String s5 = null;
                    String s6 = null;
                    long k = Long.MIN_VALUE;
                    ArrayList arraylist2 = new ArrayList(2);
                    int l = j + 1;

                    while (true) {
                        if (l < arraylist.size()) {
                            label105:
                            {
                                s1 = (String) arraylist.get(l);
                                s2 = (String) arraylist1.get(l);
                                if ("Discard".equalsIgnoreCase(s1)) {
                                    flag = true;
                                } else if ("Secure".equalsIgnoreCase(s1)) {
                                    flag1 = true;
                                } else if ("HTTPOnly".equalsIgnoreCase(s1)) {
                                    flag2 = true;
                                } else if ("Comment".equalsIgnoreCase(s1)) {
                                    s3 = s2;
                                } else if ("CommentURL".equalsIgnoreCase(s1)) {
                                    s4 = s2;
                                } else if ("Domain".equalsIgnoreCase(s1)) {
                                    s5 = s2;
                                } else if ("Path".equalsIgnoreCase(s1)) {
                                    s6 = s2;
                                } else if ("Expires".equalsIgnoreCase(s1)) {
                                    try {
                                        long i1 = HttpHeaderDateFormat.get().parse(s2).getTime() - System.currentTimeMillis();

                                        k = i1 / 1000L + (long) (i1 % 1000L != 0L ? 1 : 0);
                                    } catch (ParseException parseexception) {
                                        ;
                                    }
                                } else if ("Max-Age".equalsIgnoreCase(s1)) {
                                    k = (long) Integer.parseInt(s2);
                                } else if ("Version".equalsIgnoreCase(s1)) {
                                    i = Integer.parseInt(s2);
                                } else {
                                    if (!"Port".equalsIgnoreCase(s1)) {
                                        break label105;
                                    }

                                    String[] astring = StringUtil.split(s2, ',');
                                    String[] astring1 = astring;
                                    int j1 = astring.length;

                                    for (int k1 = 0; k1 < j1; ++k1) {
                                        String s7 = astring1[k1];

                                        try {
                                            arraylist2.add(Integer.valueOf(s7));
                                        } catch (NumberFormatException numberformatexception1) {
                                            ;
                                        }
                                    }
                                }

                                ++l;
                                ++j;
                                continue;
                            }
                        }

                        defaultcookie.setVersion(i);
                        defaultcookie.setMaxAge(k);
                        defaultcookie.setPath(s6);
                        defaultcookie.setDomain(s5);
                        defaultcookie.setSecure(flag1);
                        defaultcookie.setHttpOnly(flag2);
                        if (i > 0) {
                            defaultcookie.setComment(s3);
                        }

                        if (i > 1) {
                            defaultcookie.setCommentUrl(s4);
                            defaultcookie.setPorts((Iterable) arraylist2);
                            defaultcookie.setDiscard(flag);
                        }

                        treeset.add(defaultcookie);
                        ++j;
                        break;
                    }
                }

                return treeset;
            }
        }
    }

    private static void extractKeyValuePairs(String s, List<String> list, List<String> list1) {
        int i = s.length();
        int j = 0;

        label79:
        while (j != i) {
            switch (s.charAt(j)) {
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ' ':
                case ',':
                case ';':
                    ++j;
                    break;
                default:
                    while (j != i) {
                        if (s.charAt(j) != '$') {
                            String s1;
                            String s2;

                            if (j == i) {
                                s1 = null;
                                s2 = null;
                            } else {
                                int k = j;

                                label70:
                                while (true) {
                                    switch (s.charAt(j)) {
                                        case ';':
                                            s1 = s.substring(k, j);
                                            s2 = null;
                                            break label70;
                                        case '=':
                                            s1 = s.substring(k, j);
                                            ++j;
                                            if (j == i) {
                                                s2 = "";
                                            } else {
                                                char c0 = s.charAt(j);

                                                if (c0 != '"' && c0 != '\'') {
                                                    int l = s.indexOf(59, j);

                                                    if (l > 0) {
                                                        s2 = s.substring(j, l);
                                                        j = l;
                                                    } else {
                                                        s2 = s.substring(j);
                                                        j = i;
                                                    }
                                                } else {
                                                    StringBuilder stringbuilder = new StringBuilder(s.length() - j);
                                                    char c1 = c0;
                                                    boolean flag = false;

                                                    ++j;

                                                    while (j != i) {
                                                        if (flag) {
                                                            flag = false;
                                                            c0 = s.charAt(j++);
                                                            switch (c0) {
                                                                case '"':
                                                                case '\'':
                                                                case '\\':
                                                                    stringbuilder.setCharAt(stringbuilder.length() - 1, c0);
                                                                    break;
                                                                default:
                                                                    stringbuilder.append(c0);
                                                            }
                                                        } else {
                                                            c0 = s.charAt(j++);
                                                            if (c0 == c1) {
                                                                s2 = stringbuilder.toString();
                                                                break label70;
                                                            }

                                                            stringbuilder.append(c0);
                                                            if (c0 == '\\') {
                                                                flag = true;
                                                            }
                                                        }
                                                    }

                                                    s2 = stringbuilder.toString();
                                                }
                                            }
                                            break label70;
                                        default:
                                            ++j;
                                            if (j == i) {
                                                s1 = s.substring(k);
                                                s2 = null;
                                                break label70;
                                            }
                                    }
                                }
                            }

                            list.add(s1);
                            list1.add(s2);
                            continue label79;
                        }

                        ++j;
                    }

                    return;
            }
        }

    }

    private CookieDecoder() {}
}
