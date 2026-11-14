package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleDoubleMap implements TDoubleDoubleMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleDoubleMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap tdoubledoublemap) {
        if (tdoubledoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubledoublemap;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap tdoubledoublemap, Object object) {
        this.m = tdoubledoublemap;
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
    public boolean containsValue(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(d0);
        }
    }

    @Override
    public double get(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(d0);
        }
    }

    @Override
    public double put(double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(d0, d1);
        }
    }

    @Override
    public double remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(d0);
        }
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TDoubleDoubleMap tdoubledoublemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tdoubledoublemap);
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
    public TDoubleDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public double putIfAbsent(double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(d0, d1);
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
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tdoubleprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tdoubledoubleprocedure);
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
    public boolean retainEntries(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tdoubledoubleprocedure);
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
    public boolean adjustValue(double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(d0, d1);
        }
    }

    @Override
    public double adjustOrPutValue(double d0, double d1, double d2) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(d0, d1, d2);
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
