package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableByteDoubleMap implements TByteDoubleMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteDoubleMap m;
    private transient TByteSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableByteDoubleMap(TByteDoubleMap tbytedoublemap) {
        if (tbytedoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytedoublemap;
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
    public boolean containsValue(double d0) {
        return this.m.containsValue(d0);
    }

    @Override
    public double get(byte b0) {
        return this.m.get(b0);
    }

    @Override
    public double put(byte b0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteDoubleMap tbytedoublemap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Double> map) {
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
    public TDoubleCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public double[] values() {
        return this.m.values();
    }

    @Override
    public double[] values(double[] adouble) {
        return this.m.values(adouble);
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
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.m.forEachKey(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        return this.m.forEachValue(tdoubleprocedure);
    }

    @Override
    public boolean forEachEntry(TByteDoubleProcedure tbytedoubleprocedure) {
        return this.m.forEachEntry(tbytedoubleprocedure);
    }

    @Override
    public TByteDoubleIterator iterator() {
        return new TByteDoubleIterator() {
            TByteDoubleIterator iter;

            {
                this.iter = TUnmodifiableByteDoubleMap.this.m.iterator();
            }

            @Override
            public byte key() {
                return this.iter.key();
            }

            @Override
            public double value() {
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
            public double setValue(double d0) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public double putIfAbsent(byte b0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteDoubleProcedure tbytedoubleprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte b0, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(byte b0, double d0, double d1) {
        throw new UnsupportedOperationException();
    }
}
