package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectIntMap<K> implements TObjectIntMap<K>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectIntMap<K> m;
    private transient Set<K> keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableObjectIntMap(TObjectIntMap<K> tobjectintmap) {
        if (tobjectintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectintmap;
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
    public boolean containsValue(int i) {
        return this.m.containsValue(i);
    }

    @Override
    public int get(Object object) {
        return this.m.get(object);
    }

    @Override
    public int put(K k0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectIntMap<? extends K> tobjectintmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> map) {
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.m.forEachKey(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        return this.m.forEachValue(tintprocedure);
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> tobjectintprocedure) {
        return this.m.forEachEntry(tobjectintprocedure);
    }

    @Override
    public TObjectIntIterator<K> iterator() {
        return new TObjectIntIterator<K>() {
            TObjectIntIterator<K> iter;

            {
                this.iter = TUnmodifiableObjectIntMap.this.m.iterator();
            }

            @Override
            public K key() {
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
    public int putIfAbsent(K k0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectIntProcedure<? super K> tobjectintprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K k0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K k0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(K k0, int i, int j) {
        throw new UnsupportedOperationException();
    }
}
