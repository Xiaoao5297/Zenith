package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortShortMap implements TShortShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortShortMap m;
    private transient TShortSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableShortShortMap(TShortShortMap tshortshortmap) {
        if (tshortshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortshortmap;
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
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public short put(short short0, short short1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortShortMap tshortshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Short> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TShortShortProcedure tshortshortprocedure) {
        return this.m.forEachEntry(tshortshortprocedure);
    }

    @Override
    public TShortShortIterator iterator() {
        return new TShortShortIterator() {
            TShortShortIterator iter;

            {
                this.iter = TUnmodifiableShortShortMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public short putIfAbsent(short short0, short short1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortShortProcedure tshortshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, short short1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(short short0, short short1, short short2) {
        throw new UnsupportedOperationException();
    }
}
