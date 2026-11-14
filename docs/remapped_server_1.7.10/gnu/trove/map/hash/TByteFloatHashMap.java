package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
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

public class TByteFloatHashMap extends TByteFloatHash implements TByteFloatMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TByteFloatHashMap() {}

    public TByteFloatHashMap(int i) {
        super(i);
    }

    public TByteFloatHashMap(int i, float f) {
        super(i, f);
    }

    public TByteFloatHashMap(int i, float f, byte b0, float f1) {
        super(i, f, b0, f1);
    }

    public TByteFloatHashMap(byte[] abyte, float[] afloat) {
        super(Math.max(abyte.length, afloat.length));
        int i = Math.min(abyte.length, afloat.length);

        for (int j = 0; j < i; ++j) {
            this.put(abyte[j], afloat[j]);
        }

    }

    public TByteFloatHashMap(TByteFloatMap tbytefloatmap) {
        super(tbytefloatmap.size());
        if (tbytefloatmap instanceof TByteFloatHashMap) {
            TByteFloatHashMap tbytefloathashmap = (TByteFloatHashMap) tbytefloatmap;

            this._loadFactor = tbytefloathashmap._loadFactor;
            this.no_entry_key = tbytefloathashmap.no_entry_key;
            this.no_entry_value = tbytefloathashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tbytefloatmap);
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
        byte[] abyte = this._set;
        float[] afloat = this._values;
        byte[] abyte1 = this._states;

        this._set = new byte[i];
        this._values = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte1[k] == 1) {
                byte b0 = abyte[k];
                int l = this.insertKey(b0);

                this._values[l] = afloat[k];
            }
        }

    }

    @Override
    public float put(byte b0, float f) {
        int i = this.insertKey(b0);

        return this.doPut(b0, f, i);
    }

    @Override
    public float putIfAbsent(byte b0, float f) {
        int i = this.insertKey(b0);

        return i < 0 ? this._values[-i - 1] : this.doPut(b0, f, i);
    }

    private float doPut(byte b0, float f, int i) {
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
    public void putAll(Map<? extends Byte, ? extends Float> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Byte) entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TByteFloatMap tbytefloatmap) {
        this.ensureCapacity(tbytefloatmap.size());
        TByteFloatIterator tbytefloatiterator = tbytefloatmap.iterator();

        while (tbytefloatiterator.hasNext()) {
            tbytefloatiterator.advance();
            this.put(tbytefloatiterator.key(), tbytefloatiterator.value());
        }

    }

    @Override
    public float get(byte b0) {
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
    public float remove(byte b0) {
        float f = this.no_entry_value;
        int i = this.index(b0);

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
    public TByteSet keySet() {
        return new TByteFloatHashMap.TKeyView();
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
    public TFloatCollection valueCollection() {
        return new TByteFloatHashMap.TValueView();
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
    public boolean containsKey(byte b0) {
        return this.contains(b0);
    }

    @Override
    public TByteFloatIterator iterator() {
        return new TByteFloatHashMap.TByteFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure tbyteprocedure) {
        return this.forEach(tbyteprocedure);
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
    public boolean forEachEntry(TByteFloatProcedure tbytefloatprocedure) {
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        float[] afloat = this._values;
        int i = abyte1.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tbytefloatprocedure.execute(abyte1[i], afloat[i]));

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
    public boolean retainEntries(TByteFloatProcedure tbytefloatprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        byte[] abyte1 = this._set;
        float[] afloat = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tbytefloatprocedure.execute(abyte1[i], afloat[i])) {
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
        return this.adjustValue(b0, 1.0F);
    }

    @Override
    public boolean adjustValue(byte b0, float f) {
        int i = this.index(b0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += f;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(byte b0, float f, float f1) {
        int i = this.insertKey(b0);
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

        byte b1 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return f2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TByteFloatMap)) {
            return false;
        } else {
            TByteFloatMap tbytefloatmap = (TByteFloatMap) object;

            if (tbytefloatmap.size() != this.size()) {
                return false;
            } else {
                float[] afloat = this._values;
                byte[] abyte = this._states;
                float f = this.getNoEntryValue();
                float f1 = tbytefloatmap.getNoEntryValue();
                int i = afloat.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        byte b0 = this._set[i];
                        float f2 = tbytefloatmap.get(b0);
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

        this.forEachEntry(new TByteFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(byte b0, float f) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(b0);
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
                objectoutput.writeByte(this._set[i]);
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
            byte b0 = objectinput.readByte();
            float f = objectinput.readFloat();

            this.put(b0, f);
        }

    }

    class TByteFloatHashIterator extends THashPrimitiveIterator implements TByteFloatIterator {

        TByteFloatHashIterator(TByteFloatHashMap tbytefloathashmap) {
            super(tbytefloathashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public byte key() {
            return TByteFloatHashMap.this._set[this._index];
        }

        @Override
        public float value() {
            return TByteFloatHashMap.this._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            TByteFloatHashMap.this._values[this._index] = f;
            return f1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TByteFloatValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TByteFloatHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TByteFloatKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        TByteFloatKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public byte next() {
            this.moveToNextIndex();
            return TByteFloatHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TByteFloatHashMap.this.removeAt(this._index);
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
            return TByteFloatHashMap.this.new TByteFloatValueHashIterator(TByteFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TByteFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TByteFloatHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TByteFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TByteFloatHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TByteFloatHashMap.this._values;
            byte[] abyte = TByteFloatHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (abyte[i] == 0 || abyte[i] == 2 || f != afloat[i]);

            TByteFloatHashMap.this.removeAt(i);
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
            } while (TByteFloatHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TByteFloatHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TByteFloatHashMap.this.containsValue(f)) {
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
            float[] afloat1 = TByteFloatHashMap.this._values;
            byte[] abyte = TByteFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TByteFloatHashMap.this.removeAt(i);
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
            TByteFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TByteFloatHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteFloatHashMap.this.forEachValue(new TFloatProcedure() {
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

    protected class TKeyView implements TByteSet {

        protected TKeyView() {}

        @Override
        public TByteIterator iterator() {
            return TByteFloatHashMap.this.new TByteFloatKeyHashIterator(TByteFloatHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteFloatHashMap.this._size;
        }

        @Override
        public boolean contains(byte b0) {
            return TByteFloatHashMap.this.contains(b0);
        }

        @Override
        public byte[] toArray() {
            return TByteFloatHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] abyte) {
            return TByteFloatHashMap.this.keys(abyte);
        }

        @Override
        public boolean add(byte b0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte b0) {
            return TByteFloatHashMap.this.no_entry_value != TByteFloatHashMap.this.remove(b0);
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
            } while (TByteFloatHashMap.this.containsKey(b0));

            return false;
        }

        @Override
        public boolean containsAll(TByteCollection tbytecollection) {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }
            } while (TByteFloatHashMap.this.containsKey(tbyteiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(byte[] abyte) {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!TByteFloatHashMap.this.contains(b0)) {
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
            byte[] abyte1 = TByteFloatHashMap.this._set;
            byte[] abyte2 = TByteFloatHashMap.this._states;
            int i = abyte1.length;

            while (i-- > 0) {
                if (abyte2[i] == 1 && Arrays.binarySearch(abyte, abyte1[i]) < 0) {
                    TByteFloatHashMap.this.removeAt(i);
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
            TByteFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure tbyteprocedure) {
            return TByteFloatHashMap.this.forEachKey(tbyteprocedure);
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
                    int i = TByteFloatHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TByteFloatHashMap.this._states[i] != 1 || tbyteset.contains(TByteFloatHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TByteFloatHashMap.this._states.length;

            while (j-- > 0) {
                if (TByteFloatHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TByteFloatHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TByteFloatHashMap.this.forEachKey(new TByteProcedure() {
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
