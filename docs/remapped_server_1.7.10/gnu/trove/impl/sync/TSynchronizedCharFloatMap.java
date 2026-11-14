package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharFloatMap implements TCharFloatMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharFloatMap m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedCharFloatMap(TCharFloatMap tcharfloatmap) {
        if (tcharfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharfloatmap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharFloatMap(TCharFloatMap tcharfloatmap, Object object) {
        this.m = tcharfloatmap;
        this.mutex = object;
    }

    @Override
    public int size() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.size();
        }
    }

    @Override
    public boolean isEmpty() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.isEmpty();
        }
    }

    @Override
    public boolean containsKey(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(c0);
        }
    }

    @Override
    public boolean containsValue(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(f);
        }
    }

    @Override
    public float get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public float put(char c0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, f);
        }
    }

    @Override
    public float remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Float> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharFloatMap tcharfloatmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tcharfloatmap);
        }
    }

    @Override
    public void clear() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.clear();
        }
    }

    @Override
    public TCharSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedCharSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public char[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public char[] keys(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(achar);
        }
    }

    @Override
    public TFloatCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedFloatCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public float[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public float[] values(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(afloat);
        }
    }

    @Override
    public TCharFloatIterator iterator() {
        return this.m.iterator();
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
    public float putIfAbsent(char c0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, f);
        }
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tcharprocedure);
        }
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tfloatprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TCharFloatProcedure tcharfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tcharfloatprocedure);
        }
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tfloatfunction);
        }
    }

    @Override
    public boolean retainEntries(TCharFloatProcedure tcharfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tcharfloatprocedure);
        }
    }

    @Override
    public boolean increment(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(c0);
        }
    }

    @Override
    public boolean adjustValue(char c0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(c0, f);
        }
    }

    @Override
    public float adjustOrPutValue(char c0, float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(c0, f, f1);
        }
    }

    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.equals(object);
        }
    }

    public int hashCode() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.hashCode();
        }
    }

    public String toString() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.toString();
        }
    }

    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        Object object = this.mutex;

        synchronized (this.mutex) {
            objectoutputstream.defaultWriteObject();
        }
    }
}
