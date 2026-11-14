package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatShortMap implements TFloatShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatShortMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedFloatShortMap(TFloatShortMap tfloatshortmap) {
        if (tfloatshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatShortMap(TFloatShortMap tfloatshortmap, Object object) {
        this.m = tfloatshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(f);
        }
    }

    @Override
    public short put(float f, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(f, short0);
        }
    }

    @Override
    public short remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(f);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TFloatShortMap tfloatshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tfloatshortmap);
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
    public TShortCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public short[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public short[] values(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(ashort);
        }
    }

    @Override
    public TFloatShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(float f, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(f, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TFloatShortProcedure tfloatshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tfloatshortprocedure);
        }
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tshortfunction);
        }
    }

    @Override
    public boolean retainEntries(TFloatShortProcedure tfloatshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tfloatshortprocedure);
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
    public boolean adjustValue(float f, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(f, short0);
        }
    }

    @Override
    public short adjustOrPutValue(float f, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(f, short0, short1);
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
