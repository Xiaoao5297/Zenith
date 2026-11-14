package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V> {

    FilteredKeyListMultimap(ListMultimap<K, V> listmultimap, Predicate<? super K> predicate) {
        super(listmultimap, predicate);
    }

    @Override
    public ListMultimap<K, V> unfiltered() {
        return (ListMultimap) super.unfiltered();
    }

    @Override
    public List<V> get(K k0) {
        return (List) super.get(k0);
    }

    @Override
    public List<V> removeAll(@Nullable Object object) {
        return (List) super.removeAll(object);
    }

    @Override
    public List<V> replaceValues(K k0, Iterable<? extends V> iterable) {
        return (List) super.replaceValues(k0, iterable);
    }
}
