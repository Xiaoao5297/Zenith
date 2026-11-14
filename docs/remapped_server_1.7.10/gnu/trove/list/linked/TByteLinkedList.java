package gnu.trove.list.linked;

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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TByteLinkedList implements TByteList, Externalizable {

    byte no_entry_value;
    int size;
    TByteLinkedList.TByteLink head = null;
    TByteLinkedList.TByteLink tail;

    public TByteLinkedList() {
        this.tail = this.head;
    }

    public TByteLinkedList(byte b0) {
        this.tail = this.head;
        this.no_entry_value = b0;
    }

    public TByteLinkedList(TByteList tbytelist) {
        this.tail = this.head;
        this.no_entry_value = tbytelist.getNoEntryValue();
        TByteIterator tbyteiterator = tbytelist.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            this.add(b0);
        }

    }

    @Override
    public byte getNoEntryValue() {
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
    public boolean add(byte b0) {
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = new TByteLinkedList.TByteLink(b0);

        if (no(this.head)) {
            this.head = tbytelinkedlist_tbytelink;
            this.tail = tbytelinkedlist_tbytelink;
        } else {
            tbytelinkedlist_tbytelink.setPrevious(this.tail);
            this.tail.setNext(tbytelinkedlist_tbytelink);
            this.tail = tbytelinkedlist_tbytelink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(byte[] abyte) {
        byte[] abyte1 = abyte;
        int i = abyte.length;

        for (int j = 0; j < i; ++j) {
            byte b0 = abyte1[j];

            this.add(b0);
        }

    }

    @Override
    public void add(byte[] abyte, int i, int j) {
        for (int k = 0; k < j; ++k) {
            byte b0 = abyte[i + k];

            this.add(b0);
        }

    }

    @Override
    public void insert(int i, byte b0) {
        TByteLinkedList tbytelinkedlist = new TByteLinkedList();

        tbytelinkedlist.add(b0);
        this.insert(i, tbytelinkedlist);
    }

    @Override
    public void insert(int i, byte[] abyte) {
        this.insert(i, link(abyte, 0, abyte.length));
    }

    @Override
    public void insert(int i, byte[] abyte, int j, int k) {
        this.insert(i, link(abyte, j, k));
    }

    void insert(int i, TByteLinkedList tbytelinkedlist) {
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

        this.size += tbytelinkedlist.size;
        if (tbytelinkedlist_tbytelink == this.head) {
            tbytelinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tbytelinkedlist.tail);
            this.head = tbytelinkedlist.head;
        } else {
            if (no(tbytelinkedlist_tbytelink)) {
                if (this.size == 0) {
                    this.head = tbytelinkedlist.head;
                    this.tail = tbytelinkedlist.tail;
                } else {
                    this.tail.setNext(tbytelinkedlist.head);
                    tbytelinkedlist.head.setPrevious(this.tail);
                    this.tail = tbytelinkedlist.tail;
                }
            } else {
                TByteLinkedList.TByteLink tbytelinkedlist_tbytelink1 = tbytelinkedlist_tbytelink.getPrevious();

                tbytelinkedlist_tbytelink.getPrevious().setNext(tbytelinkedlist.head);
                tbytelinkedlist.tail.setNext(tbytelinkedlist_tbytelink);
                tbytelinkedlist_tbytelink.setPrevious(tbytelinkedlist.tail);
                tbytelinkedlist.head.setPrevious(tbytelinkedlist_tbytelink1);
            }

        }
    }

    static TByteLinkedList link(byte[] abyte, int i, int j) {
        TByteLinkedList tbytelinkedlist = new TByteLinkedList();

        for (int k = 0; k < j; ++k) {
            tbytelinkedlist.add(abyte[i + k]);
        }

        return tbytelinkedlist;
    }

    @Override
    public byte get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

            return no(tbytelinkedlist_tbytelink) ? this.no_entry_value : tbytelinkedlist_tbytelink.getValue();
        }
    }

    public TByteLinkedList.TByteLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TByteLinkedList.TByteLink getLink(TByteLinkedList.TByteLink tbytelinkedlist_tbytelink, int i, int j) {
        return getLink(tbytelinkedlist_tbytelink, i, j, true);
    }

    private static TByteLinkedList.TByteLink getLink(TByteLinkedList.TByteLink tbytelinkedlist_tbytelink, int i, int j, boolean flag) {
        for (int k = i; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = flag ? tbytelinkedlist_tbytelink.getNext() : tbytelinkedlist_tbytelink.getPrevious()) {
            if (k == j) {
                return tbytelinkedlist_tbytelink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public byte set(int i, byte b0) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

            if (no(tbytelinkedlist_tbytelink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                byte b1 = tbytelinkedlist_tbytelink.getValue();

                tbytelinkedlist_tbytelink.setValue(b0);
                return b1;
            }
        }
    }

    @Override
    public void set(int i, byte[] abyte) {
        this.set(i, abyte, 0, abyte.length);
    }

    @Override
    public void set(int i, byte[] abyte, int j, int k) {
        for (int l = 0; l < k; ++l) {
            byte b0 = abyte[j + l];

            this.set(i + l, b0);
        }

    }

    @Override
    public byte replace(int i, byte b0) {
        return this.set(i, b0);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(byte b0) {
        boolean flag = false;

        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            if (tbytelinkedlist_tbytelink.getValue() == b0) {
                flag = true;
                this.removeLink(tbytelinkedlist_tbytelink);
            }
        }

        return flag;
    }

    private void removeLink(TByteLinkedList.TByteLink tbytelinkedlist_tbytelink) {
        if (!no(tbytelinkedlist_tbytelink)) {
            --this.size;
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink1 = tbytelinkedlist_tbytelink.getPrevious();
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink2 = tbytelinkedlist_tbytelink.getNext();

            if (got(tbytelinkedlist_tbytelink1)) {
                tbytelinkedlist_tbytelink1.setNext(tbytelinkedlist_tbytelink2);
            } else {
                this.head = tbytelinkedlist_tbytelink2;
            }

            if (got(tbytelinkedlist_tbytelink2)) {
                tbytelinkedlist_tbytelink2.setPrevious(tbytelinkedlist_tbytelink1);
            } else {
                this.tail = tbytelinkedlist_tbytelink1;
            }

            tbytelinkedlist_tbytelink.setNext((TByteLinkedList.TByteLink) null);
            tbytelinkedlist_tbytelink.setPrevious((TByteLinkedList.TByteLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Byte obyte;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Byte)) {
                    return false;
                }

                obyte = (Byte) object;
            } while (this.contains(obyte));

            return false;
        }
    }

    @Override
    public boolean containsAll(TByteCollection tbytecollection) {
        if (this.isEmpty()) {
            return false;
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
        if (this.isEmpty()) {
            return false;
        } else {
            byte[] abyte1 = abyte;
            int i = abyte.length;

            for (int j = 0; j < i; ++j) {
                byte b0 = abyte1[j];

                if (!this.contains(b0)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Byte obyte = (Byte) iterator.next();

            if (this.add(obyte)) {
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

    @Override
    public boolean retainAll(byte[] abyte) {
        Arrays.sort(abyte);
        boolean flag = false;
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            if (Arrays.binarySearch(abyte, tbyteiterator.next()) < 0) {
                tbyteiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            if (collection.contains(tbyteiterator.next())) {
                tbyteiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TByteCollection tbytecollection) {
        boolean flag = false;
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            if (tbytecollection.contains(tbyteiterator.next())) {
                tbyteiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(byte[] abyte) {
        Arrays.sort(abyte);
        boolean flag = false;
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            if (Arrays.binarySearch(abyte, tbyteiterator.next()) >= 0) {
                tbyteiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public byte removeAt(int i) {
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

        if (no(tbytelinkedlist_tbytelink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            byte b0 = tbytelinkedlist_tbytelink.getValue();

            this.removeLink(tbytelinkedlist_tbytelink);
            return b0;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            tbytelinkedlist_tbytelink.setValue(tbytefunction.execute(tbytelinkedlist_tbytelink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head;
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink1 = this.tail;
        TByteLinkedList.TByteLink tbytelinkedlist_tbytelink2 = this.head;

        while (got(tbytelinkedlist_tbytelink2)) {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink3 = tbytelinkedlist_tbytelink2.getNext();
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink4 = tbytelinkedlist_tbytelink2.getPrevious();
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink5 = tbytelinkedlist_tbytelink2;

            tbytelinkedlist_tbytelink2 = tbytelinkedlist_tbytelink2.getNext();
            tbytelinkedlist_tbytelink5.setNext(tbytelinkedlist_tbytelink4);
            tbytelinkedlist_tbytelink5.setPrevious(tbytelinkedlist_tbytelink3);
        }

        this.head = tbytelinkedlist_tbytelink1;
        this.tail = tbytelinkedlist_tbytelink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink1 = this.getLinkAt(j);
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink2 = null;
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink3 = tbytelinkedlist_tbytelink.getPrevious();
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink4 = tbytelinkedlist_tbytelink;

            while (tbytelinkedlist_tbytelink4 != tbytelinkedlist_tbytelink1) {
                TByteLinkedList.TByteLink tbytelinkedlist_tbytelink5 = tbytelinkedlist_tbytelink4.getNext();
                TByteLinkedList.TByteLink tbytelinkedlist_tbytelink6 = tbytelinkedlist_tbytelink4.getPrevious();

                tbytelinkedlist_tbytelink2 = tbytelinkedlist_tbytelink4;
                tbytelinkedlist_tbytelink4 = tbytelinkedlist_tbytelink4.getNext();
                tbytelinkedlist_tbytelink2.setNext(tbytelinkedlist_tbytelink6);
                tbytelinkedlist_tbytelink2.setPrevious(tbytelinkedlist_tbytelink5);
            }

            if (got(tbytelinkedlist_tbytelink2)) {
                tbytelinkedlist_tbytelink3.setNext(tbytelinkedlist_tbytelink2);
                tbytelinkedlist_tbytelink1.setPrevious(tbytelinkedlist_tbytelink3);
            }

            tbytelinkedlist_tbytelink.setNext(tbytelinkedlist_tbytelink1);
            tbytelinkedlist_tbytelink1.setPrevious(tbytelinkedlist_tbytelink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tbytelinkedlist_tbytelink);
            this.add(tbytelinkedlist_tbytelink.getValue());
        }

    }

    @Override
    public TByteList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TByteLinkedList tbytelinkedlist = new TByteLinkedList();
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tbytelinkedlist.add(tbytelinkedlist_tbytelink.getValue());
                tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext();
            }

            return tbytelinkedlist;
        }
    }

    @Override
    public byte[] toArray() {
        return this.toArray(new byte[this.size], 0, this.size);
    }

    @Override
    public byte[] toArray(int i, int j) {
        return this.toArray(new byte[j], i, 0, j);
    }

    @Override
    public byte[] toArray(byte[] abyte) {
        return this.toArray(abyte, 0, this.size);
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j) {
        return this.toArray(abyte, i, 0, j);
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j, int k) {
        if (k == 0) {
            return abyte;
        } else if (i >= 0 && i < this.size()) {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                abyte[j + l] = tbytelinkedlist_tbytelink.getValue();
                tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext();
            }

            return abyte;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TByteProcedure tbyteprocedure) {
        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            if (!tbyteprocedure.execute(tbytelinkedlist_tbytelink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TByteProcedure tbyteprocedure) {
        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.tail; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getPrevious()) {
            if (!tbyteprocedure.execute(tbytelinkedlist_tbytelink.getValue())) {
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
        TByteList tbytelist = this.subList(i, j);
        byte[] abyte = tbytelist.toArray();

        Arrays.sort(abyte);
        this.set(i, abyte);
    }

    @Override
    public void fill(byte b0) {
        this.fill(0, this.size, b0);
    }

    @Override
    public void fill(int i, int j, byte b0) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);
            int k;

            if (j > this.size) {
                for (k = i; k < this.size; ++k) {
                    tbytelinkedlist_tbytelink.setValue(b0);
                    tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext();
                }

                for (k = this.size; k < j; ++k) {
                    this.add(b0);
                }
            } else {
                for (k = i; k < j; ++k) {
                    tbytelinkedlist_tbytelink.setValue(b0);
                    tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(byte b0) {
        return this.binarySearch(b0, 0, this.size());
    }

    @Override
    public int binarySearch(byte b0, int i, int j) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + j + " > " + this.size);
        } else if (j < i) {
            return -(i + 1);
        } else {
            int k = i;
            TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i);
            int l = j;

            while (k < l) {
                int i1 = k + l >>> 1;
                TByteLinkedList.TByteLink tbytelinkedlist_tbytelink1 = getLink(tbytelinkedlist_tbytelink, k, i1);

                if (tbytelinkedlist_tbytelink1.getValue() == b0) {
                    return i1;
                }

                if (tbytelinkedlist_tbytelink1.getValue() < b0) {
                    k = i1 + 1;
                    tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink1.next;
                } else {
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
        int j = i;

        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i); got(tbytelinkedlist_tbytelink.getNext()); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            if (tbytelinkedlist_tbytelink.getValue() == b0) {
                return j;
            }

            ++j;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(byte b0) {
        return this.lastIndexOf(0, b0);
    }

    @Override
    public int lastIndexOf(int i, byte b0) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int j = -1;
            int k = i;

            for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.getLinkAt(i); got(tbytelinkedlist_tbytelink.getNext()); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
                if (tbytelinkedlist_tbytelink.getValue() == b0) {
                    j = k;
                }

                ++k;
            }

            return j;
        }
    }

    @Override
    public boolean contains(byte b0) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
                if (tbytelinkedlist_tbytelink.getValue() == b0) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TByteIterator iterator() {
        return new TByteIterator() {
            TByteLinkedList.TByteLink l;
            TByteLinkedList.TByteLink current;

            {
                this.l = TByteLinkedList.this.head;
            }

            @Override
            public byte next() {
                if (TByteLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    byte b0 = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return b0;
                }
            }

            @Override
            public boolean hasNext() {
                return TByteLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TByteLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TByteList grep(TByteProcedure tbyteprocedure) {
        TByteLinkedList tbytelinkedlist = new TByteLinkedList();

        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            if (tbyteprocedure.execute(tbytelinkedlist_tbytelink.getValue())) {
                tbytelinkedlist.add(tbytelinkedlist_tbytelink.getValue());
            }
        }

        return tbytelinkedlist;
    }

    @Override
    public TByteList inverseGrep(TByteProcedure tbyteprocedure) {
        TByteLinkedList tbytelinkedlist = new TByteLinkedList();

        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            if (!tbyteprocedure.execute(tbytelinkedlist_tbytelink.getValue())) {
                tbytelinkedlist.add(tbytelinkedlist_tbytelink.getValue());
            }
        }

        return tbytelinkedlist;
    }

    @Override
    public byte max() {
        byte b0 = Byte.MIN_VALUE;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
                if (b0 < tbytelinkedlist_tbytelink.getValue()) {
                    b0 = tbytelinkedlist_tbytelink.getValue();
                }
            }

            return b0;
        }
    }

    @Override
    public byte min() {
        byte b0 = 127;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
                if (b0 > tbytelinkedlist_tbytelink.getValue()) {
                    b0 = tbytelinkedlist_tbytelink.getValue();
                }
            }

            return b0;
        }
    }

    @Override
    public byte sum() {
        byte b0 = 0;

        for (TByteLinkedList.TByteLink tbytelinkedlist_tbytelink = this.head; got(tbytelinkedlist_tbytelink); tbytelinkedlist_tbytelink = tbytelinkedlist_tbytelink.getNext()) {
            b0 += tbytelinkedlist_tbytelink.getValue();
        }

        return b0;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeByte(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            objectoutput.writeByte(b0);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readByte();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readByte());
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
            TByteLinkedList tbytelinkedlist = (TByteLinkedList) object;

            if (this.no_entry_value != tbytelinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tbytelinkedlist.size) {
                return false;
            } else {
                TByteIterator tbyteiterator = this.iterator();
                TByteIterator tbyteiterator1 = tbytelinkedlist.iterator();

                do {
                    if (!tbyteiterator.hasNext()) {
                        return true;
                    }

                    if (!tbyteiterator1.hasNext()) {
                        return false;
                    }
                } while (tbyteiterator.next() == tbyteiterator1.next());

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

        for (TByteIterator tbyteiterator = this.iterator(); tbyteiterator.hasNext(); i = 31 * i + HashFunctions.hash(tbyteiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TByteIterator tbyteiterator = this.iterator();

        while (tbyteiterator.hasNext()) {
            byte b0 = tbyteiterator.next();

            stringbuilder.append(b0);
            if (tbyteiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TByteProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(byte b0) {
            if (TByteLinkedList.this.remove(b0)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TByteLink {

        byte value;
        TByteLinkedList.TByteLink previous;
        TByteLinkedList.TByteLink next;

        TByteLink(byte b0) {
            this.value = b0;
        }

        public byte getValue() {
            return this.value;
        }

        public void setValue(byte b0) {
            this.value = b0;
        }

        public TByteLinkedList.TByteLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TByteLinkedList.TByteLink tbytelinkedlist_tbytelink) {
            this.previous = tbytelinkedlist_tbytelink;
        }

        public TByteLinkedList.TByteLink getNext() {
            return this.next;
        }

        public void setNext(TByteLinkedList.TByteLink tbytelinkedlist_tbytelink) {
            this.next = tbytelinkedlist_tbytelink;
        }
    }
}
