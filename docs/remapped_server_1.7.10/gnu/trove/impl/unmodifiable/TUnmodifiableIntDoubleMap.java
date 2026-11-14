package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableIntDoubleMap implements TIntDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntDoubleMap m;
    private transient TIntSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableIntDoubleMap(TIntDoubleMap tintdoublemap) {
        if (tintdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintdoublemap;
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
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(int i) {
        return this.m.get(i);
    }

    @Override
    public double put(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntDoubleMap tintdoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Double> map) {
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.m.forEachKey(tintprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TIntDoubleProcedure tintdoubleprocedure) {
        return this.m.forEachEntry(tintdoubleprocedure);
    }

    @Override
    public TIntDoubleIterator iterator() {
        return new TIntDoubleIterator() {
            TIntDoubleIterator iter;

            {
                this.iter = TUnmodifiableIntDoubleMap.this.m.iterator();
            }

            @Override
            public int key() {
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
    public double putIfAbsent(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntDoubleProcedure tintdoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(int i, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
