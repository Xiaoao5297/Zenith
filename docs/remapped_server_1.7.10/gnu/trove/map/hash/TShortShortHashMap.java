package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TShortShortHashMap extends TShortShortHash implements TShortShortMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TShortShortHashMap() {}

    public TShortShortHashMap(int i) {
        super(i);
    }

    public TShortShortHashMap(int i, float f) {
        super(i, f);
    }

    public TShortShortHashMap(int i, float f, short short0, short short1) {
        super(i, f, short0, short1);
    }

    public TShortShortHashMap(short[] ashort, short[] ashort1) {
        super(Math.max(ashort.length, ashort1.length));
        int i = Math.min(ashort.length, ashort1.length);

        for (int j = 0; j < i; ++j) {
            this.put(ashort[j], ashort1[j]);
        }

    }

    public TShortShortHashMap(TShortShortMap tshortshortmap) {
        super(tshortshortmap.size());
        if (tshortshortmap instanceof TShortShortHashMap) {
            TShortShortHashMap tshortshorthashmap = (TShortShortHashMap) tshortshortmap;

            this._loadFactor = tshortshorthashmap._loadFactor;
            this.no_entry_key = tshortshorthashmap.no_entry_key;
            this.no_entry_value = tshortshorthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tshortshortmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new short[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        short[] ashort = this._set;
        short[] ashort1 = this._values;
        byte[] abyte = this._states;

        this._set = new short[i];
        this._values = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                short short0 = ashort[k];
                int l = this.insertKey(short0);

                this._values[l] = ashort1[k];
            }
        }

    }

    @Override
    public short put(short short0, short short1) {
        int i = this.insertKey(short0);

        return this.doPut(short0, short1, i);
    }

    @Override
    public short putIfAbsent(short short0, short short1) {
        int i = this.insertKey(short0);

        return i < 0 ? this._values[-i - 1] : this.doPut(short0, short1, i);
    }

    private short doPut(short short0, short short1, int i) {
        short short2 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            short2 = this._values[i];
            flag = false;
        }

        this._values[i] = short1;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Short> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TShortShortMap tshortshortmap) {
        this.ensureCapacity(tshortshortmap.size());
        TShortShortIterator tshortshortiterator = tshortshortmap.iterator();

        while (tshortshortiterator.hasNext()) {
            tshortshortiterator.advance();
            this.put(tshortshortiterator.key(), tshortshortiterator.value());
        }

    }

    @Override
    public short get(short short0) {
        int i = this.index(short0);

        return i < 0 ? this.no_entry_value : this._values[i];
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
        Arrays.fill(this._states, 0, this._states.length, (byte) 0);
    }

    @Override
    public boolean isEmpty() {
        return 0 == this._size;
    }

    @Override
    public short remove(short short0) {
        short short1 = this.no_entry_value;
        int i = this.index(short0);

        if (i >= 0) {
            short1 = this._values[i];
            this.removeAt(i);
        }

        return short1;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TShortSet keySet() {
        return new TShortShortHashMap.TKeyView();
    }

    @Override
    public short[] keys() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._set;
        byte[] abyte = this._states;
        int i = ashort1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[j++] = ashort1[i];
            }
        }

        return ashort;
    }

    @Override
    public short[] keys(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._set;
        byte[] abyte = this._states;
        int j = ashort1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                ashort[k++] = ashort1[j];
            }
        }

        return ashort;
    }

    @Override
    public TShortCollection valueCollection() {
        return new TShortShortHashMap.TValueView();
    }

    @Override
    public short[] values() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._values;
        byte[] abyte = this._states;
        int i = ashort1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[j++] = ashort1[i];
            }
        }

        return ashort;
    }

    @Override
    public short[] values(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._values;
        byte[] abyte = this._states;
        int j = ashort1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                ashort[k++] = ashort1[j];
            }
        }

        return ashort;
    }

    @Override
    public boolean containsValue(short short0) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || short0 != ashort[i]);

        return true;
    }

    @Override
    public boolean containsKey(short short0) {
        return this.contains(short0);
    }

    @Override
    public TShortShortIterator iterator() {
        return new TShortShortHashMap.TShortShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.forEach(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortprocedure.execute(ashort[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TShortShortProcedure tshortshortprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._set;
        short[] ashort1 = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortshortprocedure.execute(ashort[i], ashort1[i]));

        return false;
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[i] = tshortfunction.execute(ashort[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TShortShortProcedure tshortshortprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        short[] ashort = this._set;
        short[] ashort1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = ashort.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tshortshortprocedure.execute(ashort[i], ashort1[i])) {
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
    public boolean increment(short short0) {
        return this.adjustValue(short0, (short) 1);
    }

    @Override
    public boolean adjustValue(short short0, short short1) {
        int i = this.index(short0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += short1;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(short short0, short short1, short short2) {
        int i = this.insertKey(short0);
        short short3;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            short3 = this._values[i] += short1;
            flag = false;
        } else {
            short3 = this._values[i] = short2;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TShortShortMap)) {
            return false;
        } else {
            TShortShortMap tshortshortmap = (TShortShortMap) object;

            if (tshortshortmap.size() != this.size()) {
                return false;
            } else {
                short[] ashort = this._values;
                byte[] abyte = this._states;
                short short0 = this.getNoEntryValue();
                short short1 = tshortshortmap.getNoEntryValue();
                int i = ashort.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        short short2 = this._set[i];
                        short short3 = tshortshortmap.get(short2);
                        short short4 = ashort[i];

                        if (short4 != short3 && short4 != short0 && short3 != short1) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }
    }

    public int hashCode() {
        int i = 0;
        byte[] abyte = this._states;
        int j = this._values.length;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                i += HashFunctions.hash(this._set[j]) ^ HashFunctions.hash(this._values[j]);
            }
        }

        return i;
    }

    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder("{");

        this.forEachEntry(new TShortShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short short0, short short1) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(short0);
                stringbuilder.append("=");
                stringbuilder.append(short1);
                return true;
            }
        });
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    @Override
    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        super.writeExternal(objectoutput);
        objectoutput.writeInt(this._size);
        int i = this._states.length;

        while (i-- > 0) {
            if (this._states[i] == 1) {
                objectoutput.writeShort(this._set[i]);
                objectoutput.writeShort(this._values[i]);
            }
        }

    }

    @Override
    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        super.readExternal(objectinput);
        int i = objectinput.readInt();

        this.setUp(i);

        while (i-- > 0) {
            short short0 = objectinput.readShort();
            short short1 = objectinput.readShort();

            this.put(short0, short1);
        }

    }

    class TShortShortHashIterator extends THashPrimitiveIterator implements TShortShortIterator {

        TShortShortHashIterator(TShortShortHashMap tshortshorthashmap) {
            super(tshortshorthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public short key() {
            return TShortShortHashMap.this._set[this._index];
        }

        @Override
        public short value() {
            return TShortShortHashMap.this._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            TShortShortHashMap.this._values[this._index] = short0;
            return short1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortShortValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortShortHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortShortKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortShortKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortShortHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TShortCollection {

        protected TValueView() {}

        @Override
        public TShortIterator iterator() {
            return TShortShortHashMap.this.new TShortShortValueHashIterator(TShortShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TShortShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TShortShortHashMap.this._values;
            short[] ashort1 = TShortShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (ashort1[i] == 0 || ashort1[i] == 2 || short0 != ashort[i]);

            TShortShortHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            short short0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Short)) {
                    return false;
                }

                short0 = (Short) object;
            } while (TShortShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortShortHashMap.this.containsValue(short0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection tshortcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] ashort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TShortIterator tshortiterator = this.iterator();

            while (tshortiterator.hasNext()) {
                if (!collection.contains(tshortiterator.next())) {
                    tshortiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                return false;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = this.iterator();

                while (tshortiterator.hasNext()) {
                    if (!tshortcollection.contains(tshortiterator.next())) {
                        tshortiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(short[] ashort) {
            boolean flag = false;

            Arrays.sort(ashort);
            short[] ashort1 = TShortShortHashMap.this._values;
            byte[] abyte = TShortShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortShortHashMap.this.removeAt(i);
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

                if (object instanceof Short) {
                    short short0 = (Short) object;

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = tshortcollection.iterator();

                while (tshortiterator.hasNext()) {
                    short short0 = tshortiterator.next();

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(short[] ashort) {
            boolean flag = false;
            int i = ashort.length;

            while (i-- > 0) {
                if (this.remove(ashort[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TShortShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortShortHashMap.this.forEachValue(new TShortProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(short short0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(short0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }

    protected class TKeyView implements TShortSet {

        protected TKeyView() {}

        @Override
        public TShortIterator iterator() {
            return TShortShortHashMap.this.new TShortShortKeyHashIterator(TShortShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortShortHashMap.this.contains(short0);
        }

        @Override
        public short[] toArray() {
            return TShortShortHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortShortHashMap.this.keys(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            return TShortShortHashMap.this.no_entry_value != TShortShortHashMap.this.remove(short0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            short short0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Short)) {
                    return false;
                }

                short0 = (Short) object;
            } while (TShortShortHashMap.this.containsKey(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortShortHashMap.this.containsKey(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortShortHashMap.this.contains(short0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection tshortcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] ashort) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TShortIterator tshortiterator = this.iterator();

            while (tshortiterator.hasNext()) {
                if (!collection.contains(tshortiterator.next())) {
                    tshortiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                return false;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = this.iterator();

                while (tshortiterator.hasNext()) {
                    if (!tshortcollection.contains(tshortiterator.next())) {
                        tshortiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(short[] ashort) {
            boolean flag = false;

            Arrays.sort(ashort);
            short[] ashort1 = TShortShortHashMap.this._set;
            byte[] abyte = TShortShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortShortHashMap.this.removeAt(i);
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

                if (object instanceof Short) {
                    short short0 = (Short) object;

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TShortCollection tshortcollection) {
            if (this == tshortcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TShortIterator tshortiterator = tshortcollection.iterator();

                while (tshortiterator.hasNext()) {
                    short short0 = tshortiterator.next();

                    if (this.remove(short0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(short[] ashort) {
            boolean flag = false;
            int i = ashort.length;

            while (i-- > 0) {
                if (this.remove(ashort[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TShortShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortShortHashMap.this.forEachKey(tshortprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TShortSet)) {
                return false;
            } else {
                TShortSet tshortset = (TShortSet) object;

                if (tshortset.size() != this.size()) {
                    return false;
                } else {
                    int i = TShortShortHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TShortShortHashMap.this._states[i] != 1 || tshortset.contains(TShortShortHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TShortShortHashMap.this._states.length;

            while (j-- > 0) {
                if (TShortShortHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TShortShortHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortShortHashMap.this.forEachKey(new TShortProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(short short0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(short0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
