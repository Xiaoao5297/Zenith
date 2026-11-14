package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntCharMap implements TIntCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntCharMap m;
    private transient TIntSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableIntCharMap(TIntCharMap tintcharmap) {
        if (tintcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintcharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(int i) {
        return this.m.get(i);
    }

    @Override
    public char put(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntCharMap tintcharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Character> map) {
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TIntCharProcedure tintcharprocedure) {
        return this.m.forEachEntry(tintcharprocedure);
    }

    @Override
    public TIntCharIterator iterator() {
        return new TIntCharIterator() {
            TIntCharIterator iter;

            {
                this.iter = TUnmodifiableIntCharMap.this.m.iterator();
            }

            @Override
            public int key() {
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
    public char putIfAbsent(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntCharProcedure tintcharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(int i, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
