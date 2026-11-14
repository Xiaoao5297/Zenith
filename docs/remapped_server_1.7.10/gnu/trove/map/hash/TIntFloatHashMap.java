package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntFloatHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntFloatProcedure;
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

public class TIntFloatHashMap extends TIntFloatHash implements TIntFloatMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TIntFloatHashMap() {}

    public TIntFloatHashMap(int i) {
        super(i);
    }

    public TIntFloatHashMap(int i, float f) {
        super(i, f);
    }

    public TIntFloatHashMap(int i, float f, int j, float f1) {
        super(i, f, j, f1);
    }

    public TIntFloatHashMap(int[] aint, float[] afloat) {
        super(Math.max(aint.length, afloat.length));
        int i = Math.min(aint.length, afloat.length);

        for (int j = 0; j < i; ++j) {
            this.put(aint[j], afloat[j]);
        }

    }

    public TIntFloatHashMap(TIntFloatMap tintfloatmap) {
        super(tintfloatmap.size());
        if (tintfloatmap instanceof TIntFloatHashMap) {
            TIntFloatHashMap tintfloathashmap = (TIntFloatHashMap) tintfloatmap;

            this._loadFactor = tintfloathashmap._loadFactor;
            this.no_entry_key = tintfloathashmap.no_entry_key;
            this.no_entry_value = tintfloathashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tintfloatmap);
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
        int[] aint = this._set;
        float[] afloat = this._values;
        byte[] abyte = this._states;

        this._set = new int[i];
        this._values = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                int l = aint[k];
                int i1 = this.insertKey(l);

                this._values[i1] = afloat[k];
            }
        }

    }

    @Override
    public float put(int i, float f) {
        int j = this.insertKey(i);

        return this.doPut(i, f, j);
    }

    @Override
    public float putIfAbsent(int i, float f) {
        int j = this.insertKey(i);

        return j < 0 ? this._values[-j - 1] : this.doPut(i, f, j);
    }

    private float doPut(int i, float f, int j) {
        float f1 = this.no_entry_value;
        boolean flag = true;

        if (j < 0) {
            j = -j - 1;
            f1 = this._values[j];
            flag = false;
        }

        this._values[j] = f;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f1;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Float> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Integer) entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TIntFloatMap tintfloatmap) {
        this.ensureCapacity(tintfloatmap.size());
        TIntFloatIterator tintfloatiterator = tintfloatmap.iterator();

        while (tintfloatiterator.hasNext()) {
            tintfloatiterator.advance();
            this.put(tintfloatiterator.key(), tintfloatiterator.value());
        }

    }

    @Override
    public float get(int i) {
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
    public float remove(int i) {
        float f = this.no_entry_value;
        int j = this.index(i);

        if (j >= 0) {
            f = this._values[j];
            this.removeAt(j);
        }

        return f;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TIntSet keySet() {
        return new TIntFloatHashMap.TKeyView();
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
    public TFloatCollection valueCollection() {
        return new TIntFloatHashMap.TValueView();
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
    public boolean containsKey(int i) {
        return this.contains(i);
    }

    @Override
    public TIntFloatIterator iterator() {
        return new TIntFloatHashMap.TIntFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TIntProcedure tintprocedure) {
        return this.forEach(tintprocedure);
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
    public boolean forEachEntry(TIntFloatProcedure tintfloatprocedure) {
        byte[] abyte = this._states;
        int[] aint = this._set;
        float[] afloat = this._values;
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tintfloatprocedure.execute(aint[i], afloat[i]));

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
    public boolean retainEntries(TIntFloatProcedure tintfloatprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        int[] aint = this._set;
        float[] afloat = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = aint.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tintfloatprocedure.execute(aint[i], afloat[i])) {
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
        return this.adjustValue(i, 1.0F);
    }

    @Override
    public boolean adjustValue(int i, float f) {
        int j = this.index(i);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += f;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(int i, float f, float f1) {
        int j = this.insertKey(i);
        float f2;
        boolean flag;

        if (j < 0) {
            j = -j - 1;
            f2 = this._values[j] += f;
            flag = false;
        } else {
            f2 = this._values[j] = f1;
            flag = true;
        }

        byte b0 = this._states[j];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TIntFloatMap)) {
            return false;
        } else {
            TIntFloatMap tintfloatmap = (TIntFloatMap) object;

            if (tintfloatmap.size() != this.size()) {
                return false;
            } else {
                float[] afloat = this._values;
                byte[] abyte = this._states;
                float f = this.getNoEntryValue();
                float f1 = tintfloatmap.getNoEntryValue();
                int i = afloat.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        int j = this._set[i];
                        float f2 = tintfloatmap.get(j);
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

        this.forEachEntry(new TIntFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(int i, float f) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(i);
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
                objectoutput.writeInt(this._set[i]);
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
            int j = objectinput.readInt();
            float f = objectinput.readFloat();

            this.put(j, f);
        }

    }

    class TIntFloatHashIterator extends THashPrimitiveIterator implements TIntFloatIterator {

        TIntFloatHashIterator(TIntFloatHashMap tintfloathashmap) {
            super(tintfloathashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public int key() {
            return TIntFloatHashMap.this._set[this._index];
        }

        @Override
        public float value() {
            return TIntFloatHashMap.this._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            TIntFloatHashMap.this._values[this._index] = f;
            return f1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TIntFloatValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TIntFloatHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TIntFloatKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {

        TIntFloatKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public int next() {
            this.moveToNextIndex();
            return TIntFloatHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TIntFloatHashMap.this.removeAt(this._index);
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
            return TIntFloatHashMap.this.new TIntFloatValueHashIterator(TIntFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TIntFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TIntFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TIntFloatHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TIntFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TIntFloatHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TIntFloatHashMap.this._values;
            int[] aint = TIntFloatHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (aint[i] == 0 || aint[i] == 2 || f != afloat[i]);

            TIntFloatHashMap.this.removeAt(i);
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
            } while (TIntFloatHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TIntFloatHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TIntFloatHashMap.this.containsValue(f)) {
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
            float[] afloat1 = TIntFloatHashMap.this._values;
            byte[] abyte = TIntFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TIntFloatHashMap.this.removeAt(i);
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
            TIntFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TIntFloatHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntFloatHashMap.this.forEachValue(new TFloatProcedure() {
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

    protected class TKeyView implements TIntSet {

        protected TKeyView() {}

        @Override
        public TIntIterator iterator() {
            return TIntFloatHashMap.this.new TIntFloatKeyHashIterator(TIntFloatHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TIntFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TIntFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TIntFloatHashMap.this._size;
        }

        @Override
        public boolean contains(int i) {
            return TIntFloatHashMap.this.contains(i);
        }

        @Override
        public int[] toArray() {
            return TIntFloatHashMap.this.keys();
        }

        @Override
        public int[] toArray(int[] aint) {
            return TIntFloatHashMap.this.keys(aint);
        }

        @Override
        public boolean add(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int i) {
            return TIntFloatHashMap.this.no_entry_value != TIntFloatHashMap.this.remove(i);
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
            } while (TIntFloatHashMap.this.containsKey(i));

            return false;
        }

        @Override
        public boolean containsAll(TIntCollection tintcollection) {
            TIntIterator tintiterator = tintcollection.iterator();

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }
            } while (TIntFloatHashMap.this.containsKey(tintiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(int[] aint) {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!TIntFloatHashMap.this.contains(k)) {
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
            int[] aint1 = TIntFloatHashMap.this._set;
            byte[] abyte = TIntFloatHashMap.this._states;
            int i = aint1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(aint, aint1[i]) < 0) {
                    TIntFloatHashMap.this.removeAt(i);
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
            TIntFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure tintprocedure) {
            return TIntFloatHashMap.this.forEachKey(tintprocedure);
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
                    int i = TIntFloatHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TIntFloatHashMap.this._states[i] != 1 || tintset.contains(TIntFloatHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TIntFloatHashMap.this._states.length;

            while (j-- > 0) {
                if (TIntFloatHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TIntFloatHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TIntFloatHashMap.this.forEachKey(new TIntProcedure() {
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
