package gnu.trove.list.array;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
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

public class TDoubleArrayList implements TDoubleList, Externalizable {

    static final long serialVersionUID = 1L;
    protected double[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected double no_entry_value;

    public TDoubleArrayList() {
        this(10, 0.0D);
    }

    public TDoubleArrayList(int i) {
        this(i, 0.0D);
    }

    public TDoubleArrayList(int i, double d0) {
        this._data = new double[i];
        this._pos = 0;
        this.no_entry_value = d0;
    }

    public TDoubleArrayList(TDoubleCollection tdoublecollection) {
        this(tdoublecollection.size());
        this.addAll(tdoublecollection);
    }

    public TDoubleArrayList(double[] adouble) {
        this(adouble.length);
        this.add(adouble);
    }

    protected TDoubleArrayList(double[] adouble, double d0, boolean flag) {
        if (!flag) {
            throw new IllegalStateException("Wrong call");
        } else if (adouble == null) {
            throw new IllegalArgumentException("values can not be null");
        } else {
            this._data = adouble;
            this._pos = adouble.length;
            this.no_entry_value = d0;
        }
    }

    public static TDoubleArrayList wrap(double[] adouble) {
        return wrap(adouble, 0.0D);
    }

    public static TDoubleArrayList wrap(double[] adouble, double d0) {
        return new TDoubleArrayList(adouble, d0, true) {
            @Override
            public void ensureCapacity(int i) {
                if (i > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int i) {
        if (i > this._data.length) {
            int j = Math.max(this._data.length << 1, i);
            double[] adouble = new double[j];

            System.arraycopy(this._data, 0, adouble, 0, this._data.length);
            this._data = adouble;
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
            double[] adouble = new double[this.size()];

            this.toArray(adouble, 0, adouble.length);
            this._data = adouble;
        }

    }

    @Override
    public boolean add(double d0) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = d0;
        return true;
    }

    @Override
    public void add(double[] adouble) {
        this.add(adouble, 0, adouble.length);
    }

    @Override
    public void add(double[] adouble, int i, int j) {
        this.ensureCapacity(this._pos + j);
        System.arraycopy(adouble, i, this._data, this._pos, j);
        this._pos += j;
    }

    @Override
    public void insert(int i, double d0) {
        if (i == this._pos) {
            this.add(d0);
        } else {
            this.ensureCapacity(this._pos + 1);
            System.arraycopy(this._data, i, this._data, i + 1, this._pos - i);
            this._data[i] = d0;
            ++this._pos;
        }
    }

    @Override
    public void insert(int i, double[] adouble) {
        this.insert(i, adouble, 0, adouble.length);
    }

    @Override
    public void insert(int i, double[] adouble, int j, int k) {
        if (i == this._pos) {
            this.add(adouble, j, k);
        } else {
            this.ensureCapacity(this._pos + k);
            System.arraycopy(this._data, i, this._data, i + k, this._pos - i);
            System.arraycopy(adouble, j, this._data, i, k);
            this._pos += k;
        }
    }

    @Override
    public double get(int i) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            return this._data[i];
        }
    }

    public double getQuick(int i) {
        return this._data[i];
    }

    @Override
    public double set(int i, double d0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            double d1 = this._data[i];

            this._data[i] = d0;
            return d1;
        }
    }

    @Override
    public double replace(int i, double d0) {
        if (i >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else {
            double d1 = this._data[i];

            this._data[i] = d0;
            return d1;
        }
    }

    @Override
    public void set(int i, double[] adouble) {
        this.set(i, adouble, 0, adouble.length);
    }

    @Override
    public void set(int i, double[] adouble, int j, int k) {
        if (i >= 0 && i + k <= this._pos) {
            System.arraycopy(adouble, j, this._data, i, k);
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    public void setQuick(int i, double d0) {
        this._data[i] = d0;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int i) {
        this._data = new double[i];
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
    public boolean remove(double d0) {
        for (int i = 0; i < this._pos; ++i) {
            if (d0 == this._data[i]) {
                this.remove(i, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public double removeAt(int i) {
        double d0 = this.get(i);

        this.remove(i, 1);
        return d0;
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
    public TDoubleIterator iterator() {
        return new TDoubleArrayList.TDoubleArrayIterator(0);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();

        double d0;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Object object = iterator.next();

            if (!(object instanceof Double)) {
                return false;
            }

            d0 = (Double) object;
        } while (this.contains(d0));

        return false;
    }

    @Override
    public boolean containsAll(TDoubleCollection tdoublecollection) {
        if (this == tdoublecollection) {
            return true;
        } else {
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            double d0;

            do {
                if (!tdoubleiterator.hasNext()) {
                    return true;
                }

                d0 = tdoubleiterator.next();
            } while (this.contains(d0));

            return false;
        }
    }

    @Override
    public boolean containsAll(double[] adouble) {
        int i = adouble.length;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (this.contains(adouble[i]));

        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Double odouble = (Double) iterator.next();
            double d0 = odouble;

            if (this.add(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TDoubleCollection tdoublecollection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            if (this.add(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(double[] adouble) {
        boolean flag = false;
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            if (this.add(d0)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (!collection.contains(tdoubleiterator.next())) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TDoubleCollection tdoublecollection) {
        if (this == tdoublecollection) {
            return false;
        } else {
            boolean flag = false;
            TDoubleIterator tdoubleiterator = this.iterator();

            while (tdoubleiterator.hasNext()) {
                if (!tdoublecollection.contains(tdoubleiterator.next())) {
                    tdoubleiterator.remove();
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean retainAll(double[] adouble) {
        boolean flag = false;

        Arrays.sort(adouble);
        double[] adouble1 = this._data;
        int i = this._pos;

        while (i-- > 0) {
            if (Arrays.binarySearch(adouble, adouble1[i]) < 0) {
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

            if (object instanceof Double) {
                double d0 = (Double) object;

                if (this.remove(d0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TDoubleCollection tdoublecollection) {
        if (tdoublecollection == this) {
            this.clear();
            return true;
        } else {
            boolean flag = false;
            TDoubleIterator tdoubleiterator = tdoublecollection.iterator();

            while (tdoubleiterator.hasNext()) {
                double d0 = tdoubleiterator.next();

                if (this.remove(d0)) {
                    flag = true;
                }
            }

            return flag;
        }
    }

    @Override
    public boolean removeAll(double[] adouble) {
        boolean flag = false;
        int i = adouble.length;

        while (i-- > 0) {
            if (this.remove(adouble[i])) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        for (int i = this._pos; i-- > 0; this._data[i] = tdoublefunction.execute(this._data[i])) {
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
        double d0 = this._data[i];

        this._data[i] = this._data[j];
        this._data[j] = d0;
    }

    @Override
    public TDoubleList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("end index " + j + " greater than begin index " + i);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        } else {
            TDoubleArrayList tdoublearraylist = new TDoubleArrayList(j - i);

            for (int k = i; k < j; ++k) {
                tdoublearraylist.add(this._data[k]);
            }

            return tdoublearraylist;
        }
    }

    @Override
    public double[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public double[] toArray(int i, int j) {
        double[] adouble = new double[j];

        this.toArray(adouble, i, j);
        return adouble;
    }

    @Override
    public double[] toArray(double[] adouble) {
        int i = adouble.length;

        if (adouble.length > this._pos) {
            i = this._pos;
            adouble[i] = this.no_entry_value;
        }

        this.toArray(adouble, 0, i);
        return adouble;
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j) {
        if (j == 0) {
            return adouble;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, adouble, 0, j);
            return adouble;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j, int k) {
        if (k == 0) {
            return adouble;
        } else if (i >= 0 && i < this._pos) {
            System.arraycopy(this._data, i, adouble, j, k);
            return adouble;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof TDoubleArrayList) {
            TDoubleArrayList tdoublearraylist = (TDoubleArrayList) object;

            if (tdoublearraylist.size() != this.size()) {
                return false;
            } else {
                int i = this._pos;

                do {
                    if (i-- <= 0) {
                        return true;
                    }
                } while (this._data[i] == tdoublearraylist._data[i]);

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
    public boolean forEach(TDoubleProcedure tdoubleprocedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (!tdoubleprocedure.execute(this._data[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure tdoubleprocedure) {
        int i = this._pos;

        do {
            if (i-- <= 0) {
                return true;
            }
        } while (tdoubleprocedure.execute(this._data[i]));

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
    public void fill(double d0) {
        Arrays.fill(this._data, 0, this._pos, d0);
    }

    @Override
    public void fill(int i, int j, double d0) {
        if (j > this._pos) {
            this.ensureCapacity(j);
            this._pos = j;
        }

        Arrays.fill(this._data, i, j, d0);
    }

    @Override
    public int binarySearch(double d0) {
        return this.binarySearch(d0, 0, this._pos);
    }

    @Override
    public int binarySearch(double d0, int i, int j) {
        if (i < 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        } else if (j > this._pos) {
            throw new ArrayIndexOutOfBoundsException(j);
        } else {
            int k = i;
            int l = j - 1;

            while (k <= l) {
                int i1 = k + l >>> 1;
                double d1 = this._data[i1];

                if (d1 < d0) {
                    k = i1 + 1;
                } else {
                    if (d1 <= d0) {
                        return i1;
                    }

                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(double d0) {
        return this.indexOf(0, d0);
    }

    @Override
    public int indexOf(int i, double d0) {
        for (int j = i; j < this._pos; ++j) {
            if (this._data[j] == d0) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(double d0) {
        return this.lastIndexOf(this._pos, d0);
    }

    @Override
    public int lastIndexOf(int i, double d0) {
        int j = i;

        do {
            if (j-- <= 0) {
                return -1;
            }
        } while (this._data[j] != d0);

        return j;
    }

    @Override
    public boolean contains(double d0) {
        return this.lastIndexOf(d0) >= 0;
    }

    @Override
    public TDoubleList grep(TDoubleProcedure tdoubleprocedure) {
        TDoubleArrayList tdoublearraylist = new TDoubleArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (tdoubleprocedure.execute(this._data[i])) {
                tdoublearraylist.add(this._data[i]);
            }
        }

        return tdoublearraylist;
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure tdoubleprocedure) {
        TDoubleArrayList tdoublearraylist = new TDoubleArrayList();

        for (int i = 0; i < this._pos; ++i) {
            if (!tdoubleprocedure.execute(this._data[i])) {
                tdoublearraylist.add(this._data[i]);
            }
        }

        return tdoublearraylist;
    }

    @Override
    public double max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        } else {
            double d0 = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] > d0) {
                    d0 = this._data[i];
                }
            }

            return d0;
        }
    }

    @Override
    public double min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        } else {
            double d0 = Double.POSITIVE_INFINITY;

            for (int i = 0; i < this._pos; ++i) {
                if (this._data[i] < d0) {
                    d0 = this._data[i];
                }
            }

            return d0;
        }
    }

    @Override
    public double sum() {
        double d0 = 0.0D;

        for (int i = 0; i < this._pos; ++i) {
            d0 += this._data[i];
        }

        return d0;
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
        objectoutput.writeDouble(this.no_entry_value);
        int i = this._data.length;

        objectoutput.writeInt(i);

        for (int j = 0; j < i; ++j) {
            objectoutput.writeDouble(this._data[j]);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this._pos = objectinput.readInt();
        this.no_entry_value = objectinput.readDouble();
        int i = objectinput.readInt();

        this._data = new double[i];

        for (int j = 0; j < i; ++j) {
            this._data[j] = objectinput.readDouble();
        }

    }

    class TDoubleArrayIterator implements TDoubleIterator {

        private int cursor = 0;
        int lastRet = -1;

        TDoubleArrayIterator(int i) {
            this.cursor = i;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < TDoubleArrayList.this.size();
        }

        @Override
        public double next() {
            try {
                double d0 = TDoubleArrayList.this.get(this.cursor);

                this.lastRet = this.cursor++;
                return d0;
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
                    TDoubleArrayList.this.remove(this.lastRet, 1);
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
