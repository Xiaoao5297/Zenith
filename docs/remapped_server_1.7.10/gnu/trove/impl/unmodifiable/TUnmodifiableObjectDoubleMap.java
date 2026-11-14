package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectDoubleMap<K> implements TObjectDoubleMap<K>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectDoubleMap<K> m;
    private transient Set<K> keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableObjectDoubleMap(TObjectDoubleMap<K> tobjectdoublemap) {
        if (tobjectdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectdoublemap;
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
    public boolean containsKey(Object object) {
        return this.m.containsKey(object);
    }

    @Override
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(Object object) {
        return this.m.get(object);
    }

    @Override
    public double put(K k0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectDoubleMap<? extends K> tobjectdoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public Object[] keys() {
        return this.m.keys();
    }

    @Override
    public K[] keys(K[] ak) {
        return this.m.keys(ak);
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

    @Override
    public boolean equals(Object object) {
        return object == this || this.m.equals(object);
    }

    @Override
    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.m.forEachKey(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        return this.m.forEachEntry(tobjectdoubleprocedure);
    }

    @Override
    public TObjectDoubleIterator<K> iterator() {
        return new TObjectDoubleIterator<K>() {
            TObjectDoubleIterator<K> iter;

            {
                this.iter = TUnmodifiableObjectDoubleMap.this.m.iterator();
            }

            @Override
            public K key() {
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
    public double putIfAbsent(K k0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K k0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K k0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(K k0, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
