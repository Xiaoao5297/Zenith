package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;

final class SoftReferenceMap<K, V> extends ReferenceMap<K, V> {

    public SoftReferenceMap(Map<K, Reference<V>> map) {
        super(map);
    }

    @Override
    Reference<V> fold(V v0) {
        return new SoftReference(v0);
    }
}
