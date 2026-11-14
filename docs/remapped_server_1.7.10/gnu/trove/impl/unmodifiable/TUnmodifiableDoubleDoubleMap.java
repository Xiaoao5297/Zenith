package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleDoubleMap implements TDoubleDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleDoubleMap m;
    private transient TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableDoubleDoubleMap(TDoubleDoubleMap tdoubledoublemap) {
        if (tdoubledoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubledoublemap;
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
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(double d0) {
        return this.m.get(d0);
    }

    @Override
    public double put(double d0, double d1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleDoubleMap tdoubledoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> map) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachKey(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        return this.m.forEachEntry(tdoubledoubleprocedure);
    }

    @Override
    public TDoubleDoubleIterator iterator() {
        return new TDoubleDoubleIterator() {
            TDoubleDoubleIterator iter;

            {
                this.iter = TUnmodifiableDoubleDoubleMap.this.m.iterator();
            }

            @Override
            public double key() {
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
    public double putIfAbsent(double d0, double d1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double d0, double d1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(double d0, double d1, double d2) {
        throw new UnsupportedOperationException();
    }
}
