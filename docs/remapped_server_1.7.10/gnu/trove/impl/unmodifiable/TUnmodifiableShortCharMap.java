package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortCharMap implements TShortCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortCharMap m;
    private transient TShortSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableShortCharMap(TShortCharMap tshortcharmap) {
        if (tshortcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortcharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public char put(short short0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortCharMap tshortcharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Character> map) {
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
    public TCharCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public char[] values() {
        return this.m.values();
    }

    @Override
    public char[] values(char[] achar) {
        return this.m.values(achar);
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
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TShortCharProcedure tshortcharprocedure) {
        return this.m.forEachEntry(tshortcharprocedure);
    }

    @Override
    public TShortCharIterator iterator() {
        return new TShortCharIterator() {
            TShortCharIterator iter;

            {
                this.iter = TUnmodifiableShortCharMap.this.m.iterator();
            }

            @Override
            public short key() {
                return this.iter.key();
            }

            @Override
            public char value() {
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
            public char setValue(char c0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public char putIfAbsent(short short0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortCharProcedure tshortcharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(short short0, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
