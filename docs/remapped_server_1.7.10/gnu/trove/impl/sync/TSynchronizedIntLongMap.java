package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedIntLongMap implements TIntLongMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TIntLongMap m;
    final Object mutex;
    private transient TIntSet keySet = null;
    private transient TLongCollection values = null;

    public TSynchronizedIntLongMap(TIntLongMap tintlongmap) {
        if (tintlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintlongmap;
            this.mutex = this;
        }
    }

    public TSynchronizedIntLongMap(TIntLongMap tintlongmap, Object object) {
        this.m = tintlongmap;
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
    public boolean containsKey(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(i);
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
    public long get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public long put(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, j);
        }
    }

    @Override
    public long remove(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Long> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TIntLongMap tintlongmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tintlongmap);
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
    public TIntSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedIntSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public int[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public int[] keys(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(aint);
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
    public TIntLongIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public long putIfAbsent(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, j);
        }
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tintprocedure);
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
    public boolean forEachEntry(TIntLongProcedure tintlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tintlongprocedure);
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
    public boolean retainEntries(TIntLongProcedure tintlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tintlongprocedure);
        }
    }

    @Override
    public boolean increment(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(i);
        }
    }

    @Override
    public boolean adjustValue(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, j);
        }
    }

    @Override
    public long adjustOrPutValue(int i, long j, long k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, j, k);
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
