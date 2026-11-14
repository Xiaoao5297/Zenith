package gnu.trove.impl.sync;

import gnu.trove.function.TFloatFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedFloatList extends TSynchronizedFloatCollection implements TFloatList {

    static final long serialVersionUID = -7754090372962971524L;
    final TFloatList list;

    public TSynchronizedFloatList(TFloatList tfloatlist) {
        super(tfloatlist);
        this.list = tfloatlist;
    }

    public TSynchronizedFloatList(TFloatList tfloatlist, Object object) {
        super(tfloatlist, object);
        this.list = tfloatlist;
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
    public float get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public float set(int i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, f);
        }
    }

    @Override
    public void set(int i, float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, afloat);
        }
    }

    @Override
    public void set(int i, float[] afloat, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, afloat, j, k);
        }
    }

    @Override
    public float replace(int i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, f);
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
    public float removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(afloat);
        }
    }

    @Override
    public void add(float[] afloat, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(afloat, i, j);
        }
    }

    @Override
    public void insert(int i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, f);
        }
    }

    @Override
    public void insert(int i, float[] afloat) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, afloat);
        }
    }

    @Override
    public void insert(int i, float[] afloat, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, afloat, j, k);
        }
    }

    @Override
    public int indexOf(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(f);
        }
    }

    @Override
    public int lastIndexOf(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(f);
        }
    }

    @Override
    public TFloatList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedFloatList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public float[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(afloat, i, j);
        }
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(afloat, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, f);
        }
    }

    @Override
    public int lastIndexOf(int i, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, f);
        }
    }

    @Override
    public void fill(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(f);
        }
    }

    @Override
    public void fill(int i, int j, float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, f);
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
    public int binarySearch(float f) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(f);
        }
    }

    @Override
    public int binarySearch(float f, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(f, i, j);
        }
    }

    @Override
    public TFloatList grep(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tfloatprocedure);
        }
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tfloatprocedure);
        }
    }

    @Override
    public float max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public float min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public float sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TFloatProcedure tfloatprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tfloatprocedure);
        }
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tfloatfunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(this.list) : this;
    }
}
