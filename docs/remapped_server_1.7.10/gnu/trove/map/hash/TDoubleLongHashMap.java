package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
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

public class TDoubleLongHashMap extends TDoubleLongHash implements TDoubleLongMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TDoubleLongHashMap() {}

    public TDoubleLongHashMap(int i) {
        super(i);
    }

    public TDoubleLongHashMap(int i, float f) {
        super(i, f);
    }

    public TDoubleLongHashMap(int i, float f, double d0, long j) {
        super(i, f, d0, j);
    }

    public TDoubleLongHashMap(double[] adouble, long[] along) {
        super(Math.max(adouble.length, along.length));
        int i = Math.min(adouble.length, along.length);

        for (int j = 0; j < i; ++j) {
            this.put(adouble[j], along[j]);
        }

    }

    public TDoubleLongHashMap(TDoubleLongMap tdoublelongmap) {
        super(tdoublelongmap.size());
        if (tdoublelongmap instanceof TDoubleLongHashMap) {
            TDoubleLongHashMap tdoublelonghashmap = (TDoubleLongHashMap) tdoublelongmap;

            this._loadFactor = tdoublelonghashmap._loadFactor;
            this.no_entry_key = tdoublelonghashmap.no_entry_key;
            this.no_entry_value = tdoublelonghashmap.no_entry_value;
            if (this.no_entry_key != 0.0D) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tdoublelongmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new long[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        double[] adouble = this._set;
        long[] along = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = along[k];
            }
        }

    }

    @Override
    public long put(double d0, long i) {
        int j = this.insertKey(d0);

        return this.doPut(d0, i, j);
    }

    @Override
    public long putIfAbsent(double d0, long i) {
        int j = this.insertKey(d0);

        return j < 0 ? this._values[-j - 1] : this.doPut(d0, i, j);
    }

    private long doPut(double d0, long i, int j) {
        long k = this.no_entry_value;
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
    public void putAll(Map<? extends Double, ? extends Long> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleLongMap tdoublelongmap) {
        this.ensureCapacity(tdoublelongmap.size());
        TDoubleLongIterator tdoublelongiterator = tdoublelongmap.iterator();

        while (tdoublelongiterator.hasNext()) {
            tdoublelongiterator.advance();
            this.put(tdoublelongiterator.key(), tdoublelongiterator.value());
        }

    }

    @Override
    public long get(double d0) {
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
    public long remove(double d0) {
        long i = this.no_entry_value;
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
        return new TDoubleLongHashMap.TKeyView();
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
    public TLongCollection valueCollection() {
        return new TDoubleLongHashMap.TValueView();
    }

    @Override
    public long[] values() {
        long[] along = new long[this.size()];
        long[] along1 = this._values;
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
    public long[] values(long[] along) {
        int i = this.size();

        if (along.length < i) {
            along = new long[i];
        }

        long[] along1 = this._values;
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
    public boolean containsValue(long i) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int j = along.length;

        do {
            if (j-- <= 0) {
                return false;
            }
        } while (abyte[j] != 1 || i != along[j]);

        return true;
    }

    @Override
    public boolean containsKey(double d0) {
        return this.contains(d0);
    }

    @Override
    public TDoubleLongIterator iterator() {
        return new TDoubleLongHashMap.TDoubleLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure tlongprocedure) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tlongprocedure.execute(along[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TDoubleLongProcedure tdoublelongprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        long[] along = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoublelongprocedure.execute(adouble[i], along[i]));

        return false;
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        byte[] abyte = this._states;
        long[] along = this._values;
        int i = along.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                along[i] = tlongfunction.execute(along[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TDoubleLongProcedure tdoublelongprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        long[] along = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoublelongprocedure.execute(adouble[i], along[i])) {
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
        return this.adjustValue(d0, 1L);
    }

    @Override
    public boolean adjustValue(double d0, long i) {
        int j = this.index(d0);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(double d0, long i, long j) {
        int k = this.insertKey(d0);
        long l;
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
        if (!(object instanceof TDoubleLongMap)) {
            return false;
        } else {
            TDoubleLongMap tdoublelongmap = (TDoubleLongMap) object;

            if (tdoublelongmap.size() != this.size()) {
                return false;
            } else {
                long[] along = this._values;
                byte[] abyte = this._states;
                long i = this.getNoEntryValue();
                long j = tdoublelongmap.getNoEntryValue();
                int k = along.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        double d0 = this._set[k];
                        long l = tdoublelongmap.get(d0);
                        long i1 = along[k];

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

        this.forEachEntry(new TDoubleLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, long i) {
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
                objectoutput.writeLong(this._values[i]);
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
            long j = objectinput.readLong();

            this.put(d0, j);
        }

    }

    class TDoubleLongHashIterator extends THashPrimitiveIterator implements TDoubleLongIterator {

        TDoubleLongHashIterator(TDoubleLongHashMap tdoublelonghashmap) {
            super(tdoublelonghashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
            return TDoubleLongHashMap.this._set[this._index];
        }

        @Override
        public long value() {
            return TDoubleLongHashMap.this._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            TDoubleLongHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TDoubleLongValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TDoubleLongHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleLongKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleLongKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleLongHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TLongCollection {

        protected TValueView() {}

        @Override
        public TLongIterator iterator() {
            return TDoubleLongHashMap.this.new TDoubleLongValueHashIterator(TDoubleLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TDoubleLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TDoubleLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TDoubleLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TDoubleLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TDoubleLongHashMap.this._values;
            double[] adouble = TDoubleLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (adouble[j] == 0.0D || adouble[j] == 2.0D || i != along[j]);

            TDoubleLongHashMap.this.removeAt(j);
            return true;
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
            } while (TDoubleLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TDoubleLongHashMap.this.containsValue(k)) {
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
            long[] along1 = TDoubleLongHashMap.this._values;
            byte[] abyte = TDoubleLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TDoubleLongHashMap.this.removeAt(i);
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
            TDoubleLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TDoubleLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleLongHashMap.this.forEachValue(new TLongProcedure() {
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

    protected class TKeyView implements TDoubleSet {

        protected TKeyView() {}

        @Override
        public TDoubleIterator iterator() {
            return TDoubleLongHashMap.this.new TDoubleLongKeyHashIterator(TDoubleLongHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleLongHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleLongHashMap.this.contains(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleLongHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleLongHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return TDoubleLongHashMap.this.no_entry_value != TDoubleLongHashMap.this.remove(d0);
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
            } while (TDoubleLongHashMap.this.containsKey(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleLongHashMap.this.containsKey(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleLongHashMap.this.contains(d0)) {
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
            double[] adouble1 = TDoubleLongHashMap.this._set;
            byte[] abyte = TDoubleLongHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleLongHashMap.this.removeAt(i);
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
            TDoubleLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleLongHashMap.this.forEachKey(tdoubleprocedure);
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
                    int i = TDoubleLongHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleLongHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleLongHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleLongHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleLongHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleLongHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleLongHashMap.this.forEachKey(new TDoubleProcedure() {
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
