package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Equivalence<T> {

    protected Equivalence() {}

    public final boolean equivalent(@Nullable T t0, @Nullable T t1) {
        return t0 == t1 ? true : (t0 != null && t1 != null ? this.doEquivalent(t0, t1) : false);
    }

    protected abstract boolean doEquivalent(T t0, T t1);

    public final int hash(@Nullable T t0) {
        return t0 == null ? 0 : this.doHash(t0);
    }

    protected abstract int doHash(T t0);

    public final <F> Equivalence<F> onResultOf(Function<F, ? extends T> function) {
        return new FunctionalEquivalence<>(function, this);
    }

    public final <S extends T> Equivalence.Wrapper<S> wrap(@Nullable S s0) {
        return new Equivalence.Wrapper<>(this, s0);
    }

    @GwtCompatible(serializable = true)
    public final <S extends T> Equivalence<Iterable<S>> pairwise() {
        return new PairwiseEquivalence<>(this);
    }

    @Beta
    public final Predicate<T> equivalentTo(@Nullable T t0) {
        return new Equivalence.EquivalentToPredicate<>(this, t0);
    }

    public static Equivalence<Object> equals() {
        return Equivalence.Equals.INSTANCE;
    }

    public static Equivalence<Object> identity() {
        return Equivalence.Identity.INSTANCE;
    }

    static final class Identity extends Equivalence<Object> implements Serializable {

        static final Equivalence.Identity INSTANCE = new Equivalence.Identity();
        private static final long serialVersionUID = 1L;

        Identity() {}

        @Override
        protected boolean doEquivalent(Object object, Object object1) {
            return false;
        }

        @Override
        protected int doHash(Object object) {
            return System.identityHashCode(object);
        }

        private Object readResolve() {
            return Equivalence.Identity.INSTANCE;
        }
    }

    static final class Equals extends Equivalence<Object> implements Serializable {

        static final Equivalence.Equals INSTANCE = new Equivalence.Equals();
        private static final long serialVersionUID = 1L;

        Equals() {}

        @Override
        protected boolean doEquivalent(Object object, Object object1) {
            return object.equals(object1);
        }

        @Override
        public int doHash(Object object) {
            return object.hashCode();
        }

        private Object readResolve() {
            return Equivalence.Equals.INSTANCE;
        }
    }

    private static final class EquivalentToPredicate<T> implements Predicate<T>, Serializable {

        private final Equivalence<T> equivalence;
        @Nullable
        private final T target;
        private static final long serialVersionUID = 0L;

        EquivalentToPredicate(Equivalence<T> equivalence, @Nullable T t0) {
            this.equivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
            this.target = t0;
        }

        @Override
        public boolean apply(@Nullable T t0) {
            return this.equivalence.equivalent(t0, this.target);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof Equivalence.EquivalentToPredicate)) {
                return false;
            } else {
                Equivalence.EquivalentToPredicate equivalence_equivalenttopredicate = (Equivalence.EquivalentToPredicate) object;

                return this.equivalence.equals(equivalence_equivalenttopredicate.equivalence) && Objects.equal(this.target, equivalence_equivalenttopredicate.target);
            }
        }

        public int hashCode() {
            return Objects.hashCode(this.equivalence, this.target);
        }

        public String toString() {
            return this.equivalence + ".equivalentTo(" + this.target + ")";
        }
    }

    public static final class Wrapper<T> implements Serializable {

        private final Equivalence<? super T> equivalence;
        @Nullable
        private final T reference;
        private static final long serialVersionUID = 0L;

        private Wrapper(Equivalence<? super T> equivalence, @Nullable T t0) {
            this.equivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
            this.reference = t0;
        }

        @Nullable
        public T get() {
            return this.reference;
        }

        public boolean equals(@Nullable Object object) {
            if (object == this) {
                return true;
            } else {
                if (object instanceof Equivalence.Wrapper) {
                    Equivalence.Wrapper equivalence_wrapper = (Equivalence.Wrapper) object;

                    if (this.equivalence.equals(equivalence_wrapper.equivalence)) {
                        Equivalence equivalence = this.equivalence;

                        return equivalence.equivalent(this.reference, equivalence_wrapper.reference);
                    }
                }

                return false;
            }
        }

        public int hashCode() {
            return this.equivalence.hash(this.reference);
        }

        public String toString() {
            return this.equivalence + ".wrap(" + this.reference + ")";
        }
    }
}
