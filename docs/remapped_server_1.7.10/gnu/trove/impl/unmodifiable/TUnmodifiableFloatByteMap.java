package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatByteMap implements TFloatByteMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatByteMap m;
    private transient TFloatSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableFloatByteMap(TFloatByteMap tfloatbytemap) {
        if (tfloatbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatbytemap;
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
    public boolean containsValue(byte b0) {
        return this.m.containsValue(b0);
    }

    @Override
    public byte get(float f) {
        return this.m.get(f);
    }

    @Override
    public byte put(float f, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatByteMap tfloatbytemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Byte> map) {
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        return this.m.forEachValue(tbyteprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatByteProcedure tfloatbyteprocedure) {
        return this.m.forEachEntry(tfloatbyteprocedure);
    }

    @Override
    public TFloatByteIterator iterator() {
        return new TFloatByteIterator() {
            TFloatByteIterator iter;

            {
                this.iter = TUnmodifiableFloatByteMap.this.m.iterator();
            }

            @Override
            public float key() {
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
    public byte putIfAbsent(float f, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatByteProcedure tfloatbyteprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float f, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(float f, byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }
}
