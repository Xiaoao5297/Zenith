package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class TSynchronizedCharByteMap implements TCharByteMap, Serializable {

    private static final long serialVersionUID = 1978198479659022715L;
    private final TCharByteMap m;
    final Object mutex;
    private transient TCharSet keySet = null;
    private transient TByteCollection values = null;

    public TSynchronizedCharByteMap(TCharByteMap tcharbytemap) {
        if (tcharbytemap == null) {
            throw new NullPointerException();
        } else {
            this.m = tcharbytemap;
            this.mutex = this;
        }
    }

    public TSynchronizedCharByteMap(TCharByteMap tcharbytemap, Object object) {
        this.m = tcharbytemap;
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
    public boolean containsValue(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.containsValue(b0);
        }
    }

    @Override
    public byte get(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.get(c0);
        }
    }

    @Override
    public byte put(char c0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.put(c0, b0);
        }
    }

    @Override
    public byte remove(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.remove(c0);
        }
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Byte> map) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(map);
        }
    }

    @Override
    public void putAll(TCharByteMap tcharbytemap) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.putAll(tcharbytemap);
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
    public TByteCollection valueCollection() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            if (this.values == null) {
                this.values = new TSynchronizedByteCollection(this.m.valueCollection(), this.mutex);
            }

            return this.values;
        }
    }

    @Override
    public byte[] values() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values();
        }
    }

    @Override
    public byte[] values(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.values(abyte);
        }
    }

    @Override
    public TCharByteIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public byte putIfAbsent(char c0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.putIfAbsent(c0, b0);
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
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachValue(tbyteprocedure);
        }
    }

    @Override
    public boolean forEachEntry(TCharByteProcedure tcharbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.forEachEntry(tcharbyteprocedure);
        }
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.m.transformValues(tbytefunction);
        }
    }

    @Override
    public boolean retainEntries(TCharByteProcedure tcharbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.retainEntries(tcharbyteprocedure);
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
    public boolean adjustValue(char c0, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustValue(c0, b0);
        }
    }

    @Override
    public byte adjustOrPutValue(char c0, byte b0, byte b1) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.m.adjustOrPutValue(c0, b0, b1);
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
