package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongFloatMap implements TLongFloatMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongFloatMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedLongFloatMap(TLongFloatMap tlongfloatmap) {
        if (tlongfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongfloatmap;
            this.mutex = this;
        }
    }

    public TSynchronizedLongFloatMap(TLongFloatMap tlongfloatmap, Object object) {
        this.m = tlongfloatmap;
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
    public boolean containsKey(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(i);
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
    public float get(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public float put(long i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, f);
        }
    }

    @Override
    public float remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Float> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TLongFloatMap tlongfloatmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tlongfloatmap);
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
    public TLongSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedLongSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public long[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public long[] keys(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(along);
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
    public TLongFloatIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public float putIfAbsent(long i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, f);
        }
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tlongprocedure);
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
    public boolean forEachEntry(TLongFloatProcedure tlongfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tlongfloatprocedure);
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
    public boolean retainEntries(TLongFloatProcedure tlongfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tlongfloatprocedure);
        }
    }

    @Override
    public boolean increment(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(i);
        }
    }

    @Override
    public boolean adjustValue(long i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, f);
        }
    }

    @Override
    public float adjustOrPutValue(long i, float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, f, f1);
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
