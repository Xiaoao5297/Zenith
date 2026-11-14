package gnu.trove.impl.sync;

import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedShortList extends TSynchronizedShortCollection implements TShortList {

    static final long serialVersionUID = -7754090372962971524L;
    final TShortList list;

    public TSynchronizedShortList(TShortList tshortlist) {
        super(tshortlist);
        this.list = tshortlist;
    }

    public TSynchronizedShortList(TShortList tshortlist, Object object) {
        super(tshortlist, object);
        this.list = tshortlist;
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
    public short get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public short set(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, short0);
        }
    }

    @Override
    public void set(int i, short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, ashort);
        }
    }

    @Override
    public void set(int i, short[] ashort, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, ashort, j, k);
        }
    }

    @Override
    public short replace(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, short0);
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
    public short removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(ashort);
        }
    }

    @Override
    public void add(short[] ashort, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(ashort, i, j);
        }
    }

    @Override
    public void insert(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, short0);
        }
    }

    @Override
    public void insert(int i, short[] ashort) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, ashort);
        }
    }

    @Override
    public void insert(int i, short[] ashort, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, ashort, j, k);
        }
    }

    @Override
    public int indexOf(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(short0);
        }
    }

    @Override
    public int lastIndexOf(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(short0);
        }
    }

    @Override
    public TShortList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedShortList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public short[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(ashort, i, j);
        }
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(ashort, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, short0);
        }
    }

    @Override
    public int lastIndexOf(int i, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, short0);
        }
    }

    @Override
    public void fill(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(short0);
        }
    }

    @Override
    public void fill(int i, int j, short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, short0);
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
    public int binarySearch(short short0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(short0);
        }
    }

    @Override
    public int binarySearch(short short0, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(short0, i, j);
        }
    }

    @Override
    public TShortList grep(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tshortprocedure);
        }
    }

    @Override
    public TShortList inverseGrep(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tshortprocedure);
        }
    }

    @Override
    public short max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public short min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public short sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TShortProcedure tshortprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tshortprocedure);
        }
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tshortfunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessShortList(this.list) : this;
    }
}
