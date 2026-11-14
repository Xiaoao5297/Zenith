package gnu.trove.list.linked;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TFloatLinkedList implements TFloatList, Externalizable {

    float no_entry_value;
    int size;
    TFloatLinkedList.TFloatLink head = null;
    TFloatLinkedList.TFloatLink tail;

    public TFloatLinkedList() {
        this.tail = this.head;
    }

    public TFloatLinkedList(float f) {
        this.tail = this.head;
        this.no_entry_value = f;
    }

    public TFloatLinkedList(TFloatList tfloatlist) {
        this.tail = this.head;
        this.no_entry_value = tfloatlist.getNoEntryValue();
        TFloatIterator tfloatiterator = tfloatlist.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            this.add(f);
        }

    }

    @Override
    public float getNoEntryValue() {
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
    public boolean add(float f) {
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = new TFloatLinkedList.TFloatLink(f);

        if (no(this.head)) {
            this.head = tfloatlinkedlist_tfloatlink;
            this.tail = tfloatlinkedlist_tfloatlink;
        } else {
            tfloatlinkedlist_tfloatlink.setPrevious(this.tail);
            this.tail.setNext(tfloatlinkedlist_tfloatlink);
            this.tail = tfloatlinkedlist_tfloatlink;
        }

        ++this.size;
        return true;
    }

    @Override
    public void add(float[] afloat) {
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            this.add(f);
        }

    }

    @Override
    public void add(float[] afloat, int i, int j) {
        for (int k = 0; k < j; ++k) {
            float f = afloat[i + k];

            this.add(f);
        }

    }

    @Override
    public void insert(int i, float f) {
        TFloatLinkedList tfloatlinkedlist = new TFloatLinkedList();

        tfloatlinkedlist.add(f);
        this.insert(i, tfloatlinkedlist);
    }

    @Override
    public void insert(int i, float[] afloat) {
        this.insert(i, link(afloat, 0, afloat.length));
    }

    @Override
    public void insert(int i, float[] afloat, int j, int k) {
        this.insert(i, link(afloat, j, k));
    }

    void insert(int i, TFloatLinkedList tfloatlinkedlist) {
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

        this.size += tfloatlinkedlist.size;
        if (tfloatlinkedlist_tfloatlink == this.head) {
            tfloatlinkedlist.tail.setNext(this.head);
            this.head.setPrevious(tfloatlinkedlist.tail);
            this.head = tfloatlinkedlist.head;
        } else {
            if (no(tfloatlinkedlist_tfloatlink)) {
                if (this.size == 0) {
                    this.head = tfloatlinkedlist.head;
                    this.tail = tfloatlinkedlist.tail;
                } else {
                    this.tail.setNext(tfloatlinkedlist.head);
                    tfloatlinkedlist.head.setPrevious(this.tail);
                    this.tail = tfloatlinkedlist.tail;
                }
            } else {
                TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink1 = tfloatlinkedlist_tfloatlink.getPrevious();

                tfloatlinkedlist_tfloatlink.getPrevious().setNext(tfloatlinkedlist.head);
                tfloatlinkedlist.tail.setNext(tfloatlinkedlist_tfloatlink);
                tfloatlinkedlist_tfloatlink.setPrevious(tfloatlinkedlist.tail);
                tfloatlinkedlist.head.setPrevious(tfloatlinkedlist_tfloatlink1);
            }

        }
    }

    static TFloatLinkedList link(float[] afloat, int i, int j) {
        TFloatLinkedList tfloatlinkedlist = new TFloatLinkedList();

        for (int k = 0; k < j; ++k) {
            tfloatlinkedlist.add(afloat[i + k]);
        }

        return tfloatlinkedlist;
    }

    @Override
    public float get(int i) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

            return no(tfloatlinkedlist_tfloatlink) ? this.no_entry_value : tfloatlinkedlist_tfloatlink.getValue();
        }
    }

    public TFloatLinkedList.TFloatLink getLinkAt(int i) {
        return i >= this.size() ? null : (i <= this.size() >>> 1 ? getLink(this.head, 0, i, true) : getLink(this.tail, this.size() - 1, i, false));
    }

    private static TFloatLinkedList.TFloatLink getLink(TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink, int i, int j) {
        return getLink(tfloatlinkedlist_tfloatlink, i, j, true);
    }

    private static TFloatLinkedList.TFloatLink getLink(TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink, int i, int j, boolean flag) {
        for (int k = i; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = flag ? tfloatlinkedlist_tfloatlink.getNext() : tfloatlinkedlist_tfloatlink.getPrevious()) {
            if (k == j) {
                return tfloatlinkedlist_tfloatlink;
            }

            k += flag ? 1 : -1;
        }

        return null;
    }

    @Override
    public float set(int i, float f) {
        if (i > this.size) {
            throw new IndexOutOfBoundsException("index " + i + " exceeds size " + this.size);
        } else {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

            if (no(tfloatlinkedlist_tfloatlink)) {
                throw new IndexOutOfBoundsException("at offset " + i);
            } else {
                float f1 = tfloatlinkedlist_tfloatlink.getValue();

                tfloatlinkedlist_tfloatlink.setValue(f);
                return f1;
            }
        }
    }

    @Override
    public void set(int i, float[] afloat) {
        this.set(i, afloat, 0, afloat.length);
    }

    @Override
    public void set(int i, float[] afloat, int j, int k) {
        for (int l = 0; l < k; ++l) {
            float f = afloat[j + l];

            this.set(i + l, f);
        }

    }

    @Override
    public float replace(int i, float f) {
        return this.set(i, f);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(float f) {
        boolean flag = false;

        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            if (tfloatlinkedlist_tfloatlink.getValue() == f) {
                flag = true;
                this.removeLink(tfloatlinkedlist_tfloatlink);
            }
        }

        return flag;
    }

    private void removeLink(TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink) {
        if (!no(tfloatlinkedlist_tfloatlink)) {
            --this.size;
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink1 = tfloatlinkedlist_tfloatlink.getPrevious();
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink2 = tfloatlinkedlist_tfloatlink.getNext();

            if (got(tfloatlinkedlist_tfloatlink1)) {
                tfloatlinkedlist_tfloatlink1.setNext(tfloatlinkedlist_tfloatlink2);
            } else {
                this.head = tfloatlinkedlist_tfloatlink2;
            }

            if (got(tfloatlinkedlist_tfloatlink2)) {
                tfloatlinkedlist_tfloatlink2.setPrevious(tfloatlinkedlist_tfloatlink1);
            } else {
                this.tail = tfloatlinkedlist_tfloatlink1;
            }

            tfloatlinkedlist_tfloatlink.setNext((TFloatLinkedList.TFloatLink) null);
            tfloatlinkedlist_tfloatlink.setPrevious((TFloatLinkedList.TFloatLink) null);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        } else {
            Iterator iterator = collection.iterator();

            Float ofloat;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Object object = iterator.next();

                if (!(object instanceof Float)) {
                    return false;
                }

                ofloat = (Float) object;
            } while (this.contains(ofloat));

            return false;
        }
    }

    @Override
    public boolean containsAll(TFloatCollection tfloatcollection) {
        if (this.isEmpty()) {
            return false;
        } else {
            TFloatIterator tfloatiterator = tfloatcollection.iterator();

            float f;

            do {
                if (!tfloatiterator.hasNext()) {
                    return true;
                }

                f = tfloatiterator.next();
            } while (this.contains(f));

            return false;
        }
    }

    @Override
    public boolean containsAll(float[] afloat) {
        if (this.isEmpty()) {
            return false;
        } else {
            float[] afloat1 = afloat;
            int i = afloat.length;

            for (int j = 0; j < i; ++j) {
                float f = afloat1[j];

                if (!this.contains(f)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Float> collection) {
        boolean flag = false;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Float ofloat = (Float) iterator.next();

            if (this.add(ofloat)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = tfloatcollection.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean addAll(float[] afloat) {
        boolean flag = false;
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            if (this.add(f)) {
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (!collection.contains(tfloatiterator.next())) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (!tfloatcollection.contains(tfloatiterator.next())) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean retainAll(float[] afloat) {
        Arrays.sort(afloat);
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (Arrays.binarySearch(afloat, tfloatiterator.next()) < 0) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (collection.contains(tfloatiterator.next())) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(TFloatCollection tfloatcollection) {
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (tfloatcollection.contains(tfloatiterator.next())) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean removeAll(float[] afloat) {
        Arrays.sort(afloat);
        boolean flag = false;
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            if (Arrays.binarySearch(afloat, tfloatiterator.next()) >= 0) {
                tfloatiterator.remove();
                flag = true;
            }
        }

        return flag;
    }

    @Override
    public float removeAt(int i) {
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

        if (no(tfloatlinkedlist_tfloatlink)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + i);
        } else {
            float f = tfloatlinkedlist_tfloatlink.getValue();

            this.removeLink(tfloatlinkedlist_tfloatlink);
            return f;
        }
    }

    @Override
    public void remove(int i, int j) {
        for (int k = 0; k < j; ++k) {
            this.removeAt(i);
        }

    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            tfloatlinkedlist_tfloatlink.setValue(tfloatfunction.execute(tfloatlinkedlist_tfloatlink.getValue()));
        }

    }

    @Override
    public void reverse() {
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head;
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink1 = this.tail;
        TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink2 = this.head;

        while (got(tfloatlinkedlist_tfloatlink2)) {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink3 = tfloatlinkedlist_tfloatlink2.getNext();
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink4 = tfloatlinkedlist_tfloatlink2.getPrevious();
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink5 = tfloatlinkedlist_tfloatlink2;

            tfloatlinkedlist_tfloatlink2 = tfloatlinkedlist_tfloatlink2.getNext();
            tfloatlinkedlist_tfloatlink5.setNext(tfloatlinkedlist_tfloatlink4);
            tfloatlinkedlist_tfloatlink5.setPrevious(tfloatlinkedlist_tfloatlink3);
        }

        this.head = tfloatlinkedlist_tfloatlink1;
        this.tail = tfloatlinkedlist_tfloatlink;
    }

    @Override
    public void reverse(int i, int j) {
        if (i > j) {
            throw new IllegalArgumentException("from > to : " + i + ">" + j);
        } else {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink1 = this.getLinkAt(j);
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink2 = null;
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink3 = tfloatlinkedlist_tfloatlink.getPrevious();
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink4 = tfloatlinkedlist_tfloatlink;

            while (tfloatlinkedlist_tfloatlink4 != tfloatlinkedlist_tfloatlink1) {
                TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink5 = tfloatlinkedlist_tfloatlink4.getNext();
                TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink6 = tfloatlinkedlist_tfloatlink4.getPrevious();

                tfloatlinkedlist_tfloatlink2 = tfloatlinkedlist_tfloatlink4;
                tfloatlinkedlist_tfloatlink4 = tfloatlinkedlist_tfloatlink4.getNext();
                tfloatlinkedlist_tfloatlink2.setNext(tfloatlinkedlist_tfloatlink6);
                tfloatlinkedlist_tfloatlink2.setPrevious(tfloatlinkedlist_tfloatlink5);
            }

            if (got(tfloatlinkedlist_tfloatlink2)) {
                tfloatlinkedlist_tfloatlink3.setNext(tfloatlinkedlist_tfloatlink2);
                tfloatlinkedlist_tfloatlink1.setPrevious(tfloatlinkedlist_tfloatlink3);
            }

            tfloatlinkedlist_tfloatlink.setNext(tfloatlinkedlist_tfloatlink1);
            tfloatlinkedlist_tfloatlink1.setPrevious(tfloatlinkedlist_tfloatlink);
        }
    }

    @Override
    public void shuffle(Random random) {
        for (int i = 0; i < this.size; ++i) {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(random.nextInt(this.size()));

            this.removeLink(tfloatlinkedlist_tfloatlink);
            this.add(tfloatlinkedlist_tfloatlink.getValue());
        }

    }

    @Override
    public TFloatList subList(int i, int j) {
        if (j < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than end index " + j);
        } else if (this.size < i) {
            throw new IllegalArgumentException("begin index " + i + " greater than last index " + this.size);
        } else if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        } else {
            TFloatLinkedList tfloatlinkedlist = new TFloatLinkedList();
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

            for (int k = i; k < j; ++k) {
                tfloatlinkedlist.add(tfloatlinkedlist_tfloatlink.getValue());
                tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext();
            }

            return tfloatlinkedlist;
        }
    }

    @Override
    public float[] toArray() {
        return this.toArray(new float[this.size], 0, this.size);
    }

    @Override
    public float[] toArray(int i, int j) {
        return this.toArray(new float[j], i, 0, j);
    }

    @Override
    public float[] toArray(float[] afloat) {
        return this.toArray(afloat, 0, this.size);
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j) {
        return this.toArray(afloat, i, 0, j);
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j, int k) {
        if (k == 0) {
            return afloat;
        } else if (i >= 0 && i < this.size()) {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);

            for (int l = 0; l < k; ++l) {
                afloat[j + l] = tfloatlinkedlist_tfloatlink.getValue();
                tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext();
            }

            return afloat;
        } else {
            throw new ArrayIndexOutOfBoundsException(i);
        }
    }

    @Override
    public boolean forEach(TFloatProcedure tfloatprocedure) {
        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            if (!tfloatprocedure.execute(tfloatlinkedlist_tfloatlink.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean forEachDescending(TFloatProcedure tfloatprocedure) {
        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.tail; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getPrevious()) {
            if (!tfloatprocedure.execute(tfloatlinkedlist_tfloatlink.getValue())) {
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
        TFloatList tfloatlist = this.subList(i, j);
        float[] afloat = tfloatlist.toArray();

        Arrays.sort(afloat);
        this.set(i, afloat);
    }

    @Override
    public void fill(float f) {
        this.fill(0, this.size, f);
    }

    @Override
    public void fill(int i, int j, float f) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else {
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);
            int k;

            if (j > this.size) {
                for (k = i; k < this.size; ++k) {
                    tfloatlinkedlist_tfloatlink.setValue(f);
                    tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext();
                }

                for (k = this.size; k < j; ++k) {
                    this.add(f);
                }
            } else {
                for (k = i; k < j; ++k) {
                    tfloatlinkedlist_tfloatlink.setValue(f);
                    tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext();
                }
            }

        }
    }

    @Override
    public int binarySearch(float f) {
        return this.binarySearch(f, 0, this.size());
    }

    @Override
    public int binarySearch(float f, int i, int j) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        } else if (j > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + j + " > " + this.size);
        } else if (j < i) {
            return -(i + 1);
        } else {
            int k = i;
            TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i);
            int l = j;

            while (k < l) {
                int i1 = k + l >>> 1;
                TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink1 = getLink(tfloatlinkedlist_tfloatlink, k, i1);

                if (tfloatlinkedlist_tfloatlink1.getValue() == f) {
                    return i1;
                }

                if (tfloatlinkedlist_tfloatlink1.getValue() < f) {
                    k = i1 + 1;
                    tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink1.next;
                } else {
                    l = i1 - 1;
                }
            }

            return -(k + 1);
        }
    }

    @Override
    public int indexOf(float f) {
        return this.indexOf(0, f);
    }

    @Override
    public int indexOf(int i, float f) {
        int j = i;

        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i); got(tfloatlinkedlist_tfloatlink.getNext()); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            if (tfloatlinkedlist_tfloatlink.getValue() == f) {
                return j;
            }

            ++j;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(float f) {
        return this.lastIndexOf(0, f);
    }

    @Override
    public int lastIndexOf(int i, float f) {
        if (this.isEmpty()) {
            return -1;
        } else {
            int j = -1;
            int k = i;

            for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.getLinkAt(i); got(tfloatlinkedlist_tfloatlink.getNext()); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
                if (tfloatlinkedlist_tfloatlink.getValue() == f) {
                    j = k;
                }

                ++k;
            }

            return j;
        }
    }

    @Override
    public boolean contains(float f) {
        if (this.isEmpty()) {
            return false;
        } else {
            for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
                if (tfloatlinkedlist_tfloatlink.getValue() == f) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatIterator() {
            TFloatLinkedList.TFloatLink l;
            TFloatLinkedList.TFloatLink current;

            {
                this.l = TFloatLinkedList.this.head;
            }

            @Override
            public float next() {
                if (TFloatLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                } else {
                    float f = this.l.getValue();

                    this.current = this.l;
                    this.l = this.l.getNext();
                    return f;
                }
            }

            @Override
            public boolean hasNext() {
                return TFloatLinkedList.got(this.l);
            }

            @Override
            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                } else {
                    TFloatLinkedList.this.removeLink(this.current);
                    this.current = null;
                }
            }
        };
    }

    @Override
    public TFloatList grep(TFloatProcedure tfloatprocedure) {
        TFloatLinkedList tfloatlinkedlist = new TFloatLinkedList();

        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            if (tfloatprocedure.execute(tfloatlinkedlist_tfloatlink.getValue())) {
                tfloatlinkedlist.add(tfloatlinkedlist_tfloatlink.getValue());
            }
        }

        return tfloatlinkedlist;
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure tfloatprocedure) {
        TFloatLinkedList tfloatlinkedlist = new TFloatLinkedList();

        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            if (!tfloatprocedure.execute(tfloatlinkedlist_tfloatlink.getValue())) {
                tfloatlinkedlist.add(tfloatlinkedlist_tfloatlink.getValue());
            }
        }

        return tfloatlinkedlist;
    }

    @Override
    public float max() {
        float f = Float.NEGATIVE_INFINITY;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
                if (f < tfloatlinkedlist_tfloatlink.getValue()) {
                    f = tfloatlinkedlist_tfloatlink.getValue();
                }
            }

            return f;
        }
    }

    @Override
    public float min() {
        float f = Float.POSITIVE_INFINITY;

        if (this.isEmpty()) {
            throw new IllegalStateException();
        } else {
            for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
                if (f > tfloatlinkedlist_tfloatlink.getValue()) {
                    f = tfloatlinkedlist_tfloatlink.getValue();
                }
            }

            return f;
        }
    }

    @Override
    public float sum() {
        float f = 0.0F;

        for (TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink = this.head; got(tfloatlinkedlist_tfloatlink); tfloatlinkedlist_tfloatlink = tfloatlinkedlist_tfloatlink.getNext()) {
            f += tfloatlinkedlist_tfloatlink.getValue();
        }

        return f;
    }

    public void writeExternal(ObjectOutput objectoutput) throws IOException {
        objectoutput.writeByte(0);
        objectoutput.writeFloat(this.no_entry_value);
        objectoutput.writeInt(this.size);
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            objectoutput.writeFloat(f);
        }

    }

    public void readExternal(ObjectInput objectinput) throws IOException, ClassNotFoundException {
        objectinput.readByte();
        this.no_entry_value = objectinput.readFloat();
        int i = objectinput.readInt();

        for (int j = 0; j < i; ++j) {
            this.add(objectinput.readFloat());
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
            TFloatLinkedList tfloatlinkedlist = (TFloatLinkedList) object;

            if (this.no_entry_value != tfloatlinkedlist.no_entry_value) {
                return false;
            } else if (this.size != tfloatlinkedlist.size) {
                return false;
            } else {
                TFloatIterator tfloatiterator = this.iterator();
                TFloatIterator tfloatiterator1 = tfloatlinkedlist.iterator();

                do {
                    if (!tfloatiterator.hasNext()) {
                        return true;
                    }

                    if (!tfloatiterator1.hasNext()) {
                        return false;
                    }
                } while (tfloatiterator.next() == tfloatiterator1.next());

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

        for (TFloatIterator tfloatiterator = this.iterator(); tfloatiterator.hasNext(); i = 31 * i + HashFunctions.hash(tfloatiterator.next())) {
            ;
        }

        return i;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("{");
        TFloatIterator tfloatiterator = this.iterator();

        while (tfloatiterator.hasNext()) {
            float f = tfloatiterator.next();

            stringbuilder.append(f);
            if (tfloatiterator.hasNext()) {
                stringbuilder.append(", ");
            }
        }

        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    class RemoveProcedure implements TFloatProcedure {

        boolean changed = false;

        RemoveProcedure() {}

        @Override
        public boolean execute(float f) {
            if (TFloatLinkedList.this.remove(f)) {
                this.changed = true;
            }

            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TFloatLink {

        float value;
        TFloatLinkedList.TFloatLink previous;
        TFloatLinkedList.TFloatLink next;

        TFloatLink(float f) {
            this.value = f;
        }

        public float getValue() {
            return this.value;
        }

        public void setValue(float f) {
            this.value = f;
        }

        public TFloatLinkedList.TFloatLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink) {
            this.previous = tfloatlinkedlist_tfloatlink;
        }

        public TFloatLinkedList.TFloatLink getNext() {
            return this.next;
        }

        public void setNext(TFloatLinkedList.TFloatLink tfloatlinkedlist_tfloatlink) {
            this.next = tfloatlinkedlist_tfloatlink;
        }
    }
}
