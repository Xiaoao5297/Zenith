package gnu.trove.impl.sync;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedByteCharMap implements TByteCharMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TByteCharMap m;
    final Object mutex;
    private transient TByteSet keySet = null;
    private transient TCharCollection values = null;

    public TSynchronizedByteCharMap(TByteCharMap tbytecharmap) {
        if (tbytecharmap == null) {
            throw new NullPointerException();
        } else {
            this.m = tbytecharmap;
            this.mutex = this;
        }
    }

    public TSynchronizedByteCharMap(TByteCharMap tbytecharmap, Object object) {
        this.m = tbytecharmap;
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
    public boolean containsKey(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsKey(b0);
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
    public char get(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(b0);
        }
    }

    @Override
    public char put(byte b0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(b0, c0);
        }
    }

    @Override
    public char remove(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(b0);
        }
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Character> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TByteCharMap tbytecharmap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tbytecharmap);
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
    public TByteSet keySet() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedByteSet(this.m.keySet(), this.mutex);
            }

            return this.keySet;
        }
    }

    @Override
    public byte[] keys() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys();
        }
    }

    @Override
    public byte[] keys(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.keys(abyte);
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
    public TByteCharIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public char putIfAbsent(byte b0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(b0, c0);
        }
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachKey(tbyteprocedure);
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
    public boolean forEachEntry(TByteCharProcedure tbytecharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tbytecharprocedure);
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
    public boolean retainEntries(TByteCharProcedure tbytecharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tbytecharprocedure);
        }
    }

    @Override
    public boolean increment(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.increment(b0);
        }
    }

    @Override
    public boolean adjustValue(byte b0, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(b0, c0);
        }
    }

    @Override
    public char adjustOrPutValue(byte b0, char c0, char c1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(b0, c0, c1);
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
