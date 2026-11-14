package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TShortLinkedList implements TShortList, Externalizable {

    short no_entry_value;
    int size;
    TShortLinkedList.TShortLink head = null;
    TShortLinkedList.TShortLink tail;

    public TShortLinkedList() {
        this.tail = this.head;
    }

    public TShortLinkedList(short short0) {
        this.tail = this.head;
        this.no_entry_value = short0;
    }

    public TShortLinkedList(TShortList tshortlist) {
        this.tail = this.head;
        this.no_entry_value = tshortlist.getNoEntryValue();
        TShortIterator tshortiterator = tshortlist.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            this.add(short0);
        }

    }

    @Override
    public short getNoEntryValue() {
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
    public boolean add(short short0) {
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = new TShortLinkedList.TShortLink(short0);

        if (no(this.head)) {
            this.head = tshortlinkedlist_tshortlink;
            this.tail = tshortlinkedlist_tshortlink;
        } else {
            tshortlinkedlist_tshortlink.setPrevious(this.tail);
            this.tail.setNext(tshortlinkedlist_tshortlink);
            this.tail = tshortlinkedlist_tshortlink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(short[] ashort) {
        short[] ashort1 = ashort;
        int i = ashort.length;

        for (int j = 0; j < i; ++j) {
            short short0 = ashort1[j];

            this.add(short0);
        }

    }

    @Override
    public void add(short[] ashort, int i, int j) {
        for (int k = 0; k < j; ++k) {
            short short0 = ashort[i + k];

            this.add(short0);
        }

    }

    @Override
    public void insert(int i, short short0) {
        TShortLinkedList tshortlinkedlist = new TShortLinkedList();

        tshortlinkedlist.add(short0);
        this.insert(i, tshortlinkedlist);
    }

    @Override
    public void insert(int i, short[] ashort) {
        this.insert(i, link(ashort, 0, ashort.length));
    }

    @Override
    public void insert(int i, short[] ashort, int j, int k) {
        this.insert(i, link(ashort, j, k));
    }

    void insert(int i, TShortLinkedList tshortlinkedlist) {
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

        this.size += tshortlinkedlist.size;
        if (tshortlinkedlist_tshortlink == this.head) {
            tshortlinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tshortlinkedlist.tail);
            this.head = tshortlinkedlist.head;
        } else {
            if (no(tshortlinkedlist_tshortlink)) {
                if (this.size == 0) {
                    this.head = tshortlinkedlist.head;
                    this.tail = tshortlinkedlist.tail;
                } else {
                    this.tail.setNext(tshortlinkedlist.head);
                    tshortlinkedlist.head.setPrevious(this.tail);
                    this.tail = tshortlinkedlist.tail;
                }
            } else {
                TShortLinkedList.TShortLink tshortlinkedlist_tshortlink1 = tshortlinkedlist_tshortlink.getPrevious();

                tshortlinkedlist_tshortlink.getPrevious().setNext(tshortlinkedlist.head);
                tshortlinkedlist.tail.setNext(tshortlinkedlist_tshortlink);
                tshortlinkedlist_tshortlink.setPrevious(tshortlinkedlist.tail);
                tshortlinkedlist.head.setPrevious(tshortlinkedlist_tshortlink1);
            }

        }
    }

    static TShortLinkedList link(short[] ashort, int i, int j) {
        TShortLinkedList tshortlinkedlist = new TShortLinkedList();

        for (int k = 0; k < j; ++k) {
            tshortlinkedlist.add(ashort[i + k]);
        }

        return tshortlinkedlist;
    }

    @Override
    public short get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

            return no(tshortlinkedlist_tshortlink) ? this.no_entry_value : tshortlinkedlist_tshortlink.getValue();
        }
    }

    public TShortLinkedList.TShortLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TShortLinkedList.TShortLink getLink(TShortLinkedList.TShortLink tshortlinkedlist_tshortlink, int i, int j) {
        return getLink(tshortlinkedlist_tshortlink, i, j, true);
    }

    private static TShortLinkedList.TShortLink getLink(TShortLinkedList.TShortLink tshortlinkedlist_tshortlink, int i, int j, boolean flag) {
        for (int k = i; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = flag ? tshortlinkedlist_tshortlink.getNext() : tshortlinkedlist_tshortlink.getPrevious()) {
            if (k == j) {
                return tshortlinkedlist_tshortlink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public short set(int i, short short0) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

            if (no(tshortlinkedlist_tshortlink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                short short1 = tshortlinkedlist_tshortlink.getValue();

                tshortlinkedlist_tshortlink.setValue(short0);
                return short1;
            }
        }
    }

    @Override
    public void set(int i, short[] ashort) {
        this.set(i, ashort, 0, ashort.length);
    }

    @Override
    public void set(int i, short[] ashort, int j, int k) {
        for (int l = 0; l < k; ++l) {
            short short0 = ashort[j + l];

            this.set(i + l, short0);
        }

    }

    @Override
    public short replace(int i, short short0) {
        return this.set(i, short0);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(short short0) {
        boolean flag = false;

        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            if (tshortlinkedlist_tshortlink.getValue() == short0) {
                flag = true;
                this.removeLink(tshortlinkedlist_tshortlink);
            }
        }

        return flag;
    }

    private void removeLink(TShortLinkedList.TShortLink tshortlinkedlist_tshortlink) {
        if (!no(tshortlinkedlist_tshortlink)) {
            --this.size;
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink1 = tshortlinkedlist_tshortlink.getPrevious();
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink2 = tshortlinkedlist_tshortlink.getNext();

            if (got(tshortlinkedlist_tshortlink1)) {
                tshortlinkedlist_tshortlink1.setNext(tshortlinkedlist_tshortlink2);
            } else {
                this.head = tshortlinkedlist_tshortlink2;
            }

            if (got(tshortlinkedlist_tshortlink2)) {
                tshortlinkedlist_tshortlink2.setPrevious(tshortlinkedlist_tshortlink1);
            } else {
                this.tail = tshortlinkedlist_tshortlink1;
            }

            tshortlinkedlist_tshortlink.setNext((TShortLinkedList.TShortLink) null);
            tshortlinkedlist_tshortlink.setPrevious((TShortLinkedList.TShortLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Short oshort;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Short)) {
                    return false;
                }

                oshort = (Short) object;
            } while (this.contains(oshort));

            return false;
        }
    }

    @Override
    public boolean containsAll(TShortCollection tshortcollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            short[] ashort1 = ashort;
            int i = ashort.length;

            for (int j = 0; j < i; ++j) {
                short short0 = ashort1[j];

                if (!this.contains(short0)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Short> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Short oshort = (Short) iterator.next();

            if (this.add(oshort)) {
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

    @Override
    public boolean retainAll(short[] ashort) {
        Arrays.sort(ashort);
        boolean flag = false;
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            if (Arrays.binarySearch(ashort, tshortiterator.next()) < 0) {
                tshortiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            if (collection.contains(tshortiterator.next())) {
                tshortiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TShortCollection tshortcollection) {
        boolean flag = false;
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            if (tshortcollection.contains(tshortiterator.next())) {
                tshortiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(short[] ashort) {
        Arrays.sort(ashort);
        boolean flag = false;
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            if (Arrays.binarySearch(ashort, tshortiterator.next()) >= 0) {
                tshortiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public short removeAt(int i) {
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

        if (no(tshortlinkedlist_tshortlink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            short short0 = tshortlinkedlist_tshortlink.getValue();

            this.removeLink(tshortlinkedlist_tshortlink);
            return short0;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            tshortlinkedlist_tshortlink.setValue(tshortfunction.execute(tshortlinkedlist_tshortlink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head;
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink1 = this.tail;
        TShortLinkedList.TShortLink tshortlinkedlist_tshortlink2 = this.head;

        while (got(tshortlinkedlist_tshortlink2)) {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink3 = tshortlinkedlist_tshortlink2.getNext();
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink4 = tshortlinkedlist_tshortlink2.getPrevious();
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink5 = tshortlinkedlist_tshortlink2;

            tshortlinkedlist_tshortlink2 = tshortlinkedlist_tshortlink2.getNext();
            tshortlinkedlist_tshortlink5.setNext(tshortlinkedlist_tshortlink4);
            tshortlinkedlist_tshortlink5.setPrevious(tshortlinkedlist_tshortlink3);
        }

        this.head = tshortlinkedlist_tshortlink1;
        this.tail = tshortlinkedlist_tshortlink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink1 = this.getLinkAt(j);
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink2 = null;
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink3 = tshortlinkedlist_tshortlink.getPrevious();
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink4 = tshortlinkedlist_tshortlink;

            while (tshortlinkedlist_tshortlink4 != tshortlinkedlist_tshortlink1) {
                TShortLinkedList.TShortLink tshortlinkedlist_tshortlink5 = tshortlinkedlist_tshortlink4.getNext();
                TShortLinkedList.TShortLink tshortlinkedlist_tshortlink6 = tshortlinkedlist_tshortlink4.getPrevious();

                tshortlinkedlist_tshortlink2 = tshortlinkedlist_tshortlink4;
                tshortlinkedlist_tshortlink4 = tshortlinkedlist_tshortlink4.getNext();
                tshortlinkedlist_tshortlink2.setNext(tshortlinkedlist_tshortlink6);
                tshortlinkedlist_tshortlink2.setPrevious(tshortlinkedlist_tshortlink5);
            }

            if (got(tshortlinkedlist_tshortlink2)) {
                tshortlinkedlist_tshortlink3.setNext(tshortlinkedlist_tshortlink2);
                tshortlinkedlist_tshortlink1.setPrevious(tshortlinkedlist_tshortlink3);
            }

            tshortlinkedlist_tshortlink.setNext(tshortlinkedlist_tshortlink1);
            tshortlinkedlist_tshortlink1.setPrevious(tshortlinkedlist_tshortlink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tshortlinkedlist_tshortlink);
            this.add(tshortlinkedlist_tshortlink.getValue());
        }

    }

    @Override
    public TShortList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TShortLinkedList tshortlinkedlist = new TShortLinkedList();
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tshortlinkedlist.add(tshortlinkedlist_tshortlink.getValue());
                tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext();
            }

            return tshortlinkedlist;
        }
    }

    @Override
    public short[] toArray() {
        return this.toArray(new short[this.size], 0, this.size);
    }

    @Override
    public short[] toArray(int i, int j) {
        return this.toArray(new short[j], i, 0, j);
    }

    @Override
    public short[] toArray(short[] ashort) {
        return this.toArray(ashort, 0, this.size);
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j) {
        return this.toArray(ashort, i, 0, j);
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j, int k) {
        if (k == 0) {
            return ashort;
        } else if (i >= 0 && i < this.size()) {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                ashort[j + l] = tshortlinkedlist_tshortlink.getValue();
                tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext();
            }

            return ashort;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TShortProcedure tshortprocedure) {
        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            if (!tshortprocedure.execute(tshortlinkedlist_tshortlink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TShortProcedure tshortprocedure) {
        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.tail; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getPrevious()) {
            if (!tshortprocedure.execute(tshortlinkedlist_tshortlink.getValue())) {
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
        TShortList tshortlist = this.subList(i, j);
        short[] ashort = tshortlist.toArray();

        Arrays.sort(ashort);
        this.set(i, ashort);
    }

    @Override
    public void fill(short short0) {
        this.fill(0, this.size, short0);
    }

    @Override
    public void fill(int i, int j, short short0) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);
            int k;

            if (j > this.size) {
                for (k = i; k < this.size; ++k) {
                    tshortlinkedlist_tshortlink.setValue(short0);
                    tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext();
                }

                for (k = this.size; k < j; ++k) {
                    this.add(short0);
                }
            } else {
                for (k = i; k < j; ++k) {
                    tshortlinkedlist_tshortlink.setValue(short0);
                    tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(short short0) {
        return this.binarySearch(short0, 0, this.size());
    }

    @Override
    public int binarySearch(short short0, int i, int j) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + j + " > " + this.size);
        } else if (j < i) {
            return -(i + 1);
        } else {
            int k = i;
            TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i);
            int l = j;

            while (k < l) {
                int i1 = k + l >>> 1;
                TShortLinkedList.TShortLink tshortlinkedlist_tshortlink1 = getLink(tshortlinkedlist_tshortlink, k, i1);

                if (tshortlinkedlist_tshortlink1.getValue() == short0) {
                    return i1;
                }

                if (tshortlinkedlist_tshortlink1.getValue() < short0) {
                    k = i1 + 1;
                    tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink1.next;
                } else {
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
        int j = i;

        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i); got(tshortlinkedlist_tshortlink.getNext()); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            if (tshortlinkedlist_tshortlink.getValue() == short0) {
                return j;
            }

            ++j;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(short short0) {
        return this.lastIndexOf(0, short0);
    }

    @Override
    public int lastIndexOf(int i, short short0) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int j = -1;
            int k = i;

            for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.getLinkAt(i); got(tshortlinkedlist_tshortlink.getNext()); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
                if (tshortlinkedlist_tshortlink.getValue() == short0) {
                    j = k;
                }

                ++k;
            }

            return j;
        }
    }

    @Override
    public boolean contains(short short0) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
                if (tshortlinkedlist_tshortlink.getValue() == short0) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TShortIterator iterator() {
        return new TShortIterator() {
            TShortLinkedList.TShortLink l;
            TShortLinkedList.TShortLink current;

            {
                this.l = TShortLinkedList.this.head;
            }

            @Override
            public short next() {
                if (TShortLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    short short0 = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return short0;
                }
            }

            @Override
            public boolean hasNext() {
                return TShortLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TShortLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TShortList grep(TShortProcedure tshortprocedure) {
        TShortLinkedList tshortlinkedlist = new TShortLinkedList();

        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            if (tshortprocedure.execute(tshortlinkedlist_tshortlink.getValue())) {
                tshortlinkedlist.add(tshortlinkedlist_tshortlink.getValue());
            }
        }

        return tshortlinkedlist;
    }

    @Override
    public TShortList inverseGrep(TShortProcedure tshortprocedure) {
        TShortLinkedList tshortlinkedlist = new TShortLinkedList();

        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            if (!tshortprocedure.execute(tshortlinkedlist_tshortlink.getValue())) {
                tshortlinkedlist.add(tshortlinkedlist_tshortlink.getValue());
            }
        }

        return tshortlinkedlist;
    }

    @Override
    public short max() {
        short short0 = Short.MIN_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
                if (short0 < tshortlinkedlist_tshortlink.getValue()) {
                    short0 = tshortlinkedlist_tshortlink.getValue();
                }
            }

            return short0;
        }
    }

    @Override
    public short min() {
        short short0 = 32767;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
                if (short0 > tshortlinkedlist_tshortlink.getValue()) {
                    short0 = tshortlinkedlist_tshortlink.getValue();
                }
            }

            return short0;
        }
    }

    @Override
    public short sum() {
        short short0 = 0;

        for (TShortLinkedList.TShortLink tshortlinkedlist_tshortlink = this.head; got(tshortlinkedlist_tshortlink); tshortlinkedlist_tshortlink = tshortlinkedlist_tshortlink.getNext()) {
            short0 += tshortlinkedlist_tshortlink.getValue();
        }

        return short0;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeShort(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            objectoutput.writeShort(short0);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readShort();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readShort());
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
            TShortLinkedList tshortlinkedlist = (TShortLinkedList) object;

            if (this.no_entry_value != tshortlinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tshortlinkedlist.size) {
                return false;
            } else {
                TShortIterator tshortiterator = this.iterator();
                TShortIterator tshortiterator1 = tshortlinkedlist.iterator();

                do {
                    if (!tshortiterator.hasNext()) {
                        return true;
                    }

                    if (!tshortiterator1.hasNext()) {
                        return false;
                    }
                } while (tshortiterator.next() == tshortiterator1.next());

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

        for (TShortIterator tshortiterator = this.iterator(); tshortiterator.hasNext(); i = 31 * i + HashFunctions.hash(tshortiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TShortIterator tshortiterator = this.iterator();

        while (tshortiterator.hasNext()) {
            short short0 = tshortiterator.next();

            stringbuilder.append(short0);
            if (tshortiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TShortProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(short short0) {
            if (TShortLinkedList.this.remove(short0)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TShortLink {

        short value;
        TShortLinkedList.TShortLink previous;
        TShortLinkedList.TShortLink next;

        TShortLink(short short0) {
            this.value = short0;
        }

        public short getValue() {
            return this.value;
        }

        public void setValue(short short0) {
            this.value = short0;
        }

        public TShortLinkedList.TShortLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TShortLinkedList.TShortLink tshortlinkedlist_tshortlink) {
            this.previous = tshortlinkedlist_tshortlink;
        }

        public TShortLinkedList.TShortLink getNext() {
            return this.next;
        }

        public void setNext(TShortLinkedList.TShortLink tshortlinkedlist_tshortlink) {
            this.next = tshortlinkedlist_tshortlink;
        }
    }
}
