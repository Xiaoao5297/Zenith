package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedLongObjectMap<V> implements TLongObjectMap<V>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongObjectMap<V> m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedLongObjectMap(TLongObjectMap<V> tlongobjectmap) {
        if (tlongobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongobjectmap;
            this.mutex = this;
        }
    }

    public TSynchronizedLongObjectMap(TLongObjectMap<V> tlongobjectmap, Object object) {
        this.m = tlongobjectmap;
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
    public boolean containsValue(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(object);
        }
    }

    @Override
    public V get(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public V put(long i, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, v0);
        }
    }

    @Override
    public V remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TLongObjectMap<? extends V> tlongobjectmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tlongobjectmap);
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
    public TLongObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public V putIfAbsent(long i, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, v0);
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
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tobjectprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tlongobjectprocedure);
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
    public boolean retainEntries(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tlongobjectprocedure);
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
