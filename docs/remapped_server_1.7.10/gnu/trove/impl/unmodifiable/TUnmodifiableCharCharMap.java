package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharCharMap implements TCharCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharCharMap m;
    private transient TCharSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableCharCharMap(TCharCharMap tcharcharmap) {
        if (tcharcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharcharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public char put(char c0, char c1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharCharMap tcharcharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Character> map) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TCharCharProcedure tcharcharprocedure) {
        return this.m.forEachEntry(tcharcharprocedure);
    }

    @Override
    public TCharCharIterator iterator() {
        return new TCharCharIterator() {
            TCharCharIterator iter;

            {
                this.iter = TUnmodifiableCharCharMap.this.m.iterator();
            }

            @Override
            public char key() {
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
    public char putIfAbsent(char c0, char c1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharCharProcedure tcharcharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char c0, char c1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(char c0, char c1, char c2) {
        throw new UnsupportedOperationException();
    }
}
