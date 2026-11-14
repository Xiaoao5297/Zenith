package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteDoubleMap implements TByteDoubleMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TByteDoubleMap m;
    final Object mutex;
    private transient TByteSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedByteDoubleMap(TByteDoubleMap tbytedoublemap) {
        if (tbytedoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytedoublemap;
            this.mutex = this;
        }
    }

    public TSynchronizedByteDoubleMap(TByteDoubleMap tbytedoublemap, Object object) {
        this.m = tbytedoublemap;
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
    public boolean containsValue(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(d0);
        }
    }

    @Override
    public double get(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(b0);
        }
    }

    @Override
    public double put(byte b0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(b0, d0);
        }
    }

    @Override
    public double remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(b0);
        }
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Double> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TByteDoubleMap tbytedoublemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tbytedoublemap);
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
    public TDoubleCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedDoubleCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public double[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public double[] values(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(adouble);
        }
    }

    @Override
    public TByteDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public double putIfAbsent(byte b0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(b0, d0);
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
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tdoubleprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TByteDoubleProcedure tbytedoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tbytedoubleprocedure);
        }
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tdoublefunction);
        }
    }

    @Override
    public boolean retainEntries(TByteDoubleProcedure tbytedoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tbytedoubleprocedure);
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
    public boolean adjustValue(byte b0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(b0, d0);
        }
    }

    @Override
    public double adjustOrPutValue(byte b0, double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(b0, d0, d1);
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
