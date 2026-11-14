package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.procedure.TFloatIntProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatIntMap implements TFloatIntMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatIntMap m;
    private transient TFloatSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableFloatIntMap(TFloatIntMap tfloatintmap) {
        if (tfloatintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatintmap;
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
    public boolean containsValue(int i) {
        return this.m.containsValue(i);
    }

    @Override
    public int get(float f) {
        return this.m.get(f);
    }

    @Override
    public int put(float f, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatIntMap tfloatintmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Integer> map) {
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
    public TIntCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] aint) {
        return this.m.values(aint);
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        return this.m.forEachValue(tintprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatIntProcedure tfloatintprocedure) {
        return this.m.forEachEntry(tfloatintprocedure);
    }

    @Override
    public TFloatIntIterator iterator() {
        return new TFloatIntIterator() {
            TFloatIntIterator iter;

            {
                this.iter = TUnmodifiableFloatIntMap.this.m.iterator();
            }

            @Override
            public float key() {
                return this.iter.key();
            }

            @Override
            public int value() {
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
            public int setValue(int i) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(float f, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatIntProcedure tfloatintprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float f, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(float f, int i, int j) {
        throw new UnsupportedOperationException();
    }
}
