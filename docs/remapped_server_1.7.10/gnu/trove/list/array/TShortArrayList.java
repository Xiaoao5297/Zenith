package gnu.trove.list.array;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
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

public class TShortArrayList implements TShortList, Externalizable {

    static final long serialVersionUID = 1L;
    protected short[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected short no_entry_value;

    public TShortArrayList() {
        this(10, (short) 0);
    }

    public TShortArrayList(int i) {
        this(i, (short) 0);
    }

    public TShortArrayList(int i, short short0) {
        this._data = new short[i];
        this._pos = 0;
        this.no_entry_value = short0;
    }

    public TShortArrayList(TShortCollection tshortcollection) {
        this(tshortcollection.size());
        this.addAll(tshortcollection);
    }

    public TShortArrayList(short[] ashort) {
        this(ashort.length);
        this.add(ashort);
    }

    protected TShortArrayList(short[] ashort, short short0, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (ashort == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = ashort;
            this._pos = ashort.length;
            this.no_entry_value = short0;
        }
    }

    public static TShortArrayList wrap(short[] ashort) {
        return wrap(ashort, (short) 0);
    }

    public static TShortArrayList wrap(short[] ashort, short short0) {
        return new TShortArrayList(ashort, short0, true) {
            @Override
            public void ensureCapacity(int i) {
                if (i > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public short getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            short[] ashort = new short[j];

            System.arraycopy(this._data, 0, ashort, 0, this._data.length);
            this._data = ashort;
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
            short[] ashort = new short[this.size()];

            this.toArray(ashort, 0, ashort.length);
            this._data = ashort;
        }

    }

    @Override
    public boolean add(short short0) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = short0;
        return true;
    }

    @Override
    public void add(short[] ashort) {
        this.add(ashort, 0, ashort.length);
    }

    @Override
    public void add(short[] ashort, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(ashort, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, short short0) {
        if (i == this._pos) {
            this.add(short0);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = short0;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, short[] ashort) {
        this.insert(i, ashort, 0, ashort.length);
    }

    @Override
    public void insert(int i, short[] ashort, int j, int k) {
        if (i == this._pos) {
            this.add(ashort, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(ashort, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public short get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public short getQuick(int i) {
        return this._data[i];
    }

    @Override
    public short set(int i, short short0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            short short1 = this._data[i];

            this._data[i] = short0;
            return short1;
        }
    }

    @Override
    public short replace(int i, short short0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            short short1 = this._data[i];

            this._data[i] = short0;
            return short1;
        }
    }

    @Override
    public void set(int i, short[] ashort) {
        this.set(i, ashort, 0, ashort.length);
    }

    @Override
    public void set(int i, short[] ashort, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(ashort, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, short short0) {
        this._data[i] = short0;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new short[i];
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
    public boolean remove(short short0) {
        for (int i = 0; i < this._pos; ++i) {
            if (short0 == this._data[i]) {
                this.remove(i, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public short removeAt(int i) {
        short short0 = this.get(i);

        this.remove(i, 1);
        return short0;
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
    public TShortIterator iterator() {
        return new TShortArrayList.TShortArrayIterator(0);
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
        } while (this.contains(short0));

        return false;
    }

    @Override
    public boolean containsAll(TShortCollection tshortcollection) {
        if (this == tshortcollection) {
            return true;
        } else {
            TShortIterator tshortiterator = tshortcollection.iterator();

            short short0;

            do {
                if (!tshortiterator.hasNext()) {
                    return true;
                }

                short0 = tshortiterator.next();
            } while (this.contains(short0));

            return false;
        }
    }

    @Override
    public boolean containsAll(short[] ashort) {
        int i = ashort.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(ashort[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Short oshort = (Short) iterator.next();
            short short0 = oshort;

            if (this.add(short0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TShortCollection tshortcollection) {
        boolean flag = false;
        TShortIterator tshortiterator = tshortcollection.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            if (this.add(short0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(short[] ashort) {
        boolean flag = false;
        short[] ashort1 = ashort;
        int i = ashort.length;

        for (int j = 0; j < i; ++j) {
            short short0 = ashort1[j];

            if (this.add(short0)) {
                flag = true;
            }
        }

        return flag;
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
        short[] ashort1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(ashort, ashort1[i]) < 0) {
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
        if (tshortcollection == this) {
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
    public void transformValues(TShortFunction tshortfunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tshortfunction.execute(this._data[i])) {
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
        short short0 = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = short0;
    }

    @Override
    public TShortList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TShortArrayList tshortarraylist = new TShortArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tshortarraylist.add(this._data[k]);
            }

            return tshortarraylist;
        }
    }

    @Override
    public short[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public short[] toArray(int i, int j) {
        short[] ashort = new short[j];

        this.toArray(ashort, i, j);
        return ashort;
    }

    @Override
    public short[] toArray(short[] ashort) {
        int i = ashort.length;

        if (ashort.length > this._pos) {
            i = this._pos;
            ashort[i] = this.no_entry_value;
        }

        this.toArray(ashort, 0, i);
        return ashort;
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j) {
        if (j == 0) {
            return ashort;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, ashort, 0, j);
            return ashort;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j, int k) {
        if (k == 0) {
            return ashort;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, ashort, j, k);
            return ashort;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TShortArrayList) {
            TShortArrayList tshortarraylist = (TShortArrayList) object;

            if (tshortarraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tshortarraylist._data[i]);

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
    public boolean forEach(TShortProcedure tshortprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tshortprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TShortProcedure tshortprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tshortprocedure.execute(this._data[i]));

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
    public void fill(short short0) {
        Arrays.fill(this._data, 0, this._pos, short0);
    }

    @Override
    public void fill(int i, int j, short short0) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, short0);
    }

    @Override
    public int binarySearch(short short0) {
        return this.binarySearch(short0, 0, this._pos);
    }

    @Override
    public int binarySearch(short short0, int i, int j) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else if (j > this._pos) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else {
            int k = i;
            int l = j - 1;

            while (k <= l) {
                int i1 = k + l >>> 1;
                short short1 = this._data[i1];

                if (short1 < short0) {
                    k = i1 + 1;
                } else {
                    if (short1 <= short0) {
                        return i1;
                    }

                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(short short0) {
        return this.indexOf(0, short0);
    }

    @Override
    public int indexOf(int i, short short0) {
        for (int j = i; j < this._pos; ++j) {
            if (this._data[j] == short0) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(short short0) {
        return this.lastIndexOf(this._pos, short0);
    }

    @Override
    public int lastIndexOf(int i, short short0) {
        int j = i;

        do {
            if (j-- <= 0) {
                return -1;
            }
        } while (this._data[j] != short0);

        return j;
    }

    @Override
    public boolean contains(short short0) {
        return this.lastIndexOf(short0) >= 0;
    }

    @Override
    public TShortList grep(TShortProcedure tshortprocedure) {
        TShortArrayList tshortarraylist = new TShortArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tshortprocedure.execute(this._data[i])) {
                tshortarraylist.add(this._data[i]);
            }
        }

        return tshortarraylist;
    }

    @Override
    public TShortList inverseGrep(TShortProcedure tshortprocedure) {
        TShortArrayList tshortarraylist = new TShortArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tshortprocedure.execute(this._data[i])) {
                tshortarraylist.add(this._data[i]);
            }
        }

        return tshortarraylist;
    }

    @Override
    public short max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            short short0 = Short.MIN_VALUE;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] > short0) {
                    short0 = this._data[i];
                }
            }

            return short0;
        }
    }

    @Override
    public short min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            short short0 = 32767;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] < short0) {
                    short0 = this._data[i];
                }
            }

            return short0;
        }
    }

    @Override
    public short sum() {
        short short0 = 0;

        for (int i = 0; i < this._pos; ++i) {
            short0 += this._data[i];
        }

        return short0;
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
        objectoutput.writeShort(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeShort(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readShort();
        int i = objectinput.readInt();

        this._data = new short[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readShort();
        }

    }

    class TShortArrayIterator implements TShortIterator {

        private int cursor = 0;
        int lastRet = -1;

        TShortArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TShortArrayList.this.size();
        }

        @Override
        public short next() {
            try {
                short short0 = TShortArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return short0;
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
                    TShortArrayList.this.remove(this.lastRet, 1);
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
