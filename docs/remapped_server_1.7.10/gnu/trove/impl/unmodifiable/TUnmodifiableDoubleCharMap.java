package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleCharMap implements TDoubleCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleCharMap m;
    private transient TDoubleSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableDoubleCharMap(TDoubleCharMap tdoublecharmap) {
        if (tdoublecharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoublecharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(double d0) {
        return this.m.get(d0);
    }

    @Override
    public char put(double d0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleCharMap tdoublecharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Character> map) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachKey(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TDoubleCharProcedure tdoublecharprocedure) {
        return this.m.forEachEntry(tdoublecharprocedure);
    }

    @Override
    public TDoubleCharIterator iterator() {
        return new TDoubleCharIterator() {
            TDoubleCharIterator iter;

            {
                this.iter = TUnmodifiableDoubleCharMap.this.m.iterator();
            }

            @Override
            public double key() {
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
    public char putIfAbsent(double d0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleCharProcedure tdoublecharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double d0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(double d0, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
