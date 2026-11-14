package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortFloatMap implements TShortFloatMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortFloatMap m;
    private transient TShortSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableShortFloatMap(TShortFloatMap tshortfloatmap) {
        if (tshortfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortfloatmap;
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
    public boolean containsValue(float f) {
        return this.m.containsValue(f);
    }

    @Override
    public float get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public float put(short short0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortFloatMap tshortfloatmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Float> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        return this.m.forEachValue(tfloatprocedure);
    }

    @Override
    public boolean forEachEntry(TShortFloatProcedure tshortfloatprocedure) {
        return this.m.forEachEntry(tshortfloatprocedure);
    }

    @Override
    public TShortFloatIterator iterator() {
        return new TShortFloatIterator() {
            TShortFloatIterator iter;

            {
                this.iter = TUnmodifiableShortFloatMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public float putIfAbsent(short short0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortFloatProcedure tshortfloatprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(short short0, float f, float f1) {
        throw new UnsupportedOperationException();
    }
}
