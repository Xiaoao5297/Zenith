package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E> implements SortedIterable<E> {

    ImmutableSortedAsList(ImmutableSortedSet<E> immutablesortedset, ImmutableList<E> immutablelist) {
        super(immutablesortedset, immutablelist);
    }

    @Override
    ImmutableSortedSet<E> delegateCollection() {
        return (ImmutableSortedSet) super.delegateCollection();
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.delegateCollection().comparator();
    }

    @GwtIncompatible("ImmutableSortedSet.indexOf")
    @Override
    public int indexOf(@Nullable Object object) {
        int i = this.delegateCollection().indexOf(object);

        return i >= 0 && this.get(i).equals(object) ? i : -1;
    }

    @GwtIncompatible("ImmutableSortedSet.indexOf")
    @Override
    public int lastIndexOf(@Nullable Object object) {
        return this.indexOf(object);
    }

    @Override
    public boolean contains(Object object) {
        return this.indexOf(object) >= 0;
    }

    @GwtIncompatible("super.subListUnchecked does not exist; inherited subList is valid if slow")
    @Override
    ImmutableList<E> subListUnchecked(int i, int j) {
        return (new RegularImmutableSortedSet<>(super.subListUnchecked(i, j), this.comparator())).asList();
    }
}
