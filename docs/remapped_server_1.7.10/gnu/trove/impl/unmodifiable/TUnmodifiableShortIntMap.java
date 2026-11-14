package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortIntMap implements TShortIntMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortIntMap m;
    private transient TShortSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableShortIntMap(TShortIntMap tshortintmap) {
        if (tshortintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortintmap;
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
    public boolean containsValue(int i) {
        return this.m.containsValue(i);
    }

    @Override
    public int get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public int put(short short0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortIntMap tshortintmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Integer> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        return this.m.forEachValue(tintprocedure);
    }

    @Override
    public boolean forEachEntry(TShortIntProcedure tshortintprocedure) {
        return this.m.forEachEntry(tshortintprocedure);
    }

    @Override
    public TShortIntIterator iterator() {
        return new TShortIntIterator() {
            TShortIntIterator iter;

            {
                this.iter = TUnmodifiableShortIntMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public int putIfAbsent(short short0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortIntProcedure tshortintprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(short short0, int i, int j) {
        throw new UnsupportedOperationException();
    }
}
