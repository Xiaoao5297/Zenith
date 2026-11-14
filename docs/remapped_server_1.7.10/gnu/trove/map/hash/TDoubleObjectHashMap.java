package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
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

public class TDoubleObjectHashMap<V> extends TDoubleHash implements TDoubleObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TDoubleObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected double no_entry_key;

    public TDoubleObjectHashMap() {
        this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
            @Override
            public boolean execute(double d0, V v0) {
                TDoubleObjectHashMap.this.put(d0, v0);
                return true;
            }
        };
    }

    public TDoubleObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
            @Override
            public boolean execute(double d0, V v0) {
                TDoubleObjectHashMap.this.put(d0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TDoubleObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
            @Override
            public boolean execute(double d0, V v0) {
                TDoubleObjectHashMap.this.put(d0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TDoubleObjectHashMap(int i, float f, double d0) {
        super(i, f);
        this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
            @Override
            public boolean execute(double d0, V v0) {
                TDoubleObjectHashMap.this.put(d0, v0);
                return true;
            }
        };
        this.no_entry_key = d0;
    }

    public TDoubleObjectHashMap(TDoubleObjectMap<? extends V> tdoubleobjectmap) {
        this(tdoubleobjectmap.size(), 0.5F, tdoubleobjectmap.getNoEntryKey());
        this.putAll(tdoubleobjectmap);
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
        double[] adouble = this._set;
        Object[] aobject = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = aobject[k];
            }
        }

    }

    @Override
    public double getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(double d0) {
        return this.contains(d0);
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
    public V get(double d0) {
        int i = this.index(d0);

        return i < 0 ? null : this._values[i];
    }

    @Override
    public V put(double d0, V v0) {
        int i = this.insertKey(d0);

        return this.doPut(v0, i);
    }

    @Override
    public V putIfAbsent(double d0, V v0) {
        int i = this.insertKey(d0);

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
    public V remove(double d0) {
        Object object = null;
        int i = this.index(d0);

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
    public void putAll(Map<? extends Double, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleObjectMap<? extends V> tdoubleobjectmap) {
        tdoubleobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TDoubleSet keySet() {
        return new TDoubleObjectHashMap.KeyView();
    }

    @Override
    public double[] keys() {
        double[] adouble = new double[this.size()];
        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int i = adouble1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[j++] = adouble1[i];
            }
        }

        return adouble;
    }

    @Override
    public double[] keys(double[] adouble) {
        if (adouble.length < this._size) {
            adouble = new double[this._size];
        }

        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int i = adouble1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[j++] = adouble1[i];
            }
        }

        return adouble;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TDoubleObjectHashMap.ValueView();
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
    public TDoubleObjectIterator<V> iterator() {
        return new TDoubleObjectHashMap.TDoubleObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
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
    public boolean forEachEntry(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        Object[] aobject = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoubleobjectprocedure.execute(adouble[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TDoubleObjectProcedure<? super V> tdoubleobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoubleobjectprocedure.execute(adouble[i], aobject[i])) {
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
        if (!(object instanceof TDoubleObjectMap)) {
            return false;
        } else {
            TDoubleObjectMap tdoubleobjectmap = (TDoubleObjectMap) object;

            if (tdoubleobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TDoubleObjectIterator tdoubleobjectiterator = this.iterator();

                    while (tdoubleobjectiterator.hasNext()) {
                        tdoubleobjectiterator.advance();
                        double d0 = tdoubleobjectiterator.key();
                        Object object1 = tdoubleobjectiterator.value();

                        if (object1 == null) {
                            if (tdoubleobjectmap.get(d0) != null || !tdoubleobjectmap.containsKey(d0)) {
                                return false;
                            }
                        } else if (!object1.equals(tdoubleobjectmap.get(d0))) {
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
        objectoutput.writeDouble(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeDouble(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readDouble();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            double d0 = objectinput.readDouble();
            Object object = objectinput.readObject();

            this.put(d0, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TDoubleObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(d0);
                stringbuilder.append("=");
                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TDoubleObjectHashIterator<V> extends THashPrimitiveIterator implements TDoubleObjectIterator<V> {

        private final TDoubleObjectHashMap<V> _map;

        public TDoubleObjectHashIterator(TDoubleObjectHashMap<V> tdoubleobjecthashmap) {
            super(tdoubleobjecthashmap);
            this._map = tdoubleobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
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
            TDoubleObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TDoubleObjectHashMap.this.size();
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
            return TDoubleObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TDoubleObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TDoubleObjectHashMap.ValueView.TDoubleObjectValueHashIterator(TDoubleObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TDoubleObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TDoubleObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TDoubleObjectHashMap.this._values;
            byte[] abyte = TDoubleObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TDoubleObjectHashMap.this.removeAt(i);
            return true;
        }

        class TDoubleObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TDoubleObjectHashMap _map;

            public TDoubleObjectValueHashIterator(TDoubleObjectHashMap tdoubleobjecthashmap) {
                super(tdoubleobjecthashmap);
                this._map = tdoubleobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TDoubleObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TDoubleSet {

        KeyView() {}

        @Override
        public double getNoEntryValue() {
            return TDoubleObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TDoubleObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleObjectHashMap.this.containsKey(d0);
        }

        @Override
        public TDoubleIterator iterator() {
            return new TDoubleObjectHashMap.KeyView.TDoubleHashIterator(TDoubleObjectHashMap.this);
        }

        @Override
        public double[] toArray() {
            return TDoubleObjectHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleObjectHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return null != TDoubleObjectHashMap.this.remove(d0);
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
            } while (TDoubleObjectHashMap.this.containsKey((Double) object));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            if (tdoublecollection == this) {
                return true;
            } else {
                TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

                do {
                    if (!tdoubleiterator.hasNext()) {
                        return true;
                    }
                } while (TDoubleObjectHashMap.this.containsKey(tdoubleiterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleObjectHashMap.this.containsKey(d0)) {
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
            double[] adouble1 = TDoubleObjectHashMap.this._set;
            byte[] abyte = TDoubleObjectHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleObjectHashMap.this.removeAt(i);
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
            if (tdoublecollection == this) {
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
            TDoubleObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleObjectHashMap.this.forEachKey(tdoubleprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TDoubleSet)) {
                return false;
            } else {
                TDoubleSet tdoubleset = (TDoubleSet) object;

                if (tdoubleset.size() != this.size()) {
                    return false;
                } else {
                    int i = TDoubleObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleObjectHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TDoubleObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TDoubleObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TDoubleObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TDoubleHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

            private final TDoubleHash _hash;

            public TDoubleHashIterator(TDoubleHash tdoublehash) {
                super(tdoublehash);
                this._hash = tdoublehash;
            }

            @Override
            public double next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
