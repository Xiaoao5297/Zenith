package gnu.trove.list.array;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TFloatArrayList implements TFloatList, Externalizable {

    static final long serialVersionUID = 1L;
    protected float[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected float no_entry_value;

    public TFloatArrayList() {
        this(10, 0.0F);
    }

    public TFloatArrayList(int i) {
        this(i, 0.0F);
    }

    public TFloatArrayList(int i, float f) {
        this._data = new float[i];
        this._pos = 0;
        this.no_entry_value = f;
    }

    public TFloatArrayList(TFloatCollection tfloatcollection) {
        this(tfloatcollection.size());
        this.addAll(tfloatcollection);
    }

    public TFloatArrayList(float[] afloat) {
        this(afloat.length);
        this.add(afloat);
    }

    protected TFloatArrayList(float[] afloat, float f, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (afloat == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = afloat;
            this._pos = afloat.length;
            this.no_entry_value = f;
        }
    }

    public static TFloatArrayList wrap(float[] afloat) {
        return wrap(afloat, 0.0F);
    }

    public static TFloatArrayList wrap(float[] afloat, float f) {
        return new TFloatArrayList(afloat, f, true) {
            @Override
            public void ensureCapacity(int i) {
                if (i > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public float getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            float[] afloat = new float[j];

            System.arraycopy(this._data, 0, afloat, 0, this._data.length);
            this._data = afloat;
        }

    }

    @Override
    public int size() {
        return this._pos;
    }

    @Override
    public boolean isEmpty() {
        return this._pos == 0;
    }

    public void trimToSize() {
        if (this._data.length > this.size()) {
            float[] afloat = new float[this.size()];

            this.toArray(afloat, 0, afloat.length);
            this._data = afloat;
        }

    }

    @Override
    public boolean add(float f) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = f;
        return true;
    }

    @Override
    public void add(float[] afloat) {
        this.add(afloat, 0, afloat.length);
    }

    @Override
    public void add(float[] afloat, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(afloat, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, float f) {
        if (i == this._pos) {
            this.add(f);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = f;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, float[] afloat) {
        this.insert(i, afloat, 0, afloat.length);
    }

    @Override
    public void insert(int i, float[] afloat, int j, int k) {
        if (i == this._pos) {
            this.add(afloat, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(afloat, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public float get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public float getQuick(int i) {
        return this._data[i];
    }

    @Override
    public float set(int i, float f) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            float f1 = this._data[i];

            this._data[i] = f;
            return f1;
        }
    }

    @Override
    public float replace(int i, float f) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            float f1 = this._data[i];

            this._data[i] = f;
            return f1;
        }
    }

    @Override
    public void set(int i, float[] afloat) {
        this.set(i, afloat, 0, afloat.length);
    }

    @Override
    public void set(int i, float[] afloat, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(afloat, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, float f) {
        this._data[i] = f;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new float[i];
        this._pos = 0;
    }

    public void reset() {
        this._pos = 0;
        Arrays.fill(this._data, this.no_entry_value);
    }

    public void resetQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(float f) {
        for (int i = 0; i < this._pos; ++i) {
            if (f == this._data[i]) {
                this.remove(i, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public float removeAt(int i) {
        float f = this.get(i);

        this.remove(i, 1);
        return f;
    }

    @Override
    public void remove(int i, int j) {
        if (j != 0) {
            if (i >= 0 && i < this._pos) {
                if (i == 0) {
                    System.arraycopy(this._data, j, this._data, 0, this._pos - j);
                } else if (this._pos - j != i) {
                    System.arraycopy(this._data, i + j, this._data, i, this._pos - (i + j));
                }

                this._pos -= j;
            } else {
                throw new ArrayIndexOutOfBoundsException(i);
            }
        }
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatArrayList.TFloatArrayIterator(0);
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
        } while (this.contains(f));

        return false;
    }

    @Override
    public boolean containsAll(TFloatCollection tfloatcollection) {
        if (this == tfloatcollection) {
            return true;
        } else {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            float f;

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }

                f = tfloatiterator.next();
            } while (this.contains(f));

            return false;
        }
    }

    @Override
    public boolean containsAll(float[] afloat) {
        int i = afloat.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(afloat[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Float ofloat = (Float) iterator.next();
            float f = ofloat;

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = tfloatcollection.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(float[] afloat) {
        boolean flag = false;
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
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
        float[] afloat1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(afloat, afloat1[i]) < 0) {
                this.remove(i, 1);
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
        if (tfloatcollection == this) {
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
    public void transformValues(TFloatFunction tfloatfunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tfloatfunction.execute(this._data[i])) {
            ;
        }

    }

    @Override
    public void reverse() {
        this.reverse(0, this._pos);
    }

    @Override
    public void reverse(int i, int j) {
        if (i != j) {
            if (i > j) {
                throw new IllegalArgumentException("from cannot be greater than to");
            } else {
                int k = i;

                for (int l = j - 1; k < l; --l) {
                    this.swap(k, l);
                    ++k;
                }

            }
        }
    }

    @Override
    public void shuffle(Random random) {
        int i = this._pos;

        while (i-- > 1) {
            this.swap(i, random.nextInt(i));
        }

    }

    private void swap(int i, int j) {
        float f = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = f;
    }

    @Override
    public TFloatList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TFloatArrayList tfloatarraylist = new TFloatArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tfloatarraylist.add(this._data[k]);
            }

            return tfloatarraylist;
        }
    }

    @Override
    public float[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public float[] toArray(int i, int j) {
        float[] afloat = new float[j];

        this.toArray(afloat, i, j);
        return afloat;
    }

    @Override
    public float[] toArray(float[] afloat) {
        int i = afloat.length;

        if (afloat.length > this._pos) {
            i = this._pos;
            afloat[i] = this.no_entry_value;
        }

        this.toArray(afloat, 0, i);
        return afloat;
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j) {
        if (j == 0) {
            return afloat;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, afloat, 0, j);
            return afloat;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j, int k) {
        if (k == 0) {
            return afloat;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, afloat, j, k);
            return afloat;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TFloatArrayList) {
            TFloatArrayList tfloatarraylist = (TFloatArrayList) object;

            if (tfloatarraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tfloatarraylist._data[i]);

                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = 0;

        for (int j = this._pos; j-- > 0; i += HashFunctions.hash(this._data[j])) {
            ;
        }

        return i;
    }

    @Override
    public boolean forEach(TFloatProcedure tfloatprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tfloatprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TFloatProcedure tfloatprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tfloatprocedure.execute(this._data[i]));

        return false;
    }

    @Override
    public void sort() {
        Arrays.sort(this._data, 0, this._pos);
    }

    @Override
    public void sort(int i, int j) {
        Arrays.sort(this._data, i, j);
    }

    @Override
    public void fill(float f) {
        Arrays.fill(this._data, 0, this._pos, f);
    }

    @Override
    public void fill(int i, int j, float f) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, f);
    }

    @Override
    public int binarySearch(float f) {
        return this.binarySearch(f, 0, this._pos);
    }

    @Override
    public int binarySearch(float f, int i, int j) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else if (j > this._pos) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else {
            int k = i;
            int l = j - 1;

            while (k <= l) {
                int i1 = k + l >>> 1;
                float f1 = this._data[i1];

                if (f1 < f) {
                    k = i1 + 1;
                } else {
                    if (f1 <= f) {
                        return i1;
                    }

                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(float f) {
        return this.indexOf(0, f);
    }

    @Override
    public int indexOf(int i, float f) {
        for (int j = i; j < this._pos; ++j) {
            if (this._data[j] == f) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(float f) {
        return this.lastIndexOf(this._pos, f);
    }

    @Override
    public int lastIndexOf(int i, float f) {
        int j = i;

        do {
            if (j-- <= 0) {
                return -1;
            }
        } while (this._data[j] != f);

        return j;
    }

    @Override
    public boolean contains(float f) {
        return this.lastIndexOf(f) >= 0;
    }

    @Override
    public TFloatList grep(TFloatProcedure tfloatprocedure) {
        TFloatArrayList tfloatarraylist = new TFloatArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tfloatprocedure.execute(this._data[i])) {
                tfloatarraylist.add(this._data[i]);
            }
        }

        return tfloatarraylist;
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure tfloatprocedure) {
        TFloatArrayList tfloatarraylist = new TFloatArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tfloatprocedure.execute(this._data[i])) {
                tfloatarraylist.add(this._data[i]);
            }
        }

        return tfloatarraylist;
    }

    @Override
    public float max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            float f = Float.NEGATIVE_INFINITY;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] > f) {
                    f = this._data[i];
                }
            }

            return f;
        }
    }

    @Override
    public float min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            float f = Float.POSITIVE_INFINITY;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] < f) {
                    f = this._data[i];
                }
            }

            return f;
        }
    }

    @Override
    public float sum() {
        float f = 0.0F;

        for (int i = 0; i < this._pos; ++i) {
            f += this._data[i];
        }

        return f;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        int i = 0;

        for (int j = this._pos - 1; i < j; ++i) {
            stringbuilder.append(this._data[i]);
            stringbuilder.append(", ");
        }

        if (this.size() > 0) {
            stringbuilder.append(this._data[this._pos - 1]);
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeInt(this._pos);
        objectoutput.writeFloat(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeFloat(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readFloat();
        int i = objectinput.readInt();

        this._data = new float[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readFloat();
        }

    }

    class TFloatArrayIterator implements TFloatIterator {

        private int cursor = 0;
        int lastRet = -1;

        TFloatArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TFloatArrayList.this.size();
        }

        @Override
        public float next() {
            try {
                float f = TFloatArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return f;
            } catch (IndexOutOfBoundsException indexoutofboundsexception) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            } else {
                try {
                    TFloatArrayList.this.remove(this.lastRet, 1);
                    if (this.lastRet < this.cursor) {
                        --this.cursor;
                    }

                    this.lastRet = -1;
                } catch (IndexOutOfBoundsException indexoutofboundsexception) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }
}
