package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Predicates {

    private static final Joiner COMMA_JOINER = Joiner.on(",");

    private Predicates() {}

    @GwtCompatible(serializable = true)
    public static <T> Predicate<T> alwaysTrue() {
        return Predicates.ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }

    @GwtCompatible(serializable = true)
    public static <T> Predicate<T> alwaysFalse() {
        return Predicates.ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
    }

    @GwtCompatible(serializable = true)
    public static <T> Predicate<T> isNull() {
        return Predicates.ObjectPredicate.IS_NULL.withNarrowedType();
    }

    @GwtCompatible(serializable = true)
    public static <T> Predicate<T> notNull() {
        return Predicates.ObjectPredicate.NOT_NULL.withNarrowedType();
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return new Predicates.NotPredicate<>(predicate);
    }

    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> iterable) {
        return new Predicates.AndPredicate<>(defensiveCopy(iterable));
    }

    public static <T> Predicate<T> and(Predicate<? super T>... apredicate) {
        return new Predicates.AndPredicate<>(defensiveCopy((Object[]) apredicate));
    }

    public static <T> Predicate<T> and(Predicate<? super T> predicate, Predicate<? super T> predicate1) {
        return new Predicates.AndPredicate<>(asList((Predicate) Preconditions.checkNotNull(predicate), (Predicate) Preconditions.checkNotNull(predicate1)));
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> iterable) {
        return new Predicates.OrPredicate<>(defensiveCopy(iterable));
    }

    public static <T> Predicate<T> or(Predicate<? super T>... apredicate) {
        return new Predicates.OrPredicate<>(defensiveCopy((Object[]) apredicate));
    }

    public static <T> Predicate<T> or(Predicate<? super T> predicate, Predicate<? super T> predicate1) {
        return new Predicates.OrPredicate<>(asList((Predicate) Preconditions.checkNotNull(predicate), (Predicate) Preconditions.checkNotNull(predicate1)));
    }

    public static <T> Predicate<T> equalTo(@Nullable T t0) {
        return (Predicate) (t0 == null ? isNull() : new Predicates.IsEqualToPredicate<>(t0));
    }

    @GwtIncompatible("Class.isInstance")
    public static Predicate<Object> instanceOf(Class<?> oclass) {
        return new Predicates.InstanceOfPredicate(oclass);
    }

    @GwtIncompatible("Class.isAssignableFrom")
    @Beta
    public static Predicate<Class<?>> assignableFrom(Class<?> oclass) {
        return new Predicates.AssignableFromPredicate(oclass);
    }

    public static <T> Predicate<T> in(Collection<? extends T> collection) {
        return new Predicates.InPredicate<>(collection);
    }

    public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
        return new Predicates.CompositionPredicate<>(predicate, function);
    }

    @GwtIncompatible("java.util.regex.Pattern")
    public static Predicate<CharSequence> containsPattern(String s) {
        return new Predicates.ContainsPatternPredicate(s);
    }

    @GwtIncompatible("java.util.regex.Pattern")
    public static Predicate<CharSequence> contains(Pattern pattern) {
        return new Predicates.ContainsPatternPredicate(pattern);
    }

    private static <T> List<Predicate<? super T>> asList(Predicate<? super T> predicate, Predicate<? super T> predicate1) {
        return Arrays.asList(predicate, predicate1);
    }

    private static <T> List<T> defensiveCopy(T... at) {
        return defensiveCopy((Iterable) Arrays.asList(at));
    }

    static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            arraylist.add(Preconditions.checkNotNull(object));
        }

        return arraylist;
    }

    @GwtIncompatible("Only used by other GWT-incompatible code.")
    private static class ContainsPatternPredicate implements Predicate<CharSequence>, Serializable {

        final Pattern pattern;
        private static final long serialVersionUID = 0L;

        ContainsPatternPredicate(Pattern pattern) {
            this.pattern = (Pattern) Preconditions.checkNotNull(pattern);
        }

        ContainsPatternPredicate(String s) {
            this(Pattern.compile(s));
        }

        public boolean apply(CharSequence charsequence) {
            return this.pattern.matcher(charsequence).find();
        }

        public int hashCode() {
            return Objects.hashCode(this.pattern.pattern(), this.pattern.flags());
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Predicates.ContainsPatternPredicate)) {
                return false;
            } else {
                Predicates.ContainsPatternPredicate predicates_containspatternpredicate = (Predicates.ContainsPatternPredicate) object;

                return Objects.equal(this.pattern.pattern(), predicates_containspatternpredicate.pattern.pattern()) && Objects.equal(this.pattern.flags(), predicates_containspatternpredicate.pattern.flags());
            }
        }

        public String toString() {
            return Objects.toStringHelper((Object) this).add("pattern", this.pattern).add("pattern.flags", Integer.toHexString(this.pattern.flags())).toString();
        }
    }

    private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable {

        final Predicate<B> p;
        final Function<A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        private CompositionPredicate(Predicate<B> predicate, Function<A, ? extends B> function) {
            this.p = (Predicate) Preconditions.checkNotNull(predicate);
            this.f = (Function) Preconditions.checkNotNull(function);
        }

        @Override
        public boolean apply(@Nullable A a0) {
            return this.p.apply(this.f.apply(a0));
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Predicates.CompositionPredicate)) {
                return false;
            } else {
                Predicates.CompositionPredicate predicates_compositionpredicate = (Predicates.CompositionPredicate) object;

                return this.f.equals(predicates_compositionpredicate.f) && this.p.equals(predicates_compositionpredicate.p);
            }
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.p.hashCode();
        }

        public String toString() {
            return this.p.toString() + "(" + this.f.toString() + ")";
        }
    }

    private static class InPredicate<T> implements Predicate<T>, Serializable {

        private final Collection<?> target;
        private static final long serialVersionUID = 0L;

        private InPredicate(Collection<?> collection) {
            this.target = (Collection) Preconditions.checkNotNull(collection);
        }

        @Override
        public boolean apply(@Nullable T t0) {
            try {
                return this.target.contains(t0);
            } catch (NullPointerException nullpointerexception) {
                return false;
            } catch (ClassCastException classcastexception) {
                return false;
            }
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.InPredicate) {
                Predicates.InPredicate predicates_inpredicate = (Predicates.InPredicate) object;

                return this.target.equals(predicates_inpredicate.target);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.target.hashCode();
        }

        public String toString() {
            return "In(" + this.target + ")";
        }
    }

    @GwtIncompatible("Class.isAssignableFrom")
    private static class AssignableFromPredicate implements Predicate<Class<?>>, Serializable {

        private final Class<?> clazz;
        private static final long serialVersionUID = 0L;

        private AssignableFromPredicate(Class<?> oclass) {
            this.clazz = (Class) Preconditions.checkNotNull(oclass);
        }

        public boolean apply(Class<?> oclass) {
            return this.clazz.isAssignableFrom(oclass);
        }

        public int hashCode() {
            return this.clazz.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.AssignableFromPredicate) {
                Predicates.AssignableFromPredicate predicates_assignablefrompredicate = (Predicates.AssignableFromPredicate) object;

                return this.clazz == predicates_assignablefrompredicate.clazz;
            } else {
                return false;
            }
        }

        public String toString() {
            return "IsAssignableFrom(" + this.clazz.getName() + ")";
        }
    }

    @GwtIncompatible("Class.isInstance")
    private static class InstanceOfPredicate implements Predicate<Object>, Serializable {

        private final Class<?> clazz;
        private static final long serialVersionUID = 0L;

        private InstanceOfPredicate(Class<?> oclass) {
            this.clazz = (Class) Preconditions.checkNotNull(oclass);
        }

        @Override
        public boolean apply(@Nullable Object object) {
            return this.clazz.isInstance(object);
        }

        public int hashCode() {
            return this.clazz.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.InstanceOfPredicate) {
                Predicates.InstanceOfPredicate predicates_instanceofpredicate = (Predicates.InstanceOfPredicate) object;

                return this.clazz == predicates_instanceofpredicate.clazz;
            } else {
                return false;
            }
        }

        public String toString() {
            return "IsInstanceOf(" + this.clazz.getName() + ")";
        }
    }

    private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable {

        private final T target;
        private static final long serialVersionUID = 0L;

        private IsEqualToPredicate(T t0) {
            this.target = t0;
        }

        @Override
        public boolean apply(T t0) {
            return this.target.equals(t0);
        }

        public int hashCode() {
            return this.target.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.IsEqualToPredicate) {
                Predicates.IsEqualToPredicate predicates_isequaltopredicate = (Predicates.IsEqualToPredicate) object;

                return this.target.equals(predicates_isequaltopredicate.target);
            } else {
                return false;
            }
        }

        public String toString() {
            return "IsEqualTo(" + this.target + ")";
        }
    }

    private static class OrPredicate<T> implements Predicate<T>, Serializable {

        private final List<? extends Predicate<? super T>> components;
        private static final long serialVersionUID = 0L;

        private OrPredicate(List<? extends Predicate<? super T>> list) {
            this.components = list;
        }

        @Override
        public boolean apply(@Nullable T t0) {
            for (int i = 0; i < this.components.size(); ++i) {
                if (((Predicate) this.components.get(i)).apply(t0)) {
                    return true;
                }
            }

            return false;
        }

        public int hashCode() {
            return this.components.hashCode() + 87855567;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.OrPredicate) {
                Predicates.OrPredicate predicates_orpredicate = (Predicates.OrPredicate) object;

                return this.components.equals(predicates_orpredicate.components);
            } else {
                return false;
            }
        }

        public String toString() {
            return "Or(" + Predicates.COMMA_JOINER.join((Iterable) this.components) + ")";
        }
    }

    private static class AndPredicate<T> implements Predicate<T>, Serializable {

        private final List<? extends Predicate<? super T>> components;
        private static final long serialVersionUID = 0L;

        private AndPredicate(List<? extends Predicate<? super T>> list) {
            this.components = list;
        }

        @Override
        public boolean apply(@Nullable T t0) {
            for (int i = 0; i < this.components.size(); ++i) {
                if (!((Predicate) this.components.get(i)).apply(t0)) {
                    return false;
                }
            }

            return true;
        }

        public int hashCode() {
            return this.components.hashCode() + 306654252;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.AndPredicate) {
                Predicates.AndPredicate predicates_andpredicate = (Predicates.AndPredicate) object;

                return this.components.equals(predicates_andpredicate.components);
            } else {
                return false;
            }
        }

        public String toString() {
            return "And(" + Predicates.COMMA_JOINER.join((Iterable) this.components) + ")";
        }
    }

    private static class NotPredicate<T> implements Predicate<T>, Serializable {

        final Predicate<T> predicate;
        private static final long serialVersionUID = 0L;

        NotPredicate(Predicate<T> predicate) {
            this.predicate = (Predicate) Preconditions.checkNotNull(predicate);
        }

        @Override
        public boolean apply(@Nullable T t0) {
            return !this.predicate.apply(t0);
        }

        public int hashCode() {
            return ~this.predicate.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof Predicates.NotPredicate) {
                Predicates.NotPredicate predicates_notpredicate = (Predicates.NotPredicate) object;

                return this.predicate.equals(predicates_notpredicate.predicate);
            } else {
                return false;
            }
        }

        public String toString() {
            return "Not(" + this.predicate.toString() + ")";
        }
    }

    static enum ObjectPredicate implements Predicate<Object> {

        ALWAYS_TRUE {
            @Override
            public boolean apply(@Nullable Object object) {
                return true;
            }
        },
        ALWAYS_FALSE {
            @Override
            public boolean apply(@Nullable Object object) {
                return false;
            }
        },
        IS_NULL {
            @Override
            public boolean apply(@Nullable Object object) {
                return object == null;
            }
        },
        NOT_NULL {
            @Override
            public boolean apply(@Nullable Object object) {
                return object != null;
            }
        };

        private ObjectPredicate() {}

        <T> Predicate<T> withNarrowedType() {
            return this;
        }
    }
}
