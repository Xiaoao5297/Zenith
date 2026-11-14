package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
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

public class TDoubleFloatHashMap extends TDoubleFloatHash implements TDoubleFloatMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TDoubleFloatHashMap() {}

    public TDoubleFloatHashMap(int i) {
        super(i);
    }

    public TDoubleFloatHashMap(int i, float f) {
        super(i, f);
    }

    public TDoubleFloatHashMap(int i, float f, double d0, float f1) {
        super(i, f, d0, f1);
    }

    public TDoubleFloatHashMap(double[] adouble, float[] afloat) {
        super(Math.max(adouble.length, afloat.length));
        int i = Math.min(adouble.length, afloat.length);

        for (int j = 0; j < i; ++j) {
            this.put(adouble[j], afloat[j]);
        }

    }

    public TDoubleFloatHashMap(TDoubleFloatMap tdoublefloatmap) {
        super(tdoublefloatmap.size());
        if (tdoublefloatmap instanceof TDoubleFloatHashMap) {
            TDoubleFloatHashMap tdoublefloathashmap = (TDoubleFloatHashMap) tdoublefloatmap;

            this._loadFactor = tdoublefloathashmap._loadFactor;
            this.no_entry_key = tdoublefloathashmap.no_entry_key;
            this.no_entry_value = tdoublefloathashmap.no_entry_value;
            if (this.no_entry_key != 0.0D) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tdoublefloatmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new float[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        double[] adouble = this._set;
        float[] afloat = this._values;
        byte[] abyte = this._states;

        this._set = new double[i];
        this._values = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                double d0 = adouble[k];
                int l = this.insertKey(d0);

                this._values[l] = afloat[k];
            }
        }

    }

    @Override
    public float put(double d0, float f) {
        int i = this.insertKey(d0);

        return this.doPut(d0, f, i);
    }

    @Override
    public float putIfAbsent(double d0, float f) {
        int i = this.insertKey(d0);

        return i < 0 ? this._values[-i - 1] : this.doPut(d0, f, i);
    }

    private float doPut(double d0, float f, int i) {
        float f1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            f1 = this._values[i];
            flag = false;
        }

        this._values[i] = f;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f1;
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Float> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Double) entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TDoubleFloatMap tdoublefloatmap) {
        this.ensureCapacity(tdoublefloatmap.size());
        TDoubleFloatIterator tdoublefloatiterator = tdoublefloatmap.iterator();

        while (tdoublefloatiterator.hasNext()) {
            tdoublefloatiterator.advance();
            this.put(tdoublefloatiterator.key(), tdoublefloatiterator.value());
        }

    }

    @Override
    public float get(double d0) {
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
    public float remove(double d0) {
        float f = this.no_entry_value;
        int i = this.index(d0);

        if (i >= 0) {
            f = this._values[i];
            this.removeAt(i);
        }

        return f;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TDoubleSet keySet() {
        return new TDoubleFloatHashMap.TKeyView();
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
    public TFloatCollection valueCollection() {
        return new TDoubleFloatHashMap.TValueView();
    }

    @Override
    public float[] values() {
        float[] afloat = new float[this.size()];
        float[] afloat1 = this._values;
        byte[] abyte = this._states;
        int i = afloat1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[j++] = afloat1[i];
            }
        }

        return afloat;
    }

    @Override
    public float[] values(float[] afloat) {
        int i = this.size();

        if (afloat.length < i) {
            afloat = new float[i];
        }

        float[] afloat1 = this._values;
        byte[] abyte = this._states;
        int j = afloat1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                afloat[k++] = afloat1[j];
            }
        }

        return afloat;
    }

    @Override
    public boolean containsValue(float f) {
        byte[] abyte = this._states;
        float[] afloat = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || f != afloat[i]);

        return true;
    }

    @Override
    public boolean containsKey(double d0) {
        return this.contains(d0);
    }

    @Override
    public TDoubleFloatIterator iterator() {
        return new TDoubleFloatHashMap.TDoubleFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure tdoubleprocedure) {
        return this.forEach(tdoubleprocedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure tfloatprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatprocedure.execute(afloat[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TDoubleFloatProcedure tdoublefloatprocedure) {
        byte[] abyte = this._states;
        double[] adouble = this._set;
        float[] afloat = this._values;
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tdoublefloatprocedure.execute(adouble[i], afloat[i]));

        return false;
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        byte[] abyte = this._states;
        float[] afloat = this._values;
        int i = afloat.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                afloat[i] = tfloatfunction.execute(afloat[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TDoubleFloatProcedure tdoublefloatprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        double[] adouble = this._set;
        float[] afloat = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = adouble.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tdoublefloatprocedure.execute(adouble[i], afloat[i])) {
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
        return this.adjustValue(d0, 1.0F);
    }

    @Override
    public boolean adjustValue(double d0, float f) {
        int i = this.index(d0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += f;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(double d0, float f, float f1) {
        int i = this.insertKey(d0);
        float f2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            f2 = this._values[i] += f;
            flag = false;
        } else {
            f2 = this._values[i] = f1;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TDoubleFloatMap)) {
            return false;
        } else {
            TDoubleFloatMap tdoublefloatmap = (TDoubleFloatMap) object;

            if (tdoublefloatmap.size() != this.size()) {
                return false;
            } else {
                float[] afloat = this._values;
                byte[] abyte = this._states;
                float f = this.getNoEntryValue();
                float f1 = tdoublefloatmap.getNoEntryValue();
                int i = afloat.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        double d0 = this._set[i];
                        float f2 = tdoublefloatmap.get(d0);
                        float f3 = afloat[i];

                        if (f3 != f2 && f3 != f && f2 != f1) {
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

        this.forEachEntry(new TDoubleFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(double d0, float f) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(d0);
                stringbuilder.append("=");
                stringbuilder.append(f);
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
                objectoutput.writeFloat(this._values[i]);
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
            float f = objectinput.readFloat();

            this.put(d0, f);
        }

    }

    class TDoubleFloatHashIterator extends THashPrimitiveIterator implements TDoubleFloatIterator {

        TDoubleFloatHashIterator(TDoubleFloatHashMap tdoublefloathashmap) {
            super(tdoublefloathashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public double key() {
            return TDoubleFloatHashMap.this._set[this._index];
        }

        @Override
        public float value() {
            return TDoubleFloatHashMap.this._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            TDoubleFloatHashMap.this._values[this._index] = f;
            return f1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TDoubleFloatValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TDoubleFloatHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TDoubleFloatKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        TDoubleFloatKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public double next() {
            this.moveToNextIndex();
            return TDoubleFloatHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TDoubleFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TFloatCollection {

        protected TValueView() {}

        @Override
        public TFloatIterator iterator() {
            return TDoubleFloatHashMap.this.new TDoubleFloatValueHashIterator(TDoubleFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TDoubleFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TDoubleFloatHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TDoubleFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TDoubleFloatHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TDoubleFloatHashMap.this._values;
            double[] adouble = TDoubleFloatHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (adouble[i] == 0.0D || adouble[i] == 2.0D || f != afloat[i]);

            TDoubleFloatHashMap.this.removeAt(i);
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            float f;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Float)) {
                    return false;
                }

                f = (Float) object;
            } while (TDoubleFloatHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleFloatHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TDoubleFloatHashMap.this.containsValue(f)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Float> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TFloatCollection tfloatcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(float[] afloat) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TFloatIterator tfloatiterator = this.iterator();

            while (tfloatiterator.hasNext()) {
                if (!collection.contains(tfloatiterator.next())) {
                    tfloatiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TFloatCollection tfloatcollection) {
            if (this == tfloatcollection) {
                return false;
            } else {
                boolean flag = false;
                TFloatIterator tfloatiterator = this.iterator();

                while (tfloatiterator.hasNext()) {
                    if (!tfloatcollection.contains(tfloatiterator.next())) {
                        tfloatiterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(float[] afloat) {
            boolean flag = false;

            Arrays.sort(afloat);
            float[] afloat1 = TDoubleFloatHashMap.this._values;
            byte[] abyte = TDoubleFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TDoubleFloatHashMap.this.removeAt(i);
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

                if (object instanceof Float) {
                    float f = (Float) object;

                    if (this.remove(f)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TFloatCollection tfloatcollection) {
            if (this == tfloatcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TFloatIterator tfloatiterator = tfloatcollection.iterator();

                while (tfloatiterator.hasNext()) {
                    float f = tfloatiterator.next();

                    if (this.remove(f)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(float[] afloat) {
            boolean flag = false;
            int i = afloat.length;

            while (i-- > 0) {
                if (this.remove(afloat[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TDoubleFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TDoubleFloatHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleFloatHashMap.this.forEachValue(new TFloatProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(float f) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(f);
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
            return TDoubleFloatHashMap.this.new TDoubleFloatKeyHashIterator(TDoubleFloatHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleFloatHashMap.this._size;
        }

        @Override
        public boolean contains(double d0) {
            return TDoubleFloatHashMap.this.contains(d0);
        }

        @Override
        public double[] toArray() {
            return TDoubleFloatHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] adouble) {
            return TDoubleFloatHashMap.this.keys(adouble);
        }

        @Override
        public boolean add(double d0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double d0) {
            return TDoubleFloatHashMap.this.no_entry_value != TDoubleFloatHashMap.this.remove(d0);
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
            } while (TDoubleFloatHashMap.this.containsKey(d0));

            return false;
        }

        @Override
        public boolean containsAll(TDoubleCollection tdoublecollection) {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }
            } while (TDoubleFloatHashMap.this.containsKey(tdoubleiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(double[] adouble) {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!TDoubleFloatHashMap.this.contains(d0)) {
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
            double[] adouble1 = TDoubleFloatHashMap.this._set;
            byte[] abyte = TDoubleFloatHashMap.this._states;
            int i = adouble1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(adouble, adouble1[i]) < 0) {
                    TDoubleFloatHashMap.this.removeAt(i);
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
            TDoubleFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure tdoubleprocedure) {
            return TDoubleFloatHashMap.this.forEachKey(tdoubleprocedure);
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
                    int i = TDoubleFloatHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TDoubleFloatHashMap.this._states[i] != 1 || tdoubleset.contains(TDoubleFloatHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TDoubleFloatHashMap.this._states.length;

            while (j-- > 0) {
                if (TDoubleFloatHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TDoubleFloatHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TDoubleFloatHashMap.this.forEachKey(new TDoubleProcedure() {
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
