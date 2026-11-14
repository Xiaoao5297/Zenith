package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface BiMap<K, V> extends Map<K, V> {

    V put(@Nullable K k0, @Nullable V v0);

    V forcePut(@Nullable K k0, @Nullable V v0);

    void putAll(Map<? extends K, ? extends V> map);

    Set<V> values();

    BiMap<V, K> inverse();
}
