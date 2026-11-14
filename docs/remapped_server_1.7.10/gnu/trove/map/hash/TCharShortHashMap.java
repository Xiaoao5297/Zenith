package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TCharShortHashMap extends TCharShortHash implements TCharShortMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TCharShortHashMap() {}

    public TCharShortHashMap(int i) {
        super(i);
    }

    public TCharShortHashMap(int i, float f) {
        super(i, f);
    }

    public TCharShortHashMap(int i, float f, char c0, short short0) {
        super(i, f, c0, short0);
    }

    public TCharShortHashMap(char[] achar, short[] ashort) {
        super(Math.max(achar.length, ashort.length));
        int i = Math.min(achar.length, ashort.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], ashort[j]);
        }

    }

    public TCharShortHashMap(TCharShortMap tcharshortmap) {
        super(tcharshortmap.size());
        if (tcharshortmap instanceof TCharShortHashMap) {
            TCharShortHashMap tcharshorthashmap = (TCharShortHashMap) tcharshortmap;

            this._loadFactor = tcharshorthashmap._loadFactor;
            this.no_entry_key = tcharshorthashmap.no_entry_key;
            this.no_entry_value = tcharshorthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tcharshortmap);
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
        char[] achar = this._set;
        short[] ashort = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = ashort[k];
            }
        }

    }

    @Override
    public short put(char c0, short short0) {
        int i = this.insertKey(c0);

        return this.doPut(c0, short0, i);
    }

    @Override
    public short putIfAbsent(char c0, short short0) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, short0, i);
    }

    private short doPut(char c0, short short0, int i) {
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
    public void putAll(Map<? extends Character, ? extends Short> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharShortMap tcharshortmap) {
        this.ensureCapacity(tcharshortmap.size());
        TCharShortIterator tcharshortiterator = tcharshortmap.iterator();

        while (tcharshortiterator.hasNext()) {
            tcharshortiterator.advance();
            this.put(tcharshortiterator.key(), tcharshortiterator.value());
        }

    }

    @Override
    public short get(char c0) {
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
    public short remove(char c0) {
        short short0 = this.no_entry_value;
        int i = this.index(c0);

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
    public TCharSet keySet() {
        return new TCharShortHashMap.TKeyView();
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
    public TShortCollection valueCollection() {
        return new TCharShortHashMap.TValueView();
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
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharShortIterator iterator() {
        return new TCharShortHashMap.TCharShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
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
    public boolean forEachEntry(TCharShortProcedure tcharshortprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        short[] ashort = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharshortprocedure.execute(achar[i], ashort[i]));

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
    public boolean retainEntries(TCharShortProcedure tcharshortprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        short[] ashort = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharshortprocedure.execute(achar[i], ashort[i])) {
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
        return this.adjustValue(c0, (short) 1);
    }

    @Override
    public boolean adjustValue(char c0, short short0) {
        int i = this.index(c0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += short0;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(char c0, short short0, short short1) {
        int i = this.insertKey(c0);
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

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TCharShortMap)) {
            return false;
        } else {
            TCharShortMap tcharshortmap = (TCharShortMap) object;

            if (tcharshortmap.size() != this.size()) {
                return false;
            } else {
                short[] ashort = this._values;
                byte[] abyte = this._states;
                short short0 = this.getNoEntryValue();
                short short1 = tcharshortmap.getNoEntryValue();
                int i = ashort.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        char c0 = this._set[i];
                        short short2 = tcharshortmap.get(c0);
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

        this.forEachEntry(new TCharShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, short short0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
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
                objectoutput.writeChar(this._set[i]);
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
            char c0 = objectinput.readChar();
            short short0 = objectinput.readShort();

            this.put(c0, short0);
        }

    }

    class TCharShortHashIterator extends THashPrimitiveIterator implements TCharShortIterator {

        TCharShortHashIterator(TCharShortHashMap tcharshorthashmap) {
            super(tcharshorthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharShortHashMap.this._set[this._index];
        }

        @Override
        public short value() {
            return TCharShortHashMap.this._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            TCharShortHashMap.this._values[this._index] = short0;
            return short1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TCharShortValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TCharShortHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharShortKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharShortKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharShortHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharShortHashMap.this.removeAt(this._index);
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
            return TCharShortHashMap.this.new TCharShortValueHashIterator(TCharShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TCharShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TCharShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TCharShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TCharShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TCharShortHashMap.this._values;
            char[] achar = TCharShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (achar[i] == 0 || achar[i] == 2 || short0 != ashort[i]);

            TCharShortHashMap.this.removeAt(i);
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
            } while (TCharShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TCharShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TCharShortHashMap.this.containsValue(short0)) {
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
            short[] ashort1 = TCharShortHashMap.this._values;
            byte[] abyte = TCharShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TCharShortHashMap.this.removeAt(i);
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
            TCharShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TCharShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharShortHashMap.this.forEachValue(new TShortProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharShortHashMap.this.new TCharShortKeyHashIterator(TCharShortHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharShortHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharShortHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharShortHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharShortHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharShortHashMap.this.no_entry_value != TCharShortHashMap.this.remove(c0);
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
            } while (TCharShortHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharShortHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharShortHashMap.this.contains(c0)) {
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
            char[] achar1 = TCharShortHashMap.this._set;
            byte[] abyte = TCharShortHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharShortHashMap.this.removeAt(i);
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
            TCharShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharShortHashMap.this.forEachKey(tcharprocedure);
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
                    int i = TCharShortHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharShortHashMap.this._states[i] != 1 || tcharset.contains(TCharShortHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharShortHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharShortHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharShortHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharShortHashMap.this.forEachKey(new TCharProcedure() {
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
