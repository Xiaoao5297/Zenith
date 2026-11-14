package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatShortMap implements TFloatShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatShortMap m;
    private transient TFloatSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableFloatShortMap(TFloatShortMap tfloatshortmap) {
        if (tfloatshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatshortmap;
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
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(float f) {
        return this.m.get(f);
    }

    @Override
    public short put(float f, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatShortMap tfloatshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Short> map) {
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
    public TShortCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public short[] values() {
        return this.m.values();
    }

    @Override
    public short[] values(short[] ashort) {
        return this.m.values(ashort);
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatShortProcedure tfloatshortprocedure) {
        return this.m.forEachEntry(tfloatshortprocedure);
    }

    @Override
    public TFloatShortIterator iterator() {
        return new TFloatShortIterator() {
            TFloatShortIterator iter;

            {
                this.iter = TUnmodifiableFloatShortMap.this.m.iterator();
            }

            @Override
            public float key() {
                return this.iter.key();
            }

            @Override
            public short value() {
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
            public short setValue(short short0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public short putIfAbsent(float f, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatShortProcedure tfloatshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float f, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(float f, short short0, short short1) {
        throw new UnsupportedOperationException();
    }
}
