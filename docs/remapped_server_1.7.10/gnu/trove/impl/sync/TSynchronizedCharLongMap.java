package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharLongMap implements TCharLongMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharLongMap m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient TLongCollection values = null;

    public TSynchronizedCharLongMap(TCharLongMap tcharlongmap) {
        if (tcharlongmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharlongmap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharLongMap(TCharLongMap tcharlongmap, Object object) {
        this.m = tcharlongmap;
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
    public boolean containsValue(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public long get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public long put(char c0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, i);
        }
    }

    @Override
    public long remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Long> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharLongMap tcharlongmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tcharlongmap);
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
    public TCharLongIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public long putIfAbsent(char c0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, i);
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
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tlongprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TCharLongProcedure tcharlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tcharlongprocedure);
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
    public boolean retainEntries(TCharLongProcedure tcharlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tcharlongprocedure);
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
    public boolean adjustValue(char c0, long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(c0, i);
        }
    }

    @Override
    public long adjustOrPutValue(char c0, long i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(c0, i, j);
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
