package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectLongMap<K> implements TObjectLongMap<K>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectLongMap<K> m;
    private transient Set<K> keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableObjectLongMap(TObjectLongMap<K> tobjectlongmap) {
        if (tobjectlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectlongmap;
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
    public boolean containsValue(long i) {
        return this.m.containsValue(i);
    }

    @Override
    public long get(Object object) {
        return this.m.get(object);
    }

    @Override
    public long put(K k0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectLongMap<? extends K> tobjectlongmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> map) {
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
    public TLongCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public long[] values() {
        return this.m.values();
    }

    @Override
    public long[] values(long[] along) {
        return this.m.values(along);
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
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.m.forEachKey(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        return this.m.forEachValue(tlongprocedure);
    }

    @Override
    public boolean forEachEntry(TObjectLongProcedure<? super K> tobjectlongprocedure) {
        return this.m.forEachEntry(tobjectlongprocedure);
    }

    @Override
    public TObjectLongIterator<K> iterator() {
        return new TObjectLongIterator<K>() {
            TObjectLongIterator<K> iter;

            {
                this.iter = TUnmodifiableObjectLongMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public long value() {
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
            public long setValue(long i) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public long putIfAbsent(K k0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectLongProcedure<? super K> tobjectlongprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K k0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K k0, long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(K k0, long i, long j) {
        throw new UnsupportedOperationException();
    }
}
