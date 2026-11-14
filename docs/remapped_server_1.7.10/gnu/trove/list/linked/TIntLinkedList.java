package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TIntLinkedList implements TIntList, Externalizable {

    int no_entry_value;
    int size;
    TIntLinkedList.TIntLink head = null;
    TIntLinkedList.TIntLink tail;

    public TIntLinkedList() {
        this.tail = this.head;
    }

    public TIntLinkedList(int i) {
        this.tail = this.head;
        this.no_entry_value = i;
    }

    public TIntLinkedList(TIntList tintlist) {
        this.tail = this.head;
        this.no_entry_value = tintlist.getNoEntryValue();
        TIntIterator tintiterator = tintlist.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            this.add(i);
        }

    }

    @Override
    public int getNoEntryValue() {
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
    public boolean add(int i) {
        TIntLinkedList.TIntLink tintlinkedlist_tintlink = new TIntLinkedList.TIntLink(i);

        if (no(this.head)) {
            this.head = tintlinkedlist_tintlink;
            this.tail = tintlinkedlist_tintlink;
        } else {
            tintlinkedlist_tintlink.setPrevious(this.tail);
            this.tail.setNext(tintlinkedlist_tintlink);
            this.tail = tintlinkedlist_tintlink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(int[] aint) {
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            this.add(k);
        }

    }

    @Override
    public void add(int[] aint, int i, int j) {
        for (int k = 0; k < j; ++k) {
            int l = aint[i + k];

            this.add(l);
        }

    }

    @Override
    public void insert(int i, int j) {
        TIntLinkedList tintlinkedlist = new TIntLinkedList();

        tintlinkedlist.add(j);
        this.insert(i, tintlinkedlist);
    }

    @Override
    public void insert(int i, int[] aint) {
        this.insert(i, link(aint, 0, aint.length));
    }

    @Override
    public void insert(int i, int[] aint, int j, int k) {
        this.insert(i, link(aint, j, k));
    }

    void insert(int i, TIntLinkedList tintlinkedlist) {
        TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

        this.size += tintlinkedlist.size;
        if (tintlinkedlist_tintlink == this.head) {
            tintlinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tintlinkedlist.tail);
            this.head = tintlinkedlist.head;
        } else {
            if (no(tintlinkedlist_tintlink)) {
                if (this.size == 0) {
                    this.head = tintlinkedlist.head;
                    this.tail = tintlinkedlist.tail;
                } else {
                    this.tail.setNext(tintlinkedlist.head);
                    tintlinkedlist.head.setPrevious(this.tail);
                    this.tail = tintlinkedlist.tail;
                }
            } else {
                TIntLinkedList.TIntLink tintlinkedlist_tintlink1 = tintlinkedlist_tintlink.getPrevious();

                tintlinkedlist_tintlink.getPrevious().setNext(tintlinkedlist.head);
                tintlinkedlist.tail.setNext(tintlinkedlist_tintlink);
                tintlinkedlist_tintlink.setPrevious(tintlinkedlist.tail);
                tintlinkedlist.head.setPrevious(tintlinkedlist_tintlink1);
            }

        }
    }

    static TIntLinkedList link(int[] aint, int i, int j) {
        TIntLinkedList tintlinkedlist = new TIntLinkedList();

        for (int k = 0; k < j; ++k) {
            tintlinkedlist.add(aint[i + k]);
        }

        return tintlinkedlist;
    }

    @Override
    public int get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

            return no(tintlinkedlist_tintlink) ? this.no_entry_value : tintlinkedlist_tintlink.getValue();
        }
    }

    public TIntLinkedList.TIntLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TIntLinkedList.TIntLink getLink(TIntLinkedList.TIntLink tintlinkedlist_tintlink, int i, int j) {
        return getLink(tintlinkedlist_tintlink, i, j, true);
    }

    private static TIntLinkedList.TIntLink getLink(TIntLinkedList.TIntLink tintlinkedlist_tintlink, int i, int j, boolean flag) {
        for (int k = i; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = flag ? tintlinkedlist_tintlink.getNext() : tintlinkedlist_tintlink.getPrevious()) {
            if (k == j) {
                return tintlinkedlist_tintlink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public int set(int i, int j) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

            if (no(tintlinkedlist_tintlink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                int k = tintlinkedlist_tintlink.getValue();

                tintlinkedlist_tintlink.setValue(j);
                return k;
            }
        }
    }

    @Override
    public void set(int i, int[] aint) {
        this.set(i, aint, 0, aint.length);
    }

    @Override
    public void set(int i, int[] aint, int j, int k) {
        for (int l = 0; l < k; ++l) {
            int i1 = aint[j + l];

            this.set(i + l, i1);
        }

    }

    @Override
    public int replace(int i, int j) {
        return this.set(i, j);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(int i) {
        boolean flag = false;

        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            if (tintlinkedlist_tintlink.getValue() == i) {
                flag = true;
                this.removeLink(tintlinkedlist_tintlink);
            }
        }

        return flag;
    }

    private void removeLink(TIntLinkedList.TIntLink tintlinkedlist_tintlink) {
        if (!no(tintlinkedlist_tintlink)) {
            --this.size;
            TIntLinkedList.TIntLink tintlinkedlist_tintlink1 = tintlinkedlist_tintlink.getPrevious();
            TIntLinkedList.TIntLink tintlinkedlist_tintlink2 = tintlinkedlist_tintlink.getNext();

            if (got(tintlinkedlist_tintlink1)) {
                tintlinkedlist_tintlink1.setNext(tintlinkedlist_tintlink2);
            } else {
                this.head = tintlinkedlist_tintlink2;
            }

            if (got(tintlinkedlist_tintlink2)) {
                tintlinkedlist_tintlink2.setPrevious(tintlinkedlist_tintlink1);
            } else {
                this.tail = tintlinkedlist_tintlink1;
            }

            tintlinkedlist_tintlink.setNext((TIntLinkedList.TIntLink) null);
            tintlinkedlist_tintlink.setPrevious((TIntLinkedList.TIntLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Integer integer;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Integer)) {
                    return false;
                }

                integer = (Integer) object;
            } while (this.contains(integer));

            return false;
        }
    }

    @Override
    public boolean containsAll(TIntCollection tintcollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            int[] aint1 = aint;
            int i = aint.length;

            for (int j = 0; j < i; ++j) {
                int k = aint1[j];

                if (!this.contains(k)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();

            if (this.add(integer)) {
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

    @Override
    public boolean retainAll(int[] aint) {
        Arrays.sort(aint);
        boolean flag = false;
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            if (Arrays.binarySearch(aint, tintiterator.next()) < 0) {
                tintiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            if (collection.contains(tintiterator.next())) {
                tintiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TIntCollection tintcollection) {
        boolean flag = false;
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            if (tintcollection.contains(tintiterator.next())) {
                tintiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(int[] aint) {
        Arrays.sort(aint);
        boolean flag = false;
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            if (Arrays.binarySearch(aint, tintiterator.next()) >= 0) {
                tintiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public int removeAt(int i) {
        TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

        if (no(tintlinkedlist_tintlink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            int j = tintlinkedlist_tintlink.getValue();

            this.removeLink(tintlinkedlist_tintlink);
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
    public void transformValues(TIntFunction tintfunction) {
        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            tintlinkedlist_tintlink.setValue(tintfunction.execute(tintlinkedlist_tintlink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head;
        TIntLinkedList.TIntLink tintlinkedlist_tintlink1 = this.tail;
        TIntLinkedList.TIntLink tintlinkedlist_tintlink2 = this.head;

        while (got(tintlinkedlist_tintlink2)) {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink3 = tintlinkedlist_tintlink2.getNext();
            TIntLinkedList.TIntLink tintlinkedlist_tintlink4 = tintlinkedlist_tintlink2.getPrevious();
            TIntLinkedList.TIntLink tintlinkedlist_tintlink5 = tintlinkedlist_tintlink2;

            tintlinkedlist_tintlink2 = tintlinkedlist_tintlink2.getNext();
            tintlinkedlist_tintlink5.setNext(tintlinkedlist_tintlink4);
            tintlinkedlist_tintlink5.setPrevious(tintlinkedlist_tintlink3);
        }

        this.head = tintlinkedlist_tintlink1;
        this.tail = tintlinkedlist_tintlink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);
            TIntLinkedList.TIntLink tintlinkedlist_tintlink1 = this.getLinkAt(j);
            TIntLinkedList.TIntLink tintlinkedlist_tintlink2 = null;
            TIntLinkedList.TIntLink tintlinkedlist_tintlink3 = tintlinkedlist_tintlink.getPrevious();
            TIntLinkedList.TIntLink tintlinkedlist_tintlink4 = tintlinkedlist_tintlink;

            while (tintlinkedlist_tintlink4 != tintlinkedlist_tintlink1) {
                TIntLinkedList.TIntLink tintlinkedlist_tintlink5 = tintlinkedlist_tintlink4.getNext();
                TIntLinkedList.TIntLink tintlinkedlist_tintlink6 = tintlinkedlist_tintlink4.getPrevious();

                tintlinkedlist_tintlink2 = tintlinkedlist_tintlink4;
                tintlinkedlist_tintlink4 = tintlinkedlist_tintlink4.getNext();
                tintlinkedlist_tintlink2.setNext(tintlinkedlist_tintlink6);
                tintlinkedlist_tintlink2.setPrevious(tintlinkedlist_tintlink5);
            }

            if (got(tintlinkedlist_tintlink2)) {
                tintlinkedlist_tintlink3.setNext(tintlinkedlist_tintlink2);
                tintlinkedlist_tintlink1.setPrevious(tintlinkedlist_tintlink3);
            }

            tintlinkedlist_tintlink.setNext(tintlinkedlist_tintlink1);
            tintlinkedlist_tintlink1.setPrevious(tintlinkedlist_tintlink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tintlinkedlist_tintlink);
            this.add(tintlinkedlist_tintlink.getValue());
        }

    }

    @Override
    public TIntList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TIntLinkedList tintlinkedlist = new TIntLinkedList();
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tintlinkedlist.add(tintlinkedlist_tintlink.getValue());
                tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext();
            }

            return tintlinkedlist;
        }
    }

    @Override
    public int[] toArray() {
        return this.toArray(new int[this.size], 0, this.size);
    }

    @Override
    public int[] toArray(int i, int j) {
        return this.toArray(new int[j], i, 0, j);
    }

    @Override
    public int[] toArray(int[] aint) {
        return this.toArray(aint, 0, this.size);
    }

    @Override
    public int[] toArray(int[] aint, int i, int j) {
        return this.toArray(aint, i, 0, j);
    }

    @Override
    public int[] toArray(int[] aint, int i, int j, int k) {
        if (k == 0) {
            return aint;
        } else if (i >= 0 && i < this.size()) {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                aint[j + l] = tintlinkedlist_tintlink.getValue();
                tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext();
            }

            return aint;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TIntProcedure tintprocedure) {
        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            if (!tintprocedure.execute(tintlinkedlist_tintlink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TIntProcedure tintprocedure) {
        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.tail; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getPrevious()) {
            if (!tintprocedure.execute(tintlinkedlist_tintlink.getValue())) {
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
        TIntList tintlist = this.subList(i, j);
        int[] aint = tintlist.toArray();

        Arrays.sort(aint);
        this.set(i, aint);
    }

    @Override
    public void fill(int i) {
        this.fill(0, this.size, i);
    }

    @Override
    public void fill(int i, int j, int k) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i);
            int l;

            if (j > this.size) {
                for (l = i; l < this.size; ++l) {
                    tintlinkedlist_tintlink.setValue(k);
                    tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext();
                }

                for (l = this.size; l < j; ++l) {
                    this.add(k);
                }
            } else {
                for (l = i; l < j; ++l) {
                    tintlinkedlist_tintlink.setValue(k);
                    tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(int i) {
        return this.binarySearch(i, 0, this.size());
    }

    @Override
    public int binarySearch(int i, int j, int k) {
        if (j < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (k > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + k + " > " + this.size);
        } else if (k < j) {
            return -(j + 1);
        } else {
            int l = j;
            TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(j);
            int i1 = k;

            while (l < i1) {
                int j1 = l + i1 >>> 1;
                TIntLinkedList.TIntLink tintlinkedlist_tintlink1 = getLink(tintlinkedlist_tintlink, l, j1);

                if (tintlinkedlist_tintlink1.getValue() == i) {
                    return j1;
                }

                if (tintlinkedlist_tintlink1.getValue() < i) {
                    l = j1 + 1;
                    tintlinkedlist_tintlink = tintlinkedlist_tintlink1.next;
                } else {
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
        int k = i;

        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i); got(tintlinkedlist_tintlink.getNext()); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            if (tintlinkedlist_tintlink.getValue() == j) {
                return k;
            }

            ++k;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int i) {
        return this.lastIndexOf(0, i);
    }

    @Override
    public int lastIndexOf(int i, int j) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int k = -1;
            int l = i;

            for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.getLinkAt(i); got(tintlinkedlist_tintlink.getNext()); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
                if (tintlinkedlist_tintlink.getValue() == j) {
                    k = l;
                }

                ++l;
            }

            return k;
        }
    }

    @Override
    public boolean contains(int i) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
                if (tintlinkedlist_tintlink.getValue() == i) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TIntIterator iterator() {
        return new TIntIterator() {
            TIntLinkedList.TIntLink l;
            TIntLinkedList.TIntLink current;

            {
                this.l = TIntLinkedList.this.head;
            }

            @Override
            public int next() {
                if (TIntLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    int i = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return i;
                }
            }

            @Override
            public boolean hasNext() {
                return TIntLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TIntLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TIntList grep(TIntProcedure tintprocedure) {
        TIntLinkedList tintlinkedlist = new TIntLinkedList();

        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            if (tintprocedure.execute(tintlinkedlist_tintlink.getValue())) {
                tintlinkedlist.add(tintlinkedlist_tintlink.getValue());
            }
        }

        return tintlinkedlist;
    }

    @Override
    public TIntList inverseGrep(TIntProcedure tintprocedure) {
        TIntLinkedList tintlinkedlist = new TIntLinkedList();

        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            if (!tintprocedure.execute(tintlinkedlist_tintlink.getValue())) {
                tintlinkedlist.add(tintlinkedlist_tintlink.getValue());
            }
        }

        return tintlinkedlist;
    }

    @Override
    public int max() {
        int i = Integer.MIN_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
                if (i < tintlinkedlist_tintlink.getValue()) {
                    i = tintlinkedlist_tintlink.getValue();
                }
            }

            return i;
        }
    }

    @Override
    public int min() {
        int i = Integer.MAX_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
                if (i > tintlinkedlist_tintlink.getValue()) {
                    i = tintlinkedlist_tintlink.getValue();
                }
            }

            return i;
        }
    }

    @Override
    public int sum() {
        int i = 0;

        for (TIntLinkedList.TIntLink tintlinkedlist_tintlink = this.head; got(tintlinkedlist_tintlink); tintlinkedlist_tintlink = tintlinkedlist_tintlink.getNext()) {
            i += tintlinkedlist_tintlink.getValue();
        }

        return i;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeInt(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            objectoutput.writeInt(i);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readInt();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readInt());
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
            TIntLinkedList tintlinkedlist = (TIntLinkedList) object;

            if (this.no_entry_value != tintlinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tintlinkedlist.size) {
                return false;
            } else {
                TIntIterator tintiterator = this.iterator();
                TIntIterator tintiterator1 = tintlinkedlist.iterator();

                do {
                    if (!tintiterator.hasNext()) {
                        return true;
                    }

                    if (!tintiterator1.hasNext()) {
                        return false;
                    }
                } while (tintiterator.next() == tintiterator1.next());

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

        for (TIntIterator tintiterator = this.iterator(); tintiterator.hasNext(); i = 31 * i + HashFunctions.hash(tintiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TIntIterator tintiterator = this.iterator();

        while (tintiterator.hasNext()) {
            int i = tintiterator.next();

            stringbuilder.append(i);
            if (tintiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TIntProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(int i) {
            if (TIntLinkedList.this.remove(i)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TIntLink {

        int value;
        TIntLinkedList.TIntLink previous;
        TIntLinkedList.TIntLink next;

        TIntLink(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int i) {
            this.value = i;
        }

        public TIntLinkedList.TIntLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TIntLinkedList.TIntLink tintlinkedlist_tintlink) {
            this.previous = tintlinkedlist_tintlink;
        }

        public TIntLinkedList.TIntLink getNext() {
            return this.next;
        }

        public void setNext(TIntLinkedList.TIntLink tintlinkedlist_tintlink) {
            this.next = tintlinkedlist_tintlink;
        }
    }
}
