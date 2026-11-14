package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TCharObjectHashMap<V> extends TCharHash implements TCharObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TCharObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected char no_entry_key;

    public TCharObjectHashMap() {
        this.PUT_ALL_PROC = new TCharObjectProcedure<V>() {
            @Override
            public boolean execute(char c0, V v0) {
                TCharObjectHashMap.this.put(c0, v0);
                return true;
            }
        };
    }

    public TCharObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TCharObjectProcedure<V>() {
            @Override
            public boolean execute(char c0, V v0) {
                TCharObjectHashMap.this.put(c0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TCharObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TCharObjectProcedure<V>() {
            @Override
            public boolean execute(char c0, V v0) {
                TCharObjectHashMap.this.put(c0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
    }

    public TCharObjectHashMap(int i, float f, char c0) {
        super(i, f);
        this.PUT_ALL_PROC = new TCharObjectProcedure<V>() {
            @Override
            public boolean execute(char c0, V v0) {
                TCharObjectHashMap.this.put(c0, v0);
                return true;
            }
        };
        this.no_entry_key = c0;
    }

    public TCharObjectHashMap(TCharObjectMap<? extends V> tcharobjectmap) {
        this(tcharobjectmap.size(), 0.5F, tcharobjectmap.getNoEntryKey());
        this.putAll(tcharobjectmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = (Object[]) (new Object[j]);
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        char[] achar = this._set;
        Object[] aobject = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = aobject[k];
            }
        }

    }

    @Override
    public char getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public boolean containsValue(Object object) {
        byte[] abyte = this._states;
        Object[] aobject = this._values;
        int i;

        if (null == object) {
            i = aobject.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && null == aobject[i]) {
                    return true;
                }
            }
        } else {
            i = aobject.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && (object == aobject[i] || object.equals(aobject[i]))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(char c0) {
        int i = this.index(c0);

        return i < 0 ? null : this._values[i];
    }

    @Override
    public V put(char c0, V v0) {
        int i = this.insertKey(c0);

        return this.doPut(v0, i);
    }

    @Override
    public V putIfAbsent(char c0, V v0) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(v0, i);
    }

    private V doPut(V v0, int i) {
        Object object = null;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            object = this._values[i];
            flag = false;
        }

        this._values[i] = v0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return object;
    }

    @Override
    public V remove(char c0) {
        Object object = null;
        int i = this.index(c0);

        if (i >= 0) {
            object = this._values[i];
            this.removeAt(i);
        }

        return object;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = null;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TCharObjectMap<? extends V> tcharobjectmap) {
        tcharobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TCharSet keySet() {
        return new TCharObjectHashMap.KeyView();
    }

    @Override
    public char[] keys() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = achar1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public char[] keys(char[] achar) {
        if (achar.length < this._size) {
            achar = new char[this._size];
        }

        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = achar1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TCharObjectHashMap.ValueView();
    }

    @Override
    public Object[] values() {
        Object[] aobject = new Object[this.size()];
        Object[] aobject1 = this._values;
        byte[] abyte = this._states;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aobject[j++] = aobject1[i];
            }
        }

        return aobject;
    }

    @Override
    public V[] values(V[] av) {
        if (av.length < this._size) {
            av = (Object[]) ((Object[]) Array.newInstance(av.getClass().getComponentType(), this._size));
        }

        Object[] aobject = this._values;
        byte[] abyte = this._states;
        int i = aobject.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                av[j++] = aobject[i];
            }
        }

        return av;
    }

    @Override
    public TCharObjectIterator<V> iterator() {
        return new TCharObjectHashMap.TCharObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        byte[] abyte = this._states;
        Object[] aobject = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tobjectprocedure.execute(aobject[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        Object[] aobject = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharobjectprocedure.execute(achar[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TCharObjectProcedure<? super V> tcharobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharobjectprocedure.execute(achar[i], aobject[i])) {
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
    public void transformValues(TObjectFunction<V, V> tobjectfunction) {
        byte[] abyte = this._states;
        Object[] aobject = this._values;
        int i = aobject.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aobject[i] = tobjectfunction.execute(aobject[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TCharObjectMap)) {
            return false;
        } else {
            TCharObjectMap tcharobjectmap = (TCharObjectMap) object;

            if (tcharobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TCharObjectIterator tcharobjectiterator = this.iterator();

                    while (tcharobjectiterator.hasNext()) {
                        tcharobjectiterator.advance();
                        char c0 = tcharobjectiterator.key();
                        Object object1 = tcharobjectiterator.value();

                        if (object1 == null) {
                            if (tcharobjectmap.get(c0) != null || !tcharobjectmap.containsKey(c0)) {
                                return false;
                            }
                        } else if (!object1.equals(tcharobjectmap.get(c0))) {
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
        Object[] aobject = this._values;
        byte[] abyte = this._states;
        int j = aobject.length;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                i += HashFunctions.hash(this._set[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeChar(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeChar(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readChar();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            char c0 = objectinput.readChar();
            Object object = objectinput.readObject();

            this.put(c0, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TCharObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(c0);
                stringbuilder.append("=");
                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TCharObjectHashIterator<V> extends THashPrimitiveIterator implements TCharObjectIterator<V> {

        private final TCharObjectHashMap<V> _map;

        public TCharObjectHashIterator(TCharObjectHashMap<V> tcharobjecthashmap) {
            super(tcharobjecthashmap);
            this._map = tcharobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return this._map._set[this._index];
        }

        @Override
        public V value() {
            return this._map._values[this._index];
        }

        @Override
        public V setValue(V v0) {
            Object object = this.value();

            this._map._values[this._index] = v0;
            return object;
        }
    }

    private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {

        private MapBackedView() {}

        public abstract Iterator<E> iterator();

        public abstract boolean removeElement(E e0);

        public abstract boolean containsElement(E e0);

        public boolean contains(Object object) {
            return this.containsElement(object);
        }

        public boolean remove(Object object) {
            return this.removeElement(object);
        }

        public void clear() {
            TCharObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TCharObjectHashMap.this.size();
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
            return TCharObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TCharObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TCharObjectHashMap.ValueView.TCharObjectValueHashIterator(TCharObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TCharObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TCharObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TCharObjectHashMap.this._values;
            byte[] abyte = TCharObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TCharObjectHashMap.this.removeAt(i);
            return true;
        }

        class TCharObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TCharObjectHashMap _map;

            public TCharObjectValueHashIterator(TCharObjectHashMap tcharobjecthashmap) {
                super(tcharobjecthashmap);
                this._map = tcharobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TCharObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TCharSet {

        KeyView() {}

        @Override
        public char getNoEntryValue() {
            return TCharObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TCharObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(char c0) {
            return TCharObjectHashMap.this.containsKey(c0);
        }

        @Override
        public TCharIterator iterator() {
            return new TCharObjectHashMap.KeyView.TCharHashIterator(TCharObjectHashMap.this);
        }

        @Override
        public char[] toArray() {
            return TCharObjectHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharObjectHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return null != TCharObjectHashMap.this.remove(c0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            Object object;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                object = iterator.next();
            } while (TCharObjectHashMap.this.containsKey((Character) object));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            if (tcharcollection == this) {
                return true;
            } else {
                TCharIterator tchariterator = tcharcollection.iterator();

                do {
                    if (!tchariterator.hasNext()) {
                        return true;
                    }
                } while (TCharObjectHashMap.this.containsKey(tchariterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharObjectHashMap.this.containsKey(c0)) {
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
            char[] achar1 = TCharObjectHashMap.this._set;
            byte[] abyte = TCharObjectHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharObjectHashMap.this.removeAt(i);
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
            if (tcharcollection == this) {
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
            TCharObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharObjectHashMap.this.forEachKey(tcharprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TCharSet)) {
                return false;
            } else {
                TCharSet tcharset = (TCharSet) object;

                if (tcharset.size() != this.size()) {
                    return false;
                } else {
                    int i = TCharObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharObjectHashMap.this._states[i] != 1 || tcharset.contains(TCharObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TCharObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TCharObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TCharObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TCharHashIterator extends THashPrimitiveIterator implements TCharIterator {

            private final TCharHash _hash;

            public TCharHashIterator(TCharHash tcharhash) {
                super(tcharhash);
                this._hash = tcharhash;
            }

            @Override
            public char next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
