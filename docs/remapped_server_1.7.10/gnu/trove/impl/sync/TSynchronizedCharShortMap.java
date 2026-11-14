package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharShortMap implements TCharShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharShortMap m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedCharShortMap(TCharShortMap tcharshortmap) {
        if (tcharshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharShortMap(TCharShortMap tcharshortmap, Object object) {
        this.m = tcharshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public short put(char c0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, short0);
        }
    }

    @Override
    public short remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharShortMap tcharshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tcharshortmap);
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
    public TCharShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(char c0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TCharShortProcedure tcharshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tcharshortprocedure);
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
    public boolean retainEntries(TCharShortProcedure tcharshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tcharshortprocedure);
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
    public boolean adjustValue(char c0, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(c0, short0);
        }
    }

    @Override
    public short adjustOrPutValue(char c0, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(c0, short0, short1);
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
