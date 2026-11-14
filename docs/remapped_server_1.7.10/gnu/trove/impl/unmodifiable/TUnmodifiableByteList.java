package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TByteFunction;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableByteList extends TUnmodifiableByteCollection implements TByteList {

    static final long serialVersionUID = -283967356065247728L;
    final TByteList list;

    public TUnmodifiableByteList(TByteList tbytelist) {
        super(tbytelist);
        this.list = tbytelist;
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
    public byte get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(byte b0) {
        return this.list.indexOf(b0);
    }

    @Override
    public int lastIndexOf(byte b0) {
        return this.list.lastIndexOf(b0);
    }

    @Override
    public byte[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j) {
        return this.list.toArray(abyte, i, j);
    }

    @Override
    public byte[] toArray(byte[] abyte, int i, int j, int k) {
        return this.list.toArray(abyte, i, j, k);
    }

    @Override
    public boolean forEachDescending(TByteProcedure tbyteprocedure) {
        return this.list.forEachDescending(tbyteprocedure);
    }

    @Override
    public int binarySearch(byte b0) {
        return this.list.binarySearch(b0);
    }

    @Override
    public int binarySearch(byte b0, int i, int j) {
        return this.list.binarySearch(b0, i, j);
    }

    @Override
    public int indexOf(int i, byte b0) {
        return this.list.indexOf(i, b0);
    }

    @Override
    public int lastIndexOf(int i, byte b0) {
        return this.list.lastIndexOf(i, b0);
    }

    @Override
    public TByteList grep(TByteProcedure tbyteprocedure) {
        return this.list.grep(tbyteprocedure);
    }

    @Override
    public TByteList inverseGrep(TByteProcedure tbyteprocedure) {
        return this.list.inverseGrep(tbyteprocedure);
    }

    @Override
    public byte max() {
        return this.list.max();
    }

    @Override
    public byte min() {
        return this.list.min();
    }

    @Override
    public byte sum() {
        return this.list.sum();
    }

    @Override
    public TByteList subList(int i, int j) {
        return new TUnmodifiableByteList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessByteList(this.list) : this;
    }

    @Override
    public void add(byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(byte[] abyte, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, byte[] abyte, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte set(int i, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, byte[] abyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, byte[] abyte, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte replace(int i, byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction tbytefunction) {
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
    public void fill(byte b0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, byte b0) {
        throw new UnsupportedOperationException();
    }
}
