package gnu.trove.list.array;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
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

public class TByteArrayList implements TByteList, Externalizable {

    static final long serialVersionUID = 1L;
    protected byte[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected byte no_entry_value;

    public TByteArrayList() {
        this(10, (byte) 0);
    }

    public TByteArrayList(int i) {
        this(i, (byte) 0);
    }

    public TByteArrayList(int i, byte b0) {
        this._data = new byte[i];
        this._pos = 0;
        this.no_entry_value = b0;
    }

    public TByteArrayList(TByteCollection tbytecollection) {
        this(tbytecollection.size());
        this.addAll(tbytecollection);
    }

    public TByteArrayList(byte[] abyte) {
        this(abyte.length);
        this.add(abyte);
    }

    protected TByteArrayList(byte[] abyte, byte b0, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (abyte == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = abyte;
            this._pos = abyte.length;
            this.no_entry_value = b0;
        }
    }

    public static TByteArrayList wrap(byte[] abyte) {
        return wrap(abyte, (byte) 0);
    }

    public static TByteArrayList wrap(byte[] abyte, byte b0) {
        return new TByteArrayList(abyte, b0, true) {
            @Override
            public void ensureCapacity(int i) {
                if (i > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public byte getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            byte[] abyte = new byte[j];

            System.arraycopy(this._data, 0, abyte, 0, this._data.length);
            this._data = abyte;
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
            byte[] abyte = new byte[this.size()];

            this.toArray(abyte, 0, abyte.length);
            this._data = abyte;
        }

    }

    @Override
    public boolean add(byte b0) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = b0;
        return true;
    }

    @Override
    public void add(byte[] abyte) {
        this.add(abyte, 0, abyte.length);
    }

    @Override
    public void add(byte[] abyte, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(abyte, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, byte b0) {
        if (i == this._pos) {
            this.add(b0);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = b0;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, byte[] abyte) {
        this.insert(i, abyte, 0, abyte.length);
    }

    @Override
    public void insert(int i, byte[] abyte, int j, int k) {
        if (i == this._pos) {
            this.add(abyte, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(abyte, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public byte get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public byte getQuick(int i) {
        return this._data[i];
    }

    @Override
    public byte set(int i, byte b0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            byte b1 = this._data[i];

            this._data[i] = b0;
            return b1;
        }
    }

    @Override
    public byte replace(int i, byte b0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            byte b1 = this._data[i];

            this._data[i] = b0;
            return b1;
        }
    }

    @Override
    public void set(int i, byte[] abyte) {
        this.set(i, abyte, 0, abyte.length);
    }

    @Override
    public void set(int i, byte[] abyte, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(abyte, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, byte b0) {
        this._data[i] = b0;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new byte[i];
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
    public boolean remove(byte b0) {
        for (int i = 0; i < this._pos; ++i) {
            if (b0 == this._data[i]) {
                this.remove(i, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public byte removeAt(int i) {
        byte b0 = this.get(i);

        this.remove(i, 1);
        return b0;
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
    public TByteIterator iterator() {
        return new TByteArrayList.TByteArrayIterator(0);
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
        } while (this.contains(b0));

        return false;
    }

    @Override
    public boolean containsAll(TByteCollection tbytecollection) {
        if (this == tbytecollection) {
            return true;
        } else {
            TByteIterator tbyteiterator = tbytecollection.iterator();

            byte b0;

            do {
                if (!tbyteiterator.hasNext()) {
                    return true;
                }

                b0 = tbyteiterator.next();
            } while (this.contains(b0));

            return false;
        }
    }

    @Override
    public boolean containsAll(byte[] abyte) {
        int i = abyte.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(abyte[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Byte obyte = (Byte) iterator.next();
            byte b0 = obyte;

            if (this.add(b0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TByteCollection tbytecollection) {
        boolean flag = false;
        TByteIterator tbyteiterator = tbytecollection.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            if (this.add(b0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(byte[] abyte) {
        boolean flag = false;
        byte[] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte b0 = abyte1[j];

            if (this.add(b0)) {
                flag = true;
            }
        }

        return flag;
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
        byte[] abyte1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(abyte, abyte1[i]) < 0) {
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
        if (tbytecollection == this) {
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
    public void transformValues(TByteFunction tbytefunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tbytefunction.execute(this._data[i])) {
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
        byte b0 = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = b0;
    }

    @Override
    public TByteList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TByteArrayList tbytearraylist = new TByteArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tbytearraylist.add(this._data[k]);
            }

            return tbytearraylist;
        }
    }

    @Override
    public byte[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public byte[] toArray(int i, int j) {
        byte[] abyte = new byte[j];

        this.toArray(abyte, i, j);
        return abyte;
    }

    @Override
    public byte[] toArray(byte[] abyte) {
        int i = abyte.length;

        if (abyte.length > this._pos) {
            i = this._pos;
            abyte[i] = this.no_entry_value;
        }

        this.toArray(abyte, 0, i);
        return abyte;
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j) {
        if (j == 0) {
            return abyte;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, abyte, 0, j);
            return abyte;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j, int k) {
        if (k == 0) {
            return abyte;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, abyte, j, k);
            return abyte;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TByteArrayList) {
            TByteArrayList tbytearraylist = (TByteArrayList) object;

            if (tbytearraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tbytearraylist._data[i]);

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
    public boolean forEach(TByteProcedure tbyteprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tbyteprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TByteProcedure tbyteprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tbyteprocedure.execute(this._data[i]));

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
    public void fill(byte b0) {
        Arrays.fill(this._data, 0, this._pos, b0);
    }

    @Override
    public void fill(int i, int j, byte b0) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, b0);
    }

    @Override
    public int binarySearch(byte b0) {
        return this.binarySearch(b0, 0, this._pos);
    }

    @Override
    public int binarySearch(byte b0, int i, int j) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else if (j > this._pos) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else {
            int k = i;
            int l = j - 1;

            while (k <= l) {
                int i1 = k + l >>> 1;
                byte b1 = this._data[i1];

                if (b1 < b0) {
                    k = i1 + 1;
                } else {
                    if (b1 <= b0) {
                        return i1;
                    }

                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(byte b0) {
        return this.indexOf(0, b0);
    }

    @Override
    public int indexOf(int i, byte b0) {
        for (int j = i; j < this._pos; ++j) {
            if (this._data[j] == b0) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(byte b0) {
        return this.lastIndexOf(this._pos, b0);
    }

    @Override
    public int lastIndexOf(int i, byte b0) {
        int j = i;

        do {
            if (j-- <= 0) {
                return -1;
            }
        } while (this._data[j] != b0);

        return j;
    }

    @Override
    public boolean contains(byte b0) {
        return this.lastIndexOf(b0) >= 0;
    }

    @Override
    public TByteList grep(TByteProcedure tbyteprocedure) {
        TByteArrayList tbytearraylist = new TByteArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tbyteprocedure.execute(this._data[i])) {
                tbytearraylist.add(this._data[i]);
            }
        }

        return tbytearraylist;
    }

    @Override
    public TByteList inverseGrep(TByteProcedure tbyteprocedure) {
        TByteArrayList tbytearraylist = new TByteArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tbyteprocedure.execute(this._data[i])) {
                tbytearraylist.add(this._data[i]);
            }
        }

        return tbytearraylist;
    }

    @Override
    public byte max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            byte b0 = Byte.MIN_VALUE;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] > b0) {
                    b0 = this._data[i];
                }
            }

            return b0;
        }
    }

    @Override
    public byte min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            byte b0 = 127;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] < b0) {
                    b0 = this._data[i];
                }
            }

            return b0;
        }
    }

    @Override
    public byte sum() {
        byte b0 = 0;

        for (int i = 0; i < this._pos; ++i) {
            b0 += this._data[i];
        }

        return b0;
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
        objectoutput.writeByte(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeByte(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readByte();
        int i = objectinput.readInt();

        this._data = new byte[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readByte();
        }

    }

    class TByteArrayIterator implements TByteIterator {

        private int cursor = 0;
        int lastRet = -1;

        TByteArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TByteArrayList.this.size();
        }

        @Override
        public byte next() {
            try {
                byte b0 = TByteArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return b0;
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
                    TByteArrayList.this.remove(this.lastRet, 1);
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
