package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableFloatCharMap implements TFloatCharMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatCharMap m;
    private transient TFloatSet keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableFloatCharMap(TFloatCharMap tfloatcharmap) {
        if (tfloatcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatcharmap;
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
    public boolean containsValue(char c0) {
        return this.m.containsValue(c0);
    }

    @Override
    public char get(float f) {
        return this.m.get(f);
    }

    @Override
    public char put(float f, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatCharMap tfloatcharmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Character> map) {
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.m.forEachKey(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        return this.m.forEachValue(tcharprocedure);
    }

    @Override
    public boolean forEachEntry(TFloatCharProcedure tfloatcharprocedure) {
        return this.m.forEachEntry(tfloatcharprocedure);
    }

    @Override
    public TFloatCharIterator iterator() {
        return new TFloatCharIterator() {
            TFloatCharIterator iter;

            {
                this.iter = TUnmodifiableFloatCharMap.this.m.iterator();
            }

            @Override
            public float key() {
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
    public char putIfAbsent(float f, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatCharProcedure tfloatcharprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float f, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(float f, char c0, char c1) {
        throw new UnsupportedOperationException();
    }
}
