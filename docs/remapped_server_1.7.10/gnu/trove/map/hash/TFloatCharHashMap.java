package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
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

public class TFloatCharHashMap extends TFloatCharHash implements TFloatCharMap, Externalizable {

    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TFloatCharHashMap() {}

    public TFloatCharHashMap(int i) {
        super(i);
    }

    public TFloatCharHashMap(int i, float f) {
        super(i, f);
    }

    public TFloatCharHashMap(int i, float f, float f1, char c0) {
        super(i, f, f1, c0);
    }

    public TFloatCharHashMap(float[] afloat, char[] achar) {
        super(Math.max(afloat.length, achar.length));
        int i = Math.min(afloat.length, achar.length);

        for (int j = 0; j < i; ++j) {
            this.put(afloat[j], achar[j]);
        }

    }

    public TFloatCharHashMap(TFloatCharMap tfloatcharmap) {
        super(tfloatcharmap.size());
        if (tfloatcharmap instanceof TFloatCharHashMap) {
            TFloatCharHashMap tfloatcharhashmap = (TFloatCharHashMap) tfloatcharmap;

            this._loadFactor = tfloatcharhashmap._loadFactor;
            this.no_entry_key = tfloatcharhashmap.no_entry_key;
            this.no_entry_value = tfloatcharhashmap.no_entry_value;
            if (this.no_entry_key != 0.0F) {
                Arrays.fill(this._set, this.no_entry_key);
            }

            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }

            this.setUp((int) Math.ceil((double) (10.0F / this._loadFactor)));
        }

        this.putAll(tfloatcharmap);
    }

    @Override
    protected int setUp(int i) {
        int j = super.setUp(i);

        this._values = new char[j];
        return j;
    }

    @Override
    protected void rehash(int i) {
        int j = this._set.length;
        float[] afloat = this._set;
        char[] achar = this._values;
        byte[] abyte = this._states;

        this._set = new float[i];
        this._values = new char[i];
        this._states = new byte[i];
        int k = j;

        while (k-- > 0) {
            if (abyte[k] == 1) {
                float f = afloat[k];
                int l = this.insertKey(f);

                this._values[l] = achar[k];
            }
        }

    }

    @Override
    public char put(float f, char c0) {
        int i = this.insertKey(f);

        return this.doPut(f, c0, i);
    }

    @Override
    public char putIfAbsent(float f, char c0) {
        int i = this.insertKey(f);

        return i < 0 ? this._values[-i - 1] : this.doPut(f, c0, i);
    }

    private char doPut(float f, char c0, int i) {
        char c1 = this.no_entry_value;
        boolean flag = true;

        if (i < 0) {
            i = -i - 1;
            c1 = this._values[i];
            flag = false;
        }

        this._values[i] = c0;
        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c1;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Character> map) {
        this.ensureCapacity(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            this.put((Float) entry.getKey(), (Character) entry.getValue());
        }

    }

    @Override
    public void putAll(TFloatCharMap tfloatcharmap) {
        this.ensureCapacity(tfloatcharmap.size());
        TFloatCharIterator tfloatchariterator = tfloatcharmap.iterator();

        while (tfloatchariterator.hasNext()) {
            tfloatchariterator.advance();
            this.put(tfloatchariterator.key(), tfloatchariterator.value());
        }

    }

    @Override
    public char get(float f) {
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
    public char remove(float f) {
        char c0 = this.no_entry_value;
        int i = this.index(f);

        if (i >= 0) {
            c0 = this._values[i];
            this.removeAt(i);
        }

        return c0;
    }

    @Override
    protected void removeAt(int i) {
        this._values[i] = this.no_entry_value;
        super.removeAt(i);
    }

    @Override
    public TFloatSet keySet() {
        return new TFloatCharHashMap.TKeyView();
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
    public TCharCollection valueCollection() {
        return new TFloatCharHashMap.TValueView();
    }

    @Override
    public char[] values() {
        char[] achar = new char[this.size()];
        char[] achar1 = this._values;
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
    public char[] values(char[] achar) {
        int i = this.size();

        if (achar.length < i) {
            achar = new char[i];
        }

        char[] achar1 = this._values;
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
    public boolean containsValue(char c0) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return false;
            }
        } while (abyte[i] != 1 || c0 != achar[i]);

        return true;
    }

    @Override
    public boolean containsKey(float f) {
        return this.contains(f);
    }

    @Override
    public TFloatCharIterator iterator() {
        return new TFloatCharHashMap.TFloatCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure tfloatprocedure) {
        return this.forEach(tfloatprocedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure tcharprocedure) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tcharprocedure.execute(achar[i]));

        return false;
    }

    @Override
    public boolean forEachEntry(TFloatCharProcedure tfloatcharprocedure) {
        byte[] abyte = this._states;
        float[] afloat = this._set;
        char[] achar = this._values;
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (abyte[i] != 1 || tfloatcharprocedure.execute(afloat[i], achar[i]));

        return false;
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        byte[] abyte = this._states;
        char[] achar = this._values;
        int i = achar.length;

        while (i-- > 0) {
            if (abyte[i] == 1) {
                achar[i] = tcharfunction.execute(achar[i]);
            }
        }

    }

    @Override
    public boolean retainEntries(TFloatCharProcedure tfloatcharprocedure) {
        boolean flag = false;
        byte[] abyte = this._states;
        float[] afloat = this._set;
        char[] achar = this._values;

        this.tempDisableAutoCompaction();

        try {
            int i = afloat.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && !tfloatcharprocedure.execute(afloat[i], achar[i])) {
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
        return this.adjustValue(f, '\u0001');
    }

    @Override
    public boolean adjustValue(float f, char c0) {
        int i = this.index(f);

        if (i < 0) {
            return false;
        } else {
            this._values[i] += c0;
            return true;
        }
    }

    @Override
    public char adjustOrPutValue(float f, char c0, char c1) {
        int i = this.insertKey(f);
        char c2;
        boolean flag;

        if (i < 0) {
            i = -i - 1;
            c2 = this._values[i] += c0;
            flag = false;
        } else {
            c2 = this._values[i] = c1;
            flag = true;
        }

        byte b0 = this._states[i];

        if (flag) {
            this.postInsertHook(this.consumeFreeSlot);
        }

        return c2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TFloatCharMap)) {
            return false;
        } else {
            TFloatCharMap tfloatcharmap = (TFloatCharMap) object;

            if (tfloatcharmap.size() != this.size()) {
                return false;
            } else {
                char[] achar = this._values;
                byte[] abyte = this._states;
                char c0 = this.getNoEntryValue();
                char c1 = tfloatcharmap.getNoEntryValue();
                int i = achar.length;

                while (i-- > 0) {
                    if (abyte[i] == 1) {
                        float f = this._set[i];
                        char c2 = tfloatcharmap.get(f);
                        char c3 = achar[i];

                        if (c3 != c2 && c3 != c0 && c2 != c1) {
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

        this.forEachEntry(new TFloatCharProcedure() {
            private boolean first = true;

            @Override
            public boolean execute(float f, char c0) {
                if (this.first) {
                    this.first = false;
                } else {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(f);
                stringbuilder.append("=");
                stringbuilder.append(c0);
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
                objectoutput.writeChar(this._values[i]);
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
            char c0 = objectinput.readChar();

            this.put(f, c0);
        }

    }

    class TFloatCharHashIterator extends THashPrimitiveIterator implements TFloatCharIterator {

        TFloatCharHashIterator(TFloatCharHashMap tfloatcharhashmap) {
            super(tfloatcharhashmap);
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
            return TFloatCharHashMap.this._set[this._index];
        }

        @Override
        public char value() {
            return TFloatCharHashMap.this._values[this._index];
        }

        @Override
        public char setValue(char c0) {
            char c1 = this.value();

            TFloatCharHashMap.this._values[this._index] = c0;
            return c1;
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {

        TFloatCharValueHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public char next() {
            this.moveToNextIndex();
            return TFloatCharHashMap.this._values[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    class TFloatCharKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        TFloatCharKeyHashIterator(TPrimitiveHash tprimitivehash) {
            super(tprimitivehash);
        }

        @Override
        public float next() {
            this.moveToNextIndex();
            return TFloatCharHashMap.this._set[this._index];
        }

        @Override
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    this._hash.tempDisableAutoCompaction();
                    TFloatCharHashMap.this.removeAt(this._index);
                } finally {
                    this._hash.reenableAutoCompaction(false);
                }

                --this._expectedSize;
            }
        }
    }

    protected class TValueView implements TCharCollection {

        protected TValueView() {}

        @Override
        public TCharIterator iterator() {
            return TFloatCharHashMap.this.new TFloatCharValueHashIterator(TFloatCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TFloatCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TFloatCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatCharHashMap.this._size;
        }

        @Override
        public boolean contains(char c0) {
            return TFloatCharHashMap.this.containsValue(c0);
        }

        @Override
        public char[] toArray() {
            return TFloatCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] achar) {
            return TFloatCharHashMap.this.values(achar);
        }

        @Override
        public boolean add(char c0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char c0) {
            char[] achar = TFloatCharHashMap.this._values;
            float[] afloat = TFloatCharHashMap.this._set;
            int i = achar.length;

            do {
                if (i-- <= 0) {
                    return false;
                }
            } while (afloat[i] == 0.0F || afloat[i] == 2.0F || c0 != achar[i]);

            TFloatCharHashMap.this.removeAt(i);
            return true;
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
            } while (TFloatCharHashMap.this.containsValue(c0));

            return false;
        }

        @Override
        public boolean containsAll(TCharCollection tcharcollection) {
            TCharIterator tchariterator = tcharcollection.iterator();

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }
            } while (TFloatCharHashMap.this.containsValue(tchariterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(char[] achar) {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!TFloatCharHashMap.this.containsValue(c0)) {
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
            char[] achar1 = TFloatCharHashMap.this._values;
            byte[] abyte = TFloatCharHashMap.this._states;
            int i = achar1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(achar, achar1[i]) < 0) {
                    TFloatCharHashMap.this.removeAt(i);
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
            TFloatCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure tcharprocedure) {
            return TFloatCharHashMap.this.forEachValue(tcharprocedure);
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatCharHashMap.this.forEachValue(new TCharProcedure() {
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

    protected class TKeyView implements TFloatSet {

        protected TKeyView() {}

        @Override
        public TFloatIterator iterator() {
            return TFloatCharHashMap.this.new TFloatCharKeyHashIterator(TFloatCharHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatCharHashMap.this._size;
        }

        @Override
        public boolean contains(float f) {
            return TFloatCharHashMap.this.contains(f);
        }

        @Override
        public float[] toArray() {
            return TFloatCharHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] afloat) {
            return TFloatCharHashMap.this.keys(afloat);
        }

        @Override
        public boolean add(float f) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float f) {
            return TFloatCharHashMap.this.no_entry_value != TFloatCharHashMap.this.remove(f);
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
            } while (TFloatCharHashMap.this.containsKey(f));

            return false;
        }

        @Override
        public boolean containsAll(TFloatCollection tfloatcollection) {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }
            } while (TFloatCharHashMap.this.containsKey(tfloatiterator.next()));

            return false;
        }

        @Override
        public boolean containsAll(float[] afloat) {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!TFloatCharHashMap.this.contains(f)) {
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
            float[] afloat1 = TFloatCharHashMap.this._set;
            byte[] abyte = TFloatCharHashMap.this._states;
            int i = afloat1.length;

            while (i-- > 0) {
                if (abyte[i] == 1 && Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                    TFloatCharHashMap.this.removeAt(i);
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
            TFloatCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure tfloatprocedure) {
            return TFloatCharHashMap.this.forEachKey(tfloatprocedure);
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
                    int i = TFloatCharHashMap.this._states.length;

                    do {
                        if (i-- <= 0) {
                            return true;
                        }
                    } while (TFloatCharHashMap.this._states[i] != 1 || tfloatset.contains(TFloatCharHashMap.this._set[i]));

                    return false;
                }
            }
        }

        @Override
        public int hashCode() {
            int i = 0;
            int j = TFloatCharHashMap.this._states.length;

            while (j-- > 0) {
                if (TFloatCharHashMap.this._states[j] == 1) {
                    i += HashFunctions.hash(TFloatCharHashMap.this._set[j]);
                }
            }

            return i;
        }

        public String toString() {
            final StringBuilder stringbuilder = new StringBuilder("{");

            TFloatCharHashMap.this.forEachKey(new TFloatProcedure() {
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
