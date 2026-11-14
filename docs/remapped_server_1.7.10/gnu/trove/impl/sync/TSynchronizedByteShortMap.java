package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteShortMap implements TByteShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TByteShortMap m;
    final Object mutex;
    private transient TByteSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedByteShortMap(TByteShortMap tbyteshortmap) {
        if (tbyteshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbyteshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedByteShortMap(TByteShortMap tbyteshortmap, Object object) {
        this.m = tbyteshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(b0);
        }
    }

    @Override
    public short put(byte b0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(b0, short0);
        }
    }

    @Override
    public short remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(b0);
        }
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TByteShortMap tbyteshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tbyteshortmap);
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
    public TShortCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public short[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public short[] values(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(ashort);
        }
    }

    @Override
    public TByteShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(byte b0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(b0, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TByteShortProcedure tbyteshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tbyteshortprocedure);
        }
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tshortfunction);
        }
    }

    @Override
    public boolean retainEntries(TByteShortProcedure tbyteshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tbyteshortprocedure);
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
    public boolean adjustValue(byte b0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(b0, short0);
        }
    }

    @Override
    public short adjustOrPutValue(byte b0, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(b0, short0, short1);
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
