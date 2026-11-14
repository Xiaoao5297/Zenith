package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TLongFunction;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableLongList extends TUnmodifiableLongCollection implements TLongList {

    static final long serialVersionUID = -283967356065247728L;
    final TLongList list;

    public TUnmodifiableLongList(TLongList tlonglist) {
        super(tlonglist);
        this.list = tlonglist;
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
    public long get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(long i) {
        return this.list.indexOf(i);
    }

    @Override
    public int lastIndexOf(long i) {
        return this.list.lastIndexOf(i);
    }

    @Override
    public long[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public long[] toArray(long[] along, int i, int j) {
        return this.list.toArray(along, i, j);
    }

    @Override
    public long[] toArray(long[] along, int i, int j, int k) {
        return this.list.toArray(along, i, j, k);
    }

    @Override
    public boolean forEachDescending(TLongProcedure tlongprocedure) {
        return this.list.forEachDescending(tlongprocedure);
    }

    @Override
    public int binarySearch(long i) {
        return this.list.binarySearch(i);
    }

    @Override
    public int binarySearch(long i, int j, int k) {
        return this.list.binarySearch(i, j, k);
    }

    @Override
    public int indexOf(int i, long j) {
        return this.list.indexOf(i, j);
    }

    @Override
    public int lastIndexOf(int i, long j) {
        return this.list.lastIndexOf(i, j);
    }

    @Override
    public TLongList grep(TLongProcedure tlongprocedure) {
        return this.list.grep(tlongprocedure);
    }

    @Override
    public TLongList inverseGrep(TLongProcedure tlongprocedure) {
        return this.list.inverseGrep(tlongprocedure);
    }

    @Override
    public long max() {
        return this.list.max();
    }

    @Override
    public long min() {
        return this.list.min();
    }

    @Override
    public long sum() {
        return this.list.sum();
    }

    @Override
    public TLongList subList(int i, int j) {
        return new TUnmodifiableLongList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessLongList(this.list) : this;
    }

    @Override
    public void add(long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(long[] along, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, long[] along, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long set(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, long[] along) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, long[] along, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long replace(int i, long j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction tlongfunction) {
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
    public void fill(long i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, long k) {
        throw new UnsupportedOperationException();
    }
}
