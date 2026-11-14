package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.map.TByteByteMap;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
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

public class TByteByteHashMap extends TByteByteHash implements TByteByteMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TByteByteHashMap() {}

    public TByteByteHashMap(int i) {
        super(i);
    }

    public TByteByteHashMap(int i, float f) {
        super(i, f);
    }

    public TByteByteHashMap(int i, float f, byte b0, byte b1) {
        super(i, f, b0, b1);
    }

    public TByteByteHashMap(byte[] abyte, byte[] abyte1) {
        super(Math.max(abyte.length, abyte1.length));
        int i = Math.min(abyte.length, abyte1.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], abyte1[j]);
        }

    }

    public TByteByteHashMap(TByteByteMap tbytebytemap) {
        super(tbytebytemap.size());
        if (tbytebytemap instanceof TByteByteHashMap) {
            TByteByteHashMap tbytebytehashmap = (TByteByteHashMap) tbytebytemap;

            this._loadFactor = tbytebytehashmap._loadFactor;
            this.no_entry_key = tbytebytehashmap.no_entry_key;
            this.no_entry_value = tbytebytehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbytebytemap);
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
        byte[] abyte = this._set;
        byte[] abyte1 = this._values;
        byte[] abyte2 = this._states;

        this._set = new byte[i];
        this._values = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte2[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = abyte1[k];
            }
        }

    }

    @Override
    public byte put(byte b0, byte b1) {
        int i = this.insertKey(b0);

        return this.doPut(b0, b1, i);
    }

    @Override
    public byte putIfAbsent(byte b0, byte b1) {
        int i = this.insertKey(b0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, b1, i);
    }

    private byte doPut(byte b0, byte b1, int i) {
        byte b2 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            b2 = this._values[i];
            flag = false;
        }

        this._values[i] = b1;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b2;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteByteMap tbytebytemap) {
        this.ensureCapacity(tbytebytemap.size());
        TByteByteIterator tbytebyteiterator = tbytebytemap.iterator();

        while (tbytebyteiterator.hasNext()) {
            tbytebyteiterator.advance();
            this.put(tbytebyteiterator.key(), tbytebyteiterator.value());
        }

    }

    @Override
    public byte get(byte b0) {
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
    public byte remove(byte b0) {
        byte b1 = this.no_entry_value;
        int i = this.index(b0);

        if (i >= 0) {
            b1 = this._values[i];
            this.removeAt(i);
        }

        return b1;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TByteSet keySet() {
        return new TByteByteHashMap.TKeyView();
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
    public TByteCollection valueCollection() {
        return new TByteByteHashMap.TValueView();
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
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteByteIterator iterator() {
        return new TByteByteHashMap.TByteByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
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
    public boolean forEachEntry(TByteByteProcedure tbytebyteprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbytebyteprocedure.execute(abyte1[i], abyte2[i]));

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
    public boolean retainEntries(TByteByteProcedure tbytebyteprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbytebyteprocedure.execute(abyte1[i], abyte2[i])) {
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
        return this.adjustValue(b0, (byte) 1);
    }

    @Override
    public boolean adjustValue(byte b0, byte b1) {
        int i = this.index(b0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += b1;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(byte b0, byte b1, byte b2) {
        int i = this.insertKey(b0);
        byte b3;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            b3 = this._values[i] += b1;
            flag = false;
        } else {
            b3 = this._values[i] = b2;
            flag = true;
        }

        byte b4 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteByteMap)) {
            return false;
        } else {
            TByteByteMap tbytebytemap = (TByteByteMap) object;

            if (tbytebytemap.size() != this.size()) {
                return false;
            } else {
                byte[] abyte = this._values;
                byte[] abyte1 = this._states;
                byte b0 = this.getNoEntryValue();
                byte b1 = tbytebytemap.getNoEntryValue();
                int i = abyte.length;

                while (i-- > 0) {
                    if (abyte1[i] == 1) {
                        byte b2 = this._set[i];
                        byte b3 = tbytebytemap.get(b2);
                        byte b4 = abyte[i];

                        if (b4 != b3 && b4 != b0 && b3 != b1) {
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

        this.forEachEntry(new TByteByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, byte b1) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
                stringbuilder.append("=");
                stringbuilder.append(b1);
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
            byte b0 = objectinput.readByte();
            byte b1 = objectinput.readByte();

            this.put(b0, b1);
        }

    }

    class TByteByteHashIterator extends THashPrimitiveIterator implements TByteByteIterator {

        TByteByteHashIterator(TByteByteHashMap tbytebytehashmap) {
            super(tbytebytehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteByteHashMap.this._set[this._index];
        }

        @Override
        public byte value() {
            return TByteByteHashMap.this._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            TByteByteHashMap.this._values[this._index] = b0;
            return b1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteByteValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteByteHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteByteKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteByteKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteByteHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteByteHashMap.this.removeAt(this._index);
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
            return TByteByteHashMap.this.new TByteByteValueHashIterator(TByteByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteByteHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteByteHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TByteByteHashMap.this._values;
            byte[] abyte1 = TByteByteHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (abyte1[i] == 0 || abyte1[i] == 2 || b0 != abyte[i]);

            TByteByteHashMap.this.removeAt(i);
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
            } while (TByteByteHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteByteHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteByteHashMap.this.containsValue(b0)) {
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
            byte[] abyte1 = TByteByteHashMap.this._values;
            byte[] abyte2 = TByteByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteByteHashMap.this.removeAt(i);
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
            TByteByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteByteHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteByteHashMap.this.forEachValue(new TByteProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteByteHashMap.this.new TByteByteKeyHashIterator(TByteByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteByteHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteByteHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteByteHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteByteHashMap.this.no_entry_value != TByteByteHashMap.this.remove(b0);
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
            } while (TByteByteHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteByteHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteByteHashMap.this.contains(b0)) {
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
            byte[] abyte1 = TByteByteHashMap.this._set;
            byte[] abyte2 = TByteByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteByteHashMap.this.removeAt(i);
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
            TByteByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteByteHashMap.this.forEachKey(tbyteprocedure);
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
                    int i = TByteByteHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteByteHashMap.this._states[i] != 1 || tbyteset.contains(TByteByteHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteByteHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteByteHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteByteHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteByteHashMap.this.forEachKey(new TByteProcedure() {
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
