package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TShortFunction;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableShortList extends TUnmodifiableShortCollection implements TShortList {

    static final long serialVersionUID = -283967356065247728L;
    final TShortList list;

    public TUnmodifiableShortList(TShortList tshortlist) {
        super(tshortlist);
        this.list = tshortlist;
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
    public short get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(short short0) {
        return this.list.indexOf(short0);
    }

    @Override
    public int lastIndexOf(short short0) {
        return this.list.lastIndexOf(short0);
    }

    @Override
    public short[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j) {
        return this.list.toArray(ashort, i, j);
    }

    @Override
    public short[] toArray(short[] ashort, int i, int j, int k) {
        return this.list.toArray(ashort, i, j, k);
    }

    @Override
    public boolean forEachDescending(TShortProcedure tshortprocedure) {
        return this.list.forEachDescending(tshortprocedure);
    }

    @Override
    public int binarySearch(short short0) {
        return this.list.binarySearch(short0);
    }

    @Override
    public int binarySearch(short short0, int i, int j) {
        return this.list.binarySearch(short0, i, j);
    }

    @Override
    public int indexOf(int i, short short0) {
        return this.list.indexOf(i, short0);
    }

    @Override
    public int lastIndexOf(int i, short short0) {
        return this.list.lastIndexOf(i, short0);
    }

    @Override
    public TShortList grep(TShortProcedure tshortprocedure) {
        return this.list.grep(tshortprocedure);
    }

    @Override
    public TShortList inverseGrep(TShortProcedure tshortprocedure) {
        return this.list.inverseGrep(tshortprocedure);
    }

    @Override
    public short max() {
        return this.list.max();
    }

    @Override
    public short min() {
        return this.list.min();
    }

    @Override
    public short sum() {
        return this.list.sum();
    }

    @Override
    public TShortList subList(int i, int j) {
        return new TUnmodifiableShortList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessShortList(this.list) : this;
    }

    @Override
    public void add(short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(short[] ashort, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, short[] ashort, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short set(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, short[] ashort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, short[] ashort, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short replace(int i, short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction tshortfunction) {
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
    public void fill(short short0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, short short0) {
        throw new UnsupportedOperationException();
    }
}
