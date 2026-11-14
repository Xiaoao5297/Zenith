package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.procedure.TFloatIntProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatIntMap implements TFloatIntMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatIntMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TIntCollection values = null;

    public TSynchronizedFloatIntMap(TFloatIntMap tfloatintmap) {
        if (tfloatintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatintmap;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatIntMap(TFloatIntMap tfloatintmap, Object object) {
        this.m = tfloatintmap;
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
    public boolean containsValue(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public int get(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(f);
        }
    }

    @Override
    public int put(float f, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(f, i);
        }
    }

    @Override
    public int remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(f);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Integer> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TFloatIntMap tfloatintmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tfloatintmap);
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
    public TIntCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedIntCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public int[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public int[] values(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(aint);
        }
    }

    @Override
    public TFloatIntIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public int putIfAbsent(float f, int i) {
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
    public boolean forEachValue(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tintprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TFloatIntProcedure tfloatintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tfloatintprocedure);
        }
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tintfunction);
        }
    }

    @Override
    public boolean retainEntries(TFloatIntProcedure tfloatintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tfloatintprocedure);
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
    public boolean adjustValue(float f, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(f, i);
        }
    }

    @Override
    public int adjustOrPutValue(float f, int i, int j) {
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
