package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {

    private final transient ImmutableMap<E, Integer> map;
    private final transient int size;

    RegularImmutableMultiset(ImmutableMap<E, Integer> immutablemap, int i) {
        this.map = immutablemap;
        this.size = i;
    }

    @Override
    boolean isPartialView() {
        return this.map.isPartialView();
    }

    @Override
    public int count(@Nullable Object object) {
        Integer integer = (Integer) this.map.get(object);

        return integer == null ? 0 : integer;
    }

    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(@Nullable Object object) {
        return this.map.containsKey(object);
    }

    @Override
    public ImmutableSet<E> elementSet() {
        return this.map.keySet();
    }

    @Override
    Multiset.Entry<E> getEntry(int i) {
        java.util.Map.Entry java_util_map_entry = (java.util.Map.Entry) this.map.entrySet().asList().get(i);

        return Multisets.immutableEntry(java_util_map_entry.getKey(), (Integer) java_util_map_entry.getValue());
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
}
