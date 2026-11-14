package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
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

public class TCharCharHashMap extends TCharCharHash implements TCharCharMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TCharCharHashMap() {}

    public TCharCharHashMap(int i) {
        super(i);
    }

    public TCharCharHashMap(int i, float f) {
        super(i, f);
    }

    public TCharCharHashMap(int i, float f, char c0, char c1) {
        super(i, f, c0, c1);
    }

    public TCharCharHashMap(char[] achar, char[] achar1) {
        super(Math.max(achar.length, achar1.length));
        int i = Math.min(achar.length, achar1.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], achar1[j]);
        }

    }

    public TCharCharHashMap(TCharCharMap tcharcharmap) {
        super(tcharcharmap.size());
        if (tcharcharmap instanceof TCharCharHashMap) {
            TCharCharHashMap tcharcharhashmap = (TCharCharHashMap) tcharcharmap;

            this._loadFactor = tcharcharhashmap._loadFactor;
            this.no_entry_key = tcharcharhashmap.no_entry_key;
            this.no_entry_value = tcharcharhashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tcharcharmap);
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
        char[] achar = this._set;
        char[] achar1 = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = achar1[k];
            }
        }

    }

    @Override
    public char put(char c0, char c1) {
        int i = this.insertKey(c0);

        return this.doPut(c0, c1, i);
    }

    @Override
    public char putIfAbsent(char c0, char c1) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, c1, i);
    }

    private char doPut(char c0, char c1, int i) {
        char c2 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            c2 = this._values[i];
            flag = false;
        }

        this._values[i] = c1;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Character> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharCharMap tcharcharmap) {
        this.ensureCapacity(tcharcharmap.size());
        TCharCharIterator tcharchariterator = tcharcharmap.iterator();

        while (tcharchariterator.hasNext()) {
            tcharchariterator.advance();
            this.put(tcharchariterator.key(), tcharchariterator.value());
        }

    }

    @Override
    public char get(char c0) {
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
    public char remove(char c0) {
        char c1 = this.no_entry_value;
        int i = this.index(c0);

        if (i >= 0) {
            c1 = this._values[i];
            this.removeAt(i);
        }

        return c1;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TCharSet keySet() {
        return new TCharCharHashMap.TKeyView();
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
    public TCharCollection valueCollection() {
        return new TCharCharHashMap.TValueView();
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
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharCharIterator iterator() {
        return new TCharCharHashMap.TCharCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
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
    public boolean forEachEntry(TCharCharProcedure tcharcharprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        char[] achar1 = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharcharprocedure.execute(achar[i], achar1[i]));

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
    public boolean retainEntries(TCharCharProcedure tcharcharprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        char[] achar1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharcharprocedure.execute(achar[i], achar1[i])) {
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
        return this.adjustValue(c0, '\u0001');
    }

    @Override
    public boolean adjustValue(char c0, char c1) {
        int i = this.index(c0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += c1;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(char c0, char c1, char c2) {
        int i = this.insertKey(c0);
        char c3;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            c3 = this._values[i] += c1;
            flag = false;
        } else {
            c3 = this._values[i] = c2;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TCharCharMap)) {
            return false;
        } else {
            TCharCharMap tcharcharmap = (TCharCharMap) object;

            if (tcharcharmap.size() != this.size()) {
                return false;
            } else {
                char[] achar = this._values;
                byte[] abyte = this._states;
                char c0 = this.getNoEntryValue();
                char c1 = tcharcharmap.getNoEntryValue();
                int i = achar.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        char c2 = this._set[i];
                        char c3 = tcharcharmap.get(c2);
                        char c4 = achar[i];

                        if (c4 != c3 && c4 != c0 && c3 != c1) {
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

        this.forEachEntry(new TCharCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, char c1) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
                stringbuilder.append("=");
                stringbuilder.append(c1);
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
            char c0 = objectinput.readChar();
            char c1 = objectinput.readChar();

            this.put(c0, c1);
        }

    }

    class TCharCharHashIterator extends THashPrimitiveIterator implements TCharCharIterator {

        TCharCharHashIterator(TCharCharHashMap tcharcharhashmap) {
            super(tcharcharhashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharCharHashMap.this._set[this._index];
        }

        @Override
        public char value() {
            return TCharCharHashMap.this._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            TCharCharHashMap.this._values[this._index] = c0;
            return c1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharCharValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharCharHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharCharKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharCharKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharCharHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharCharHashMap.this.removeAt(this._index);
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
            return TCharCharHashMap.this.new TCharCharValueHashIterator(TCharCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharCharHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TCharCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharCharHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TCharCharHashMap.this._values;
            char[] achar1 = TCharCharHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (achar1[i] == 0 || achar1[i] == 2 || c0 != achar[i]);

            TCharCharHashMap.this.removeAt(i);
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
            } while (TCharCharHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharCharHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharCharHashMap.this.containsValue(c0)) {
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
            char[] achar1 = TCharCharHashMap.this._values;
            byte[] abyte = TCharCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharCharHashMap.this.removeAt(i);
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
            TCharCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharCharHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharCharHashMap.this.forEachValue(new TCharProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharCharHashMap.this.new TCharCharKeyHashIterator(TCharCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharCharHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharCharHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharCharHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharCharHashMap.this.no_entry_value != TCharCharHashMap.this.remove(c0);
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
            } while (TCharCharHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharCharHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharCharHashMap.this.contains(c0)) {
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
            char[] achar1 = TCharCharHashMap.this._set;
            byte[] abyte = TCharCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharCharHashMap.this.removeAt(i);
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
            TCharCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharCharHashMap.this.forEachKey(tcharprocedure);
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
                    int i = TCharCharHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharCharHashMap.this._states[i] != 1 || tcharset.contains(TCharCharHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharCharHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharCharHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharCharHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharCharHashMap.this.forEachKey(new TCharProcedure() {
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
