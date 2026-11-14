package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntShortHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TIntShortMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
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

public class TIntShortHashMap extends TIntShortHash implements TIntShortMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TIntShortHashMap() {}

    public TIntShortHashMap(int i) {
        super(i);
    }

    public TIntShortHashMap(int i, float f) {
        super(i, f);
    }

    public TIntShortHashMap(int i, float f, int j, short short0) {
        super(i, f, j, short0);
    }

    public TIntShortHashMap(int[] aint, short[] ashort) {
        super(Math.max(aint.length, ashort.length));
        int i = Math.min(aint.length, ashort.length);

        for (int j = 0; j < i; ++j) {
            this.put(aint[j], ashort[j]);
        }

    }

    public TIntShortHashMap(TIntShortMap tintshortmap) {
        super(tintshortmap.size());
        if (tintshortmap instanceof TIntShortHashMap) {
            TIntShortHashMap tintshorthashmap = (TIntShortHashMap) tintshortmap;

            this._loadFactor = tintshorthashmap._loadFactor;
            this.no_entry_key = tintshorthashmap.no_entry_key;
            this.no_entry_value = tintshorthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tintshortmap);
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
        int[] aint = this._set;
        short[] ashort = this._values;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._values = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = ashort[k];
            }
        }

    }

    @Override
    public short put(int i, short short0) {
        int j = this.insertKey(i);

        return this.doPut(i, short0, j);
    }

    @Override
    public short putIfAbsent(int i, short short0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, short0, j);
    }

    private short doPut(int i, short short0, int j) {
        short short1 = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            short1 = this._values[j];
            flag = false;
        }

        this._values[j] = short0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short1;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Short> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TIntShortMap tintshortmap) {
        this.ensureCapacity(tintshortmap.size());
        TIntShortIterator tintshortiterator = tintshortmap.iterator();

        while (tintshortiterator.hasNext()) {
            tintshortiterator.advance();
            this.put(tintshortiterator.key(), tintshortiterator.value());
        }

    }

    @Override
    public short get(int i) {
        int j = this.index(i);

        return j < 0 ? this.no_entry_value : this._values[j];
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
    public short remove(int i) {
        short short0 = this.no_entry_value;
        int j = this.index(i);

        if (j >= 0) {
            short0 = this._values[j];
            this.removeAt(j);
        }

        return short0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TIntSet keySet() {
        return new TIntShortHashMap.TKeyView();
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
        int i = this.size();

        if (aint.length < i) {
            aint = new int[i];
        }

        int[] aint1 = this._set;
        byte[] abyte = this._states;
        int j = aint1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                aint[k++] = aint1[j];
            }
        }

        return aint;
    }

    @Override
    public TShortCollection valueCollection() {
        return new TIntShortHashMap.TValueView();
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
    public boolean containsKey(int i) {
        return this.contains(i);
    }

    @Override
    public TIntShortIterator iterator() {
        return new TIntShortHashMap.TIntShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntShortProcedure tintshortprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        short[] ashort = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintshortprocedure.execute(aint[i], ashort[i]));

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
    public boolean retainEntries(TIntShortProcedure tintshortprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        short[] ashort = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintshortprocedure.execute(aint[i], ashort[i])) {
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
    public boolean increment(int i) {
        return this.adjustValue(i, (short) 1);
    }

    @Override
    public boolean adjustValue(int i, short short0) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += short0;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(int i, short short0, short short1) {
        int j = this.insertKey(i);
        short short2;
        boolean flag;

        if (j < 0) {
            j = -j - 1;
            short2 = this._values[j] += short0;
            flag = false;
        } else {
            short2 = this._values[j] = short1;
            flag = true;
        }

        byte b0 = this._states[j];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TIntShortMap)) {
            return false;
        } else {
            TIntShortMap tintshortmap = (TIntShortMap) object;

            if (tintshortmap.size() != this.size()) {
                return false;
            } else {
                short[] ashort = this._values;
                byte[] abyte = this._states;
                short short0 = this.getNoEntryValue();
                short short1 = tintshortmap.getNoEntryValue();
                int i = ashort.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        int j = this._set[i];
                        short short2 = tintshortmap.get(j);
                        short short3 = ashort[i];

                        if (short3 != short2 && short3 != short0 && short2 != short1) {
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

        this.forEachEntry(new TIntShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int i, short short0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
                stringbuilder.append("=");
                stringbuilder.append(short0);
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
                objectoutput.writeInt(this._set[i]);
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
            int j = objectinput.readInt();
            short short0 = objectinput.readShort();

            this.put(j, short0);
        }

    }

    class TIntShortHashIterator extends THashPrimitiveIterator implements TIntShortIterator {

        TIntShortHashIterator(TIntShortHashMap tintshorthashmap) {
            super(tintshorthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
            return TIntShortHashMap.this._set[this._index];
        }

        @Override
        public short value() {
            return TIntShortHashMap.this._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            TIntShortHashMap.this._values[this._index] = short0;
            return short1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TIntShortValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TIntShortHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntShortKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntShortKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntShortHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntShortHashMap.this.removeAt(this._index);
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
            return TIntShortHashMap.this.new TIntShortValueHashIterator(TIntShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TIntShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TIntShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TIntShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TIntShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TIntShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TIntShortHashMap.this._values;
            int[] aint = TIntShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aint[i] == 0 || aint[i] == 2 || short0 != ashort[i]);

            TIntShortHashMap.this.removeAt(i);
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
            } while (TIntShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TIntShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TIntShortHashMap.this.containsValue(short0)) {
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
            short[] ashort1 = TIntShortHashMap.this._values;
            byte[] abyte = TIntShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TIntShortHashMap.this.removeAt(i);
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
            TIntShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TIntShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntShortHashMap.this.forEachValue(new TShortProcedure() {
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

    protected class TKeyView implements TIntSet {

        protected TKeyView() {}

        @Override
        public TIntIterator iterator() {
            return TIntShortHashMap.this.new TIntShortKeyHashIterator(TIntShortHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntShortHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntShortHashMap.this.contains(i);
        }

        @Override
        public int[] toArray() {
            return TIntShortHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntShortHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return TIntShortHashMap.this.no_entry_value != TIntShortHashMap.this.remove(i);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            int i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Integer)) {
                    return false;
                }

                i = (Integer) object;
            } while (TIntShortHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntShortHashMap.this.containsKey(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntShortHashMap.this.contains(k)) {
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
            int[] aint1 = TIntShortHashMap.this._set;
            byte[] abyte = TIntShortHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntShortHashMap.this.removeAt(i);
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
            if (this == tintcollection) {
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
            TIntShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntShortHashMap.this.forEachKey(tintprocedure);
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
                    int i = TIntShortHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntShortHashMap.this._states[i] != 1 || tintset.contains(TIntShortHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntShortHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntShortHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntShortHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntShortHashMap.this.forEachKey(new TIntProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(int i) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(i);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
