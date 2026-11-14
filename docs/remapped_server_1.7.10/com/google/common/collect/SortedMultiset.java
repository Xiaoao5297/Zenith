package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@Beta
@GwtCompatible(emulated = true)
public interface SortedMultiset<E> extends SortedMultisetBridge<E>, SortedIterable<E> {

    @Override
    Comparator<? super E> comparator();

    Multiset.Entry<E> firstEntry();

    Multiset.Entry<E> lastEntry();

    Multiset.Entry<E> pollFirstEntry();

    Multiset.Entry<E> pollLastEntry();

    @Override
    NavigableSet<E> elementSet();

    @Override
    Iterator<E> iterator();

    SortedMultiset<E> descendingMultiset();

    SortedMultiset<E> headMultiset(E e0, BoundType boundtype);

    SortedMultiset<E> subMultiset(E e0, BoundType boundtype, E e1, BoundType boundtype1);

    SortedMultiset<E> tailMultiset(E e0, BoundType boundtype);
}
