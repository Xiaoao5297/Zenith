package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
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

public class TCharFloatHashMap extends TCharFloatHash implements TCharFloatMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TCharFloatHashMap() {}

    public TCharFloatHashMap(int i) {
        super(i);
    }

    public TCharFloatHashMap(int i, float f) {
        super(i, f);
    }

    public TCharFloatHashMap(int i, float f, char c0, float f1) {
        super(i, f, c0, f1);
    }

    public TCharFloatHashMap(char[] achar, float[] afloat) {
        super(Math.max(achar.length, afloat.length));
        int i = Math.min(achar.length, afloat.length);

        for (int j = 0; j < i; ++j) {
            this.put(achar[j], afloat[j]);
        }

    }

    public TCharFloatHashMap(TCharFloatMap tcharfloatmap) {
        super(tcharfloatmap.size());
        if (tcharfloatmap instanceof TCharFloatHashMap) {
            TCharFloatHashMap tcharfloathashmap = (TCharFloatHashMap) tcharfloatmap;

            this._loadFactor = tcharfloathashmap._loadFactor;
            this.no_entry_key = tcharfloathashmap.no_entry_key;
            this.no_entry_value = tcharfloathashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0.0F) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tcharfloatmap);
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
        char[] achar = this._set;
        float[] afloat = this._values;
        byte[] abyte = this._states;

        this._set = new char[i];
        this._values = new float[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                char c0 = achar[k];
                int l = this.insertKey(c0);

                this._values[l] = afloat[k];
            }
        }

    }

    @Override
    public float put(char c0, float f) {
        int i = this.insertKey(c0);

        return this.doPut(c0, f, i);
    }

    @Override
    public float putIfAbsent(char c0, float f) {
        int i = this.insertKey(c0);

        return i < 0 ? this._values[-i - 1] : this.doPut(c0, f, i);
    }

    private float doPut(char c0, float f, int i) {
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
    public void putAll(Map<? extends Character, ? extends Float> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Character) entry.getKey(), (Float) entry.getValue());
        }

    }

    @Override
    public void putAll(TCharFloatMap tcharfloatmap) {
        this.ensureCapacity(tcharfloatmap.size());
        TCharFloatIterator tcharfloatiterator = tcharfloatmap.iterator();

        while (tcharfloatiterator.hasNext()) {
            tcharfloatiterator.advance();
            this.put(tcharfloatiterator.key(), tcharfloatiterator.value());
        }

    }

    @Override
    public float get(char c0) {
        int i = this.index(c0);

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
    public float remove(char c0) {
        float f = this.no_entry_value;
        int i = this.index(c0);

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
    public TCharSet keySet() {
        return new TCharFloatHashMap.TKeyView();
    }

    @Override
    public char[] keys() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int i = achar1.length;
        int j = 0;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[j++] = achar1[i];
            }
        }

        return achar;
    }

    @Override
    public char[] keys(char[] achar) {
        int i = this.size();

        if (achar.length < i) {
            achar = new char[i];
        }

        char[] achar1 = this._set;
        byte[] abyte = this._states;
        int j = achar1.length;
        int k = 0;

        while (j-- > 0) {
            if (abyte[j] == 1) {
                achar[k++] = achar1[j];
            }
        }

        return achar;
    }

    @Override
    public TFloatCollection valueCollection() {
        return new TCharFloatHashMap.TValueView();
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
    public boolean containsKey(char c0) {
        return this.contains(c0);
    }

    @Override
    public TCharFloatIterator iterator() {
        return new TCharFloatHashMap.TCharFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure tcharprocedure) {
        return this.forEach(tcharprocedure);
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
    public boolean forEachEntry(TCharFloatProcedure tcharfloatprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._set;
        float[] afloat = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharfloatprocedure.execute(achar[i], afloat[i]));

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
    public boolean retainEntries(TCharFloatProcedure tcharfloatprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        char[] achar = this._set;
        float[] afloat = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = achar.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tcharfloatprocedure.execute(achar[i], afloat[i])) {
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
    public boolean increment(char c0) {
        return this.adjustValue(c0, 1.0F);
    }

    @Override
    public boolean adjustValue(char c0, float f) {
        int i = this.index(c0);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += f;
            return true;
        }
    }

    @Override
    public float adjustOrPutValue(char c0, float f, float f1) {
        int i = this.insertKey(c0);
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
        if (!(object instanceof TCharFloatMap)) {
            return false;
        } else {
            TCharFloatMap tcharfloatmap = (TCharFloatMap) object;

            if (tcharfloatmap.size() != this.size()) {
                return false;
            } else {
                float[] afloat = this._values;
                byte[] abyte = this._states;
                float f = this.getNoEntryValue();
                float f1 = tcharfloatmap.getNoEntryValue();
                int i = afloat.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        char c0 = this._set[i];
                        float f2 = tcharfloatmap.get(c0);
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

        this.forEachEntry(new TCharFloatProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(char c0, float f) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(c0);
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
                objectoutput.writeChar(this._set[i]);
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
            char c0 = objectinput.readChar();
            float f = objectinput.readFloat();

            this.put(c0, f);
        }

    }

    class TCharFloatHashIterator extends THashPrimitiveIterator implements TCharFloatIterator {

        TCharFloatHashIterator(TCharFloatHashMap tcharfloathashmap) {
            super(tcharfloathashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public char key() {
            return TCharFloatHashMap.this._set[this._index];
        }

        @Override
        public float value() {
            return TCharFloatHashMap.this._values[this._index];
        }

        @Override
        public float setValue(float f) {
            float f1 = this.value();

            TCharFloatHashMap.this._values[this._index] = f;
            return f1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TCharFloatValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TCharFloatHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharFloatHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TCharFloatKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TCharFloatKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TCharFloatHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TCharFloatHashMap.this.removeAt(this._index);
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
            return TCharFloatHashMap.this.new TCharFloatValueHashIterator(TCharFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TCharFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TCharFloatHashMap.this.containsValue(f);
        }

        @Override
        public float[] toArray() {
            return TCharFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TCharFloatHashMap.this.values(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            float[] afloat = TCharFloatHashMap.this._values;
            char[] achar = TCharFloatHashMap.this._set;
            int i = afloat.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (achar[i] == 0 || achar[i] == 2 || f != afloat[i]);

            TCharFloatHashMap.this.removeAt(i);
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
            } while (TCharFloatHashMap.this.containsValue(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TCharFloatHashMap.this.containsValue(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TCharFloatHashMap.this.containsValue(f)) {
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
            float[] afloat1 = TCharFloatHashMap.this._values;
            byte[] abyte = TCharFloatHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TCharFloatHashMap.this.removeAt(i);
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
            TCharFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TCharFloatHashMap.this.forEachValue(tfloatprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharFloatHashMap.this.forEachValue(new TFloatProcedure() {
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

    protected class TKeyView implements TCharSet {

        protected TKeyView() {}

        @Override
        public TCharIterator iterator() {
            return TCharFloatHashMap.this.new TCharFloatKeyHashIterator(TCharFloatHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharFloatHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TCharFloatHashMap.this.contains(c0);
        }

        @Override
        public char[] toArray() {
            return TCharFloatHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TCharFloatHashMap.this.keys(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            return TCharFloatHashMap.this.no_entry_value != TCharFloatHashMap.this.remove(c0);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            Iterator iterator = collection.iterator();

            char c0;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Character)) {
                    return false;
                }

                c0 = (Character) object;
            } while (TCharFloatHashMap.this.containsKey(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TCharFloatHashMap.this.containsKey(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TCharFloatHashMap.this.contains(c0)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Character> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TCharCollection tcharcollection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(char[] achar) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean flag = false;
            TCharIterator tchariterator = this.iterator();

            while (tchariterator.hasNext()) {
                if (!collection.contains(tchariterator.next())) {
                    tchariterator.remove();
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public boolean retainAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                return false;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = this.iterator();

                while (tchariterator.hasNext()) {
                    if (!tcharcollection.contains(tchariterator.next())) {
                        tchariterator.remove();
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean retainAll(char[] achar) {
            boolean flag = false;

            Arrays.sort(achar);
            char[] achar1 = TCharFloatHashMap.this._set;
            byte[] abyte = TCharFloatHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TCharFloatHashMap.this.removeAt(i);
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

                if (object instanceof Character) {
                    char c0 = (Character) object;

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }
            }

            return flag;
        }

        @Override
        public boolean removeAll(TCharCollection tcharcollection) {
            if (this == tcharcollection) {
                this.clear();
                return true;
            } else {
                boolean flag = false;
                TCharIterator tchariterator = tcharcollection.iterator();

                while (tchariterator.hasNext()) {
                    char c0 = tchariterator.next();

                    if (this.remove(c0)) {
                        flag = true;
                    }
                }

                return flag;
            }
        }

        @Override
        public boolean removeAll(char[] achar) {
            boolean flag = false;
            int i = achar.length;

            while (i-- > 0) {
                if (this.remove(achar[i])) {
                    flag = true;
                }
            }

            return flag;
        }

        @Override
        public void clear() {
            TCharFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TCharFloatHashMap.this.forEachKey(tcharprocedure);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TCharSet)) {
                return false;
            } else {
                TCharSet tcharset = (TCharSet) object;

                if (tcharset.size() != this.size()) {
                    return false;
                } else {
                    int i = TCharFloatHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TCharFloatHashMap.this._states[i] != 1 || tcharset.contains(TCharFloatHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TCharFloatHashMap.this._states.length;

            while (j-- > 0) {
                if (TCharFloatHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TCharFloatHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TCharFloatHashMap.this.forEachKey(new TCharProcedure() {
                private boolean first = true;

                @Override
                public boolean execute(char c0) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(c0);
                    return true;
                }
            });
            stringbuilder.append("}");
            return stringbuilder.toString();
        }
    }
}
