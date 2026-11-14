package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
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

public class TByteLongHashMap extends TByteLongHash implements TByteLongMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TByteLongHashMap() {}

    public TByteLongHashMap(int i) {
        super(i);
    }

    public TByteLongHashMap(int i, float f) {
        super(i, f);
    }

    public TByteLongHashMap(int i, float f, byte b0, long j) {
        super(i, f, b0, j);
    }

    public TByteLongHashMap(byte[] abyte, long[] along) {
        super(Math.max(abyte.length, along.length));
        int i = Math.min(abyte.length, along.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], along[j]);
        }

    }

    public TByteLongHashMap(TByteLongMap tbytelongmap) {
        super(tbytelongmap.size());
        if (tbytelongmap instanceof TByteLongHashMap) {
            TByteLongHashMap tbytelonghashmap = (TByteLongHashMap) tbytelongmap;

            this._loadFactor = tbytelonghashmap._loadFactor;
            this.no_entry_key = tbytelonghashmap.no_entry_key;
            this.no_entry_value = tbytelonghashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbytelongmap);
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
        byte[] abyte = this._set;
        long[] along = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = along[k];
            }
        }

    }

    @Override
    public long put(byte b0, long i) {
        int j = this.insertKey(b0);

        return this.doPut(b0, i, j);
    }

    @Override
    public long putIfAbsent(byte b0, long i) {
        int j = this.insertKey(b0);

        return j < 0 ? this._values[-j - 1] : this.doPut(b0, i, j);
    }

    private long doPut(byte b0, long i, int j) {
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
    public void putAll(Map<? extends Byte, ? extends Long> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteLongMap tbytelongmap) {
        this.ensureCapacity(tbytelongmap.size());
        TByteLongIterator tbytelongiterator = tbytelongmap.iterator();

        while (tbytelongiterator.hasNext()) {
            tbytelongiterator.advance();
            this.put(tbytelongiterator.key(), tbytelongiterator.value());
        }

    }

    @Override
    public long get(byte b0) {
        int i = this.index(b0);

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
    public long remove(byte b0) {
        long i = this.no_entry_value;
        int j = this.index(b0);

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
    public TByteSet keySet() {
        return new TByteLongHashMap.TKeyView();
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
        int i = this.size();

        if (abyte.length < i) {
            abyte = new byte[i];
        }

        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int j = abyte1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte2[j] == 1) {
                abyte[k++] = abyte1[j];
            }
        }

        return abyte;
    }

    @Override
    public TLongCollection valueCollection() {
        return new TByteLongHashMap.TValueView();
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
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteLongIterator iterator() {
        return new TByteLongHashMap.TByteLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
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
    public boolean forEachEntry(TByteLongProcedure tbytelongprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        long[] along = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbytelongprocedure.execute(abyte1[i], along[i]));

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
    public boolean retainEntries(TByteLongProcedure tbytelongprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        long[] along = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbytelongprocedure.execute(abyte1[i], along[i])) {
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
    public boolean increment(byte b0) {
        return this.adjustValue(b0, 1L);
    }

    @Override
    public boolean adjustValue(byte b0, long i) {
        int j = this.index(b0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(byte b0, long i, long j) {
        int k = this.insertKey(b0);
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

        byte b1 = this._states[k];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return l;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteLongMap)) {
            return false;
        } else {
            TByteLongMap tbytelongmap = (TByteLongMap) object;

            if (tbytelongmap.size() != this.size()) {
                return false;
            } else {
                long[] along = this._values;
                byte[] abyte = this._states;
                long i = this.getNoEntryValue();
                long j = tbytelongmap.getNoEntryValue();
                int k = along.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        byte b0 = this._set[k];
                        long l = tbytelongmap.get(b0);
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

        this.forEachEntry(new TByteLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, long i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
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
                objectoutput.writeByte(this._set[i]);
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
            byte b0 = objectinput.readByte();
            long j = objectinput.readLong();

            this.put(b0, j);
        }

    }

    class TByteLongHashIterator extends THashPrimitiveIterator implements TByteLongIterator {

        TByteLongHashIterator(TByteLongHashMap tbytelonghashmap) {
            super(tbytelonghashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteLongHashMap.this._set[this._index];
        }

        @Override
        public long value() {
            return TByteLongHashMap.this._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            TByteLongHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TByteLongValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TByteLongHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteLongKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteLongKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteLongHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteLongHashMap.this.removeAt(this._index);
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
            return TByteLongHashMap.this.new TByteLongValueHashIterator(TByteLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TByteLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TByteLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TByteLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TByteLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TByteLongHashMap.this._values;
            byte[] abyte = TByteLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (abyte[j] == 0 || abyte[j] == 2 || i != along[j]);

            TByteLongHashMap.this.removeAt(j);
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
            } while (TByteLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TByteLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TByteLongHashMap.this.containsValue(k)) {
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
            long[] along1 = TByteLongHashMap.this._values;
            byte[] abyte = TByteLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TByteLongHashMap.this.removeAt(i);
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
            TByteLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TByteLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteLongHashMap.this.forEachValue(new TLongProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteLongHashMap.this.new TByteLongKeyHashIterator(TByteLongHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteLongHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteLongHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteLongHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteLongHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteLongHashMap.this.no_entry_value != TByteLongHashMap.this.remove(b0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            byte b0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Byte)) {
                    return false;
                }

                b0 = (Byte) object;
            } while (TByteLongHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteLongHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteLongHashMap.this.contains(b0)) {
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
            byte[] abyte1 = TByteLongHashMap.this._set;
            byte[] abyte2 = TByteLongHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteLongHashMap.this.removeAt(i);
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
            if (this == tbytecollection) {
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
            TByteLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteLongHashMap.this.forEachKey(tbyteprocedure);
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
                    int i = TByteLongHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteLongHashMap.this._states[i] != 1 || tbyteset.contains(TByteLongHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteLongHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteLongHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteLongHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteLongHashMap.this.forEachKey(new TByteProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(byte b0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(b0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
