package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
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

public class TObjectLongHashMap<K> extends TObjectHash<K> implements TObjectLongMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectLongProcedure<K> PUT_ALL_PROC;
    protected transient long[] _values;
    protected long no_entry_value;

    public TObjectLongHashMap() {
        this.PUT_ALL_PROC = new TObjectLongProcedure<K>() {
            @Override
            public boolean execute(K k0, long i) {
                TObjectLongHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TObjectLongProcedure<K>() {
            @Override
            public boolean execute(K k0, long i) {
                TObjectLongHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TObjectLongProcedure<K>() {
            @Override
            public boolean execute(K k0, long i) {
                TObjectLongHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongHashMap(int i, float f, long j) {
        super(i, f);
        this.PUT_ALL_PROC = new TObjectLongProcedure<K>() {
            @Override
            public boolean execute(K k0, long i) {
                TObjectLongHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = j;
        if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectLongHashMap(TObjectLongMap<? extends K> tobjectlongmap) {
        this(tobjectlongmap.size(), 0.5F, tobjectlongmap.getNoEntryValue());
        if (tobjectlongmap instanceof TObjectLongHashMap) {
            TObjectLongHashMap tobjectlonghashmap = (TObjectLongHashMap) tobjectlongmap;

            this._loadFactor = tobjectlonghashmap._loadFactor;
            this.no_entry_value = tobjectlonghashmap.no_entry_value;
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectlongmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new long[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        long[] along = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectLongHashMap.FREE);
        this._values = new long[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            if (aobject[k] != TObjectLongHashMap.FREE && aobject[k] != TObjectLongHashMap.REMOVED) {
                Object object = aobject[k];
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._set[l] = object;
                this._values[l] = along[k];
            }
        }

    }

    @Override
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(long i) {
        Object[] aobject = this._set;
        long[] along = this._values;
        int j = along.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (aobject[j] == TObjectLongHashMap.FREE || aobject[j] == TObjectLongHashMap.REMOVED || i != along[j]);

        return true;
    }

    @Override
    public long get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public long put(K k0, long i) {
        int j = this.insertKey(k0);

        return this.doPut(i, j);
    }

    @Override
    public long putIfAbsent(K k0, long i) {
        int j = this.insertKey(k0);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, j);
    }

    private long doPut(long i, int j) {
        long k = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            k = this._values[j];
            flag = false;
        }

        this._values[j] = i;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return k;
    }

    @Override
    public long remove(Object object) {
        long i = this.no_entry_value;
        int j = this.index(object);

        if (j >= 0) {
            i = this._values[j];
            this.removeAt(j);
        }

        return i;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectLongMap<? extends K> tobjectlongmap) {
        tobjectlongmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectLongHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectLongHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectLongHashMap.FREE && aobject1[i] != TObjectLongHashMap.REMOVED) {
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
            if (aobject[j] != TObjectLongHashMap.FREE && aobject[j] != TObjectLongHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TLongCollection valueCollection() {
        return new TObjectLongHashMap.TLongValueCollection();
    }

    @Override
    public long[] values() {
        long[] along = new long[this.size()];
        long[] along1 = this._values;
        Object[] aobject = this._set;
        int i = along1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectLongHashMap.FREE && aobject[i] != TObjectLongHashMap.REMOVED) {
                along[j++] = along1[i];
            }
        }

        return along;
    }

    @Override
    public long[] values(long[] along) {
        int i = this.size();

        if (along.length < i) {
            along = new long[i];
        }

        long[] along1 = this._values;
        Object[] aobject = this._set;
        int j = along1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectLongHashMap.FREE && aobject[j] != TObjectLongHashMap.REMOVED) {
                along[k++] = along1[j];
            }
        }

        if (along.length > i) {
            along[i] = this.no_entry_value;
        }

        return along;
    }

    @Override
    public TObjectLongIterator<K> iterator() {
        return new TObjectLongHashMap.TObjectLongHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, 1L);
    }

    @Override
    public boolean adjustValue(K k0, long i) {
        int j = this.index(k0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(K k0, long i, long j) {
        int k = this.insertKey(k0);
        long l;
        boolean flag;

        if (k < 0) {
            k = -k - 1;
            l = this._values[k] += i;
            flag = false;
        } else {
            l = this._values[k] = j;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return l;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        Object[] aobject = this._set;
        long[] along = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectLongHashMap.FREE || aobject[i] == TObjectLongHashMap.REMOVED || tlongprocedure.execute(along[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectLongProcedure<? super K> tobjectlongprocedure) {
        Object[] aobject = this._set;
        long[] along = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectLongHashMap.FREE || aobject[i] == TObjectLongHashMap.REMOVED || tobjectlongprocedure.execute(aobject[i], along[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectLongProcedure<? super K> tobjectlongprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        long[] along = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectLongHashMap.FREE && aobject[i] != TObjectLongHashMap.REMOVED && !tobjectlongprocedure.execute(aobject[i], along[i])) {
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
    public void transformValues(TLongFunction tlongfunction) {
        Object[] aobject = this._set;
        long[] along = this._values;
        int i = along.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectLongHashMap.REMOVED) {
                along[i] = tlongfunction.execute(along[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectLongMap)) {
            return false;
        } else {
            TObjectLongMap tobjectlongmap = (TObjectLongMap) object;

            if (tobjectlongmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectLongIterator tobjectlongiterator = this.iterator();

                    while (tobjectlongiterator.hasNext()) {
                        tobjectlongiterator.advance();
                        Object object1 = tobjectlongiterator.key();
                        long i = tobjectlongiterator.value();

                        if (i == this.no_entry_value) {
                            if (tobjectlongmap.get(object1) != tobjectlongmap.getNoEntryValue() || !tobjectlongmap.containsKey(object1)) {
                                return false;
                            }
                        } else if (i != tobjectlongmap.get(object1)) {
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
        long[] along = this._values;
        int j = along.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectLongHashMap.FREE && aobject[j] != TObjectLongHashMap.REMOVED) {
                i += HashFunctions.hash(along[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeLong(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectLongHashMap.REMOVED && this._set[i] != TObjectLongHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeLong(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_value = objectinput.readLong();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            long j = objectinput.readLong();

            this.put(object, j);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectLongProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, long i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(i);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectLongHashIterator<K> extends TObjectHashIterator<K> implements TObjectLongIterator<K> {

        private final TObjectLongHashMap<K> _map;

        public TObjectLongHashIterator(TObjectLongHashMap<K> tobjectlonghashmap) {
            super(tobjectlonghashmap);
            this._map = tobjectlonghashmap;
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
        public long value() {
            return this._map._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            this._map._values[this._index] = i;
            return j;
        }
    }

    class TLongValueCollection implements TLongCollection {

        TLongValueCollection() {}

        @Override
        public TLongIterator iterator() {
            return new TObjectLongHashMap.TLongValueCollection.TObjectLongValueHashIterator();
        }

        @Override
        public long getNoEntryValue() {
            return TObjectLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TObjectLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TObjectLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TObjectLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TObjectLongHashMap.this._values;
            Object[] aobject = TObjectLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (aobject[j] == TObjectHash.FREE || aobject[j] == TObjectHash.REMOVED || i != along[j]);

            TObjectLongHashMap.this.removeAt(j);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            long i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Long)) {
                    return false;
                }

                i = (Long) object;
            } while (TObjectLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TObjectLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TObjectLongHashMap.this.containsValue(k)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Long> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TLongCollection tlongcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long[] along) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TLongIterator tlongiterator = this.iterator();

            while (tlongiterator.hasNext()) {
                if (!collection.contains(tlongiterator.next())) {
                    tlongiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TLongCollection tlongcollection) {
            if (this == tlongcollection) {
                return false;
            } else {
                boolean flag = false;
                TLongIterator tlongiterator = this.iterator();

                while (tlongiterator.hasNext()) {
                    if (!tlongcollection.contains(tlongiterator.next())) {
                        tlongiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(long[] along) {
            boolean flag = false;

            Arrays.sort(along);
            long[] along1 = TObjectLongHashMap.this._values;
            Object[] aobject = TObjectLongHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(along, along1[i]) < 0) {
                    TObjectLongHashMap.this.removeAt(i);
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

                if (object instanceof Long) {
                    long i = (Long) object;

                    if (this.remove(i)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TLongCollection tlongcollection) {
            if (this == tlongcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TLongIterator tlongiterator = tlongcollection.iterator();

                while (tlongiterator.hasNext()) {
                    long i = tlongiterator.next();

                    if (this.remove(i)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(long[] along) {
            boolean flag = false;
            int i = along.length;

            while (i-- > 0) {
                if (this.remove(along[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TObjectLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectLongHashMap.this.forEachValue(new TLongProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(long i) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(i);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectLongValueHashIterator implements TLongIterator {

            protected THash _hash = TObjectLongHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectLongValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public long next() {
                this.moveToNextIndex();
                return TObjectLongHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectLongHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectLongHashMap.this._set;
                    int i = this._index;

                    while (i-- > 0 && (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED)) {
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
            TObjectLongHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectLongHashMap.this.size();
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
            return TObjectLongHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectLongHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectLongHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectLongHashMap.this.no_entry_value != TObjectLongHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectLongHashMap.this.contains(k0);
        }
    }
}
