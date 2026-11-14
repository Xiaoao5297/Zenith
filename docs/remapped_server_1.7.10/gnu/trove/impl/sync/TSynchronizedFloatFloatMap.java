package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatFloatMap implements TFloatFloatMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatFloatMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedFloatFloatMap(TFloatFloatMap tfloatfloatmap) {
        if (tfloatfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatfloatmap;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatFloatMap(TFloatFloatMap tfloatfloatmap, Object object) {
        this.m = tfloatfloatmap;
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
    public boolean containsKey(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(f);
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
    public float get(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(f);
        }
    }

    @Override
    public float put(float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(f, f1);
        }
    }

    @Override
    public float remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(f);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Float> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TFloatFloatMap tfloatfloatmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tfloatfloatmap);
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
    public TFloatSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedFloatSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public float[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public float[] keys(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(afloat);
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
    public TFloatFloatIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public float putIfAbsent(float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(f, f1);
        }
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tfloatprocedure);
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
    public boolean forEachEntry(TFloatFloatProcedure tfloatfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tfloatfloatprocedure);
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
    public boolean retainEntries(TFloatFloatProcedure tfloatfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tfloatfloatprocedure);
        }
    }

    @Override
    public boolean increment(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(f);
        }
    }

    @Override
    public boolean adjustValue(float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(f, f1);
        }
    }

    @Override
    public float adjustOrPutValue(float f, float f1, float f2) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(f, f1, f2);
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
