package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
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

public class TIntIntHashMap extends TIntIntHash implements TIntIntMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TIntIntHashMap() {}

    public TIntIntHashMap(int i) {
        super(i);
    }

    public TIntIntHashMap(int i, float f) {
        super(i, f);
    }

    public TIntIntHashMap(int i, float f, int j, int k) {
        super(i, f, j, k);
    }

    public TIntIntHashMap(int[] aint, int[] aint1) {
        super(Math.max(aint.length, aint1.length));
        int i = Math.min(aint.length, aint1.length);

        for (int j = 0; j < i; ++j) {
            this.put(aint[j], aint1[j]);
        }

    }

    public TIntIntHashMap(TIntIntMap tintintmap) {
        super(tintintmap.size());
        if (tintintmap instanceof TIntIntHashMap) {
            TIntIntHashMap tintinthashmap = (TIntIntHashMap) tintintmap;

            this._loadFactor = tintinthashmap._loadFactor;
            this.no_entry_key = tintinthashmap.no_entry_key;
            this.no_entry_value = tintinthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tintintmap);
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
        int[] aint = this._set;
        int[] aint1 = this._values;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._values = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = aint1[k];
            }
        }

    }

    @Override
    public int put(int i, int j) {
        int k = this.insertKey(i);

        return this.doPut(i, j, k);
    }

    @Override
    public int putIfAbsent(int i, int j) {
        int k = this.insertKey(i);

        return k < 0 ? this._values[-k - 1] : this.doPut(i, j, k);
    }

    private int doPut(int i, int j, int k) {
        int l = this.no_entry_value;
        boolean flag = true;

        if (k < 0) {
            k = -k - 1;
            l = this._values[k];
            flag = false;
        }

        this._values[k] = j;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return l;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TIntIntMap tintintmap) {
        this.ensureCapacity(tintintmap.size());
        TIntIntIterator tintintiterator = tintintmap.iterator();

        while (tintintiterator.hasNext()) {
            tintintiterator.advance();
            this.put(tintintiterator.key(), tintintiterator.value());
        }

    }

    @Override
    public int get(int i) {
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
    public int remove(int i) {
        int j = this.no_entry_value;
        int k = this.index(i);

        if (k >= 0) {
            j = this._values[k];
            this.removeAt(k);
        }

        return j;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TIntSet keySet() {
        return new TIntIntHashMap.TKeyView();
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
    public TIntCollection valueCollection() {
        return new TIntIntHashMap.TValueView();
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
    public boolean containsKey(int i) {
        return this.contains(i);
    }

    @Override
    public TIntIntIterator iterator() {
        return new TIntIntHashMap.TIntIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntIntProcedure tintintprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        int[] aint1 = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintintprocedure.execute(aint[i], aint1[i]));

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
    public boolean retainEntries(TIntIntProcedure tintintprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        int[] aint1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintintprocedure.execute(aint[i], aint1[i])) {
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
        return this.adjustValue(i, 1);
    }

    @Override
    public boolean adjustValue(int i, int j) {
        int k = this.index(i);

        if (k < 0) {
            return false;
        } else {
            this._values[k] += j;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(int i, int j, int k) {
        int l = this.insertKey(i);
        int i1;
        boolean flag;

        if (l < 0) {
            l = -l - 1;
            i1 = this._values[l] += j;
            flag = false;
        } else {
            i1 = this._values[l] = k;
            flag = true;
        }

        byte b0 = this._states[l];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return i1;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TIntIntMap)) {
            return false;
        } else {
            TIntIntMap tintintmap = (TIntIntMap) object;

            if (tintintmap.size() != this.size()) {
                return false;
            } else {
                int[] aint = this._values;
                byte[] abyte = this._states;
                int i = this.getNoEntryValue();
                int j = tintintmap.getNoEntryValue();
                int k = aint.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        int l = this._set[k];
                        int i1 = tintintmap.get(l);
                        int j1 = aint[k];

                        if (j1 != i1 && j1 != i && i1 != j) {
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

        this.forEachEntry(new TIntIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int i, int j) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
                stringbuilder.append("=");
                stringbuilder.append(j);
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
            int j = objectinput.readInt();
            int k = objectinput.readInt();

            this.put(j, k);
        }

    }

    class TIntIntHashIterator extends THashPrimitiveIterator implements TIntIntIterator {

        TIntIntHashIterator(TIntIntHashMap tintinthashmap) {
            super(tintinthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
            return TIntIntHashMap.this._set[this._index];
        }

        @Override
        public int value() {
            return TIntIntHashMap.this._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            TIntIntHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntIntValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntIntHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntIntKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntIntKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntIntHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntIntHashMap.this.removeAt(this._index);
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
            return TIntIntHashMap.this.new TIntIntValueHashIterator(TIntIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TIntIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntIntHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TIntIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntIntHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TIntIntHashMap.this._values;
            int[] aint1 = TIntIntHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (aint1[j] == 0 || aint1[j] == 2 || i != aint[j]);

            TIntIntHashMap.this.removeAt(j);
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
            } while (TIntIntHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntIntHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntIntHashMap.this.containsValue(k)) {
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
            int[] aint1 = TIntIntHashMap.this._values;
            byte[] abyte = TIntIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntIntHashMap.this.removeAt(i);
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
            TIntIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntIntHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntIntHashMap.this.forEachValue(new TIntProcedure() {
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

    protected class TKeyView implements TIntSet {

        protected TKeyView() {}

        @Override
        public TIntIterator iterator() {
            return TIntIntHashMap.this.new TIntIntKeyHashIterator(TIntIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntIntHashMap.this.contains(i);
        }

        @Override
        public int[] toArray() {
            return TIntIntHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntIntHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return TIntIntHashMap.this.no_entry_value != TIntIntHashMap.this.remove(i);
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
            } while (TIntIntHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntIntHashMap.this.containsKey(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntIntHashMap.this.contains(k)) {
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
            int[] aint1 = TIntIntHashMap.this._set;
            byte[] abyte = TIntIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntIntHashMap.this.removeAt(i);
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
            TIntIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntIntHashMap.this.forEachKey(tintprocedure);
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
                    int i = TIntIntHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntIntHashMap.this._states[i] != 1 || tintset.contains(TIntIntHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntIntHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntIntHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntIntHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntIntHashMap.this.forEachKey(new TIntProcedure() {
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
