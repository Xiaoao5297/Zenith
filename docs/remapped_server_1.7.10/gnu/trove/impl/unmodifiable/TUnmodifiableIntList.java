package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TIntFunction;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableIntList extends TUnmodifiableIntCollection implements TIntList {

    static final long serialVersionUID = -283967356065247728L;
    final TIntList list;

    public TUnmodifiableIntList(TIntList tintlist) {
        super(tintlist);
        this.list = tintlist;
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.list.equals(object);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public int get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(int i) {
        return this.list.indexOf(i);
    }

    @Override
    public int lastIndexOf(int i) {
        return this.list.lastIndexOf(i);
    }

    @Override
    public int[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public int[] toArray(int[] aint, int i, int j) {
        return this.list.toArray(aint, i, j);
    }

    @Override
    public int[] toArray(int[] aint, int i, int j, int k) {
        return this.list.toArray(aint, i, j, k);
    }

    @Override
    public boolean forEachDescending(TIntProcedure tintprocedure) {
        return this.list.forEachDescending(tintprocedure);
    }

    @Override
    public int binarySearch(int i) {
        return this.list.binarySearch(i);
    }

    @Override
    public int binarySearch(int i, int j, int k) {
        return this.list.binarySearch(i, j, k);
    }

    @Override
    public int indexOf(int i, int j) {
        return this.list.indexOf(i, j);
    }

    @Override
    public int lastIndexOf(int i, int j) {
        return this.list.lastIndexOf(i, j);
    }

    @Override
    public TIntList grep(TIntProcedure tintprocedure) {
        return this.list.grep(tintprocedure);
    }

    @Override
    public TIntList inverseGrep(TIntProcedure tintprocedure) {
        return this.list.inverseGrep(tintprocedure);
    }

    @Override
    public int max() {
        return this.list.max();
    }

    @Override
    public int min() {
        return this.list.min();
    }

    @Override
    public int sum() {
        return this.list.sum();
    }

    @Override
    public TIntList subList(int i, int j) {
        return new TUnmodifiableIntList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessIntList(this.list) : this;
    }

    @Override
    public void add(int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int[] aint, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, int[] aint, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int set(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, int[] aint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, int[] aint, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int replace(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction tintfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shuffle(Random random) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, int k) {
        throw new UnsupportedOperationException();
    }
}
