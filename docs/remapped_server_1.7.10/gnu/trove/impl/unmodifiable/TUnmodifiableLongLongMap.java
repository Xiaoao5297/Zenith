package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongLongMap implements TLongLongMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongLongMap m;
    private transient TLongSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableLongLongMap(TLongLongMap tlonglongmap) {
        if (tlonglongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlonglongmap;
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
    public boolean containsValue(long i) {
        return this.m.containsValue(i);
    }

    @Override
    public long get(long i) {
        return this.m.get(i);
    }

    @Override
    public long put(long i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongLongMap tlonglongmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Long> map) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.m.forEachKey(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        return this.m.forEachValue(tlongprocedure);
    }

    @Override
    public boolean forEachEntry(TLongLongProcedure tlonglongprocedure) {
        return this.m.forEachEntry(tlonglongprocedure);
    }

    @Override
    public TLongLongIterator iterator() {
        return new TLongLongIterator() {
            TLongLongIterator iter;

            {
                this.iter = TUnmodifiableLongLongMap.this.m.iterator();
            }

            @Override
            public long key() {
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
    public long putIfAbsent(long i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongLongProcedure tlonglongprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(long i, long j, long k) {
        throw new UnsupportedOperationException();
    }
}
