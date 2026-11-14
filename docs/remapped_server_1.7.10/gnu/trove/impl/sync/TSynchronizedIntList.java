package gnu.trove.impl.sync;

import gnu.trove.function.TIntFunction;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedIntList extends TSynchronizedIntCollection implements TIntList {

    static final long serialVersionUID = -7754090372962971524L;
    final TIntList list;

    public TSynchronizedIntList(TIntList tintlist) {
        super(tintlist);
        this.list = tintlist;
    }

    public TSynchronizedIntList(TIntList tintlist, Object object) {
        super(tintlist, object);
        this.list = tintlist;
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
    public int get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public int set(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, j);
        }
    }

    @Override
    public void set(int i, int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, aint);
        }
    }

    @Override
    public void set(int i, int[] aint, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, aint, j, k);
        }
    }

    @Override
    public int replace(int i, int j) {
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
    public int removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(aint);
        }
    }

    @Override
    public void add(int[] aint, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(aint, i, j);
        }
    }

    @Override
    public void insert(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, j);
        }
    }

    @Override
    public void insert(int i, int[] aint) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, aint);
        }
    }

    @Override
    public void insert(int i, int[] aint, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, aint, j, k);
        }
    }

    @Override
    public int indexOf(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i);
        }
    }

    @Override
    public int lastIndexOf(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i);
        }
    }

    @Override
    public TIntList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedIntList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public int[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public int[] toArray(int[] aint, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(aint, i, j);
        }
    }

    @Override
    public int[] toArray(int[] aint, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(aint, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, j);
        }
    }

    @Override
    public int lastIndexOf(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, j);
        }
    }

    @Override
    public void fill(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i);
        }
    }

    @Override
    public void fill(int i, int j, int k) {
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
    public int binarySearch(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(i);
        }
    }

    @Override
    public int binarySearch(int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(i, j, k);
        }
    }

    @Override
    public TIntList grep(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tintprocedure);
        }
    }

    @Override
    public TIntList inverseGrep(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tintprocedure);
        }
    }

    @Override
    public int max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public int min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public int sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TIntProcedure tintprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tintprocedure);
        }
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tintfunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(this.list) : this;
    }
}
