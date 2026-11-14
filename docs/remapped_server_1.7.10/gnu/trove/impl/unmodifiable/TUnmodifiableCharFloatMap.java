package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

public class TUnmodifiableCharFloatMap implements TCharFloatMap, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharFloatMap m;
    private transient TCharSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableCharFloatMap(TCharFloatMap tcharfloatmap) {
        if (tcharfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharfloatmap;
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
    public boolean containsValue(float f) {
        return this.m.containsValue(f);
    }

    @Override
    public float get(char c0) {
        return this.m.get(c0);
    }

    @Override
    public float put(char c0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharFloatMap tcharfloatmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Float> map) {
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
    public TFloatCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }

        return this.values;
    }

    @Override
    public float[] values() {
        return this.m.values();
    }

    @Override
    public float[] values(float[] afloat) {
        return this.m.values(afloat);
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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.m.forEachKey(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        return this.m.forEachValue(tfloatprocedure);
    }

    @Override
    public boolean forEachEntry(TCharFloatProcedure tcharfloatprocedure) {
        return this.m.forEachEntry(tcharfloatprocedure);
    }

    @Override
    public TCharFloatIterator iterator() {
        return new TCharFloatIterator() {
            TCharFloatIterator iter;

            {
                this.iter = TUnmodifiableCharFloatMap.this.m.iterator();
            }

            @Override
            public char key() {
                return this.iter.key();
            }

            @Override
            public float value() {
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
            public float setValue(float f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public float putIfAbsent(char c0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharFloatProcedure tcharfloatprocedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char c0, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(char c0, float f, float f1) {
        throw new UnsupportedOperationException();
    }
}
