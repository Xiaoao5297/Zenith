package gnu.trove.impl.sync;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedDoubleList extends TSynchronizedDoubleCollection implements TDoubleList {

    static final long serialVersionUID = -7754090372962971524L;
    final TDoubleList list;

    public TSynchronizedDoubleList(TDoubleList tdoublelist) {
        super(tdoublelist);
        this.list = tdoublelist;
    }

    public TSynchronizedDoubleList(TDoubleList tdoublelist, Object object) {
        super(tdoublelist, object);
        this.list = tdoublelist;
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
    public double get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public double set(int i, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, d0);
        }
    }

    @Override
    public void set(int i, double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, adouble);
        }
    }

    @Override
    public void set(int i, double[] adouble, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, adouble, j, k);
        }
    }

    @Override
    public double replace(int i, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, d0);
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
    public double removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(adouble);
        }
    }

    @Override
    public void add(double[] adouble, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(adouble, i, j);
        }
    }

    @Override
    public void insert(int i, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, d0);
        }
    }

    @Override
    public void insert(int i, double[] adouble) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, adouble);
        }
    }

    @Override
    public void insert(int i, double[] adouble, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, adouble, j, k);
        }
    }

    @Override
    public int indexOf(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(d0);
        }
    }

    @Override
    public int lastIndexOf(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(d0);
        }
    }

    @Override
    public TDoubleList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedDoubleList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public double[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(adouble, i, j);
        }
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(adouble, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, d0);
        }
    }

    @Override
    public int lastIndexOf(int i, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, d0);
        }
    }

    @Override
    public void fill(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(d0);
        }
    }

    @Override
    public void fill(int i, int j, double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, d0);
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
    public int binarySearch(double d0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(d0);
        }
    }

    @Override
    public int binarySearch(double d0, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(d0, i, j);
        }
    }

    @Override
    public TDoubleList grep(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tdoubleprocedure);
        }
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tdoubleprocedure);
        }
    }

    @Override
    public double max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public double min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public double sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure tdoubleprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tdoubleprocedure);
        }
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tdoublefunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(this.list) : this;
    }
}
