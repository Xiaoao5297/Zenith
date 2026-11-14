package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedFloatByteMap implements TFloatByteMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatByteMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedFloatByteMap(TFloatByteMap tfloatbytemap) {
        if (tfloatbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tfloatbytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedFloatByteMap(TFloatByteMap tfloatbytemap, Object object) {
        this.m = tfloatbytemap;
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
    public boolean containsKey(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(f);
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
    public byte get(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(f);
        }
    }

    @Override
    public byte put(float f, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(f, b0);
        }
    }

    @Override
    public byte remove(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(f);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TFloatByteMap tfloatbytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tfloatbytemap);
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
    public TFloatSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedFloatSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public float[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public float[] keys(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(afloat);
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
    public TFloatByteIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(float f, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(f, b0);
        }
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tfloatprocedure);
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
    public boolean forEachEntry(TFloatByteProcedure tfloatbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tfloatbyteprocedure);
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
    public boolean retainEntries(TFloatByteProcedure tfloatbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tfloatbyteprocedure);
        }
    }

    @Override
    public boolean increment(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(f);
        }
    }

    @Override
    public boolean adjustValue(float f, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(f, b0);
        }
    }

    @Override
    public byte adjustOrPutValue(float f, byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(f, b0, b1);
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
