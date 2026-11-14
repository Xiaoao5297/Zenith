package io.netty.handler.codec.spdy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class DefaultSpdyHeaders extends SpdyHeaders {

    private static final int BUCKET_SIZE = 17;
    private final DefaultSpdyHeaders.HeaderEntry[] entries = new DefaultSpdyHeaders.HeaderEntry[17];
    private final DefaultSpdyHeaders.HeaderEntry head = new DefaultSpdyHeaders.HeaderEntry(-1, (String) null, (String) null);

    private static int hash(String s) {
        int i = 0;

        for (int j = s.length() - 1; j >= 0; --j) {
            char c0 = s.charAt(j);

            if (c0 >= 'A' && c0 <= 'Z') {
                c0 = (char) (c0 + 32);
            }

            i = 31 * i + c0;
        }

        if (i > 0) {
            return i;
        } else if (i == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return -i;
        }
    }

    private static boolean eq(String s, String s1) {
        int i = s.length();

        if (i != s1.length()) {
            return false;
        } else {
            for (int j = i - 1; j >= 0; --j) {
                char c0 = s.charAt(j);
                char c1 = s1.charAt(j);

                if (c0 != c1) {
                    if (c0 >= 'A' && c0 <= 'Z') {
                        c0 = (char) (c0 + 32);
                    }

                    if (c1 >= 'A' && c1 <= 'Z') {
                        c1 = (char) (c1 + 32);
                    }

                    if (c0 != c1) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private static int index(int i) {
        return i % 17;
    }

    DefaultSpdyHeaders() {
        this.head.before = this.head.after = this.head;
    }

    @Override
    public SpdyHeaders add(String s, Object object) {
        String s1 = s.toLowerCase();

        SpdyCodecUtil.validateHeaderName(s1);
        String s2 = toString(object);

        SpdyCodecUtil.validateHeaderValue(s2);
        int i = hash(s1);
        int j = index(i);

        this.add0(i, j, s1, s2);
        return this;
    }

    private void add0(int i, int j, String s, String s1) {
        DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.entries[j];
        DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry1;

        this.entries[j] = defaultspdyheaders_headerentry1 = new DefaultSpdyHeaders.HeaderEntry(i, s, s1);
        defaultspdyheaders_headerentry1.next = defaultspdyheaders_headerentry;
        defaultspdyheaders_headerentry1.addBefore(this.head);
    }

    @Override
    public SpdyHeaders remove(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            String s1 = s.toLowerCase();
            int i = hash(s1);
            int j = index(i);

            this.remove0(i, j, s1);
            return this;
        }
    }

    private void remove0(int i, int j, String s) {
        DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.entries[j];

        if (defaultspdyheaders_headerentry != null) {
            DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry1;

            while (defaultspdyheaders_headerentry.hash == i && eq(s, defaultspdyheaders_headerentry.key)) {
                defaultspdyheaders_headerentry.remove();
                defaultspdyheaders_headerentry1 = defaultspdyheaders_headerentry.next;
                if (defaultspdyheaders_headerentry1 == null) {
                    this.entries[j] = null;
                    return;
                }

                this.entries[j] = defaultspdyheaders_headerentry1;
                defaultspdyheaders_headerentry = defaultspdyheaders_headerentry1;
            }

            while (true) {
                defaultspdyheaders_headerentry1 = defaultspdyheaders_headerentry.next;
                if (defaultspdyheaders_headerentry1 == null) {
                    return;
                }

                if (defaultspdyheaders_headerentry1.hash == i && eq(s, defaultspdyheaders_headerentry1.key)) {
                    defaultspdyheaders_headerentry.next = defaultspdyheaders_headerentry1.next;
                    defaultspdyheaders_headerentry1.remove();
                } else {
                    defaultspdyheaders_headerentry = defaultspdyheaders_headerentry1;
                }
            }
        }
    }

    @Override
    public SpdyHeaders set(String s, Object object) {
        String s1 = s.toLowerCase();

        SpdyCodecUtil.validateHeaderName(s1);
        String s2 = toString(object);

        SpdyCodecUtil.validateHeaderValue(s2);
        int i = hash(s1);
        int j = index(i);

        this.remove0(i, j, s1);
        this.add0(i, j, s1, s2);
        return this;
    }

    @Override
    public SpdyHeaders set(String s, Iterable<?> iterable) {
        if (iterable == null) {
            throw new NullPointerException("values");
        } else {
            String s1 = s.toLowerCase();

            SpdyCodecUtil.validateHeaderName(s1);
            int i = hash(s1);
            int j = index(i);

            this.remove0(i, j, s1);
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                if (object == null) {
                    break;
                }

                String s2 = toString(object);

                SpdyCodecUtil.validateHeaderValue(s2);
                this.add0(i, j, s1, s2);
            }

            return this;
        }
    }

    @Override
    public SpdyHeaders clear() {
        for (int i = 0; i < this.entries.length; ++i) {
            this.entries[i] = null;
        }

        this.head.before = this.head.after = this.head;
        return this;
    }

    @Override
    public String get(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            int i = hash(s);
            int j = index(i);

            for (DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.entries[j]; defaultspdyheaders_headerentry != null; defaultspdyheaders_headerentry = defaultspdyheaders_headerentry.next) {
                if (defaultspdyheaders_headerentry.hash == i && eq(s, defaultspdyheaders_headerentry.key)) {
                    return defaultspdyheaders_headerentry.value;
                }
            }

            return null;
        }
    }

    @Override
    public List<String> getAll(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            LinkedList linkedlist = new LinkedList();
            int i = hash(s);
            int j = index(i);

            for (DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.entries[j]; defaultspdyheaders_headerentry != null; defaultspdyheaders_headerentry = defaultspdyheaders_headerentry.next) {
                if (defaultspdyheaders_headerentry.hash == i && eq(s, defaultspdyheaders_headerentry.key)) {
                    linkedlist.addFirst(defaultspdyheaders_headerentry.value);
                }
            }

            return linkedlist;
        }
    }

    @Override
    public List<Entry<String, String>> entries() {
        LinkedList linkedlist = new LinkedList();

        for (DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.head.after; defaultspdyheaders_headerentry != this.head; defaultspdyheaders_headerentry = defaultspdyheaders_headerentry.after) {
            linkedlist.add(defaultspdyheaders_headerentry);
        }

        return linkedlist;
    }

    @Override
    public boolean contains(String s) {
        return this.get(s) != null;
    }

    @Override
    public Set<String> names() {
        TreeSet treeset = new TreeSet();

        for (DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry = this.head.after; defaultspdyheaders_headerentry != this.head; defaultspdyheaders_headerentry = defaultspdyheaders_headerentry.after) {
            treeset.add(defaultspdyheaders_headerentry.key);
        }

        return treeset;
    }

    @Override
    public SpdyHeaders add(String s, Iterable<?> iterable) {
        SpdyCodecUtil.validateHeaderValue(s);
        int i = hash(s);
        int j = index(i);
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();
            String s1 = toString(object);

            SpdyCodecUtil.validateHeaderValue(s1);
            this.add0(i, j, s, s1);
        }

        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.entries().isEmpty();
    }

    private static String toString(Object object) {
        return object == null ? null : object.toString();
    }

    private static final class HeaderEntry implements Entry<String, String> {

        final int hash;
        final String key;
        String value;
        DefaultSpdyHeaders.HeaderEntry next;
        DefaultSpdyHeaders.HeaderEntry before;
        DefaultSpdyHeaders.HeaderEntry after;

        HeaderEntry(int i, String s, String s1) {
            this.hash = i;
            this.key = s;
            this.value = s1;
        }

        void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }

        void addBefore(DefaultSpdyHeaders.HeaderEntry defaultspdyheaders_headerentry) {
            this.after = defaultspdyheaders_headerentry;
            this.before = defaultspdyheaders_headerentry.before;
            this.before.after = this;
            this.after.before = this;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String setValue(String s) {
            if (s == null) {
                throw new NullPointerException("value");
            } else {
                SpdyCodecUtil.validateHeaderValue(s);
                String s1 = this.value;

                this.value = s;
                return s1;
            }
        }

        public String toString() {
            return this.key + '=' + this.value;
        }
    }
}
