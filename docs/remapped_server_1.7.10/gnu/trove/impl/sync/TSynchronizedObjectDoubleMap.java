package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectDoubleMap<K> implements TObjectDoubleMap<K>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectDoubleMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedObjectDoubleMap(TObjectDoubleMap<K> tobjectdoublemap) {
        if (tobjectdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectdoublemap;
            this.mutex = this;
        }
    }

    public TSynchronizedObjectDoubleMap(TObjectDoubleMap<K> tobjectdoublemap, Object object) {
        this.m = tobjectdoublemap;
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
    public boolean containsValue(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(d0);
        }
    }

    @Override
    public double get(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(object);
        }
    }

    @Override
    public double put(K k0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(k0, d0);
        }
    }

    @Override
    public double remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(object);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TObjectDoubleMap<? extends K> tobjectdoublemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tobjectdoublemap);
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
    public TDoubleCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedDoubleCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public double[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public double[] values(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(adouble);
        }
    }

    @Override
    public TObjectDoubleIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public double putIfAbsent(K k0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(k0, d0);
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
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tdoubleprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tobjectdoubleprocedure);
        }
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tdoublefunction);
        }
    }

    @Override
    public boolean retainEntries(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tobjectdoubleprocedure);
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
    public boolean adjustValue(K k0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(k0, d0);
        }
    }

    @Override
    public double adjustOrPutValue(K k0, double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(k0, d0, d1);
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
