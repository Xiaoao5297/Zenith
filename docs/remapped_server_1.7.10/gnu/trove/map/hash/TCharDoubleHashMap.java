package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
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

public class TCharDoubleHashMap extends TCharDoubleHash implements TCharDoubleMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TCharDoubleHashMap() {}

    public TCharDoubleHashMap(int i) {
        super(i);
    }

    public TCharDoubleHashMap(int i, float f) {
        super(i, f);
    }

    public TCharDoubleHashMap(int i, float f, char c0, double d0) {
        super(i, f, c0, d0);
    }

    public TCharDoubleHashMap(char[] achar, double[] adouble) {
        super(Math.max(achar.length, adouble.length));
        int i = Math.min(achar.length, adouble.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], adouble[j]);
        }

    }

    public TCharDoubleHashMap(TCharDoubleMap tchardoublemap) {
        super(tchardoublemap.size());
        if (tchardoublemap instanceof TCharDoubleHashMap) {
            TCharDoubleHashMap tchardoublehashmap = (TCharDoubleHashMap) tchardoublemap;

            this._loadFactor = tchardoublehashmap._loadFactor;
            this.no_entry_key = tchardoublehashmap.no_entry_key;
            this.no_entry_value = tchardoublehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tchardoublemap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new double[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        char[] achar = this._set;
        double[] adouble = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = adouble[k];
            }
        }

    }

    @Override
    public double put(char c0, double d0) {
        int i = this.insertKey(c0);

        return this.doPut(c0, d0, i);
    }

    @Override
    public double putIfAbsent(char c0, double d0) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, d0, i);
    }

    private double doPut(char c0, double d0, int i) {
        double d1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            d1 = this._values[i];
            flag = false;
        }

        this._values[i] = d0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d1;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Double> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharDoubleMap tchardoublemap) {
        this.ensureCapacity(tchardoublemap.size());
        TCharDoubleIterator tchardoubleiterator = tchardoublemap.iterator();

        while (tchardoubleiterator.hasNext()) {
            tchardoubleiterator.advance();
            this.put(tchardoubleiterator.key(), tchardoubleiterator.value());
        }

    }

    @Override
    public double get(char c0) {
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
    public double remove(char c0) {
        double d0 = this.no_entry_value;
        int i = this.index(c0);

        if (i >= 0) {
            d0 = this._values[i];
            this.removeAt(i);
        }

        return d0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TCharSet keySet() {
        return new TCharDoubleHashMap.TKeyView();
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
    public TDoubleCollection valueCollection() {
        return new TCharDoubleHashMap.TValueView();
    }

    @Override
    public double[] values() {
        double[] adouble = new double[this.size()];
        double[] adouble1 = this._values;
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
    public double[] values(double[] adouble) {
        int i = this.size();

        if (adouble.length < i) {
            adouble = new double[i];
        }

        double[] adouble1 = this._values;
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
    public boolean containsValue(double d0) {
        byte[] abyte = this._states;
        double[] adouble = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || d0 != adouble[i]);

        return true;
    }

    @Override
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharDoubleIterator iterator() {
        return new TCharDoubleHashMap.TCharDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure tdoubleprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoubleprocedure.execute(adouble[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TCharDoubleProcedure tchardoubleprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        double[] adouble = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tchardoubleprocedure.execute(achar[i], adouble[i]));

        return false;
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        byte[] abyte = this._states;
        double[] adouble = this._values;
        int i = adouble.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                adouble[i] = tdoublefunction.execute(adouble[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TCharDoubleProcedure tchardoubleprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        double[] adouble = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tchardoubleprocedure.execute(achar[i], adouble[i])) {
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
        return this.adjustValue(c0, 1.0D);
    }

    @Override
    public boolean adjustValue(char c0, double d0) {
        int i = this.index(c0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += d0;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(char c0, double d0, double d1) {
        int i = this.insertKey(c0);
        double d2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            d2 = this._values[i] += d0;
            flag = false;
        } else {
            d2 = this._values[i] = d1;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TCharDoubleMap)) {
            return false;
        } else {
            TCharDoubleMap tchardoublemap = (TCharDoubleMap) object;

            if (tchardoublemap.size() != this.size()) {
                return false;
            } else {
                double[] adouble = this._values;
                byte[] abyte = this._states;
                double d0 = this.getNoEntryValue();
                double d1 = tchardoublemap.getNoEntryValue();
                int i = adouble.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        char c0 = this._set[i];
                        double d2 = tchardoublemap.get(c0);
                        double d3 = adouble[i];

                        if (d3 != d2 && d3 != d0 && d2 != d1) {
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

        this.forEachEntry(new TCharDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, double d0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
                stringbuilder.append("=");
                stringbuilder.append(d0);
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
                objectoutput.writeDouble(this._values[i]);
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
            double d0 = objectinput.readDouble();

            this.put(c0, d0);
        }

    }

    class TCharDoubleHashIterator extends THashPrimitiveIterator implements TCharDoubleIterator {

        TCharDoubleHashIterator(TCharDoubleHashMap tchardoublehashmap) {
            super(tchardoublehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharDoubleHashMap.this._set[this._index];
        }

        @Override
        public double value() {
            return TCharDoubleHashMap.this._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            TCharDoubleHashMap.this._values[this._index] = d0;
            return d1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TCharDoubleValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TCharDoubleHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharDoubleKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharDoubleKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharDoubleHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TDoubleCollection {

        protected TValueView() {}

        @Override
        public TDoubleIterator iterator() {
            return TCharDoubleHashMap.this.new TCharDoubleValueHashIterator(TCharDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TCharDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TCharDoubleHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TCharDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TCharDoubleHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TCharDoubleHashMap.this._values;
            char[] achar = TCharDoubleHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (achar[i] == 0 || achar[i] == 2 || d0 != adouble[i]);

            TCharDoubleHashMap.this.removeAt(i);
            return true;
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
            } while (TCharDoubleHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TCharDoubleHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TCharDoubleHashMap.this.containsValue(d0)) {
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
            double[] adouble1 = TCharDoubleHashMap.this._values;
            byte[] abyte = TCharDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TCharDoubleHashMap.this.removeAt(i);
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
            TCharDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TCharDoubleHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharDoubleHashMap.this.new TCharDoubleKeyHashIterator(TCharDoubleHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharDoubleHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharDoubleHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharDoubleHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharDoubleHashMap.this.no_entry_value != TCharDoubleHashMap.this.remove(c0);
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
            } while (TCharDoubleHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharDoubleHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharDoubleHashMap.this.contains(c0)) {
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
            char[] achar1 = TCharDoubleHashMap.this._set;
            byte[] abyte = TCharDoubleHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharDoubleHashMap.this.removeAt(i);
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
            TCharDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharDoubleHashMap.this.forEachKey(tcharprocedure);
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
                    int i = TCharDoubleHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharDoubleHashMap.this._states[i] != 1 || tcharset.contains(TCharDoubleHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharDoubleHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharDoubleHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharDoubleHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharDoubleHashMap.this.forEachKey(new TCharProcedure() {
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
