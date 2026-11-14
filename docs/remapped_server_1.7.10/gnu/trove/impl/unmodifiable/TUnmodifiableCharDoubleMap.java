package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharDoubleMap implements TCharDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharDoubleMap m;
    private transient TCharSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableCharDoubleMap(TCharDoubleMap tchardoublemap) {
        if (tchardoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tchardoublemap;
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
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public double put(char c0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharDoubleMap tchardoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Double> map) {
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
    public TDoubleCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public double[] values() {
        return this.m.values();
    }

    @Override
    public double[] values(double[] adouble) {
        return this.m.values(adouble);
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
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TCharDoubleProcedure tchardoubleprocedure) {
        return this.m.forEachEntry(tchardoubleprocedure);
    }

    @Override
    public TCharDoubleIterator iterator() {
        return new TCharDoubleIterator() {
            TCharDoubleIterator iter;

            {
                this.iter = TUnmodifiableCharDoubleMap.this.m.iterator();
            }

            @Override
            public char key() {
                return this.iter.key();
            }

            @Override
            public double value() {
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
            public double setValue(double d0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public double putIfAbsent(char c0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharDoubleProcedure tchardoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char c0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(char c0, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
