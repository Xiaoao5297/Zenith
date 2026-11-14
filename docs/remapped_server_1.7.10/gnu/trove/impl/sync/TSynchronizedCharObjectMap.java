package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedCharObjectMap<V> implements TCharObjectMap<V>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharObjectMap<V> m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedCharObjectMap(TCharObjectMap<V> tcharobjectmap) {
        if (tcharobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharobjectmap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharObjectMap(TCharObjectMap<V> tcharobjectmap, Object object) {
        this.m = tcharobjectmap;
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
    public boolean containsKey(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(c0);
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
    public V get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public V put(char c0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, v0);
        }
    }

    @Override
    public V remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharObjectMap<? extends V> tcharobjectmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tcharobjectmap);
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
    public TCharSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedCharSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public char[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public char[] keys(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(achar);
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
    public TCharObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public V putIfAbsent(char c0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, v0);
        }
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tcharprocedure);
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
    public boolean forEachEntry(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tcharobjectprocedure);
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
    public boolean retainEntries(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tcharobjectprocedure);
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
