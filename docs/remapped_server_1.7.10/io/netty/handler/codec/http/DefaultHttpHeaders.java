package io.netty.handler.codec.http;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class DefaultHttpHeaders extends HttpHeaders {

    private static final int BUCKET_SIZE = 17;
    private final DefaultHttpHeaders.HeaderEntry[] entries = new DefaultHttpHeaders.HeaderEntry[17];
    private final DefaultHttpHeaders.HeaderEntry head = new DefaultHttpHeaders.HeaderEntry(-1, (String) null, (String) null);

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

    public DefaultHttpHeaders() {
        this.head.before = this.head.after = this.head;
    }

    void validateHeaderName0(String s) {
        validateHeaderName(s);
    }

    @Override
    public HttpHeaders add(String s, Object object) {
        this.validateHeaderName0(s);
        String s1 = toString(object);

        validateHeaderValue(s1);
        int i = hash(s);
        int j = index(i);

        this.add0(i, j, s, s1);
        return this;
    }

    @Override
    public HttpHeaders add(String s, Iterable<?> iterable) {
        this.validateHeaderName0(s);
        int i = hash(s);
        int j = index(i);
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();
            String s1 = toString(object);

            validateHeaderValue(s1);
            this.add0(i, j, s, s1);
        }

        return this;
    }

    private void add0(int i, int j, String s, String s1) {
        DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.entries[j];
        DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry1;

        this.entries[j] = defaulthttpheaders_headerentry1 = new DefaultHttpHeaders.HeaderEntry(i, s, s1);
        defaulthttpheaders_headerentry1.next = defaulthttpheaders_headerentry;
        defaulthttpheaders_headerentry1.addBefore(this.head);
    }

    @Override
    public HttpHeaders remove(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            int i = hash(s);
            int j = index(i);

            this.remove0(i, j, s);
            return this;
        }
    }

    private void remove0(int i, int j, String s) {
        DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.entries[j];

        if (defaulthttpheaders_headerentry != null) {
            DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry1;

            while (defaulthttpheaders_headerentry.hash == i && eq(s, defaulthttpheaders_headerentry.key)) {
                defaulthttpheaders_headerentry.remove();
                defaulthttpheaders_headerentry1 = defaulthttpheaders_headerentry.next;
                if (defaulthttpheaders_headerentry1 == null) {
                    this.entries[j] = null;
                    return;
                }

                this.entries[j] = defaulthttpheaders_headerentry1;
                defaulthttpheaders_headerentry = defaulthttpheaders_headerentry1;
            }

            while (true) {
                defaulthttpheaders_headerentry1 = defaulthttpheaders_headerentry.next;
                if (defaulthttpheaders_headerentry1 == null) {
                    return;
                }

                if (defaulthttpheaders_headerentry1.hash == i && eq(s, defaulthttpheaders_headerentry1.key)) {
                    defaulthttpheaders_headerentry.next = defaulthttpheaders_headerentry1.next;
                    defaulthttpheaders_headerentry1.remove();
                } else {
                    defaulthttpheaders_headerentry = defaulthttpheaders_headerentry1;
                }
            }
        }
    }

    @Override
    public HttpHeaders set(String s, Object object) {
        this.validateHeaderName0(s);
        String s1 = toString(object);

        validateHeaderValue(s1);
        int i = hash(s);
        int j = index(i);

        this.remove0(i, j, s);
        this.add0(i, j, s, s1);
        return this;
    }

    @Override
    public HttpHeaders set(String s, Iterable<?> iterable) {
        if (iterable == null) {
            throw new NullPointerException("values");
        } else {
            this.validateHeaderName0(s);
            int i = hash(s);
            int j = index(i);

            this.remove0(i, j, s);
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                if (object == null) {
                    break;
                }

                String s1 = toString(object);

                validateHeaderValue(s1);
                this.add0(i, j, s, s1);
            }

            return this;
        }
    }

    @Override
    public HttpHeaders clear() {
        Arrays.fill(this.entries, (Object) null);
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
            DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.entries[j];

            String s1;

            for (s1 = null; defaulthttpheaders_headerentry != null; defaulthttpheaders_headerentry = defaulthttpheaders_headerentry.next) {
                if (defaulthttpheaders_headerentry.hash == i && eq(s, defaulthttpheaders_headerentry.key)) {
                    s1 = defaulthttpheaders_headerentry.value;
                }
            }

            return s1;
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

            for (DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.entries[j]; defaulthttpheaders_headerentry != null; defaulthttpheaders_headerentry = defaulthttpheaders_headerentry.next) {
                if (defaulthttpheaders_headerentry.hash == i && eq(s, defaulthttpheaders_headerentry.key)) {
                    linkedlist.addFirst(defaulthttpheaders_headerentry.value);
                }
            }

            return linkedlist;
        }
    }

    @Override
    public List<Entry<String, String>> entries() {
        LinkedList linkedlist = new LinkedList();

        for (DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.head.after; defaulthttpheaders_headerentry != this.head; defaulthttpheaders_headerentry = defaulthttpheaders_headerentry.after) {
            linkedlist.add(defaulthttpheaders_headerentry);
        }

        return linkedlist;
    }

    public Iterator<Entry<String, String>> iterator() {
        return this.entries().iterator();
    }

    @Override
    public boolean contains(String s) {
        return this.get(s) != null;
    }

    @Override
    public boolean isEmpty() {
        return this.head == this.head.after;
    }

    @Override
    public Set<String> names() {
        TreeSet treeset = new TreeSet(String.CASE_INSENSITIVE_ORDER);

        for (DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry = this.head.after; defaulthttpheaders_headerentry != this.head; defaulthttpheaders_headerentry = defaulthttpheaders_headerentry.after) {
            treeset.add(defaulthttpheaders_headerentry.key);
        }

        return treeset;
    }

    private static String toString(Object object) {
        return object == null ? null : (object instanceof String ? (String) object : (object instanceof Number ? object.toString() : (object instanceof Date ? HttpHeaderDateFormat.get().format((Date) object) : (object instanceof Calendar ? HttpHeaderDateFormat.get().format(((Calendar) object).getTime()) : object.toString()))));
    }

    private static final class HeaderEntry implements Entry<String, String> {

        final int hash;
        final String key;
        String value;
        DefaultHttpHeaders.HeaderEntry next;
        DefaultHttpHeaders.HeaderEntry before;
        DefaultHttpHeaders.HeaderEntry after;

        HeaderEntry(int i, String s, String s1) {
            this.hash = i;
            this.key = s;
            this.value = s1;
        }

        void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }

        void addBefore(DefaultHttpHeaders.HeaderEntry defaulthttpheaders_headerentry) {
            this.after = defaulthttpheaders_headerentry;
            this.before = defaulthttpheaders_headerentry.before;
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
                HttpHeaders.validateHeaderValue(s);
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
