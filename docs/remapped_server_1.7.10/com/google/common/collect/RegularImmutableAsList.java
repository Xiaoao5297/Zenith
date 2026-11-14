package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;

@GwtCompatible(emulated = true)
class RegularImmutableAsList<E> extends ImmutableAsList<E> {

    private final ImmutableCollection<E> delegate;
    private final ImmutableList<? extends E> delegateList;

    RegularImmutableAsList(ImmutableCollection<E> immutablecollection, ImmutableList<? extends E> immutablelist) {
        this.delegate = immutablecollection;
        this.delegateList = immutablelist;
    }

    RegularImmutableAsList(ImmutableCollection<E> immutablecollection, Object[] aobject) {
        this(immutablecollection, ImmutableList.asImmutableList(aobject));
    }

    @Override
    ImmutableCollection<E> delegateCollection() {
        return this.delegate;
    }

    ImmutableList<? extends E> delegateList() {
        return this.delegateList;
    }

    @Override
    public UnmodifiableListIterator<E> listIterator(int i) {
        return this.delegateList.listIterator(i);
    }

    @GwtIncompatible("not present in emulated superclass")
    @Override
    int copyIntoArray(Object[] aobject, int i) {
        return this.delegateList.copyIntoArray(aobject, i);
    }

    public E get(int i) {
        return this.delegateList.get(i);
    }
}
