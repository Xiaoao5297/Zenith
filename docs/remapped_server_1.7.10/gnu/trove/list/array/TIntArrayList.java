package gnu.trove.list.array;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
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

public class TIntArrayList implements TIntList, Externalizable {

    static final long serialVersionUID = 1L;
    protected int[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected int no_entry_value;

    public TIntArrayList() {
        this(10, 0);
    }

    public TIntArrayList(int i) {
        this(i, 0);
    }

    public TIntArrayList(int i, int j) {
        this._data = new int[i];
        this._pos = 0;
        this.no_entry_value = j;
    }

    public TIntArrayList(TIntCollection tintcollection) {
        this(tintcollection.size());
        this.addAll(tintcollection);
    }

    public TIntArrayList(int[] aint) {
        this(aint.length);
        this.add(aint);
    }

    protected TIntArrayList(int[] aint, int i, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (aint == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = aint;
            this._pos = aint.length;
            this.no_entry_value = i;
        }
    }

    public static TIntArrayList wrap(int[] aint) {
        return wrap(aint, 0);
    }

    public static TIntArrayList wrap(int[] aint, int i) {
        return new TIntArrayList(aint, i, true) {
            @Override
            public void ensureCapacity(int j) {
                if (j > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public int getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            int[] aint = new int[j];

            System.arraycopy(this._data, 0, aint, 0, this._data.length);
            this._data = aint;
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
            int[] aint = new int[this.size()];

            this.toArray(aint, 0, aint.length);
            this._data = aint;
        }

    }

    @Override
    public boolean add(int i) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = i;
        return true;
    }

    @Override
    public void add(int[] aint) {
        this.add(aint, 0, aint.length);
    }

    @Override
    public void add(int[] aint, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(aint, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, int j) {
        if (i == this._pos) {
            this.add(j);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = j;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, int[] aint) {
        this.insert(i, aint, 0, aint.length);
    }

    @Override
    public void insert(int i, int[] aint, int j, int k) {
        if (i == this._pos) {
            this.add(aint, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(aint, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public int get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public int getQuick(int i) {
        return this._data[i];
    }

    @Override
    public int set(int i, int j) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            int k = this._data[i];

            this._data[i] = j;
            return k;
        }
    }

    @Override
    public int replace(int i, int j) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            int k = this._data[i];

            this._data[i] = j;
            return k;
        }
    }

    @Override
    public void set(int i, int[] aint) {
        this.set(i, aint, 0, aint.length);
    }

    @Override
    public void set(int i, int[] aint, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(aint, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, int j) {
        this._data[i] = j;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new int[i];
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
    public boolean remove(int i) {
        for (int j = 0; j < this._pos; ++j) {
            if (i == this._data[j]) {
                this.remove(j, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public int removeAt(int i) {
        int j = this.get(i);

        this.remove(i, 1);
        return j;
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
    public TIntIterator iterator() {
        return new TIntArrayList.TIntArrayIterator(0);
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
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(TIntCollection tintcollection) {
        if (this == tintcollection) {
            return true;
        } else {
            TIntIterator tintiterator = tintcollection.iterator();

            int i;

            do {
                if (!tintiterator.hasNext()) {
                    return true;
                }

                i = tintiterator.next();
            } while (this.contains(i));

            return false;
        }
    }

    @Override
    public boolean containsAll(int[] aint) {
        int i = aint.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(aint[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            int i = integer;

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TIntCollection tintcollection) {
        boolean flag = false;
        TIntIterator tintiterator = tintcollection.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(int[] aint) {
        boolean flag = false;
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            if (this.add(k)) {
                flag = true;
            }
        }

        return flag;
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
        int[] aint1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(aint, aint1[i]) < 0) {
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
        if (tintcollection == this) {
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
    public void transformValues(TIntFunction tintfunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tintfunction.execute(this._data[i])) {
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
        int k = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = k;
    }

    @Override
    public TIntList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TIntArrayList tintarraylist = new TIntArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tintarraylist.add(this._data[k]);
            }

            return tintarraylist;
        }
    }

    @Override
    public int[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public int[] toArray(int i, int j) {
        int[] aint = new int[j];

        this.toArray(aint, i, j);
        return aint;
    }

    @Override
    public int[] toArray(int[] aint) {
        int i = aint.length;

        if (aint.length > this._pos) {
            i = this._pos;
            aint[i] = this.no_entry_value;
        }

        this.toArray(aint, 0, i);
        return aint;
    }

    @Override
    public int[] toArray(int[] aint, int i, int j) {
        if (j == 0) {
            return aint;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, aint, 0, j);
            return aint;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public int[] toArray(int[] aint, int i, int j, int k) {
        if (k == 0) {
            return aint;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, aint, j, k);
            return aint;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TIntArrayList) {
            TIntArrayList tintarraylist = (TIntArrayList) object;

            if (tintarraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tintarraylist._data[i]);

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
    public boolean forEach(TIntProcedure tintprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tintprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TIntProcedure tintprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tintprocedure.execute(this._data[i]));

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
    public void fill(int i) {
        Arrays.fill(this._data, 0, this._pos, i);
    }

    @Override
    public void fill(int i, int j, int k) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, k);
    }

    @Override
    public int binarySearch(int i) {
        return this.binarySearch(i, 0, this._pos);
    }

    @Override
    public int binarySearch(int i, int j, int k) {
        if (j < 0) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else if (k > this._pos) {
            throw new ArrayIndexOutOfBoundsException(k);
        } else {
            int l = j;
            int i1 = k - 1;

            while (l <= i1) {
                int j1 = l + i1 >>> 1;
                int k1 = this._data[j1];

                if (k1 < i) {
                    l = j1 + 1;
                } else {
                    if (k1 <= i) {
                        return j1;
                    }

                    i1 = j1 - 1;
                }
            }

            return -(l + 1);
        }
    }

    @Override
    public int indexOf(int i) {
        return this.indexOf(0, i);
    }

    @Override
    public int indexOf(int i, int j) {
        for (int k = i; k < this._pos; ++k) {
            if (this._data[k] == j) {
                return k;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int i) {
        return this.lastIndexOf(this._pos, i);
    }

    @Override
    public int lastIndexOf(int i, int j) {
        int k = i;

        do {
            if (k-- <= 0) {
                return -1;
            }
        } while (this._data[k] != j);

        return k;
    }

    @Override
    public boolean contains(int i) {
        return this.lastIndexOf(i) >= 0;
    }

    @Override
    public TIntList grep(TIntProcedure tintprocedure) {
        TIntArrayList tintarraylist = new TIntArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tintprocedure.execute(this._data[i])) {
                tintarraylist.add(this._data[i]);
            }
        }

        return tintarraylist;
    }

    @Override
    public TIntList inverseGrep(TIntProcedure tintprocedure) {
        TIntArrayList tintarraylist = new TIntArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tintprocedure.execute(this._data[i])) {
                tintarraylist.add(this._data[i]);
            }
        }

        return tintarraylist;
    }

    @Override
    public int max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            int i = Integer.MIN_VALUE;

            for (int j = 0; j < this._pos; ++j) {
                if (this._data[j] > i) {
                    i = this._data[j];
                }
            }

            return i;
        }
    }

    @Override
    public int min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            int i = Integer.MAX_VALUE;

            for (int j = 0; j < this._pos; ++j) {
                if (this._data[j] < i) {
                    i = this._data[j];
                }
            }

            return i;
        }
    }

    @Override
    public int sum() {
        int i = 0;

        for (int j = 0; j < this._pos; ++j) {
            i += this._data[j];
        }

        return i;
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
        objectoutput.writeInt(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeInt(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readInt();
        int i = objectinput.readInt();

        this._data = new int[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readInt();
        }

    }

    class TIntArrayIterator implements TIntIterator {

        private int cursor = 0;
        int lastRet = -1;

        TIntArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TIntArrayList.this.size();
        }

        @Override
        public int next() {
            try {
                int i = TIntArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return i;
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
                    TIntArrayList.this.remove(this.lastRet, 1);
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
