package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
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

public class TShortLongHashMap extends TShortLongHash implements TShortLongMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TShortLongHashMap() {}

    public TShortLongHashMap(int i) {
        super(i);
    }

    public TShortLongHashMap(int i, float f) {
        super(i, f);
    }

    public TShortLongHashMap(int i, float f, short short0, long j) {
        super(i, f, short0, j);
    }

    public TShortLongHashMap(short[] ashort, long[] along) {
        super(Math.max(ashort.length, along.length));
        int i = Math.min(ashort.length, along.length);

        for (int j = 0; j < i; ++j) {
            this.put(ashort[j], along[j]);
        }

    }

    public TShortLongHashMap(TShortLongMap tshortlongmap) {
        super(tshortlongmap.size());
        if (tshortlongmap instanceof TShortLongHashMap) {
            TShortLongHashMap tshortlonghashmap = (TShortLongHashMap) tshortlongmap;

            this._loadFactor = tshortlonghashmap._loadFactor;
            this.no_entry_key = tshortlonghashmap.no_entry_key;
            this.no_entry_value = tshortlonghashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tshortlongmap);
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
        short[] ashort = this._set;
        long[] along = this._values;
        byte[] abyte = this._states;

        this._set = new short[i];
        this._values = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                short short0 = ashort[k];
                int l = this.insertKey(short0);

                this._values[l] = along[k];
            }
        }

    }

    @Override
    public long put(short short0, long i) {
        int j = this.insertKey(short0);

        return this.doPut(short0, i, j);
    }

    @Override
    public long putIfAbsent(short short0, long i) {
        int j = this.insertKey(short0);

        return j < 0 ? this._values[-j - 1] : this.doPut(short0, i, j);
    }

    private long doPut(short short0, long i, int j) {
        long k = this.no_entry_value;
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
    public void putAll(Map<? extends Short, ? extends Long> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TShortLongMap tshortlongmap) {
        this.ensureCapacity(tshortlongmap.size());
        TShortLongIterator tshortlongiterator = tshortlongmap.iterator();

        while (tshortlongiterator.hasNext()) {
            tshortlongiterator.advance();
            this.put(tshortlongiterator.key(), tshortlongiterator.value());
        }

    }

    @Override
    public long get(short short0) {
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
    public long remove(short short0) {
        long i = this.no_entry_value;
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
        return new TShortLongHashMap.TKeyView();
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
    public TLongCollection valueCollection() {
        return new TShortLongHashMap.TValueView();
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
    public boolean containsKey(short short0) {
        return this.contains(short0);
    }

    @Override
    public TShortLongIterator iterator() {
        return new TShortLongHashMap.TShortLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.forEach(tshortprocedure);
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
    public boolean forEachEntry(TShortLongProcedure tshortlongprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._set;
        long[] along = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortlongprocedure.execute(ashort[i], along[i]));

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
    public boolean retainEntries(TShortLongProcedure tshortlongprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        short[] ashort = this._set;
        long[] along = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = ashort.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tshortlongprocedure.execute(ashort[i], along[i])) {
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
        return this.adjustValue(short0, 1L);
    }

    @Override
    public boolean adjustValue(short short0, long i) {
        int j = this.index(short0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(short short0, long i, long j) {
        int k = this.insertKey(short0);
        long l;
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
        if (!(object instanceof TShortLongMap)) {
            return false;
        } else {
            TShortLongMap tshortlongmap = (TShortLongMap) object;

            if (tshortlongmap.size() != this.size()) {
                return false;
            } else {
                long[] along = this._values;
                byte[] abyte = this._states;
                long i = this.getNoEntryValue();
                long j = tshortlongmap.getNoEntryValue();
                int k = along.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        short short0 = this._set[k];
                        long l = tshortlongmap.get(short0);
                        long i1 = along[k];

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

        this.forEachEntry(new TShortLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short short0, long i) {
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
            short short0 = objectinput.readShort();
            long j = objectinput.readLong();

            this.put(short0, j);
        }

    }

    class TShortLongHashIterator extends THashPrimitiveIterator implements TShortLongIterator {

        TShortLongHashIterator(TShortLongHashMap tshortlonghashmap) {
            super(tshortlonghashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public short key() {
            return TShortLongHashMap.this._set[this._index];
        }

        @Override
        public long value() {
            return TShortLongHashMap.this._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            TShortLongHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TShortLongValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TShortLongHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortLongKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortLongKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortLongHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortLongHashMap.this.removeAt(this._index);
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
            return TShortLongHashMap.this.new TShortLongValueHashIterator(TShortLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TShortLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TShortLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TShortLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TShortLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TShortLongHashMap.this._values;
            short[] ashort = TShortLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (ashort[j] == 0 || ashort[j] == 2 || i != along[j]);

            TShortLongHashMap.this.removeAt(j);
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
            } while (TShortLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TShortLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TShortLongHashMap.this.containsValue(k)) {
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
            long[] along1 = TShortLongHashMap.this._values;
            byte[] abyte = TShortLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TShortLongHashMap.this.removeAt(i);
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
            TShortLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TShortLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortLongHashMap.this.forEachValue(new TLongProcedure() {
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

    protected class TKeyView implements TShortSet {

        protected TKeyView() {}

        @Override
        public TShortIterator iterator() {
            return TShortLongHashMap.this.new TShortLongKeyHashIterator(TShortLongHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortLongHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortLongHashMap.this.contains(short0);
        }

        @Override
        public short[] toArray() {
            return TShortLongHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortLongHashMap.this.keys(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            return TShortLongHashMap.this.no_entry_value != TShortLongHashMap.this.remove(short0);
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
            } while (TShortLongHashMap.this.containsKey(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortLongHashMap.this.containsKey(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortLongHashMap.this.contains(short0)) {
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
            short[] ashort1 = TShortLongHashMap.this._set;
            byte[] abyte = TShortLongHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortLongHashMap.this.removeAt(i);
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
            TShortLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortLongHashMap.this.forEachKey(tshortprocedure);
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
                    int i = TShortLongHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TShortLongHashMap.this._states[i] != 1 || tshortset.contains(TShortLongHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TShortLongHashMap.this._states.length;

            while (j-- > 0) {
                if (TShortLongHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TShortLongHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortLongHashMap.this.forEachKey(new TShortProcedure() {
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
