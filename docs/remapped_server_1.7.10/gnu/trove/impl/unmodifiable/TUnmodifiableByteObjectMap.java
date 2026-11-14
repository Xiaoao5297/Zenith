package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TUnmodifiableByteObjectMap<V> implements TByteObjectMap<V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteObjectMap<V> m;
    private transient TByteSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableByteObjectMap(TByteObjectMap<V> tbyteobjectmap) {
        if (tbyteobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbyteobjectmap;
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
    public boolean containsKey(byte b0) {
        return this.m.containsKey(b0);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.m.containsValue(object);
    }

    @Override
    public V get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public V put(byte b0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteObjectMap<? extends V> tbyteobjectmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TByteSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }

        return this.keySet;
    }

    @Override
    public byte[] keys() {
        return this.m.keys();
    }

    @Override
    public byte[] keys(byte[] abyte) {
        return this.m.keys(abyte);
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        return this.m.forEachValue(tobjectprocedure);
    }

    @Override
    public boolean forEachEntry(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        return this.m.forEachEntry(tbyteobjectprocedure);
    }

    @Override
    public TByteObjectIterator<V> iterator() {
        return new TByteObjectIterator<V>() {
            TByteObjectIterator<V> iter;

            {
                this.iter = TUnmodifiableByteObjectMap.this.m.iterator();
            }

            @Override
            public byte key() {
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
    public V putIfAbsent(byte b0, V v0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        throw new UnsupportedOperationException();
    }
}
