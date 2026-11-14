package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectFloatMap<K> implements TObjectFloatMap<K>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectFloatMap<K> m;
    private transient Set<K> keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableObjectFloatMap(TObjectFloatMap<K> tobjectfloatmap) {
        if (tobjectfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectfloatmap;
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
    public boolean containsKey(Object object) {
        return this.m.containsKey(object);
    }

    @Override
    public boolean containsValue(float f) {
        return this.m.containsValue(f);
    }

    @Override
    public float get(Object object) {
        return this.m.get(object);
    }

    @Override
    public float put(K k0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> tobjectfloatmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public Object[] keys() {
        return this.m.keys();
    }

    @Override
    public K[] keys(K[] ak) {
        return this.m.keys(ak);
    }

    @Override
    public TFloatCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public float[] values() {
        return this.m.values();
    }

    @Override
    public float[] values(float[] afloat) {
        return this.m.values(afloat);
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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.m.forEachKey(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        return this.m.forEachValue(tfloatprocedure);
    }

    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        return this.m.forEachEntry(tobjectfloatprocedure);
    }

    @Override
    public TObjectFloatIterator<K> iterator() {
        return new TObjectFloatIterator<K>() {
            TObjectFloatIterator<K> iter;

            {
                this.iter = TUnmodifiableObjectFloatMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public float value() {
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
            public float setValue(float f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public float putIfAbsent(K k0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K k0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K k0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(K k0, float f, float f1) {
        throw new UnsupportedOperationException();
    }
}
