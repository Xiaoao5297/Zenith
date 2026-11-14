package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class TUnmodifiableObjectByteMap<K> implements TObjectByteMap<K>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectByteMap<K> m;
    private transient Set<K> keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableObjectByteMap(TObjectByteMap<K> tobjectbytemap) {
        if (tobjectbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectbytemap;
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
    public boolean containsValue(byte b0) {
        return this.m.containsValue(b0);
    }

    @Override
    public byte get(Object object) {
        return this.m.get(object);
    }

    @Override
    public byte put(K k0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectByteMap<? extends K> tobjectbytemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Byte> map) {
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
    public TByteCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public byte[] values() {
        return this.m.values();
    }

    @Override
    public byte[] values(byte[] abyte) {
        return this.m.values(abyte);
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
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.m.forEachKey(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        return this.m.forEachValue(tbyteprocedure);
    }

    @Override
    public boolean forEachEntry(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        return this.m.forEachEntry(tobjectbyteprocedure);
    }

    @Override
    public TObjectByteIterator<K> iterator() {
        return new TObjectByteIterator<K>() {
            TObjectByteIterator<K> iter;

            {
                this.iter = TUnmodifiableObjectByteMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public byte value() {
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
            public byte setValue(byte b0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public byte putIfAbsent(K k0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K k0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K k0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(K k0, byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }
}
