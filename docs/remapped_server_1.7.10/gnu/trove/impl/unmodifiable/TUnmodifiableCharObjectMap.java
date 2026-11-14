package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableCharObjectMap<V> implements TCharObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharObjectMap<V> m;
    private transient TCharSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableCharObjectMap(TCharObjectMap<V> tcharobjectmap) {
        if (tcharobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharobjectmap;
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
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public V put(char c0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharObjectMap<? extends V> tcharobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
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
    public Collection<V> valueCollection() {
        if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public Object[] values() {
        return this.m.values();
    }

    @Override
    public V[] values(V[] av) {
        return this.m.values(av);
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        return this.m.forEachEntry(tcharobjectprocedure);
    }

    @Override
    public TCharObjectIterator<V> iterator() {
        return new TCharObjectIterator<V>() {
            TCharObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableCharObjectMap.this.m.iterator();
            }

            @Override
            public char key() {
                return this.iter.key();
            }

            @Override
            public V value() {
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
            public V setValue(V v0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public V putIfAbsent(char c0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
