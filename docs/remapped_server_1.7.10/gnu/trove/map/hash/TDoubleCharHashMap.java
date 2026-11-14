package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
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

public class TDoubleCharHashMap extends TDoubleCharHash implements TDoubleCharMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TDoubleCharHashMap() {}

    public TDoubleCharHashMap(int i) {
        super(i);
    }

    public TDoubleCharHashMap(int i, float f) {
        super(i, f);
    }

    public TDoubleCharHashMap(int i, float f, double d0, char c0) {
        super(i, f, d0, c0);
    }

    public TDoubleCharHashMap(double[] adouble, char[] achar) {
        super(Math.max(adouble.length, achar.length));
        int i = Math.min(adouble.length, achar.length);

        for (int j = 0; j < i; ++j) {
            this.put(adouble[j], achar[j]);
        }

    }

    public TDoubleCharHashMap(TDoubleCharMap tdoublecharmap) {
        super(tdoublecharmap.size());
        if (tdoublecharmap instanceof TDoubleCharHashMap) {
            TDoubleCharHashMap tdoublecharhashmap = (TDoubleCharHashMap) tdoublecharmap;

            this._loadFactor = tdoublecharhashmap._loadFactor;
            this.no_entry_key = tdoublecharhashmap.no_entry_key;
            this.no_entry_value = tdoublecharhashmap.no_entry_value;
            if (this.no_entry_key != 0.0D) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tdoublecharmap);
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
        double[] adouble = this._set;
        char[] achar = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = achar[k];
            }
        }

    }

    @Override
    public char put(double d0, char c0) {
        int i = this.insertKey(d0);

        return this.doPut(d0, c0, i);
    }

    @Override
    public char putIfAbsent(double d0, char c0) {
        int i = this.insertKey(d0);

        return i < 0 ? this._values[-i - 1] : this.doPut(d0, c0, i);
    }

    private char doPut(double d0, char c0, int i) {
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
    public void putAll(Map<? extends Double, ? extends Character> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleCharMap tdoublecharmap) {
        this.ensureCapacity(tdoublecharmap.size());
        TDoubleCharIterator tdoublechariterator = tdoublecharmap.iterator();

        while (tdoublechariterator.hasNext()) {
            tdoublechariterator.advance();
            this.put(tdoublechariterator.key(), tdoublechariterator.value());
        }

    }

    @Override
    public char get(double d0) {
        int i = this.index(d0);

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
    public char remove(double d0) {
        char c0 = this.no_entry_value;
        int i = this.index(d0);

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
    public TDoubleSet keySet() {
        return new TDoubleCharHashMap.TKeyView();
    }

    @Override
    public double[] keys() {
        double[] adouble = new double[this.size()];
        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int i = adouble1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[j++] = adouble1[i];
            }
        }

        return adouble;
    }

    @Override
    public double[] keys(double[] adouble) {
        int i = this.size();

        if (adouble.length < i) {
            adouble = new double[i];
        }

        double[] adouble1 = this._set;
        byte[] abyte = this._states;
        int j = adouble1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                adouble[k++] = adouble1[j];
            }
        }

        return adouble;
    }

    @Override
    public TCharCollection valueCollection() {
        return new TDoubleCharHashMap.TValueView();
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
    public boolean containsKey(double d0) {
        return this.contains(d0);
    }

    @Override
    public TDoubleCharIterator iterator() {
        return new TDoubleCharHashMap.TDoubleCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
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
    public boolean forEachEntry(TDoubleCharProcedure tdoublecharprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        char[] achar = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoublecharprocedure.execute(adouble[i], achar[i]));

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
    public boolean retainEntries(TDoubleCharProcedure tdoublecharprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        char[] achar = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoublecharprocedure.execute(adouble[i], achar[i])) {
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
    public boolean increment(double d0) {
        return this.adjustValue(d0, '\u0001');
    }

    @Override
    public boolean adjustValue(double d0, char c0) {
        int i = this.index(d0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += c0;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(double d0, char c0, char c1) {
        int i = this.insertKey(d0);
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

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TDoubleCharMap)) {
            return false;
        } else {
            TDoubleCharMap tdoublecharmap = (TDoubleCharMap) object;

            if (tdoublecharmap.size() != this.size()) {
                return false;
            } else {
                char[] achar = this._values;
                byte[] abyte = this._states;
                char c0 = this.getNoEntryValue();
                char c1 = tdoublecharmap.getNoEntryValue();
                int i = achar.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        double d0 = this._set[i];
                        char c2 = tdoublecharmap.get(d0);
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

        this.forEachEntry(new TDoubleCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, char c0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(d0);
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
                objectoutput.writeDouble(this._set[i]);
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
            double d0 = objectinput.readDouble();
            char c0 = objectinput.readChar();

            this.put(d0, c0);
        }

    }

    class TDoubleCharHashIterator extends THashPrimitiveIterator implements TDoubleCharIterator {

        TDoubleCharHashIterator(TDoubleCharHashMap tdoublecharhashmap) {
            super(tdoublecharhashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
            return TDoubleCharHashMap.this._set[this._index];
        }

        @Override
        public char value() {
            return TDoubleCharHashMap.this._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            TDoubleCharHashMap.this._values[this._index] = c0;
            return c1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TDoubleCharValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TDoubleCharHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleCharKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleCharKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleCharHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleCharHashMap.this.removeAt(this._index);
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
            return TDoubleCharHashMap.this.new TDoubleCharValueHashIterator(TDoubleCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TDoubleCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TDoubleCharHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TDoubleCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TDoubleCharHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TDoubleCharHashMap.this._values;
            double[] adouble = TDoubleCharHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (adouble[i] == 0.0D || adouble[i] == 2.0D || c0 != achar[i]);

            TDoubleCharHashMap.this.removeAt(i);
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
            } while (TDoubleCharHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TDoubleCharHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TDoubleCharHashMap.this.containsValue(c0)) {
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
            char[] achar1 = TDoubleCharHashMap.this._values;
            byte[] abyte = TDoubleCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TDoubleCharHashMap.this.removeAt(i);
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
            TDoubleCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TDoubleCharHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleCharHashMap.this.forEachValue(new TCharProcedure() {
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

    protected class TKeyView implements TDoubleSet {

        protected TKeyView() {}

        @Override
        public TDoubleIterator iterator() {
            return TDoubleCharHashMap.this.new TDoubleCharKeyHashIterator(TDoubleCharHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleCharHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleCharHashMap.this.contains(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleCharHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleCharHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return TDoubleCharHashMap.this.no_entry_value != TDoubleCharHashMap.this.remove(d0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            double d0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Double)) {
                    return false;
                }

                d0 = (Double) object;
            } while (TDoubleCharHashMap.this.containsKey(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleCharHashMap.this.containsKey(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleCharHashMap.this.contains(d0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Double> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TDoubleCollection tdoublecollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(double[] adouble) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TDoubleIterator tdoubleiterator = this.iterator();

            while (tdoubleiterator.hasNext()) {
                if (!collection.contains(tdoubleiterator.next())) {
                    tdoubleiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TDoubleCollection tdoublecollection) {
            if (this == tdoublecollection) {
                return false;
            } else {
                boolean flag = false;
                TDoubleIterator tdoubleiterator = this.iterator();

                while (tdoubleiterator.hasNext()) {
                    if (!tdoublecollection.contains(tdoubleiterator.next())) {
                        tdoubleiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(double[] adouble) {
            boolean flag = false;

            Arrays.sort(adouble);
            double[] adouble1 = TDoubleCharHashMap.this._set;
            byte[] abyte = TDoubleCharHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleCharHashMap.this.removeAt(i);
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

                if (object instanceof Double) {
                    double d0 = (Double) object;

                    if (this.remove(d0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TDoubleCollection tdoublecollection) {
            if (this == tdoublecollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

                while (tdoubleiterator.hasNext()) {
                    double d0 = tdoubleiterator.next();

                    if (this.remove(d0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(double[] adouble) {
            boolean flag = false;
            int i = adouble.length;

            while (i-- > 0) {
                if (this.remove(adouble[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TDoubleCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleCharHashMap.this.forEachKey(tdoubleprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TDoubleSet)) {
                return false;
            } else {
                TDoubleSet tdoubleset = (TDoubleSet) object;

                if (tdoubleset.size() != this.size()) {
                    return false;
                } else {
                    int i = TDoubleCharHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleCharHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleCharHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleCharHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleCharHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleCharHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleCharHashMap.this.forEachKey(new TDoubleProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(double d0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(d0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
