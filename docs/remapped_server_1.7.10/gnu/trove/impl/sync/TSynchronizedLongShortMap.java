package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongShortMap implements TLongShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongShortMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedLongShortMap(TLongShortMap tlongshortmap) {
        if (tlongshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedLongShortMap(TLongShortMap tlongshortmap, Object object) {
        this.m = tlongshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public short put(long i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, short0);
        }
    }

    @Override
    public short remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TLongShortMap tlongshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tlongshortmap);
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
    public TLongShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(long i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TLongShortProcedure tlongshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tlongshortprocedure);
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
    public boolean retainEntries(TLongShortProcedure tlongshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tlongshortprocedure);
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
    public boolean adjustValue(long i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, short0);
        }
    }

    @Override
    public short adjustOrPutValue(long i, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, short0, short1);
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
