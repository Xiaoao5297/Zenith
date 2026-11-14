package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableDoubleObjectMap<V> implements TDoubleObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleObjectMap<V> m;
    private transient TDoubleSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableDoubleObjectMap(TDoubleObjectMap<V> tdoubleobjectmap) {
        if (tdoubleobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubleobjectmap;
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
    public boolean containsKey(double d0) {
        return this.m.containsKey(d0);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(double d0) {
        return this.m.get(d0);
    }

    @Override
    public V put(double d0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleObjectMap<? extends V> tdoubleobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TDoubleSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public double[] keys() {
        return this.m.keys();
    }

    @Override
    public double[] keys(double[] adouble) {
        return this.m.keys(adouble);
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachKey(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure) {
        return this.m.forEachEntry(tdoubleobjectprocedure);
    }

    @Override
    public TDoubleObjectIterator<V> iterator() {
        return new TDoubleObjectIterator<V>() {
            TDoubleObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableDoubleObjectMap.this.m.iterator();
            }

            @Override
            public double key() {
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
    public V putIfAbsent(double d0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
