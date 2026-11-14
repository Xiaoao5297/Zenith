package gnu.trove.map.custom_hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class TObjectByteCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectByteMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectByteProcedure<K> PUT_ALL_PROC;
    protected transient byte[] _values;
    protected byte no_entry_value;

    public TObjectByteCustomHashMap() {
        this.PUT_ALL_PROC = new TObjectByteProcedure<K>() {
            @Override
            public boolean execute(K k0, byte b0) {
                TObjectByteCustomHashMap.this.put(k0, b0);
                return true;
            }
        };
    }

    public TObjectByteCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
        this.PUT_ALL_PROC = new TObjectByteProcedure<K>() {
            @Override
            public boolean execute(K k0, byte b0) {
                TObjectByteCustomHashMap.this.put(k0, b0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TObjectByteCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
        this.PUT_ALL_PROC = new TObjectByteProcedure<K>() {
            @Override
            public boolean execute(K k0, byte b0) {
                TObjectByteCustomHashMap.this.put(k0, b0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TObjectByteCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectByteProcedure<K>() {
            @Override
            public boolean execute(K k0, byte b0) {
                TObjectByteCustomHashMap.this.put(k0, b0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TObjectByteCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f, byte b0) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectByteProcedure<K>() {
            @Override
            public boolean execute(K k0, byte b0) {
                TObjectByteCustomHashMap.this.put(k0, b0);
                return true;
            }
        };
        this.no_entry_value = b0;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectByteCustomHashMap(HashingStrategy<? super K> hashingstrategy, TObjectByteMap<? extends K> tobjectbytemap) {
        this(hashingstrategy, tobjectbytemap.size(), 0.5F, tobjectbytemap.getNoEntryValue());
        if (tobjectbytemap instanceof TObjectByteCustomHashMap) {
            TObjectByteCustomHashMap tobjectbytecustomhashmap = (TObjectByteCustomHashMap) tobjectbytemap;

            this._loadFactor = tobjectbytecustomhashmap._loadFactor;
            this.no_entry_value = tobjectbytecustomhashmap.no_entry_value;
            this.strategy = tobjectbytecustomhashmap.strategy;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectbytemap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new byte[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        byte[] abyte = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectByteCustomHashMap.FREE);
        this._values = new byte[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            Object object = aobject[k];

            if (object != TObjectByteCustomHashMap.FREE && object != TObjectByteCustomHashMap.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._values[l] = abyte[k];
            }
        }

    }

    @Override
    public byte getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(byte b0) {
        Object[] aobject = this._set;
        byte[] abyte = this._values;
        int i = abyte.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (aobject[i] == TObjectByteCustomHashMap.FREE || aobject[i] == TObjectByteCustomHashMap.REMOVED || b0 != abyte[i]);

        return true;
    }

    @Override
    public byte get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public byte put(K k0, byte b0) {
        int i = this.insertKey(k0);

        return this.doPut(b0, i);
    }

    @Override
    public byte putIfAbsent(K k0, byte b0) {
        int i = this.insertKey(k0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, i);
    }

    private byte doPut(byte b0, int i) {
        byte b1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            b1 = this._values[i];
            flag = false;
        }

        this._values[i] = b0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b1;
    }

    @Override
    public byte remove(Object object) {
        byte b0 = this.no_entry_value;
        int i = this.index(object);

        if (i >= 0) {
            b0 = this._values[i];
            this.removeAt(i);
        }

        return b0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Byte> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectByteMap<? extends K> tobjectbytemap) {
        tobjectbytemap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectByteCustomHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectByteCustomHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectByteCustomHashMap.FREE && aobject1[i] != TObjectByteCustomHashMap.REMOVED) {
                aobject[j++] = aobject1[i];
            }
        }

        return aobject;
    }

    @Override
    public K[] keys(K[] ak) {
        int i = this.size();

        if (ak.length < i) {
            ak = (Object[]) ((Object[]) Array.newInstance(ak.getClass().getComponentType(), i));
        }

        Object[] aobject = this._set;
        int j = aobject.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectByteCustomHashMap.FREE && aobject[j] != TObjectByteCustomHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TByteCollection valueCollection() {
        return new TObjectByteCustomHashMap.TByteValueCollection();
    }

    @Override
    public byte[] values() {
        byte[] abyte = new byte[this.size()];
        byte[] abyte1 = this._values;
        Object[] aobject = this._set;
        int i = abyte1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectByteCustomHashMap.FREE && aobject[i] != TObjectByteCustomHashMap.REMOVED) {
                abyte[j++] = abyte1[i];
            }
        }

        return abyte;
    }

    @Override
    public byte[] values(byte[] abyte) {
        int i = this.size();

        if (abyte.length < i) {
            abyte = new byte[i];
        }

        byte[] abyte1 = this._values;
        Object[] aobject = this._set;
        int j = abyte1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectByteCustomHashMap.FREE && aobject[j] != TObjectByteCustomHashMap.REMOVED) {
                abyte[k++] = abyte1[j];
            }
        }

        if (abyte.length > i) {
            abyte[i] = this.no_entry_value;
        }

        return abyte;
    }

    @Override
    public TObjectByteIterator<K> iterator() {
        return new TObjectByteCustomHashMap.TObjectByteHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, (byte) 1);
    }

    @Override
    public boolean adjustValue(K k0, byte b0) {
        int i = this.index(k0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += b0;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(K k0, byte b0, byte b1) {
        int i = this.insertKey(k0);
        byte b2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            b2 = this._values[i] += b0;
            flag = false;
        } else {
            b2 = this._values[i] = b1;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b2;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        Object[] aobject = this._set;
        byte[] abyte = this._values;
        int i = abyte.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectByteCustomHashMap.FREE || aobject[i] == TObjectByteCustomHashMap.REMOVED || tbyteprocedure.execute(abyte[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        Object[] aobject = this._set;
        byte[] abyte = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectByteCustomHashMap.FREE || aobject[i] == TObjectByteCustomHashMap.REMOVED || tobjectbyteprocedure.execute(aobject[i], abyte[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectByteProcedure<? super K> tobjectbyteprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        byte[] abyte = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectByteCustomHashMap.FREE && aobject[i] != TObjectByteCustomHashMap.REMOVED && !tobjectbyteprocedure.execute(aobject[i], abyte[i])) {
                    this.removeAt(i);
                    flag = true;
                }
            }
        } finally {
            this.reenableAutoCompaction(true);
        }

        return flag;
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        Object[] aobject = this._set;
        byte[] abyte = this._values;
        int i = abyte.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectByteCustomHashMap.REMOVED) {
                abyte[i] = tbytefunction.execute(abyte[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectByteMap)) {
            return false;
        } else {
            TObjectByteMap tobjectbytemap = (TObjectByteMap) object;

            if (tobjectbytemap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectByteIterator tobjectbyteiterator = this.iterator();

                    while (tobjectbyteiterator.hasNext()) {
                        tobjectbyteiterator.advance();
                        Object object1 = tobjectbyteiterator.key();
                        byte b0 = tobjectbyteiterator.value();

                        if (b0 == this.no_entry_value) {
                            if (tobjectbytemap.get(object1) != tobjectbytemap.getNoEntryValue() || !tobjectbytemap.containsKey(object1)) {
                                return false;
                            }
                        } else if (b0 != tobjectbytemap.get(object1)) {
                            return false;
                        }
                    }
                } catch (ClassCastException classcastexception) {
                    ;
                }

                return true;
            }
        }
    }

    @Override
    public int hashCode() {
        int i = 0;
        Object[] aobject = this._set;
        byte[] abyte = this._values;
        int j = abyte.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectByteCustomHashMap.FREE && aobject[j] != TObjectByteCustomHashMap.REMOVED) {
                i += HashFunctions.hash(abyte[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
        objectoutput.writeByte(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectByteCustomHashMap.REMOVED && this._set[i] != TObjectByteCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeByte(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
        this.no_entry_value = objectinput.readByte();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            byte b0 = objectinput.readByte();

            this.put(object, b0);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectByteProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, byte b0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(b0);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectByteHashIterator<K> extends TObjectHashIterator<K> implements TObjectByteIterator<K> {

        private final TObjectByteCustomHashMap<K> _map;

        public TObjectByteHashIterator(TObjectByteCustomHashMap<K> tobjectbytecustomhashmap) {
            super(tobjectbytecustomhashmap);
            this._map = tobjectbytecustomhashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public K key() {
            return this._map._set[this._index];
        }

        @Override
        public byte value() {
            return this._map._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            this._map._values[this._index] = b0;
            return b1;
        }
    }

    class TByteValueCollection implements TByteCollection {

        TByteValueCollection() {}

        @Override
        public TByteIterator iterator() {
            return new TObjectByteCustomHashMap.TByteValueCollection.TObjectByteValueHashIterator();
        }

        @Override
        public byte getNoEntryValue() {
            return TObjectByteCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectByteCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectByteCustomHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TObjectByteCustomHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TObjectByteCustomHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TObjectByteCustomHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TObjectByteCustomHashMap.this._values;
            Object[] aobject = TObjectByteCustomHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || b0 != abyte[i]);

            TObjectByteCustomHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            byte b0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Byte)) {
                    return false;
                }

                b0 = (Byte) object;
            } while (TObjectByteCustomHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TObjectByteCustomHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TObjectByteCustomHashMap.this.containsValue(b0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TByteCollection tbytecollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(byte[] abyte) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TByteIterator tbyteiterator = this.iterator();

            while (tbyteiterator.hasNext()) {
                if (!collection.contains(tbyteiterator.next())) {
                    tbyteiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TByteCollection tbytecollection) {
            if (this == tbytecollection) {
                return false;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = this.iterator();

                while (tbyteiterator.hasNext()) {
                    if (!tbytecollection.contains(tbyteiterator.next())) {
                        tbyteiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(byte[] abyte) {
            boolean flag = false;

            Arrays.sort(abyte);
            byte[] abyte1 = TObjectByteCustomHashMap.this._values;
            Object[] aobject = TObjectByteCustomHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TObjectByteCustomHashMap.this.removeAt(i);
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean flag = false;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                if (object instanceof Byte) {
                    byte b0 = (Byte) object;

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TByteCollection tbytecollection) {
            if (this == tbytecollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = tbytecollection.iterator();

                while (tbyteiterator.hasNext()) {
                    byte b0 = tbyteiterator.next();

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(byte[] abyte) {
            boolean flag = false;
            int i = abyte.length;

            while (i-- > 0) {
                if (this.remove(abyte[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectByteCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TObjectByteCustomHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectByteCustomHashMap.this.forEachValue(new TByteProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(byte b0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(b0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectByteValueHashIterator implements TByteIterator {

            protected THash _hash = TObjectByteCustomHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectByteValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public byte next() {
                this.moveToNextIndex();
                return TObjectByteCustomHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectByteCustomHashMap.this.removeAt(this._index);
                    } finally {
                        this._hash.reenableAutoCompaction(false);
                    }

                    --this._expectedSize;
                }
            }

            protected final void moveToNextIndex() {
                if ((this._index = this.nextIndex()) < 0) {
                    throw new NoSuchElementException();
                }
            }

            protected final int nextIndex() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    Object[] aobject = TObjectByteCustomHashMap.this._set;
                    int i = this._index;

                    while (i-- > 0 && (aobject[i] == TCustomObjectHash.FREE || aobject[i] == TCustomObjectHash.REMOVED)) {
                        ;
                    }

                    return i;
                }
            }
        }
    }

    private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {

        private MapBackedView() {}

        public abstract boolean removeElement(E e0);

        public abstract boolean containsElement(E e0);

        public boolean contains(Object object) {
            return this.containsElement(object);
        }

        public boolean remove(Object object) {
            return this.removeElement(object);
        }

        public void clear() {
            TObjectByteCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectByteCustomHashMap.this.size();
        }

        public Object[] toArray() {
            Object[] aobject = new Object[this.size()];
            Iterator iterator = this.iterator();

            for (int i = 0; iterator.hasNext(); ++i) {
                aobject[i] = iterator.next();
            }

            return aobject;
        }

        public <T> T[] toArray(T[] at) {
            int i = this.size();

            if (at.length < i) {
                at = (Object[]) ((Object[]) Array.newInstance(at.getClass().getComponentType(), i));
            }

            Iterator iterator = this.iterator();
            Object[] aobject = at;

            for (int j = 0; j < i; ++j) {
                aobject[j] = iterator.next();
            }

            if (at.length > i) {
                at[i] = null;
            }

            return at;
        }

        public boolean isEmpty() {
            return TObjectByteCustomHashMap.this.isEmpty();
        }

        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            Iterator iterator = this.iterator();

            while (iterator.hasNext()) {
                if (!collection.contains(iterator.next())) {
                    iterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    protected class KeyView extends TObjectByteCustomHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectByteCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectByteCustomHashMap.this.no_entry_value != TObjectByteCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectByteCustomHashMap.this.contains(k0);
        }
    }
}
