package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleLongMap implements TDoubleLongMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleLongMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TLongCollection values = null;

    public TSynchronizedDoubleLongMap(TDoubleLongMap tdoublelongmap) {
        if (tdoublelongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoublelongmap;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleLongMap(TDoubleLongMap tdoublelongmap, Object object) {
        this.m = tdoublelongmap;
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
    public boolean containsValue(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public long get(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(d0);
        }
    }

    @Override
    public long put(double d0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(d0, i);
        }
    }

    @Override
    public long remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(d0);
        }
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Long> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TDoubleLongMap tdoublelongmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tdoublelongmap);
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
    public TDoubleLongIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public long putIfAbsent(double d0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(d0, i);
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
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tlongprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TDoubleLongProcedure tdoublelongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tdoublelongprocedure);
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
    public boolean retainEntries(TDoubleLongProcedure tdoublelongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tdoublelongprocedure);
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
    public boolean adjustValue(double d0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(d0, i);
        }
    }

    @Override
    public long adjustOrPutValue(double d0, long i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(d0, i, j);
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
