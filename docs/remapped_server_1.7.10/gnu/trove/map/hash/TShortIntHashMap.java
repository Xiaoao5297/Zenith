package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TShortIntHashMap extends TShortIntHash implements TShortIntMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TShortIntHashMap() {}

    public TShortIntHashMap(int i) {
        super(i);
    }

    public TShortIntHashMap(int i, float f) {
        super(i, f);
    }

    public TShortIntHashMap(int i, float f, short short0, int j) {
        super(i, f, short0, j);
    }

    public TShortIntHashMap(short[] ashort, int[] aint) {
        super(Math.max(ashort.length, aint.length));
        int i = Math.min(ashort.length, aint.length);

        for (int j = 0; j < i; ++j) {
            this.put(ashort[j], aint[j]);
        }

    }

    public TShortIntHashMap(TShortIntMap tshortintmap) {
        super(tshortintmap.size());
        if (tshortintmap instanceof TShortIntHashMap) {
            TShortIntHashMap tshortinthashmap = (TShortIntHashMap) tshortintmap;

            this._loadFactor = tshortinthashmap._loadFactor;
            this.no_entry_key = tshortinthashmap.no_entry_key;
            this.no_entry_value = tshortinthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tshortintmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new int[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        short[] ashort = this._set;
        int[] aint = this._values;
        byte[] abyte = this._states;

        this._set = new short[i];
        this._values = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                short short0 = ashort[k];
                int l = this.insertKey(short0);

                this._values[l] = aint[k];
            }
        }

    }

    @Override
    public int put(short short0, int i) {
        int j = this.insertKey(short0);

        return this.doPut(short0, i, j);
    }

    @Override
    public int putIfAbsent(short short0, int i) {
        int j = this.insertKey(short0);

        return j < 0 ? this._values[-j - 1] : this.doPut(short0, i, j);
    }

    private int doPut(short short0, int i, int j) {
        int k = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            k = this._values[j];
            flag = false;
        }

        this._values[j] = i;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return k;
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TShortIntMap tshortintmap) {
        this.ensureCapacity(tshortintmap.size());
        TShortIntIterator tshortintiterator = tshortintmap.iterator();

        while (tshortintiterator.hasNext()) {
            tshortintiterator.advance();
            this.put(tshortintiterator.key(), tshortintiterator.value());
        }

    }

    @Override
    public int get(short short0) {
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
    public int remove(short short0) {
        int i = this.no_entry_value;
        int j = this.index(short0);

        if (j >= 0) {
            i = this._values[j];
            this.removeAt(j);
        }

        return i;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TShortSet keySet() {
        return new TShortIntHashMap.TKeyView();
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
    public TIntCollection valueCollection() {
        return new TShortIntHashMap.TValueView();
    }

    @Override
    public int[] values() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._values;
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
    public int[] values(int[] aint) {
        int i = this.size();

        if (aint.length < i) {
            aint = new int[i];
        }

        int[] aint1 = this._values;
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
    public boolean containsValue(int i) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int j = aint.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (abyte[j] != 1 || i != aint[j]);

        return true;
    }

    @Override
    public boolean containsKey(short short0) {
        return this.contains(short0);
    }

    @Override
    public TShortIntIterator iterator() {
        return new TShortIntHashMap.TShortIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.forEach(tshortprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintprocedure.execute(aint[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TShortIntProcedure tshortintprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._set;
        int[] aint = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortintprocedure.execute(ashort[i], aint[i]));

        return false;
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int i = aint.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[i] = tintfunction.execute(aint[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TShortIntProcedure tshortintprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        short[] ashort = this._set;
        int[] aint = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = ashort.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tshortintprocedure.execute(ashort[i], aint[i])) {
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
        return this.adjustValue(short0, 1);
    }

    @Override
    public boolean adjustValue(short short0, int i) {
        int j = this.index(short0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(short short0, int i, int j) {
        int k = this.insertKey(short0);
        int l;
        boolean flag;

        if (k < 0) {
            k = -k - 1;
            l = this._values[k] += i;
            flag = false;
        } else {
            l = this._values[k] = j;
            flag = true;
        }

        byte b0 = this._states[k];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return l;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TShortIntMap)) {
            return false;
        } else {
            TShortIntMap tshortintmap = (TShortIntMap) object;

            if (tshortintmap.size() != this.size()) {
                return false;
            } else {
                int[] aint = this._values;
                byte[] abyte = this._states;
                int i = this.getNoEntryValue();
                int j = tshortintmap.getNoEntryValue();
                int k = aint.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        short short0 = this._set[k];
                        int l = tshortintmap.get(short0);
                        int i1 = aint[k];

                        if (i1 != l && i1 != i && l != j) {
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

        this.forEachEntry(new TShortIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short short0, int i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(short0);
                stringbuilder.append("=");
                stringbuilder.append(i);
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
                objectoutput.writeInt(this._values[i]);
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
            int j = objectinput.readInt();

            this.put(short0, j);
        }

    }

    class TShortIntHashIterator extends THashPrimitiveIterator implements TShortIntIterator {

        TShortIntHashIterator(TShortIntHashMap tshortinthashmap) {
            super(tshortinthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public short key() {
            return TShortIntHashMap.this._set[this._index];
        }

        @Override
        public int value() {
            return TShortIntHashMap.this._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            TShortIntHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TShortIntValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TShortIntHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortIntKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortIntKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortIntHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TIntCollection {

        protected TValueView() {}

        @Override
        public TIntIterator iterator() {
            return TShortIntHashMap.this.new TShortIntValueHashIterator(TShortIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TShortIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TShortIntHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TShortIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TShortIntHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TShortIntHashMap.this._values;
            short[] ashort = TShortIntHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (ashort[j] == 0 || ashort[j] == 2 || i != aint[j]);

            TShortIntHashMap.this.removeAt(j);
            return true;
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
            } while (TShortIntHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TShortIntHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TShortIntHashMap.this.containsValue(k)) {
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
            int[] aint1 = TShortIntHashMap.this._values;
            byte[] abyte = TShortIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TShortIntHashMap.this.removeAt(i);
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
            TShortIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TShortIntHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortIntHashMap.this.forEachValue(new TIntProcedure() {
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

    protected class TKeyView implements TShortSet {

        protected TKeyView() {}

        @Override
        public TShortIterator iterator() {
            return TShortIntHashMap.this.new TShortIntKeyHashIterator(TShortIntHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortIntHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortIntHashMap.this.contains(short0);
        }

        @Override
        public short[] toArray() {
            return TShortIntHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortIntHashMap.this.keys(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            return TShortIntHashMap.this.no_entry_value != TShortIntHashMap.this.remove(short0);
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
            } while (TShortIntHashMap.this.containsKey(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortIntHashMap.this.containsKey(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortIntHashMap.this.contains(short0)) {
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
            short[] ashort1 = TShortIntHashMap.this._set;
            byte[] abyte = TShortIntHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortIntHashMap.this.removeAt(i);
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
            TShortIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortIntHashMap.this.forEachKey(tshortprocedure);
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
                    int i = TShortIntHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TShortIntHashMap.this._states[i] != 1 || tshortset.contains(TShortIntHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TShortIntHashMap.this._states.length;

            while (j-- > 0) {
                if (TShortIntHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TShortIntHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortIntHashMap.this.forEachKey(new TShortProcedure() {
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
