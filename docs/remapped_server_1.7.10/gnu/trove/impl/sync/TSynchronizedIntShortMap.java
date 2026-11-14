package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedIntShortMap implements TIntShortMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TIntShortMap m;
    final Object mutex;
    private transient TIntSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedIntShortMap(TIntShortMap tintshortmap) {
        if (tintshortmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tintshortmap;
            this.mutex = this;
        }
    }

    public TSynchronizedIntShortMap(TIntShortMap tintshortmap, Object object) {
        this.m = tintshortmap;
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
    public boolean containsValue(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(short0);
        }
    }

    @Override
    public short get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(i);
        }
    }

    @Override
    public short put(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(i, short0);
        }
    }

    @Override
    public short remove(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(i);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Short> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TIntShortMap tintshortmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tintshortmap);
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
    public TIntShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public short putIfAbsent(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(i, short0);
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
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tshortprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TIntShortProcedure tintshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tintshortprocedure);
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
    public boolean retainEntries(TIntShortProcedure tintshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tintshortprocedure);
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
    public boolean adjustValue(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(i, short0);
        }
    }

    @Override
    public short adjustOrPutValue(int i, short short0, short short1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(i, short0, short1);
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
