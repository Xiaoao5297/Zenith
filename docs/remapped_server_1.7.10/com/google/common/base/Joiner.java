package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
public class Joiner {

    private final String separator;

    public static Joiner on(String s) {
        return new Joiner(s);
    }

    public static Joiner on(char c0) {
        return new Joiner(String.valueOf(c0));
    }

    private Joiner(String s) {
        this.separator = (String) Preconditions.checkNotNull(s);
    }

    private Joiner(Joiner joiner) {
        this.separator = joiner.separator;
    }

    public <A extends Appendable> A appendTo(A a0, Iterable<?> iterable) throws IOException {
        return this.appendTo(a0, iterable.iterator());
    }

    public <A extends Appendable> A appendTo(A a0, Iterator<?> iterator) throws IOException {
        Preconditions.checkNotNull(a0);
        if (iterator.hasNext()) {
            a0.append(this.toString(iterator.next()));

            while (iterator.hasNext()) {
                a0.append(this.separator);
                a0.append(this.toString(iterator.next()));
            }
        }

        return a0;
    }

    public final <A extends Appendable> A appendTo(A a0, Object[] aobject) throws IOException {
        return this.appendTo(a0, (Iterable) Arrays.asList(aobject));
    }

    public final <A extends Appendable> A appendTo(A a0, @Nullable Object object, @Nullable Object object1, Object... aobject) throws IOException {
        return this.appendTo(a0, iterable(object, object1, aobject));
    }

    public final StringBuilder appendTo(StringBuilder stringbuilder, Iterable<?> iterable) {
        return this.appendTo(stringbuilder, iterable.iterator());
    }

    public final StringBuilder appendTo(StringBuilder stringbuilder, Iterator<?> iterator) {
        try {
            this.appendTo((Appendable) stringbuilder, iterator);
            return stringbuilder;
        } catch (IOException ioexception) {
            throw new AssertionError(ioexception);
        }
    }

    public final StringBuilder appendTo(StringBuilder stringbuilder, Object[] aobject) {
        return this.appendTo(stringbuilder, (Iterable) Arrays.asList(aobject));
    }

    public final StringBuilder appendTo(StringBuilder stringbuilder, @Nullable Object object, @Nullable Object object1, Object... aobject) {
        return this.appendTo(stringbuilder, iterable(object, object1, aobject));
    }

    public final String join(Iterable<?> iterable) {
        return this.join(iterable.iterator());
    }

    public final String join(Iterator<?> iterator) {
        return this.appendTo(new StringBuilder(), iterator).toString();
    }

    public final String join(Object[] aobject) {
        return this.join((Iterable) Arrays.asList(aobject));
    }

    public final String join(@Nullable Object object, @Nullable Object object1, Object... aobject) {
        return this.join(iterable(object, object1, aobject));
    }

    @CheckReturnValue
    public Joiner useForNull(final String s) {
        Preconditions.checkNotNull(s);
        return new Joiner(this) {
            @Override
            CharSequence toString(@Nullable Object object) {
                return (CharSequence) (object == null ? s : Joiner.this.toString(object));
            }

            @Override
            public Joiner useForNull(String s1) {
                throw new UnsupportedOperationException("already specified useForNull");
            }

            @Override
            public Joiner skipNulls() {
                throw new UnsupportedOperationException("already specified useForNull");
            }
        };
    }

    @CheckReturnValue
    public Joiner skipNulls() {
        return new Joiner(this) {
            @Override
            public <A extends Appendable> A appendTo(A a0, Iterator<?> iterator) throws IOException {
                Preconditions.checkNotNull(a0, "appendable");
                Preconditions.checkNotNull(iterator, "parts");

                Object object;

                while (iterator.hasNext()) {
                    object = iterator.next();
                    if (object != null) {
                        a0.append(Joiner.this.toString(object));
                        break;
                    }
                }

                while (iterator.hasNext()) {
                    object = iterator.next();
                    if (object != null) {
                        a0.append(Joiner.this.separator);
                        a0.append(Joiner.this.toString(object));
                    }
                }

                return a0;
            }

            @Override
            public Joiner useForNull(String s) {
                throw new UnsupportedOperationException("already specified skipNulls");
            }

            @Override
            public Joiner.MapJoiner withKeyValueSeparator(String s) {
                throw new UnsupportedOperationException("can't use .skipNulls() with maps");
            }
        };
    }

    @CheckReturnValue
    public Joiner.MapJoiner withKeyValueSeparator(String s) {
        return new Joiner.MapJoiner(this, s);
    }

    CharSequence toString(Object object) {
        Preconditions.checkNotNull(object);
        return (CharSequence) (object instanceof CharSequence ? (CharSequence) object : object.toString());
    }

    private static Iterable<Object> iterable(final Object object, final Object object1, final Object[] aobject) {
        Preconditions.checkNotNull(aobject);
        return new AbstractList<Object>() {
            public int size() {
                return aobject.length + 2;
            }

            public Object get(int i) {
                switch (i) {
                    case 0:
                        return object;
                    case 1:
                        return object1;
                    default:
                        return aobject[i - 2];
                }
            }
        };
    }

    public static final class MapJoiner {

        private final Joiner joiner;
        private final String keyValueSeparator;

        private MapJoiner(Joiner joiner, String s) {
            this.joiner = joiner;
            this.keyValueSeparator = (String) Preconditions.checkNotNull(s);
        }

        public <A extends Appendable> A appendTo(A a0, Map<?, ?> map) throws IOException {
            return this.appendTo(a0, (Iterable) map.entrySet());
        }

        public StringBuilder appendTo(StringBuilder stringbuilder, Map<?, ?> map) {
            return this.appendTo(stringbuilder, (Iterable) map.entrySet());
        }

        public String join(Map<?, ?> map) {
            return this.join((Iterable) map.entrySet());
        }

        @Beta
        public <A extends Appendable> A appendTo(A a0, Iterable<? extends Entry<?, ?>> iterable) throws IOException {
            return this.appendTo(a0, iterable.iterator());
        }

        @Beta
        public <A extends Appendable> A appendTo(A a0, Iterator<? extends Entry<?, ?>> iterator) throws IOException {
            Preconditions.checkNotNull(a0);
            if (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                a0.append(this.joiner.toString(entry.getKey()));
                a0.append(this.keyValueSeparator);
                a0.append(this.joiner.toString(entry.getValue()));

                while (iterator.hasNext()) {
                    a0.append(this.joiner.separator);
                    Entry entry1 = (Entry) iterator.next();

                    a0.append(this.joiner.toString(entry1.getKey()));
                    a0.append(this.keyValueSeparator);
                    a0.append(this.joiner.toString(entry1.getValue()));
                }
            }

            return a0;
        }

        @Beta
        public StringBuilder appendTo(StringBuilder stringbuilder, Iterable<? extends Entry<?, ?>> iterable) {
            return this.appendTo(stringbuilder, iterable.iterator());
        }

        @Beta
        public StringBuilder appendTo(StringBuilder stringbuilder, Iterator<? extends Entry<?, ?>> iterator) {
            try {
                this.appendTo((Appendable) stringbuilder, iterator);
                return stringbuilder;
            } catch (IOException ioexception) {
                throw new AssertionError(ioexception);
            }
        }

        @Beta
        public String join(Iterable<? extends Entry<?, ?>> iterable) {
            return this.join(iterable.iterator());
        }

        @Beta
        public String join(Iterator<? extends Entry<?, ?>> iterator) {
            return this.appendTo(new StringBuilder(), iterator).toString();
        }

        @CheckReturnValue
        public Joiner.MapJoiner useForNull(String s) {
            return new Joiner.MapJoiner(this.joiner.useForNull(s), this.keyValueSeparator);
        }
    }
}
