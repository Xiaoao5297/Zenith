package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedIntCharMap implements TIntCharMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TIntCharMap m;
    final Object mutex;
    private transient TIntSet keySet = null;
    private transient TCharCollection values = null;

    public TSynchronizedIntCharMap(TIntCharMap tintcharmap) {
        if (tintcharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintcharmap;
            this.mutex = this;
        }
    }

    public TSynchronizedIntCharMap(TIntCharMap tintcharmap, Object object) {
        this.m = tintcharmap;
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
    public boolean containsKey(int i) {
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
    public char get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public char put(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, c0);
        }
    }

    @Override
    public char remove(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Character> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TIntCharMap tintcharmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tintcharmap);
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
    public TIntSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedIntSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public int[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public int[] keys(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(aint);
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
    public TIntCharIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public char putIfAbsent(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, c0);
        }
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tintprocedure);
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
    public boolean forEachEntry(TIntCharProcedure tintcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tintcharprocedure);
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
    public boolean retainEntries(TIntCharProcedure tintcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tintcharprocedure);
        }
    }

    @Override
    public boolean increment(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(i);
        }
    }

    @Override
    public boolean adjustValue(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, c0);
        }
    }

    @Override
    public char adjustOrPutValue(int i, char c0, char c1) {
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
