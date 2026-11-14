package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TCharLinkedList implements TCharList, Externalizable {

    char no_entry_value;
    int size;
    TCharLinkedList.TCharLink head = null;
    TCharLinkedList.TCharLink tail;

    public TCharLinkedList() {
        this.tail = this.head;
    }

    public TCharLinkedList(char c0) {
        this.tail = this.head;
        this.no_entry_value = c0;
    }

    public TCharLinkedList(TCharList tcharlist) {
        this.tail = this.head;
        this.no_entry_value = tcharlist.getNoEntryValue();
        TCharIterator tchariterator = tcharlist.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            this.add(c0);
        }

    }

    @Override
    public char getNoEntryValue() {
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
    public boolean add(char c0) {
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = new TCharLinkedList.TCharLink(c0);

        if (no(this.head)) {
            this.head = tcharlinkedlist_tcharlink;
            this.tail = tcharlinkedlist_tcharlink;
        } else {
            tcharlinkedlist_tcharlink.setPrevious(this.tail);
            this.tail.setNext(tcharlinkedlist_tcharlink);
            this.tail = tcharlinkedlist_tcharlink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(char[] achar) {
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar1[j];

            this.add(c0);
        }

    }

    @Override
    public void add(char[] achar, int i, int j) {
        for (int k = 0; k < j; ++k) {
            char c0 = achar[i + k];

            this.add(c0);
        }

    }

    @Override
    public void insert(int i, char c0) {
        TCharLinkedList tcharlinkedlist = new TCharLinkedList();

        tcharlinkedlist.add(c0);
        this.insert(i, tcharlinkedlist);
    }

    @Override
    public void insert(int i, char[] achar) {
        this.insert(i, link(achar, 0, achar.length));
    }

    @Override
    public void insert(int i, char[] achar, int j, int k) {
        this.insert(i, link(achar, j, k));
    }

    void insert(int i, TCharLinkedList tcharlinkedlist) {
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

        this.size += tcharlinkedlist.size;
        if (tcharlinkedlist_tcharlink == this.head) {
            tcharlinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tcharlinkedlist.tail);
            this.head = tcharlinkedlist.head;
        } else {
            if (no(tcharlinkedlist_tcharlink)) {
                if (this.size == 0) {
                    this.head = tcharlinkedlist.head;
                    this.tail = tcharlinkedlist.tail;
                } else {
                    this.tail.setNext(tcharlinkedlist.head);
                    tcharlinkedlist.head.setPrevious(this.tail);
                    this.tail = tcharlinkedlist.tail;
                }
            } else {
                TCharLinkedList.TCharLink tcharlinkedlist_tcharlink1 = tcharlinkedlist_tcharlink.getPrevious();

                tcharlinkedlist_tcharlink.getPrevious().setNext(tcharlinkedlist.head);
                tcharlinkedlist.tail.setNext(tcharlinkedlist_tcharlink);
                tcharlinkedlist_tcharlink.setPrevious(tcharlinkedlist.tail);
                tcharlinkedlist.head.setPrevious(tcharlinkedlist_tcharlink1);
            }

        }
    }

    static TCharLinkedList link(char[] achar, int i, int j) {
        TCharLinkedList tcharlinkedlist = new TCharLinkedList();

        for (int k = 0; k < j; ++k) {
            tcharlinkedlist.add(achar[i + k]);
        }

        return tcharlinkedlist;
    }

    @Override
    public char get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

            return no(tcharlinkedlist_tcharlink) ? this.no_entry_value : tcharlinkedlist_tcharlink.getValue();
        }
    }

    public TCharLinkedList.TCharLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TCharLinkedList.TCharLink getLink(TCharLinkedList.TCharLink tcharlinkedlist_tcharlink, int i, int j) {
        return getLink(tcharlinkedlist_tcharlink, i, j, true);
    }

    private static TCharLinkedList.TCharLink getLink(TCharLinkedList.TCharLink tcharlinkedlist_tcharlink, int i, int j, boolean flag) {
        for (int k = i; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = flag ? tcharlinkedlist_tcharlink.getNext() : tcharlinkedlist_tcharlink.getPrevious()) {
            if (k == j) {
                return tcharlinkedlist_tcharlink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public char set(int i, char c0) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

            if (no(tcharlinkedlist_tcharlink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                char c1 = tcharlinkedlist_tcharlink.getValue();

                tcharlinkedlist_tcharlink.setValue(c0);
                return c1;
            }
        }
    }

    @Override
    public void set(int i, char[] achar) {
        this.set(i, achar, 0, achar.length);
    }

    @Override
    public void set(int i, char[] achar, int j, int k) {
        for (int l = 0; l < k; ++l) {
            char c0 = achar[j + l];

            this.set(i + l, c0);
        }

    }

    @Override
    public char replace(int i, char c0) {
        return this.set(i, c0);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(char c0) {
        boolean flag = false;

        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            if (tcharlinkedlist_tcharlink.getValue() == c0) {
                flag = true;
                this.removeLink(tcharlinkedlist_tcharlink);
            }
        }

        return flag;
    }

    private void removeLink(TCharLinkedList.TCharLink tcharlinkedlist_tcharlink) {
        if (!no(tcharlinkedlist_tcharlink)) {
            --this.size;
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink1 = tcharlinkedlist_tcharlink.getPrevious();
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink2 = tcharlinkedlist_tcharlink.getNext();

            if (got(tcharlinkedlist_tcharlink1)) {
                tcharlinkedlist_tcharlink1.setNext(tcharlinkedlist_tcharlink2);
            } else {
                this.head = tcharlinkedlist_tcharlink2;
            }

            if (got(tcharlinkedlist_tcharlink2)) {
                tcharlinkedlist_tcharlink2.setPrevious(tcharlinkedlist_tcharlink1);
            } else {
                this.tail = tcharlinkedlist_tcharlink1;
            }

            tcharlinkedlist_tcharlink.setNext((TCharLinkedList.TCharLink) null);
            tcharlinkedlist_tcharlink.setPrevious((TCharLinkedList.TCharLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Character character;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Character)) {
                    return false;
                }

                character = (Character) object;
            } while (this.contains(character));

            return false;
        }
    }

    @Override
    public boolean containsAll(TCharCollection tcharcollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            char[] achar1 = achar;
            int i = achar.length;

            for (int j = 0; j < i; ++j) {
                char c0 = achar1[j];

                if (!this.contains(c0)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Character character = (Character) iterator.next();

            if (this.add(character)) {
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

    @Override
    public boolean retainAll(char[] achar) {
        Arrays.sort(achar);
        boolean flag = false;
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            if (Arrays.binarySearch(achar, tchariterator.next()) < 0) {
                tchariterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            if (collection.contains(tchariterator.next())) {
                tchariterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TCharCollection tcharcollection) {
        boolean flag = false;
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            if (tcharcollection.contains(tchariterator.next())) {
                tchariterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(char[] achar) {
        Arrays.sort(achar);
        boolean flag = false;
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            if (Arrays.binarySearch(achar, tchariterator.next()) >= 0) {
                tchariterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public char removeAt(int i) {
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

        if (no(tcharlinkedlist_tcharlink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            char c0 = tcharlinkedlist_tcharlink.getValue();

            this.removeLink(tcharlinkedlist_tcharlink);
            return c0;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            tcharlinkedlist_tcharlink.setValue(tcharfunction.execute(tcharlinkedlist_tcharlink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head;
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink1 = this.tail;
        TCharLinkedList.TCharLink tcharlinkedlist_tcharlink2 = this.head;

        while (got(tcharlinkedlist_tcharlink2)) {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink3 = tcharlinkedlist_tcharlink2.getNext();
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink4 = tcharlinkedlist_tcharlink2.getPrevious();
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink5 = tcharlinkedlist_tcharlink2;

            tcharlinkedlist_tcharlink2 = tcharlinkedlist_tcharlink2.getNext();
            tcharlinkedlist_tcharlink5.setNext(tcharlinkedlist_tcharlink4);
            tcharlinkedlist_tcharlink5.setPrevious(tcharlinkedlist_tcharlink3);
        }

        this.head = tcharlinkedlist_tcharlink1;
        this.tail = tcharlinkedlist_tcharlink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink1 = this.getLinkAt(j);
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink2 = null;
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink3 = tcharlinkedlist_tcharlink.getPrevious();
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink4 = tcharlinkedlist_tcharlink;

            while (tcharlinkedlist_tcharlink4 != tcharlinkedlist_tcharlink1) {
                TCharLinkedList.TCharLink tcharlinkedlist_tcharlink5 = tcharlinkedlist_tcharlink4.getNext();
                TCharLinkedList.TCharLink tcharlinkedlist_tcharlink6 = tcharlinkedlist_tcharlink4.getPrevious();

                tcharlinkedlist_tcharlink2 = tcharlinkedlist_tcharlink4;
                tcharlinkedlist_tcharlink4 = tcharlinkedlist_tcharlink4.getNext();
                tcharlinkedlist_tcharlink2.setNext(tcharlinkedlist_tcharlink6);
                tcharlinkedlist_tcharlink2.setPrevious(tcharlinkedlist_tcharlink5);
            }

            if (got(tcharlinkedlist_tcharlink2)) {
                tcharlinkedlist_tcharlink3.setNext(tcharlinkedlist_tcharlink2);
                tcharlinkedlist_tcharlink1.setPrevious(tcharlinkedlist_tcharlink3);
            }

            tcharlinkedlist_tcharlink.setNext(tcharlinkedlist_tcharlink1);
            tcharlinkedlist_tcharlink1.setPrevious(tcharlinkedlist_tcharlink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tcharlinkedlist_tcharlink);
            this.add(tcharlinkedlist_tcharlink.getValue());
        }

    }

    @Override
    public TCharList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TCharLinkedList tcharlinkedlist = new TCharLinkedList();
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tcharlinkedlist.add(tcharlinkedlist_tcharlink.getValue());
                tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext();
            }

            return tcharlinkedlist;
        }
    }

    @Override
    public char[] toArray() {
        return this.toArray(new char[this.size], 0, this.size);
    }

    @Override
    public char[] toArray(int i, int j) {
        return this.toArray(new char[j], i, 0, j);
    }

    @Override
    public char[] toArray(char[] achar) {
        return this.toArray(achar, 0, this.size);
    }

    @Override
    public char[] toArray(char[] achar, int i, int j) {
        return this.toArray(achar, i, 0, j);
    }

    @Override
    public char[] toArray(char[] achar, int i, int j, int k) {
        if (k == 0) {
            return achar;
        } else if (i >= 0 && i < this.size()) {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                achar[j + l] = tcharlinkedlist_tcharlink.getValue();
                tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext();
            }

            return achar;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TCharProcedure tcharprocedure) {
        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            if (!tcharprocedure.execute(tcharlinkedlist_tcharlink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TCharProcedure tcharprocedure) {
        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.tail; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getPrevious()) {
            if (!tcharprocedure.execute(tcharlinkedlist_tcharlink.getValue())) {
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
        TCharList tcharlist = this.subList(i, j);
        char[] achar = tcharlist.toArray();

        Arrays.sort(achar);
        this.set(i, achar);
    }

    @Override
    public void fill(char c0) {
        this.fill(0, this.size, c0);
    }

    @Override
    public void fill(int i, int j, char c0) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);
            int k;

            if (j > this.size) {
                for (k = i; k < this.size; ++k) {
                    tcharlinkedlist_tcharlink.setValue(c0);
                    tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext();
                }

                for (k = this.size; k < j; ++k) {
                    this.add(c0);
                }
            } else {
                for (k = i; k < j; ++k) {
                    tcharlinkedlist_tcharlink.setValue(c0);
                    tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(char c0) {
        return this.binarySearch(c0, 0, this.size());
    }

    @Override
    public int binarySearch(char c0, int i, int j) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + j + " > " + this.size);
        } else if (j < i) {
            return -(i + 1);
        } else {
            int k = i;
            TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i);
            int l = j;

            while (k < l) {
                int i1 = k + l >>> 1;
                TCharLinkedList.TCharLink tcharlinkedlist_tcharlink1 = getLink(tcharlinkedlist_tcharlink, k, i1);

                if (tcharlinkedlist_tcharlink1.getValue() == c0) {
                    return i1;
                }

                if (tcharlinkedlist_tcharlink1.getValue() < c0) {
                    k = i1 + 1;
                    tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink1.next;
                } else {
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
        int j = i;

        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i); got(tcharlinkedlist_tcharlink.getNext()); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            if (tcharlinkedlist_tcharlink.getValue() == c0) {
                return j;
            }

            ++j;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(char c0) {
        return this.lastIndexOf(0, c0);
    }

    @Override
    public int lastIndexOf(int i, char c0) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int j = -1;
            int k = i;

            for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.getLinkAt(i); got(tcharlinkedlist_tcharlink.getNext()); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
                if (tcharlinkedlist_tcharlink.getValue() == c0) {
                    j = k;
                }

                ++k;
            }

            return j;
        }
    }

    @Override
    public boolean contains(char c0) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
                if (tcharlinkedlist_tcharlink.getValue() == c0) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TCharIterator iterator() {
        return new TCharIterator() {
            TCharLinkedList.TCharLink l;
            TCharLinkedList.TCharLink current;

            {
                this.l = TCharLinkedList.this.head;
            }

            @Override
            public char next() {
                if (TCharLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    char c0 = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return c0;
                }
            }

            @Override
            public boolean hasNext() {
                return TCharLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TCharLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TCharList grep(TCharProcedure tcharprocedure) {
        TCharLinkedList tcharlinkedlist = new TCharLinkedList();

        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            if (tcharprocedure.execute(tcharlinkedlist_tcharlink.getValue())) {
                tcharlinkedlist.add(tcharlinkedlist_tcharlink.getValue());
            }
        }

        return tcharlinkedlist;
    }

    @Override
    public TCharList inverseGrep(TCharProcedure tcharprocedure) {
        TCharLinkedList tcharlinkedlist = new TCharLinkedList();

        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            if (!tcharprocedure.execute(tcharlinkedlist_tcharlink.getValue())) {
                tcharlinkedlist.add(tcharlinkedlist_tcharlink.getValue());
            }
        }

        return tcharlinkedlist;
    }

    @Override
    public char max() {
        char c0 = 0;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
                if (c0 < tcharlinkedlist_tcharlink.getValue()) {
                    c0 = tcharlinkedlist_tcharlink.getValue();
                }
            }

            return c0;
        }
    }

    @Override
    public char min() {
        char c0 = '\uffff';

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
                if (c0 > tcharlinkedlist_tcharlink.getValue()) {
                    c0 = tcharlinkedlist_tcharlink.getValue();
                }
            }

            return c0;
        }
    }

    @Override
    public char sum() {
        char c0 = 0;

        for (TCharLinkedList.TCharLink tcharlinkedlist_tcharlink = this.head; got(tcharlinkedlist_tcharlink); tcharlinkedlist_tcharlink = tcharlinkedlist_tcharlink.getNext()) {
            c0 += tcharlinkedlist_tcharlink.getValue();
        }

        return c0;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeChar(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            objectoutput.writeChar(c0);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readChar();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readChar());
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
            TCharLinkedList tcharlinkedlist = (TCharLinkedList) object;

            if (this.no_entry_value != tcharlinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tcharlinkedlist.size) {
                return false;
            } else {
                TCharIterator tchariterator = this.iterator();
                TCharIterator tchariterator1 = tcharlinkedlist.iterator();

                do {
                    if (!tchariterator.hasNext()) {
                        return true;
                    }

                    if (!tchariterator1.hasNext()) {
                        return false;
                    }
                } while (tchariterator.next() == tchariterator1.next());

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

        for (TCharIterator tchariterator = this.iterator(); tchariterator.hasNext(); i = 31 * i + HashFunctions.hash(tchariterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TCharIterator tchariterator = this.iterator();

        while (tchariterator.hasNext()) {
            char c0 = tchariterator.next();

            stringbuilder.append(c0);
            if (tchariterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TCharProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(char c0) {
            if (TCharLinkedList.this.remove(c0)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TCharLink {

        char value;
        TCharLinkedList.TCharLink previous;
        TCharLinkedList.TCharLink next;

        TCharLink(char c0) {
            this.value = c0;
        }

        public char getValue() {
            return this.value;
        }

        public void setValue(char c0) {
            this.value = c0;
        }

        public TCharLinkedList.TCharLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TCharLinkedList.TCharLink tcharlinkedlist_tcharlink) {
            this.previous = tcharlinkedlist_tcharlink;
        }

        public TCharLinkedList.TCharLink getNext() {
            return this.next;
        }

        public void setNext(TCharLinkedList.TCharLink tcharlinkedlist_tcharlink) {
            this.next = tcharlinkedlist_tcharlink;
        }
    }
}
