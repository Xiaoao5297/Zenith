package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedShortObjectMap<V> implements TShortObjectMap<V>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortObjectMap<V> m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedShortObjectMap(TShortObjectMap<V> tshortobjectmap) {
        if (tshortobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortobjectmap;
            this.mutex = this;
        }
    }

    public TSynchronizedShortObjectMap(TShortObjectMap<V> tshortobjectmap, Object object) {
        this.m = tshortobjectmap;
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
    public boolean containsValue(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(object);
        }
    }

    @Override
    public V get(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(short0);
        }
    }

    @Override
    public V put(short short0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(short0, v0);
        }
    }

    @Override
    public V remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(short0);
        }
    }

    @Override
    public void putAll(Map<? extends Short, ? extends V> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TShortObjectMap<? extends V> tshortobjectmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tshortobjectmap);
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
    public TShortObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public V putIfAbsent(short short0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(short0, v0);
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
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tobjectprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TShortObjectProcedure<? super V> tshortobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tshortobjectprocedure);
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
    public boolean retainEntries(TShortObjectProcedure<? super V> tshortobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tshortobjectprocedure);
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
