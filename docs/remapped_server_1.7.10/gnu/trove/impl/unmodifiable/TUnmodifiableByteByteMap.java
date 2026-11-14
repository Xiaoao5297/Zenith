package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteByteMap implements TByteByteMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteByteMap m;
    private transient TByteSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableByteByteMap(TByteByteMap tbytebytemap) {
        if (tbytebytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytebytemap;
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
    public boolean containsValue(byte b0) {
        return this.m.containsValue(b0);
    }

    @Override
    public byte get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public byte put(byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteByteMap tbytebytemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Byte> map) {
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        return this.m.forEachValue(tbyteprocedure);
    }

    @Override
    public boolean forEachEntry(TByteByteProcedure tbytebyteprocedure) {
        return this.m.forEachEntry(tbytebyteprocedure);
    }

    @Override
    public TByteByteIterator iterator() {
        return new TByteByteIterator() {
            TByteByteIterator iter;

            {
                this.iter = TUnmodifiableByteByteMap.this.m.iterator();
            }

            @Override
            public byte key() {
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
    public byte putIfAbsent(byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteByteProcedure tbytebyteprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(byte b0, byte b1, byte b2) {
        throw new UnsupportedOperationException();
    }
}
