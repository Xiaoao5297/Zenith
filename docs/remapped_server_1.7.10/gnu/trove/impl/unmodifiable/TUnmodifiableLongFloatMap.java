package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongFloatMap implements TLongFloatMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongFloatMap m;
    private transient TLongSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableLongFloatMap(TLongFloatMap tlongfloatmap) {
        if (tlongfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongfloatmap;
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
    public boolean containsValue(float f) {
        return this.m.containsValue(f);
    }

    @Override
    public float get(long i) {
        return this.m.get(i);
    }

    @Override
    public float put(long i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongFloatMap tlongfloatmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Float> map) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.m.forEachKey(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        return this.m.forEachValue(tfloatprocedure);
    }

    @Override
    public boolean forEachEntry(TLongFloatProcedure tlongfloatprocedure) {
        return this.m.forEachEntry(tlongfloatprocedure);
    }

    @Override
    public TLongFloatIterator iterator() {
        return new TLongFloatIterator() {
            TLongFloatIterator iter;

            {
                this.iter = TUnmodifiableLongFloatMap.this.m.iterator();
            }

            @Override
            public long key() {
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
    public float putIfAbsent(long i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongFloatProcedure tlongfloatprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(long i, float f, float f1) {
        throw new UnsupportedOperationException();
    }
}
