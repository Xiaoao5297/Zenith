package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableLongObjectMap<V> implements TLongObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongObjectMap<V> m;
    private transient TLongSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableLongObjectMap(TLongObjectMap<V> tlongobjectmap) {
        if (tlongobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongobjectmap;
        }
    }

    @Override
    public int size() {
        return this.m.size();
    }

    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    @Override
    public boolean containsKey(long i) {
        return this.m.containsKey(i);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(long i) {
        return this.m.get(i);
    }

    @Override
    public V put(long i, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongObjectMap<? extends V> tlongobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TLongSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public long[] keys() {
        return this.m.keys();
    }

    @Override
    public long[] keys(long[] along) {
        return this.m.keys(along);
    }

    @Override
    public Collection<V> valueCollection() {
        if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public Object[] values() {
        return this.m.values();
    }

    @Override
    public V[] values(V[] av) {
        return this.m.values(av);
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.m.equals(object);
    }

    @Override
    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.m.forEachKey(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        return this.m.forEachEntry(tlongobjectprocedure);
    }

    @Override
    public TLongObjectIterator<V> iterator() {
        return new TLongObjectIterator<V>() {
            TLongObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableLongObjectMap.this.m.iterator();
            }

            @Override
            public long key() {
                return this.iter.key();
            }

            @Override
            public V value() {
                return this.iter.value();
            }

            @Override
            public void advance() {
                this.iter.advance();
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public V setValue(V v0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public V putIfAbsent(long i, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
