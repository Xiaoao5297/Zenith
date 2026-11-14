package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortByteHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
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

public class TShortByteHashMap extends TShortByteHash implements TShortByteMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TShortByteHashMap() {}

    public TShortByteHashMap(int i) {
        super(i);
    }

    public TShortByteHashMap(int i, float f) {
        super(i, f);
    }

    public TShortByteHashMap(int i, float f, short short0, byte b0) {
        super(i, f, short0, b0);
    }

    public TShortByteHashMap(short[] ashort, byte[] abyte) {
        super(Math.max(ashort.length, abyte.length));
        int i = Math.min(ashort.length, abyte.length);

        for (int j = 0; j < i; ++j) {
            this.put(ashort[j], abyte[j]);
        }

    }

    public TShortByteHashMap(TShortByteMap tshortbytemap) {
        super(tshortbytemap.size());
        if (tshortbytemap instanceof TShortByteHashMap) {
            TShortByteHashMap tshortbytehashmap = (TShortByteHashMap) tshortbytemap;

            this._loadFactor = tshortbytehashmap._loadFactor;
            this.no_entry_key = tshortbytehashmap.no_entry_key;
            this.no_entry_value = tshortbytehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tshortbytemap);
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
        short[] ashort = this._set;
        byte[] abyte = this._values;
        byte[] abyte1 = this._states;

        this._set = new short[i];
        this._values = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                short short0 = ashort[k];
                int l = this.insertKey(short0);

                this._values[l] = abyte[k];
            }
        }

    }

    @Override
    public byte put(short short0, byte b0) {
        int i = this.insertKey(short0);

        return this.doPut(short0, b0, i);
    }

    @Override
    public byte putIfAbsent(short short0, byte b0) {
        int i = this.insertKey(short0);

        return i < 0 ? this._values[-i - 1] : this.doPut(short0, b0, i);
    }

    private byte doPut(short short0, byte b0, int i) {
        byte b1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            b1 = this._values[i];
            flag = false;
        }

        this._values[i] = b0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b1;
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TShortByteMap tshortbytemap) {
        this.ensureCapacity(tshortbytemap.size());
        TShortByteIterator tshortbyteiterator = tshortbytemap.iterator();

        while (tshortbyteiterator.hasNext()) {
            tshortbyteiterator.advance();
            this.put(tshortbyteiterator.key(), tshortbyteiterator.value());
        }

    }

    @Override
    public byte get(short short0) {
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
    public byte remove(short short0) {
        byte b0 = this.no_entry_value;
        int i = this.index(short0);

        if (i >= 0) {
            b0 = this._values[i];
            this.removeAt(i);
        }

        return b0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TShortSet keySet() {
        return new TShortByteHashMap.TKeyView();
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
    public TByteCollection valueCollection() {
        return new TShortByteHashMap.TValueView();
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
    public boolean containsKey(short short0) {
        return this.contains(short0);
    }

    @Override
    public TShortByteIterator iterator() {
        return new TShortByteHashMap.TShortByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.forEach(tshortprocedure);
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
    public boolean forEachEntry(TShortByteProcedure tshortbyteprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._set;
        byte[] abyte1 = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortbyteprocedure.execute(ashort[i], abyte1[i]));

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
    public boolean retainEntries(TShortByteProcedure tshortbyteprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        short[] ashort = this._set;
        byte[] abyte1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = ashort.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tshortbyteprocedure.execute(ashort[i], abyte1[i])) {
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
        return this.adjustValue(short0, (byte) 1);
    }

    @Override
    public boolean adjustValue(short short0, byte b0) {
        int i = this.index(short0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += b0;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(short short0, byte b0, byte b1) {
        int i = this.insertKey(short0);
        byte b2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            b2 = this._values[i] += b0;
            flag = false;
        } else {
            b2 = this._values[i] = b1;
            flag = true;
        }

        byte b3 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return b2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TShortByteMap)) {
            return false;
        } else {
            TShortByteMap tshortbytemap = (TShortByteMap) object;

            if (tshortbytemap.size() != this.size()) {
                return false;
            } else {
                byte[] abyte = this._values;
                byte[] abyte1 = this._states;
                byte b0 = this.getNoEntryValue();
                byte b1 = tshortbytemap.getNoEntryValue();
                int i = abyte.length;

                while (i-- > 0) {
                    if (abyte1[i] == 1) {
                        short short0 = this._set[i];
                        byte b2 = tshortbytemap.get(short0);
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

        this.forEachEntry(new TShortByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short short0, byte b0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(short0);
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
                objectoutput.writeShort(this._set[i]);
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
            short short0 = objectinput.readShort();
            byte b0 = objectinput.readByte();

            this.put(short0, b0);
        }

    }

    class TShortByteHashIterator extends THashPrimitiveIterator implements TShortByteIterator {

        TShortByteHashIterator(TShortByteHashMap tshortbytehashmap) {
            super(tshortbytehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public short key() {
            return TShortByteHashMap.this._set[this._index];
        }

        @Override
        public byte value() {
            return TShortByteHashMap.this._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            TShortByteHashMap.this._values[this._index] = b0;
            return b1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TShortByteValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TShortByteHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortByteKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortByteKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortByteHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortByteHashMap.this.removeAt(this._index);
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
            return TShortByteHashMap.this.new TShortByteValueHashIterator(TShortByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TShortByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TShortByteHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TShortByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TShortByteHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TShortByteHashMap.this._values;
            short[] ashort = TShortByteHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (ashort[i] == 0 || ashort[i] == 2 || b0 != abyte[i]);

            TShortByteHashMap.this.removeAt(i);
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
            } while (TShortByteHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TShortByteHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TShortByteHashMap.this.containsValue(b0)) {
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
            byte[] abyte1 = TShortByteHashMap.this._values;
            byte[] abyte2 = TShortByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TShortByteHashMap.this.removeAt(i);
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
            TShortByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TShortByteHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortByteHashMap.this.forEachValue(new TByteProcedure() {
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

    protected class TKeyView implements TShortSet {

        protected TKeyView() {}

        @Override
        public TShortIterator iterator() {
            return TShortByteHashMap.this.new TShortByteKeyHashIterator(TShortByteHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortByteHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortByteHashMap.this.contains(short0);
        }

        @Override
        public short[] toArray() {
            return TShortByteHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortByteHashMap.this.keys(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            return TShortByteHashMap.this.no_entry_value != TShortByteHashMap.this.remove(short0);
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
            } while (TShortByteHashMap.this.containsKey(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortByteHashMap.this.containsKey(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortByteHashMap.this.contains(short0)) {
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
            short[] ashort1 = TShortByteHashMap.this._set;
            byte[] abyte = TShortByteHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortByteHashMap.this.removeAt(i);
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
            TShortByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortByteHashMap.this.forEachKey(tshortprocedure);
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
                    int i = TShortByteHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TShortByteHashMap.this._states[i] != 1 || tshortset.contains(TShortByteHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TShortByteHashMap.this._states.length;

            while (j-- > 0) {
                if (TShortByteHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TShortByteHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortByteHashMap.this.forEachKey(new TShortProcedure() {
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
