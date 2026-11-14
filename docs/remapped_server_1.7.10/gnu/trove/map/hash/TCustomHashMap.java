package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
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
import java.util.Set;

public class TCustomHashMap<K, V> extends TCustomObjectHash<K> implements TMap<K, V>, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient V[] _values;

    public TCustomHashMap() {}

    public TCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
    }

    public TCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
    }

    public TCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
    }

    public TCustomHashMap(HashingStrategy<? super K> hashingstrategy, Map<? extends K, ? extends V> map) {
        this(hashingstrategy, map.size());
        this.putAll(map);
    }

    public TCustomHashMap(HashingStrategy<? super K> hashingstrategy, TCustomHashMap<? extends K, ? extends V> tcustomhashmap) {
        this(hashingstrategy, tcustomhashmap.size());
        this.putAll(tcustomhashmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = (Object[]) (new Object[j]);
        return j;
    }

    public V put(K k0, V v0) {
        int i = this.insertKey(k0);

        return this.doPut(v0, i);
    }

    @Override
    public V putIfAbsent(K k0, V v0) {
        int i = this.insertKey(k0);

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

    public boolean equals(Object object) {
        if (!(object instanceof Map)) {
            return false;
        } else {
            Map map = (Map) object;

            return map.size() != this.size() ? false : this.forEachEntry(new TCustomHashMap.EqProcedure<>(map));
        }
    }

    public int hashCode() {
        TCustomHashMap.HashProcedure tcustomhashmap_hashprocedure = new TCustomHashMap.HashProcedure();

        this.forEachEntry(tcustomhashmap_hashprocedure);
        return tcustomhashmap_hashprocedure.getHashCode();
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectObjectProcedure<K, V>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, V v0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(k0);
                stringbuilder.append("=");
                stringbuilder.append(v0);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> tobjectprocedure) {
        Object[] aobject = this._values;
        Object[] aobject1 = this._set;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject1[i] == TCustomHashMap.FREE || aobject1[i] == TCustomHashMap.REMOVED || tobjectprocedure.execute(aobject[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> tobjectobjectprocedure) {
        Object[] aobject = this._set;
        Object[] aobject1 = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TCustomHashMap.FREE || aobject[i] == TCustomHashMap.REMOVED || tobjectobjectprocedure.execute(aobject[i], aobject1[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> tobjectobjectprocedure) {
        boolean flag = false;
        Object[] aobject = this._set;
        Object[] aobject1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TCustomHashMap.FREE && aobject[i] != TCustomHashMap.REMOVED && !tobjectobjectprocedure.execute(aobject[i], aobject1[i])) {
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
        Object[] aobject = this._values;
        Object[] aobject1 = this._set;
        int i = aobject.length;

        while (i-- > 0) {
            if (aobject1[i] != TCustomHashMap.FREE && aobject1[i] != TCustomHashMap.REMOVED) {
                aobject[i] = tobjectfunction.execute(aobject[i]);
            }
        }

    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        int k = this.size();
        Object[] aobject = this._set;
        Object[] aobject1 = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TCustomHashMap.FREE);
        this._values = (Object[]) (new Object[i]);
        int l = j;

        while (l-- > 0) {
            Object object = aobject[l];

            if (object != TCustomHashMap.FREE && object != TCustomHashMap.REMOVED) {
                int i1 = this.insertKey(object);

                if (i1 < 0) {
                    this.throwObjectContractViolation(this._set[-i1 - 1], object, this.size(), k, aobject);
                }

                this._values[i1] = aobject1[l];
            }
        }

    }

    public V get(Object object) {
        int i = this.index(object);

        return i >= 0 && this.strategy.equals(this._set[i], object) ? this._values[i] : null;
    }

    @Override
    public void clear() {
        if (this.size() != 0) {
            super.clear();
            Arrays.fill(this._set, 0, this._set.length, TCustomHashMap.FREE);
            Arrays.fill(this._values, 0, this._values.length, (Object) null);
        }
    }

    public V remove(Object object) {
        Object object1 = null;
        int i = this.index(object);

        if (i >= 0) {
            object1 = this._values[i];
            this.removeAt(i);
        }

        return object1;
    }

    @Override
    public void removeAt(int i) {
        this._values[i] = null;
        super.removeAt(i);
    }

    public Collection<V> values() {
        return new TCustomHashMap.ValueView();
    }

    public Set<K> keySet() {
        return new TCustomHashMap.KeyView();
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new TCustomHashMap.EntryView();
    }

    public boolean containsValue(Object object) {
        Object[] aobject = this._set;
        Object[] aobject1 = this._values;
        int i;

        if (null == object) {
            i = aobject1.length;

            while (i-- > 0) {
                if (aobject[i] != TCustomHashMap.FREE && aobject[i] != TCustomHashMap.REMOVED && object == aobject1[i]) {
                    return true;
                }
            }
        } else {
            i = aobject1.length;

            while (i-- > 0) {
                if (aobject[i] != TCustomHashMap.FREE && aobject[i] != TCustomHashMap.REMOVED && (object == aobject1[i] || this.strategy.equals(object, aobject1[i]))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry java_util_map_entry = (java.util.Map.Entry) iterator.next();

            this.put(java_util_map_entry.getKey(), java_util_map_entry.getValue());
        }

    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(1);
        super.writeExternal(objectoutput);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TCustomHashMap.REMOVED && this._set[i] != TCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        byte b0 = objectinput.readByte();

        if (b0 != 0) {
            super.readExternal(objectinput);
        }

        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            Object object1 = objectinput.readObject();

            this.put(object, object1);
        }

    }

    final class Entry implements java.util.Map.Entry<K, V> {

        private K key;
        private V val;
        private final int index;

        Entry(K k0, V v0, int i) {
            this.key = k0;
            this.val = v0;
            this.index = i;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.val;
        }

        public V setValue(V v0) {
            if (TCustomHashMap.this._values[this.index] != this.val) {
                throw new ConcurrentModificationException();
            } else {
                Object object = this.val;

                TCustomHashMap.this._values[this.index] = v0;
                this.val = v0;
                return object;
            }
        }

        public boolean equals(Object object) {
            if (!(object instanceof java.util.Map.Entry)) {
                return false;
            } else {
                boolean flag;
                label39:
                {
                    java.util.Map.Entry java_util_map_entry = (java.util.Map.Entry) object;

                    if (this.getKey() == null) {
                        if (java_util_map_entry.getKey() != null) {
                            break label39;
                        }
                    } else if (!TCustomHashMap.this.strategy.equals(this.getKey(), java_util_map_entry.getKey())) {
                        break label39;
                    }

                    if (this.getValue() == null) {
                        if (java_util_map_entry.getValue() != null) {
                            break label39;
                        }
                    } else if (!this.getValue().equals(java_util_map_entry.getValue())) {
                        break label39;
                    }

                    flag = true;
                    return flag;
                }

                flag = false;
                return flag;
            }
        }

        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public String toString() {
            return this.key + "=" + this.val;
        }
    }

    protected class KeyView extends TCustomHashMap<K, V>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        @Override
        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return null != TCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TCustomHashMap.this.contains(k0);
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
            TCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TCustomHashMap.this.size();
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
            return TCustomHashMap.this.isEmpty();
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

        public String toString() {
            Iterator iterator = this.iterator();

            if (!iterator.hasNext()) {
                return "{}";
            } else {
                StringBuilder stringbuilder = new StringBuilder();

                stringbuilder.append('{');

                while (true) {
                    Object object = iterator.next();

                    stringbuilder.append(object == this ? "(this Collection)" : object);
                    if (!iterator.hasNext()) {
                        return stringbuilder.append('}').toString();
                    }

                    stringbuilder.append(", ");
                }
            }
        }
    }

    protected class EntryView extends TCustomHashMap<K, V>.MapBackedView<java.util.Map.Entry<K, V>> {

        protected EntryView() {
            super(null);
        }

        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
            return new TCustomHashMap.EntryView.EntryIterator(TCustomHashMap.this);
        }

        public boolean removeElement(java.util.Map.Entry<K, V> java_util_map_entry) {
            Object object = this.keyForEntry(java_util_map_entry);
            int i = TCustomHashMap.this.index(object);

            if (i >= 0) {
                Object object1 = this.valueForEntry(java_util_map_entry);

                if (object1 == TCustomHashMap.this._values[i] || null != object1 && TCustomHashMap.this.strategy.equals(object1, TCustomHashMap.this._values[i])) {
                    TCustomHashMap.this.removeAt(i);
                    return true;
                }
            }

            return false;
        }

        public boolean containsElement(java.util.Map.Entry<K, V> java_util_map_entry) {
            Object object = TCustomHashMap.this.get(this.keyForEntry(java_util_map_entry));
            Object object1 = java_util_map_entry.getValue();

            return object1 == object || null != object && TCustomHashMap.this.strategy.equals(object, object1);
        }

        protected V valueForEntry(java.util.Map.Entry<K, V> java_util_map_entry) {
            return java_util_map_entry.getValue();
        }

        protected K keyForEntry(java.util.Map.Entry<K, V> java_util_map_entry) {
            return java_util_map_entry.getKey();
        }

        private final class EntryIterator extends TObjectHashIterator {

            EntryIterator(TCustomHashMap<K, V> tcustomhashmap) {
                super(tcustomhashmap);
            }

            @Override
            public TCustomHashMap<K, V>.Entry objectAtIndex(int i) {
                return TCustomHashMap.this.new Entry(TCustomHashMap.this._set[i], TCustomHashMap.this._values[i], i);
            }
        }
    }

    protected class ValueView extends TCustomHashMap<K, V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TObjectHashIterator(TCustomHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TCustomHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TCustomHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TCustomHashMap.this._values;
            Object[] aobject1 = TCustomHashMap.this._set;
            int i = aobject.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while ((aobject1[i] == TObjectHash.FREE || aobject1[i] == TObjectHash.REMOVED || v0 != aobject[i]) && (null == aobject[i] || !TCustomHashMap.this.strategy.equals(aobject[i], v0)));

            TCustomHashMap.this.removeAt(i);
            return true;
        }
    }

    private static final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V> {

        private final Map<K, V> _otherMap;

        EqProcedure(Map<K, V> map) {
            this._otherMap = map;
        }

        @Override
        public final boolean execute(K k0, V v0) {
            if (v0 == null && !this._otherMap.containsKey(k0)) {
                return false;
            } else {
                Object object = this._otherMap.get(k0);

                return object == v0 || object != null && object.equals(v0);
            }
        }
    }

    private final class HashProcedure implements TObjectObjectProcedure<K, V> {

        private int h;

        private HashProcedure() {
            this.h = 0;
        }

        public int getHashCode() {
            return this.h;
        }

        @Override
        public final boolean execute(K k0, V v0) {
            this.h += HashFunctions.hash(k0) ^ (v0 == null ? 0 : v0.hashCode());
            return true;
        }
    }
}
