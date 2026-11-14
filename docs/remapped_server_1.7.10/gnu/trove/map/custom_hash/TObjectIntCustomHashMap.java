package gnu.trove.map.custom_hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
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

public class TObjectIntCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectIntMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectIntProcedure<K> PUT_ALL_PROC;
    protected transient int[] _values;
    protected int no_entry_value;

    public TObjectIntCustomHashMap() {
        this.PUT_ALL_PROC = new TObjectIntProcedure<K>() {
            @Override
            public boolean execute(K k0, int i) {
                TObjectIntCustomHashMap.this.put(k0, i);
                return true;
            }
        };
    }

    public TObjectIntCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
        this.PUT_ALL_PROC = new TObjectIntProcedure<K>() {
            @Override
            public boolean execute(K k0, int i) {
                TObjectIntCustomHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
        this.PUT_ALL_PROC = new TObjectIntProcedure<K>() {
            @Override
            public boolean execute(K k0, int i) {
                TObjectIntCustomHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectIntProcedure<K>() {
            @Override
            public boolean execute(K k0, int i) {
                TObjectIntCustomHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TObjectIntCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f, int j) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectIntProcedure<K>() {
            @Override
            public boolean execute(K k0, int i) {
                TObjectIntCustomHashMap.this.put(k0, i);
                return true;
            }
        };
        this.no_entry_value = j;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectIntCustomHashMap(HashingStrategy<? super K> hashingstrategy, TObjectIntMap<? extends K> tobjectintmap) {
        this(hashingstrategy, tobjectintmap.size(), 0.5F, tobjectintmap.getNoEntryValue());
        if (tobjectintmap instanceof TObjectIntCustomHashMap) {
            TObjectIntCustomHashMap tobjectintcustomhashmap = (TObjectIntCustomHashMap) tobjectintmap;

            this._loadFactor = tobjectintcustomhashmap._loadFactor;
            this.no_entry_value = tobjectintcustomhashmap.no_entry_value;
            this.strategy = tobjectintcustomhashmap.strategy;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectintmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new int[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        int[] aint = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectIntCustomHashMap.FREE);
        this._values = new int[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            Object object = aobject[k];

            if (object != TObjectIntCustomHashMap.FREE && object != TObjectIntCustomHashMap.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._values[l] = aint[k];
            }
        }

    }

    @Override
    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(int i) {
        Object[] aobject = this._set;
        int[] aint = this._values;
        int j = aint.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (aobject[j] == TObjectIntCustomHashMap.FREE || aobject[j] == TObjectIntCustomHashMap.REMOVED || i != aint[j]);

        return true;
    }

    @Override
    public int get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public int put(K k0, int i) {
        int j = this.insertKey(k0);

        return this.doPut(i, j);
    }

    @Override
    public int putIfAbsent(K k0, int i) {
        int j = this.insertKey(k0);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, j);
    }

    private int doPut(int i, int j) {
        int k = this.no_entry_value;
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
    public int remove(Object object) {
        int i = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Integer> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectIntMap<? extends K> tobjectintmap) {
        tobjectintmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectIntCustomHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectIntCustomHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectIntCustomHashMap.FREE && aobject1[i] != TObjectIntCustomHashMap.REMOVED) {
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
            if (aobject[j] != TObjectIntCustomHashMap.FREE && aobject[j] != TObjectIntCustomHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TIntCollection valueCollection() {
        return new TObjectIntCustomHashMap.TIntValueCollection();
    }

    @Override
    public int[] values() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._values;
        Object[] aobject = this._set;
        int i = aint1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectIntCustomHashMap.FREE && aobject[i] != TObjectIntCustomHashMap.REMOVED) {
                aint[j++] = aint1[i];
            }
        }

        return aint;
    }

    @Override
    public int[] values(int[] aint) {
        int i = this.size();

        if (aint.length < i) {
            aint = new int[i];
        }

        int[] aint1 = this._values;
        Object[] aobject = this._set;
        int j = aint1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectIntCustomHashMap.FREE && aobject[j] != TObjectIntCustomHashMap.REMOVED) {
                aint[k++] = aint1[j];
            }
        }

        if (aint.length > i) {
            aint[i] = this.no_entry_value;
        }

        return aint;
    }

    @Override
    public TObjectIntIterator<K> iterator() {
        return new TObjectIntCustomHashMap.TObjectIntHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, 1);
    }

    @Override
    public boolean adjustValue(K k0, int i) {
        int j = this.index(k0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(K k0, int i, int j) {
        int k = this.insertKey(k0);
        int l;
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
    public boolean forEachValue(TIntProcedure tintprocedure) {
        Object[] aobject = this._set;
        int[] aint = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectIntCustomHashMap.FREE || aobject[i] == TObjectIntCustomHashMap.REMOVED || tintprocedure.execute(aint[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> tobjectintprocedure) {
        Object[] aobject = this._set;
        int[] aint = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectIntCustomHashMap.FREE || aobject[i] == TObjectIntCustomHashMap.REMOVED || tobjectintprocedure.execute(aobject[i], aint[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectIntProcedure<? super K> tobjectintprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        int[] aint = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectIntCustomHashMap.FREE && aobject[i] != TObjectIntCustomHashMap.REMOVED && !tobjectintprocedure.execute(aobject[i], aint[i])) {
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
    public void transformValues(TIntFunction tintfunction) {
        Object[] aobject = this._set;
        int[] aint = this._values;
        int i = aint.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectIntCustomHashMap.REMOVED) {
                aint[i] = tintfunction.execute(aint[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectIntMap)) {
            return false;
        } else {
            TObjectIntMap tobjectintmap = (TObjectIntMap) object;

            if (tobjectintmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectIntIterator tobjectintiterator = this.iterator();

                    while (tobjectintiterator.hasNext()) {
                        tobjectintiterator.advance();
                        Object object1 = tobjectintiterator.key();
                        int i = tobjectintiterator.value();

                        if (i == this.no_entry_value) {
                            if (tobjectintmap.get(object1) != tobjectintmap.getNoEntryValue() || !tobjectintmap.containsKey(object1)) {
                                return false;
                            }
                        } else if (i != tobjectintmap.get(object1)) {
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
        int[] aint = this._values;
        int j = aint.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectIntCustomHashMap.FREE && aobject[j] != TObjectIntCustomHashMap.REMOVED) {
                i += HashFunctions.hash(aint[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
        objectoutput.writeInt(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectIntCustomHashMap.REMOVED && this._set[i] != TObjectIntCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeInt(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
        this.no_entry_value = objectinput.readInt();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            int j = objectinput.readInt();

            this.put(object, j);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectIntProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, int i) {
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

    class TObjectIntHashIterator<K> extends TObjectHashIterator<K> implements TObjectIntIterator<K> {

        private final TObjectIntCustomHashMap<K> _map;

        public TObjectIntHashIterator(TObjectIntCustomHashMap<K> tobjectintcustomhashmap) {
            super(tobjectintcustomhashmap);
            this._map = tobjectintcustomhashmap;
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
        public int value() {
            return this._map._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            this._map._values[this._index] = i;
            return j;
        }
    }

    class TIntValueCollection implements TIntCollection {

        TIntValueCollection() {}

        @Override
        public TIntIterator iterator() {
            return new TObjectIntCustomHashMap.TIntValueCollection.TObjectIntValueHashIterator();
        }

        @Override
        public int getNoEntryValue() {
            return TObjectIntCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectIntCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectIntCustomHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TObjectIntCustomHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TObjectIntCustomHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TObjectIntCustomHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TObjectIntCustomHashMap.this._values;
            Object[] aobject = TObjectIntCustomHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (aobject[j] == TObjectHash.FREE || aobject[j] == TObjectHash.REMOVED || i != aint[j]);

            TObjectIntCustomHashMap.this.removeAt(j);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            int i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Integer)) {
                    return false;
                }

                i = (Integer) object;
            } while (TObjectIntCustomHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TObjectIntCustomHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TObjectIntCustomHashMap.this.containsValue(k)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Integer> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TIntCollection tintcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int[] aint) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TIntIterator tintiterator = this.iterator();

            while (tintiterator.hasNext()) {
                if (!collection.contains(tintiterator.next())) {
                    tintiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TIntCollection tintcollection) {
            if (this == tintcollection) {
                return false;
            } else {
                boolean flag = false;
                TIntIterator tintiterator = this.iterator();

                while (tintiterator.hasNext()) {
                    if (!tintcollection.contains(tintiterator.next())) {
                        tintiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(int[] aint) {
            boolean flag = false;

            Arrays.sort(aint);
            int[] aint1 = TObjectIntCustomHashMap.this._values;
            Object[] aobject = TObjectIntCustomHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TObjectIntCustomHashMap.this.removeAt(i);
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

                if (object instanceof Integer) {
                    int i = (Integer) object;

                    if (this.remove(i)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TIntCollection tintcollection) {
            if (this == tintcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TIntIterator tintiterator = tintcollection.iterator();

                while (tintiterator.hasNext()) {
                    int i = tintiterator.next();

                    if (this.remove(i)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(int[] aint) {
            boolean flag = false;
            int i = aint.length;

            while (i-- > 0) {
                if (this.remove(aint[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectIntCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TObjectIntCustomHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectIntCustomHashMap.this.forEachValue(new TIntProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(int i) {
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

        class TObjectIntValueHashIterator implements TIntIterator {

            protected THash _hash = TObjectIntCustomHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectIntValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public int next() {
                this.moveToNextIndex();
                return TObjectIntCustomHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectIntCustomHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectIntCustomHashMap.this._set;
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
            TObjectIntCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectIntCustomHashMap.this.size();
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
            return TObjectIntCustomHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectIntCustomHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectIntCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectIntCustomHashMap.this.no_entry_value != TObjectIntCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectIntCustomHashMap.this.contains(k0);
        }
    }
}
