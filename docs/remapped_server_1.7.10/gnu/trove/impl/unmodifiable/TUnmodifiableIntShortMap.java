package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntShortMap implements TIntShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntShortMap m;
    private transient TIntSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableIntShortMap(TIntShortMap tintshortmap) {
        if (tintshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintshortmap;
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
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(int i) {
        return this.m.get(i);
    }

    @Override
    public short put(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntShortMap tintshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Short> map) {
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TIntShortProcedure tintshortprocedure) {
        return this.m.forEachEntry(tintshortprocedure);
    }

    @Override
    public TIntShortIterator iterator() {
        return new TIntShortIterator() {
            TIntShortIterator iter;

            {
                this.iter = TUnmodifiableIntShortMap.this.m.iterator();
            }

            @Override
            public int key() {
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
    public short putIfAbsent(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntShortProcedure tintshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(int i, short short0, short short1) {
        throw new UnsupportedOperationException();
    }
}
