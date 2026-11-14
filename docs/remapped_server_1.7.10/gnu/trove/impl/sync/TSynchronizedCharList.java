package gnu.trove.impl.sync;

import gnu.trove.function.TCharFunction;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedCharList extends TSynchronizedCharCollection implements TCharList {

    static final long serialVersionUID = -7754090372962971524L;
    final TCharList list;

    public TSynchronizedCharList(TCharList tcharlist) {
        super(tcharlist);
        this.list = tcharlist;
    }

    public TSynchronizedCharList(TCharList tcharlist, Object object) {
        super(tcharlist, object);
        this.list = tcharlist;
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
    public char get(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.get(i);
        }
    }

    @Override
    public char set(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.set(i, c0);
        }
    }

    @Override
    public void set(int i, char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, achar);
        }
    }

    @Override
    public void set(int i, char[] achar, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.set(i, achar, j, k);
        }
    }

    @Override
    public char replace(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.replace(i, c0);
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
    public char removeAt(int i) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.removeAt(i);
        }
    }

    @Override
    public void add(char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(achar);
        }
    }

    @Override
    public void add(char[] achar, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.add(achar, i, j);
        }
    }

    @Override
    public void insert(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, c0);
        }
    }

    @Override
    public void insert(int i, char[] achar) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, achar);
        }
    }

    @Override
    public void insert(int i, char[] achar, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.insert(i, achar, j, k);
        }
    }

    @Override
    public int indexOf(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(c0);
        }
    }

    @Override
    public int lastIndexOf(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(c0);
        }
    }

    @Override
    public TCharList subList(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return new TSynchronizedCharList(this.list.subList(i, j), this.mutex);
        }
    }

    @Override
    public char[] toArray(int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(i, j);
        }
    }

    @Override
    public char[] toArray(char[] achar, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(achar, i, j);
        }
    }

    @Override
    public char[] toArray(char[] achar, int i, int j, int k) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.toArray(achar, i, j, k);
        }
    }

    @Override
    public int indexOf(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.indexOf(i, c0);
        }
    }

    @Override
    public int lastIndexOf(int i, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.lastIndexOf(i, c0);
        }
    }

    @Override
    public void fill(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(c0);
        }
    }

    @Override
    public void fill(int i, int j, char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.fill(i, j, c0);
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
    public int binarySearch(char c0) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(c0);
        }
    }

    @Override
    public int binarySearch(char c0, int i, int j) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.binarySearch(c0, i, j);
        }
    }

    @Override
    public TCharList grep(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.grep(tcharprocedure);
        }
    }

    @Override
    public TCharList inverseGrep(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.inverseGrep(tcharprocedure);
        }
    }

    @Override
    public char max() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.max();
        }
    }

    @Override
    public char min() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.min();
        }
    }

    @Override
    public char sum() {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.sum();
        }
    }

    @Override
    public boolean forEachDescending(TCharProcedure tcharprocedure) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            return this.list.forEachDescending(tcharprocedure);
        }
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
        Object object = this.mutex;

        synchronized (this.mutex) {
            this.list.transformValues(tcharfunction);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(this.list) : this;
    }
}
