package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TDoubleLinkedList implements TDoubleList, Externalizable {

    double no_entry_value;
    int size;
    TDoubleLinkedList.TDoubleLink head = null;
    TDoubleLinkedList.TDoubleLink tail;

    public TDoubleLinkedList() {
        this.tail = this.head;
    }

    public TDoubleLinkedList(double d0) {
        this.tail = this.head;
        this.no_entry_value = d0;
    }

    public TDoubleLinkedList(TDoubleList tdoublelist) {
        this.tail = this.head;
        this.no_entry_value = tdoublelist.getNoEntryValue();
        TDoubleIterator tdoubleiterator = tdoublelist.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            this.add(d0);
        }

    }

    @Override
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean add(double d0) {
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = new TDoubleLinkedList.TDoubleLink(d0);

        if (no(this.head)) {
            this.head = tdoublelinkedlist_tdoublelink;
            this.tail = tdoublelinkedlist_tdoublelink;
        } else {
            tdoublelinkedlist_tdoublelink.setPrevious(this.tail);
            this.tail.setNext(tdoublelinkedlist_tdoublelink);
            this.tail = tdoublelinkedlist_tdoublelink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(double[] adouble) {
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            this.add(d0);
        }

    }

    @Override
    public void add(double[] adouble, int i, int j) {
        for (int k = 0; k < j; ++k) {
            double d0 = adouble[i + k];

            this.add(d0);
        }

    }

    @Override
    public void insert(int i, double d0) {
        TDoubleLinkedList tdoublelinkedlist = new TDoubleLinkedList();

        tdoublelinkedlist.add(d0);
        this.insert(i, tdoublelinkedlist);
    }

    @Override
    public void insert(int i, double[] adouble) {
        this.insert(i, link(adouble, 0, adouble.length));
    }

    @Override
    public void insert(int i, double[] adouble, int j, int k) {
        this.insert(i, link(adouble, j, k));
    }

    void insert(int i, TDoubleLinkedList tdoublelinkedlist) {
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

        this.size += tdoublelinkedlist.size;
        if (tdoublelinkedlist_tdoublelink == this.head) {
            tdoublelinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tdoublelinkedlist.tail);
            this.head = tdoublelinkedlist.head;
        } else {
            if (no(tdoublelinkedlist_tdoublelink)) {
                if (this.size == 0) {
                    this.head = tdoublelinkedlist.head;
                    this.tail = tdoublelinkedlist.tail;
                } else {
                    this.tail.setNext(tdoublelinkedlist.head);
                    tdoublelinkedlist.head.setPrevious(this.tail);
                    this.tail = tdoublelinkedlist.tail;
                }
            } else {
                TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink1 = tdoublelinkedlist_tdoublelink.getPrevious();

                tdoublelinkedlist_tdoublelink.getPrevious().setNext(tdoublelinkedlist.head);
                tdoublelinkedlist.tail.setNext(tdoublelinkedlist_tdoublelink);
                tdoublelinkedlist_tdoublelink.setPrevious(tdoublelinkedlist.tail);
                tdoublelinkedlist.head.setPrevious(tdoublelinkedlist_tdoublelink1);
            }

        }
    }

    static TDoubleLinkedList link(double[] adouble, int i, int j) {
        TDoubleLinkedList tdoublelinkedlist = new TDoubleLinkedList();

        for (int k = 0; k < j; ++k) {
            tdoublelinkedlist.add(adouble[i + k]);
        }

        return tdoublelinkedlist;
    }

    @Override
    public double get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

            return no(tdoublelinkedlist_tdoublelink) ? this.no_entry_value : tdoublelinkedlist_tdoublelink.getValue();
        }
    }

    public TDoubleLinkedList.TDoubleLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TDoubleLinkedList.TDoubleLink getLink(TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink, int i, int j) {
        return getLink(tdoublelinkedlist_tdoublelink, i, j, true);
    }

    private static TDoubleLinkedList.TDoubleLink getLink(TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink, int i, int j, boolean flag) {
        for (int k = i; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = flag ? tdoublelinkedlist_tdoublelink.getNext() : tdoublelinkedlist_tdoublelink.getPrevious()) {
            if (k == j) {
                return tdoublelinkedlist_tdoublelink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public double set(int i, double d0) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

            if (no(tdoublelinkedlist_tdoublelink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                double d1 = tdoublelinkedlist_tdoublelink.getValue();

                tdoublelinkedlist_tdoublelink.setValue(d0);
                return d1;
            }
        }
    }

    @Override
    public void set(int i, double[] adouble) {
        this.set(i, adouble, 0, adouble.length);
    }

    @Override
    public void set(int i, double[] adouble, int j, int k) {
        for (int l = 0; l < k; ++l) {
            double d0 = adouble[j + l];

            this.set(i + l, d0);
        }

    }

    @Override
    public double replace(int i, double d0) {
        return this.set(i, d0);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(double d0) {
        boolean flag = false;

        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            if (tdoublelinkedlist_tdoublelink.getValue() == d0) {
                flag = true;
                this.removeLink(tdoublelinkedlist_tdoublelink);
            }
        }

        return flag;
    }

    private void removeLink(TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink) {
        if (!no(tdoublelinkedlist_tdoublelink)) {
            --this.size;
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink1 = tdoublelinkedlist_tdoublelink.getPrevious();
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink2 = tdoublelinkedlist_tdoublelink.getNext();

            if (got(tdoublelinkedlist_tdoublelink1)) {
                tdoublelinkedlist_tdoublelink1.setNext(tdoublelinkedlist_tdoublelink2);
            } else {
                this.head = tdoublelinkedlist_tdoublelink2;
            }

            if (got(tdoublelinkedlist_tdoublelink2)) {
                tdoublelinkedlist_tdoublelink2.setPrevious(tdoublelinkedlist_tdoublelink1);
            } else {
                this.tail = tdoublelinkedlist_tdoublelink1;
            }

            tdoublelinkedlist_tdoublelink.setNext((TDoubleLinkedList.TDoubleLink) null);
            tdoublelinkedlist_tdoublelink.setPrevious((TDoubleLinkedList.TDoubleLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Double odouble;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Double)) {
                    return false;
                }

                odouble = (Double) object;
            } while (this.contains(odouble));

            return false;
        }
    }

    @Override
    public boolean containsAll(TDoubleCollection tdoublecollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            double[] adouble1 = adouble;
            int i = adouble.length;

            for (int j = 0; j < i; ++j) {
                double d0 = adouble1[j];

                if (!this.contains(d0)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Double> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Double odouble = (Double) iterator.next();

            if (this.add(odouble)) {
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

    @Override
    public boolean retainAll(double[] adouble) {
        Arrays.sort(adouble);
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (Arrays.binarySearch(adouble, tdoubleiterator.next()) < 0) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (collection.contains(tdoubleiterator.next())) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TDoubleCollection tdoublecollection) {
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (tdoublecollection.contains(tdoubleiterator.next())) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(double[] adouble) {
        Arrays.sort(adouble);
        boolean flag = false;
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            if (Arrays.binarySearch(adouble, tdoubleiterator.next()) >= 0) {
                tdoubleiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public double removeAt(int i) {
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

        if (no(tdoublelinkedlist_tdoublelink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            double d0 = tdoublelinkedlist_tdoublelink.getValue();

            this.removeLink(tdoublelinkedlist_tdoublelink);
            return d0;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            tdoublelinkedlist_tdoublelink.setValue(tdoublefunction.execute(tdoublelinkedlist_tdoublelink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head;
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink1 = this.tail;
        TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink2 = this.head;

        while (got(tdoublelinkedlist_tdoublelink2)) {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink3 = tdoublelinkedlist_tdoublelink2.getNext();
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink4 = tdoublelinkedlist_tdoublelink2.getPrevious();
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink5 = tdoublelinkedlist_tdoublelink2;

            tdoublelinkedlist_tdoublelink2 = tdoublelinkedlist_tdoublelink2.getNext();
            tdoublelinkedlist_tdoublelink5.setNext(tdoublelinkedlist_tdoublelink4);
            tdoublelinkedlist_tdoublelink5.setPrevious(tdoublelinkedlist_tdoublelink3);
        }

        this.head = tdoublelinkedlist_tdoublelink1;
        this.tail = tdoublelinkedlist_tdoublelink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink1 = this.getLinkAt(j);
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink2 = null;
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink3 = tdoublelinkedlist_tdoublelink.getPrevious();
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink4 = tdoublelinkedlist_tdoublelink;

            while (tdoublelinkedlist_tdoublelink4 != tdoublelinkedlist_tdoublelink1) {
                TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink5 = tdoublelinkedlist_tdoublelink4.getNext();
                TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink6 = tdoublelinkedlist_tdoublelink4.getPrevious();

                tdoublelinkedlist_tdoublelink2 = tdoublelinkedlist_tdoublelink4;
                tdoublelinkedlist_tdoublelink4 = tdoublelinkedlist_tdoublelink4.getNext();
                tdoublelinkedlist_tdoublelink2.setNext(tdoublelinkedlist_tdoublelink6);
                tdoublelinkedlist_tdoublelink2.setPrevious(tdoublelinkedlist_tdoublelink5);
            }

            if (got(tdoublelinkedlist_tdoublelink2)) {
                tdoublelinkedlist_tdoublelink3.setNext(tdoublelinkedlist_tdoublelink2);
                tdoublelinkedlist_tdoublelink1.setPrevious(tdoublelinkedlist_tdoublelink3);
            }

            tdoublelinkedlist_tdoublelink.setNext(tdoublelinkedlist_tdoublelink1);
            tdoublelinkedlist_tdoublelink1.setPrevious(tdoublelinkedlist_tdoublelink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tdoublelinkedlist_tdoublelink);
            this.add(tdoublelinkedlist_tdoublelink.getValue());
        }

    }

    @Override
    public TDoubleList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TDoubleLinkedList tdoublelinkedlist = new TDoubleLinkedList();
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tdoublelinkedlist.add(tdoublelinkedlist_tdoublelink.getValue());
                tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext();
            }

            return tdoublelinkedlist;
        }
    }

    @Override
    public double[] toArray() {
        return this.toArray(new double[this.size], 0, this.size);
    }

    @Override
    public double[] toArray(int i, int j) {
        return this.toArray(new double[j], i, 0, j);
    }

    @Override
    public double[] toArray(double[] adouble) {
        return this.toArray(adouble, 0, this.size);
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j) {
        return this.toArray(adouble, i, 0, j);
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j, int k) {
        if (k == 0) {
            return adouble;
        } else if (i >= 0 && i < this.size()) {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                adouble[j + l] = tdoublelinkedlist_tdoublelink.getValue();
                tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext();
            }

            return adouble;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TDoubleProcedure tdoubleprocedure) {
        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            if (!tdoubleprocedure.execute(tdoublelinkedlist_tdoublelink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure tdoubleprocedure) {
        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.tail; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getPrevious()) {
            if (!tdoubleprocedure.execute(tdoublelinkedlist_tdoublelink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void sort() {
        this.sort(0, this.size);
    }

    @Override
    public void sort(int i, int j) {
        TDoubleList tdoublelist = this.subList(i, j);
        double[] adouble = tdoublelist.toArray();

        Arrays.sort(adouble);
        this.set(i, adouble);
    }

    @Override
    public void fill(double d0) {
        this.fill(0, this.size, d0);
    }

    @Override
    public void fill(int i, int j, double d0) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);
            int k;

            if (j > this.size) {
                for (k = i; k < this.size; ++k) {
                    tdoublelinkedlist_tdoublelink.setValue(d0);
                    tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext();
                }

                for (k = this.size; k < j; ++k) {
                    this.add(d0);
                }
            } else {
                for (k = i; k < j; ++k) {
                    tdoublelinkedlist_tdoublelink.setValue(d0);
                    tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(double d0) {
        return this.binarySearch(d0, 0, this.size());
    }

    @Override
    public int binarySearch(double d0, int i, int j) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + j + " > " + this.size);
        } else if (j < i) {
            return -(i + 1);
        } else {
            int k = i;
            TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i);
            int l = j;

            while (k < l) {
                int i1 = k + l >>> 1;
                TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink1 = getLink(tdoublelinkedlist_tdoublelink, k, i1);

                if (tdoublelinkedlist_tdoublelink1.getValue() == d0) {
                    return i1;
                }

                if (tdoublelinkedlist_tdoublelink1.getValue() < d0) {
                    k = i1 + 1;
                    tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink1.next;
                } else {
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
        int j = i;

        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i); got(tdoublelinkedlist_tdoublelink.getNext()); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            if (tdoublelinkedlist_tdoublelink.getValue() == d0) {
                return j;
            }

            ++j;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(double d0) {
        return this.lastIndexOf(0, d0);
    }

    @Override
    public int lastIndexOf(int i, double d0) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int j = -1;
            int k = i;

            for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.getLinkAt(i); got(tdoublelinkedlist_tdoublelink.getNext()); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
                if (tdoublelinkedlist_tdoublelink.getValue() == d0) {
                    j = k;
                }

                ++k;
            }

            return j;
        }
    }

    @Override
    public boolean contains(double d0) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
                if (tdoublelinkedlist_tdoublelink.getValue() == d0) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TDoubleIterator iterator() {
        return new TDoubleIterator() {
            TDoubleLinkedList.TDoubleLink l;
            TDoubleLinkedList.TDoubleLink current;

            {
                this.l = TDoubleLinkedList.this.head;
            }

            @Override
            public double next() {
                if (TDoubleLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    double d0 = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return d0;
                }
            }

            @Override
            public boolean hasNext() {
                return TDoubleLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TDoubleLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TDoubleList grep(TDoubleProcedure tdoubleprocedure) {
        TDoubleLinkedList tdoublelinkedlist = new TDoubleLinkedList();

        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            if (tdoubleprocedure.execute(tdoublelinkedlist_tdoublelink.getValue())) {
                tdoublelinkedlist.add(tdoublelinkedlist_tdoublelink.getValue());
            }
        }

        return tdoublelinkedlist;
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure tdoubleprocedure) {
        TDoubleLinkedList tdoublelinkedlist = new TDoubleLinkedList();

        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            if (!tdoubleprocedure.execute(tdoublelinkedlist_tdoublelink.getValue())) {
                tdoublelinkedlist.add(tdoublelinkedlist_tdoublelink.getValue());
            }
        }

        return tdoublelinkedlist;
    }

    @Override
    public double max() {
        double d0 = Double.NEGATIVE_INFINITY;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
                if (d0 < tdoublelinkedlist_tdoublelink.getValue()) {
                    d0 = tdoublelinkedlist_tdoublelink.getValue();
                }
            }

            return d0;
        }
    }

    @Override
    public double min() {
        double d0 = Double.POSITIVE_INFINITY;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
                if (d0 > tdoublelinkedlist_tdoublelink.getValue()) {
                    d0 = tdoublelinkedlist_tdoublelink.getValue();
                }
            }

            return d0;
        }
    }

    @Override
    public double sum() {
        double d0 = 0.0D;

        for (TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink = this.head; got(tdoublelinkedlist_tdoublelink); tdoublelinkedlist_tdoublelink = tdoublelinkedlist_tdoublelink.getNext()) {
            d0 += tdoublelinkedlist_tdoublelink.getValue();
        }

        return d0;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeDouble(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            objectoutput.writeDouble(d0);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readDouble();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readDouble());
        }

    }

    static boolean got(Object object) {
        return object != null;
    }

    static boolean no(Object object) {
        return object == null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            TDoubleLinkedList tdoublelinkedlist = (TDoubleLinkedList) object;

            if (this.no_entry_value != tdoublelinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tdoublelinkedlist.size) {
                return false;
            } else {
                TDoubleIterator tdoubleiterator = this.iterator();
                TDoubleIterator tdoubleiterator1 = tdoublelinkedlist.iterator();

                do {
                    if (!tdoubleiterator.hasNext()) {
                        return true;
                    }

                    if (!tdoubleiterator1.hasNext()) {
                        return false;
                    }
                } while (tdoubleiterator.next() == tdoubleiterator1.next());

                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = HashFunctions.hash(this.no_entry_value);

        i = 31 * i + this.size;

        for (TDoubleIterator tdoubleiterator = this.iterator(); tdoubleiterator.hasNext(); i = 31 * i + HashFunctions.hash(tdoubleiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TDoubleIterator tdoubleiterator = this.iterator();

        while (tdoubleiterator.hasNext()) {
            double d0 = tdoubleiterator.next();

            stringbuilder.append(d0);
            if (tdoubleiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TDoubleProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(double d0) {
            if (TDoubleLinkedList.this.remove(d0)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TDoubleLink {

        double value;
        TDoubleLinkedList.TDoubleLink previous;
        TDoubleLinkedList.TDoubleLink next;

        TDoubleLink(double d0) {
            this.value = d0;
        }

        public double getValue() {
            return this.value;
        }

        public void setValue(double d0) {
            this.value = d0;
        }

        public TDoubleLinkedList.TDoubleLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink) {
            this.previous = tdoublelinkedlist_tdoublelink;
        }

        public TDoubleLinkedList.TDoubleLink getNext() {
            return this.next;
        }

        public void setNext(TDoubleLinkedList.TDoubleLink tdoublelinkedlist_tdoublelink) {
            this.next = tdoublelinkedlist_tdoublelink;
        }
    }
}
