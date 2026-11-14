package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedIntObjectMap<V> implements TIntObjectMap<V>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TIntObjectMap<V> m;
    final Object mutex;
    private transient TIntSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedIntObjectMap(TIntObjectMap<V> tintobjectmap) {
        if (tintobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintobjectmap;
            this.mutex = this;
        }
    }

    public TSynchronizedIntObjectMap(TIntObjectMap<V> tintobjectmap, Object object) {
        this.m = tintobjectmap;
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
    public boolean containsKey(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(i);
        }
    }

    @Override
    public boolean containsValue(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(object);
        }
    }

    @Override
    public V get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public V put(int i, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, v0);
        }
    }

    @Override
    public V remove(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TIntObjectMap<? extends V> tintobjectmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tintobjectmap);
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
    public TIntSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedIntSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public int[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public int[] keys(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(aint);
        }
    }

    @Override
    public Collection<V> valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new SynchronizedCollection<>(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public Object[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public V[] values(V[] av) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(av);
        }
    }

    @Override
    public TIntObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public V putIfAbsent(int i, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, v0);
        }
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tintprocedure);
        }
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tobjectprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TIntObjectProcedure<? super V> tintobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tintobjectprocedure);
        }
    }

    @Override
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tobjectfunction);
        }
    }

    @Override
    public boolean retainEntries(TIntObjectProcedure<? super V> tintobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tintobjectprocedure);
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
