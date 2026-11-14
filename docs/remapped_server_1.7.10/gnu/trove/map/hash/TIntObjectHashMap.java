package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
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

public class TIntObjectHashMap<V> extends TIntHash implements TIntObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TIntObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected int no_entry_key;

    public TIntObjectHashMap() {
        this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
            @Override
            public boolean execute(int i, V v0) {
                TIntObjectHashMap.this.put(i, v0);
                return true;
            }
        };
    }

    public TIntObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
            @Override
            public boolean execute(int i, V v0) {
                TIntObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TIntObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
            @Override
            public boolean execute(int i, V v0) {
                TIntObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
    }

    public TIntObjectHashMap(int i, float f, int j) {
        super(i, f);
        this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
            @Override
            public boolean execute(int i, V v0) {
                TIntObjectHashMap.this.put(i, v0);
                return true;
            }
        };
        this.no_entry_key = j;
    }

    public TIntObjectHashMap(TIntObjectMap<? extends V> tintobjectmap) {
        this(tintobjectmap.size(), 0.5F, tintobjectmap.getNoEntryKey());
        this.putAll(tintobjectmap);
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
        int[] aint = this._set;
        Object[] aobject = this._values;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = aobject[k];
            }
        }

    }

    @Override
    public int getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(int i) {
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
    public V get(int i) {
        int j = this.index(i);

        return j < 0 ? null : this._values[j];
    }

    @Override
    public V put(int i, V v0) {
        int j = this.insertKey(i);

        return this.doPut(v0, j);
    }

    @Override
    public V putIfAbsent(int i, V v0) {
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
    public V remove(int i) {
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
    public void putAll(Map<? extends Integer, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TIntObjectMap<? extends V> tintobjectmap) {
        tintobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TIntSet keySet() {
        return new TIntObjectHashMap.KeyView();
    }

    @Override
    public int[] keys() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._set;
        byte[] abyte = this._states;
        int i = aint1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[j++] = aint1[i];
            }
        }

        return aint;
    }

    @Override
    public int[] keys(int[] aint) {
        if (aint.length < this._size) {
            aint = new int[this._size];
        }

        int[] aint1 = this._set;
        byte[] abyte = this._states;
        int i = aint1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[j++] = aint1[i];
            }
        }

        return aint;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TIntObjectHashMap.ValueView();
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
    public TIntObjectIterator<V> iterator() {
        return new TIntObjectHashMap.TIntObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntObjectProcedure<? super V> tintobjectprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        Object[] aobject = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintobjectprocedure.execute(aint[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TIntObjectProcedure<? super V> tintobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintobjectprocedure.execute(aint[i], aobject[i])) {
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
        if (!(object instanceof TIntObjectMap)) {
            return false;
        } else {
            TIntObjectMap tintobjectmap = (TIntObjectMap) object;

            if (tintobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TIntObjectIterator tintobjectiterator = this.iterator();

                    while (tintobjectiterator.hasNext()) {
                        tintobjectiterator.advance();
                        int i = tintobjectiterator.key();
                        Object object1 = tintobjectiterator.value();

                        if (object1 == null) {
                            if (tintobjectmap.get(i) != null || !tintobjectmap.containsKey(i)) {
                                return false;
                            }
                        } else if (!object1.equals(tintobjectmap.get(i))) {
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
        objectoutput.writeInt(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeInt(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readInt();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            int j = objectinput.readInt();
            Object object = objectinput.readObject();

            this.put(j, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TIntObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(int i, Object object) {
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

    class TIntObjectHashIterator<V> extends THashPrimitiveIterator implements TIntObjectIterator<V> {

        private final TIntObjectHashMap<V> _map;

        public TIntObjectHashIterator(TIntObjectHashMap<V> tintobjecthashmap) {
            super(tintobjecthashmap);
            this._map = tintobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
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
            TIntObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TIntObjectHashMap.this.size();
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
            return TIntObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TIntObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TIntObjectHashMap.ValueView.TIntObjectValueHashIterator(TIntObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TIntObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TIntObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TIntObjectHashMap.this._values;
            byte[] abyte = TIntObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TIntObjectHashMap.this.removeAt(i);
            return true;
        }

        class TIntObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TIntObjectHashMap _map;

            public TIntObjectValueHashIterator(TIntObjectHashMap tintobjecthashmap) {
                super(tintobjecthashmap);
                this._map = tintobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TIntObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TIntSet {

        KeyView() {}

        @Override
        public int getNoEntryValue() {
            return TIntObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TIntObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(int i) {
            return TIntObjectHashMap.this.containsKey(i);
        }

        @Override
        public TIntIterator iterator() {
            return new TIntObjectHashMap.KeyView.TIntHashIterator(TIntObjectHashMap.this);
        }

        @Override
        public int[] toArray() {
            return TIntObjectHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntObjectHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return null != TIntObjectHashMap.this.remove(i);
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
            } while (TIntObjectHashMap.this.containsKey((Integer) object));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            if (tintcollection == this) {
                return true;
            } else {
                TIntIterator tintiterator = tintcollection.iterator();

                do {
                    if (!tintiterator.hasNext()) {
                        return true;
                    }
                } while (TIntObjectHashMap.this.containsKey(tintiterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntObjectHashMap.this.containsKey(k)) {
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
            int[] aint1 = TIntObjectHashMap.this._set;
            byte[] abyte = TIntObjectHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntObjectHashMap.this.removeAt(i);
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
            if (tintcollection == this) {
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
            TIntObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntObjectHashMap.this.forEachKey(tintprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TIntSet)) {
                return false;
            } else {
                TIntSet tintset = (TIntSet) object;

                if (tintset.size() != this.size()) {
                    return false;
                } else {
                    int i = TIntObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntObjectHashMap.this._states[i] != 1 || tintset.contains(TIntObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TIntObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TIntObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TIntObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {

            private final TIntHash _hash;

            public TIntHashIterator(TIntHash tinthash) {
                super(tinthash);
                this._hash = tinthash;
            }

            @Override
            public int next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
