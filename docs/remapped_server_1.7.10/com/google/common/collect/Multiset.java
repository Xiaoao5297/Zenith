package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multiset<E> extends Collection<E> {

    int count(@Nullable Object object);

    int add(@Nullable E e0, int i);

    int remove(@Nullable Object object, int i);

    int setCount(E e0, int i);

    boolean setCount(E e0, int i, int j);

    Set<E> elementSet();

    Set<Multiset.Entry<E>> entrySet();

    boolean equals(@Nullable Object object);

    int hashCode();

    String toString();

    Iterator<E> iterator();

    boolean contains(@Nullable Object object);

    boolean containsAll(Collection<?> collection);

    boolean add(E e0);

    boolean remove(@Nullable Object object);

    boolean removeAll(Collection<?> collection);

    boolean retainAll(Collection<?> collection);

    public interface Entry<E> {

        E getElement();

        int getCount();

        boolean equals(Object object);

        int hashCode();

        String toString();
    }
}
