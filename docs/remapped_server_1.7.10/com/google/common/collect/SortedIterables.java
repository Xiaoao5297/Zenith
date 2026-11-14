package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.SortedSet;

@GwtCompatible
final class SortedIterables {

    private SortedIterables() {}

    public static boolean hasSameComparator(Comparator<?> comparator, Iterable<?> iterable) {
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(iterable);
        Comparator comparator1;

        if (iterable instanceof SortedSet) {
            comparator1 = comparator((SortedSet) iterable);
        } else {
            if (!(iterable instanceof SortedIterable)) {
                return false;
            }

            comparator1 = ((SortedIterable) iterable).comparator();
        }

        return comparator.equals(comparator1);
    }

    public static <E> Comparator<? super E> comparator(SortedSet<E> sortedset) {
        Object object = sortedset.comparator();

        if (object == null) {
            object = Ordering.natural();
        }

        return (Comparator) object;
    }
}
