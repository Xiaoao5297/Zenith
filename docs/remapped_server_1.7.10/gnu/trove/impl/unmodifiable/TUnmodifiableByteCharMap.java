package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteCharMap implements TByteCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteCharMap m;
    private transient TByteSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableByteCharMap(TByteCharMap tbytecharmap) {
        if (tbytecharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytecharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public char put(byte b0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteCharMap tbytecharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Character> map) {
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
    public TCharCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public char[] values() {
        return this.m.values();
    }

    @Override
    public char[] values(char[] achar) {
        return this.m.values(achar);
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
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TByteCharProcedure tbytecharprocedure) {
        return this.m.forEachEntry(tbytecharprocedure);
    }

    @Override
    public TByteCharIterator iterator() {
        return new TByteCharIterator() {
            TByteCharIterator iter;

            {
                this.iter = TUnmodifiableByteCharMap.this.m.iterator();
            }

            @Override
            public byte key() {
                return this.iter.key();
            }

            @Override
            public char value() {
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
            public char setValue(char c0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public char putIfAbsent(byte b0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteCharProcedure tbytecharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte b0, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(byte b0, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
