package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
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
import java.util.Set;

public class THashMap<K, V> extends TObjectHash<K> implements TMap<K, V>, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient V[] _values;

    public THashMap() {}

    public THashMap(int i) {
        super(i);
    }

    public THashMap(int i, float f) {
        super(i, f);
    }

    public THashMap(Map<? extends K, ? extends V> map) {
        this(map.size());
        this.putAll(map);
    }

    public THashMap(THashMap<? extends K, ? extends V> thashmap) {
        this(thashmap.size());
        this.putAll(thashmap);
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

            return map.size() != this.size() ? false : this.forEachEntry(new THashMap.EqProcedure<>(map));
        }
    }

    public int hashCode() {
        THashMap.HashProcedure thashmap_hashprocedure = new THashMap.HashProcedure();

        this.forEachEntry(thashmap_hashprocedure);
        return thashmap_hashprocedure.getHashCode();
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
        } while (aobject1[i] == THashMap.FREE || aobject1[i] == THashMap.REMOVED || tobjectprocedure.execute(aobject[i]));

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
        } while (aobject[i] == THashMap.FREE || aobject[i] == THashMap.REMOVED || tobjectobjectprocedure.execute(aobject[i], aobject1[i]));

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
                if (aobject[i] != THashMap.FREE && aobject[i] != THashMap.REMOVED && !tobjectobjectprocedure.execute(aobject[i], aobject1[i])) {
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
            if (aobject1[i] != THashMap.FREE && aobject1[i] != THashMap.REMOVED) {
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
        Arrays.fill(this._set, THashMap.FREE);
        this._values = (Object[]) (new Object[i]);
        int l = 0;
        int i1 = j;

        while (i1-- > 0) {
            Object object = aobject[i1];

            if (object != THashMap.FREE && object != THashMap.REMOVED) {
                int j1 = this.insertKey(object);

                if (j1 < 0) {
                    this.throwObjectContractViolation(this._set[-j1 - 1], object, this.size(), k, aobject);
                }

                this._values[j1] = aobject1[i1];
                ++l;
            }
        }

        reportPotentialConcurrentMod(this.size(), k);
    }

    public V get(Object object) {
        int i = this.index(object);

        return i < 0 ? null : this._values[i];
    }

    @Override
    public void clear() {
        if (this.size() != 0) {
            super.clear();
            Arrays.fill(this._set, 0, this._set.length, THashMap.FREE);
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
        return new THashMap.ValueView();
    }

    public Set<K> keySet() {
        return new THashMap.KeyView();
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new THashMap.EntryView();
    }

    public boolean containsValue(Object object) {
        Object[] aobject = this._set;
        Object[] aobject1 = this._values;
        int i;

        if (null == object) {
            i = aobject1.length;

            while (i-- > 0) {
                if (aobject[i] != THashMap.FREE && aobject[i] != THashMap.REMOVED && object == aobject1[i]) {
                    return true;
                }
            }
        } else {
            i = aobject1.length;

            while (i-- > 0) {
                if (aobject[i] != THashMap.FREE && aobject[i] != THashMap.REMOVED && (object == aobject1[i] || this.equals(object, aobject1[i]))) {
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
            if (this._set[i] != THashMap.REMOVED && this._set[i] != THashMap.FREE) {
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
            if (THashMap.this._values[this.index] != this.val) {
                throw new ConcurrentModificationException();
            } else {
                Object object = this.val;

                THashMap.this._values[this.index] = v0;
                this.val = v0;
                return object;
            }
        }

        public boolean equals(Object object) {
            if (!(object instanceof java.util.Map.Entry)) {
                return false;
            } else {
                java.util.Map.Entry java_util_map_entry = (java.util.Map.Entry) object;

                return THashMap.this.equals(this.getKey(), java_util_map_entry.getKey()) && THashMap.this.equals(this.getValue(), this.getValue());
            }
        }

        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public String toString() {
            return this.key + "=" + this.val;
        }
    }

    protected class KeyView extends THashMap<K, V>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        @Override
        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(THashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return null != THashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return THashMap.this.contains(k0);
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
            try {
                return this.removeElement(object);
            } catch (ClassCastException classcastexception) {
                return false;
            }
        }

        public void clear() {
            THashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return THashMap.this.size();
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
            return THashMap.this.isEmpty();
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

    protected class EntryView extends THashMap<K, V>.MapBackedView<java.util.Map.Entry<K, V>> {

        protected EntryView() {
            super(null);
        }

        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
            return new THashMap.EntryView.EntryIterator(THashMap.this);
        }

        public boolean removeElement(java.util.Map.Entry<K, V> java_util_map_entry) {
            if (java_util_map_entry == null) {
                return false;
            } else {
                Object object = this.keyForEntry(java_util_map_entry);
                int i = THashMap.this.index(object);

                if (i >= 0) {
                    Object object1 = this.valueForEntry(java_util_map_entry);

                    if (object1 == THashMap.this._values[i] || null != object1 && THashMap.this.equals(object1, THashMap.this._values[i])) {
                        THashMap.this.removeAt(i);
                        return true;
                    }
                }

                return false;
            }
        }

        public boolean containsElement(java.util.Map.Entry<K, V> java_util_map_entry) {
            Object object = THashMap.this.get(this.keyForEntry(java_util_map_entry));
            Object object1 = java_util_map_entry.getValue();

            return object1 == object || null != object && THashMap.this.equals(object, object1);
        }

        protected V valueForEntry(java.util.Map.Entry<K, V> java_util_map_entry) {
            return java_util_map_entry.getValue();
        }

        protected K keyForEntry(java.util.Map.Entry<K, V> java_util_map_entry) {
            return java_util_map_entry.getKey();
        }

        private final class EntryIterator extends TObjectHashIterator {

            EntryIterator(THashMap<K, V> thashmap) {
                super(thashmap);
            }

            @Override
            public THashMap<K, V>.Entry objectAtIndex(int i) {
                return THashMap.this.new Entry(THashMap.this._set[i], THashMap.this._values[i], i);
            }
        }
    }

    protected class ValueView extends THashMap<K, V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TObjectHashIterator(THashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return THashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return THashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = THashMap.this._values;
            Object[] aobject1 = THashMap.this._set;
            int i = aobject.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while ((aobject1[i] == TObjectHash.FREE || aobject1[i] == TObjectHash.REMOVED || v0 != aobject[i]) && (null == aobject[i] || !THashMap.this.equals(aobject[i], v0)));

            THashMap.this.removeAt(i);
            return true;
        }
    }

    private final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V> {

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

                return object == v0 || object != null && THashMap.this.equals(object, v0);
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
