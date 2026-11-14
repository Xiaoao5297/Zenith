package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongByteHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
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

public class TLongByteHashMap extends TLongByteHash implements TLongByteMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TLongByteHashMap() {}

    public TLongByteHashMap(int i) {
        super(i);
    }

    public TLongByteHashMap(int i, float f) {
        super(i, f);
    }

    public TLongByteHashMap(int i, float f, long j, byte b0) {
        super(i, f, j, b0);
    }

    public TLongByteHashMap(long[] along, byte[] abyte) {
        super(Math.max(along.length, abyte.length));
        int i = Math.min(along.length, abyte.length);

        for (int j = 0; j < i; ++j) {
            this.put(along[j], abyte[j]);
        }

    }

    public TLongByteHashMap(TLongByteMap tlongbytemap) {
        super(tlongbytemap.size());
        if (tlongbytemap instanceof TLongByteHashMap) {
            TLongByteHashMap tlongbytehashmap = (TLongByteHashMap) tlongbytemap;

            this._loadFactor = tlongbytehashmap._loadFactor;
            this.no_entry_key = tlongbytehashmap.no_entry_key;
            this.no_entry_value = tlongbytehashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tlongbytemap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new byte[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        long[] along = this._set;
        byte[] abyte = this._values;
        byte[] abyte1 = this._states;

        this._set = new long[i];
        this._values = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                long l = along[k];
                int i1 = this.insertKey(l);

                this._values[i1] = abyte[k];
            }
        }

    }

    @Override
    public byte put(long i, byte b0) {
        int j = this.insertKey(i);

        return this.doPut(i, b0, j);
    }

    @Override
    public byte putIfAbsent(long i, byte b0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, b0, j);
    }

    private byte doPut(long i, byte b0, int j) {
        byte b1 = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            b1 = this._values[j];
            flag = false;
        }

        this._values[j] = b0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b1;
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TLongByteMap tlongbytemap) {
        this.ensureCapacity(tlongbytemap.size());
        TLongByteIterator tlongbyteiterator = tlongbytemap.iterator();

        while (tlongbyteiterator.hasNext()) {
            tlongbyteiterator.advance();
            this.put(tlongbyteiterator.key(), tlongbyteiterator.value());
        }

    }

    @Override
    public byte get(long i) {
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
    public byte remove(long i) {
        byte b0 = this.no_entry_value;
        int j = this.index(i);

        if (j >= 0) {
            b0 = this._values[j];
            this.removeAt(j);
        }

        return b0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TLongSet keySet() {
        return new TLongByteHashMap.TKeyView();
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
    public TByteCollection valueCollection() {
        return new TLongByteHashMap.TValueView();
    }

    @Override
    public byte[] values() {
        byte[] abyte = new byte[this.size()];
        byte[] abyte1 = this._values;
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
    public byte[] values(byte[] abyte) {
        int i = this.size();

        if (abyte.length < i) {
            abyte = new byte[i];
        }

        byte[] abyte1 = this._values;
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
    public boolean containsValue(byte b0) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || b0 != abyte1[i]);

        return true;
    }

    @Override
    public boolean containsKey(long i) {
        return this.contains(i);
    }

    @Override
    public TLongByteIterator iterator() {
        return new TLongByteHashMap.TLongByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.forEach(tlongprocedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure tbyteprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbyteprocedure.execute(abyte1[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TLongByteProcedure tlongbyteprocedure) {
        byte[] abyte = this._states;
        long[] along = this._set;
        byte[] abyte1 = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongbyteprocedure.execute(along[i], abyte1[i]));

        return false;
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._values;
        int i = abyte1.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                abyte1[i] = tbytefunction.execute(abyte1[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TLongByteProcedure tlongbyteprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        long[] along = this._set;
        byte[] abyte1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = along.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tlongbyteprocedure.execute(along[i], abyte1[i])) {
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
        return this.adjustValue(i, (byte) 1);
    }

    @Override
    public boolean adjustValue(long i, byte b0) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += b0;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(long i, byte b0, byte b1) {
        int j = this.insertKey(i);
        byte b2;
        boolean flag;

        if (j < 0) {
            j = -j - 1;
            b2 = this._values[j] += b0;
            flag = false;
        } else {
            b2 = this._values[j] = b1;
            flag = true;
        }

        byte b3 = this._states[j];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TLongByteMap)) {
            return false;
        } else {
            TLongByteMap tlongbytemap = (TLongByteMap) object;

            if (tlongbytemap.size() != this.size()) {
                return false;
            } else {
                byte[] abyte = this._values;
                byte[] abyte1 = this._states;
                byte b0 = this.getNoEntryValue();
                byte b1 = tlongbytemap.getNoEntryValue();
                int i = abyte.length;

                while (i-- > 0) {
                    if (abyte1[i] == 1) {
                        long j = this._set[i];
                        byte b2 = tlongbytemap.get(j);
                        byte b3 = abyte[i];

                        if (b3 != b2 && b3 != b0 && b2 != b1) {
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

        this.forEachEntry(new TLongByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(long i, byte b0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
                stringbuilder.append("=");
                stringbuilder.append(b0);
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
                objectoutput.writeByte(this._values[i]);
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
            byte b0 = objectinput.readByte();

            this.put(j, b0);
        }

    }

    class TLongByteHashIterator extends THashPrimitiveIterator implements TLongByteIterator {

        TLongByteHashIterator(TLongByteHashMap tlongbytehashmap) {
            super(tlongbytehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
            return TLongByteHashMap.this._set[this._index];
        }

        @Override
        public byte value() {
            return TLongByteHashMap.this._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            TLongByteHashMap.this._values[this._index] = b0;
            return b1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TLongByteValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TLongByteHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongByteKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TLongByteKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TLongByteHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TByteCollection {

        protected TValueView() {}

        @Override
        public TByteIterator iterator() {
            return TLongByteHashMap.this.new TLongByteValueHashIterator(TLongByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TLongByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TLongByteHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TLongByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TLongByteHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TLongByteHashMap.this._values;
            long[] along = TLongByteHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (along[i] == 0L || along[i] == 2L || b0 != abyte[i]);

            TLongByteHashMap.this.removeAt(i);
            return true;
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
            } while (TLongByteHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TLongByteHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TLongByteHashMap.this.containsValue(b0)) {
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
            byte[] abyte1 = TLongByteHashMap.this._values;
            byte[] abyte2 = TLongByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TLongByteHashMap.this.removeAt(i);
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
            TLongByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TLongByteHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongByteHashMap.this.forEachValue(new TByteProcedure() {
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

    protected class TKeyView implements TLongSet {

        protected TKeyView() {}

        @Override
        public TLongIterator iterator() {
            return TLongByteHashMap.this.new TLongByteKeyHashIterator(TLongByteHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongByteHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TLongByteHashMap.this.contains(i);
        }

        @Override
        public long[] toArray() {
            return TLongByteHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongByteHashMap.this.keys(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            return TLongByteHashMap.this.no_entry_value != TLongByteHashMap.this.remove(i);
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
            } while (TLongByteHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TLongByteHashMap.this.containsKey(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongByteHashMap.this.contains(k)) {
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
            long[] along1 = TLongByteHashMap.this._set;
            byte[] abyte = TLongByteHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongByteHashMap.this.removeAt(i);
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
            TLongByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongByteHashMap.this.forEachKey(tlongprocedure);
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
                    int i = TLongByteHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TLongByteHashMap.this._states[i] != 1 || tlongset.contains(TLongByteHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TLongByteHashMap.this._states.length;

            while (j-- > 0) {
                if (TLongByteHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TLongByteHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongByteHashMap.this.forEachKey(new TLongProcedure() {
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
