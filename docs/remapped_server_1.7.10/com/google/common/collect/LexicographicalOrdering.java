package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class LexicographicalOrdering<T> extends Ordering<Iterable<T>> implements Serializable {

    final Ordering<? super T> elementOrder;
    private static final long serialVersionUID = 0L;

    LexicographicalOrdering(Ordering<? super T> ordering) {
        this.elementOrder = ordering;
    }

    public int compare(Iterable<T> iterable, Iterable<T> iterable1) {
        Iterator iterator = iterable.iterator();
        Iterator iterator1 = iterable1.iterator();

        int i;

        do {
            if (!iterator.hasNext()) {
                if (iterator1.hasNext()) {
                    return -1;
                }

                return 0;
            }

            if (!iterator1.hasNext()) {
                return 1;
            }

            i = this.elementOrder.compare(iterator.next(), iterator1.next());
        } while (i == 0);

        return i;
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof LexicographicalOrdering) {
            LexicographicalOrdering lexicographicalordering = (LexicographicalOrdering) object;

            return this.elementOrder.equals(lexicographicalordering.elementOrder);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.elementOrder.hashCode() ^ 2075626741;
    }

    public String toString() {
        return this.elementOrder + ".lexicographical()";
    }
}
