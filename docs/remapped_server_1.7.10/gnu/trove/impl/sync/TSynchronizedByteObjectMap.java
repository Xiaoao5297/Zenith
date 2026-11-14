package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedByteObjectMap<V> implements TByteObjectMap<V>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TByteObjectMap<V> m;
    final Object mutex;
    private transient TByteSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedByteObjectMap(TByteObjectMap<V> tbyteobjectmap) {
        if (tbyteobjectmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbyteobjectmap;
            this.mutex = this;
        }
    }

    public TSynchronizedByteObjectMap(TByteObjectMap<V> tbyteobjectmap, Object object) {
        this.m = tbyteobjectmap;
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
    public boolean containsKey(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(b0);
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
    public V get(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(b0);
        }
    }

    @Override
    public V put(byte b0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(b0, v0);
        }
    }

    @Override
    public V remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(b0);
        }
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TByteObjectMap<? extends V> tbyteobjectmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tbyteobjectmap);
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
    public TByteSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedByteSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public byte[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public byte[] keys(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(abyte);
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
    public TByteObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public V putIfAbsent(byte b0, V v0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(b0, v0);
        }
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tbyteprocedure);
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
    public boolean forEachEntry(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tbyteobjectprocedure);
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
    public boolean retainEntries(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tbyteobjectprocedure);
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
