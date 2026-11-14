package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleIntMap implements TDoubleIntMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleIntMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TIntCollection values = null;

    public TSynchronizedDoubleIntMap(TDoubleIntMap tdoubleintmap) {
        if (tdoubleintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubleintmap;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleIntMap(TDoubleIntMap tdoubleintmap, Object object) {
        this.m = tdoubleintmap;
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
    public boolean containsValue(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public int get(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(d0);
        }
    }

    @Override
    public int put(double d0, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(d0, i);
        }
    }

    @Override
    public int remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(d0);
        }
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Integer> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TDoubleIntMap tdoubleintmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tdoubleintmap);
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
    public TIntCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedIntCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public int[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public int[] values(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(aint);
        }
    }

    @Override
    public TDoubleIntIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public int putIfAbsent(double d0, int i) {
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
    public boolean forEachValue(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tintprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TDoubleIntProcedure tdoubleintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tdoubleintprocedure);
        }
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tintfunction);
        }
    }

    @Override
    public boolean retainEntries(TDoubleIntProcedure tdoubleintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tdoubleintprocedure);
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
    public boolean adjustValue(double d0, int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(d0, i);
        }
    }

    @Override
    public int adjustOrPutValue(double d0, int i, int j) {
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
