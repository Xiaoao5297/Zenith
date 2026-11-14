package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TCharByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
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

public class TCharByteHashMap extends TCharByteHash implements TCharByteMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TCharByteHashMap() {}

    public TCharByteHashMap(int i) {
        super(i);
    }

    public TCharByteHashMap(int i, float f) {
        super(i, f);
    }

    public TCharByteHashMap(int i, float f, char c0, byte b0) {
        super(i, f, c0, b0);
    }

    public TCharByteHashMap(char[] achar, byte[] abyte) {
        super(Math.max(achar.length, abyte.length));
        int i = Math.min(achar.length, abyte.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], abyte[j]);
        }

    }

    public TCharByteHashMap(TCharByteMap tcharbytemap) {
        super(tcharbytemap.size());
        if (tcharbytemap instanceof TCharByteHashMap) {
            TCharByteHashMap tcharbytehashmap = (TCharByteHashMap) tcharbytemap;

            this._loadFactor = tcharbytehashmap._loadFactor;
            this.no_entry_key = tcharbytehashmap.no_entry_key;
            this.no_entry_value = tcharbytehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tcharbytemap);
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
        char[] achar = this._set;
        byte[] abyte = this._values;
        byte[] abyte1 = this._states;

        this._set = new char[i];
        this._values = new byte[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = abyte[k];
            }
        }

    }

    @Override
    public byte put(char c0, byte b0) {
        int i = this.insertKey(c0);

        return this.doPut(c0, b0, i);
    }

    @Override
    public byte putIfAbsent(char c0, byte b0) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, b0, i);
    }

    private byte doPut(char c0, byte b0, int i) {
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
    public void putAll(Map<? extends Character, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Byte) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharByteMap tcharbytemap) {
        this.ensureCapacity(tcharbytemap.size());
        TCharByteIterator tcharbyteiterator = tcharbytemap.iterator();

        while (tcharbyteiterator.hasNext()) {
            tcharbyteiterator.advance();
            this.put(tcharbyteiterator.key(), tcharbyteiterator.value());
        }

    }

    @Override
    public byte get(char c0) {
        int i = this.index(c0);

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
    public byte remove(char c0) {
        byte b0 = this.no_entry_value;
        int i = this.index(c0);

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
    public TCharSet keySet() {
        return new TCharByteHashMap.TKeyView();
    }

    @Override
    public char[] keys() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = achar1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public char[] keys(char[] achar) {
        int i = this.size();

        if (achar.length < i) {
            achar = new char[i];
        }

        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int j = achar1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                achar[k++] = achar1[j];
            }
        }

        return achar;
    }

    @Override
    public TByteCollection valueCollection() {
        return new TCharByteHashMap.TValueView();
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
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharByteIterator iterator() {
        return new TCharByteHashMap.TCharByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
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
    public boolean forEachEntry(TCharByteProcedure tcharbyteprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        byte[] abyte1 = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharbyteprocedure.execute(achar[i], abyte1[i]));

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
    public boolean retainEntries(TCharByteProcedure tcharbyteprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        byte[] abyte1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharbyteprocedure.execute(achar[i], abyte1[i])) {
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
    public boolean increment(char c0) {
        return this.adjustValue(c0, (byte) 1);
    }

    @Override
    public boolean adjustValue(char c0, byte b0) {
        int i = this.index(c0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += b0;
            return true;
        }
    }

    @Override
    public byte adjustOrPutValue(char c0, byte b0, byte b1) {
        int i = this.insertKey(c0);
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
        if (!(object instanceof TCharByteMap)) {
            return false;
        } else {
            TCharByteMap tcharbytemap = (TCharByteMap) object;

            if (tcharbytemap.size() != this.size()) {
                return false;
            } else {
                byte[] abyte = this._values;
                byte[] abyte1 = this._states;
                byte b0 = this.getNoEntryValue();
                byte b1 = tcharbytemap.getNoEntryValue();
                int i = abyte.length;

                while (i-- > 0) {
                    if (abyte1[i] == 1) {
                        char c0 = this._set[i];
                        byte b2 = tcharbytemap.get(c0);
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

        this.forEachEntry(new TCharByteProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, byte b0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
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
                objectoutput.writeChar(this._set[i]);
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
            char c0 = objectinput.readChar();
            byte b0 = objectinput.readByte();

            this.put(c0, b0);
        }

    }

    class TCharByteHashIterator extends THashPrimitiveIterator implements TCharByteIterator {

        TCharByteHashIterator(TCharByteHashMap tcharbytehashmap) {
            super(tcharbytehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharByteHashMap.this._set[this._index];
        }

        @Override
        public byte value() {
            return TCharByteHashMap.this._values[this._index];
        }

        @Override
        public byte setValue(byte b0) {
            byte b1 = this.value();

            TCharByteHashMap.this._values[this._index] = b0;
            return b1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TCharByteValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TCharByteHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharByteHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharByteKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharByteKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharByteHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharByteHashMap.this.removeAt(this._index);
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
            return TCharByteHashMap.this.new TCharByteValueHashIterator(TCharByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TCharByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TCharByteHashMap.this.containsValue(b0);
        }

        @Override
        public byte[] toArray() {
            return TCharByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TCharByteHashMap.this.values(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            byte[] abyte = TCharByteHashMap.this._values;
            char[] achar = TCharByteHashMap.this._set;
            int i = abyte.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (achar[i] == 0 || achar[i] == 2 || b0 != abyte[i]);

            TCharByteHashMap.this.removeAt(i);
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
            } while (TCharByteHashMap.this.containsValue(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TCharByteHashMap.this.containsValue(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TCharByteHashMap.this.containsValue(b0)) {
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
            byte[] abyte1 = TCharByteHashMap.this._values;
            byte[] abyte2 = TCharByteHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TCharByteHashMap.this.removeAt(i);
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
            TCharByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TCharByteHashMap.this.forEachValue(tbyteprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharByteHashMap.this.forEachValue(new TByteProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharByteHashMap.this.new TCharByteKeyHashIterator(TCharByteHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharByteHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharByteHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharByteHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharByteHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharByteHashMap.this.no_entry_value != TCharByteHashMap.this.remove(c0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            char c0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Character)) {
                    return false;
                }

                c0 = (Character) object;
            } while (TCharByteHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharByteHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharByteHashMap.this.contains(c0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Character> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TCharCollection tcharcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(char[] achar) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TCharIterator tchariterator = this.iterator();

            while (tchariterator.hasNext()) {
                if (!collection.contains(tchariterator.next())) {
                    tchariterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                return false;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = this.iterator();

                while (tchariterator.hasNext()) {
                    if (!tcharcollection.contains(tchariterator.next())) {
                        tchariterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(char[] achar) {
            boolean flag = false;

            Arrays.sort(achar);
            char[] achar1 = TCharByteHashMap.this._set;
            byte[] abyte = TCharByteHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharByteHashMap.this.removeAt(i);
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

                if (object instanceof Character) {
                    char c0 = (Character) object;

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = tcharcollection.iterator();

                while (tchariterator.hasNext()) {
                    char c0 = tchariterator.next();

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(char[] achar) {
            boolean flag = false;
            int i = achar.length;

            while (i-- > 0) {
                if (this.remove(achar[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TCharByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharByteHashMap.this.forEachKey(tcharprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TCharSet)) {
                return false;
            } else {
                TCharSet tcharset = (TCharSet) object;

                if (tcharset.size() != this.size()) {
                    return false;
                } else {
                    int i = TCharByteHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharByteHashMap.this._states[i] != 1 || tcharset.contains(TCharByteHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharByteHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharByteHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharByteHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharByteHashMap.this.forEachKey(new TCharProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(char c0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(c0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
