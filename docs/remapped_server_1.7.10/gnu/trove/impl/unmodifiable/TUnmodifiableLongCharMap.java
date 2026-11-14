package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableLongCharMap implements TLongCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongCharMap m;
    private transient TLongSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableLongCharMap(TLongCharMap tlongcharmap) {
        if (tlongcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongcharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(long i) {
        return this.m.get(i);
    }

    @Override
    public char put(long i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongCharMap tlongcharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Character> map) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.m.forEachKey(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TLongCharProcedure tlongcharprocedure) {
        return this.m.forEachEntry(tlongcharprocedure);
    }

    @Override
    public TLongCharIterator iterator() {
        return new TLongCharIterator() {
            TLongCharIterator iter;

            {
                this.iter = TUnmodifiableLongCharMap.this.m.iterator();
            }

            @Override
            public long key() {
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
    public char putIfAbsent(long i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongCharProcedure tlongcharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(long i, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
