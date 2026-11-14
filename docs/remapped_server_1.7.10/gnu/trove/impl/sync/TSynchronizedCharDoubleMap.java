package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharDoubleMap implements TCharDoubleMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharDoubleMap m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient TDoubleCollection values = null;

    public TSynchronizedCharDoubleMap(TCharDoubleMap tchardoublemap) {
        if (tchardoublemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tchardoublemap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharDoubleMap(TCharDoubleMap tchardoublemap, Object object) {
        this.m = tchardoublemap;
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
    public boolean containsKey(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(c0);
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
    public double get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public double put(char c0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, d0);
        }
    }

    @Override
    public double remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Double> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharDoubleMap tchardoublemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tchardoublemap);
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
    public TCharSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedCharSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public char[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public char[] keys(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(achar);
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
    public TCharDoubleIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public double putIfAbsent(char c0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, d0);
        }
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tcharprocedure);
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
    public boolean forEachEntry(TCharDoubleProcedure tchardoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tchardoubleprocedure);
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
    public boolean retainEntries(TCharDoubleProcedure tchardoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tchardoubleprocedure);
        }
    }

    @Override
    public boolean increment(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(c0);
        }
    }

    @Override
    public boolean adjustValue(char c0, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(c0, d0);
        }
    }

    @Override
    public double adjustOrPutValue(char c0, double d0, double d1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(c0, d0, d1);
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
