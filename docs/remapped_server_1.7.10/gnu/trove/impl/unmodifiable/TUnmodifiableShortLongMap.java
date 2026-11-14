package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortLongMap implements TShortLongMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortLongMap m;
    private transient TShortSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableShortLongMap(TShortLongMap tshortlongmap) {
        if (tshortlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortlongmap;
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
    public boolean containsValue(long i) {
        return this.m.containsValue(i);
    }

    @Override
    public long get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public long put(short short0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortLongMap tshortlongmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Long> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        return this.m.forEachValue(tlongprocedure);
    }

    @Override
    public boolean forEachEntry(TShortLongProcedure tshortlongprocedure) {
        return this.m.forEachEntry(tshortlongprocedure);
    }

    @Override
    public TShortLongIterator iterator() {
        return new TShortLongIterator() {
            TShortLongIterator iter;

            {
                this.iter = TUnmodifiableShortLongMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public long putIfAbsent(short short0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortLongProcedure tshortlongprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(short short0, long i, long j) {
        throw new UnsupportedOperationException();
    }
}
