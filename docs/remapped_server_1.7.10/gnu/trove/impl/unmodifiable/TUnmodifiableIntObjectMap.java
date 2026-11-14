package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableIntObjectMap<V> implements TIntObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntObjectMap<V> m;
    private transient TIntSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableIntObjectMap(TIntObjectMap<V> tintobjectmap) {
        if (tintobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintobjectmap;
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
    public boolean containsKey(int i) {
        return this.m.containsKey(i);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(int i) {
        return this.m.get(i);
    }

    @Override
    public V put(int i, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntObjectMap<? extends V> tintobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TIntSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public int[] keys() {
        return this.m.keys();
    }

    @Override
    public int[] keys(int[] aint) {
        return this.m.keys(aint);
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TIntObjectProcedure<? super V> tintobjectprocedure) {
        return this.m.forEachEntry(tintobjectprocedure);
    }

    @Override
    public TIntObjectIterator<V> iterator() {
        return new TIntObjectIterator<V>() {
            TIntObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableIntObjectMap.this.m.iterator();
            }

            @Override
            public int key() {
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
    public V putIfAbsent(int i, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntObjectProcedure<? super V> tintobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
