package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortDoubleMap implements TShortDoubleMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortDoubleMap m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedShortDoubleMap(TShortDoubleMap tshortdoublemap) {
        if (tshortdoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortdoublemap;
            this.mutex = this;
        }
    }

    public TSynchronizedShortDoubleMap(TShortDoubleMap tshortdoublemap, Object object) {
        this.m = tshortdoublemap;
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
    public boolean containsValue(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(d0);
        }
    }

    @Override
    public double get(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(short0);
        }
    }

    @Override
    public double put(short short0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(short0, d0);
        }
    }

    @Override
    public double remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(short0);
        }
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Double> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TShortDoubleMap tshortdoublemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tshortdoublemap);
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
    public TShortDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public double putIfAbsent(short short0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(short0, d0);
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
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tdoubleprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TShortDoubleProcedure tshortdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tshortdoubleprocedure);
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
    public boolean retainEntries(TShortDoubleProcedure tshortdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tshortdoubleprocedure);
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
    public boolean adjustValue(short short0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(short0, d0);
        }
    }

    @Override
    public double adjustOrPutValue(short short0, double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(short0, d0, d1);
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
