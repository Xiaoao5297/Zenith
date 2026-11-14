package gnu.trove.map.custom_hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
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

public class TObjectCharCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectCharMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectCharProcedure<K> PUT_ALL_PROC;
    protected transient char[] _values;
    protected char no_entry_value;

    public TObjectCharCustomHashMap() {
        this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
            @Override
            public boolean execute(K k0, char c0) {
                TObjectCharCustomHashMap.this.put(k0, c0);
                return true;
            }
        };
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
        this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
            @Override
            public boolean execute(K k0, char c0) {
                TObjectCharCustomHashMap.this.put(k0, c0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
        this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
            @Override
            public boolean execute(K k0, char c0) {
                TObjectCharCustomHashMap.this.put(k0, c0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
            @Override
            public boolean execute(K k0, char c0) {
                TObjectCharCustomHashMap.this.put(k0, c0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f, char c0) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
            @Override
            public boolean execute(K k0, char c0) {
                TObjectCharCustomHashMap.this.put(k0, c0);
                return true;
            }
        };
        this.no_entry_value = c0;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectCharCustomHashMap(HashingStrategy<? super K> hashingstrategy, TObjectCharMap<? extends K> tobjectcharmap) {
        this(hashingstrategy, tobjectcharmap.size(), 0.5F, tobjectcharmap.getNoEntryValue());
        if (tobjectcharmap instanceof TObjectCharCustomHashMap) {
            TObjectCharCustomHashMap tobjectcharcustomhashmap = (TObjectCharCustomHashMap) tobjectcharmap;

            this._loadFactor = tobjectcharcustomhashmap._loadFactor;
            this.no_entry_value = tobjectcharcustomhashmap.no_entry_value;
            this.strategy = tobjectcharcustomhashmap.strategy;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectcharmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new char[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        char[] achar = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectCharCustomHashMap.FREE);
        this._values = new char[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            Object object = aobject[k];

            if (object != TObjectCharCustomHashMap.FREE && object != TObjectCharCustomHashMap.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._values[l] = achar[k];
            }
        }

    }

    @Override
    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(char c0) {
        Object[] aobject = this._set;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (aobject[i] == TObjectCharCustomHashMap.FREE || aobject[i] == TObjectCharCustomHashMap.REMOVED || c0 != achar[i]);

        return true;
    }

    @Override
    public char get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public char put(K k0, char c0) {
        int i = this.insertKey(k0);

        return this.doPut(c0, i);
    }

    @Override
    public char putIfAbsent(K k0, char c0) {
        int i = this.insertKey(k0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, i);
    }

    private char doPut(char c0, int i) {
        char c1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            c1 = this._values[i];
            flag = false;
        }

        this._values[i] = c0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c1;
    }

    @Override
    public char remove(Object object) {
        char c0 = this.no_entry_value;
        int i = this.index(object);

        if (i >= 0) {
            c0 = this._values[i];
            this.removeAt(i);
        }

        return c0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectCharMap<? extends K> tobjectcharmap) {
        tobjectcharmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectCharCustomHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectCharCustomHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectCharCustomHashMap.FREE && aobject1[i] != TObjectCharCustomHashMap.REMOVED) {
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
            if (aobject[j] != TObjectCharCustomHashMap.FREE && aobject[j] != TObjectCharCustomHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TCharCollection valueCollection() {
        return new TObjectCharCustomHashMap.TCharValueCollection();
    }

    @Override
    public char[] values() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._values;
        Object[] aobject = this._set;
        int i = achar1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectCharCustomHashMap.FREE && aobject[i] != TObjectCharCustomHashMap.REMOVED) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public char[] values(char[] achar) {
        int i = this.size();

        if (achar.length < i) {
            achar = new char[i];
        }

        char[] achar1 = this._values;
        Object[] aobject = this._set;
        int j = achar1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectCharCustomHashMap.FREE && aobject[j] != TObjectCharCustomHashMap.REMOVED) {
                achar[k++] = achar1[j];
            }
        }

        if (achar.length > i) {
            achar[i] = this.no_entry_value;
        }

        return achar;
    }

    @Override
    public TObjectCharIterator<K> iterator() {
        return new TObjectCharCustomHashMap.TObjectCharHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, '\u0001');
    }

    @Override
    public boolean adjustValue(K k0, char c0) {
        int i = this.index(k0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += c0;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(K k0, char c0, char c1) {
        int i = this.insertKey(k0);
        char c2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            c2 = this._values[i] += c0;
            flag = false;
        } else {
            c2 = this._values[i] = c1;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        Object[] aobject = this._set;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectCharCustomHashMap.FREE || aobject[i] == TObjectCharCustomHashMap.REMOVED || tcharprocedure.execute(achar[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectCharProcedure<? super K> tobjectcharprocedure) {
        Object[] aobject = this._set;
        char[] achar = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectCharCustomHashMap.FREE || aobject[i] == TObjectCharCustomHashMap.REMOVED || tobjectcharprocedure.execute(aobject[i], achar[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectCharProcedure<? super K> tobjectcharprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        char[] achar = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectCharCustomHashMap.FREE && aobject[i] != TObjectCharCustomHashMap.REMOVED && !tobjectcharprocedure.execute(aobject[i], achar[i])) {
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
    public void transformValues(TCharFunction tcharfunction) {
        Object[] aobject = this._set;
        char[] achar = this._values;
        int i = achar.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectCharCustomHashMap.REMOVED) {
                achar[i] = tcharfunction.execute(achar[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectCharMap)) {
            return false;
        } else {
            TObjectCharMap tobjectcharmap = (TObjectCharMap) object;

            if (tobjectcharmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectCharIterator tobjectchariterator = this.iterator();

                    while (tobjectchariterator.hasNext()) {
                        tobjectchariterator.advance();
                        Object object1 = tobjectchariterator.key();
                        char c0 = tobjectchariterator.value();

                        if (c0 == this.no_entry_value) {
                            if (tobjectcharmap.get(object1) != tobjectcharmap.getNoEntryValue() || !tobjectcharmap.containsKey(object1)) {
                                return false;
                            }
                        } else if (c0 != tobjectcharmap.get(object1)) {
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
        char[] achar = this._values;
        int j = achar.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectCharCustomHashMap.FREE && aobject[j] != TObjectCharCustomHashMap.REMOVED) {
                i += HashFunctions.hash(achar[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
        objectoutput.writeChar(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectCharCustomHashMap.REMOVED && this._set[i] != TObjectCharCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeChar(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
        this.no_entry_value = objectinput.readChar();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            char c0 = objectinput.readChar();

            this.put(object, c0);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectCharProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, char c0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(c0);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectCharHashIterator<K> extends TObjectHashIterator<K> implements TObjectCharIterator<K> {

        private final TObjectCharCustomHashMap<K> _map;

        public TObjectCharHashIterator(TObjectCharCustomHashMap<K> tobjectcharcustomhashmap) {
            super(tobjectcharcustomhashmap);
            this._map = tobjectcharcustomhashmap;
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
        public char value() {
            return this._map._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            this._map._values[this._index] = c0;
            return c1;
        }
    }

    class TCharValueCollection implements TCharCollection {

        TCharValueCollection() {}

        @Override
        public TCharIterator iterator() {
            return new TObjectCharCustomHashMap.TCharValueCollection.TObjectCharValueHashIterator();
        }

        @Override
        public char getNoEntryValue() {
            return TObjectCharCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectCharCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectCharCustomHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TObjectCharCustomHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TObjectCharCustomHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TObjectCharCustomHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TObjectCharCustomHashMap.this._values;
            Object[] aobject = TObjectCharCustomHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || c0 != achar[i]);

            TObjectCharCustomHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            char c0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Character)) {
                    return false;
                }

                c0 = (Character) object;
            } while (TObjectCharCustomHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TObjectCharCustomHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TObjectCharCustomHashMap.this.containsValue(c0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Character> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TCharCollection tcharcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(char[] achar) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TCharIterator tchariterator = this.iterator();

            while (tchariterator.hasNext()) {
                if (!collection.contains(tchariterator.next())) {
                    tchariterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                return false;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = this.iterator();

                while (tchariterator.hasNext()) {
                    if (!tcharcollection.contains(tchariterator.next())) {
                        tchariterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(char[] achar) {
            boolean flag = false;

            Arrays.sort(achar);
            char[] achar1 = TObjectCharCustomHashMap.this._values;
            Object[] aobject = TObjectCharCustomHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TObjectCharCustomHashMap.this.removeAt(i);
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

                if (object instanceof Character) {
                    char c0 = (Character) object;

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = tcharcollection.iterator();

                while (tchariterator.hasNext()) {
                    char c0 = tchariterator.next();

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(char[] achar) {
            boolean flag = false;
            int i = achar.length;

            while (i-- > 0) {
                if (this.remove(achar[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectCharCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TObjectCharCustomHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectCharCustomHashMap.this.forEachValue(new TCharProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(char c0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(c0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectCharValueHashIterator implements TCharIterator {

            protected THash _hash = TObjectCharCustomHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectCharValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public char next() {
                this.moveToNextIndex();
                return TObjectCharCustomHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectCharCustomHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectCharCustomHashMap.this._set;
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
            TObjectCharCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectCharCustomHashMap.this.size();
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
            return TObjectCharCustomHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectCharCustomHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectCharCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectCharCustomHashMap.this.no_entry_value != TObjectCharCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectCharCustomHashMap.this.contains(k0);
        }
    }
}
