package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntFloatMap implements TIntFloatMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntFloatMap m;
    private transient TIntSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableIntFloatMap(TIntFloatMap tintfloatmap) {
        if (tintfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintfloatmap;
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
    public boolean containsValue(float f) {
        return this.m.containsValue(f);
    }

    @Override
    public float get(int i) {
        return this.m.get(i);
    }

    @Override
    public float put(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntFloatMap tintfloatmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Float> map) {
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

    public boolean equals(Object object) {
        return object == this || this.m.equals(object);
    }

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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        return this.m.forEachValue(tfloatprocedure);
    }

    @Override
    public boolean forEachEntry(TIntFloatProcedure tintfloatprocedure) {
        return this.m.forEachEntry(tintfloatprocedure);
    }

    @Override
    public TIntFloatIterator iterator() {
        return new TIntFloatIterator() {
            TIntFloatIterator iter;

            {
                this.iter = TUnmodifiableIntFloatMap.this.m.iterator();
            }

            @Override
            public int key() {
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
    public float putIfAbsent(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntFloatProcedure tintfloatprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(int i, float f, float f1) {
        throw new UnsupportedOperationException();
    }
}
