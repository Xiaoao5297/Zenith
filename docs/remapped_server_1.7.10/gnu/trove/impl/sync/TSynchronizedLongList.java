package gnu.trove.impl.sync;

import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedLongList extends TSynchronizedLongCollection implements TLongList {

    static final long serialVersionUID = -7754090372962971524L;
    final TLongList list;

    public TSynchronizedLongList(TLongList tlonglist) {
        super(tlonglist);
        this.list = tlonglist;
    }

    public TSynchronizedLongList(TLongList tlonglist, Object object) {
        super(tlonglist, object);
        this.list = tlonglist;
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
    public long get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public long set(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, j);
        }
    }

    @Override
    public void set(int i, long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, along);
        }
    }

    @Override
    public void set(int i, long[] along, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, along, j, k);
        }
    }

    @Override
    public long replace(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, j);
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
    public long removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(along);
        }
    }

    @Override
    public void add(long[] along, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(along, i, j);
        }
    }

    @Override
    public void insert(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, j);
        }
    }

    @Override
    public void insert(int i, long[] along) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, along);
        }
    }

    @Override
    public void insert(int i, long[] along, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, along, j, k);
        }
    }

    @Override
    public int indexOf(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i);
        }
    }

    @Override
    public int lastIndexOf(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i);
        }
    }

    @Override
    public TLongList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedLongList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public long[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public long[] toArray(long[] along, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(along, i, j);
        }
    }

    @Override
    public long[] toArray(long[] along, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(along, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, j);
        }
    }

    @Override
    public int lastIndexOf(int i, long j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, j);
        }
    }

    @Override
    public void fill(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i);
        }
    }

    @Override
    public void fill(int i, int j, long k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, k);
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
    public int binarySearch(long i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(i);
        }
    }

    @Override
    public int binarySearch(long i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(i, j, k);
        }
    }

    @Override
    public TLongList grep(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tlongprocedure);
        }
    }

    @Override
    public TLongList inverseGrep(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tlongprocedure);
        }
    }

    @Override
    public long max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public long min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public long sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TLongProcedure tlongprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tlongprocedure);
        }
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tlongfunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessLongList(this.list) : this;
    }
}
