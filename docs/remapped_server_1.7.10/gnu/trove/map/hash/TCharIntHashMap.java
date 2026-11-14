package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharIntHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
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

public class TCharIntHashMap extends TCharIntHash implements TCharIntMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TCharIntHashMap() {}

    public TCharIntHashMap(int i) {
        super(i);
    }

    public TCharIntHashMap(int i, float f) {
        super(i, f);
    }

    public TCharIntHashMap(int i, float f, char c0, int j) {
        super(i, f, c0, j);
    }

    public TCharIntHashMap(char[] achar, int[] aint) {
        super(Math.max(achar.length, aint.length));
        int i = Math.min(achar.length, aint.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], aint[j]);
        }

    }

    public TCharIntHashMap(TCharIntMap tcharintmap) {
        super(tcharintmap.size());
        if (tcharintmap instanceof TCharIntHashMap) {
            TCharIntHashMap tcharinthashmap = (TCharIntHashMap) tcharintmap;

            this._loadFactor = tcharinthashmap._loadFactor;
            this.no_entry_key = tcharinthashmap.no_entry_key;
            this.no_entry_value = tcharinthashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tcharintmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new int[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        char[] achar = this._set;
        int[] aint = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = aint[k];
            }
        }

    }

    @Override
    public int put(char c0, int i) {
        int j = this.insertKey(c0);

        return this.doPut(c0, i, j);
    }

    @Override
    public int putIfAbsent(char c0, int i) {
        int j = this.insertKey(c0);

        return j < 0 ? this._values[-j - 1] : this.doPut(c0, i, j);
    }

    private int doPut(char c0, int i, int j) {
        int k = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            k = this._values[j];
            flag = false;
        }

        this._values[j] = i;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return k;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharIntMap tcharintmap) {
        this.ensureCapacity(tcharintmap.size());
        TCharIntIterator tcharintiterator = tcharintmap.iterator();

        while (tcharintiterator.hasNext()) {
            tcharintiterator.advance();
            this.put(tcharintiterator.key(), tcharintiterator.value());
        }

    }

    @Override
    public int get(char c0) {
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
    public int remove(char c0) {
        int i = this.no_entry_value;
        int j = this.index(c0);

        if (j >= 0) {
            i = this._values[j];
            this.removeAt(j);
        }

        return i;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TCharSet keySet() {
        return new TCharIntHashMap.TKeyView();
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
    public TIntCollection valueCollection() {
        return new TCharIntHashMap.TValueView();
    }

    @Override
    public int[] values() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._values;
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
    public int[] values(int[] aint) {
        int i = this.size();

        if (aint.length < i) {
            aint = new int[i];
        }

        int[] aint1 = this._values;
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
    public boolean containsValue(int i) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int j = aint.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (abyte[j] != 1 || i != aint[j]);

        return true;
    }

    @Override
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharIntIterator iterator() {
        return new TCharIntHashMap.TCharIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure tintprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintprocedure.execute(aint[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TCharIntProcedure tcharintprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        int[] aint = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharintprocedure.execute(achar[i], aint[i]));

        return false;
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        byte[] abyte = this._states;
        int[] aint = this._values;
        int i = aint.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                aint[i] = tintfunction.execute(aint[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TCharIntProcedure tcharintprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        int[] aint = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharintprocedure.execute(achar[i], aint[i])) {
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
        return this.adjustValue(c0, 1);
    }

    @Override
    public boolean adjustValue(char c0, int i) {
        int j = this.index(c0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(char c0, int i, int j) {
        int k = this.insertKey(c0);
        int l;
        boolean flag;

        if (k < 0) {
            k = -k - 1;
            l = this._values[k] += i;
            flag = false;
        } else {
            l = this._values[k] = j;
            flag = true;
        }

        byte b0 = this._states[k];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return l;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TCharIntMap)) {
            return false;
        } else {
            TCharIntMap tcharintmap = (TCharIntMap) object;

            if (tcharintmap.size() != this.size()) {
                return false;
            } else {
                int[] aint = this._values;
                byte[] abyte = this._states;
                int i = this.getNoEntryValue();
                int j = tcharintmap.getNoEntryValue();
                int k = aint.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        char c0 = this._set[k];
                        int l = tcharintmap.get(c0);
                        int i1 = aint[k];

                        if (i1 != l && i1 != i && l != j) {
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

        this.forEachEntry(new TCharIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, int i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
                stringbuilder.append("=");
                stringbuilder.append(i);
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
                objectoutput.writeInt(this._values[i]);
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
            int j = objectinput.readInt();

            this.put(c0, j);
        }

    }

    class TCharIntHashIterator extends THashPrimitiveIterator implements TCharIntIterator {

        TCharIntHashIterator(TCharIntHashMap tcharinthashmap) {
            super(tcharinthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharIntHashMap.this._set[this._index];
        }

        @Override
        public int value() {
            return TCharIntHashMap.this._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            TCharIntHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TCharIntValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TCharIntHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharIntKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharIntKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharIntHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TIntCollection {

        protected TValueView() {}

        @Override
        public TIntIterator iterator() {
            return TCharIntHashMap.this.new TCharIntValueHashIterator(TCharIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TCharIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TCharIntHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TCharIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TCharIntHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TCharIntHashMap.this._values;
            char[] achar = TCharIntHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (achar[j] == 0 || achar[j] == 2 || i != aint[j]);

            TCharIntHashMap.this.removeAt(j);
            return true;
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
            } while (TCharIntHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TCharIntHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TCharIntHashMap.this.containsValue(k)) {
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
            int[] aint1 = TCharIntHashMap.this._values;
            byte[] abyte = TCharIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TCharIntHashMap.this.removeAt(i);
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
            TCharIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TCharIntHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharIntHashMap.this.forEachValue(new TIntProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharIntHashMap.this.new TCharIntKeyHashIterator(TCharIntHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharIntHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharIntHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharIntHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharIntHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharIntHashMap.this.no_entry_value != TCharIntHashMap.this.remove(c0);
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
            } while (TCharIntHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharIntHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharIntHashMap.this.contains(c0)) {
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
            char[] achar1 = TCharIntHashMap.this._set;
            byte[] abyte = TCharIntHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharIntHashMap.this.removeAt(i);
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
            TCharIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharIntHashMap.this.forEachKey(tcharprocedure);
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
                    int i = TCharIntHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharIntHashMap.this._states[i] != 1 || tcharset.contains(TCharIntHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharIntHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharIntHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharIntHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharIntHashMap.this.forEachKey(new TCharProcedure() {
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
