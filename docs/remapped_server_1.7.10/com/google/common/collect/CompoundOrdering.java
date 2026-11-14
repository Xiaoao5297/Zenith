package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Comparator;

@GwtCompatible(serializable = true)
final class CompoundOrdering<T> extends Ordering<T> implements Serializable {

    final ImmutableList<Comparator<? super T>> comparators;
    private static final long serialVersionUID = 0L;

    CompoundOrdering(Comparator<? super T> comparator, Comparator<? super T> comparator1) {
        this.comparators = ImmutableList.of(comparator, comparator1);
    }

    CompoundOrdering(Iterable<? extends Comparator<? super T>> iterable) {
        this.comparators = ImmutableList.copyOf(iterable);
    }

    @Override
    public int compare(T t0, T t1) {
        int i = this.comparators.size();

        for (int j = 0; j < i; ++j) {
            int k = ((Comparator) this.comparators.get(j)).compare(t0, t1);

            if (k != 0) {
                return k;
            }
        }

        return 0;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof CompoundOrdering) {
            CompoundOrdering compoundordering = (CompoundOrdering) object;

            return this.comparators.equals(compoundordering.comparators);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.comparators.hashCode();
    }

    public String toString() {
        return "Ordering.compound(" + this.comparators + ")";
    }
}
