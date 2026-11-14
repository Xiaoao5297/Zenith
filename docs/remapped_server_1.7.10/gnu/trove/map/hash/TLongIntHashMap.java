package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
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

public class TLongIntHashMap extends TLongIntHash implements TLongIntMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TLongIntHashMap() {}

    public TLongIntHashMap(int i) {
        super(i);
    }

    public TLongIntHashMap(int i, float f) {
        super(i, f);
    }

    public TLongIntHashMap(int i, float f, long j, int k) {
        super(i, f, j, k);
    }

    public TLongIntHashMap(long[] along, int[] aint) {
        super(Math.max(along.length, aint.length));
        int i = Math.min(along.length, aint.length);

        for (int j = 0; j < i; ++j) {
            this.put(along[j], aint[j]);
        }

    }

    public TLongIntHashMap(TLongIntMap tlongintmap) {
        super(tlongintmap.size());
        if (tlongintmap instanceof TLongIntHashMap) {
            TLongIntHashMap tlonginthashmap = (TLongIntHashMap) tlongintmap;

            this._loadFactor = tlonginthashmap._loadFactor;
            this.no_entry_key = tlonginthashmap.no_entry_key;
            this.no_entry_value = tlonginthashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tlongintmap);
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
        long[] along = this._set;
        int[] aint = this._values;
        byte[] abyte = this._states;

        this._set = new long[i];
        this._values = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                long l = along[k];
                int i1 = this.insertKey(l);

                this._values[i1] = aint[k];
            }
        }

    }

    @Override
    public int put(long i, int j) {
        int k = this.insertKey(i);

        return this.doPut(i, j, k);
    }

    @Override
    public int putIfAbsent(long i, int j) {
        int k = this.insertKey(i);

        return k < 0 ? this._values[-k - 1] : this.doPut(i, j, k);
    }

    private int doPut(long i, int j, int k) {
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
    public void putAll(Map<? extends Long, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TLongIntMap tlongintmap) {
        this.ensureCapacity(tlongintmap.size());
        TLongIntIterator tlongintiterator = tlongintmap.iterator();

        while (tlongintiterator.hasNext()) {
            tlongintiterator.advance();
            this.put(tlongintiterator.key(), tlongintiterator.value());
        }

    }

    @Override
    public int get(long i) {
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
    public int remove(long i) {
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
    public TLongSet keySet() {
        return new TLongIntHashMap.TKeyView();
    }

    @Override
    public long[] keys() {
        long[] along = new long[this.size()];
        long[] along1 = this._set;
        byte[] abyte = this._states;
        int i = along1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[j++] = along1[i];
            }
        }

        return along;
    }

    @Override
    public long[] keys(long[] along) {
        int i = this.size();

        if (along.length < i) {
            along = new long[i];
        }

        long[] along1 = this._set;
        byte[] abyte = this._states;
        int j = along1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                along[k++] = along1[j];
            }
        }

        return along;
    }

    @Override
    public TIntCollection valueCollection() {
        return new TLongIntHashMap.TValueView();
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
    public boolean containsKey(long i) {
        return this.contains(i);
    }

    @Override
    public TLongIntIterator iterator() {
        return new TLongIntHashMap.TLongIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.forEach(tlongprocedure);
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
    public boolean forEachEntry(TLongIntProcedure tlongintprocedure) {
        byte[] abyte = this._states;
        long[] along = this._set;
        int[] aint = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongintprocedure.execute(along[i], aint[i]));

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
    public boolean retainEntries(TLongIntProcedure tlongintprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        long[] along = this._set;
        int[] aint = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = along.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tlongintprocedure.execute(along[i], aint[i])) {
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
    public boolean increment(long i) {
        return this.adjustValue(i, 1);
    }

    @Override
    public boolean adjustValue(long i, int j) {
        int k = this.index(i);

        if (k < 0) {
            return false;
        } else {
            this._values[k] += j;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(long i, int j, int k) {
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
        if (!(object instanceof TLongIntMap)) {
            return false;
        } else {
            TLongIntMap tlongintmap = (TLongIntMap) object;

            if (tlongintmap.size() != this.size()) {
                return false;
            } else {
                int[] aint = this._values;
                byte[] abyte = this._states;
                int i = this.getNoEntryValue();
                int j = tlongintmap.getNoEntryValue();
                int k = aint.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        long l = this._set[k];
                        int i1 = tlongintmap.get(l);
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

        this.forEachEntry(new TLongIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(long i, int j) {
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
                objectoutput.writeLong(this._set[i]);
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
            long j = objectinput.readLong();
            int k = objectinput.readInt();

            this.put(j, k);
        }

    }

    class TLongIntHashIterator extends THashPrimitiveIterator implements TLongIntIterator {

        TLongIntHashIterator(TLongIntHashMap tlonginthashmap) {
            super(tlonginthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
            return TLongIntHashMap.this._set[this._index];
        }

        @Override
        public int value() {
            return TLongIntHashMap.this._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            TLongIntHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TLongIntValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TLongIntHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongIntKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TLongIntKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TLongIntHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongIntHashMap.this.removeAt(this._index);
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
            return TLongIntHashMap.this.new TLongIntValueHashIterator(TLongIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TLongIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TLongIntHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TLongIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TLongIntHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TLongIntHashMap.this._values;
            long[] along = TLongIntHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (along[j] == 0L || along[j] == 2L || i != aint[j]);

            TLongIntHashMap.this.removeAt(j);
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
            } while (TLongIntHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TLongIntHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TLongIntHashMap.this.containsValue(k)) {
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
            int[] aint1 = TLongIntHashMap.this._values;
            byte[] abyte = TLongIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TLongIntHashMap.this.removeAt(i);
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
            TLongIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TLongIntHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongIntHashMap.this.forEachValue(new TIntProcedure() {
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

    protected class TKeyView implements TLongSet {

        protected TKeyView() {}

        @Override
        public TLongIterator iterator() {
            return TLongIntHashMap.this.new TLongIntKeyHashIterator(TLongIntHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongIntHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TLongIntHashMap.this.contains(i);
        }

        @Override
        public long[] toArray() {
            return TLongIntHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongIntHashMap.this.keys(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            return TLongIntHashMap.this.no_entry_value != TLongIntHashMap.this.remove(i);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            long i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Long)) {
                    return false;
                }

                i = (Long) object;
            } while (TLongIntHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TLongIntHashMap.this.containsKey(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongIntHashMap.this.contains(k)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Long> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TLongCollection tlongcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long[] along) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TLongIterator tlongiterator = this.iterator();

            while (tlongiterator.hasNext()) {
                if (!collection.contains(tlongiterator.next())) {
                    tlongiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TLongCollection tlongcollection) {
            if (this == tlongcollection) {
                return false;
            } else {
                boolean flag = false;
                TLongIterator tlongiterator = this.iterator();

                while (tlongiterator.hasNext()) {
                    if (!tlongcollection.contains(tlongiterator.next())) {
                        tlongiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(long[] along) {
            boolean flag = false;

            Arrays.sort(along);
            long[] along1 = TLongIntHashMap.this._set;
            byte[] abyte = TLongIntHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongIntHashMap.this.removeAt(i);
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

                if (object instanceof Long) {
                    long i = (Long) object;

                    if (this.remove(i)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TLongCollection tlongcollection) {
            if (this == tlongcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TLongIterator tlongiterator = tlongcollection.iterator();

                while (tlongiterator.hasNext()) {
                    long i = tlongiterator.next();

                    if (this.remove(i)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(long[] along) {
            boolean flag = false;
            int i = along.length;

            while (i-- > 0) {
                if (this.remove(along[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TLongIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongIntHashMap.this.forEachKey(tlongprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TLongSet)) {
                return false;
            } else {
                TLongSet tlongset = (TLongSet) object;

                if (tlongset.size() != this.size()) {
                    return false;
                } else {
                    int i = TLongIntHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TLongIntHashMap.this._states[i] != 1 || tlongset.contains(TLongIntHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TLongIntHashMap.this._states.length;

            while (j-- > 0) {
                if (TLongIntHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TLongIntHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongIntHashMap.this.forEachKey(new TLongProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(long i) {
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
