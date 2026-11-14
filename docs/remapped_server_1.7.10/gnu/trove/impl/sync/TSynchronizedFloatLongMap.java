package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatLongMap implements TFloatLongMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatLongMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TLongCollection values = null;

    public TSynchronizedFloatLongMap(TFloatLongMap tfloatlongmap) {
        if (tfloatlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatlongmap;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatLongMap(TFloatLongMap tfloatlongmap, Object object) {
        this.m = tfloatlongmap;
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
    public boolean containsValue(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public long get(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(f);
        }
    }

    @Override
    public long put(float f, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(f, i);
        }
    }

    @Override
    public long remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(f);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Long> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TFloatLongMap tfloatlongmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tfloatlongmap);
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
    public TLongCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedLongCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public long[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public long[] values(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(along);
        }
    }

    @Override
    public TFloatLongIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public long putIfAbsent(float f, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(f, i);
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
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tlongprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TFloatLongProcedure tfloatlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tfloatlongprocedure);
        }
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tlongfunction);
        }
    }

    @Override
    public boolean retainEntries(TFloatLongProcedure tfloatlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tfloatlongprocedure);
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
    public boolean adjustValue(float f, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(f, i);
        }
    }

    @Override
    public long adjustOrPutValue(float f, long i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(f, i, j);
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
