package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableDoubleList extends TUnmodifiableDoubleCollection implements TDoubleList {

    static final long serialVersionUID = -283967356065247728L;
    final TDoubleList list;

    public TUnmodifiableDoubleList(TDoubleList tdoublelist) {
        super(tdoublelist);
        this.list = tdoublelist;
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
    public double get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(double d0) {
        return this.list.indexOf(d0);
    }

    @Override
    public int lastIndexOf(double d0) {
        return this.list.lastIndexOf(d0);
    }

    @Override
    public double[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j) {
        return this.list.toArray(adouble, i, j);
    }

    @Override
    public double[] toArray(double[] adouble, int i, int j, int k) {
        return this.list.toArray(adouble, i, j, k);
    }

    @Override
    public boolean forEachDescending(TDoubleProcedure tdoubleprocedure) {
        return this.list.forEachDescending(tdoubleprocedure);
    }

    @Override
    public int binarySearch(double d0) {
        return this.list.binarySearch(d0);
    }

    @Override
    public int binarySearch(double d0, int i, int j) {
        return this.list.binarySearch(d0, i, j);
    }

    @Override
    public int indexOf(int i, double d0) {
        return this.list.indexOf(i, d0);
    }

    @Override
    public int lastIndexOf(int i, double d0) {
        return this.list.lastIndexOf(i, d0);
    }

    @Override
    public TDoubleList grep(TDoubleProcedure tdoubleprocedure) {
        return this.list.grep(tdoubleprocedure);
    }

    @Override
    public TDoubleList inverseGrep(TDoubleProcedure tdoubleprocedure) {
        return this.list.inverseGrep(tdoubleprocedure);
    }

    @Override
    public double max() {
        return this.list.max();
    }

    @Override
    public double min() {
        return this.list.min();
    }

    @Override
    public double sum() {
        return this.list.sum();
    }

    @Override
    public TDoubleList subList(int i, int j) {
        return new TUnmodifiableDoubleList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessDoubleList(this.list) : this;
    }

    @Override
    public void add(double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(double[] adouble, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, double[] adouble, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double set(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, double[] adouble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, double[] adouble, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double replace(int i, double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction tdoublefunction) {
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
    public void fill(double d0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, double d0) {
        throw new UnsupportedOperationException();
    }
}
