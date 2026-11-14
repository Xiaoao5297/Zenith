package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
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

public class TDoubleDoubleHashMap extends TDoubleDoubleHash implements TDoubleDoubleMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TDoubleDoubleHashMap() {}

    public TDoubleDoubleHashMap(int i) {
        super(i);
    }

    public TDoubleDoubleHashMap(int i, float f) {
        super(i, f);
    }

    public TDoubleDoubleHashMap(int i, float f, double d0, double d1) {
        super(i, f, d0, d1);
    }

    public TDoubleDoubleHashMap(double[] adouble, double[] adouble1) {
        super(Math.max(adouble.length, adouble1.length));
        int i = Math.min(adouble.length, adouble1.length);

        for (int j = 0; j < i; ++j) {
            this.put(adouble[j], adouble1[j]);
        }

    }

    public TDoubleDoubleHashMap(TDoubleDoubleMap tdoubledoublemap) {
        super(tdoubledoublemap.size());
        if (tdoubledoublemap instanceof TDoubleDoubleHashMap) {
            TDoubleDoubleHashMap tdoubledoublehashmap = (TDoubleDoubleHashMap) tdoubledoublemap;

            this._loadFactor = tdoubledoublehashmap._loadFactor;
            this.no_entry_key = tdoubledoublehashmap.no_entry_key;
            this.no_entry_value = tdoubledoublehashmap.no_entry_value;
            if (this.no_entry_key != 0.0D) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tdoubledoublemap);
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
        double[] adouble = this._set;
        double[] adouble1 = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = adouble1[k];
            }
        }

    }

    @Override
    public double put(double d0, double d1) {
        int i = this.insertKey(d0);

        return this.doPut(d0, d1, i);
    }

    @Override
    public double putIfAbsent(double d0, double d1) {
        int i = this.insertKey(d0);

        return i < 0 ? this._values[-i - 1] : this.doPut(d0, d1, i);
    }

    private double doPut(double d0, double d1, int i) {
        double d2 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            d2 = this._values[i];
            flag = false;
        }

        this._values[i] = d1;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d2;
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleDoubleMap tdoubledoublemap) {
        this.ensureCapacity(tdoubledoublemap.size());
        TDoubleDoubleIterator tdoubledoubleiterator = tdoubledoublemap.iterator();

        while (tdoubledoubleiterator.hasNext()) {
            tdoubledoubleiterator.advance();
            this.put(tdoubledoubleiterator.key(), tdoubledoubleiterator.value());
        }

    }

    @Override
    public double get(double d0) {
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
    public double remove(double d0) {
        double d1 = this.no_entry_value;
        int i = this.index(d0);

        if (i >= 0) {
            d1 = this._values[i];
            this.removeAt(i);
        }

        return d1;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TDoubleSet keySet() {
        return new TDoubleDoubleHashMap.TKeyView();
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
    public TDoubleCollection valueCollection() {
        return new TDoubleDoubleHashMap.TValueView();
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
    public boolean containsKey(double d0) {
        return this.contains(d0);
    }

    @Override
    public TDoubleDoubleIterator iterator() {
        return new TDoubleDoubleHashMap.TDoubleDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
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
    public boolean forEachEntry(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        double[] adouble1 = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoubledoubleprocedure.execute(adouble[i], adouble1[i]));

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
    public boolean retainEntries(TDoubleDoubleProcedure tdoubledoubleprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        double[] adouble1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoubledoubleprocedure.execute(adouble[i], adouble1[i])) {
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
        return this.adjustValue(d0, 1.0D);
    }

    @Override
    public boolean adjustValue(double d0, double d1) {
        int i = this.index(d0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += d1;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(double d0, double d1, double d2) {
        int i = this.insertKey(d0);
        double d3;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            d3 = this._values[i] += d1;
            flag = false;
        } else {
            d3 = this._values[i] = d2;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TDoubleDoubleMap)) {
            return false;
        } else {
            TDoubleDoubleMap tdoubledoublemap = (TDoubleDoubleMap) object;

            if (tdoubledoublemap.size() != this.size()) {
                return false;
            } else {
                double[] adouble = this._values;
                byte[] abyte = this._states;
                double d0 = this.getNoEntryValue();
                double d1 = tdoubledoublemap.getNoEntryValue();
                int i = adouble.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        double d2 = this._set[i];
                        double d3 = tdoubledoublemap.get(d2);
                        double d4 = adouble[i];

                        if (d4 != d3 && d4 != d0 && d3 != d1) {
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

        this.forEachEntry(new TDoubleDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, double d1) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(d0);
                stringbuilder.append("=");
                stringbuilder.append(d1);
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
            double d0 = objectinput.readDouble();
            double d1 = objectinput.readDouble();

            this.put(d0, d1);
        }

    }

    class TDoubleDoubleHashIterator extends THashPrimitiveIterator implements TDoubleDoubleIterator {

        TDoubleDoubleHashIterator(TDoubleDoubleHashMap tdoubledoublehashmap) {
            super(tdoubledoublehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
            return TDoubleDoubleHashMap.this._set[this._index];
        }

        @Override
        public double value() {
            return TDoubleDoubleHashMap.this._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            TDoubleDoubleHashMap.this._values[this._index] = d0;
            return d1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleDoubleValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleDoubleHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleDoubleKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleDoubleKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleDoubleHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleDoubleHashMap.this.removeAt(this._index);
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
            return TDoubleDoubleHashMap.this.new TDoubleDoubleValueHashIterator(TDoubleDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleDoubleHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleDoubleHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TDoubleDoubleHashMap.this._values;
            double[] adouble1 = TDoubleDoubleHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (adouble1[i] == 0.0D || adouble1[i] == 2.0D || d0 != adouble[i]);

            TDoubleDoubleHashMap.this.removeAt(i);
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
            } while (TDoubleDoubleHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleDoubleHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleDoubleHashMap.this.containsValue(d0)) {
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
            double[] adouble1 = TDoubleDoubleHashMap.this._values;
            byte[] abyte = TDoubleDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleDoubleHashMap.this.removeAt(i);
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
            TDoubleDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleDoubleHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

    protected class TKeyView implements TDoubleSet {

        protected TKeyView() {}

        @Override
        public TDoubleIterator iterator() {
            return TDoubleDoubleHashMap.this.new TDoubleDoubleKeyHashIterator(TDoubleDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleDoubleHashMap.this.contains(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleDoubleHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleDoubleHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return TDoubleDoubleHashMap.this.no_entry_value != TDoubleDoubleHashMap.this.remove(d0);
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
            } while (TDoubleDoubleHashMap.this.containsKey(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleDoubleHashMap.this.containsKey(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleDoubleHashMap.this.contains(d0)) {
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
            double[] adouble1 = TDoubleDoubleHashMap.this._set;
            byte[] abyte = TDoubleDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleDoubleHashMap.this.removeAt(i);
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
            TDoubleDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleDoubleHashMap.this.forEachKey(tdoubleprocedure);
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
                    int i = TDoubleDoubleHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleDoubleHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleDoubleHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleDoubleHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleDoubleHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleDoubleHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleDoubleHashMap.this.forEachKey(new TDoubleProcedure() {
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
