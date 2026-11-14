package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteShortMap implements TByteShortMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteShortMap m;
    private transient TByteSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableByteShortMap(TByteShortMap tbyteshortmap) {
        if (tbyteshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbyteshortmap;
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
    public boolean containsValue(short short0) {
        return this.m.containsValue(short0);
    }

    @Override
    public short get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public short put(byte b0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteShortMap tbyteshortmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Short> map) {
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
    public TShortCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public short[] values() {
        return this.m.values();
    }

    @Override
    public short[] values(short[] ashort) {
        return this.m.values(ashort);
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
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        return this.m.forEachValue(tshortprocedure);
    }

    @Override
    public boolean forEachEntry(TByteShortProcedure tbyteshortprocedure) {
        return this.m.forEachEntry(tbyteshortprocedure);
    }

    @Override
    public TByteShortIterator iterator() {
        return new TByteShortIterator() {
            TByteShortIterator iter;

            {
                this.iter = TUnmodifiableByteShortMap.this.m.iterator();
            }

            @Override
            public byte key() {
                return this.iter.key();
            }

            @Override
            public short value() {
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
            public short setValue(short short0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public short putIfAbsent(byte b0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteShortProcedure tbyteshortprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte b0, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(byte b0, short short0, short short1) {
        throw new UnsupportedOperationException();
    }
}
