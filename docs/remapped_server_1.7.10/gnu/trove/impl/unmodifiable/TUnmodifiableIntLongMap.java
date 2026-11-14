package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntLongMap implements TIntLongMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntLongMap m;
    private transient TIntSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableIntLongMap(TIntLongMap tintlongmap) {
        if (tintlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintlongmap;
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
    public boolean containsValue(long i) {
        return this.m.containsValue(i);
    }

    @Override
    public long get(int i) {
        return this.m.get(i);
    }

    @Override
    public long put(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntLongMap tintlongmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Long> map) {
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
    public TLongCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public long[] values() {
        return this.m.values();
    }

    @Override
    public long[] values(long[] along) {
        return this.m.values(along);
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
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        return this.m.forEachValue(tlongprocedure);
    }

    @Override
    public boolean forEachEntry(TIntLongProcedure tintlongprocedure) {
        return this.m.forEachEntry(tintlongprocedure);
    }

    @Override
    public TIntLongIterator iterator() {
        return new TIntLongIterator() {
            TIntLongIterator iter;

            {
                this.iter = TUnmodifiableIntLongMap.this.m.iterator();
            }

            @Override
            public int key() {
                return this.iter.key();
            }

            @Override
            public long value() {
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
            public long setValue(long i) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public long putIfAbsent(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntLongProcedure tintlongprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(int i, long j, long k) {
        throw new UnsupportedOperationException();
    }
}
