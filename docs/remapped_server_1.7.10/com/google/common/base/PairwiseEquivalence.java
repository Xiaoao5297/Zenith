package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
final class PairwiseEquivalence<T> extends Equivalence<Iterable<T>> implements Serializable {

    final Equivalence<? super T> elementEquivalence;
    private static final long serialVersionUID = 1L;

    PairwiseEquivalence(Equivalence<? super T> equivalence) {
        this.elementEquivalence = (Equivalence) Preconditions.checkNotNull(equivalence);
    }

    protected boolean doEquivalent(Iterable<T> iterable, Iterable<T> iterable1) {
        Iterator iterator = iterable.iterator();
        Iterator iterator1 = iterable1.iterator();

        while (iterator.hasNext() && iterator1.hasNext()) {
            if (!this.elementEquivalence.equivalent(iterator.next(), iterator1.next())) {
                return false;
            }
        }

        return !iterator.hasNext() && !iterator1.hasNext();
    }

    protected int doHash(Iterable<T> iterable) {
        int i = 78721;

        Object object;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); i = i * 24943 + this.elementEquivalence.hash(object)) {
            object = iterator.next();
        }

        return i;
    }

    public boolean equals(@Nullable Object object) {
        if (object instanceof PairwiseEquivalence) {
            PairwiseEquivalence pairwiseequivalence = (PairwiseEquivalence) object;

            return this.elementEquivalence.equals(pairwiseequivalence.elementEquivalence);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.elementEquivalence.hashCode() ^ 1185147655;
    }

    public String toString() {
        return this.elementEquivalence + ".pairwise()";
    }
}
