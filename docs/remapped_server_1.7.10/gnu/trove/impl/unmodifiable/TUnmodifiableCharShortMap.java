package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharShortMap implements TCharShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharShortMap m;
    private transient TCharSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableCharShortMap(TCharShortMap tcharshortmap) {
        if (tcharshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharshortmap;
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
    public boolean containsKey(char c0) {
        return this.m.containsKey(c0);
    }

    @Override
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public short put(char c0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharShortMap tcharshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Short> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TCharSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public char[] keys() {
        return this.m.keys();
    }

    @Override
    public char[] keys(char[] achar) {
        return this.m.keys(achar);
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TCharShortProcedure tcharshortprocedure) {
        return this.m.forEachEntry(tcharshortprocedure);
    }

    @Override
    public TCharShortIterator iterator() {
        return new TCharShortIterator() {
            TCharShortIterator iter;

            {
                this.iter = TUnmodifiableCharShortMap.this.m.iterator();
            }

            @Override
            public char key() {
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
    public short putIfAbsent(char c0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharShortProcedure tcharshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char c0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(char c0, short short0, short short1) {
        throw new UnsupportedOperationException();
    }
}
