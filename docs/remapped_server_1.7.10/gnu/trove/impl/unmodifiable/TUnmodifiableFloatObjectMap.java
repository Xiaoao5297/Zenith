package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableFloatObjectMap<V> implements TFloatObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatObjectMap<V> m;
    private transient TFloatSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableFloatObjectMap(TFloatObjectMap<V> tfloatobjectmap) {
        if (tfloatobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatobjectmap;
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
    public boolean containsKey(float f) {
        return this.m.containsKey(f);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(float f) {
        return this.m.get(f);
    }

    @Override
    public V put(float f, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatObjectMap<? extends V> tfloatobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TFloatSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public float[] keys() {
        return this.m.keys();
    }

    @Override
    public float[] keys(float[] afloat) {
        return this.m.keys(afloat);
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatObjectProcedure<? super V> tfloatobjectprocedure) {
        return this.m.forEachEntry(tfloatobjectprocedure);
    }

    @Override
    public TFloatObjectIterator<V> iterator() {
        return new TFloatObjectIterator<V>() {
            TFloatObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableFloatObjectMap.this.m.iterator();
            }

            @Override
            public float key() {
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
    public V putIfAbsent(float f, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatObjectProcedure<? super V> tfloatobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
