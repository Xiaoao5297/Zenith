package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TLongLinkedList implements TLongList, Externalizable {

    long no_entry_value;
    int size;
    TLongLinkedList.TLongLink head = null;
    TLongLinkedList.TLongLink tail;

    public TLongLinkedList() {
        this.tail = this.head;
    }

    public TLongLinkedList(long i) {
        this.tail = this.head;
        this.no_entry_value = i;
    }

    public TLongLinkedList(TLongList tlonglist) {
        this.tail = this.head;
        this.no_entry_value = tlonglist.getNoEntryValue();
        TLongIterator tlongiterator = tlonglist.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            this.add(i);
        }

    }

    @Override
    public long getNoEntryValue() {
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
    public boolean add(long i) {
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = new TLongLinkedList.TLongLink(i);

        if (no(this.head)) {
            this.head = tlonglinkedlist_tlonglink;
            this.tail = tlonglinkedlist_tlonglink;
        } else {
            tlonglinkedlist_tlonglink.setPrevious(this.tail);
            this.tail.setNext(tlonglinkedlist_tlonglink);
            this.tail = tlonglinkedlist_tlonglink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(long[] along) {
        long[] along1 = along;
        int i = along.length;

        for (int j = 0; j < i; ++j) {
            long k = along1[j];

            this.add(k);
        }

    }

    @Override
    public void add(long[] along, int i, int j) {
        for (int k = 0; k < j; ++k) {
            long l = along[i + k];

            this.add(l);
        }

    }

    @Override
    public void insert(int i, long j) {
        TLongLinkedList tlonglinkedlist = new TLongLinkedList();

        tlonglinkedlist.add(j);
        this.insert(i, tlonglinkedlist);
    }

    @Override
    public void insert(int i, long[] along) {
        this.insert(i, link(along, 0, along.length));
    }

    @Override
    public void insert(int i, long[] along, int j, int k) {
        this.insert(i, link(along, j, k));
    }

    void insert(int i, TLongLinkedList tlonglinkedlist) {
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

        this.size += tlonglinkedlist.size;
        if (tlonglinkedlist_tlonglink == this.head) {
            tlonglinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tlonglinkedlist.tail);
            this.head = tlonglinkedlist.head;
        } else {
            if (no(tlonglinkedlist_tlonglink)) {
                if (this.size == 0) {
                    this.head = tlonglinkedlist.head;
                    this.tail = tlonglinkedlist.tail;
                } else {
                    this.tail.setNext(tlonglinkedlist.head);
                    tlonglinkedlist.head.setPrevious(this.tail);
                    this.tail = tlonglinkedlist.tail;
                }
            } else {
                TLongLinkedList.TLongLink tlonglinkedlist_tlonglink1 = tlonglinkedlist_tlonglink.getPrevious();

                tlonglinkedlist_tlonglink.getPrevious().setNext(tlonglinkedlist.head);
                tlonglinkedlist.tail.setNext(tlonglinkedlist_tlonglink);
                tlonglinkedlist_tlonglink.setPrevious(tlonglinkedlist.tail);
                tlonglinkedlist.head.setPrevious(tlonglinkedlist_tlonglink1);
            }

        }
    }

    static TLongLinkedList link(long[] along, int i, int j) {
        TLongLinkedList tlonglinkedlist = new TLongLinkedList();

        for (int k = 0; k < j; ++k) {
            tlonglinkedlist.add(along[i + k]);
        }

        return tlonglinkedlist;
    }

    @Override
    public long get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

            return no(tlonglinkedlist_tlonglink) ? this.no_entry_value : tlonglinkedlist_tlonglink.getValue();
        }
    }

    public TLongLinkedList.TLongLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TLongLinkedList.TLongLink getLink(TLongLinkedList.TLongLink tlonglinkedlist_tlonglink, int i, int j) {
        return getLink(tlonglinkedlist_tlonglink, i, j, true);
    }

    private static TLongLinkedList.TLongLink getLink(TLongLinkedList.TLongLink tlonglinkedlist_tlonglink, int i, int j, boolean flag) {
        for (int k = i; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = flag ? tlonglinkedlist_tlonglink.getNext() : tlonglinkedlist_tlonglink.getPrevious()) {
            if (k == j) {
                return tlonglinkedlist_tlonglink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public long set(int i, long j) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

            if (no(tlonglinkedlist_tlonglink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                long k = tlonglinkedlist_tlonglink.getValue();

                tlonglinkedlist_tlonglink.setValue(j);
                return k;
            }
        }
    }

    @Override
    public void set(int i, long[] along) {
        this.set(i, along, 0, along.length);
    }

    @Override
    public void set(int i, long[] along, int j, int k) {
        for (int l = 0; l < k; ++l) {
            long i1 = along[j + l];

            this.set(i + l, i1);
        }

    }

    @Override
    public long replace(int i, long j) {
        return this.set(i, j);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(long i) {
        boolean flag = false;

        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            if (tlonglinkedlist_tlonglink.getValue() == i) {
                flag = true;
                this.removeLink(tlonglinkedlist_tlonglink);
            }
        }

        return flag;
    }

    private void removeLink(TLongLinkedList.TLongLink tlonglinkedlist_tlonglink) {
        if (!no(tlonglinkedlist_tlonglink)) {
            --this.size;
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink1 = tlonglinkedlist_tlonglink.getPrevious();
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink2 = tlonglinkedlist_tlonglink.getNext();

            if (got(tlonglinkedlist_tlonglink1)) {
                tlonglinkedlist_tlonglink1.setNext(tlonglinkedlist_tlonglink2);
            } else {
                this.head = tlonglinkedlist_tlonglink2;
            }

            if (got(tlonglinkedlist_tlonglink2)) {
                tlonglinkedlist_tlonglink2.setPrevious(tlonglinkedlist_tlonglink1);
            } else {
                this.tail = tlonglinkedlist_tlonglink1;
            }

            tlonglinkedlist_tlonglink.setNext((TLongLinkedList.TLongLink) null);
            tlonglinkedlist_tlonglink.setPrevious((TLongLinkedList.TLongLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Long olong;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Long)) {
                    return false;
                }

                olong = (Long) object;
            } while (this.contains(olong));

            return false;
        }
    }

    @Override
    public boolean containsAll(TLongCollection tlongcollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            long[] along1 = along;
            int i = along.length;

            for (int j = 0; j < i; ++j) {
                long k = along1[j];

                if (!this.contains(k)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Long olong = (Long) iterator.next();

            if (this.add(olong)) {
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

    @Override
    public boolean retainAll(long[] along) {
        Arrays.sort(along);
        boolean flag = false;
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            if (Arrays.binarySearch(along, tlongiterator.next()) < 0) {
                tlongiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            if (collection.contains(tlongiterator.next())) {
                tlongiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TLongCollection tlongcollection) {
        boolean flag = false;
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            if (tlongcollection.contains(tlongiterator.next())) {
                tlongiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(long[] along) {
        Arrays.sort(along);
        boolean flag = false;
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            if (Arrays.binarySearch(along, tlongiterator.next()) >= 0) {
                tlongiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public long removeAt(int i) {
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

        if (no(tlonglinkedlist_tlonglink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            long j = tlonglinkedlist_tlonglink.getValue();

            this.removeLink(tlonglinkedlist_tlonglink);
            return j;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            tlonglinkedlist_tlonglink.setValue(tlongfunction.execute(tlonglinkedlist_tlonglink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head;
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink1 = this.tail;
        TLongLinkedList.TLongLink tlonglinkedlist_tlonglink2 = this.head;

        while (got(tlonglinkedlist_tlonglink2)) {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink3 = tlonglinkedlist_tlonglink2.getNext();
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink4 = tlonglinkedlist_tlonglink2.getPrevious();
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink5 = tlonglinkedlist_tlonglink2;

            tlonglinkedlist_tlonglink2 = tlonglinkedlist_tlonglink2.getNext();
            tlonglinkedlist_tlonglink5.setNext(tlonglinkedlist_tlonglink4);
            tlonglinkedlist_tlonglink5.setPrevious(tlonglinkedlist_tlonglink3);
        }

        this.head = tlonglinkedlist_tlonglink1;
        this.tail = tlonglinkedlist_tlonglink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink1 = this.getLinkAt(j);
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink2 = null;
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink3 = tlonglinkedlist_tlonglink.getPrevious();
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink4 = tlonglinkedlist_tlonglink;

            while (tlonglinkedlist_tlonglink4 != tlonglinkedlist_tlonglink1) {
                TLongLinkedList.TLongLink tlonglinkedlist_tlonglink5 = tlonglinkedlist_tlonglink4.getNext();
                TLongLinkedList.TLongLink tlonglinkedlist_tlonglink6 = tlonglinkedlist_tlonglink4.getPrevious();

                tlonglinkedlist_tlonglink2 = tlonglinkedlist_tlonglink4;
                tlonglinkedlist_tlonglink4 = tlonglinkedlist_tlonglink4.getNext();
                tlonglinkedlist_tlonglink2.setNext(tlonglinkedlist_tlonglink6);
                tlonglinkedlist_tlonglink2.setPrevious(tlonglinkedlist_tlonglink5);
            }

            if (got(tlonglinkedlist_tlonglink2)) {
                tlonglinkedlist_tlonglink3.setNext(tlonglinkedlist_tlonglink2);
                tlonglinkedlist_tlonglink1.setPrevious(tlonglinkedlist_tlonglink3);
            }

            tlonglinkedlist_tlonglink.setNext(tlonglinkedlist_tlonglink1);
            tlonglinkedlist_tlonglink1.setPrevious(tlonglinkedlist_tlonglink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tlonglinkedlist_tlonglink);
            this.add(tlonglinkedlist_tlonglink.getValue());
        }

    }

    @Override
    public TLongList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TLongLinkedList tlonglinkedlist = new TLongLinkedList();
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tlonglinkedlist.add(tlonglinkedlist_tlonglink.getValue());
                tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext();
            }

            return tlonglinkedlist;
        }
    }

    @Override
    public long[] toArray() {
        return this.toArray(new long[this.size], 0, this.size);
    }

    @Override
    public long[] toArray(int i, int j) {
        return this.toArray(new long[j], i, 0, j);
    }

    @Override
    public long[] toArray(long[] along) {
        return this.toArray(along, 0, this.size);
    }

    @Override
    public long[] toArray(long[] along, int i, int j) {
        return this.toArray(along, i, 0, j);
    }

    @Override
    public long[] toArray(long[] along, int i, int j, int k) {
        if (k == 0) {
            return along;
        } else if (i >= 0 && i < this.size()) {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                along[j + l] = tlonglinkedlist_tlonglink.getValue();
                tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext();
            }

            return along;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TLongProcedure tlongprocedure) {
        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            if (!tlongprocedure.execute(tlonglinkedlist_tlonglink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TLongProcedure tlongprocedure) {
        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.tail; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getPrevious()) {
            if (!tlongprocedure.execute(tlonglinkedlist_tlonglink.getValue())) {
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
        TLongList tlonglist = this.subList(i, j);
        long[] along = tlonglist.toArray();

        Arrays.sort(along);
        this.set(i, along);
    }

    @Override
    public void fill(long i) {
        this.fill(0, this.size, i);
    }

    @Override
    public void fill(int i, int j, long k) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i);
            int l;

            if (j > this.size) {
                for (l = i; l < this.size; ++l) {
                    tlonglinkedlist_tlonglink.setValue(k);
                    tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext();
                }

                for (l = this.size; l < j; ++l) {
                    this.add(k);
                }
            } else {
                for (l = i; l < j; ++l) {
                    tlonglinkedlist_tlonglink.setValue(k);
                    tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(long i) {
        return this.binarySearch(i, 0, this.size());
    }

    @Override
    public int binarySearch(long i, int j, int k) {
        if (j < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (k > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + k + " > " + this.size);
        } else if (k < j) {
            return -(j + 1);
        } else {
            int l = j;
            TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(j);
            int i1 = k;

            while (l < i1) {
                int j1 = l + i1 >>> 1;
                TLongLinkedList.TLongLink tlonglinkedlist_tlonglink1 = getLink(tlonglinkedlist_tlonglink, l, j1);

                if (tlonglinkedlist_tlonglink1.getValue() == i) {
                    return j1;
                }

                if (tlonglinkedlist_tlonglink1.getValue() < i) {
                    l = j1 + 1;
                    tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink1.next;
                } else {
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
        int k = i;

        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i); got(tlonglinkedlist_tlonglink.getNext()); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            if (tlonglinkedlist_tlonglink.getValue() == j) {
                return k;
            }

            ++k;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(long i) {
        return this.lastIndexOf(0, i);
    }

    @Override
    public int lastIndexOf(int i, long j) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int k = -1;
            int l = i;

            for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.getLinkAt(i); got(tlonglinkedlist_tlonglink.getNext()); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
                if (tlonglinkedlist_tlonglink.getValue() == j) {
                    k = l;
                }

                ++l;
            }

            return k;
        }
    }

    @Override
    public boolean contains(long i) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
                if (tlonglinkedlist_tlonglink.getValue() == i) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TLongIterator iterator() {
        return new TLongIterator() {
            TLongLinkedList.TLongLink l;
            TLongLinkedList.TLongLink current;

            {
                this.l = TLongLinkedList.this.head;
            }

            @Override
            public long next() {
                if (TLongLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    long i = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return i;
                }
            }

            @Override
            public boolean hasNext() {
                return TLongLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TLongLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TLongList grep(TLongProcedure tlongprocedure) {
        TLongLinkedList tlonglinkedlist = new TLongLinkedList();

        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            if (tlongprocedure.execute(tlonglinkedlist_tlonglink.getValue())) {
                tlonglinkedlist.add(tlonglinkedlist_tlonglink.getValue());
            }
        }

        return tlonglinkedlist;
    }

    @Override
    public TLongList inverseGrep(TLongProcedure tlongprocedure) {
        TLongLinkedList tlonglinkedlist = new TLongLinkedList();

        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            if (!tlongprocedure.execute(tlonglinkedlist_tlonglink.getValue())) {
                tlonglinkedlist.add(tlonglinkedlist_tlonglink.getValue());
            }
        }

        return tlonglinkedlist;
    }

    @Override
    public long max() {
        long i = Long.MIN_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
                if (i < tlonglinkedlist_tlonglink.getValue()) {
                    i = tlonglinkedlist_tlonglink.getValue();
                }
            }

            return i;
        }
    }

    @Override
    public long min() {
        long i = Long.MAX_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
                if (i > tlonglinkedlist_tlonglink.getValue()) {
                    i = tlonglinkedlist_tlonglink.getValue();
                }
            }

            return i;
        }
    }

    @Override
    public long sum() {
        long i = 0L;

        for (TLongLinkedList.TLongLink tlonglinkedlist_tlonglink = this.head; got(tlonglinkedlist_tlonglink); tlonglinkedlist_tlonglink = tlonglinkedlist_tlonglink.getNext()) {
            i += tlonglinkedlist_tlonglink.getValue();
        }

        return i;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeLong(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            objectoutput.writeLong(i);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readLong();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readLong());
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
            TLongLinkedList tlonglinkedlist = (TLongLinkedList) object;

            if (this.no_entry_value != tlonglinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tlonglinkedlist.size) {
                return false;
            } else {
                TLongIterator tlongiterator = this.iterator();
                TLongIterator tlongiterator1 = tlonglinkedlist.iterator();

                do {
                    if (!tlongiterator.hasNext()) {
                        return true;
                    }

                    if (!tlongiterator1.hasNext()) {
                        return false;
                    }
                } while (tlongiterator.next() == tlongiterator1.next());

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

        for (TLongIterator tlongiterator = this.iterator(); tlongiterator.hasNext(); i = 31 * i + HashFunctions.hash(tlongiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TLongIterator tlongiterator = this.iterator();

        while (tlongiterator.hasNext()) {
            long i = tlongiterator.next();

            stringbuilder.append(i);
            if (tlongiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TLongProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(long i) {
            if (TLongLinkedList.this.remove(i)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TLongLink {

        long value;
        TLongLinkedList.TLongLink previous;
        TLongLinkedList.TLongLink next;

        TLongLink(long i) {
            this.value = i;
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long i) {
            this.value = i;
        }

        public TLongLinkedList.TLongLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TLongLinkedList.TLongLink tlonglinkedlist_tlonglink) {
            this.previous = tlonglinkedlist_tlonglink;
        }

        public TLongLinkedList.TLongLink getNext() {
            return this.next;
        }

        public void setNext(TLongLinkedList.TLongLink tlonglinkedlist_tlonglink) {
            this.next = tlonglinkedlist_tlonglink;
        }
    }
}
