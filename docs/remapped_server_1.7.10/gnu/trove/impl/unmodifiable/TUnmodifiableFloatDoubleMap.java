package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatDoubleMap implements TFloatDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatDoubleMap m;
    private transient TFloatSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableFloatDoubleMap(TFloatDoubleMap tfloatdoublemap) {
        if (tfloatdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatdoublemap;
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
    public boolean containsKey(float f) {
        return this.m.containsKey(f);
    }

    @Override
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(float f) {
        return this.m.get(f);
    }

    @Override
    public double put(float f, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatDoubleMap tfloatdoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TFloatSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public float[] keys() {
        return this.m.keys();
    }

    @Override
    public float[] keys(float[] afloat) {
        return this.m.keys(afloat);
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatDoubleProcedure tfloatdoubleprocedure) {
        return this.m.forEachEntry(tfloatdoubleprocedure);
    }

    @Override
    public TFloatDoubleIterator iterator() {
        return new TFloatDoubleIterator() {
            TFloatDoubleIterator iter;

            {
                this.iter = TUnmodifiableFloatDoubleMap.this.m.iterator();
            }

            @Override
            public float key() {
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
    public double putIfAbsent(float f, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatDoubleProcedure tfloatdoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float f, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(float f, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
