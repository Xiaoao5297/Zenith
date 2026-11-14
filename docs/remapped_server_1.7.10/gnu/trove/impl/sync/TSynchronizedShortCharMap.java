package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedShortCharMap implements TShortCharMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortCharMap m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient TCharCollection values = null;

    public TSynchronizedShortCharMap(TShortCharMap tshortcharmap) {
        if (tshortcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tshortcharmap;
            this.mutex = this;
        }
    }

    public TSynchronizedShortCharMap(TShortCharMap tshortcharmap, Object object) {
        this.m = tshortcharmap;
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
    public boolean containsValue(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(c0);
        }
    }

    @Override
    public char get(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(short0);
        }
    }

    @Override
    public char put(short short0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(short0, c0);
        }
    }

    @Override
    public char remove(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(short0);
        }
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Character> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TShortCharMap tshortcharmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tshortcharmap);
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
    public TShortCharIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public char putIfAbsent(short short0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(short0, c0);
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
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tcharprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TShortCharProcedure tshortcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tshortcharprocedure);
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
    public boolean retainEntries(TShortCharProcedure tshortcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tshortcharprocedure);
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
    public boolean adjustValue(short short0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(short0, c0);
        }
    }

    @Override
    public char adjustOrPutValue(short short0, char c0, char c1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(short0, c0, c1);
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
