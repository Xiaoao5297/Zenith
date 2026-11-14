package gnu.trove.map.custom_hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
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

public class TObjectDoubleCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectDoubleMap<K>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TObjectDoubleProcedure<K> PUT_ALL_PROC;
    protected transient double[] _values;
    protected double no_entry_value;

    public TObjectDoubleCustomHashMap() {
        this.PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
            @Override
            public boolean execute(K k0, double d0) {
                TObjectDoubleCustomHashMap.this.put(k0, d0);
                return true;
            }
        };
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> hashingstrategy) {
        super(hashingstrategy);
        this.PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
            @Override
            public boolean execute(K k0, double d0) {
                TObjectDoubleCustomHashMap.this.put(k0, d0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i) {
        super(hashingstrategy, i);
        this.PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
            @Override
            public boolean execute(K k0, double d0) {
                TObjectDoubleCustomHashMap.this.put(k0, d0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
            @Override
            public boolean execute(K k0, double d0) {
                TObjectDoubleCustomHashMap.this.put(k0, d0);
                return true;
            }
        };
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> hashingstrategy, int i, float f, double d0) {
        super(hashingstrategy, i, f);
        this.PUT_ALL_PROC = new TObjectDoubleProcedure<K>() {
            @Override
            public boolean execute(K k0, double d0) {
                TObjectDoubleCustomHashMap.this.put(k0, d0);
                return true;
            }
        };
        this.no_entry_value = d0;
        if (this.no_entry_value != 0.0D) {
            Arrays.fill(this._values, this.no_entry_value);
        }

    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> hashingstrategy, TObjectDoubleMap<? extends K> tobjectdoublemap) {
        this(hashingstrategy, tobjectdoublemap.size(), 0.5F, tobjectdoublemap.getNoEntryValue());
        if (tobjectdoublemap instanceof TObjectDoubleCustomHashMap) {
            TObjectDoubleCustomHashMap tobjectdoublecustomhashmap = (TObjectDoubleCustomHashMap) tobjectdoublemap;

            this._loadFactor = tobjectdoublecustomhashmap._loadFactor;
            this.no_entry_value = tobjectdoublecustomhashmap.no_entry_value;
            this.strategy = tobjectdoublecustomhashmap.strategy;
            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tobjectdoublemap);
    }

    @Override
    public int setUp(int i) {
        int j = super.setUp(i);

        this._values = new double[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        Object[] aobject = (Object[]) this._set;
        double[] adouble = this._values;

        this._set = new Object[i];
        Arrays.fill(this._set, TObjectDoubleCustomHashMap.FREE);
        this._values = new double[i];
        Arrays.fill(this._values, this.no_entry_value);
        int k = j;

        while (k-- > 0) {
            Object object = aobject[k];

            if (object != TObjectDoubleCustomHashMap.FREE && object != TObjectDoubleCustomHashMap.REMOVED) {
                int l = this.insertKey(object);

                if (l < 0) {
                    this.throwObjectContractViolation(this._set[-l - 1], object);
                }

                this._values[l] = adouble[k];
            }
        }

    }

    @Override
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object object) {
        return this.contains(object);
    }

    @Override
    public boolean containsValue(double d0) {
        Object[] aobject = this._set;
        double[] adouble = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (aobject[i] == TObjectDoubleCustomHashMap.FREE || aobject[i] == TObjectDoubleCustomHashMap.REMOVED || d0 != adouble[i]);

        return true;
    }

    @Override
    public double get(Object object) {
        int i = this.index(object);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public double put(K k0, double d0) {
        int i = this.insertKey(k0);

        return this.doPut(d0, i);
    }

    @Override
    public double putIfAbsent(K k0, double d0) {
        int i = this.insertKey(k0);

        return i < 0 ? this._values[-i - 1] : this.doPut(d0, i);
    }

    private double doPut(double d0, int i) {
        double d1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            d1 = this._values[i];
            flag = false;
        }

        this._values[i] = d0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d1;
    }

    @Override
    public double remove(Object object) {
        double d0 = this.no_entry_value;
        int i = this.index(object);

        if (i >= 0) {
            d0 = this._values[i];
            this.removeAt(i);
        }

        return d0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put(entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TObjectDoubleMap<? extends K> tobjectdoublemap) {
        tobjectdoublemap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, TObjectDoubleCustomHashMap.FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new TObjectDoubleCustomHashMap.KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] aobject = (Object[]) (new Object[this.size()]);
        Object[] aobject1 = this._set;
        int i = aobject1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject1[i] != TObjectDoubleCustomHashMap.FREE && aobject1[i] != TObjectDoubleCustomHashMap.REMOVED) {
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
            if (aobject[j] != TObjectDoubleCustomHashMap.FREE && aobject[j] != TObjectDoubleCustomHashMap.REMOVED) {
                ak[k++] = aobject[j];
            }
        }

        return ak;
    }

    @Override
    public TDoubleCollection valueCollection() {
        return new TObjectDoubleCustomHashMap.TDoubleValueCollection();
    }

    @Override
    public double[] values() {
        double[] adouble = new double[this.size()];
        double[] adouble1 = this._values;
        Object[] aobject = this._set;
        int i = adouble1.length;
        int j = 0;

        while (i-- > 0) {
            if (aobject[i] != TObjectDoubleCustomHashMap.FREE && aobject[i] != TObjectDoubleCustomHashMap.REMOVED) {
                adouble[j++] = adouble1[i];
            }
        }

        return adouble;
    }

    @Override
    public double[] values(double[] adouble) {
        int i = this.size();

        if (adouble.length < i) {
            adouble = new double[i];
        }

        double[] adouble1 = this._values;
        Object[] aobject = this._set;
        int j = adouble1.length;
        int k = 0;

        while (j-- > 0) {
            if (aobject[j] != TObjectDoubleCustomHashMap.FREE && aobject[j] != TObjectDoubleCustomHashMap.REMOVED) {
                adouble[k++] = adouble1[j];
            }
        }

        if (adouble.length > i) {
            adouble[i] = this.no_entry_value;
        }

        return adouble;
    }

    @Override
    public TObjectDoubleIterator<K> iterator() {
        return new TObjectDoubleCustomHashMap.TObjectDoubleHashIterator<>(this);
    }

    @Override
    public boolean increment(K k0) {
        return this.adjustValue(k0, 1.0D);
    }

    @Override
    public boolean adjustValue(K k0, double d0) {
        int i = this.index(k0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += d0;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(K k0, double d0, double d1) {
        int i = this.insertKey(k0);
        double d2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            d2 = this._values[i] += d0;
            flag = false;
        } else {
            d2 = this._values[i] = d1;
            flag = true;
        }

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d2;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure) {
        return this.forEach(tobjectprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        Object[] aobject = this._set;
        double[] adouble = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectDoubleCustomHashMap.FREE || aobject[i] == TObjectDoubleCustomHashMap.REMOVED || tdoubleprocedure.execute(adouble[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        Object[] aobject = this._set;
        double[] adouble = this._values;
        int i = aobject.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (aobject[i] == TObjectDoubleCustomHashMap.FREE || aobject[i] == TObjectDoubleCustomHashMap.REMOVED || tobjectdoubleprocedure.execute(aobject[i], adouble[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TObjectDoubleProcedure<? super K> tobjectdoubleprocedure) {
        boolean flag = false;
        Object[] aobject = (Object[]) this._set;
        double[] adouble = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectDoubleCustomHashMap.FREE && aobject[i] != TObjectDoubleCustomHashMap.REMOVED && !tobjectdoubleprocedure.execute(aobject[i], adouble[i])) {
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
    public void transformValues(TDoubleFunction tdoublefunction) {
        Object[] aobject = this._set;
        double[] adouble = this._values;
        int i = adouble.length;

        while (i-- > 0) {
            if (aobject[i] != null && aobject[i] != TObjectDoubleCustomHashMap.REMOVED) {
                adouble[i] = tdoublefunction.execute(adouble[i]);
            }
        }

    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TObjectDoubleMap)) {
            return false;
        } else {
            TObjectDoubleMap tobjectdoublemap = (TObjectDoubleMap) object;

            if (tobjectdoublemap.size() != this.size()) {
                return false;
            } else {
                try {
                    TObjectDoubleIterator tobjectdoubleiterator = this.iterator();

                    while (tobjectdoubleiterator.hasNext()) {
                        tobjectdoubleiterator.advance();
                        Object object1 = tobjectdoubleiterator.key();
                        double d0 = tobjectdoubleiterator.value();

                        if (d0 == this.no_entry_value) {
                            if (tobjectdoublemap.get(object1) != tobjectdoublemap.getNoEntryValue() || !tobjectdoublemap.containsKey(object1)) {
                                return false;
                            }
                        } else if (d0 != tobjectdoublemap.get(object1)) {
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
        double[] adouble = this._values;
        int j = adouble.length;

        while (j-- > 0) {
            if (aobject[j] != TObjectDoubleCustomHashMap.FREE && aobject[j] != TObjectDoubleCustomHashMap.REMOVED) {
                i += HashFunctions.hash(adouble[j]) ^ (aobject[j] == null ? 0 : aobject[j].hashCode());
            }
        }

        return i;
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeObject(this.strategy);
        objectoutput.writeDouble(this.no_entry_value);
        objectoutput.writeInt(this._size);
        int i = this._set.length;

        while (i-- > 0) {
            if (this._set[i] != TObjectDoubleCustomHashMap.REMOVED && this._set[i] != TObjectDoubleCustomHashMap.FREE) {
                objectoutput.writeObject(this._set[i]);
                objectoutput.writeDouble(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.strategy = (HashingStrategy) objectinput.readObject();
        this.no_entry_value = objectinput.readDouble();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            Object object = objectinput.readObject();
            double d0 = objectinput.readDouble();

            this.put(object, d0);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TObjectDoubleProcedure<K>() {
            private boolean first = true;

            @Override
            public boolean execute(K k0, double d0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(k0).append("=").append(d0);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TObjectDoubleHashIterator<K> extends TObjectHashIterator<K> implements TObjectDoubleIterator<K> {

        private final TObjectDoubleCustomHashMap<K> _map;

        public TObjectDoubleHashIterator(TObjectDoubleCustomHashMap<K> tobjectdoublecustomhashmap) {
            super(tobjectdoublecustomhashmap);
            this._map = tobjectdoublecustomhashmap;
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
        public double value() {
            return this._map._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            this._map._values[this._index] = d0;
            return d1;
        }
    }

    class TDoubleValueCollection implements TDoubleCollection {

        TDoubleValueCollection() {}

        @Override
        public TDoubleIterator iterator() {
            return new TObjectDoubleCustomHashMap.TDoubleValueCollection.TObjectDoubleValueHashIterator();
        }

        @Override
        public double getNoEntryValue() {
            return TObjectDoubleCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectDoubleCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectDoubleCustomHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TObjectDoubleCustomHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TObjectDoubleCustomHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TObjectDoubleCustomHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TObjectDoubleCustomHashMap.this._values;
            Object[] aobject = TObjectDoubleCustomHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aobject[i] == TObjectHash.FREE || aobject[i] == TObjectHash.REMOVED || d0 != adouble[i]);

            TObjectDoubleCustomHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            double d0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Double)) {
                    return false;
                }

                d0 = (Double) object;
            } while (TObjectDoubleCustomHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TObjectDoubleCustomHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TObjectDoubleCustomHashMap.this.containsValue(d0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Double> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TDoubleCollection tdoublecollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(double[] adouble) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TDoubleIterator tdoubleiterator = this.iterator();

            while (tdoubleiterator.hasNext()) {
                if (!collection.contains(tdoubleiterator.next())) {
                    tdoubleiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TDoubleCollection tdoublecollection) {
            if (this == tdoublecollection) {
                return false;
            } else {
                boolean flag = false;
                TDoubleIterator tdoubleiterator = this.iterator();

                while (tdoubleiterator.hasNext()) {
                    if (!tdoublecollection.contains(tdoubleiterator.next())) {
                        tdoubleiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(double[] adouble) {
            boolean flag = false;

            Arrays.sort(adouble);
            double[] adouble1 = TObjectDoubleCustomHashMap.this._values;
            Object[] aobject = TObjectDoubleCustomHashMap.this._set;
            int i = aobject.length;

            while (i-- > 0) {
                if (aobject[i] != TObjectHash.FREE && aobject[i] != TObjectHash.REMOVED && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TObjectDoubleCustomHashMap.this.removeAt(i);
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

                if (object instanceof Double) {
                    double d0 = (Double) object;

                    if (this.remove(d0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TDoubleCollection tdoublecollection) {
            if (this == tdoublecollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

                while (tdoubleiterator.hasNext()) {
                    double d0 = tdoubleiterator.next();

                    if (this.remove(d0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(double[] adouble) {
            boolean flag = false;
            int i = adouble.length;

            while (i-- > 0) {
                if (this.remove(adouble[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TObjectDoubleCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TObjectDoubleCustomHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TObjectDoubleCustomHashMap.this.forEachValue(new TDoubleProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(double d0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(d0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }

        class TObjectDoubleValueHashIterator implements TDoubleIterator {

            protected THash _hash = TObjectDoubleCustomHashMap.this;
            protected int _expectedSize;
            protected int _index;

            TObjectDoubleValueHashIterator() {
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            @Override
            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            @Override
            public double next() {
                this.moveToNextIndex();
                return TObjectDoubleCustomHashMap.this._values[this._index];
            }

            @Override
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                } else {
                    try {
                        this._hash.tempDisableAutoCompaction();
                        TObjectDoubleCustomHashMap.this.removeAt(this._index);
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
                    Object[] aobject = TObjectDoubleCustomHashMap.this._set;
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
            TObjectDoubleCustomHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TObjectDoubleCustomHashMap.this.size();
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
            return TObjectDoubleCustomHashMap.this.isEmpty();
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

    protected class KeyView extends TObjectDoubleCustomHashMap<K>.MapBackedView<K> {

        protected KeyView() {
            super(null);
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator<>(TObjectDoubleCustomHashMap.this);
        }

        @Override
        public boolean removeElement(K k0) {
            return TObjectDoubleCustomHashMap.this.no_entry_value != TObjectDoubleCustomHashMap.this.remove(k0);
        }

        @Override
        public boolean containsElement(K k0) {
            return TObjectDoubleCustomHashMap.this.contains(k0);
        }
    }
}
