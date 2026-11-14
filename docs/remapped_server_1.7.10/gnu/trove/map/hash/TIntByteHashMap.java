package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntByteHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
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

public class TIntByteHashMap extends TIntByteHash implements TIntByteMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TIntByteHashMap() {}

    public TIntByteHashMap(int i) {
        super(i);
    }

    public TIntByteHashMap(int i, float f) {
        super(i, f);
    }

    public TIntByteHashMap(int i, float f, int j, byte b0) {
        super(i, f, j, b0);
    }

    public TIntByteHashMap(int[] aint, byte[] abyte) {
        super(Math.max(aint.length, abyte.length));
        int i = Math.min(aint.length, abyte.length);

        for (int j = 0; j < i; ++j) {
            this.put(aint[j], abyte[j]);
        }

    }

    public TIntByteHashMap(TIntByteMap tintbytemap) {
        super(tintbytemap.size());
        if (tintbytemap instanceof TIntByteHashMap) {
            TIntByteHashMap tintbytehashmap = (TIntByteHashMap) tintbytemap;

            this._loadFactor = tintbytehashmap._loadFactor;
            this.no_entry_key = tintbytehashmap.no_entry_key;
            this.no_entry_value = tintbytehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tintbytemap);
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
        int[] aint = this._set;
        byte[] abyte = this._values;
        byte[] abyte1 = this._states;

        this._set = new int[i];
        this._values = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = abyte[k];
            }
        }

    }

    @Override
    public byte put(int i, byte b0) {
        int j = this.insertKey(i);

        return this.doPut(i, b0, j);
    }

    @Override
    public byte putIfAbsent(int i, byte b0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, b0, j);
    }

    private byte doPut(int i, byte b0, int j) {
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
    public void putAll(Map<? extends Integer, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TIntByteMap tintbytemap) {
        this.ensureCapacity(tintbytemap.size());
        TIntByteIterator tintbyteiterator = tintbytemap.iterator();

        while (tintbyteiterator.hasNext()) {
            tintbyteiterator.advance();
            this.put(tintbyteiterator.key(), tintbyteiterator.value());
        }

    }

    @Override
    public byte get(int i) {
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
    public byte remove(int i) {
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
    public TIntSet keySet() {
        return new TIntByteHashMap.TKeyView();
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
    public TByteCollection valueCollection() {
        return new TIntByteHashMap.TValueView();
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
    public boolean containsKey(int i) {
        return this.contains(i);
    }

    @Override
    public TIntByteIterator iterator() {
        return new TIntByteHashMap.TIntByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntByteProcedure tintbyteprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        byte[] abyte1 = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintbyteprocedure.execute(aint[i], abyte1[i]));

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
    public boolean retainEntries(TIntByteProcedure tintbyteprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        byte[] abyte1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintbyteprocedure.execute(aint[i], abyte1[i])) {
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
        return this.adjustValue(i, (byte) 1);
    }

    @Override
    public boolean adjustValue(int i, byte b0) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += b0;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(int i, byte b0, byte b1) {
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
        if (!(object instanceof TIntByteMap)) {
            return false;
        } else {
            TIntByteMap tintbytemap = (TIntByteMap) object;

            if (tintbytemap.size() != this.size()) {
                return false;
            } else {
                byte[] abyte = this._values;
                byte[] abyte1 = this._states;
                byte b0 = this.getNoEntryValue();
                byte b1 = tintbytemap.getNoEntryValue();
                int i = abyte.length;

                while (i-- > 0) {
                    if (abyte1[i] == 1) {
                        int j = this._set[i];
                        byte b2 = tintbytemap.get(j);
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

        this.forEachEntry(new TIntByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int i, byte b0) {
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
                objectoutput.writeInt(this._set[i]);
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
            int j = objectinput.readInt();
            byte b0 = objectinput.readByte();

            this.put(j, b0);
        }

    }

    class TIntByteHashIterator extends THashPrimitiveIterator implements TIntByteIterator {

        TIntByteHashIterator(TIntByteHashMap tintbytehashmap) {
            super(tintbytehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
            return TIntByteHashMap.this._set[this._index];
        }

        @Override
        public byte value() {
            return TIntByteHashMap.this._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            TIntByteHashMap.this._values[this._index] = b0;
            return b1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TIntByteValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TIntByteHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntByteKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntByteKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntByteHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntByteHashMap.this.removeAt(this._index);
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
            return TIntByteHashMap.this.new TIntByteValueHashIterator(TIntByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TIntByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TIntByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TIntByteHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TIntByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TIntByteHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TIntByteHashMap.this._values;
            int[] aint = TIntByteHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aint[i] == 0 || aint[i] == 2 || b0 != abyte[i]);

            TIntByteHashMap.this.removeAt(i);
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
            } while (TIntByteHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TIntByteHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TIntByteHashMap.this.containsValue(b0)) {
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
            byte[] abyte1 = TIntByteHashMap.this._values;
            byte[] abyte2 = TIntByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TIntByteHashMap.this.removeAt(i);
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
            TIntByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TIntByteHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntByteHashMap.this.forEachValue(new TByteProcedure() {
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

    protected class TKeyView implements TIntSet {

        protected TKeyView() {}

        @Override
        public TIntIterator iterator() {
            return TIntByteHashMap.this.new TIntByteKeyHashIterator(TIntByteHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntByteHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntByteHashMap.this.contains(i);
        }

        @Override
        public int[] toArray() {
            return TIntByteHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntByteHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return TIntByteHashMap.this.no_entry_value != TIntByteHashMap.this.remove(i);
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
            } while (TIntByteHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntByteHashMap.this.containsKey(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntByteHashMap.this.contains(k)) {
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
            int[] aint1 = TIntByteHashMap.this._set;
            byte[] abyte = TIntByteHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntByteHashMap.this.removeAt(i);
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
            TIntByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntByteHashMap.this.forEachKey(tintprocedure);
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
                    int i = TIntByteHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntByteHashMap.this._states[i] != 1 || tintset.contains(TIntByteHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntByteHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntByteHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntByteHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntByteHashMap.this.forEachKey(new TIntProcedure() {
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
