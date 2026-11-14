package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
final class WellBehavedMap<K, V> extends ForwardingMap<K, V> {

    private final Map<K, V> delegate;
    private Set<Entry<K, V>> entrySet;

    private WellBehavedMap(Map<K, V> map) {
        this.delegate = map;
    }

    static <K, V> WellBehavedMap<K, V> wrap(Map<K, V> map) {
        return new WellBehavedMap<>(map);
    }

    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set set = this.entrySet;

        return set != null ? set : (this.entrySet = new WellBehavedMap.EntrySet());
    }

    private final class EntrySet extends Maps.EntrySet<K, V> {

        private EntrySet() {}

        @Override
        Map<K, V> map() {
            return WellBehavedMap.this;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new TransformedIterator<K, Entry<K, V>>(WellBehavedMap.this.keySet().iterator()) {
                @Override
                Entry<K, V> transform(final K k0) {
                    return new AbstractMapEntry<K, V>() {
                        @Override
                        public K getKey() {
                            return k0;
                        }

                        @Override
                        public V getValue() {
                            return WellBehavedMap.this.get(k0);
                        }

                        @Override
                        public V setValue(V v0) {
                            return WellBehavedMap.this.put(k0, v0);
                        }
                    };
                }
            };
        }
    }
}
