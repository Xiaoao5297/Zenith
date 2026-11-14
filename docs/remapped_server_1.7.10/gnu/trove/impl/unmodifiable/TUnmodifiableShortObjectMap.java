package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableShortObjectMap<V> implements TShortObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortObjectMap<V> m;
    private transient TShortSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableShortObjectMap(TShortObjectMap<V> tshortobjectmap) {
        if (tshortobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortobjectmap;
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
    public boolean containsKey(short short0) {
        return this.m.containsKey(short0);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public V put(short short0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortObjectMap<? extends V> tshortobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TShortSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public short[] keys() {
        return this.m.keys();
    }

    @Override
    public short[] keys(short[] ashort) {
        return this.m.keys(ashort);
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TShortObjectProcedure<? super V> tshortobjectprocedure) {
        return this.m.forEachEntry(tshortobjectprocedure);
    }

    @Override
    public TShortObjectIterator<V> iterator() {
        return new TShortObjectIterator<V>() {
            TShortObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableShortObjectMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public V putIfAbsent(short short0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortObjectProcedure<? super V> tshortobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
