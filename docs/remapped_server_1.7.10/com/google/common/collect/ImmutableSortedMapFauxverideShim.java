package com.google.common.collect;

abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V> {

    ImmutableSortedMapFauxverideShim() {}

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(K k0, V v0) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3) {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        throw new UnsupportedOperationException();
    }
}
