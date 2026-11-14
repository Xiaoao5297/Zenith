package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import java.io.Serializable;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
abstract class Cut<C extends Comparable> implements Comparable<Cut<C>>, Serializable {

    final C endpoint;
    private static final long serialVersionUID = 0L;

    Cut(@Nullable C c0) {
        this.endpoint = c0;
    }

    abstract boolean isLessThan(C c0);

    abstract BoundType typeAsLowerBound();

    abstract BoundType typeAsUpperBound();

    abstract Cut<C> withLowerBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain);

    abstract Cut<C> withUpperBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain);

    abstract void describeAsLowerBound(StringBuilder stringbuilder);

    abstract void describeAsUpperBound(StringBuilder stringbuilder);

    abstract C leastValueAbove(DiscreteDomain<C> discretedomain);

    abstract C greatestValueBelow(DiscreteDomain<C> discretedomain);

    Cut<C> canonical(DiscreteDomain<C> discretedomain) {
        return this;
    }

    public int compareTo(Cut<C> cut) {
        if (cut == belowAll()) {
            return 1;
        } else if (cut == aboveAll()) {
            return -1;
        } else {
            int i = Range.compareOrThrow(this.endpoint, cut.endpoint);

            return i != 0 ? i : Booleans.compare(this instanceof Cut.AboveValue, cut instanceof Cut.AboveValue);
        }
    }

    C endpoint() {
        return this.endpoint;
    }

    public boolean equals(Object object) {
        if (object instanceof Cut) {
            Cut cut = (Cut) object;

            try {
                int i = this.compareTo(cut);

                return i == 0;
            } catch (ClassCastException classcastexception) {
                ;
            }
        }

        return false;
    }

    static <C extends Comparable> Cut<C> belowAll() {
        return Cut.BelowAll.INSTANCE;
    }

    static <C extends Comparable> Cut<C> aboveAll() {
        return Cut.AboveAll.INSTANCE;
    }

    static <C extends Comparable> Cut<C> belowValue(C c0) {
        return new Cut.BelowValue<>(c0);
    }

    static <C extends Comparable> Cut<C> aboveValue(C c0) {
        return new Cut.AboveValue<>(c0);
    }

    private static final class AboveValue<C extends Comparable> extends Cut<C> {

        private static final long serialVersionUID = 0L;

        AboveValue(C c0) {
            super((Comparable) Preconditions.checkNotNull(c0));
        }

        @Override
        boolean isLessThan(C c0) {
            return Range.compareOrThrow(this.endpoint, c0) < 0;
        }

        @Override
        BoundType typeAsLowerBound() {
            return BoundType.OPEN;
        }

        @Override
        BoundType typeAsUpperBound() {
            return BoundType.CLOSED;
        }

        @Override
        Cut<C> withLowerBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain) {
            switch (boundtype) {
                case CLOSED:
                    Comparable comparable = discretedomain.next(this.endpoint);

                    return comparable == null ? Cut.belowAll() : belowValue(comparable);
                case OPEN:
                    return this;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        Cut<C> withUpperBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain) {
            switch (boundtype) {
                case CLOSED:
                    return this;
                case OPEN:
                    Comparable comparable = discretedomain.next(this.endpoint);

                    return comparable == null ? Cut.aboveAll() : belowValue(comparable);
                default:
                    throw new AssertionError();
            }
        }

        @Override
        void describeAsLowerBound(StringBuilder stringbuilder) {
            stringbuilder.append('(').append(this.endpoint);
        }

        @Override
        void describeAsUpperBound(StringBuilder stringbuilder) {
            stringbuilder.append(this.endpoint).append(']');
        }

        @Override
        C leastValueAbove(DiscreteDomain<C> discretedomain) {
            return discretedomain.next(this.endpoint);
        }

        @Override
        C greatestValueBelow(DiscreteDomain<C> discretedomain) {
            return this.endpoint;
        }

        @Override
        Cut<C> canonical(DiscreteDomain<C> discretedomain) {
            Comparable comparable = this.leastValueAbove(discretedomain);

            return comparable != null ? belowValue(comparable) : Cut.aboveAll();
        }

        public int hashCode() {
            return ~this.endpoint.hashCode();
        }

        public String toString() {
            return "/" + this.endpoint + "\\";
        }
    }

    private static final class BelowValue<C extends Comparable> extends Cut<C> {

        private static final long serialVersionUID = 0L;

        BelowValue(C c0) {
            super((Comparable) Preconditions.checkNotNull(c0));
        }

        @Override
        boolean isLessThan(C c0) {
            return Range.compareOrThrow(this.endpoint, c0) <= 0;
        }

        @Override
        BoundType typeAsLowerBound() {
            return BoundType.CLOSED;
        }

        @Override
        BoundType typeAsUpperBound() {
            return BoundType.OPEN;
        }

        @Override
        Cut<C> withLowerBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain) {
            switch (boundtype) {
                case CLOSED:
                    return this;
                case OPEN:
                    Comparable comparable = discretedomain.previous(this.endpoint);

                    return (Cut) (comparable == null ? Cut.belowAll() : new Cut.AboveValue<>(comparable));
                default:
                    throw new AssertionError();
            }
        }

        @Override
        Cut<C> withUpperBoundType(BoundType boundtype, DiscreteDomain<C> discretedomain) {
            switch (boundtype) {
                case CLOSED:
                    Comparable comparable = discretedomain.previous(this.endpoint);

                    return (Cut) (comparable == null ? Cut.aboveAll() : new Cut.AboveValue<>(comparable));
                case OPEN:
                    return this;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        void describeAsLowerBound(StringBuilder stringbuilder) {
            stringbuilder.append('[').append(this.endpoint);
        }

        @Override
        void describeAsUpperBound(StringBuilder stringbuilder) {
            stringbuilder.append(this.endpoint).append(')');
        }

        @Override
        C leastValueAbove(DiscreteDomain<C> discretedomain) {
            return this.endpoint;
        }

        @Override
        C greatestValueBelow(DiscreteDomain<C> discretedomain) {
            return discretedomain.previous(this.endpoint);
        }

        public int hashCode() {
            return this.endpoint.hashCode();
        }

        public String toString() {
            return "\\" + this.endpoint + "/";
        }
    }

    private static final class AboveAll extends Cut<Comparable<?>> {

        private static final Cut.AboveAll INSTANCE = new Cut.AboveAll();
        private static final long serialVersionUID = 0L;

        private AboveAll() {
            super((Comparable) null);
        }

        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }

        @Override
        boolean isLessThan(Comparable<?> comparable) {
            return false;
        }

        @Override
        BoundType typeAsLowerBound() {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        BoundType typeAsUpperBound() {
            throw new IllegalStateException();
        }

        @Override
        Cut<Comparable<?>> withLowerBoundType(BoundType boundtype, DiscreteDomain<Comparable<?>> discretedomain) {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        Cut<Comparable<?>> withUpperBoundType(BoundType boundtype, DiscreteDomain<Comparable<?>> discretedomain) {
            throw new IllegalStateException();
        }

        @Override
        void describeAsLowerBound(StringBuilder stringbuilder) {
            throw new AssertionError();
        }

        @Override
        void describeAsUpperBound(StringBuilder stringbuilder) {
            stringbuilder.append("+\u221e)");
        }

        @Override
        Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> discretedomain) {
            throw new AssertionError();
        }

        @Override
        Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> discretedomain) {
            return discretedomain.maxValue();
        }

        @Override
        public int compareTo(Cut<Comparable<?>> cut) {
            return cut == this ? 0 : 1;
        }

        public String toString() {
            return "+\u221e";
        }

        private Object readResolve() {
            return Cut.AboveAll.INSTANCE;
        }
    }

    private static final class BelowAll extends Cut<Comparable<?>> {

        private static final Cut.BelowAll INSTANCE = new Cut.BelowAll();
        private static final long serialVersionUID = 0L;

        private BelowAll() {
            super((Comparable) null);
        }

        @Override
        Comparable<?> endpoint() {
            throw new IllegalStateException("range unbounded on this side");
        }

        @Override
        boolean isLessThan(Comparable<?> comparable) {
            return true;
        }

        @Override
        BoundType typeAsLowerBound() {
            throw new IllegalStateException();
        }

        @Override
        BoundType typeAsUpperBound() {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        Cut<Comparable<?>> withLowerBoundType(BoundType boundtype, DiscreteDomain<Comparable<?>> discretedomain) {
            throw new IllegalStateException();
        }

        @Override
        Cut<Comparable<?>> withUpperBoundType(BoundType boundtype, DiscreteDomain<Comparable<?>> discretedomain) {
            throw new AssertionError("this statement should be unreachable");
        }

        @Override
        void describeAsLowerBound(StringBuilder stringbuilder) {
            stringbuilder.append("(-\u221e");
        }

        @Override
        void describeAsUpperBound(StringBuilder stringbuilder) {
            throw new AssertionError();
        }

        @Override
        Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> discretedomain) {
            return discretedomain.minValue();
        }

        @Override
        Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> discretedomain) {
            throw new AssertionError();
        }

        @Override
        Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> discretedomain) {
            try {
                return Cut.belowValue(discretedomain.minValue());
            } catch (NoSuchElementException nosuchelementexception) {
                return this;
            }
        }

        @Override
        public int compareTo(Cut<Comparable<?>> cut) {
            return cut == this ? 0 : -1;
        }

        public String toString() {
            return "-\u221e";
        }

        private Object readResolve() {
            return Cut.BelowAll.INSTANCE;
        }
    }
}
