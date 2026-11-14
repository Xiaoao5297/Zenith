package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteByteMap implements TByteByteMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TByteByteMap m;
    final Object mutex;
    private transient TByteSet keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedByteByteMap(TByteByteMap tbytebytemap) {
        if (tbytebytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytebytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedByteByteMap(TByteByteMap tbytebytemap, Object object) {
        this.m = tbytebytemap;
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
    public boolean containsValue(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(b0);
        }
    }

    @Override
    public byte get(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(b0);
        }
    }

    @Override
    public byte put(byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(b0, b1);
        }
    }

    @Override
    public byte remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(b0);
        }
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TByteByteMap tbytebytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tbytebytemap);
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
    public TByteByteIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(b0, b1);
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
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tbyteprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TByteByteProcedure tbytebyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tbytebyteprocedure);
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
    public boolean retainEntries(TByteByteProcedure tbytebyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tbytebyteprocedure);
        }
    }

    @Override
    public boolean increment(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(b0);
        }
    }

    @Override
    public boolean adjustValue(byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(b0, b1);
        }
    }

    @Override
    public byte adjustOrPutValue(byte b0, byte b1, byte b2) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(b0, b1, b2);
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
