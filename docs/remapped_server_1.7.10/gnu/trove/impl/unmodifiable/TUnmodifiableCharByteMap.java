package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharByteMap implements TCharByteMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharByteMap m;
    private transient TCharSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableCharByteMap(TCharByteMap tcharbytemap) {
        if (tcharbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharbytemap;
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
    public boolean containsValue(byte b0) {
        return this.m.containsValue(b0);
    }

    @Override
    public byte get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public byte put(char c0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharByteMap tcharbytemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Byte> map) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        return this.m.forEachValue(tbyteprocedure);
    }

    @Override
    public boolean forEachEntry(TCharByteProcedure tcharbyteprocedure) {
        return this.m.forEachEntry(tcharbyteprocedure);
    }

    @Override
    public TCharByteIterator iterator() {
        return new TCharByteIterator() {
            TCharByteIterator iter;

            {
                this.iter = TUnmodifiableCharByteMap.this.m.iterator();
            }

            @Override
            public char key() {
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
    public byte putIfAbsent(char c0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharByteProcedure tcharbyteprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char c0, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(char c0, byte b0, byte b1) {
        throw new UnsupportedOperationException();
    }
}
