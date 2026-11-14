package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TObjectShortHashMap<K> extends TObjectHash<K> implements TObjectShortMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectShortProcedure<K> PUT_ALL_PROC;
    protected transient short[] _values;
    protected short no_entry_value;

    public TObjectShortHashMap() {
        this.PUT_ALL_PROC = new TObjectShortProcedure<K>() {
            @Override
            public boolean execute(K k0, short short0) {
                TObjectShortHashMap.this.put(k0, short0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TObjectShortProcedure<K>() {
            @Override
            public boolean execute(K k0, short short0) {
                TObjectShortHashMap.this.put(k0, short0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TObjectShortProcedure<K>() {
            @Override
            public boolean execute(K k0, short short0) {
                TObjectShortHashMap.this.put(k0, short0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int i, float f, short short0) {
        super(i, f);
        this.PUT_ALL_PROC = new TObjectShortProcedure<K>() {
            @Override
            public boolean execute(K k0, short short0) {
                TObjectShortHashMap.this.put(k0, short0);
                return true;
            }
        };
        this.no_entry_value = short0;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectShortHashMap(TObjectShortMap<? extends K> tobjectshortmap) {
        this(tobjectshortmap.size(), 0.5F, tobjectshortmap.getNoEntryValue());
        if (tobjectshortmap instanceof TObjectShortHashMap) {
            TObjectShortHashMap tobjectshorthashmap = (TObjectShortHashMap) tobjectshortmap;

            this._loadFactor = tobjectshorthashmap._loadFactor;
            this.no_entry_value = tobjectshorthashmap.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectshortmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new short[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        short[] ashort = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectShortHashMap.FREE);
        this._values = new short[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            if (aobject[k] != TObjectShortHashMap.FREE && aobject[k] != TObjectShortHashMap.REMOVED) {
                Object object = aobject[k];
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._set[l] = object;
                this._values[l] = ashort[k];
            }
        }

    }

    @Override
    public short getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(short short0) {
        Object[] aobject = this._set;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (aobject[i] == TObjectShortHashMap.FREE || aobject[i] == TObjectShortHashMap.REMOVED || short0 != ashort[i]);

        return true;
    }

    @Override
    public short get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public short put(K k0, short short0) {
        int i = this.insertKey(k0);

        return this.doPut(short0, i);
    }

    @Override
    public short putIfAbsent(K k0, short short0) {
        int i = this.insertKey(k0);

        return i < 0 ? this._values[-i - 1] : this.doPut(short0, i);
    }

    private short doPut(short short0, int i) {
        short short1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            short1 = this._values[i];
            flag = false;
        }

        this._values[i] = short0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short1;
    }

    @Override
    public short remove(Object object) {
        short short0 = this.no_entry_value;
        int i = this.index(object);

        if (i >= 0) {
            short0 = this._values[i];
            this.removeAt(i);
        }

        return short0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Short> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectShortMap<? extends K> tobjectshortmap) {
        tobjectshortmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectShortHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectShortHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectShortHashMap.FREE && aobject1[i] != TObjectShortHashMap.REMOVED) {
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
            if (aobject[j] != TObjectShortHashMap.FREE && aobject[j] != TObjectShortHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TShortCollection valueCollection() {
        return new TObjectShortHashMap.TShortValueCollection();
    }

    @Override
    public short[] values() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._values;
        Object[] aobject = this._set;
        int i = ashort1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectShortHashMap.FREE && aobject[i] != TObjectShortHashMap.REMOVED) {
                ashort[j++] = ashort1[i];
            }
        }

        return ashort;
    }

    @Override
    public short[] values(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._values;
        Object[] aobject = this._set;
        int j = ashort1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectShortHashMap.FREE && aobject[j] != TObjectShortHashMap.REMOVED) {
                ashort[k++] = ashort1[j];
            }
        }

        if (ashort.length > i) {
            ashort[i] = this.no_entry_value;
        }

        return ashort;
    }

    @Override
    public TObjectShortIterator<K> iterator() {
        return new TObjectShortHashMap.TObjectShortHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, (short) 1);
    }

    @Override
    public boolean adjustValue(K k0, short short0) {
        int i = this.index(k0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += short0;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(K k0, short short0, short short1) {
        int i = this.insertKey(k0);
        short short2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            short2 = this._values[i] += short0;
            flag = false;
        } else {
            short2 = this._values[i] = short1;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        Object[] aobject = this._set;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectShortHashMap.FREE || aobject[i] == TObjectShortHashMap.REMOVED || tshortprocedure.execute(ashort[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectShortProcedure<? super K> tobjectshortprocedure) {
        Object[] aobject = this._set;
        short[] ashort = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectShortHashMap.FREE || aobject[i] == TObjectShortHashMap.REMOVED || tobjectshortprocedure.execute(aobject[i], ashort[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectShortProcedure<? super K> tobjectshortprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        short[] ashort = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectShortHashMap.FREE && aobject[i] != TObjectShortHashMap.REMOVED && !tobjectshortprocedure.execute(aobject[i], ashort[i])) {
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
    public void transformValues(TShortFunction tshortfunction) {
        Object[] aobject = this._set;
        short[] ashort = this._values;
        int i = ashort.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectShortHashMap.REMOVED) {
                ashort[i] = tshortfunction.execute(ashort[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectShortMap)) {
            return false;
        } else {
            TObjectShortMap tobjectshortmap = (TObjectShortMap) object;

            if (tobjectshortmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectShortIterator tobjectshortiterator = this.iterator();

                    while (tobjectshortiterator.hasNext()) {
                        tobjectshortiterator.advance();
                        Object object1 = tobjectshortiterator.key();
                        short short0 = tobjectshortiterator.value();

                        if (short0 == this.no_entry_value) {
                            if (tobjectshortmap.get(object1) != tobjectshortmap.getNoEntryValue() || !tobjectshortmap.containsKey(object1)) {
                                return false;
                            }
                        } else if (short0 != tobjectshortmap.get(object1)) {
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
        short[] ashort = this._values;
        int j = ashort.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectShortHashMap.FREE && aobject[j] != TObjectShortHashMap.REMOVED) {
                i += HashFunctions.hash(ashort[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeShort(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectShortHashMap.REMOVED && this._set[i] != TObjectShortHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeShort(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_value = objectinput.readShort();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            short short0 = objectinput.readShort();

            this.put(object, short0);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectShortProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, short short0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(short0);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectShortHashIterator<K> extends TObjectHashIterator<K> implements TObjectShortIterator<K> {

        private final TObjectShortHashMap<K> _map;

        public TObjectShortHashIterator(TObjectShortHashMap<K> tobjectshorthashmap) {
            super(tobjectshorthashmap);
            this._map = tobjectshorthashmap;
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
        public short value() {
            return this._map._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            this._map._values[this._index] = short0;
            return short1;
        }
    }

    class TShortValueCollection implements TShortCollection {

        TShortValueCollection() {}

        @Override
        public TShortIterator iterator() {
            return new TObjectShortHashMap.TShortValueCollection.TObjectShortValueHashIterator();
        }

        @Override
        public short getNoEntryValue() {
            return TObjectShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TObjectShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TObjectShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TObjectShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TObjectShortHashMap.this._values;
            Object[] aobject = TObjectShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || short0 != ashort[i]);

            TObjectShortHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            short short0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Short)) {
                    return false;
                }

                short0 = (Short) object;
            } while (TObjectShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TObjectShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TObjectShortHashMap.this.containsValue(short0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection tshortcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] ashort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TShortIterator tshortiterator = this.iterator();

            while (tshortiterator.hasNext()) {
                if (!collection.contains(tshortiterator.next())) {
                    tshortiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                return false;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = this.iterator();

                while (tshortiterator.hasNext()) {
                    if (!tshortcollection.contains(tshortiterator.next())) {
                        tshortiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(short[] ashort) {
            boolean flag = false;

            Arrays.sort(ashort);
            short[] ashort1 = TObjectShortHashMap.this._values;
            Object[] aobject = TObjectShortHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TObjectShortHashMap.this.removeAt(i);
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

                if (object instanceof Short) {
                    short short0 = (Short) object;

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = tshortcollection.iterator();

                while (tshortiterator.hasNext()) {
                    short short0 = tshortiterator.next();

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(short[] ashort) {
            boolean flag = false;
            int i = ashort.length;

            while (i-- > 0) {
                if (this.remove(ashort[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TObjectShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectShortHashMap.this.forEachValue(new TShortProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(short short0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(short0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectShortValueHashIterator implements TShortIterator {

            protected THash _hash = TObjectShortHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectShortValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public short next() {
                this.moveToNextIndex();
                return TObjectShortHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectShortHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectShortHashMap.this._set;
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
            TObjectShortHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectShortHashMap.this.size();
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
            return TObjectShortHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectShortHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectShortHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectShortHashMap.this.no_entry_value != TObjectShortHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectShortHashMap.this.contains(k0);
        }
    }
}
