package com.google.common.collect;

abstract class ImmutableSortedSetFauxverideShim<E> extends ImmutableSet<E> {

    ImmutableSortedSetFauxverideShim() {}

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet.Builder<E> builder() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0, E e1) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0, E e1, E e2) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3, E e4) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e0, E e1, E e2, E e3, E e4, E e5, E... ae) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <E> ImmutableSortedSet<E> copyOf(E[] ae) {
        throw new UnsupportedOperationException();
    }
}
