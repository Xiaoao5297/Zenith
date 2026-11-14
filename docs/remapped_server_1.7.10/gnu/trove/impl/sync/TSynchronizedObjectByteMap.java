package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TSynchronizedObjectByteMap<K> implements TObjectByteMap<K>, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TObjectByteMap<K> m;
    final Object mutex;
    private transient Set<K> keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedObjectByteMap(TObjectByteMap<K> tobjectbytemap) {
        if (tobjectbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tobjectbytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedObjectByteMap(TObjectByteMap<K> tobjectbytemap, Object object) {
        this.m = tobjectbytemap;
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
    public boolean containsValue(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(b0);
        }
    }

    @Override
    public byte get(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(object);
        }
    }

    @Override
    public byte put(K k0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(k0, b0);
        }
    }

    @Override
    public byte remove(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(object);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TObjectByteMap<? extends K> tobjectbytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tobjectbytemap);
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
    public TByteCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedByteCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public byte[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public byte[] values(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(abyte);
        }
    }

    @Override
    public TObjectByteIterator<K> iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(K k0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(k0, b0);
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
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tbyteprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tobjectbyteprocedure);
        }
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tbytefunction);
        }
    }

    @Override
    public boolean retainEntries(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tobjectbyteprocedure);
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
    public boolean adjustValue(K k0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(k0, b0);
        }
    }

    @Override
    public byte adjustOrPutValue(K k0, byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(k0, b0, b1);
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
