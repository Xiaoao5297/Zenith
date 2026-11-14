package com.google.common.collect;

abstract class ImmutableSortedMultisetFauxverideShim<E> extends ImmutableMultiset<E> {

    ImmutableSortedMultisetFauxverideShim() {}

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0, E e1) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0, E e1, E e2) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3, E e4) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedMultiset<E> copyOf(E[] ae) {
        throw new UnsupportedOperationException();
    }
}
