package gnu.trove.list.array;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
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

public class TCharArrayList implements TCharList, Externalizable {

    static final long serialVersionUID = 1L;
    protected char[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected char no_entry_value;

    public TCharArrayList() {
        this(10, '\u0000');
    }

    public TCharArrayList(int i) {
        this(i, '\u0000');
    }

    public TCharArrayList(int i, char c0) {
        this._data = new char[i];
        this._pos = 0;
        this.no_entry_value = c0;
    }

    public TCharArrayList(TCharCollection tcharcollection) {
        this(tcharcollection.size());
        this.addAll(tcharcollection);
    }

    public TCharArrayList(char[] achar) {
        this(achar.length);
        this.add(achar);
    }

    protected TCharArrayList(char[] achar, char c0, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (achar == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = achar;
            this._pos = achar.length;
            this.no_entry_value = c0;
        }
    }

    public static TCharArrayList wrap(char[] achar) {
        return wrap(achar, '\u0000');
    }

    public static TCharArrayList wrap(char[] achar, char c0) {
        return new TCharArrayList(achar, c0, true) {
            @Override
            public void ensureCapacity(int i) {
                if (i > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            char[] achar = new char[j];

            System.arraycopy(this._data, 0, achar, 0, this._data.length);
            this._data = achar;
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
            char[] achar = new char[this.size()];

            this.toArray(achar, 0, achar.length);
            this._data = achar;
        }

    }

    @Override
    public boolean add(char c0) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = c0;
        return true;
    }

    @Override
    public void add(char[] achar) {
        this.add(achar, 0, achar.length);
    }

    @Override
    public void add(char[] achar, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(achar, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, char c0) {
        if (i == this._pos) {
            this.add(c0);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = c0;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, char[] achar) {
        this.insert(i, achar, 0, achar.length);
    }

    @Override
    public void insert(int i, char[] achar, int j, int k) {
        if (i == this._pos) {
            this.add(achar, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(achar, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public char get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public char getQuick(int i) {
        return this._data[i];
    }

    @Override
    public char set(int i, char c0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            char c1 = this._data[i];

            this._data[i] = c0;
            return c1;
        }
    }

    @Override
    public char replace(int i, char c0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            char c1 = this._data[i];

            this._data[i] = c0;
            return c1;
        }
    }

    @Override
    public void set(int i, char[] achar) {
        this.set(i, achar, 0, achar.length);
    }

    @Override
    public void set(int i, char[] achar, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(achar, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, char c0) {
        this._data[i] = c0;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new char[i];
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
    public boolean remove(char c0) {
        for (int i = 0; i < this._pos; ++i) {
            if (c0 == this._data[i]) {
                this.remove(i, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public char removeAt(int i) {
        char c0 = this.get(i);

        this.remove(i, 1);
        return c0;
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
    public TCharIterator iterator() {
        return new TCharArrayList.TCharArrayIterator(0);
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
        } while (this.contains(c0));

        return false;
    }

    @Override
    public boolean containsAll(TCharCollection tcharcollection) {
        if (this == tcharcollection) {
            return true;
        } else {
            TCharIterator tchariterator = tcharcollection.iterator();

            char c0;

            do {
                if (!tchariterator.hasNext()) {
                    return true;
                }

                c0 = tchariterator.next();
            } while (this.contains(c0));

            return false;
        }
    }

    @Override
    public boolean containsAll(char[] achar) {
        int i = achar.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(achar[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Character character = (Character) iterator.next();
            char c0 = character;

            if (this.add(c0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TCharCollection tcharcollection) {
        boolean flag = false;
        TCharIterator tchariterator = tcharcollection.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            if (this.add(c0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(char[] achar) {
        boolean flag = false;
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar1[j];

            if (this.add(c0)) {
                flag = true;
            }
        }

        return flag;
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
        char[] achar1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(achar, achar1[i]) < 0) {
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
        if (tcharcollection == this) {
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
    public void transformValues(TCharFunction tcharfunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tcharfunction.execute(this._data[i])) {
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
        char c0 = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = c0;
    }

    @Override
    public TCharList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TCharArrayList tchararraylist = new TCharArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tchararraylist.add(this._data[k]);
            }

            return tchararraylist;
        }
    }

    @Override
    public char[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public char[] toArray(int i, int j) {
        char[] achar = new char[j];

        this.toArray(achar, i, j);
        return achar;
    }

    @Override
    public char[] toArray(char[] achar) {
        int i = achar.length;

        if (achar.length > this._pos) {
            i = this._pos;
            achar[i] = this.no_entry_value;
        }

        this.toArray(achar, 0, i);
        return achar;
    }

    @Override
    public char[] toArray(char[] achar, int i, int j) {
        if (j == 0) {
            return achar;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, achar, 0, j);
            return achar;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public char[] toArray(char[] achar, int i, int j, int k) {
        if (k == 0) {
            return achar;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, achar, j, k);
            return achar;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TCharArrayList) {
            TCharArrayList tchararraylist = (TCharArrayList) object;

            if (tchararraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tchararraylist._data[i]);

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
    public boolean forEach(TCharProcedure tcharprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tcharprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TCharProcedure tcharprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tcharprocedure.execute(this._data[i]));

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
    public void fill(char c0) {
        Arrays.fill(this._data, 0, this._pos, c0);
    }

    @Override
    public void fill(int i, int j, char c0) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, c0);
    }

    @Override
    public int binarySearch(char c0) {
        return this.binarySearch(c0, 0, this._pos);
    }

    @Override
    public int binarySearch(char c0, int i, int j) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else if (j > this._pos) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else {
            int k = i;
            int l = j - 1;

            while (k <= l) {
                int i1 = k + l >>> 1;
                char c1 = this._data[i1];

                if (c1 < c0) {
                    k = i1 + 1;
                } else {
                    if (c1 <= c0) {
                        return i1;
                    }

                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(char c0) {
        return this.indexOf(0, c0);
    }

    @Override
    public int indexOf(int i, char c0) {
        for (int j = i; j < this._pos; ++j) {
            if (this._data[j] == c0) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(char c0) {
        return this.lastIndexOf(this._pos, c0);
    }

    @Override
    public int lastIndexOf(int i, char c0) {
        int j = i;

        do {
            if (j-- <= 0) {
                return -1;
            }
        } while (this._data[j] != c0);

        return j;
    }

    @Override
    public boolean contains(char c0) {
        return this.lastIndexOf(c0) >= 0;
    }

    @Override
    public TCharList grep(TCharProcedure tcharprocedure) {
        TCharArrayList tchararraylist = new TCharArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tcharprocedure.execute(this._data[i])) {
                tchararraylist.add(this._data[i]);
            }
        }

        return tchararraylist;
    }

    @Override
    public TCharList inverseGrep(TCharProcedure tcharprocedure) {
        TCharArrayList tchararraylist = new TCharArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tcharprocedure.execute(this._data[i])) {
                tchararraylist.add(this._data[i]);
            }
        }

        return tchararraylist;
    }

    @Override
    public char max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            char c0 = 0;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] > c0) {
                    c0 = this._data[i];
                }
            }

            return c0;
        }
    }

    @Override
    public char min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            char c0 = '\uffff';

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] < c0) {
                    c0 = this._data[i];
                }
            }

            return c0;
        }
    }

    @Override
    public char sum() {
        char c0 = 0;

        for (int i = 0; i < this._pos; ++i) {
            c0 += this._data[i];
        }

        return c0;
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
        objectoutput.writeChar(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeChar(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readChar();
        int i = objectinput.readInt();

        this._data = new char[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readChar();
        }

    }

    class TCharArrayIterator implements TCharIterator {

        private int cursor = 0;
        int lastRet = -1;

        TCharArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TCharArrayList.this.size();
        }

        @Override
        public char next() {
            try {
                char c0 = TCharArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return c0;
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
                    TCharArrayList.this.remove(this.lastRet, 1);
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
