package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
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

public class TByteObjectHashMap<V> extends TByteHash implements TByteObjectMap<V>, Externalizable {

    static final long serialVersionUID = 1L;
    private final TByteObjectProcedure<V> PUT_ALL_PROC;
    protected transient V[] _values;
    protected byte no_entry_key;

    public TByteObjectHashMap() {
        this.PUT_ALL_PROC = new TByteObjectProcedure<V>() {
            @Override
            public boolean execute(byte b0, V v0) {
                TByteObjectHashMap.this.put(b0, v0);
                return true;
            }
        };
    }

    public TByteObjectHashMap(int i) {
        super(i);
        this.PUT_ALL_PROC = new TByteObjectProcedure<V>() {
            @Override
            public boolean execute(byte b0, V v0) {
                TByteObjectHashMap.this.put(b0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TByteObjectHashMap(int i, float f) {
        super(i, f);
        this.PUT_ALL_PROC = new TByteObjectProcedure<V>() {
            @Override
            public boolean execute(byte b0, V v0) {
                TByteObjectHashMap.this.put(b0, v0);
                return true;
            }
        };
        this.no_entry_key = Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
    }

    public TByteObjectHashMap(int i, float f, byte b0) {
        super(i, f);
        this.PUT_ALL_PROC = new TByteObjectProcedure<V>() {
            @Override
            public boolean execute(byte b0, V v0) {
                TByteObjectHashMap.this.put(b0, v0);
                return true;
            }
        };
        this.no_entry_key = b0;
    }

    public TByteObjectHashMap(TByteObjectMap<? extends V> tbyteobjectmap) {
        this(tbyteobjectmap.size(), 0.5F, tbyteobjectmap.getNoEntryKey());
        this.putAll(tbyteobjectmap);
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
        byte[] abyte = this._set;
        Object[] aobject = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = (Object[]) (new Object[i]);
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = aobject[k];
            }
        }

    }

    @Override
    public byte getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(byte b0) {
        return this.contains(b0);
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
    public V get(byte b0) {
        int i = this.index(b0);

        return i < 0 ? null : this._values[i];
    }

    @Override
    public V put(byte b0, V v0) {
        int i = this.insertKey(b0);

        return this.doPut(v0, i);
    }

    @Override
    public V putIfAbsent(byte b0, V v0) {
        int i = this.insertKey(b0);

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
    public V remove(byte b0) {
        Object object = null;
        int i = this.index(b0);

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
    public void putAll(Map<? extends Byte, ? extends V> map) {
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void putAll(TByteObjectMap<? extends V> tbyteobjectmap) {
        tbyteobjectmap.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
        Arrays.fill(this._values, 0, this._values.length, (Object) null);
    }

    @Override
    public TByteSet keySet() {
        return new TByteObjectHashMap.KeyView();
    }

    @Override
    public byte[] keys() {
        byte[] abyte = new byte[this.size()];
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int i = abyte1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte2[i] == 1) {
                abyte[j++] = abyte1[i];
            }
        }

        return abyte;
    }

    @Override
    public byte[] keys(byte[] abyte) {
        if (abyte.length < this._size) {
            abyte = new byte[this._size];
        }

        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int i = abyte1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte2[i] == 1) {
                abyte[j++] = abyte1[i];
            }
        }

        return abyte;
    }

    @Override
    public Collection<V> valueCollection() {
        return new TByteObjectHashMap.ValueView();
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
    public TByteObjectIterator<V> iterator() {
        return new TByteObjectHashMap.TByteObjectHashIterator<>(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
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
    public boolean forEachEntry(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        Object[] aobject = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbyteobjectprocedure.execute(abyte1[i], aobject[i]));

        return false;
    }

    @Override
    public boolean retainEntries(TByteObjectProcedure<? super V> tbyteobjectprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        Object[] aobject = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbyteobjectprocedure.execute(abyte1[i], aobject[i])) {
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
        if (!(object instanceof TByteObjectMap)) {
            return false;
        } else {
            TByteObjectMap tbyteobjectmap = (TByteObjectMap) object;

            if (tbyteobjectmap.size() != this.size()) {
                return false;
            } else {
                try {
                    TByteObjectIterator tbyteobjectiterator = this.iterator();

                    while (tbyteobjectiterator.hasNext()) {
                        tbyteobjectiterator.advance();
                        byte b0 = tbyteobjectiterator.key();
                        Object object1 = tbyteobjectiterator.value();

                        if (object1 == null) {
                            if (tbyteobjectmap.get(b0) != null || !tbyteobjectmap.containsKey(b0)) {
                                return false;
                            }
                        } else if (!object1.equals(tbyteobjectmap.get(b0))) {
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
        objectoutput.writeByte(this.no_entry_key);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeByte(this._set[i]);
                objectoutput.writeObject(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        this.no_entry_key = objectinput.readByte();
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            byte b0 = objectinput.readByte();
            Object object = objectinput.readObject();

            this.put(b0, object);
        }

    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TByteObjectProcedure<V>() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, Object object) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(",");
                }

                stringbuilder.append(b0);
                stringbuilder.append("=");
                stringbuilder.append(object);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class TByteObjectHashIterator<V> extends THashPrimitiveIterator implements TByteObjectIterator<V> {

        private final TByteObjectHashMap<V> _map;

        public TByteObjectHashIterator(TByteObjectHashMap<V> tbyteobjecthashmap) {
            super(tbyteobjecthashmap);
            this._map = tbyteobjecthashmap;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
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
            TByteObjectHashMap.this.clear();
        }

        public boolean add(E e0) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return TByteObjectHashMap.this.size();
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
            return TByteObjectHashMap.this.isEmpty();
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

    protected class ValueView extends TByteObjectHashMap<V>.MapBackedView<V> {

        protected ValueView() {
            super(null);
        }

        @Override
        public Iterator<V> iterator() {
            return new TByteObjectHashMap.ValueView.TByteObjectValueHashIterator(TByteObjectHashMap.this) {
                @Override
                protected V objectAtIndex(int i) {
                    return TByteObjectHashMap.this._values[i];
                }
            };
        }

        @Override
        public boolean containsElement(V v0) {
            return TByteObjectHashMap.this.containsValue(v0);
        }

        @Override
        public boolean removeElement(V v0) {
            Object[] aobject = TByteObjectHashMap.this._values;
            byte[] abyte = TByteObjectHashMap.this._states;
            int i = aobject.length;

            do {
                do {
                    if (i-- <= 0) {
                        return false;
                    }
                } while (abyte[i] != 1);
            } while (v0 != aobject[i] && (null == aobject[i] || !aobject[i].equals(v0)));

            TByteObjectHashMap.this.removeAt(i);
            return true;
        }

        class TByteObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {

            protected final TByteObjectHashMap _map;

            public TByteObjectValueHashIterator(TByteObjectHashMap tbyteobjecthashmap) {
                super(tbyteobjecthashmap);
                this._map = tbyteobjecthashmap;
            }

            protected V objectAtIndex(int i) {
                byte[] abyte = TByteObjectHashMap.this._states;
                Object object = this._map._values[i];

                return abyte[i] != 1 ? null : object;
            }

            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }
    }

    class KeyView implements TByteSet {

        KeyView() {}

        @Override
        public byte getNoEntryValue() {
            return TByteObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TByteObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteObjectHashMap.this.containsKey(b0);
        }

        @Override
        public TByteIterator iterator() {
            return new TByteObjectHashMap.KeyView.TByteHashIterator(TByteObjectHashMap.this);
        }

        @Override
        public byte[] toArray() {
            return TByteObjectHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteObjectHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return null != TByteObjectHashMap.this.remove(b0);
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
            } while (TByteObjectHashMap.this.containsKey((Byte) object));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            if (tbytecollection == this) {
                return true;
            } else {
                TByteIterator tbyteiterator = tbytecollection.iterator();

                do {
                    if (!tbyteiterator.hasNext()) {
                        return true;
                    }
                } while (TByteObjectHashMap.this.containsKey(tbyteiterator.next()));

                return false;
            }
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteObjectHashMap.this.containsKey(b0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TByteCollection tbytecollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(byte[] abyte) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TByteIterator tbyteiterator = this.iterator();

            while (tbyteiterator.hasNext()) {
                if (!collection.contains(tbyteiterator.next())) {
                    tbyteiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TByteCollection tbytecollection) {
            if (this == tbytecollection) {
                return false;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = this.iterator();

                while (tbyteiterator.hasNext()) {
                    if (!tbytecollection.contains(tbyteiterator.next())) {
                        tbyteiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(byte[] abyte) {
            boolean flag = false;

            Arrays.sort(abyte);
            byte[] abyte1 = TByteObjectHashMap.this._set;
            byte[] abyte2 = TByteObjectHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteObjectHashMap.this.removeAt(i);
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

                if (object instanceof Byte) {
                    byte b0 = (Byte) object;

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TByteCollection tbytecollection) {
            if (tbytecollection == this) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = tbytecollection.iterator();

                while (tbyteiterator.hasNext()) {
                    byte b0 = tbyteiterator.next();

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(byte[] abyte) {
            boolean flag = false;
            int i = abyte.length;

            while (i-- > 0) {
                if (this.remove(abyte[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TByteObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteObjectHashMap.this.forEachKey(tbyteprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TByteSet)) {
                return false;
            } else {
                TByteSet tbyteset = (TByteSet) object;

                if (tbyteset.size() != this.size()) {
                    return false;
                } else {
                    int i = TByteObjectHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteObjectHashMap.this._states[i] != 1 || tbyteset.contains(TByteObjectHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteObjectHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteObjectHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteObjectHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder("{");
            boolean flag = true;
            int i = TByteObjectHashMap.this._states.length;

            while (i-- > 0) {
                if (TByteObjectHashMap.this._states[i] == 1) {
                    if (flag) {
                        flag = false;
                    } else {
                        stringbuilder.append(",");
                    }

                    stringbuilder.append(TByteObjectHashMap.this._set[i]);
                }
            }

            return stringbuilder.toString();
        }

        class TByteHashIterator extends THashPrimitiveIterator implements TByteIterator {

            private final TByteHash _hash;

            public TByteHashIterator(TByteHash tbytehash) {
                super(tbytehash);
                this._hash = tbytehash;
            }

            @Override
            public byte next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }
    }
}
