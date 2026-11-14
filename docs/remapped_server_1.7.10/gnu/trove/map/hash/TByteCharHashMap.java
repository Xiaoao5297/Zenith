package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
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

public class TByteCharHashMap extends TByteCharHash implements TByteCharMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TByteCharHashMap() {}

    public TByteCharHashMap(int i) {
        super(i);
    }

    public TByteCharHashMap(int i, float f) {
        super(i, f);
    }

    public TByteCharHashMap(int i, float f, byte b0, char c0) {
        super(i, f, b0, c0);
    }

    public TByteCharHashMap(byte[] abyte, char[] achar) {
        super(Math.max(abyte.length, achar.length));
        int i = Math.min(abyte.length, achar.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], achar[j]);
        }

    }

    public TByteCharHashMap(TByteCharMap tbytecharmap) {
        super(tbytecharmap.size());
        if (tbytecharmap instanceof TByteCharHashMap) {
            TByteCharHashMap tbytecharhashmap = (TByteCharHashMap) tbytecharmap;

            this._loadFactor = tbytecharhashmap._loadFactor;
            this.no_entry_key = tbytecharhashmap.no_entry_key;
            this.no_entry_value = tbytecharhashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbytecharmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new char[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        byte[] abyte = this._set;
        char[] achar = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = achar[k];
            }
        }

    }

    @Override
    public char put(byte b0, char c0) {
        int i = this.insertKey(b0);

        return this.doPut(b0, c0, i);
    }

    @Override
    public char putIfAbsent(byte b0, char c0) {
        int i = this.insertKey(b0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, c0, i);
    }

    private char doPut(byte b0, char c0, int i) {
        char c1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            c1 = this._values[i];
            flag = false;
        }

        this._values[i] = c0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c1;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Character> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteCharMap tbytecharmap) {
        this.ensureCapacity(tbytecharmap.size());
        TByteCharIterator tbytechariterator = tbytecharmap.iterator();

        while (tbytechariterator.hasNext()) {
            tbytechariterator.advance();
            this.put(tbytechariterator.key(), tbytechariterator.value());
        }

    }

    @Override
    public char get(byte b0) {
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
    public char remove(byte b0) {
        char c0 = this.no_entry_value;
        int i = this.index(b0);

        if (i >= 0) {
            c0 = this._values[i];
            this.removeAt(i);
        }

        return c0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TByteSet keySet() {
        return new TByteCharHashMap.TKeyView();
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
    public TCharCollection valueCollection() {
        return new TByteCharHashMap.TValueView();
    }

    @Override
    public char[] values() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._values;
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
    public char[] values(char[] achar) {
        int i = this.size();

        if (achar.length < i) {
            achar = new char[i];
        }

        char[] achar1 = this._values;
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
    public boolean containsValue(char c0) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || c0 != achar[i]);

        return true;
    }

    @Override
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteCharIterator iterator() {
        return new TByteCharHashMap.TByteCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharprocedure.execute(achar[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TByteCharProcedure tbytecharprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        char[] achar = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbytecharprocedure.execute(abyte1[i], achar[i]));

        return false;
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[i] = tcharfunction.execute(achar[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TByteCharProcedure tbytecharprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        char[] achar = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbytecharprocedure.execute(abyte1[i], achar[i])) {
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
        return this.adjustValue(b0, '\u0001');
    }

    @Override
    public boolean adjustValue(byte b0, char c0) {
        int i = this.index(b0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += c0;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(byte b0, char c0, char c1) {
        int i = this.insertKey(b0);
        char c2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            c2 = this._values[i] += c0;
            flag = false;
        } else {
            c2 = this._values[i] = c1;
            flag = true;
        }

        byte b1 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteCharMap)) {
            return false;
        } else {
            TByteCharMap tbytecharmap = (TByteCharMap) object;

            if (tbytecharmap.size() != this.size()) {
                return false;
            } else {
                char[] achar = this._values;
                byte[] abyte = this._states;
                char c0 = this.getNoEntryValue();
                char c1 = tbytecharmap.getNoEntryValue();
                int i = achar.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        byte b0 = this._set[i];
                        char c2 = tbytecharmap.get(b0);
                        char c3 = achar[i];

                        if (c3 != c2 && c3 != c0 && c2 != c1) {
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

        this.forEachEntry(new TByteCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, char c0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
                stringbuilder.append("=");
                stringbuilder.append(c0);
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
                objectoutput.writeChar(this._values[i]);
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
            char c0 = objectinput.readChar();

            this.put(b0, c0);
        }

    }

    class TByteCharHashIterator extends THashPrimitiveIterator implements TByteCharIterator {

        TByteCharHashIterator(TByteCharHashMap tbytecharhashmap) {
            super(tbytecharhashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteCharHashMap.this._set[this._index];
        }

        @Override
        public char value() {
            return TByteCharHashMap.this._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            TByteCharHashMap.this._values[this._index] = c0;
            return c1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TByteCharValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TByteCharHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteCharKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteCharKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteCharHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TCharCollection {

        protected TValueView() {}

        @Override
        public TCharIterator iterator() {
            return TByteCharHashMap.this.new TByteCharValueHashIterator(TByteCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TByteCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TByteCharHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TByteCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TByteCharHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TByteCharHashMap.this._values;
            byte[] abyte = TByteCharHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (abyte[i] == 0 || abyte[i] == 2 || c0 != achar[i]);

            TByteCharHashMap.this.removeAt(i);
            return true;
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
            } while (TByteCharHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TByteCharHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TByteCharHashMap.this.containsValue(c0)) {
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
            char[] achar1 = TByteCharHashMap.this._values;
            byte[] abyte = TByteCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TByteCharHashMap.this.removeAt(i);
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
            TByteCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TByteCharHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteCharHashMap.this.forEachValue(new TCharProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteCharHashMap.this.new TByteCharKeyHashIterator(TByteCharHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteCharHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteCharHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteCharHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteCharHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteCharHashMap.this.no_entry_value != TByteCharHashMap.this.remove(b0);
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
            } while (TByteCharHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteCharHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteCharHashMap.this.contains(b0)) {
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
            byte[] abyte1 = TByteCharHashMap.this._set;
            byte[] abyte2 = TByteCharHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteCharHashMap.this.removeAt(i);
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
            TByteCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteCharHashMap.this.forEachKey(tbyteprocedure);
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
                    int i = TByteCharHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteCharHashMap.this._states[i] != 1 || tbyteset.contains(TByteCharHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteCharHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteCharHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteCharHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteCharHashMap.this.forEachKey(new TByteProcedure() {
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
