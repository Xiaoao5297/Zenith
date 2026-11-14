package gnu.trove.map.custom_hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
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

public class TObjectFloatCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectFloatMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectFloatProcedure<K> PUT_ALL_PROC;
    protected transient float[] _values;
    protected float no_entry_value;

    public TObjectFloatCustomHashMap() {
        this.PUT_ALL_PROC = new TObjectFloatProcedure<K>() {
            @Override
            public boolean execute(K k0, float f) {
                TObjectFloatCustomHashMap.this.put(k0, f);
                return true;
            }
        };
    }

    public TObjectFloatCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
        this.PUT_ALL_PROC = new TObjectFloatProcedure<K>() {
            @Override
            public boolean execute(K k0, float f) {
                TObjectFloatCustomHashMap.this.put(k0, f);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
        this.PUT_ALL_PROC = new TObjectFloatProcedure<K>() {
            @Override
            public boolean execute(K k0, float f) {
                TObjectFloatCustomHashMap.this.put(k0, f);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectFloatProcedure<K>() {
            @Override
            public boolean execute(K k0, float f) {
                TObjectFloatCustomHashMap.this.put(k0, f);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TObjectFloatCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f, float f1) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectFloatProcedure<K>() {
            @Override
            public boolean execute(K k0, float f) {
                TObjectFloatCustomHashMap.this.put(k0, f);
                return true;
            }
        };
        this.no_entry_value = f1;
        if (this.no_entry_value != 0.0F) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectFloatCustomHashMap(HashingStrategy<? super K> hashingstrategy, TObjectFloatMap<? extends K> tobjectfloatmap) {
        this(hashingstrategy, tobjectfloatmap.size(), 0.5F, tobjectfloatmap.getNoEntryValue());
        if (tobjectfloatmap instanceof TObjectFloatCustomHashMap) {
            TObjectFloatCustomHashMap tobjectfloatcustomhashmap = (TObjectFloatCustomHashMap) tobjectfloatmap;

            this._loadFactor = tobjectfloatcustomhashmap._loadFactor;
            this.no_entry_value = tobjectfloatcustomhashmap.no_entry_value;
            this.strategy = tobjectfloatcustomhashmap.strategy;
            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectfloatmap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new float[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        float[] afloat = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectFloatCustomHashMap.FREE);
        this._values = new float[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            Object object = aobject[k];

            if (object != TObjectFloatCustomHashMap.FREE && object != TObjectFloatCustomHashMap.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._values[l] = afloat[k];
            }
        }

    }

    @Override
    public float getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(float f) {
        Object[] aobject = this._set;
        float[] afloat = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (aobject[i] == TObjectFloatCustomHashMap.FREE || aobject[i] == TObjectFloatCustomHashMap.REMOVED || f != afloat[i]);

        return true;
    }

    @Override
    public float get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public float put(K k0, float f) {
        int i = this.insertKey(k0);

        return this.doPut(f, i);
    }

    @Override
    public float putIfAbsent(K k0, float f) {
        int i = this.insertKey(k0);

        return i < 0 ? this._values[-i - 1] : this.doPut(f, i);
    }

    private float doPut(float f, int i) {
        float f1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            f1 = this._values[i];
            flag = false;
        }

        this._values[i] = f;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f1;
    }

    @Override
    public float remove(Object object) {
        float f = this.no_entry_value;
        int i = this.index(object);

        if (i >= 0) {
            f = this._values[i];
            this.removeAt(i);
        }

        return f;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> tobjectfloatmap) {
        tobjectfloatmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectFloatCustomHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectFloatCustomHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectFloatCustomHashMap.FREE && aobject1[i] != TObjectFloatCustomHashMap.REMOVED) {
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
            if (aobject[j] != TObjectFloatCustomHashMap.FREE && aobject[j] != TObjectFloatCustomHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TFloatCollection valueCollection() {
        return new TObjectFloatCustomHashMap.TFloatValueCollection();
    }

    @Override
    public float[] values() {
        float[] afloat = new float[this.size()];
        float[] afloat1 = this._values;
        Object[] aobject = this._set;
        int i = afloat1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectFloatCustomHashMap.FREE && aobject[i] != TObjectFloatCustomHashMap.REMOVED) {
                afloat[j++] = afloat1[i];
            }
        }

        return afloat;
    }

    @Override
    public float[] values(float[] afloat) {
        int i = this.size();

        if (afloat.length < i) {
            afloat = new float[i];
        }

        float[] afloat1 = this._values;
        Object[] aobject = this._set;
        int j = afloat1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectFloatCustomHashMap.FREE && aobject[j] != TObjectFloatCustomHashMap.REMOVED) {
                afloat[k++] = afloat1[j];
            }
        }

        if (afloat.length > i) {
            afloat[i] = this.no_entry_value;
        }

        return afloat;
    }

    @Override
    public TObjectFloatIterator<K> iterator() {
        return new TObjectFloatCustomHashMap.TObjectFloatHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, 1.0F);
    }

    @Override
    public boolean adjustValue(K k0, float f) {
        int i = this.index(k0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += f;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(K k0, float f, float f1) {
        int i = this.insertKey(k0);
        float f2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            f2 = this._values[i] += f;
            flag = false;
        } else {
            f2 = this._values[i] = f1;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f2;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        Object[] aobject = this._set;
        float[] afloat = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectFloatCustomHashMap.FREE || aobject[i] == TObjectFloatCustomHashMap.REMOVED || tfloatprocedure.execute(afloat[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        Object[] aobject = this._set;
        float[] afloat = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectFloatCustomHashMap.FREE || aobject[i] == TObjectFloatCustomHashMap.REMOVED || tobjectfloatprocedure.execute(aobject[i], afloat[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> tobjectfloatprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        float[] afloat = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectFloatCustomHashMap.FREE && aobject[i] != TObjectFloatCustomHashMap.REMOVED && !tobjectfloatprocedure.execute(aobject[i], afloat[i])) {
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
    public void transformValues(TFloatFunction tfloatfunction) {
        Object[] aobject = this._set;
        float[] afloat = this._values;
        int i = afloat.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectFloatCustomHashMap.REMOVED) {
                afloat[i] = tfloatfunction.execute(afloat[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectFloatMap)) {
            return false;
        } else {
            TObjectFloatMap tobjectfloatmap = (TObjectFloatMap) object;

            if (tobjectfloatmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectFloatIterator tobjectfloatiterator = this.iterator();

                    while (tobjectfloatiterator.hasNext()) {
                        tobjectfloatiterator.advance();
                        Object object1 = tobjectfloatiterator.key();
                        float f = tobjectfloatiterator.value();

                        if (f == this.no_entry_value) {
                            if (tobjectfloatmap.get(object1) != tobjectfloatmap.getNoEntryValue() || !tobjectfloatmap.containsKey(object1)) {
                                return false;
                            }
                        } else if (f != tobjectfloatmap.get(object1)) {
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
        float[] afloat = this._values;
        int j = afloat.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectFloatCustomHashMap.FREE && aobject[j] != TObjectFloatCustomHashMap.REMOVED) {
                i += HashFunctions.hash(afloat[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
        objectoutput.writeFloat(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectFloatCustomHashMap.REMOVED && this._set[i] != TObjectFloatCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeFloat(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
        this.no_entry_value = objectinput.readFloat();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            float f = objectinput.readFloat();

            this.put(object, f);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectFloatProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, float f) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(f);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectFloatHashIterator<K> extends TObjectHashIterator<K> implements TObjectFloatIterator<K> {

        private final TObjectFloatCustomHashMap<K> _map;

        public TObjectFloatHashIterator(TObjectFloatCustomHashMap<K> tobjectfloatcustomhashmap) {
            super(tobjectfloatcustomhashmap);
            this._map = tobjectfloatcustomhashmap;
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
        public float value() {
            return this._map._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            this._map._values[this._index] = f;
            return f1;
        }
    }

    class TFloatValueCollection implements TFloatCollection {

        TFloatValueCollection() {}

        @Override
        public TFloatIterator iterator() {
            return new TObjectFloatCustomHashMap.TFloatValueCollection.TObjectFloatValueHashIterator();
        }

        @Override
        public float getNoEntryValue() {
            return TObjectFloatCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectFloatCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectFloatCustomHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TObjectFloatCustomHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TObjectFloatCustomHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TObjectFloatCustomHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TObjectFloatCustomHashMap.this._values;
            Object[] aobject = TObjectFloatCustomHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || f != afloat[i]);

            TObjectFloatCustomHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            float f;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Float)) {
                    return false;
                }

                f = (Float) object;
            } while (TObjectFloatCustomHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TObjectFloatCustomHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TObjectFloatCustomHashMap.this.containsValue(f)) {
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
            float[] afloat1 = TObjectFloatCustomHashMap.this._values;
            Object[] aobject = TObjectFloatCustomHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TObjectFloatCustomHashMap.this.removeAt(i);
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
            if (this == tfloatcollection) {
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
            TObjectFloatCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TObjectFloatCustomHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectFloatCustomHashMap.this.forEachValue(new TFloatProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(float f) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(f);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectFloatValueHashIterator implements TFloatIterator {

            protected THash _hash = TObjectFloatCustomHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectFloatValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public float next() {
                this.moveToNextIndex();
                return TObjectFloatCustomHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectFloatCustomHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectFloatCustomHashMap.this._set;
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
            TObjectFloatCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectFloatCustomHashMap.this.size();
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
            return TObjectFloatCustomHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectFloatCustomHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectFloatCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectFloatCustomHashMap.this.no_entry_value != TObjectFloatCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectFloatCustomHashMap.this.contains(k0);
        }
    }
}
