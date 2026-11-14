package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E> {

    protected ForwardingSortedSet() {}

    @Override
    protected abstract SortedSet<E> delegate();

    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }

    public E first() {
        return this.delegate().first();
    }

    public SortedSet<E> headSet(E e0) {
        return this.delegate().headSet(e0);
    }

    public E last() {
        return this.delegate().last();
    }

    public SortedSet<E> subSet(E e0, E e1) {
        return this.delegate().subSet(e0, e1);
    }

    public SortedSet<E> tailSet(E e0) {
        return this.delegate().tailSet(e0);
    }

    private int unsafeCompare(Object object, Object object1) {
        Comparator comparator = this.comparator();

        return comparator == null ? ((Comparable) object).compareTo(object1) : comparator.compare(object, object1);
    }

    @Beta
    @Override
    protected boolean standardContains(@Nullable Object object) {
        try {
            Object object1 = this.tailSet(object).first();

            return this.unsafeCompare(object1, object) == 0;
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NoSuchElementException nosuchelementexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    @Beta
    @Override
    protected boolean standardRemove(@Nullable Object object) {
        try {
            Iterator iterator = this.tailSet(object).iterator();

            if (iterator.hasNext()) {
                Object object1 = iterator.next();

                if (this.unsafeCompare(object1, object) == 0) {
                    iterator.remove();
                    return true;
                }
            }

            return false;
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    @Beta
    protected SortedSet<E> standardSubSet(E e0, E e1) {
        return this.tailSet(e0).headSet(e1);
    }
}
