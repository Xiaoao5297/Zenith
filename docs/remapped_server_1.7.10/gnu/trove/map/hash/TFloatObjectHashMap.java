package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
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

public class TFloatObjectHashMap<V> extends TFloatHash implements TFloatObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TFloatObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected float no_entry_key;

    public TFloatObjectHashMap() {
        this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
            @Override
            public boolean execute(float f, V v0) {
                TFloatObjectHashMap.this.put(f, v0);
                return true;
            }
        };
    }

    public TFloatObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
            @Override
            public boolean execute(float f, V v0) {
                TFloatObjectHashMap.this.put(f, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TFloatObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
            @Override
            public boolean execute(float f, V v0) {
                TFloatObjectHashMap.this.put(f, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TFloatObjectHashMap(int i, float f, float f1) {
        super(i, f);
        this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
            @Override
            public boolean execute(float f, V v0) {
                TFloatObjectHashMap.this.put(f, v0);
                return true;
            }
        };
        this.no_entry_key = f1;
    }

    public TFloatObjectHashMap(TFloatObjectMap<? extends V> tfloatobjectmap) {
        this(tfloatobjectmap.size(), 0.5F, tfloatobjectmap.getNoEntryKey());
        this.putAll(tfloatobjectmap);
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
        float[] afloat = this._set;
        Object[] aobject = this._values;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];
                int l = this.insertKey(f);

                this._values[l] = aobject[k];
            }
        }

    }

    @Override
    public float getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(float f) {
        return this.contains(f);
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
    public V get(float f) {
        int i = this.index(f);

        return i < 0 ? null : this._values[i];
    }

    @Override
    public V put(float f, V v0) {
        int i = this.insertKey(f);

        return this.doPut(v0, i);
    }

    @Override
    public V putIfAbsent(float f, V v0) {
        int i = this.insertKey(f);

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
    public V remove(float f) {
        Object object = null;
        int i = this.index(f);

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
    public void putAll(Map<? extends Float, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TFloatObjectMap<? extends V> tfloatobjectmap) {
        tfloatobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TFloatSet keySet() {
        return new TFloatObjectHashMap.KeyView();
    }

    @Override
    public float[] keys() {
        float[] afloat = new float[this.size()];
        float[] afloat1 = this._set;
        byte[] abyte = this._states;
        int i = afloat1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[j++] = afloat1[i];
            }
        }

        return afloat;
    }

    @Override
    public float[] keys(float[] afloat) {
        if (afloat.length < this._size) {
            afloat = new float[this._size];
        }

        float[] afloat1 = this._set;
        byte[] abyte = this._states;
        int i = afloat1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[j++] = afloat1[i];
            }
        }

        return afloat;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TFloatObjectHashMap.ValueView();
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
    public TFloatObjectIterator<V> iterator() {
        return new TFloatObjectHashMap.TFloatObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.forEach(tfloatprocedure);
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
    public boolean forEachEntry(TFloatObjectProcedure<? super V> tfloatobjectprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        Object[] aobject = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatobjectprocedure.execute(afloat[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TFloatObjectProcedure<? super V> tfloatobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        float[] afloat = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = afloat.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tfloatobjectprocedure.execute(afloat[i], aobject[i])) {
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
        if (!(object instanceof TFloatObjectMap)) {
            return false;
        } else {
            TFloatObjectMap tfloatobjectmap = (TFloatObjectMap) object;

            if (tfloatobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TFloatObjectIterator tfloatobjectiterator = this.iterator();

                    while (tfloatobjectiterator.hasNext()) {
                        tfloatobjectiterator.advance();
                        float f = tfloatobjectiterator.key();
                        Object object1 = tfloatobjectiterator.value();

                        if (object1 == null) {
                            if (tfloatobjectmap.get(f) != null || !tfloatobjectmap.containsKey(f)) {
                                return false;
                            }
                        } else if (!object1.equals(tfloatobjectmap.get(f))) {
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
        objectoutput.writeFloat(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeFloat(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readFloat();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            float f = objectinput.readFloat();
            Object object = objectinput.readObject();

            this.put(f, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TFloatObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(float f, Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(f);
                stringbuilder.append("=");
                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TFloatObjectHashIterator<V> extends THashPrimitiveIterator implements TFloatObjectIterator<V> {

        private final TFloatObjectHashMap<V> _map;

        public TFloatObjectHashIterator(TFloatObjectHashMap<V> tfloatobjecthashmap) {
            super(tfloatobjecthashmap);
            this._map = tfloatobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
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
            TFloatObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TFloatObjectHashMap.this.size();
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
            return TFloatObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TFloatObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TFloatObjectHashMap.ValueView.TFloatObjectValueHashIterator(TFloatObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TFloatObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TFloatObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TFloatObjectHashMap.this._values;
            byte[] abyte = TFloatObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TFloatObjectHashMap.this.removeAt(i);
            return true;
        }

        class TFloatObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TFloatObjectHashMap _map;

            public TFloatObjectValueHashIterator(TFloatObjectHashMap tfloatobjecthashmap) {
                super(tfloatobjecthashmap);
                this._map = tfloatobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TFloatObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TFloatSet {

        KeyView() {}

        @Override
        public float getNoEntryValue() {
            return TFloatObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TFloatObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(float f) {
            return TFloatObjectHashMap.this.containsKey(f);
        }

        @Override
        public TFloatIterator iterator() {
            return new TFloatObjectHashMap.KeyView.TFloatHashIterator(TFloatObjectHashMap.this);
        }

        @Override
        public float[] toArray() {
            return TFloatObjectHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatObjectHashMap.this.keys(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            return null != TFloatObjectHashMap.this.remove(f);
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
            } while (TFloatObjectHashMap.this.containsKey((Float) object));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            if (tfloatcollection == this) {
                return true;
            } else {
                TFloatIterator tfloatiterator = tfloatcollection.iterator();

                do {
                    if (!tfloatiterator.hasNext()) {
                        return true;
                    }
                } while (TFloatObjectHashMap.this.containsKey(tfloatiterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatObjectHashMap.this.containsKey(f)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Float> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TFloatCollection tfloatcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(float[] afloat) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TFloatIterator tfloatiterator = this.iterator();

            while (tfloatiterator.hasNext()) {
                if (!collection.contains(tfloatiterator.next())) {
                    tfloatiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TFloatCollection tfloatcollection) {
            if (this == tfloatcollection) {
                return false;
            } else {
                boolean flag = false;
                TFloatIterator tfloatiterator = this.iterator();

                while (tfloatiterator.hasNext()) {
                    if (!tfloatcollection.contains(tfloatiterator.next())) {
                        tfloatiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(float[] afloat) {
            boolean flag = false;

            Arrays.sort(afloat);
            float[] afloat1 = TFloatObjectHashMap.this._set;
            byte[] abyte = TFloatObjectHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatObjectHashMap.this.removeAt(i);
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

                if (object instanceof Float) {
                    float f = (Float) object;

                    if (this.remove(f)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TFloatCollection tfloatcollection) {
            if (tfloatcollection == this) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TFloatIterator tfloatiterator = tfloatcollection.iterator();

                while (tfloatiterator.hasNext()) {
                    float f = tfloatiterator.next();

                    if (this.remove(f)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(float[] afloat) {
            boolean flag = false;
            int i = afloat.length;

            while (i-- > 0) {
                if (this.remove(afloat[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TFloatObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatObjectHashMap.this.forEachKey(tfloatprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TFloatSet)) {
                return false;
            } else {
                TFloatSet tfloatset = (TFloatSet) object;

                if (tfloatset.size() != this.size()) {
                    return false;
                } else {
                    int i = TFloatObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TFloatObjectHashMap.this._states[i] != 1 || tfloatset.contains(TFloatObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TFloatObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TFloatObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TFloatObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TFloatObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TFloatObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TFloatObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TFloatHashIterator extends THashPrimitiveIterator implements TFloatIterator {

            private final TFloatHash _hash;

            public TFloatHashIterator(TFloatHash tfloathash) {
                super(tfloathash);
                this._hash = tfloathash;
            }

            @Override
            public float next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
