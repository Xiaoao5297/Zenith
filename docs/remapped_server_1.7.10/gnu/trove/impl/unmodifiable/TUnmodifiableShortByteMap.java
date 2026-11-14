package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableShortByteMap implements TShortByteMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortByteMap m;
    private transient TShortSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableShortByteMap(TShortByteMap tshortbytemap) {
        if (tshortbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortbytemap;
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
    public boolean containsValue(byte b0) {
        return this.m.containsValue(b0);
    }

    @Override
    public byte get(short short0) {
        return this.m.get(short0);
    }

    @Override
    public byte put(short short0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortByteMap tshortbytemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Byte> map) {
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
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.m.forEachKey(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        return this.m.forEachValue(tbyteprocedure);
    }

    @Override
    public boolean forEachEntry(TShortByteProcedure tshortbyteprocedure) {
        return this.m.forEachEntry(tshortbyteprocedure);
    }

    @Override
    public TShortByteIterator iterator() {
        return new TShortByteIterator() {
            TShortByteIterator iter;

            {
                this.iter = TUnmodifiableShortByteMap.this.m.iterator();
            }

            @Override
            public short key() {
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
    public byte putIfAbsent(short short0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortByteProcedure tshortbyteprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short short0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(short short0, byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }
}
