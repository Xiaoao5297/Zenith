package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleByteMap implements TDoubleByteMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleByteMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedDoubleByteMap(TDoubleByteMap tdoublebytemap) {
        if (tdoublebytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoublebytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleByteMap(TDoubleByteMap tdoublebytemap, Object object) {
        this.m = tdoublebytemap;
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
    public boolean containsKey(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(d0);
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
    public byte get(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(d0);
        }
    }

    @Override
    public byte put(double d0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(d0, b0);
        }
    }

    @Override
    public byte remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(d0);
        }
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TDoubleByteMap tdoublebytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tdoublebytemap);
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
    public TDoubleSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedDoubleSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public double[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public double[] keys(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(adouble);
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
    public TDoubleByteIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(double d0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(d0, b0);
        }
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tdoubleprocedure);
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
    public boolean forEachEntry(TDoubleByteProcedure tdoublebyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tdoublebyteprocedure);
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
    public boolean retainEntries(TDoubleByteProcedure tdoublebyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tdoublebyteprocedure);
        }
    }

    @Override
    public boolean increment(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(d0);
        }
    }

    @Override
    public boolean adjustValue(double d0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(d0, b0);
        }
    }

    @Override
    public byte adjustOrPutValue(double d0, byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(d0, b0, b1);
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
