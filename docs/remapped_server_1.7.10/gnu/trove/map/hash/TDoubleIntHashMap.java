package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleIntHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
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

public class TDoubleIntHashMap extends TDoubleIntHash implements TDoubleIntMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TDoubleIntHashMap() {}

    public TDoubleIntHashMap(int i) {
        super(i);
    }

    public TDoubleIntHashMap(int i, float f) {
        super(i, f);
    }

    public TDoubleIntHashMap(int i, float f, double d0, int j) {
        super(i, f, d0, j);
    }

    public TDoubleIntHashMap(double[] adouble, int[] aint) {
        super(Math.max(adouble.length, aint.length));
        int i = Math.min(adouble.length, aint.length);

        for (int j = 0; j < i; ++j) {
            this.put(adouble[j], aint[j]);
        }

    }

    public TDoubleIntHashMap(TDoubleIntMap tdoubleintmap) {
        super(tdoubleintmap.size());
        if (tdoubleintmap instanceof TDoubleIntHashMap) {
            TDoubleIntHashMap tdoubleinthashmap = (TDoubleIntHashMap) tdoubleintmap;

            this._loadFactor = tdoubleinthashmap._loadFactor;
            this.no_entry_key = tdoubleinthashmap.no_entry_key;
            this.no_entry_value = tdoubleinthashmap.no_entry_value;
            if (this.no_entry_key != 0.0D) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tdoubleintmap);
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
        double[] adouble = this._set;
        int[] aint = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = new int[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = aint[k];
            }
        }

    }

    @Override
    public int put(double d0, int i) {
        int j = this.insertKey(d0);

        return this.doPut(d0, i, j);
    }

    @Override
    public int putIfAbsent(double d0, int i) {
        int j = this.insertKey(d0);

        return j < 0 ? this._values[-j - 1] : this.doPut(d0, i, j);
    }

    private int doPut(double d0, int i, int j) {
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
    public void putAll(Map<? extends Double, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Integer) entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleIntMap tdoubleintmap) {
        this.ensureCapacity(tdoubleintmap.size());
        TDoubleIntIterator tdoubleintiterator = tdoubleintmap.iterator();

        while (tdoubleintiterator.hasNext()) {
            tdoubleintiterator.advance();
            this.put(tdoubleintiterator.key(), tdoubleintiterator.value());
        }

    }

    @Override
    public int get(double d0) {
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
    public int remove(double d0) {
        int i = this.no_entry_value;
        int j = this.index(d0);

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
    public TDoubleSet keySet() {
        return new TDoubleIntHashMap.TKeyView();
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
    public TIntCollection valueCollection() {
        return new TDoubleIntHashMap.TValueView();
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
    public boolean containsKey(double d0) {
        return this.contains(d0);
    }

    @Override
    public TDoubleIntIterator iterator() {
        return new TDoubleIntHashMap.TDoubleIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
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
    public boolean forEachEntry(TDoubleIntProcedure tdoubleintprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        int[] aint = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoubleintprocedure.execute(adouble[i], aint[i]));

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
    public boolean retainEntries(TDoubleIntProcedure tdoubleintprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        int[] aint = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoubleintprocedure.execute(adouble[i], aint[i])) {
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
        return this.adjustValue(d0, 1);
    }

    @Override
    public boolean adjustValue(double d0, int i) {
        int j = this.index(d0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public int adjustOrPutValue(double d0, int i, int j) {
        int k = this.insertKey(d0);
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
        if (!(object instanceof TDoubleIntMap)) {
            return false;
        } else {
            TDoubleIntMap tdoubleintmap = (TDoubleIntMap) object;

            if (tdoubleintmap.size() != this.size()) {
                return false;
            } else {
                int[] aint = this._values;
                byte[] abyte = this._states;
                int i = this.getNoEntryValue();
                int j = tdoubleintmap.getNoEntryValue();
                int k = aint.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        double d0 = this._set[k];
                        int l = tdoubleintmap.get(d0);
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

        this.forEachEntry(new TDoubleIntProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, int i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(d0);
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
                objectoutput.writeDouble(this._set[i]);
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
            double d0 = objectinput.readDouble();
            int j = objectinput.readInt();

            this.put(d0, j);
        }

    }

    class TDoubleIntHashIterator extends THashPrimitiveIterator implements TDoubleIntIterator {

        TDoubleIntHashIterator(TDoubleIntHashMap tdoubleinthashmap) {
            super(tdoubleinthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
            return TDoubleIntHashMap.this._set[this._index];
        }

        @Override
        public int value() {
            return TDoubleIntHashMap.this._values[this._index];
        }

        @Override
        public int setValue(int i) {
            int j = this.value();

            TDoubleIntHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TDoubleIntValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TDoubleIntHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleIntHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleIntKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleIntKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleIntHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleIntHashMap.this.removeAt(this._index);
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
            return TDoubleIntHashMap.this.new TDoubleIntValueHashIterator(TDoubleIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TDoubleIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleIntHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TDoubleIntHashMap.this.containsValue(i);
        }

        @Override
        public int[] toArray() {
            return TDoubleIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TDoubleIntHashMap.this.values(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            int[] aint = TDoubleIntHashMap.this._values;
            double[] adouble = TDoubleIntHashMap.this._set;
            int j = aint.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (adouble[j] == 0.0D || adouble[j] == 2.0D || i != aint[j]);

            TDoubleIntHashMap.this.removeAt(j);
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
            } while (TDoubleIntHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleIntHashMap.this.containsValue(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TDoubleIntHashMap.this.containsValue(k)) {
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
            int[] aint1 = TDoubleIntHashMap.this._values;
            byte[] abyte = TDoubleIntHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TDoubleIntHashMap.this.removeAt(i);
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
            TDoubleIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TDoubleIntHashMap.this.forEachValue(tintprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleIntHashMap.this.forEachValue(new TIntProcedure() {
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

    protected class TKeyView implements TDoubleSet {

        protected TKeyView() {}

        @Override
        public TDoubleIterator iterator() {
            return TDoubleIntHashMap.this.new TDoubleIntKeyHashIterator(TDoubleIntHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleIntHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleIntHashMap.this.contains(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleIntHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleIntHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return TDoubleIntHashMap.this.no_entry_value != TDoubleIntHashMap.this.remove(d0);
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
            } while (TDoubleIntHashMap.this.containsKey(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleIntHashMap.this.containsKey(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleIntHashMap.this.contains(d0)) {
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
            double[] adouble1 = TDoubleIntHashMap.this._set;
            byte[] abyte = TDoubleIntHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleIntHashMap.this.removeAt(i);
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
            TDoubleIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleIntHashMap.this.forEachKey(tdoubleprocedure);
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
                    int i = TDoubleIntHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleIntHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleIntHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleIntHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleIntHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleIntHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleIntHashMap.this.forEachKey(new TDoubleProcedure() {
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
