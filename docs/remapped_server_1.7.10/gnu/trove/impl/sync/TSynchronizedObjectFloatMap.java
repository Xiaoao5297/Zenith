package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectFloatMap<K> implements TObjectFloatMap<K>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectFloatMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedObjectFloatMap(TObjectFloatMap<K> tobjectfloatmap) {
        if (tobjectfloatmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectfloatmap;
            this.mutex = this;
        }
    }

    public TSynchronizedObjectFloatMap(TObjectFloatMap<K> tobjectfloatmap, Object object) {
        this.m = tobjectfloatmap;
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
    public boolean containsKey(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(object);
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
    public float get(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(object);
        }
    }

    @Override
    public float put(K k0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(k0, f);
        }
    }

    @Override
    public float remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(object);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> tobjectfloatmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tobjectfloatmap);
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
    public Set<K> keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new SynchronizedSet<>(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public Object[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public K[] keys(K[] ak) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(ak);
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
    public TObjectFloatIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public float putIfAbsent(K k0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(k0, f);
        }
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tobjectprocedure);
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
    public boolean forEachEntry(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tobjectfloatprocedure);
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
    public boolean retainEntries(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tobjectfloatprocedure);
        }
    }

    @Override
    public boolean increment(K k0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(k0);
        }
    }

    @Override
    public boolean adjustValue(K k0, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(k0, f);
        }
    }

    @Override
    public float adjustOrPutValue(K k0, float f, float f1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(k0, f, f1);
        }
    }

    @Override
    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.equals(object);
        }
    }

    @Override
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
