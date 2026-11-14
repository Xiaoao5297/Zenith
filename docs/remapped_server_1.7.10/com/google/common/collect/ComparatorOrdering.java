package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {

    final Comparator<T> comparator;
    private static final long serialVersionUID = 0L;

    ComparatorOrdering(Comparator<T> comparator) {
        this.comparator = (Comparator) Preconditions.checkNotNull(comparator);
    }

    @Override
    public int compare(T t0, T t1) {
        return this.comparator.compare(t0, t1);
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof ComparatorOrdering) {
            ComparatorOrdering comparatorordering = (ComparatorOrdering) object;

            return this.comparator.equals(comparatorordering.comparator);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.comparator.hashCode();
    }

    public String toString() {
        return this.comparator.toString();
    }
}
