package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongCharHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
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

public class TLongCharHashMap extends TLongCharHash implements TLongCharMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TLongCharHashMap() {}

    public TLongCharHashMap(int i) {
        super(i);
    }

    public TLongCharHashMap(int i, float f) {
        super(i, f);
    }

    public TLongCharHashMap(int i, float f, long j, char c0) {
        super(i, f, j, c0);
    }

    public TLongCharHashMap(long[] along, char[] achar) {
        super(Math.max(along.length, achar.length));
        int i = Math.min(along.length, achar.length);

        for (int j = 0; j < i; ++j) {
            this.put(along[j], achar[j]);
        }

    }

    public TLongCharHashMap(TLongCharMap tlongcharmap) {
        super(tlongcharmap.size());
        if (tlongcharmap instanceof TLongCharHashMap) {
            TLongCharHashMap tlongcharhashmap = (TLongCharHashMap) tlongcharmap;

            this._loadFactor = tlongcharhashmap._loadFactor;
            this.no_entry_key = tlongcharhashmap.no_entry_key;
            this.no_entry_value = tlongcharhashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tlongcharmap);
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
        long[] along = this._set;
        char[] achar = this._values;
        byte[] abyte = this._states;

        this._set = new long[i];
        this._values = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                long l = along[k];
                int i1 = this.insertKey(l);

                this._values[i1] = achar[k];
            }
        }

    }

    @Override
    public char put(long i, char c0) {
        int j = this.insertKey(i);

        return this.doPut(i, c0, j);
    }

    @Override
    public char putIfAbsent(long i, char c0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, c0, j);
    }

    private char doPut(long i, char c0, int j) {
        char c1 = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            c1 = this._values[j];
            flag = false;
        }

        this._values[j] = c0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c1;
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Character> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Long) entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TLongCharMap tlongcharmap) {
        this.ensureCapacity(tlongcharmap.size());
        TLongCharIterator tlongchariterator = tlongcharmap.iterator();

        while (tlongchariterator.hasNext()) {
            tlongchariterator.advance();
            this.put(tlongchariterator.key(), tlongchariterator.value());
        }

    }

    @Override
    public char get(long i) {
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
    public char remove(long i) {
        char c0 = this.no_entry_value;
        int j = this.index(i);

        if (j >= 0) {
            c0 = this._values[j];
            this.removeAt(j);
        }

        return c0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TLongSet keySet() {
        return new TLongCharHashMap.TKeyView();
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
    public TCharCollection valueCollection() {
        return new TLongCharHashMap.TValueView();
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
    public boolean containsKey(long i) {
        return this.contains(i);
    }

    @Override
    public TLongCharIterator iterator() {
        return new TLongCharHashMap.TLongCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure tlongprocedure) {
        return this.forEach(tlongprocedure);
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
    public boolean forEachEntry(TLongCharProcedure tlongcharprocedure) {
        byte[] abyte = this._states;
        long[] along = this._set;
        char[] achar = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongcharprocedure.execute(along[i], achar[i]));

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
    public boolean retainEntries(TLongCharProcedure tlongcharprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        long[] along = this._set;
        char[] achar = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = along.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tlongcharprocedure.execute(along[i], achar[i])) {
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
        return this.adjustValue(i, '\u0001');
    }

    @Override
    public boolean adjustValue(long i, char c0) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += c0;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(long i, char c0, char c1) {
        int j = this.insertKey(i);
        char c2;
        boolean flag;

        if (j < 0) {
            j = -j - 1;
            c2 = this._values[j] += c0;
            flag = false;
        } else {
            c2 = this._values[j] = c1;
            flag = true;
        }

        byte b0 = this._states[j];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TLongCharMap)) {
            return false;
        } else {
            TLongCharMap tlongcharmap = (TLongCharMap) object;

            if (tlongcharmap.size() != this.size()) {
                return false;
            } else {
                char[] achar = this._values;
                byte[] abyte = this._states;
                char c0 = this.getNoEntryValue();
                char c1 = tlongcharmap.getNoEntryValue();
                int i = achar.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        long j = this._set[i];
                        char c2 = tlongcharmap.get(j);
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

        this.forEachEntry(new TLongCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(long i, char c0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
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
                objectoutput.writeLong(this._set[i]);
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
            long j = objectinput.readLong();
            char c0 = objectinput.readChar();

            this.put(j, c0);
        }

    }

    class TLongCharHashIterator extends THashPrimitiveIterator implements TLongCharIterator {

        TLongCharHashIterator(TLongCharHashMap tlongcharhashmap) {
            super(tlongcharhashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
            return TLongCharHashMap.this._set[this._index];
        }

        @Override
        public char value() {
            return TLongCharHashMap.this._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            TLongCharHashMap.this._values[this._index] = c0;
            return c1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TLongCharValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TLongCharHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TLongCharKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TLongCharKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TLongCharHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TLongCharHashMap.this.removeAt(this._index);
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
            return TLongCharHashMap.this.new TLongCharValueHashIterator(TLongCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TLongCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TLongCharHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TLongCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TLongCharHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TLongCharHashMap.this._values;
            long[] along = TLongCharHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (along[i] == 0L || along[i] == 2L || c0 != achar[i]);

            TLongCharHashMap.this.removeAt(i);
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
            } while (TLongCharHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TLongCharHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TLongCharHashMap.this.containsValue(c0)) {
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
            char[] achar1 = TLongCharHashMap.this._values;
            byte[] abyte = TLongCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TLongCharHashMap.this.removeAt(i);
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
            TLongCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TLongCharHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongCharHashMap.this.forEachValue(new TCharProcedure() {
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

    protected class TKeyView implements TLongSet {

        protected TKeyView() {}

        @Override
        public TLongIterator iterator() {
            return TLongCharHashMap.this.new TLongCharKeyHashIterator(TLongCharHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongCharHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TLongCharHashMap.this.contains(i);
        }

        @Override
        public long[] toArray() {
            return TLongCharHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] along) {
            return TLongCharHashMap.this.keys(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            return TLongCharHashMap.this.no_entry_value != TLongCharHashMap.this.remove(i);
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
            } while (TLongCharHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TLongCharHashMap.this.containsKey(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TLongCharHashMap.this.contains(k)) {
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
            long[] along1 = TLongCharHashMap.this._set;
            byte[] abyte = TLongCharHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TLongCharHashMap.this.removeAt(i);
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
            TLongCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TLongCharHashMap.this.forEachKey(tlongprocedure);
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
                    int i = TLongCharHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TLongCharHashMap.this._states[i] != 1 || tlongset.contains(TLongCharHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TLongCharHashMap.this._states.length;

            while (j-- > 0) {
                if (TLongCharHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TLongCharHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TLongCharHashMap.this.forEachKey(new TLongProcedure() {
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
