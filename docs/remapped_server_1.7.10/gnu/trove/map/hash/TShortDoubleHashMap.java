package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortDoubleHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
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

public class TShortDoubleHashMap extends TShortDoubleHash implements TShortDoubleMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TShortDoubleHashMap() {}

    public TShortDoubleHashMap(int i) {
        super(i);
    }

    public TShortDoubleHashMap(int i, float f) {
        super(i, f);
    }

    public TShortDoubleHashMap(int i, float f, short short0, double d0) {
        super(i, f, short0, d0);
    }

    public TShortDoubleHashMap(short[] ashort, double[] adouble) {
        super(Math.max(ashort.length, adouble.length));
        int i = Math.min(ashort.length, adouble.length);

        for (int j = 0; j < i; ++j) {
            this.put(ashort[j], adouble[j]);
        }

    }

    public TShortDoubleHashMap(TShortDoubleMap tshortdoublemap) {
        super(tshortdoublemap.size());
        if (tshortdoublemap instanceof TShortDoubleHashMap) {
            TShortDoubleHashMap tshortdoublehashmap = (TShortDoubleHashMap) tshortdoublemap;

            this._loadFactor = tshortdoublehashmap._loadFactor;
            this.no_entry_key = tshortdoublehashmap.no_entry_key;
            this.no_entry_value = tshortdoublehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tshortdoublemap);
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
        short[] ashort = this._set;
        double[] adouble = this._values;
        byte[] abyte = this._states;

        this._set = new short[i];
        this._values = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                short short0 = ashort[k];
                int l = this.insertKey(short0);

                this._values[l] = adouble[k];
            }
        }

    }

    @Override
    public double put(short short0, double d0) {
        int i = this.insertKey(short0);

        return this.doPut(short0, d0, i);
    }

    @Override
    public double putIfAbsent(short short0, double d0) {
        int i = this.insertKey(short0);

        return i < 0 ? this._values[-i - 1] : this.doPut(short0, d0, i);
    }

    private double doPut(short short0, double d0, int i) {
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
    public void putAll(Map<? extends Short, ? extends Double> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Short) entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TShortDoubleMap tshortdoublemap) {
        this.ensureCapacity(tshortdoublemap.size());
        TShortDoubleIterator tshortdoubleiterator = tshortdoublemap.iterator();

        while (tshortdoubleiterator.hasNext()) {
            tshortdoubleiterator.advance();
            this.put(tshortdoubleiterator.key(), tshortdoubleiterator.value());
        }

    }

    @Override
    public double get(short short0) {
        int i = this.index(short0);

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
    public double remove(short short0) {
        double d0 = this.no_entry_value;
        int i = this.index(short0);

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
    public TShortSet keySet() {
        return new TShortDoubleHashMap.TKeyView();
    }

    @Override
    public short[] keys() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._set;
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
    public short[] keys(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._set;
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
    public TDoubleCollection valueCollection() {
        return new TShortDoubleHashMap.TValueView();
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
    public boolean containsKey(short short0) {
        return this.contains(short0);
    }

    @Override
    public TShortDoubleIterator iterator() {
        return new TShortDoubleHashMap.TShortDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure tshortprocedure) {
        return this.forEach(tshortprocedure);
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
    public boolean forEachEntry(TShortDoubleProcedure tshortdoubleprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._set;
        double[] adouble = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortdoubleprocedure.execute(ashort[i], adouble[i]));

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
    public boolean retainEntries(TShortDoubleProcedure tshortdoubleprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        short[] ashort = this._set;
        double[] adouble = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = ashort.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tshortdoubleprocedure.execute(ashort[i], adouble[i])) {
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
    public boolean increment(short short0) {
        return this.adjustValue(short0, 1.0D);
    }

    @Override
    public boolean adjustValue(short short0, double d0) {
        int i = this.index(short0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += d0;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(short short0, double d0, double d1) {
        int i = this.insertKey(short0);
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
        if (!(object instanceof TShortDoubleMap)) {
            return false;
        } else {
            TShortDoubleMap tshortdoublemap = (TShortDoubleMap) object;

            if (tshortdoublemap.size() != this.size()) {
                return false;
            } else {
                double[] adouble = this._values;
                byte[] abyte = this._states;
                double d0 = this.getNoEntryValue();
                double d1 = tshortdoublemap.getNoEntryValue();
                int i = adouble.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        short short0 = this._set[i];
                        double d2 = tshortdoublemap.get(short0);
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

        this.forEachEntry(new TShortDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(short short0, double d0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(short0);
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
                objectoutput.writeShort(this._set[i]);
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
            short short0 = objectinput.readShort();
            double d0 = objectinput.readDouble();

            this.put(short0, d0);
        }

    }

    class TShortDoubleHashIterator extends THashPrimitiveIterator implements TShortDoubleIterator {

        TShortDoubleHashIterator(TShortDoubleHashMap tshortdoublehashmap) {
            super(tshortdoublehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public short key() {
            return TShortDoubleHashMap.this._set[this._index];
        }

        @Override
        public double value() {
            return TShortDoubleHashMap.this._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            TShortDoubleHashMap.this._values[this._index] = d0;
            return d1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TShortDoubleValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TShortDoubleHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TShortDoubleKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TShortDoubleKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TShortDoubleHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TShortDoubleHashMap.this.removeAt(this._index);
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
            return TShortDoubleHashMap.this.new TShortDoubleValueHashIterator(TShortDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TShortDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TShortDoubleHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TShortDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TShortDoubleHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TShortDoubleHashMap.this._values;
            short[] ashort = TShortDoubleHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (ashort[i] == 0 || ashort[i] == 2 || d0 != adouble[i]);

            TShortDoubleHashMap.this.removeAt(i);
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
            } while (TShortDoubleHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TShortDoubleHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TShortDoubleHashMap.this.containsValue(d0)) {
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
            double[] adouble1 = TShortDoubleHashMap.this._values;
            byte[] abyte = TShortDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TShortDoubleHashMap.this.removeAt(i);
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
            TShortDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TShortDoubleHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

    protected class TKeyView implements TShortSet {

        protected TKeyView() {}

        @Override
        public TShortIterator iterator() {
            return TShortDoubleHashMap.this.new TShortDoubleKeyHashIterator(TShortDoubleHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TShortDoubleHashMap.this.contains(short0);
        }

        @Override
        public short[] toArray() {
            return TShortDoubleHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TShortDoubleHashMap.this.keys(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            return TShortDoubleHashMap.this.no_entry_value != TShortDoubleHashMap.this.remove(short0);
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
            } while (TShortDoubleHashMap.this.containsKey(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TShortDoubleHashMap.this.containsKey(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TShortDoubleHashMap.this.contains(short0)) {
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
            short[] ashort1 = TShortDoubleHashMap.this._set;
            byte[] abyte = TShortDoubleHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TShortDoubleHashMap.this.removeAt(i);
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
            TShortDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TShortDoubleHashMap.this.forEachKey(tshortprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TShortSet)) {
                return false;
            } else {
                TShortSet tshortset = (TShortSet) object;

                if (tshortset.size() != this.size()) {
                    return false;
                } else {
                    int i = TShortDoubleHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TShortDoubleHashMap.this._states[i] != 1 || tshortset.contains(TShortDoubleHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TShortDoubleHashMap.this._states.length;

            while (j-- > 0) {
                if (TShortDoubleHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TShortDoubleHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TShortDoubleHashMap.this.forEachKey(new TShortProcedure() {
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
}
