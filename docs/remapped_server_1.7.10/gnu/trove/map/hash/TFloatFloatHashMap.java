package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
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

public class TFloatFloatHashMap extends TFloatFloatHash implements TFloatFloatMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TFloatFloatHashMap() {}

    public TFloatFloatHashMap(int i) {
        super(i);
    }

    public TFloatFloatHashMap(int i, float f) {
        super(i, f);
    }

    public TFloatFloatHashMap(int i, float f, float f1, float f2) {
        super(i, f, f1, f2);
    }

    public TFloatFloatHashMap(float[] afloat, float[] afloat1) {
        super(Math.max(afloat.length, afloat1.length));
        int i = Math.min(afloat.length, afloat1.length);

        for (int j = 0; j < i; ++j) {
            this.put(afloat[j], afloat1[j]);
        }

    }

    public TFloatFloatHashMap(TFloatFloatMap tfloatfloatmap) {
        super(tfloatfloatmap.size());
        if (tfloatfloatmap instanceof TFloatFloatHashMap) {
            TFloatFloatHashMap tfloatfloathashmap = (TFloatFloatHashMap) tfloatfloatmap;

            this._loadFactor = tfloatfloathashmap._loadFactor;
            this.no_entry_key = tfloatfloathashmap.no_entry_key;
            this.no_entry_value = tfloatfloathashmap.no_entry_value;
            if (this.no_entry_key != 0.0F) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tfloatfloatmap);
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
        float[] afloat = this._set;
        float[] afloat1 = this._values;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._values = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];
                int l = this.insertKey(f);

                this._values[l] = afloat1[k];
            }
        }

    }

    @Override
    public float put(float f, float f1) {
        int i = this.insertKey(f);

        return this.doPut(f, f1, i);
    }

    @Override
    public float putIfAbsent(float f, float f1) {
        int i = this.insertKey(f);

        return i < 0 ? this._values[-i - 1] : this.doPut(f, f1, i);
    }

    private float doPut(float f, float f1, int i) {
        float f2 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            f2 = this._values[i];
            flag = false;
        }

        this._values[i] = f1;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f2;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Float> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TFloatFloatMap tfloatfloatmap) {
        this.ensureCapacity(tfloatfloatmap.size());
        TFloatFloatIterator tfloatfloatiterator = tfloatfloatmap.iterator();

        while (tfloatfloatiterator.hasNext()) {
            tfloatfloatiterator.advance();
            this.put(tfloatfloatiterator.key(), tfloatfloatiterator.value());
        }

    }

    @Override
    public float get(float f) {
        int i = this.index(f);

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
    public float remove(float f) {
        float f1 = this.no_entry_value;
        int i = this.index(f);

        if (i >= 0) {
            f1 = this._values[i];
            this.removeAt(i);
        }

        return f1;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TFloatSet keySet() {
        return new TFloatFloatHashMap.TKeyView();
    }

    @Override
    public float[] keys() {
        float[] afloat = new float[this.size()];
        float[] afloat1 = this._set;
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
    public float[] keys(float[] afloat) {
        int i = this.size();

        if (afloat.length < i) {
            afloat = new float[i];
        }

        float[] afloat1 = this._set;
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
    public TFloatCollection valueCollection() {
        return new TFloatFloatHashMap.TValueView();
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
    public boolean containsKey(float f) {
        return this.contains(f);
    }

    @Override
    public TFloatFloatIterator iterator() {
        return new TFloatFloatHashMap.TFloatFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.forEach(tfloatprocedure);
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
    public boolean forEachEntry(TFloatFloatProcedure tfloatfloatprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        float[] afloat1 = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatfloatprocedure.execute(afloat[i], afloat1[i]));

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
    public boolean retainEntries(TFloatFloatProcedure tfloatfloatprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        float[] afloat = this._set;
        float[] afloat1 = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = afloat.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tfloatfloatprocedure.execute(afloat[i], afloat1[i])) {
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
    public boolean increment(float f) {
        return this.adjustValue(f, 1.0F);
    }

    @Override
    public boolean adjustValue(float f, float f1) {
        int i = this.index(f);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += f1;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(float f, float f1, float f2) {
        int i = this.insertKey(f);
        float f3;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            f3 = this._values[i] += f1;
            flag = false;
        } else {
            f3 = this._values[i] = f2;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f3;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TFloatFloatMap)) {
            return false;
        } else {
            TFloatFloatMap tfloatfloatmap = (TFloatFloatMap) object;

            if (tfloatfloatmap.size() != this.size()) {
                return false;
            } else {
                float[] afloat = this._values;
                byte[] abyte = this._states;
                float f = this.getNoEntryValue();
                float f1 = tfloatfloatmap.getNoEntryValue();
                int i = afloat.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        float f2 = this._set[i];
                        float f3 = tfloatfloatmap.get(f2);
                        float f4 = afloat[i];

                        if (f4 != f3 && f4 != f && f3 != f1) {
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

        this.forEachEntry(new TFloatFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(float f, float f1) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(f);
                stringbuilder.append("=");
                stringbuilder.append(f1);
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
                objectoutput.writeFloat(this._set[i]);
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
            float f = objectinput.readFloat();
            float f1 = objectinput.readFloat();

            this.put(f, f1);
        }

    }

    class TFloatFloatHashIterator extends THashPrimitiveIterator implements TFloatFloatIterator {

        TFloatFloatHashIterator(TFloatFloatHashMap tfloatfloathashmap) {
            super(tfloatfloathashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
            return TFloatFloatHashMap.this._set[this._index];
        }

        @Override
        public float value() {
            return TFloatFloatHashMap.this._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            TFloatFloatHashMap.this._values[this._index] = f;
            return f1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TFloatFloatValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TFloatFloatHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatFloatKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TFloatFloatKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TFloatFloatHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatFloatHashMap.this.removeAt(this._index);
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
            return TFloatFloatHashMap.this.new TFloatFloatValueHashIterator(TFloatFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TFloatFloatHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TFloatFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatFloatHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TFloatFloatHashMap.this._values;
            float[] afloat1 = TFloatFloatHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (afloat1[i] == 0.0F || afloat1[i] == 2.0F || f != afloat[i]);

            TFloatFloatHashMap.this.removeAt(i);
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
            } while (TFloatFloatHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TFloatFloatHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatFloatHashMap.this.containsValue(f)) {
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
            float[] afloat1 = TFloatFloatHashMap.this._values;
            byte[] abyte = TFloatFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatFloatHashMap.this.removeAt(i);
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
            TFloatFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatFloatHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatFloatHashMap.this.forEachValue(new TFloatProcedure() {
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

    protected class TKeyView implements TFloatSet {

        protected TKeyView() {}

        @Override
        public TFloatIterator iterator() {
            return TFloatFloatHashMap.this.new TFloatFloatKeyHashIterator(TFloatFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TFloatFloatHashMap.this.contains(f);
        }

        @Override
        public float[] toArray() {
            return TFloatFloatHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatFloatHashMap.this.keys(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            return TFloatFloatHashMap.this.no_entry_value != TFloatFloatHashMap.this.remove(f);
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
            } while (TFloatFloatHashMap.this.containsKey(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TFloatFloatHashMap.this.containsKey(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatFloatHashMap.this.contains(f)) {
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
            float[] afloat1 = TFloatFloatHashMap.this._set;
            byte[] abyte = TFloatFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatFloatHashMap.this.removeAt(i);
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
            TFloatFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatFloatHashMap.this.forEachKey(tfloatprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TFloatSet)) {
                return false;
            } else {
                TFloatSet tfloatset = (TFloatSet) object;

                if (tfloatset.size() != this.size()) {
                    return false;
                } else {
                    int i = TFloatFloatHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TFloatFloatHashMap.this._states[i] != 1 || tfloatset.contains(TFloatFloatHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TFloatFloatHashMap.this._states.length;

            while (j-- > 0) {
                if (TFloatFloatHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TFloatFloatHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatFloatHashMap.this.forEachKey(new TFloatProcedure() {
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
}
