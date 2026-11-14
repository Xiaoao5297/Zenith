package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectCharMap<K> implements TObjectCharMap<K>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectCharMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TCharCollection values = null;

    public TSynchronizedObjectCharMap(TObjectCharMap<K> tobjectcharmap) {
        if (tobjectcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectcharmap;
            this.mutex = this;
        }
    }

    public TSynchronizedObjectCharMap(TObjectCharMap<K> tobjectcharmap, Object object) {
        this.m = tobjectcharmap;
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
    public boolean containsValue(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(c0);
        }
    }

    @Override
    public char get(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(object);
        }
    }

    @Override
    public char put(K k0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(k0, c0);
        }
    }

    @Override
    public char remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(object);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TObjectCharMap<? extends K> tobjectcharmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tobjectcharmap);
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
    public TCharCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedCharCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public char[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public char[] values(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(achar);
        }
    }

    @Override
    public TObjectCharIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public char putIfAbsent(K k0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(k0, c0);
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
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tcharprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TObjectCharProcedure<? super K> tobjectcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tobjectcharprocedure);
        }
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tcharfunction);
        }
    }

    @Override
    public boolean retainEntries(TObjectCharProcedure<? super K> tobjectcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tobjectcharprocedure);
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
    public boolean adjustValue(K k0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(k0, c0);
        }
    }

    @Override
    public char adjustOrPutValue(K k0, char c0, char c1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(k0, c0, c1);
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
