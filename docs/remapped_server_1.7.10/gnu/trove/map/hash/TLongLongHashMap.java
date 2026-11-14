package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
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

public class TLongLongHashMap extends TLongLongHash implements TLongLongMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TLongLongHashMap() {}

    public TLongLongHashMap(int i) {
        super(i);
    }

    public TLongLongHashMap(int i, float f) {
        super(i, f);
    }

    public TLongLongHashMap(int i, float f, long j, long k) {
        super(i, f, j, k);
    }

    public TLongLongHashMap(long[] along, long[] along1) {
        super(Math.max(along.length, along1.length));
        int i = Math.min(along.length, along1.length);

        for (int j = 0; j < i; ++j) {
            this.put(along[j], along1[j]);
        }

    }

    public TLongLongHashMap(TLongLongMap tlonglongmap) {
        super(tlonglongmap.size());
        if (tlonglongmap instanceof TLongLongHashMap) {
            TLongLongHashMap tlonglonghashmap = (TLongLongHashMap) tlonglongmap;

            this._loadFactor = tlonglonghashmap._loadFactor;
            this.no_entry_key = tlonglonghashmap.no_entry_key;
            this.no_entry_value = tlonglonghashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tlonglongmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new long[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        long[] along = this._set;
        long[] along1 = this._values;
        byte[] abyte = this._states;

        this._set = new long[i];
        this._values = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                long l = along[k];
                int i1 = this.insertKey(l);

                this._values[i1] = along1[k];
            }
        }

    }

    @Override
    public long put(long i, long j) {
        int k = this.insertKey(i);

        return this.doPut(i, j, k);
    }

    @Override
    public long putIfAbsent(long i, long j) {
        int k = this.insertKey(i);

        return k < 0 ? this._values[-k - 1] : this.doPut(i, j, k);
    }

    private long doPut(long i, long j, int k) {
        long l = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Long> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TLongLongMap tlonglongmap) {
        this.ensureCapacity(tlonglongmap.size());
        TLongLongIterator tlonglongiterator = tlonglongmap.iterator();

        while (tlonglongiterator.hasNext()) {
            tlonglongiterator.advance();
            this.put(tlonglongiterator.key(), tlonglongiterator.value());
        }

    }

    @Override
    public long get(long i) {
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
    public long remove(long i) {
        long j = this.no_entry_value;
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
        return new TLongLongHashMap.TKeyView();
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
    public TLongCollection valueCollection() {
        return new TLongLongHashMap.TValueView();
    }

    @Override
    public long[] values() {
        long[] along = new long[this.size()];
        long[] along1 = this._values;
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
    public long[] values(long[] along) {
        int i = this.size();

        if (along.length < i) {
            along = new long[i];
        }

        long[] along1 = this._values;
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
    public boolean containsValue(long i) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int j = along.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (abyte[j] != 1 || i != along[j]);

        return true;
    }

    @Override
    public boolean containsKey(long i) {
        return this.contains(i);
    }

    @Override
    public TLongLongIterator iterator() {
        return new TLongLongHashMap.TLongLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.forEach(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongprocedure.execute(along[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TLongLongProcedure tlonglongprocedure) {
        byte[] abyte = this._states;
        long[] along = this._set;
        long[] along1 = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlonglongprocedure.execute(along[i], along1[i]));

        return false;
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int i = along.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[i] = tlongfunction.execute(along[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TLongLongProcedure tlonglongprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        long[] along = this._set;
        long[] along1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = along.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tlonglongprocedure.execute(along[i], along1[i])) {
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
        return this.adjustValue(i, 1L);
    }

    @Override
    public boolean adjustValue(long i, long j) {
        int k = this.index(i);

        if (k < 0) {
            return false;
        } else {
            this._values[k] += j;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(long i, long j, long k) {
        int l = this.insertKey(i);
        long i1;
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
        if (!(object instanceof TLongLongMap)) {
            return false;
        } else {
            TLongLongMap tlonglongmap = (TLongLongMap) object;

            if (tlonglongmap.size() != this.size()) {
                return false;
            } else {
                long[] along = this._values;
                byte[] abyte = this._states;
                long i = this.getNoEntryValue();
                long j = tlonglongmap.getNoEntryValue();
                int k = along.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        long l = this._set[k];
                        long i1 = tlonglongmap.get(l);
                        long j1 = along[k];

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

        this.forEachEntry(new TLongLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(long i, long j) {
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
                objectoutput.writeLong(this._values[i]);
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
            long k = objectinput.readLong();

            this.put(j, k);
        }

    }

    class TLongLongHashIterator extends THashPrimitiveIterator implements TLongLongIterator {

        TLongLongHashIterator(TLongLongHashMap tlonglonghashmap) {
            super(tlonglonghashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
            return TLongLongHashMap.this._set[this._index];
        }

        @Override
        public long value() {
            return TLongLongHashMap.this._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            TLongLongHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TLongLongValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TLongLongHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongLongKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TLongLongKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TLongLongHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TLongCollection {

        protected TValueView() {}

        @Override
        public TLongIterator iterator() {
            return TLongLongHashMap.this.new TLongLongValueHashIterator(TLongLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TLongLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TLongLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TLongLongHashMap.this._values;
            long[] along1 = TLongLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (along1[j] == 0L || along1[j] == 2L || i != along[j]);

            TLongLongHashMap.this.removeAt(j);
            return true;
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
            } while (TLongLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TLongLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongLongHashMap.this.containsValue(k)) {
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
            long[] along1 = TLongLongHashMap.this._values;
            byte[] abyte = TLongLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongLongHashMap.this.removeAt(i);
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
            TLongLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongLongHashMap.this.forEachValue(new TLongProcedure() {
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

    protected class TKeyView implements TLongSet {

        protected TKeyView() {}

        @Override
        public TLongIterator iterator() {
            return TLongLongHashMap.this.new TLongLongKeyHashIterator(TLongLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TLongLongHashMap.this.contains(i);
        }

        @Override
        public long[] toArray() {
            return TLongLongHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongLongHashMap.this.keys(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            return TLongLongHashMap.this.no_entry_value != TLongLongHashMap.this.remove(i);
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
            } while (TLongLongHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TLongLongHashMap.this.containsKey(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongLongHashMap.this.contains(k)) {
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
            long[] along1 = TLongLongHashMap.this._set;
            byte[] abyte = TLongLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongLongHashMap.this.removeAt(i);
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
            TLongLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongLongHashMap.this.forEachKey(tlongprocedure);
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
                    int i = TLongLongHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TLongLongHashMap.this._states[i] != 1 || tlongset.contains(TLongLongHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TLongLongHashMap.this._states.length;

            while (j-- > 0) {
                if (TLongLongHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TLongLongHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongLongHashMap.this.forEachKey(new TLongProcedure() {
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
