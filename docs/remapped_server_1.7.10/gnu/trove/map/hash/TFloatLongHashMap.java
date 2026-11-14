package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
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

public class TFloatLongHashMap extends TFloatLongHash implements TFloatLongMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TFloatLongHashMap() {}

    public TFloatLongHashMap(int i) {
        super(i);
    }

    public TFloatLongHashMap(int i, float f) {
        super(i, f);
    }

    public TFloatLongHashMap(int i, float f, float f1, long j) {
        super(i, f, f1, j);
    }

    public TFloatLongHashMap(float[] afloat, long[] along) {
        super(Math.max(afloat.length, along.length));
        int i = Math.min(afloat.length, along.length);

        for (int j = 0; j < i; ++j) {
            this.put(afloat[j], along[j]);
        }

    }

    public TFloatLongHashMap(TFloatLongMap tfloatlongmap) {
        super(tfloatlongmap.size());
        if (tfloatlongmap instanceof TFloatLongHashMap) {
            TFloatLongHashMap tfloatlonghashmap = (TFloatLongHashMap) tfloatlongmap;

            this._loadFactor = tfloatlonghashmap._loadFactor;
            this.no_entry_key = tfloatlonghashmap.no_entry_key;
            this.no_entry_value = tfloatlonghashmap.no_entry_value;
            if (this.no_entry_key != 0.0F) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tfloatlongmap);
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
        float[] afloat = this._set;
        long[] along = this._values;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._values = new long[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];
                int l = this.insertKey(f);

                this._values[l] = along[k];
            }
        }

    }

    @Override
    public long put(float f, long i) {
        int j = this.insertKey(f);

        return this.doPut(f, i, j);
    }

    @Override
    public long putIfAbsent(float f, long i) {
        int j = this.insertKey(f);

        return j < 0 ? this._values[-j - 1] : this.doPut(f, i, j);
    }

    private long doPut(float f, long i, int j) {
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
    public void putAll(Map<? extends Float, ? extends Long> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Long) entry.getValue());
        }

    }

    @Override
    public void putAll(TFloatLongMap tfloatlongmap) {
        this.ensureCapacity(tfloatlongmap.size());
        TFloatLongIterator tfloatlongiterator = tfloatlongmap.iterator();

        while (tfloatlongiterator.hasNext()) {
            tfloatlongiterator.advance();
            this.put(tfloatlongiterator.key(), tfloatlongiterator.value());
        }

    }

    @Override
    public long get(float f) {
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
    public long remove(float f) {
        long i = this.no_entry_value;
        int j = this.index(f);

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
    public TFloatSet keySet() {
        return new TFloatLongHashMap.TKeyView();
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
    public TLongCollection valueCollection() {
        return new TFloatLongHashMap.TValueView();
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
    public boolean containsKey(float f) {
        return this.contains(f);
    }

    @Override
    public TFloatLongIterator iterator() {
        return new TFloatLongHashMap.TFloatLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.forEach(tfloatprocedure);
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
    public boolean forEachEntry(TFloatLongProcedure tfloatlongprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        long[] along = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatlongprocedure.execute(afloat[i], along[i]));

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
    public boolean retainEntries(TFloatLongProcedure tfloatlongprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        float[] afloat = this._set;
        long[] along = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = afloat.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tfloatlongprocedure.execute(afloat[i], along[i])) {
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
        return this.adjustValue(f, 1L);
    }

    @Override
    public boolean adjustValue(float f, long i) {
        int j = this.index(f);

        if (j < 0) {
            return false;
        } else {
            this._values[j] += i;
            return true;
        }
    }

    @Override
    public long adjustOrPutValue(float f, long i, long j) {
        int k = this.insertKey(f);
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
        if (!(object instanceof TFloatLongMap)) {
            return false;
        } else {
            TFloatLongMap tfloatlongmap = (TFloatLongMap) object;

            if (tfloatlongmap.size() != this.size()) {
                return false;
            } else {
                long[] along = this._values;
                byte[] abyte = this._states;
                long i = this.getNoEntryValue();
                long j = tfloatlongmap.getNoEntryValue();
                int k = along.length;

                while (k-- > 0) {
                    if (abyte[k] == 1) {
                        float f = this._set[k];
                        long l = tfloatlongmap.get(f);
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

        this.forEachEntry(new TFloatLongProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(float f, long i) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(f);
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
                objectoutput.writeFloat(this._set[i]);
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
            float f = objectinput.readFloat();
            long j = objectinput.readLong();

            this.put(f, j);
        }

    }

    class TFloatLongHashIterator extends THashPrimitiveIterator implements TFloatLongIterator {

        TFloatLongHashIterator(TFloatLongHashMap tfloatlonghashmap) {
            super(tfloatlonghashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
            return TFloatLongHashMap.this._set[this._index];
        }

        @Override
        public long value() {
            return TFloatLongHashMap.this._values[this._index];
        }

        @Override
        public long setValue(long i) {
            long j = this.value();

            TFloatLongHashMap.this._values[this._index] = i;
            return j;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {

        TFloatLongValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public long next() {
            this.moveToNextIndex();
            return TFloatLongHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatLongHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatLongKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TFloatLongKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TFloatLongHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatLongHashMap.this.removeAt(this._index);
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
            return TFloatLongHashMap.this.new TFloatLongValueHashIterator(TFloatLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TFloatLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TFloatLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatLongHashMap.this._size;
        }

        @Override
        public boolean contains(long i) {
            return TFloatLongHashMap.this.containsValue(i);
        }

        @Override
        public long[] toArray() {
            return TFloatLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] along) {
            return TFloatLongHashMap.this.values(along);
        }

        @Override
        public boolean add(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long i) {
            long[] along = TFloatLongHashMap.this._values;
            float[] afloat = TFloatLongHashMap.this._set;
            int j = along.length;

            do {
                if (j-- <= 0) {
                    return false;
                }
            } while (afloat[j] == 0.0F || afloat[j] == 2.0F || i != along[j]);

            TFloatLongHashMap.this.removeAt(j);
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
            } while (TFloatLongHashMap.this.containsValue(i));

            return false;
        }

        @Override
        public boolean containsAll(TLongCollection tlongcollection) {
            TLongIterator tlongiterator = tlongcollection.iterator();

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }
            } while (TFloatLongHashMap.this.containsValue(tlongiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(long[] along) {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!TFloatLongHashMap.this.containsValue(k)) {
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
            long[] along1 = TFloatLongHashMap.this._values;
            byte[] abyte = TFloatLongHashMap.this._states;
            int i = along1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(along, along1[i]) < 0) {
                    TFloatLongHashMap.this.removeAt(i);
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
            TFloatLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure tlongprocedure) {
            return TFloatLongHashMap.this.forEachValue(tlongprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatLongHashMap.this.forEachValue(new TLongProcedure() {
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

    protected class TKeyView implements TFloatSet {

        protected TKeyView() {}

        @Override
        public TFloatIterator iterator() {
            return TFloatLongHashMap.this.new TFloatLongKeyHashIterator(TFloatLongHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatLongHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TFloatLongHashMap.this.contains(f);
        }

        @Override
        public float[] toArray() {
            return TFloatLongHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatLongHashMap.this.keys(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            return TFloatLongHashMap.this.no_entry_value != TFloatLongHashMap.this.remove(f);
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
            } while (TFloatLongHashMap.this.containsKey(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TFloatLongHashMap.this.containsKey(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatLongHashMap.this.contains(f)) {
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
            float[] afloat1 = TFloatLongHashMap.this._set;
            byte[] abyte = TFloatLongHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatLongHashMap.this.removeAt(i);
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
            TFloatLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatLongHashMap.this.forEachKey(tfloatprocedure);
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
                    int i = TFloatLongHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TFloatLongHashMap.this._states[i] != 1 || tfloatset.contains(TFloatLongHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TFloatLongHashMap.this._states.length;

            while (j-- > 0) {
                if (TFloatLongHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TFloatLongHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatLongHashMap.this.forEachKey(new TFloatProcedure() {
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
