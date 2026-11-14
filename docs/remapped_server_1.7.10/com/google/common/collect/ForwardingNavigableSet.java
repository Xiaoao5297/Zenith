package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E> {

    protected ForwardingNavigableSet() {}

    @Override
    protected abstract NavigableSet<E> delegate();

    public E lower(E e0) {
        return this.delegate().lower(e0);
    }

    protected E standardLower(E e0) {
        return Iterators.getNext(this.headSet(e0, false).descendingIterator(), (Object) null);
    }

    public E floor(E e0) {
        return this.delegate().floor(e0);
    }

    protected E standardFloor(E e0) {
        return Iterators.getNext(this.headSet(e0, true).descendingIterator(), (Object) null);
    }

    public E ceiling(E e0) {
        return this.delegate().ceiling(e0);
    }

    protected E standardCeiling(E e0) {
        return Iterators.getNext(this.tailSet(e0, true).iterator(), (Object) null);
    }

    public E higher(E e0) {
        return this.delegate().higher(e0);
    }

    protected E standardHigher(E e0) {
        return Iterators.getNext(this.tailSet(e0, false).iterator(), (Object) null);
    }

    public E pollFirst() {
        return this.delegate().pollFirst();
    }

    protected E standardPollFirst() {
        return Iterators.pollNext(this.iterator());
    }

    public E pollLast() {
        return this.delegate().pollLast();
    }

    protected E standardPollLast() {
        return Iterators.pollNext(this.descendingIterator());
    }

    protected E standardFirst() {
        return this.iterator().next();
    }

    protected E standardLast() {
        return this.descendingIterator().next();
    }

    public NavigableSet<E> descendingSet() {
        return this.delegate().descendingSet();
    }

    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }

    public NavigableSet<E> subSet(E e0, boolean flag, E e1, boolean flag1) {
        return this.delegate().subSet(e0, flag, e1, flag1);
    }

    @Beta
    protected NavigableSet<E> standardSubSet(E e0, boolean flag, E e1, boolean flag1) {
        return this.tailSet(e0, flag).headSet(e1, flag1);
    }

    @Override
    protected SortedSet<E> standardSubSet(E e0, E e1) {
        return this.subSet(e0, true, e1, false);
    }

    public NavigableSet<E> headSet(E e0, boolean flag) {
        return this.delegate().headSet(e0, flag);
    }

    protected SortedSet<E> standardHeadSet(E e0) {
        return this.headSet(e0, false);
    }

    public NavigableSet<E> tailSet(E e0, boolean flag) {
        return this.delegate().tailSet(e0, flag);
    }

    protected SortedSet<E> standardTailSet(E e0) {
        return this.tailSet(e0, true);
    }

    @Beta
    protected class StandardDescendingSet extends Sets.DescendingSet<E> {

        public StandardDescendingSet() {
            super(ForwardingNavigableSet.this);
        }
    }
}
