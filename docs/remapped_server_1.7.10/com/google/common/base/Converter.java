package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract class Converter<A, B> implements Function<A, B> {

    private final boolean handleNullAutomatically;
    private transient Converter<B, A> reverse;

    protected Converter() {
        this(true);
    }

    Converter(boolean flag) {
        this.handleNullAutomatically = flag;
    }

    protected abstract B doForward(A a0);

    protected abstract A doBackward(B b0);

    @Nullable
    public final B convert(@Nullable A a0) {
        return this.correctedDoForward(a0);
    }

    @Nullable
    B correctedDoForward(@Nullable A a0) {
        return this.handleNullAutomatically ? (a0 == null ? null : Preconditions.checkNotNull(this.doForward(a0))) : this.doForward(a0);
    }

    @Nullable
    A correctedDoBackward(@Nullable B b0) {
        return this.handleNullAutomatically ? (b0 == null ? null : Preconditions.checkNotNull(this.doBackward(b0))) : this.doBackward(b0);
    }

    public Iterable<B> convertAll(final Iterable<? extends A> iterable) {
        Preconditions.checkNotNull(iterable, "fromIterable");
        return new Iterable<B>() {
            public Iterator<B> iterator() {
                return new Iterator<B>() {
                    private final Iterator<? extends A> fromIterator = iterable.iterator();

                    public boolean hasNext() {
                        return this.fromIterator.hasNext();
                    }

                    public B next() {
                        return Converter.this.convert(this.fromIterator.next());
                    }

                    public void remove() {
                        this.fromIterator.remove();
                    }
                };
            }
        };
    }

    public Converter<B, A> reverse() {
        Converter converter = this.reverse;

        return converter == null ? (this.reverse = new Converter.ReverseConverter<>(this)) : converter;
    }

    public <C> Converter<A, C> andThen(Converter<B, C> converter) {
        return new Converter.ConverterComposition<>(this, (Converter) Preconditions.checkNotNull(converter));
    }

    /** @deprecated */
    @Deprecated
    @Nullable
    @Override
    public final B apply(@Nullable A a0) {
        return this.convert(a0);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        return super.equals(object);
    }

    public static <T> Converter<T, T> identity() {
        return Converter.IdentityConverter.INSTANCE;
    }

    private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable {

        static final Converter.IdentityConverter INSTANCE = new Converter.IdentityConverter<>();
        private static final long serialVersionUID = 0L;

        private IdentityConverter() {}

        @Override
        protected T doForward(T t0) {
            return t0;
        }

        @Override
        protected T doBackward(T t0) {
            return t0;
        }

        @Override
        public Converter.IdentityConverter<T> reverse() {
            return this;
        }

        @Override
        public <S> Converter<T, S> andThen(Converter<T, S> converter) {
            return (Converter) Preconditions.checkNotNull(converter, "otherConverter");
        }

        public String toString() {
            return "Converter.identity()";
        }

        private Object readResolve() {
            return Converter.IdentityConverter.INSTANCE;
        }
    }

    private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {

        final Converter<A, B> first;
        final Converter<B, C> second;
        private static final long serialVersionUID = 0L;

        ConverterComposition(Converter<A, B> converter, Converter<B, C> converter1) {
            this.first = converter;
            this.second = converter1;
        }

        @Override
        protected C doForward(A a0) {
            throw new AssertionError();
        }

        @Override
        protected A doBackward(C c0) {
            throw new AssertionError();
        }

        @Nullable
        @Override
        C correctedDoForward(@Nullable A a0) {
            return this.second.correctedDoForward(this.first.correctedDoForward(a0));
        }

        @Nullable
        @Override
        A correctedDoBackward(@Nullable C c0) {
            return this.first.correctedDoBackward(this.second.correctedDoBackward(c0));
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Converter.ConverterComposition)) {
                return false;
            } else {
                Converter.ConverterComposition converter_convertercomposition = (Converter.ConverterComposition) object;

                return this.first.equals(converter_convertercomposition.first) && this.second.equals(converter_convertercomposition.second);
            }
        }

        public int hashCode() {
            return 31 * this.first.hashCode() + this.second.hashCode();
        }

        public String toString() {
            return this.first + ".andThen(" + this.second + ")";
        }
    }

    private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {

        final Converter<A, B> original;
        private static final long serialVersionUID = 0L;

        ReverseConverter(Converter<A, B> converter) {
            this.original = converter;
        }

        @Override
        protected A doForward(B b0) {
            throw new AssertionError();
        }

        @Override
        protected B doBackward(A a0) {
            throw new AssertionError();
        }

        @Nullable
        @Override
        A correctedDoForward(@Nullable B b0) {
            return this.original.correctedDoBackward(b0);
        }

        @Nullable
        @Override
        B correctedDoBackward(@Nullable A a0) {
            return this.original.correctedDoForward(a0);
        }

        @Override
        public Converter<A, B> reverse() {
            return this.original;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Converter.ReverseConverter) {
                Converter.ReverseConverter converter_reverseconverter = (Converter.ReverseConverter) object;

                return this.original.equals(converter_reverseconverter.original);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return ~this.original.hashCode();
        }

        public String toString() {
            return this.original + ".reverse()";
        }
    }
}
