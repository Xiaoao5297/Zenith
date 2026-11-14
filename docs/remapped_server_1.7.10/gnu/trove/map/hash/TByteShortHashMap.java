package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TByteShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TByteShortHashMap extends TByteShortHash implements TByteShortMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TByteShortHashMap() {}

    public TByteShortHashMap(int i) {
        super(i);
    }

    public TByteShortHashMap(int i, float f) {
        super(i, f);
    }

    public TByteShortHashMap(int i, float f, byte b0, short short0) {
        super(i, f, b0, short0);
    }

    public TByteShortHashMap(byte[] abyte, short[] ashort) {
        super(Math.max(abyte.length, ashort.length));
        int i = Math.min(abyte.length, ashort.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], ashort[j]);
        }

    }

    public TByteShortHashMap(TByteShortMap tbyteshortmap) {
        super(tbyteshortmap.size());
        if (tbyteshortmap instanceof TByteShortHashMap) {
            TByteShortHashMap tbyteshorthashmap = (TByteShortHashMap) tbyteshortmap;

            this._loadFactor = tbyteshorthashmap._loadFactor;
            this.no_entry_key = tbyteshorthashmap.no_entry_key;
            this.no_entry_value = tbyteshorthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbyteshortmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new short[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        byte[] abyte = this._set;
        short[] ashort = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = ashort[k];
            }
        }

    }

    @Override
    public short put(byte b0, short short0) {
        int i = this.insertKey(b0);

        return this.doPut(b0, short0, i);
    }

    @Override
    public short putIfAbsent(byte b0, short short0) {
        int i = this.insertKey(b0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, short0, i);
    }

    private short doPut(byte b0, short short0, int i) {
        short short1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            short1 = this._values[i];
            flag = false;
        }

        this._values[i] = short0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short1;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Short> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteShortMap tbyteshortmap) {
        this.ensureCapacity(tbyteshortmap.size());
        TByteShortIterator tbyteshortiterator = tbyteshortmap.iterator();

        while (tbyteshortiterator.hasNext()) {
            tbyteshortiterator.advance();
            this.put(tbyteshortiterator.key(), tbyteshortiterator.value());
        }

    }

    @Override
    public short get(byte b0) {
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
    public short remove(byte b0) {
        short short0 = this.no_entry_value;
        int i = this.index(b0);

        if (i >= 0) {
            short0 = this._values[i];
            this.removeAt(i);
        }

        return short0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TByteSet keySet() {
        return new TByteShortHashMap.TKeyView();
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
    public TShortCollection valueCollection() {
        return new TByteShortHashMap.TValueView();
    }

    @Override
    public short[] values() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._values;
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
    public short[] values(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._values;
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
    public boolean containsValue(short short0) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || short0 != ashort[i]);

        return true;
    }

    @Override
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteShortIterator iterator() {
        return new TByteShortHashMap.TByteShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortprocedure.execute(ashort[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TByteShortProcedure tbyteshortprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        short[] ashort = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbyteshortprocedure.execute(abyte1[i], ashort[i]));

        return false;
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[i] = tshortfunction.execute(ashort[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TByteShortProcedure tbyteshortprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        short[] ashort = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbyteshortprocedure.execute(abyte1[i], ashort[i])) {
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
        return this.adjustValue(b0, (short) 1);
    }

    @Override
    public boolean adjustValue(byte b0, short short0) {
        int i = this.index(b0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += short0;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(byte b0, short short0, short short1) {
        int i = this.insertKey(b0);
        short short2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            short2 = this._values[i] += short0;
            flag = false;
        } else {
            short2 = this._values[i] = short1;
            flag = true;
        }

        byte b1 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteShortMap)) {
            return false;
        } else {
            TByteShortMap tbyteshortmap = (TByteShortMap) object;

            if (tbyteshortmap.size() != this.size()) {
                return false;
            } else {
                short[] ashort = this._values;
                byte[] abyte = this._states;
                short short0 = this.getNoEntryValue();
                short short1 = tbyteshortmap.getNoEntryValue();
                int i = ashort.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        byte b0 = this._set[i];
                        short short2 = tbyteshortmap.get(b0);
                        short short3 = ashort[i];

                        if (short3 != short2 && short3 != short0 && short2 != short1) {
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

        this.forEachEntry(new TByteShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, short short0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
                stringbuilder.append("=");
                stringbuilder.append(short0);
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
                objectoutput.writeShort(this._values[i]);
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
            short short0 = objectinput.readShort();

            this.put(b0, short0);
        }

    }

    class TByteShortHashIterator extends THashPrimitiveIterator implements TByteShortIterator {

        TByteShortHashIterator(TByteShortHashMap tbyteshorthashmap) {
            super(tbyteshorthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteShortHashMap.this._set[this._index];
        }

        @Override
        public short value() {
            return TByteShortHashMap.this._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            TByteShortHashMap.this._values[this._index] = short0;
            return short1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TByteShortValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TByteShortHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteShortKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteShortKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteShortHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TShortCollection {

        protected TValueView() {}

        @Override
        public TShortIterator iterator() {
            return TByteShortHashMap.this.new TByteShortValueHashIterator(TByteShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TByteShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TByteShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TByteShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TByteShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TByteShortHashMap.this._values;
            byte[] abyte = TByteShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (abyte[i] == 0 || abyte[i] == 2 || short0 != ashort[i]);

            TByteShortHashMap.this.removeAt(i);
            return true;
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
            } while (TByteShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TByteShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TByteShortHashMap.this.containsValue(short0)) {
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
            short[] ashort1 = TByteShortHashMap.this._values;
            byte[] abyte = TByteShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TByteShortHashMap.this.removeAt(i);
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
            TByteShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TByteShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteShortHashMap.this.forEachValue(new TShortProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteShortHashMap.this.new TByteShortKeyHashIterator(TByteShortHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteShortHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteShortHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteShortHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteShortHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteShortHashMap.this.no_entry_value != TByteShortHashMap.this.remove(b0);
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
            } while (TByteShortHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteShortHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteShortHashMap.this.contains(b0)) {
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
            byte[] abyte1 = TByteShortHashMap.this._set;
            byte[] abyte2 = TByteShortHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteShortHashMap.this.removeAt(i);
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
            TByteShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteShortHashMap.this.forEachKey(tbyteprocedure);
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
                    int i = TByteShortHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteShortHashMap.this._states[i] != 1 || tbyteset.contains(TByteShortHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteShortHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteShortHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteShortHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteShortHashMap.this.forEachKey(new TByteProcedure() {
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
