package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongByteMap implements TLongByteMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongByteMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedLongByteMap(TLongByteMap tlongbytemap) {
        if (tlongbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongbytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedLongByteMap(TLongByteMap tlongbytemap, Object object) {
        this.m = tlongbytemap;
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
    public boolean containsValue(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(b0);
        }
    }

    @Override
    public byte get(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public byte put(long i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, b0);
        }
    }

    @Override
    public byte remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TLongByteMap tlongbytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tlongbytemap);
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
    public TLongByteIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(long i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, b0);
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
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tbyteprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TLongByteProcedure tlongbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tlongbyteprocedure);
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
    public boolean retainEntries(TLongByteProcedure tlongbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tlongbyteprocedure);
        }
    }

    @Override
    public boolean increment(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(i);
        }
    }

    @Override
    public boolean adjustValue(long i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, b0);
        }
    }

    @Override
    public byte adjustOrPutValue(long i, byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, b0, b1);
        }
    }

    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.m.equals(object);
        }
    }

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
