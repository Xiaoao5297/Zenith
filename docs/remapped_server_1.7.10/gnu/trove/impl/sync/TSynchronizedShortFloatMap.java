package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortFloatMap implements TShortFloatMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortFloatMap m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedShortFloatMap(TShortFloatMap tshortfloatmap) {
        if (tshortfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortfloatmap;
            this.mutex = this;
        }
    }

    public TSynchronizedShortFloatMap(TShortFloatMap tshortfloatmap, Object object) {
        this.m = tshortfloatmap;
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
    public boolean containsKey(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(short0);
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
    public float get(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(short0);
        }
    }

    @Override
    public float put(short short0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(short0, f);
        }
    }

    @Override
    public float remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(short0);
        }
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Float> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TShortFloatMap tshortfloatmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tshortfloatmap);
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
    public TShortSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public short[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public short[] keys(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(ashort);
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
    public TShortFloatIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public float putIfAbsent(short short0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(short0, f);
        }
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tshortprocedure);
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
    public boolean forEachEntry(TShortFloatProcedure tshortfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tshortfloatprocedure);
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
    public boolean retainEntries(TShortFloatProcedure tshortfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tshortfloatprocedure);
        }
    }

    @Override
    public boolean increment(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(short0);
        }
    }

    @Override
    public boolean adjustValue(short short0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(short0, f);
        }
    }

    @Override
    public float adjustOrPutValue(short short0, float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(short0, f, f1);
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
