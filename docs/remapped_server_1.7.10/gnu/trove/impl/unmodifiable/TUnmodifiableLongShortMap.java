package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongShortMap implements TLongShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongShortMap m;
    private transient TLongSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableLongShortMap(TLongShortMap tlongshortmap) {
        if (tlongshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongshortmap;
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
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(long i) {
        return this.m.get(i);
    }

    @Override
    public short put(long i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongShortMap tlongshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Short> map) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.m.forEachKey(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TLongShortProcedure tlongshortprocedure) {
        return this.m.forEachEntry(tlongshortprocedure);
    }

    @Override
    public TLongShortIterator iterator() {
        return new TLongShortIterator() {
            TLongShortIterator iter;

            {
                this.iter = TUnmodifiableLongShortMap.this.m.iterator();
            }

            @Override
            public long key() {
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
    public short putIfAbsent(long i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongShortProcedure tlongshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(long i, short short0, short short1) {
        throw new UnsupportedOperationException();
    }
}
