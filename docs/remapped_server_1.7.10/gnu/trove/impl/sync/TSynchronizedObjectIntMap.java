package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectIntMap<K> implements TObjectIntMap<K>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectIntMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TIntCollection values = null;

    public TSynchronizedObjectIntMap(TObjectIntMap<K> tobjectintmap) {
        if (tobjectintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectintmap;
            this.mutex = this;
        }
    }

    public TSynchronizedObjectIntMap(TObjectIntMap<K> tobjectintmap, Object object) {
        this.m = tobjectintmap;
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
    public boolean containsValue(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public int get(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(object);
        }
    }

    @Override
    public int put(K k0, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(k0, i);
        }
    }

    @Override
    public int remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(object);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TObjectIntMap<? extends K> tobjectintmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tobjectintmap);
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
    public TObjectIntIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public int putIfAbsent(K k0, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(k0, i);
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
    public boolean forEachValue(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tintprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> tobjectintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tobjectintprocedure);
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
    public boolean retainEntries(TObjectIntProcedure<? super K> tobjectintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tobjectintprocedure);
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
    public boolean adjustValue(K k0, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(k0, i);
        }
    }

    @Override
    public int adjustOrPutValue(K k0, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(k0, i, j);
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
