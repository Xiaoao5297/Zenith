package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
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

public class TByteDoubleHashMap extends TByteDoubleHash implements TByteDoubleMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TByteDoubleHashMap() {}

    public TByteDoubleHashMap(int i) {
        super(i);
    }

    public TByteDoubleHashMap(int i, float f) {
        super(i, f);
    }

    public TByteDoubleHashMap(int i, float f, byte b0, double d0) {
        super(i, f, b0, d0);
    }

    public TByteDoubleHashMap(byte[] abyte, double[] adouble) {
        super(Math.max(abyte.length, adouble.length));
        int i = Math.min(abyte.length, adouble.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], adouble[j]);
        }

    }

    public TByteDoubleHashMap(TByteDoubleMap tbytedoublemap) {
        super(tbytedoublemap.size());
        if (tbytedoublemap instanceof TByteDoubleHashMap) {
            TByteDoubleHashMap tbytedoublehashmap = (TByteDoubleHashMap) tbytedoublemap;

            this._loadFactor = tbytedoublehashmap._loadFactor;
            this.no_entry_key = tbytedoublehashmap.no_entry_key;
            this.no_entry_value = tbytedoublehashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0D) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbytedoublemap);
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
        byte[] abyte = this._set;
        double[] adouble = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = new double[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = adouble[k];
            }
        }

    }

    @Override
    public double put(byte b0, double d0) {
        int i = this.insertKey(b0);

        return this.doPut(b0, d0, i);
    }

    @Override
    public double putIfAbsent(byte b0, double d0) {
        int i = this.insertKey(b0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, d0, i);
    }

    private double doPut(byte b0, double d0, int i) {
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
    public void putAll(Map<? extends Byte, ? extends Double> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Double) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteDoubleMap tbytedoublemap) {
        this.ensureCapacity(tbytedoublemap.size());
        TByteDoubleIterator tbytedoubleiterator = tbytedoublemap.iterator();

        while (tbytedoubleiterator.hasNext()) {
            tbytedoubleiterator.advance();
            this.put(tbytedoubleiterator.key(), tbytedoubleiterator.value());
        }

    }

    @Override
    public double get(byte b0) {
        int i = this.index(b0);

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
    public double remove(byte b0) {
        double d0 = this.no_entry_value;
        int i = this.index(b0);

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
    public TByteSet keySet() {
        return new TByteDoubleHashMap.TKeyView();
    }

    @Override
    public byte[] keys() {
        byte[] abyte = new byte[this.size()];
        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int i = abyte1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte2[i] == 1) {
                abyte[j++] = abyte1[i];
            }
        }

        return abyte;
    }

    @Override
    public byte[] keys(byte[] abyte) {
        int i = this.size();

        if (abyte.length < i) {
            abyte = new byte[i];
        }

        byte[] abyte1 = this._set;
        byte[] abyte2 = this._states;
        int j = abyte1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte2[j] == 1) {
                abyte[k++] = abyte1[j];
            }
        }

        return abyte;
    }

    @Override
    public TDoubleCollection valueCollection() {
        return new TByteDoubleHashMap.TValueView();
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
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteDoubleIterator iterator() {
        return new TByteDoubleHashMap.TByteDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
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
    public boolean forEachEntry(TByteDoubleProcedure tbytedoubleprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        double[] adouble = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbytedoubleprocedure.execute(abyte1[i], adouble[i]));

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
    public boolean retainEntries(TByteDoubleProcedure tbytedoubleprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        double[] adouble = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbytedoubleprocedure.execute(abyte1[i], adouble[i])) {
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
    public boolean increment(byte b0) {
        return this.adjustValue(b0, 1.0D);
    }

    @Override
    public boolean adjustValue(byte b0, double d0) {
        int i = this.index(b0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += d0;
            return true;
        }
    }

    @Override
    public double adjustOrPutValue(byte b0, double d0, double d1) {
        int i = this.insertKey(b0);
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

        byte b1 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return d2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteDoubleMap)) {
            return false;
        } else {
            TByteDoubleMap tbytedoublemap = (TByteDoubleMap) object;

            if (tbytedoublemap.size() != this.size()) {
                return false;
            } else {
                double[] adouble = this._values;
                byte[] abyte = this._states;
                double d0 = this.getNoEntryValue();
                double d1 = tbytedoublemap.getNoEntryValue();
                int i = adouble.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        byte b0 = this._set[i];
                        double d2 = tbytedoublemap.get(b0);
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

        this.forEachEntry(new TByteDoubleProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, double d0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
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
                objectoutput.writeByte(this._set[i]);
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
            byte b0 = objectinput.readByte();
            double d0 = objectinput.readDouble();

            this.put(b0, d0);
        }

    }

    class TByteDoubleHashIterator extends THashPrimitiveIterator implements TByteDoubleIterator {

        TByteDoubleHashIterator(TByteDoubleHashMap tbytedoublehashmap) {
            super(tbytedoublehashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteDoubleHashMap.this._set[this._index];
        }

        @Override
        public double value() {
            return TByteDoubleHashMap.this._values[this._index];
        }

        @Override
        public double setValue(double d0) {
            double d1 = this.value();

            TByteDoubleHashMap.this._values[this._index] = d0;
            return d1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TByteDoubleValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TByteDoubleHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteDoubleHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteDoubleKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteDoubleKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteDoubleHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteDoubleHashMap.this.removeAt(this._index);
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
            return TByteDoubleHashMap.this.new TByteDoubleValueHashIterator(TByteDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TByteDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TByteDoubleHashMap.this.containsValue(d0);
        }

        @Override
        public double[] toArray() {
            return TByteDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TByteDoubleHashMap.this.values(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            double[] adouble = TByteDoubleHashMap.this._values;
            byte[] abyte = TByteDoubleHashMap.this._set;
            int i = adouble.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (abyte[i] == 0 || abyte[i] == 2 || d0 != adouble[i]);

            TByteDoubleHashMap.this.removeAt(i);
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
            } while (TByteDoubleHashMap.this.containsValue(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TByteDoubleHashMap.this.containsValue(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TByteDoubleHashMap.this.containsValue(d0)) {
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
            double[] adouble1 = TByteDoubleHashMap.this._values;
            byte[] abyte = TByteDoubleHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TByteDoubleHashMap.this.removeAt(i);
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
            TByteDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TByteDoubleHashMap.this.forEachValue(tdoubleprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteDoubleHashMap.this.new TByteDoubleKeyHashIterator(TByteDoubleHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteDoubleHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteDoubleHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteDoubleHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteDoubleHashMap.this.no_entry_value != TByteDoubleHashMap.this.remove(b0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            byte b0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Byte)) {
                    return false;
                }

                b0 = (Byte) object;
            } while (TByteDoubleHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteDoubleHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteDoubleHashMap.this.contains(b0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TByteCollection tbytecollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(byte[] abyte) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TByteIterator tbyteiterator = this.iterator();

            while (tbyteiterator.hasNext()) {
                if (!collection.contains(tbyteiterator.next())) {
                    tbyteiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TByteCollection tbytecollection) {
            if (this == tbytecollection) {
                return false;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = this.iterator();

                while (tbyteiterator.hasNext()) {
                    if (!tbytecollection.contains(tbyteiterator.next())) {
                        tbyteiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(byte[] abyte) {
            boolean flag = false;

            Arrays.sort(abyte);
            byte[] abyte1 = TByteDoubleHashMap.this._set;
            byte[] abyte2 = TByteDoubleHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteDoubleHashMap.this.removeAt(i);
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

                if (object instanceof Byte) {
                    byte b0 = (Byte) object;

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TByteCollection tbytecollection) {
            if (this == tbytecollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TByteIterator tbyteiterator = tbytecollection.iterator();

                while (tbyteiterator.hasNext()) {
                    byte b0 = tbyteiterator.next();

                    if (this.remove(b0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(byte[] abyte) {
            boolean flag = false;
            int i = abyte.length;

            while (i-- > 0) {
                if (this.remove(abyte[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TByteDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteDoubleHashMap.this.forEachKey(tbyteprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TByteSet)) {
                return false;
            } else {
                TByteSet tbyteset = (TByteSet) object;

                if (tbyteset.size() != this.size()) {
                    return false;
                } else {
                    int i = TByteDoubleHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteDoubleHashMap.this._states[i] != 1 || tbyteset.contains(TByteDoubleHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteDoubleHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteDoubleHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteDoubleHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteDoubleHashMap.this.forEachKey(new TByteProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(byte b0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(b0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
