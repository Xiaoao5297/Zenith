package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteIntMap implements TByteIntMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteIntMap m;
    private transient TByteSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableByteIntMap(TByteIntMap tbyteintmap) {
        if (tbyteintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbyteintmap;
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
    public boolean containsValue(int i) {
        return this.m.containsValue(i);
    }

    @Override
    public int get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public int put(byte b0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteIntMap tbyteintmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Integer> map) {
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        return this.m.forEachValue(tintprocedure);
    }

    @Override
    public boolean forEachEntry(TByteIntProcedure tbyteintprocedure) {
        return this.m.forEachEntry(tbyteintprocedure);
    }

    @Override
    public TByteIntIterator iterator() {
        return new TByteIntIterator() {
            TByteIntIterator iter;

            {
                this.iter = TUnmodifiableByteIntMap.this.m.iterator();
            }

            @Override
            public byte key() {
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
    public int putIfAbsent(byte b0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteIntProcedure tbyteintprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte b0, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(byte b0, int i, int j) {
        throw new UnsupportedOperationException();
    }
}
