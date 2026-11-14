package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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

public class TFloatShortHashMap extends TFloatShortHash implements TFloatShortMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TFloatShortHashMap() {}

    public TFloatShortHashMap(int i) {
        super(i);
    }

    public TFloatShortHashMap(int i, float f) {
        super(i, f);
    }

    public TFloatShortHashMap(int i, float f, float f1, short short0) {
        super(i, f, f1, short0);
    }

    public TFloatShortHashMap(float[] afloat, short[] ashort) {
        super(Math.max(afloat.length, ashort.length));
        int i = Math.min(afloat.length, ashort.length);

        for (int j = 0; j < i; ++j) {
            this.put(afloat[j], ashort[j]);
        }

    }

    public TFloatShortHashMap(TFloatShortMap tfloatshortmap) {
        super(tfloatshortmap.size());
        if (tfloatshortmap instanceof TFloatShortHashMap) {
            TFloatShortHashMap tfloatshorthashmap = (TFloatShortHashMap) tfloatshortmap;

            this._loadFactor = tfloatshorthashmap._loadFactor;
            this.no_entry_key = tfloatshorthashmap.no_entry_key;
            this.no_entry_value = tfloatshorthashmap.no_entry_value;
            if (this.no_entry_key != 0.0F) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tfloatshortmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new short[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        float[] afloat = this._set;
        short[] ashort = this._values;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._values = new short[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];
                int l = this.insertKey(f);

                this._values[l] = ashort[k];
            }
        }

    }

    @Override
    public short put(float f, short short0) {
        int i = this.insertKey(f);

        return this.doPut(f, short0, i);
    }

    @Override
    public short putIfAbsent(float f, short short0) {
        int i = this.insertKey(f);

        return i < 0 ? this._values[-i - 1] : this.doPut(f, short0, i);
    }

    private short doPut(float f, short short0, int i) {
        short short1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            short1 = this._values[i];
            flag = false;
        }

        this._values[i] = short0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short1;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Short> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Short) entry.getValue());
        }

    }

    @Override
    public void putAll(TFloatShortMap tfloatshortmap) {
        this.ensureCapacity(tfloatshortmap.size());
        TFloatShortIterator tfloatshortiterator = tfloatshortmap.iterator();

        while (tfloatshortiterator.hasNext()) {
            tfloatshortiterator.advance();
            this.put(tfloatshortiterator.key(), tfloatshortiterator.value());
        }

    }

    @Override
    public short get(float f) {
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
    public short remove(float f) {
        short short0 = this.no_entry_value;
        int i = this.index(f);

        if (i >= 0) {
            short0 = this._values[i];
            this.removeAt(i);
        }

        return short0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TFloatSet keySet() {
        return new TFloatShortHashMap.TKeyView();
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
    public TShortCollection valueCollection() {
        return new TFloatShortHashMap.TValueView();
    }

    @Override
    public short[] values() {
        short[] ashort = new short[this.size()];
        short[] ashort1 = this._values;
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
    public short[] values(short[] ashort) {
        int i = this.size();

        if (ashort.length < i) {
            ashort = new short[i];
        }

        short[] ashort1 = this._values;
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
    public boolean containsValue(short short0) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || short0 != ashort[i]);

        return true;
    }

    @Override
    public boolean containsKey(float f) {
        return this.contains(f);
    }

    @Override
    public TFloatShortIterator iterator() {
        return new TFloatShortHashMap.TFloatShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.forEach(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure tshortprocedure) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tshortprocedure.execute(ashort[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TFloatShortProcedure tfloatshortprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        short[] ashort = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatshortprocedure.execute(afloat[i], ashort[i]));

        return false;
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        byte[] abyte = this._states;
        short[] ashort = this._values;
        int i = ashort.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                ashort[i] = tshortfunction.execute(ashort[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TFloatShortProcedure tfloatshortprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        float[] afloat = this._set;
        short[] ashort = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = afloat.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tfloatshortprocedure.execute(afloat[i], ashort[i])) {
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
        return this.adjustValue(f, (short) 1);
    }

    @Override
    public boolean adjustValue(float f, short short0) {
        int i = this.index(f);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += short0;
            return true;
        }
    }

    @Override
    public short adjustOrPutValue(float f, short short0, short short1) {
        int i = this.insertKey(f);
        short short2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            short2 = this._values[i] += short0;
            flag = false;
        } else {
            short2 = this._values[i] = short1;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return short2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TFloatShortMap)) {
            return false;
        } else {
            TFloatShortMap tfloatshortmap = (TFloatShortMap) object;

            if (tfloatshortmap.size() != this.size()) {
                return false;
            } else {
                short[] ashort = this._values;
                byte[] abyte = this._states;
                short short0 = this.getNoEntryValue();
                short short1 = tfloatshortmap.getNoEntryValue();
                int i = ashort.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        float f = this._set[i];
                        short short2 = tfloatshortmap.get(f);
                        short short3 = ashort[i];

                        if (short3 != short2 && short3 != short0 && short2 != short1) {
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

        this.forEachEntry(new TFloatShortProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(float f, short short0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(f);
                stringbuilder.append("=");
                stringbuilder.append(short0);
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
                objectoutput.writeShort(this._values[i]);
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
            short short0 = objectinput.readShort();

            this.put(f, short0);
        }

    }

    class TFloatShortHashIterator extends THashPrimitiveIterator implements TFloatShortIterator {

        TFloatShortHashIterator(TFloatShortHashMap tfloatshorthashmap) {
            super(tfloatshorthashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
            return TFloatShortHashMap.this._set[this._index];
        }

        @Override
        public short value() {
            return TFloatShortHashMap.this._values[this._index];
        }

        @Override
        public short setValue(short short0) {
            short short1 = this.value();

            TFloatShortHashMap.this._values[this._index] = short0;
            return short1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        TFloatShortValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public short next() {
            this.moveToNextIndex();
            return TFloatShortHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatShortKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TFloatShortKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TFloatShortHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatShortHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TShortCollection {

        protected TValueView() {}

        @Override
        public TShortIterator iterator() {
            return TFloatShortHashMap.this.new TFloatShortValueHashIterator(TFloatShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TFloatShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TFloatShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatShortHashMap.this._size;
        }

        @Override
        public boolean contains(short short0) {
            return TFloatShortHashMap.this.containsValue(short0);
        }

        @Override
        public short[] toArray() {
            return TFloatShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] ashort) {
            return TFloatShortHashMap.this.values(ashort);
        }

        @Override
        public boolean add(short short0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short short0) {
            short[] ashort = TFloatShortHashMap.this._values;
            float[] afloat = TFloatShortHashMap.this._set;
            int i = ashort.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (afloat[i] == 0.0F || afloat[i] == 2.0F || short0 != ashort[i]);

            TFloatShortHashMap.this.removeAt(i);
            return true;
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
            } while (TFloatShortHashMap.this.containsValue(short0));

            return false;
        }

        @Override
        public boolean containsAll(TShortCollection tshortcollection) {
            TShortIterator tshortiterator = tshortcollection.iterator();

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }
            } while (TFloatShortHashMap.this.containsValue(tshortiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(short[] ashort) {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!TFloatShortHashMap.this.containsValue(short0)) {
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
            short[] ashort1 = TFloatShortHashMap.this._values;
            byte[] abyte = TFloatShortHashMap.this._states;
            int i = ashort1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(ashort, ashort1[i]) < 0) {
                    TFloatShortHashMap.this.removeAt(i);
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
            TFloatShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure tshortprocedure) {
            return TFloatShortHashMap.this.forEachValue(tshortprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatShortHashMap.this.forEachValue(new TShortProcedure() {
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

    protected class TKeyView implements TFloatSet {

        protected TKeyView() {}

        @Override
        public TFloatIterator iterator() {
            return TFloatShortHashMap.this.new TFloatShortKeyHashIterator(TFloatShortHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatShortHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TFloatShortHashMap.this.contains(f);
        }

        @Override
        public float[] toArray() {
            return TFloatShortHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatShortHashMap.this.keys(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            return TFloatShortHashMap.this.no_entry_value != TFloatShortHashMap.this.remove(f);
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
            } while (TFloatShortHashMap.this.containsKey(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TFloatShortHashMap.this.containsKey(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatShortHashMap.this.contains(f)) {
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
            float[] afloat1 = TFloatShortHashMap.this._set;
            byte[] abyte = TFloatShortHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatShortHashMap.this.removeAt(i);
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
            TFloatShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatShortHashMap.this.forEachKey(tfloatprocedure);
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
                    int i = TFloatShortHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TFloatShortHashMap.this._states[i] != 1 || tfloatset.contains(TFloatShortHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TFloatShortHashMap.this._states.length;

            while (j-- > 0) {
                if (TFloatShortHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TFloatShortHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatShortHashMap.this.forEachKey(new TFloatProcedure() {
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
