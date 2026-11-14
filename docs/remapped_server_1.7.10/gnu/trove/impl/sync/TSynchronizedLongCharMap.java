package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedLongCharMap implements TLongCharMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongCharMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TCharCollection values = null;

    public TSynchronizedLongCharMap(TLongCharMap tlongcharmap) {
        if (tlongcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tlongcharmap;
            this.mutex = this;
        }
    }

    public TSynchronizedLongCharMap(TLongCharMap tlongcharmap, Object object) {
        this.m = tlongcharmap;
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
    public boolean containsValue(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(c0);
        }
    }

    @Override
    public char get(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public char put(long i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, c0);
        }
    }

    @Override
    public char remove(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Character> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TLongCharMap tlongcharmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tlongcharmap);
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
    public TCharCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedCharCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public char[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public char[] values(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(achar);
        }
    }

    @Override
    public TLongCharIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public char putIfAbsent(long i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, c0);
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
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tcharprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TLongCharProcedure tlongcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tlongcharprocedure);
        }
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tcharfunction);
        }
    }

    @Override
    public boolean retainEntries(TLongCharProcedure tlongcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tlongcharprocedure);
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
    public boolean adjustValue(long i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, c0);
        }
    }

    @Override
    public char adjustOrPutValue(long i, char c0, char c1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, c0, c1);
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
