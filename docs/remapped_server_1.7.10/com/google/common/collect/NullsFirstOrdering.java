package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable {

    final Ordering<? super T> ordering;
    private static final long serialVersionUID = 0L;

    NullsFirstOrdering(Ordering<? super T> ordering) {
        this.ordering = ordering;
    }

    @Override
    public int compare(@Nullable T t0, @Nullable T t1) {
        return t0 == t1 ? 0 : (t0 == null ? -1 : (t1 == null ? 1 : this.ordering.compare(t0, t1)));
    }

    @Override
    public <S extends T> Ordering<S> reverse() {
        return this.ordering.reverse().nullsLast();
    }

    @Override
    public <S extends T> Ordering<S> nullsFirst() {
        return this;
    }

    @Override
    public <S extends T> Ordering<S> nullsLast() {
        return this.ordering.nullsLast();
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof NullsFirstOrdering) {
            NullsFirstOrdering nullsfirstordering = (NullsFirstOrdering) object;

            return this.ordering.equals(nullsfirstordering.ordering);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.ordering.hashCode() ^ 957692532;
    }

    public String toString() {
        return this.ordering + ".nullsFirst()";
    }
}
