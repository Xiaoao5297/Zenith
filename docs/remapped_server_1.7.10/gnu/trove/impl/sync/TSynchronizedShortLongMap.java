package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortLongMap implements TShortLongMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortLongMap m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient TLongCollection values = null;

    public TSynchronizedShortLongMap(TShortLongMap tshortlongmap) {
        if (tshortlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortlongmap;
            this.mutex = this;
        }
    }

    public TSynchronizedShortLongMap(TShortLongMap tshortlongmap, Object object) {
        this.m = tshortlongmap;
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
    public boolean containsKey(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(short0);
        }
    }

    @Override
    public boolean containsValue(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public long get(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(short0);
        }
    }

    @Override
    public long put(short short0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(short0, i);
        }
    }

    @Override
    public long remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(short0);
        }
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Long> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TShortLongMap tshortlongmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tshortlongmap);
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
    public TShortSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public short[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public short[] keys(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(ashort);
        }
    }

    @Override
    public TLongCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedLongCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public long[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public long[] values(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(along);
        }
    }

    @Override
    public TShortLongIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public long putIfAbsent(short short0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(short0, i);
        }
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tshortprocedure);
        }
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tlongprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TShortLongProcedure tshortlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tshortlongprocedure);
        }
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tlongfunction);
        }
    }

    @Override
    public boolean retainEntries(TShortLongProcedure tshortlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tshortlongprocedure);
        }
    }

    @Override
    public boolean increment(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(short0);
        }
    }

    @Override
    public boolean adjustValue(short short0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(short0, i);
        }
    }

    @Override
    public long adjustOrPutValue(short short0, long i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(short0, i, j);
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
