package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortDoubleMap implements TShortDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortDoubleMap m;
    private transient TShortSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableShortDoubleMap(TShortDoubleMap tshortdoublemap) {
        if (tshortdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortdoublemap;
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
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public double put(short short0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortDoubleMap tshortdoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Double> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TShortDoubleProcedure tshortdoubleprocedure) {
        return this.m.forEachEntry(tshortdoubleprocedure);
    }

    @Override
    public TShortDoubleIterator iterator() {
        return new TShortDoubleIterator() {
            TShortDoubleIterator iter;

            {
                this.iter = TUnmodifiableShortDoubleMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public double putIfAbsent(short short0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortDoubleProcedure tshortdoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(short short0, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
