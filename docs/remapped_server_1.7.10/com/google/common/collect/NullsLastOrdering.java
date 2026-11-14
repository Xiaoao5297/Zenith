package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class NullsLastOrdering<T> extends Ordering<T> implements Serializable {

    final Ordering<? super T> ordering;
    private static final long serialVersionUID = 0L;

    NullsLastOrdering(Ordering<? super T> ordering) {
        this.ordering = ordering;
    }

    @Override
    public int compare(@Nullable T t0, @Nullable T t1) {
        return t0 == t1 ? 0 : (t0 == null ? 1 : (t1 == null ? -1 : this.ordering.compare(t0, t1)));
    }

    @Override
    public <S extends T> Ordering<S> reverse() {
        return this.ordering.reverse().nullsFirst();
    }

    @Override
    public <S extends T> Ordering<S> nullsFirst() {
        return this.ordering.nullsFirst();
    }

    @Override
    public <S extends T> Ordering<S> nullsLast() {
        return this;
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof NullsLastOrdering) {
            NullsLastOrdering nullslastordering = (NullsLastOrdering) object;

            return this.ordering.equals(nullslastordering.ordering);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.ordering.hashCode() ^ -921210296;
    }

    public String toString() {
        return this.ordering + ".nullsLast()";
    }
}
