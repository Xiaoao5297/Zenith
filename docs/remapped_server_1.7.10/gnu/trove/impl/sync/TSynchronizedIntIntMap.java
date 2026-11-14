package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedIntIntMap implements TIntIntMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TIntIntMap m;
    final Object mutex;
    private transient TIntSet keySet = null;
    private transient TIntCollection values = null;

    public TSynchronizedIntIntMap(TIntIntMap tintintmap) {
        if (tintintmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintintmap;
            this.mutex = this;
        }
    }

    public TSynchronizedIntIntMap(TIntIntMap tintintmap, Object object) {
        this.m = tintintmap;
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
    public boolean containsValue(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(i);
        }
    }

    @Override
    public int get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public int put(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, j);
        }
    }

    @Override
    public int remove(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Integer> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TIntIntMap tintintmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tintintmap);
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
    public TIntIntIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public int putIfAbsent(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, j);
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
    public boolean forEachValue(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tintprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TIntIntProcedure tintintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tintintprocedure);
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
    public boolean retainEntries(TIntIntProcedure tintintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tintintprocedure);
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
    public boolean adjustValue(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, j);
        }
    }

    @Override
    public int adjustOrPutValue(int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, j, k);
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
