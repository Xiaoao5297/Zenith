package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
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

public class TLongObjectHashMap<V> extends TLongHash implements TLongObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TLongObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected long no_entry_key;

    public TLongObjectHashMap() {
        this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
            @Override
            public boolean execute(long i, V v0) {
                TLongObjectHashMap.this.put(i, v0);
                return true;
            }
        };
    }

    public TLongObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
            @Override
            public boolean execute(long i, V v0) {
                TLongObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
            @Override
            public boolean execute(long i, V v0) {
                TLongObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int i, float f, long j) {
        super(i, f);
        this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
            @Override
            public boolean execute(long i, V v0) {
                TLongObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = j;
    }

    public TLongObjectHashMap(TLongObjectMap<? extends V> tlongobjectmap) {
        this(tlongobjectmap.size(), 0.5F, tlongobjectmap.getNoEntryKey());
        this.putAll(tlongobjectmap);
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
        long[] along = this._set;
        Object[] aobject = this._values;
        byte[] abyte = this._states;

        this._set = new long[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                long l = along[k];
                int i1 = this.insertKey(l);

                this._values[i1] = aobject[k];
            }
        }

    }

    @Override
    public long getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(long i) {
        return this.contains(i);
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
    public V get(long i) {
        int j = this.index(i);

        return j < 0 ? null : this._values[j];
    }

    @Override
    public V put(long i, V v0) {
        int j = this.insertKey(i);

        return this.doPut(v0, j);
    }

    @Override
    public V putIfAbsent(long i, V v0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(v0, j);
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
    public V remove(long i) {
        Object object = null;
        int j = this.index(i);

        if (j >= 0) {
            object = this._values[j];
            this.removeAt(j);
        }

        return object;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = null;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TLongObjectMap<? extends V> tlongobjectmap) {
        tlongobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TLongSet keySet() {
        return new TLongObjectHashMap.KeyView();
    }

    @Override
    public long[] keys() {
        long[] along = new long[this.size()];
        long[] along1 = this._set;
        byte[] abyte = this._states;
        int i = along1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[j++] = along1[i];
            }
        }

        return along;
    }

    @Override
    public long[] keys(long[] along) {
        if (along.length < this._size) {
            along = new long[this._size];
        }

        long[] along1 = this._set;
        byte[] abyte = this._states;
        int i = along1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[j++] = along1[i];
            }
        }

        return along;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TLongObjectHashMap.ValueView();
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
    public TLongObjectIterator<V> iterator() {
        return new TLongObjectHashMap.TLongObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.forEach(tlongprocedure);
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
    public boolean forEachEntry(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        byte[] abyte = this._states;
        long[] along = this._set;
        Object[] aobject = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongobjectprocedure.execute(along[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TLongObjectProcedure<? super V> tlongobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        long[] along = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = along.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tlongobjectprocedure.execute(along[i], aobject[i])) {
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
        if (!(object instanceof TLongObjectMap)) {
            return false;
        } else {
            TLongObjectMap tlongobjectmap = (TLongObjectMap) object;

            if (tlongobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TLongObjectIterator tlongobjectiterator = this.iterator();

                    while (tlongobjectiterator.hasNext()) {
                        tlongobjectiterator.advance();
                        long i = tlongobjectiterator.key();
                        Object object1 = tlongobjectiterator.value();

                        if (object1 == null) {
                            if (tlongobjectmap.get(i) != null || !tlongobjectmap.containsKey(i)) {
                                return false;
                            }
                        } else if (!object1.equals(tlongobjectmap.get(i))) {
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
        objectoutput.writeLong(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeLong(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readLong();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            long j = objectinput.readLong();
            Object object = objectinput.readObject();

            this.put(j, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TLongObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(long i, Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(i);
                stringbuilder.append("=");
                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TLongObjectHashIterator<V> extends THashPrimitiveIterator implements TLongObjectIterator<V> {

        private final TLongObjectHashMap<V> _map;

        public TLongObjectHashIterator(TLongObjectHashMap<V> tlongobjecthashmap) {
            super(tlongobjecthashmap);
            this._map = tlongobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
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
            TLongObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TLongObjectHashMap.this.size();
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
            return TLongObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TLongObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TLongObjectHashMap.ValueView.TLongObjectValueHashIterator(TLongObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TLongObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TLongObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TLongObjectHashMap.this._values;
            byte[] abyte = TLongObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TLongObjectHashMap.this.removeAt(i);
            return true;
        }

        class TLongObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TLongObjectHashMap _map;

            public TLongObjectValueHashIterator(TLongObjectHashMap tlongobjecthashmap) {
                super(tlongobjecthashmap);
                this._map = tlongobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TLongObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TLongSet {

        KeyView() {}

        @Override
        public long getNoEntryValue() {
            return TLongObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TLongObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(long i) {
            return TLongObjectHashMap.this.containsKey(i);
        }

        @Override
        public TLongIterator iterator() {
            return new TLongObjectHashMap.KeyView.TLongHashIterator(TLongObjectHashMap.this);
        }

        @Override
        public long[] toArray() {
            return TLongObjectHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongObjectHashMap.this.keys(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            return null != TLongObjectHashMap.this.remove(i);
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
            } while (TLongObjectHashMap.this.containsKey((Long) object));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            if (tlongcollection == this) {
                return true;
            } else {
                TLongIterator tlongiterator = tlongcollection.iterator();

                do {
                    if (!tlongiterator.hasNext()) {
                        return true;
                    }
                } while (TLongObjectHashMap.this.containsKey(tlongiterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongObjectHashMap.this.containsKey(k)) {
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
            long[] along1 = TLongObjectHashMap.this._set;
            byte[] abyte = TLongObjectHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongObjectHashMap.this.removeAt(i);
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
            if (tlongcollection == this) {
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
            TLongObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongObjectHashMap.this.forEachKey(tlongprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TLongSet)) {
                return false;
            } else {
                TLongSet tlongset = (TLongSet) object;

                if (tlongset.size() != this.size()) {
                    return false;
                } else {
                    int i = TLongObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TLongObjectHashMap.this._states[i] != 1 || tlongset.contains(TLongObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TLongObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TLongObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TLongObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TLongObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TLongObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TLongObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TLongHashIterator extends THashPrimitiveIterator implements TLongIterator {

            private final TLongHash _hash;

            public TLongHashIterator(TLongHash tlonghash) {
                super(tlonghash);
                this._hash = tlonghash;
            }

            @Override
            public long next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
