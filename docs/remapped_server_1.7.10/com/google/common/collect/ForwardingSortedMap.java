package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V> {

    protected ForwardingSortedMap() {}

    @Override
    protected abstract SortedMap<K, V> delegate();

    public Comparator<? super K> comparator() {
        return this.delegate().comparator();
    }

    public K firstKey() {
        return this.delegate().firstKey();
    }

    public SortedMap<K, V> headMap(K k0) {
        return this.delegate().headMap(k0);
    }

    public K lastKey() {
        return this.delegate().lastKey();
    }

    public SortedMap<K, V> subMap(K k0, K k1) {
        return this.delegate().subMap(k0, k1);
    }

    public SortedMap<K, V> tailMap(K k0) {
        return this.delegate().tailMap(k0);
    }

    private int unsafeCompare(Object object, Object object1) {
        Comparator comparator = this.comparator();

        return comparator == null ? ((Comparable) object).compareTo(object1) : comparator.compare(object, object1);
    }

    @Beta
    @Override
    protected boolean standardContainsKey(@Nullable Object object) {
        try {
            Object object1 = this.tailMap(object).firstKey();

            return this.unsafeCompare(object1, object) == 0;
        } catch (ClassCastException classcastexception) {
            return false;
        } catch (NoSuchElementException nosuchelementexception) {
            return false;
        } catch (NullPointerException nullpointerexception) {
            return false;
        }
    }

    @Beta
    protected SortedMap<K, V> standardSubMap(K k0, K k1) {
        Preconditions.checkArgument(this.unsafeCompare(k0, k1) <= 0, "fromKey must be <= toKey");
        return this.tailMap(k0).headMap(k1);
    }

    @Beta
    protected class StandardKeySet extends Maps.SortedKeySet<K, V> {

        public StandardKeySet() {
            super((SortedMap) ForwardingSortedMap.this);
        }
    }
}
