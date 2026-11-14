package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
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

public class TLongArrayList implements TLongList, Externalizable {

    static final long serialVersionUID = 1L;
    protected long[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected long no_entry_value;

    public TLongArrayList() {
        this(10, 0L);
    }

    public TLongArrayList(int i) {
        this(i, 0L);
    }

    public TLongArrayList(int i, long j) {
        this._data = new long[i];
        this._pos = 0;
        this.no_entry_value = j;
    }

    public TLongArrayList(TLongCollection tlongcollection) {
        this(tlongcollection.size());
        this.addAll(tlongcollection);
    }

    public TLongArrayList(long[] along) {
        this(along.length);
        this.add(along);
    }

    protected TLongArrayList(long[] along, long i, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (along == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = along;
            this._pos = along.length;
            this.no_entry_value = i;
        }
    }

    public static TLongArrayList wrap(long[] along) {
        return wrap(along, 0L);
    }

    public static TLongArrayList wrap(long[] along, long i) {
        return new TLongArrayList(along, i, true) {
            @Override
            public void ensureCapacity(int j) {
                if (j > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            long[] along = new long[j];

            System.arraycopy(this._data, 0, along, 0, this._data.length);
            this._data = along;
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
            long[] along = new long[this.size()];

            this.toArray(along, 0, along.length);
            this._data = along;
        }

    }

    @Override
    public boolean add(long i) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = i;
        return true;
    }

    @Override
    public void add(long[] along) {
        this.add(along, 0, along.length);
    }

    @Override
    public void add(long[] along, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(along, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, long j) {
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
    public void insert(int i, long[] along) {
        this.insert(i, along, 0, along.length);
    }

    @Override
    public void insert(int i, long[] along, int j, int k) {
        if (i == this._pos) {
            this.add(along, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(along, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public long get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public long getQuick(int i) {
        return this._data[i];
    }

    @Override
    public long set(int i, long j) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            long k = this._data[i];

            this._data[i] = j;
            return k;
        }
    }

    @Override
    public long replace(int i, long j) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            long k = this._data[i];

            this._data[i] = j;
            return k;
        }
    }

    @Override
    public void set(int i, long[] along) {
        this.set(i, along, 0, along.length);
    }

    @Override
    public void set(int i, long[] along, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(along, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, long j) {
        this._data[i] = j;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new long[i];
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
    public boolean remove(long i) {
        for (int j = 0; j < this._pos; ++j) {
            if (i == this._data[j]) {
                this.remove(j, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public long removeAt(int i) {
        long j = this.get(i);

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
    public TLongIterator iterator() {
        return new TLongArrayList.TLongArrayIterator(0);
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
        } while (this.contains(i));

        return false;
    }

    @Override
    public boolean containsAll(TLongCollection tlongcollection) {
        if (this == tlongcollection) {
            return true;
        } else {
            TLongIterator tlongiterator = tlongcollection.iterator();

            long i;

            do {
                if (!tlongiterator.hasNext()) {
                    return true;
                }

                i = tlongiterator.next();
            } while (this.contains(i));

            return false;
        }
    }

    @Override
    public boolean containsAll(long[] along) {
        int i = along.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(along[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Long olong = (Long) iterator.next();
            long i = olong;

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TLongCollection tlongcollection) {
        boolean flag = false;
        TLongIterator tlongiterator = tlongcollection.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            if (this.add(i)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(long[] along) {
        boolean flag = false;
        long[] along1 = along;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along1[j];

            if (this.add(k)) {
                flag = true;
            }
        }

        return flag;
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
        long[] along1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(along, along1[i]) < 0) {
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
        if (tlongcollection == this) {
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
    public void transformValues(TLongFunction tlongfunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tlongfunction.execute(this._data[i])) {
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
        long k = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = k;
    }

    @Override
    public TLongList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TLongArrayList tlongarraylist = new TLongArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tlongarraylist.add(this._data[k]);
            }

            return tlongarraylist;
        }
    }

    @Override
    public long[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public long[] toArray(int i, int j) {
        long[] along = new long[j];

        this.toArray(along, i, j);
        return along;
    }

    @Override
    public long[] toArray(long[] along) {
        int i = along.length;

        if (along.length > this._pos) {
            i = this._pos;
            along[i] = this.no_entry_value;
        }

        this.toArray(along, 0, i);
        return along;
    }

    @Override
    public long[] toArray(long[] along, int i, int j) {
        if (j == 0) {
            return along;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, along, 0, j);
            return along;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public long[] toArray(long[] along, int i, int j, int k) {
        if (k == 0) {
            return along;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, along, j, k);
            return along;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TLongArrayList) {
            TLongArrayList tlongarraylist = (TLongArrayList) object;

            if (tlongarraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tlongarraylist._data[i]);

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
    public boolean forEach(TLongProcedure tlongprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tlongprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TLongProcedure tlongprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tlongprocedure.execute(this._data[i]));

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
    public void fill(long i) {
        Arrays.fill(this._data, 0, this._pos, i);
    }

    @Override
    public void fill(int i, int j, long k) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, k);
    }

    @Override
    public int binarySearch(long i) {
        return this.binarySearch(i, 0, this._pos);
    }

    @Override
    public int binarySearch(long i, int j, int k) {
        if (j < 0) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else if (k > this._pos) {
            throw new ArrayIndexOutOfBoundsException(k);
        } else {
            int l = j;
            int i1 = k - 1;

            while (l <= i1) {
                int j1 = l + i1 >>> 1;
                long k1 = this._data[j1];

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
    public int indexOf(long i) {
        return this.indexOf(0, i);
    }

    @Override
    public int indexOf(int i, long j) {
        for (int k = i; k < this._pos; ++k) {
            if (this._data[k] == j) {
                return k;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(long i) {
        return this.lastIndexOf(this._pos, i);
    }

    @Override
    public int lastIndexOf(int i, long j) {
        int k = i;

        do {
            if (k-- <= 0) {
                return -1;
            }
        } while (this._data[k] != j);

        return k;
    }

    @Override
    public boolean contains(long i) {
        return this.lastIndexOf(i) >= 0;
    }

    @Override
    public TLongList grep(TLongProcedure tlongprocedure) {
        TLongArrayList tlongarraylist = new TLongArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tlongprocedure.execute(this._data[i])) {
                tlongarraylist.add(this._data[i]);
            }
        }

        return tlongarraylist;
    }

    @Override
    public TLongList inverseGrep(TLongProcedure tlongprocedure) {
        TLongArrayList tlongarraylist = new TLongArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tlongprocedure.execute(this._data[i])) {
                tlongarraylist.add(this._data[i]);
            }
        }

        return tlongarraylist;
    }

    @Override
    public long max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            long i = Long.MIN_VALUE;

            for (int j = 0; j < this._pos; ++j) {
                if (this._data[j] > i) {
                    i = this._data[j];
                }
            }

            return i;
        }
    }

    @Override
    public long min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            long i = Long.MAX_VALUE;

            for (int j = 0; j < this._pos; ++j) {
                if (this._data[j] < i) {
                    i = this._data[j];
                }
            }

            return i;
        }
    }

    @Override
    public long sum() {
        long i = 0L;

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
        objectoutput.writeLong(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeLong(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readLong();
        int i = objectinput.readInt();

        this._data = new long[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readLong();
        }

    }

    class TLongArrayIterator implements TLongIterator {

        private int cursor = 0;
        int lastRet = -1;

        TLongArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TLongArrayList.this.size();
        }

        @Override
        public long next() {
            try {
                long i = TLongArrayList.this.get(this.cursor);

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
                    TLongArrayList.this.remove(this.lastRet, 1);
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
