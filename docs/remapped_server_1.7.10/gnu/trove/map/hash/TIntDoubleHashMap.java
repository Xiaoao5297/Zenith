package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntDoubleHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
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

public class TIntDoubleHashMap extends TIntDoubleHash implements TIntDoubleMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TIntDoubleHashMap() {}

    public TIntDoubleHashMap(int i) {
        super(i);
    }

    public TIntDoubleHashMap(int i, float f) {
        super(i, f);
    }

    public TIntDoubleHashMap(int i, float f, int j, double d0) {
        super(i, f, j, d0);
    }

    public TIntDoubleHashMap(int[] aint, double[] adouble) {
        super(Math.max(aint.length, adouble.length));
        int i = Math.min(aint.length, adouble.length);

        for (int j = 0; j < i; ++j) {
            this.put(aint[j], adouble[j]);
        }

    }

    public TIntDoubleHashMap(TIntDoubleMap tintdoublemap) {
        super(tintdoublemap.size());
        if (tintdoublemap instanceof TIntDoubleHashMap) {
            TIntDoubleHashMap tintdoublehashmap = (TIntDoubleHashMap) tintdoublemap;

            this._loadFactor = tintdoublehashmap._loadFactor;
            this.no_entry_key = tintdoublehashmap.no_entry_key;
            this.no_entry_value = tintdoublehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tintdoublemap);
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
        int[] aint = this._set;
        double[] adouble = this._values;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._values = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = adouble[k];
            }
        }

    }

    @Override
    public double put(int i, double d0) {
        int j = this.insertKey(i);

        return this.doPut(i, d0, j);
    }

    @Override
    public double putIfAbsent(int i, double d0) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, d0, j);
    }

    private double doPut(int i, double d0, int j) {
        double d1 = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            d1 = this._values[j];
            flag = false;
        }

        this._values[j] = d0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d1;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Double> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TIntDoubleMap tintdoublemap) {
        this.ensureCapacity(tintdoublemap.size());
        TIntDoubleIterator tintdoubleiterator = tintdoublemap.iterator();

        while (tintdoubleiterator.hasNext()) {
            tintdoubleiterator.advance();
            this.put(tintdoubleiterator.key(), tintdoubleiterator.value());
        }

    }

    @Override
    public double get(int i) {
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
    public double remove(int i) {
        double d0 = this.no_entry_value;
        int j = this.index(i);

        if (j >= 0) {
            d0 = this._values[j];
            this.removeAt(j);
        }

        return d0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TIntSet keySet() {
        return new TIntDoubleHashMap.TKeyView();
    }

    @Override
    public int[] keys() {
        int[] aint = new int[this.size()];
        int[] aint1 = this._set;
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
    public int[] keys(int[] aint) {
        int i = this.size();

        if (aint.length < i) {
            aint = new int[i];
        }

        int[] aint1 = this._set;
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
    public TDoubleCollection valueCollection() {
        return new TIntDoubleHashMap.TValueView();
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
    public boolean containsKey(int i) {
        return this.contains(i);
    }

    @Override
    public TIntDoubleIterator iterator() {
        return new TIntDoubleHashMap.TIntDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntDoubleProcedure tintdoubleprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        double[] adouble = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintdoubleprocedure.execute(aint[i], adouble[i]));

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
    public boolean retainEntries(TIntDoubleProcedure tintdoubleprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        double[] adouble = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintdoubleprocedure.execute(aint[i], adouble[i])) {
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
    public boolean increment(int i) {
        return this.adjustValue(i, 1.0D);
    }

    @Override
    public boolean adjustValue(int i, double d0) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += d0;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(int i, double d0, double d1) {
        int j = this.insertKey(i);
        double d2;
        boolean flag;

        if (j < 0) {
            j = -j - 1;
            d2 = this._values[j] += d0;
            flag = false;
        } else {
            d2 = this._values[j] = d1;
            flag = true;
        }

        byte b0 = this._states[j];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TIntDoubleMap)) {
            return false;
        } else {
            TIntDoubleMap tintdoublemap = (TIntDoubleMap) object;

            if (tintdoublemap.size() != this.size()) {
                return false;
            } else {
                double[] adouble = this._values;
                byte[] abyte = this._states;
                double d0 = this.getNoEntryValue();
                double d1 = tintdoublemap.getNoEntryValue();
                int i = adouble.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        int j = this._set[i];
                        double d2 = tintdoublemap.get(j);
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

        this.forEachEntry(new TIntDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int i, double d0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
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
                objectoutput.writeInt(this._set[i]);
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
            int j = objectinput.readInt();
            double d0 = objectinput.readDouble();

            this.put(j, d0);
        }

    }

    class TIntDoubleHashIterator extends THashPrimitiveIterator implements TIntDoubleIterator {

        TIntDoubleHashIterator(TIntDoubleHashMap tintdoublehashmap) {
            super(tintdoublehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
            return TIntDoubleHashMap.this._set[this._index];
        }

        @Override
        public double value() {
            return TIntDoubleHashMap.this._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            TIntDoubleHashMap.this._values[this._index] = d0;
            return d1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TIntDoubleValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TIntDoubleHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntDoubleKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntDoubleKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntDoubleHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntDoubleHashMap.this.removeAt(this._index);
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
            return TIntDoubleHashMap.this.new TIntDoubleValueHashIterator(TIntDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TIntDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TIntDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TIntDoubleHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TIntDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TIntDoubleHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TIntDoubleHashMap.this._values;
            int[] aint = TIntDoubleHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aint[i] == 0 || aint[i] == 2 || d0 != adouble[i]);

            TIntDoubleHashMap.this.removeAt(i);
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
            } while (TIntDoubleHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TIntDoubleHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TIntDoubleHashMap.this.containsValue(d0)) {
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
            double[] adouble1 = TIntDoubleHashMap.this._values;
            byte[] abyte = TIntDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TIntDoubleHashMap.this.removeAt(i);
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
            TIntDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TIntDoubleHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

    protected class TKeyView implements TIntSet {

        protected TKeyView() {}

        @Override
        public TIntIterator iterator() {
            return TIntDoubleHashMap.this.new TIntDoubleKeyHashIterator(TIntDoubleHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntDoubleHashMap.this.contains(i);
        }

        @Override
        public int[] toArray() {
            return TIntDoubleHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntDoubleHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return TIntDoubleHashMap.this.no_entry_value != TIntDoubleHashMap.this.remove(i);
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
            } while (TIntDoubleHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntDoubleHashMap.this.containsKey(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntDoubleHashMap.this.contains(k)) {
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
            int[] aint1 = TIntDoubleHashMap.this._set;
            byte[] abyte = TIntDoubleHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntDoubleHashMap.this.removeAt(i);
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
            TIntDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntDoubleHashMap.this.forEachKey(tintprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TIntSet)) {
                return false;
            } else {
                TIntSet tintset = (TIntSet) object;

                if (tintset.size() != this.size()) {
                    return false;
                } else {
                    int i = TIntDoubleHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntDoubleHashMap.this._states[i] != 1 || tintset.contains(TIntDoubleHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntDoubleHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntDoubleHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntDoubleHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntDoubleHashMap.this.forEachKey(new TIntProcedure() {
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
}
