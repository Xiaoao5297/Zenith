package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedDoubleShortMap implements TDoubleShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TDoubleShortMap m;
    final Object mutex;
    private transient TDoubleSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedDoubleShortMap(TDoubleShortMap tdoubleshortmap) {
        if (tdoubleshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tdoubleshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedDoubleShortMap(TDoubleShortMap tdoubleshortmap, Object object) {
        this.m = tdoubleshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(d0);
        }
    }

    @Override
    public short put(double d0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(d0, short0);
        }
    }

    @Override
    public short remove(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(d0);
        }
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TDoubleShortMap tdoubleshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tdoubleshortmap);
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
    public TDoubleShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(double d0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(d0, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TDoubleShortProcedure tdoubleshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tdoubleshortprocedure);
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
    public boolean retainEntries(TDoubleShortProcedure tdoubleshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tdoubleshortprocedure);
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
    public boolean adjustValue(double d0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(d0, short0);
        }
    }

    @Override
    public short adjustOrPutValue(double d0, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(d0, short0, short1);
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
