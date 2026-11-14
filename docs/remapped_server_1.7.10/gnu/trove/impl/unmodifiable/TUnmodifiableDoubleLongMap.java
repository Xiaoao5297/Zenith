package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleLongMap implements TDoubleLongMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleLongMap m;
    private transient TDoubleSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableDoubleLongMap(TDoubleLongMap tdoublelongmap) {
        if (tdoublelongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoublelongmap;
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
    public boolean containsKey(double d0) {
        return this.m.containsKey(d0);
    }

    @Override
    public boolean containsValue(long i) {
        return this.m.containsValue(i);
    }

    @Override
    public long get(double d0) {
        return this.m.get(d0);
    }

    @Override
    public long put(double d0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleLongMap tdoublelongmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Long> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TDoubleSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public double[] keys() {
        return this.m.keys();
    }

    @Override
    public double[] keys(double[] adouble) {
        return this.m.keys(adouble);
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachKey(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        return this.m.forEachValue(tlongprocedure);
    }

    @Override
    public boolean forEachEntry(TDoubleLongProcedure tdoublelongprocedure) {
        return this.m.forEachEntry(tdoublelongprocedure);
    }

    @Override
    public TDoubleLongIterator iterator() {
        return new TDoubleLongIterator() {
            TDoubleLongIterator iter;

            {
                this.iter = TUnmodifiableDoubleLongMap.this.m.iterator();
            }

            @Override
            public double key() {
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
    public long putIfAbsent(double d0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleLongProcedure tdoublelongprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double d0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(double d0, long i, long j) {
        throw new UnsupportedOperationException();
    }
}
