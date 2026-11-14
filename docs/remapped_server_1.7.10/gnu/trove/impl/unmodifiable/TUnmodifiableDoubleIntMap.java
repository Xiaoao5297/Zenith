package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableDoubleIntMap implements TDoubleIntMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleIntMap m;
    private transient TDoubleSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableDoubleIntMap(TDoubleIntMap tdoubleintmap) {
        if (tdoubleintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubleintmap;
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
    public boolean containsValue(int i) {
        return this.m.containsValue(i);
    }

    @Override
    public int get(double d0) {
        return this.m.get(d0);
    }

    @Override
    public int put(double d0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleIntMap tdoubleintmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Integer> map) {
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
    public TIntCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] aint) {
        return this.m.values(aint);
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachKey(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        return this.m.forEachValue(tintprocedure);
    }

    @Override
    public boolean forEachEntry(TDoubleIntProcedure tdoubleintprocedure) {
        return this.m.forEachEntry(tdoubleintprocedure);
    }

    @Override
    public TDoubleIntIterator iterator() {
        return new TDoubleIntIterator() {
            TDoubleIntIterator iter;

            {
                this.iter = TUnmodifiableDoubleIntMap.this.m.iterator();
            }

            @Override
            public double key() {
                return this.iter.key();
            }

            @Override
            public int value() {
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
            public int setValue(int i) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(double d0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleIntProcedure tdoubleintprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double d0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(double d0, int i, int j) {
        throw new UnsupportedOperationException();
    }
}
