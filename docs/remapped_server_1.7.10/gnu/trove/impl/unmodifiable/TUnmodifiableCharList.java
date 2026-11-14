package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TCharFunction;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableCharList extends TUnmodifiableCharCollection implements TCharList {

    static final long serialVersionUID = -283967356065247728L;
    final TCharList list;

    public TUnmodifiableCharList(TCharList tcharlist) {
        super(tcharlist);
        this.list = tcharlist;
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
    public char get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(char c0) {
        return this.list.indexOf(c0);
    }

    @Override
    public int lastIndexOf(char c0) {
        return this.list.lastIndexOf(c0);
    }

    @Override
    public char[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public char[] toArray(char[] achar, int i, int j) {
        return this.list.toArray(achar, i, j);
    }

    @Override
    public char[] toArray(char[] achar, int i, int j, int k) {
        return this.list.toArray(achar, i, j, k);
    }

    @Override
    public boolean forEachDescending(TCharProcedure tcharprocedure) {
        return this.list.forEachDescending(tcharprocedure);
    }

    @Override
    public int binarySearch(char c0) {
        return this.list.binarySearch(c0);
    }

    @Override
    public int binarySearch(char c0, int i, int j) {
        return this.list.binarySearch(c0, i, j);
    }

    @Override
    public int indexOf(int i, char c0) {
        return this.list.indexOf(i, c0);
    }

    @Override
    public int lastIndexOf(int i, char c0) {
        return this.list.lastIndexOf(i, c0);
    }

    @Override
    public TCharList grep(TCharProcedure tcharprocedure) {
        return this.list.grep(tcharprocedure);
    }

    @Override
    public TCharList inverseGrep(TCharProcedure tcharprocedure) {
        return this.list.inverseGrep(tcharprocedure);
    }

    @Override
    public char max() {
        return this.list.max();
    }

    @Override
    public char min() {
        return this.list.min();
    }

    @Override
    public char sum() {
        return this.list.sum();
    }

    @Override
    public TCharList subList(int i, int j) {
        return new TUnmodifiableCharList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessCharList(this.list) : this;
    }

    @Override
    public void add(char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(char[] achar, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, char[] achar, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char set(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, char[] achar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, char[] achar, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char replace(int i, char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction tcharfunction) {
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
    public void fill(char c0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, char c0) {
        throw new UnsupportedOperationException();
    }
}
