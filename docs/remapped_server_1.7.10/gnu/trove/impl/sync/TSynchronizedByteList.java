package gnu.trove.impl.sync;

import gnu.trove.function.TByteFunction;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedByteList extends TSynchronizedByteCollection implements TByteList {

    static final long serialVersionUID = -7754090372962971524L;
    final TByteList list;

    public TSynchronizedByteList(TByteList tbytelist) {
        super(tbytelist);
        this.list = tbytelist;
    }

    public TSynchronizedByteList(TByteList tbytelist, Object object) {
        super(tbytelist, object);
        this.list = tbytelist;
    }

    @Override
    public boolean equals(Object object) {
        Object object1 = this.mutex;

        synchronized (this.mutex) {
            return this.list.equals(object);
        }
    }

    @Override
    public int hashCode() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.hashCode();
        }
    }

    @Override
    public byte get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public byte set(int i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, b0);
        }
    }

    @Override
    public void set(int i, byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, abyte);
        }
    }

    @Override
    public void set(int i, byte[] abyte, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, abyte, j, k);
        }
    }

    @Override
    public byte replace(int i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, b0);
        }
    }

    @Override
    public void remove(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.remove(i, j);
        }
    }

    @Override
    public byte removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(abyte);
        }
    }

    @Override
    public void add(byte[] abyte, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(abyte, i, j);
        }
    }

    @Override
    public void insert(int i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, b0);
        }
    }

    @Override
    public void insert(int i, byte[] abyte) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, abyte);
        }
    }

    @Override
    public void insert(int i, byte[] abyte, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, abyte, j, k);
        }
    }

    @Override
    public int indexOf(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(b0);
        }
    }

    @Override
    public int lastIndexOf(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(b0);
        }
    }

    @Override
    public TByteList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedByteList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public byte[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(abyte, i, j);
        }
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(abyte, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, b0);
        }
    }

    @Override
    public int lastIndexOf(int i, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, b0);
        }
    }

    @Override
    public void fill(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(b0);
        }
    }

    @Override
    public void fill(int i, int j, byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, b0);
        }
    }

    @Override
    public void reverse() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.reverse();
        }
    }

    @Override
    public void reverse(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.reverse(i, j);
        }
    }

    @Override
    public void shuffle(Random random) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.shuffle(random);
        }
    }

    @Override
    public void sort() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.sort();
        }
    }

    @Override
    public void sort(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.sort(i, j);
        }
    }

    @Override
    public int binarySearch(byte b0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(b0);
        }
    }

    @Override
    public int binarySearch(byte b0, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(b0, i, j);
        }
    }

    @Override
    public TByteList grep(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tbyteprocedure);
        }
    }

    @Override
    public TByteList inverseGrep(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tbyteprocedure);
        }
    }

    @Override
    public byte max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public byte min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public byte sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TByteProcedure tbyteprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tbyteprocedure);
        }
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tbytefunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(this.list) : this;
    }
}
